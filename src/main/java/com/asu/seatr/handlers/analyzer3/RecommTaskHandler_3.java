package com.asu.seatr.handlers.analyzer3;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.exceptions.RecommException;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.StudentTask;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.studenttask.ST_A3;
import com.asu.seatr.models.analyzers.task.T_A3;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.SessionFactoryUtil;
import com.asu.seatr.utils.Utilities;

@SuppressWarnings("unchecked")
public class RecommTaskHandler_3 {
	static Logger log = Logger.getLogger(RecommTaskHandler_3.class);

	static List<String> shuffleArray(List<String> ar) {
		// If running on Java 6 or older, use `new Random()` on RHS here
		Random rnd = ThreadLocalRandom.current();
		for (int i = ar.size() - 1; i > 0; i--)
		{
			int index = rnd.nextInt(i + 1);
			// Simple swap
			String a = ar.get(index);
			ar.set(index, ar.get(i));
			ar.set(i, a);		  
		}
		return ar;
	}

	public static List<String> getTasks(Course course,Student student, int numberOfTasks) throws RecommException
	{
		List<String> finalTaskList = new ArrayList<String>();
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
		try
		{
			String getUnit = "Select d_current_unit_no from C_A3 where course = :course";
			Query unitQuery = session.createQuery(getUnit);
			unitQuery.setParameter("course", course);
			List<Integer> unitList = (List<Integer>)unitQuery.list();
			int unit;
			if(!unitList.isEmpty())
			{
				unit = unitList.get(0);
			}
			else
			{
				return new ArrayList<String>();
			}
			
			String TasksAnsweredFirstTime = "Select t_a3 from T_A3 t_a3 where t_a3.course = :course and t_a3.s_is_required = true "
					+ "and t_a3.s_unit_no = :unit and t_a3.task not in "
					+ "(Select student_task.task from ST_A3 st_a3 inner join st_a3.studentTask student_task on student_task.id = st_a3.studentTask "
					+ "where student_task.student = :student) order by t_a3.s_sequence_no";
			Query query = session.createQuery(TasksAnsweredFirstTime);
			query.setParameter("unit", unit);
			query.setParameter("course", course);
			query.setParameter("student", student);

			List<T_A3> taskList = (List<T_A3>)query.list();
			for(T_A3 t_a3 : taskList)
			{
				finalTaskList.add(t_a3.getTask().getExternal_id());
				log.info("tasks not yet solved: " + t_a3.getTask().getExternal_id());
			}
			if(taskList.size()<numberOfTasks)
			{	String max_n_QueryString = "Select d_max_n from C_A3 where course = :course";
				Query max_n_Query = session.createQuery(max_n_QueryString);
				max_n_Query.setParameter("course", course);
				List<Integer> max_n_List = (List<Integer>)max_n_Query.list();
				int max_n;
				if(!max_n_List.isEmpty())
				{
					max_n = max_n_List.get(0);
				}
				else
				{
					return new ArrayList<String>();
				}
				
				/*String getLatestSubmittedTask = "from ST_A3 s2 where s2.studentTask in (Select id"
						+ " from StudentTask s3 where s3.timestamp in (Select max(timestamp) as timestamp from StudentTask s4 where "
						+ "s4.student =:student group by student,task))";*/
				String getLatestSubmittedTask = "from ST_A3 s2 where s2.studentTask in (from StudentTask s3 "
						+ "where s3.timestamp in (Select max(timestamp) as timestamp from StudentTask s4 where "
						+ "s4.student =:student group by student,task))";

				
				Query latestSubmittedTaskQuery = session.createQuery(getLatestSubmittedTask);
				latestSubmittedTaskQuery.setParameter("student", student);
				List<ST_A3> stuTaskList = (List<ST_A3>)latestSubmittedTaskQuery.list();
				List<Integer> tempRequiredTasksList = new ArrayList<Integer>();
				List<Integer> tempOptionalTaskList = new ArrayList<Integer>();
				ListIterator<ST_A3> stuTaskListIterator = stuTaskList.listIterator();
				while(stuTaskListIterator.hasNext())
				{
					ST_A3 st = stuTaskListIterator.next();
					if(!st.getD_is_answered() && st.getD_current_n() <= max_n)
					{
						tempRequiredTasksList.add(st.getStudentTask().getTask().getId());
					}
					if(st.getD_is_answered())
					{
						tempOptionalTaskList.add(st.getStudentTask().getTask().getId());
					}
				}
				List<T_A3> tempTaskList = null;
				if(!tempRequiredTasksList.isEmpty())
				{
				String TasksWithBigN = "Select t_a3 from T_A3 t_a3 where t_a3.course = :course and t_a3.s_is_required = true and "
						+ "t_a3.s_unit_no = :unit and t_a3.task.id in (:taskList)"
						+ " order by t_a3.s_sequence_no asc";
				Query query2 = session.createQuery(TasksWithBigN);
				query2.setParameter("unit", unit);
				query2.setParameter("course", course);
				//query2.setParameter("student", student);
				query2.setParameterList("taskList", tempRequiredTasksList);
				query2.setMaxResults(numberOfTasks-taskList.size());
				tempTaskList = (List<T_A3>)query2.list();}
				if(taskList.size() < 1 && tempTaskList == null || (tempTaskList!=null && tempTaskList.size() < 1))
				{

					/*String optionalTasks = "Select t_a3 from T_A3 t_a3 where t_a3.course = :course and t_a3.s_is_required = false "
							+ "and t_a3.s_unit_no = (Select d_current_unit_no from C_A3 where course = :course)";*/
					String optionalTasks;
					Query query3;
					if(tempOptionalTaskList.isEmpty())
					{
						optionalTasks = "Select t_a3 from T_A3 t_a3 where t_a3.course = :course and t_a3.s_is_required = false and "
								+ "t_a3.s_unit_no = :unit";
						query3 = session.createQuery(optionalTasks);
					}
					else
					{
						optionalTasks = "Select t_a3 from T_A3 t_a3 where t_a3.course = :course and t_a3.s_is_required = false and "
								+ "t_a3.s_unit_no = :unit and t_a3.task.id not in (:taskList)";
						query3 = session.createQuery(optionalTasks);
						query3.setParameterList("taskList", tempOptionalTaskList);
					}
					
					query3.setParameter("unit", unit);
					query3.setParameter("course", course);
					//query3.setParameter("student", student);
					
					List<T_A3> tasksList = (List<T_A3>)query3.list();

					for (T_A3 t_a3 : tasksList) {
						finalTaskList.add(t_a3.getTask().getExternal_id());
						log.info("optional tasks: " + t_a3.getTask().getExternal_id());
					}

					finalTaskList = shuffleArray(finalTaskList);

					if (finalTaskList.size() > numberOfTasks) {
						finalTaskList = finalTaskList.subList(0, numberOfTasks);
					}

					return finalTaskList;
				}
				else
				{
					if(tempTaskList!=null)
					{
						for(T_A3 t_a3 : tempTaskList)
						{
							finalTaskList.add(t_a3.getTask().getExternal_id());
							log.info("incorrect required tasks: " + t_a3.getTask().getExternal_id());
						}
					}
					return finalTaskList;
				}
			}
			else
			{
				if (finalTaskList.size() > numberOfTasks) {
					finalTaskList = finalTaskList.subList(0, numberOfTasks);
				}
				return finalTaskList;
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			throw new RecommException(MyStatus.ERROR, MyMessage.RECOMMENDATION_ERROR);
		}

		finally
		{
			session.close();
		}
	}
}
