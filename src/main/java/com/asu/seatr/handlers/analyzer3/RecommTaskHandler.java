package com.asu.seatr.handlers.analyzer3;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.persistence.HibernateUtil;

import com.asu.seatr.models.analyzers.task.T_A3;

public class RecommTaskHandler {

	public static void getTasks(Course course,Student student, int number)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		try
		{
			//String hqlStudentTask = "Select student_task.task from ST_A3 st_a3 inner join st_a3.studentTask student_task on student_task.id = st_a3.studentTask";
			//Query query = session.createQuery(hqlStudentTask);
			//List<Task> tempTaskList = query.list();
			String TasksAnsweredFirstTime = "Select t_a3 from T_A3 t_a3 where t_a3.course = :course and t_a3.s_is_required = true and t_a3.s_unit_no = (Select d_current_unit_no from C_A3 where course = :course) and t_a3.task not in (Select student_task.task from ST_A3 st_a3 inner join st_a3.studentTask student_task on student_task.id = st_a3.studentTask where student_task.student = :student) order by t_a3.s_sequence_no";
			Query query = session.createQuery(TasksAnsweredFirstTime);
			query.setParameter("course", course);
			query.setParameter("student", student);
			//query.setParameter("tempTaskList", tempTaskList);
			List<T_A3> taskList = (List<T_A3>)query.list();
			for(T_A3 t_a3 : taskList)
			{
				System.out.println(t_a3.getTask().getExternal_id()+","+t_a3.getId());
			}
			if(taskList.size()<number)
			{
				
				String TasksWithBigN = "Select t_a3 from T_A3 t_a3 where t_a3.course = :course and t_a3.s_is_required = true and "
						+ "t_a3.s_unit_no = (Select d_current_unit_no from C_A3 where course = :course) and t_a3.task in "
						+ "(Select task from StudentTask s1 where s1.id in ("
						+ "Select studentTask from ST_A3 s2 where s2.id in (Select id"
						+ " from StudentTask s3 where s3.timestamp in (Select max(timestamp) as timestamp from StudentTask s4 where "
						+ "s4.student =:student group by student,task)"
						+ ") and s2.d_is_answered = false and s2.d_current_n > (Select d_max_n from C_A3 where course = :course))"
						+ ") order by t_a3.s_sequence_no asc";
				Query query2 = session.createQuery(TasksWithBigN);
				query2.setParameter("course", course);
				query2.setParameter("student", student);
				List<T_A3> tempTaskList = (List<T_A3>)query2.list();
				if(tempTaskList.size()>0)
				{
					System.out.println("n limit reached");
					System.out.println("execute optional tasks now");
				}
				else
				{
				
				String TasksWithIncorrectResponse = "Select t_a3 from T_A3 t_a3 where t_a3.course = :course and t_a3.s_is_required = true and "
						+ "t_a3.s_unit_no = (Select d_current_unit_no from C_A3 where course = :course) and t_a3.task in "
						+ "(Select task from StudentTask s1 where s1.id in ("
						+ "Select studentTask from ST_A3 s2 where s2.id in (Select id"
						+ " from StudentTask s3 where s3.timestamp in (Select max(timestamp) as timestamp from StudentTask s4 where "
						+ "s4.student =:student group by student,task)"
						+ ") and s2.d_is_answered = false)"
						+ ") order by t_a3.s_sequence_no asc";
				
				/*String TasksWithIncorrectResponse = "Select task from StudentTask s1 where s1.id in (Select studentTask from ST_A3 s2 where s2.id in (Select id"
						+ " from StudentTask s3 where s3.timestamp in (Select max(timestamp) as timestamp from StudentTask group by student,task)"
						+ ") and s2.d_is_answered = false)";*/
				Query query1 = session.createQuery(TasksWithIncorrectResponse);
				query1.setParameter("course", course);
				query1.setParameter("student", student);
				//query1.setMaxResults(number-taskList.size());
				List<T_A3> taskList1 = (List<T_A3>)query1.list();
				for(T_A3 t_a3 : taskList1)
				{
					System.out.println(t_a3.getTask().getExternal_id()+","+t_a3.getId());
				}
				}
			}
		}
		finally
		{
			session.close();
		}
	}
}