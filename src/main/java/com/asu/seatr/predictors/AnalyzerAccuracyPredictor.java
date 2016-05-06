package com.asu.seatr.predictors;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

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
import com.asu.seatr.models.analyzers.studenttask.ST_A2;
import com.asu.seatr.models.analyzers.studenttask.ST_A3;
import com.asu.seatr.models.analyzers.task_kc.TK_A2;
import com.asu.seatr.models.analyzers.task_kc.TK_A3;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.SessionFactoryUtil;
import com.asu.seatr.utils.Utilities;

import au.com.bytecode.opencsv.CSVWriter;

public class AnalyzerAccuracyPredictor {

	public static void main(String args[]) throws IOException
	{
		try {
			int threshold = 3;
			String external_course_id = "37";
			String analyzer_id = "";
			Scanner sc = new Scanner(System.in);
			System.out.println("enter the path");
			String path = sc.nextLine();
			FileOutputStream fos = new FileOutputStream(path);
			OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
			CSVWriter writer = new CSVWriter(osw);
			

			
			
			Course course = CourseHandler.getByExternalId(external_course_id);
			List<Student> studentListForCourse = StudentHandler.getByCourse(course);
			List<KnowledgeComponent> kcListForCourse = KnowledgeComponentHandler.getByCourse(course);
			
			String firstLine[] = new String[4+kcListForCourse.size()];
			firstLine[0] = "Student id";
			firstLine[1] = "Task id";
			firstLine[2] = "Status";
			firstLine[3] = "Predicted Correctness";
			int tempIndex=4;
			ListIterator<KnowledgeComponent> ki = kcListForCourse.listIterator();
			while(ki.hasNext())
			{
				firstLine[tempIndex] = "K_" + ki.next().getExternal_id();
				tempIndex++;
			}
			writer.writeNext(firstLine);
			
			
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
			
			SessionFactory sf;
			if(Utilities.isJUnitTest())
			{
				sf = SessionFactoryUtil.getSessionFactory();
			}
			else
			{	
				sf = HibernateUtil.getSessionFactory();
			}
			Session session = sf.openSession();
			session.beginTransaction();
			
			String studentTasksQuery = "from ST_A2 where studentTask in (Select id from StudentTask where task in (Select id from Task where course = :course))";
			Query query = session.createQuery(studentTasksQuery);
			query.setParameter("course", course);
			List<ST_A2> studentTaskList = (List<ST_A2>)query.list();
			
			ListIterator<ST_A2> studentTaskListIterator = studentTaskList.listIterator();
			while(studentTaskListIterator.hasNext())
			{
				int predictedCorrectness=-1;
				ST_A2 st_a2 = studentTaskListIterator.next();
				StudentTask studentTask = st_a2.getStudentTask();
				List<TK_A2> tkList = studentTask.getTask().getTK_A2();
				ListIterator<TK_A2> tkListIterator = tkList.listIterator();
				HashMap<Integer,Integer> kcMastery = studentKcMastery.get(studentTask.getStudent().getId());
				System.out.println(st_a2.getId());
				
				if(studentTask.getId()==196)
				{
					System.out.println("student task come here");
				}				
				if(studentTask.getStudent().getId()==2916)
				{
					System.out.println("come here");
				}
				while(tkListIterator.hasNext())
				{
					Integer kcId = tkListIterator.next().getKc().getId();
					int masteryLevel = kcMastery.get(kcId);
					if(st_a2.getD_status().equals("correct"))
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
					if(masteryLevel < threshold)
					{
						predictedCorrectness = 0;
					}
					else
					{
						if(predictedCorrectness!=0)
						{
							predictedCorrectness = 1;
						}
					}
					kcMastery.put(kcId, masteryLevel);
				}
				if(predictedCorrectness == -1)
				{
					predictedCorrectness = 0;
				}
				studentKcMastery.put(studentTask.getStudent().getId(),kcMastery);
				HashMap<Integer,Integer> temp = studentKcMastery.get(2916);
				System.out.println("studentKCMastery" + studentKcMastery.get(2916));
				String output[] = new String[4+kcListForCourse.size()];
				output[0] = studentTask.getStudent().getExternal_id();
				output[1] = studentTask.getTask().getExternal_id();
				output[2] = st_a2.getD_status();
				output[3] = String.valueOf(predictedCorrectness);
				int index=4;
				ListIterator<KnowledgeComponent> kcIterator = kcListForCourse.listIterator();
				while(kcIterator.hasNext())
				{
					Integer temp1 = kcMastery.get(kcIterator.next().getId());
					if(temp1==null)
					{
						output[index] = "0";
					}
					else
					{
						output[index] = String.valueOf(temp1);
					}
					index++;
				}
				writer.writeNext(output);
			}
			writer.close();
			osw.close();
			fos.close();
		} catch (CourseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		System.out.println("execution done");
}
}
