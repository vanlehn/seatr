package com.asu.seatr.predictors;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.KnowledgeComponentHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.StudentTask;
import com.asu.seatr.models.analyzers.studenttask.ST_A3;
import com.asu.seatr.models.analyzers.task_kc.TK_A3;
import com.asu.seatr.persistence.HibernateUtil;

public class AnalyzerAccuracyPredictor {

	public static void main(String args[])
	{
		try {
			int threshold = 3;
			String external_course_id = "";
			String analyzer_id = "";
			Course course = CourseHandler.getByExternalId(external_course_id);
			List<Student> studentListForCourse = StudentHandler.getByCourse(course);
			List<KnowledgeComponent> kcListForCourse = KnowledgeComponentHandler.getByCourse(course);
			HashMap<Integer,HashMap<Integer,Integer>> studentKcMastery = new HashMap<Integer,HashMap<Integer,Integer>>();
			ListIterator<Student> studentListIterator = studentListForCourse.listIterator();
			while(studentListIterator.hasNext())
			{
				HashMap<Integer,Integer> kcMastery = new HashMap<Integer,Integer>();
				ListIterator<KnowledgeComponent> kcListIterator = kcListForCourse.listIterator();
				while(kcListIterator.hasNext())
				{
					kcMastery.put(kcListIterator.next().getId(), 0);
				}
				studentKcMastery.put(studentListIterator.next().getId(), kcMastery);
			}
			
			SessionFactory sf = HibernateUtil.getSessionFactory();
			Session session = sf.openSession();
			session.beginTransaction();
			
			String studentTasksQuery = "Select * from ST_A3 where studentTask in (Select id from StudentTask where task in (Select id from task where course = :course))";
			Query query = session.createQuery(studentTasksQuery);
			query.setParameter("course", course);
			List<ST_A3> studentTaskList = (List<ST_A3>)query.list();
			
			ListIterator<ST_A3> studentTaskListIterator = studentTaskList.listIterator();
			while(studentTaskListIterator.hasNext())
			{
				ST_A3 st_a3 = studentTaskListIterator.next();
				StudentTask studentTask = st_a3.getStudentTask();
				List<TK_A3> tkList = studentTask.getTask().getTK_A3();
				ListIterator<TK_A3> tkListIterator = tkList.listIterator();
				HashMap<Integer,Integer> kcMastery = studentKcMastery.get(studentTask.getStudent().getId());
				while(tkListIterator.hasNext())
				{
					Integer kcId = tkListIterator.next().getKc().getId();
					int masteryLevel = kcMastery.get(kcId);
					if(st_a3.getD_is_answered())
					{
						++masteryLevel;
					}
					else
					{
						if(masteryLevel < 1)
						{
							masteryLevel = 0;
						}
						else
						{
							--masteryLevel;
						}
					}
					kcMastery.put(kcId, masteryLevel);
				}
				studentKcMastery.put(studentTask.getStudent().getId(),kcMastery);
			}
			
		} catch (CourseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
}
