package com.asu.seatr.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.hql.internal.ast.QuerySyntaxException;

import com.asu.seatr.models.interfaces.StudentTaskAnalyzerI;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.exceptions.StudentTaskException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.models.StudentTask;

public class StudentTaskAnalyzerHandler {

	/*
	public static List<StudentTaskAnalyzerI> readByExtId(Class typeParameterClass, String external_student_id, String external_course_id, String external_task_id) throws CourseNotFoundException, TaskNotFoundException, StudentNotFoundException {

		Course course = CourseHandler.getByExternalId(external_course_id);
		Student student = StudentHandler.getByExternalStudentId_Course(external_student_id, course);
		Task task = TaskHandler.readByExtTaskId_Course(external_task_id, course);
		List<StudentTask> studentTaskList = StudentTaskHandler.readByStudent_Task(student, task);
		StudentTask student_task = studentTaskList.get(0);
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();	
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("studentTask", student_task));		

		List<StudentTaskAnalyzerI> result = cr.list(); 
		session.close();
		return result;		
	}
	 */

	public static List<StudentTaskAnalyzerI> readOrderByTimestamp(Class typeParameterClass, 
			String external_student_id, String external_course_id, String external_task_id) throws CourseException, StudentException, TaskException, StudentTaskException {
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);

		List<StudentTask> studentTasks = StudentTaskHandler.readByExtIdOrderByTime(external_student_id, external_course_id, external_task_id);

		if(studentTasks.size() == 0) {
			throw new StudentTaskException(MyStatus.ERROR, MyMessage.STUDENT_TASK_NOT_FOUND);
		}
		StudentTask studentTask = studentTasks.get(0);

		cr.add(Restrictions.eq("studentTask", studentTask));

		List<StudentTaskAnalyzerI> result = cr.list();
		if (result.size() == 0) {
			throw new StudentTaskException(MyStatus.ERROR, MyMessage.STUDENT_TASK_ANALYZER_NOT_FOUND);
		}
		session.close();
		return result;

	}

	public static List<StudentTaskAnalyzerI> readByCriteria(Class typeParameterClass, HashMap<String, Object> eqRestrictions) {
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.allEq(eqRestrictions));
		List<StudentTaskAnalyzerI> result = cr.list();
		session.close();
		return result;		
	}


	public static StudentTaskAnalyzerI save(StudentTaskAnalyzerI studentTaskAnalyzer) {
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		try{
			session.beginTransaction();

			int id = (int)session.save(studentTaskAnalyzer);
			studentTaskAnalyzer.setId(id);
			session.getTransaction().commit();
		}
		finally
		{
			session.close();
		}
		return studentTaskAnalyzer;
	}

	public static StudentTaskAnalyzerI read(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		StudentTaskAnalyzerI studentTaskAnalyzer = (StudentTaskAnalyzerI)session.get(StudentTaskAnalyzerI.class, id);
		session.close();
		return studentTaskAnalyzer;
	}

	public static List<StudentTaskAnalyzerI> readAll(String tableName)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		List<StudentTaskAnalyzerI> records = session.createQuery("from " + tableName).list();
		session.close();
		return records;
	}

	public static StudentTaskAnalyzerI update(StudentTaskAnalyzerI studentTaskAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.update(studentTaskAnalyzer);
		//session.merge(studentTaskAnalyzer);
		session.getTransaction().commit();
		session.close();
		return studentTaskAnalyzer;
	}
	public static void delete(StudentTaskAnalyzerI studentTaskAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(studentTaskAnalyzer);
		session.getTransaction().commit();
		session.close();
	}
	public static void hqlBatchDelete(String analyzerName, List<StudentTask> studentTaskList)
	{
		try
		{
			List<Integer> studentTaskIdList = new ArrayList<Integer>();
			for(StudentTask studentTask : studentTaskList)
			{
				studentTaskIdList.add(studentTask.getId());
			}
			SessionFactory sf = HibernateUtil.getSessionFactory();
			Session session = sf.openSession();
			session.beginTransaction();
			String hql = "delete from ST_" + analyzerName + " st where st.id in :studentTaskIdList";
			session.createQuery(hql).setParameterList("studentTaskIdList", studentTaskIdList).executeUpdate();
			session.getTransaction().commit();
			session.close();
		}
		catch(QuerySyntaxException e)
		{
			System.out.println("Table Not Mapped");
		}

	}

}
