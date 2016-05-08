package com.asu.seatr.handlers;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.interfaces.TaskAnalyzerI;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.SessionFactoryUtil;
import com.asu.seatr.utils.Utilities;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class TaskAnalyzerHandler {



	public static TaskAnalyzerI save(TaskAnalyzerI taskAnalyzer) throws TaskException {
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
		try{
			int id = (int)session.save(taskAnalyzer);
			taskAnalyzer.setId(id);
			session.getTransaction().commit();
		}
		catch (ConstraintViolationException cve) {	    	

			//throw new TaskException(MyStatus.ERROR, MyMessage.TASK_ANALYZER_ALREADY_PRESENT);
			throw new TaskException(MyStatus.ERROR, MyMessage.TASK_ENTRY_ALREADY_PRESENT);
		} catch (PropertyValueException pve) {
			throw new TaskException(MyStatus.ERROR, MyMessage.FIELD_MISSING);
		}

		finally
		{session.close();}
		return taskAnalyzer;
	}

	/*
	 * @param int id
	 * @desc
	 * Read by task analyzer table's unique is.
	 */
	public static TaskAnalyzerI readById(int id)
	{
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
		TaskAnalyzerI taskAnalyzer = (TaskAnalyzerI)session.get(TaskAnalyzerI.class, id);
		session.close();
		return taskAnalyzer;
	}

	public static TaskAnalyzerI readByExtId(Class typeParameterClass, String external_task_id, String external_course_id) throws CourseException, TaskException
	{
		Course course = CourseHandler.getByExternalId(external_course_id);
		Task task = TaskHandler.readByExtTaskId_Course(external_task_id, course);
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
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("task", task));
		cr.add(Restrictions.eq("course", course));
		List<TaskAnalyzerI> result = cr.list();
		session.close();
		if (result.size() == 0) {
			throw new TaskException(MyStatus.ERROR, MyMessage.TASK_ANALYZER_NOT_FOUND);
		}

		TaskAnalyzerI taskAnalyzer = result.get(0);
		return taskAnalyzer;

	}
	public static List<TaskAnalyzerI> readByCourse(Class typeParameterClass, Course course) throws CourseException
	{
		if(course == null)
		{
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);
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
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("course", course));
		List<TaskAnalyzerI> taskAnalyzerList = (List<TaskAnalyzerI>)cr.list();
		session.close();
		return taskAnalyzerList;
	}

	public static List<TaskAnalyzerI> readAll(Class typeParameterClass)
	{
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
		Criteria cr = session.createCriteria(typeParameterClass);
		List<TaskAnalyzerI> taskAnalyzerList = (List<TaskAnalyzerI>)cr.list();
		session.close();
		return taskAnalyzerList;
	}

	public static TaskAnalyzerI update(TaskAnalyzerI taskAnalyzer)
	{
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
		session.merge(taskAnalyzer);
		session.getTransaction().commit();
		session.close();
		return taskAnalyzer;
	}
	public static void delete(TaskAnalyzerI taskAnalyzer)
	{
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
		session.delete(taskAnalyzer);
		session.getTransaction().commit();
		session.close();
	}








}
