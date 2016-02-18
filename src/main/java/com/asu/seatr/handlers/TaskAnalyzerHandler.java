package com.asu.seatr.handlers;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.exceptions.CourseNotFoundException;
import com.asu.seatr.exceptions.TaskNotFoundException;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.interfaces.TaskAnalyzerI;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

public class TaskAnalyzerHandler {



	public static TaskAnalyzerI save(TaskAnalyzerI taskAnalyzer) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();	    
	    int id = (int)session.save(taskAnalyzer);
	    taskAnalyzer.setId(id);
	    session.getTransaction().commit();
	    session.close();
	    return taskAnalyzer;
	}
	
	/*
	 * @param int id
	 * @desc
	 * Read by task analyzer table's unique is.
	 */
	public static TaskAnalyzerI readById(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		TaskAnalyzerI taskAnalyzer = (TaskAnalyzerI)session.get(TaskAnalyzerI.class, id);
		session.close();
		return taskAnalyzer;
	}
	@SuppressWarnings("unchecked")
	public static List<TaskAnalyzerI> readByExtId(Class typeParameterClass, String external_task_id, String external_course_id) throws CourseNotFoundException, TaskNotFoundException
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(Course.class);
		cr.add(Restrictions.eq("external_id", external_course_id));
		List<Course> courseList = (List<Course>)cr.list();
		if(courseList.size() < 1)
		{
			Response rb = Response.status(Status.NOT_FOUND).
					entity(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND)).build();
			throw new CourseNotFoundException(rb);
		}
		Course course = courseList.get(0);
		cr = session.createCriteria(Task.class);
		cr.add(Restrictions.eq("external_id", external_task_id));
		cr.add(Restrictions.eq("course", course));
		List<Task> taskList = (List<Task>) cr.list();
		if(taskList.size() < 1)
		{
			Response rb = Response.status(Status.NOT_FOUND).
					entity(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND)).build();
			throw new TaskNotFoundException(rb);
		}
		Task task = taskList.get(0);
		cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("task", task));
		cr.add(Restrictions.eq("course", course));
		List<TaskAnalyzerI> result = cr.list();
		session.close();
		return result;
		
	}
	
	@SuppressWarnings("unchecked")
	public static List<TaskAnalyzerI> readAll(Class typeParameterClass)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		List<TaskAnalyzerI> taskAnalyzerList = (List<TaskAnalyzerI>)cr.list();
		session.close();
		return taskAnalyzerList;
	}
	
	public static TaskAnalyzerI update(TaskAnalyzerI taskAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(taskAnalyzer);
		session.getTransaction().commit();
		session.close();
		return taskAnalyzer;
	}
	public static void delete(TaskAnalyzerI taskAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(taskAnalyzer);
		session.getTransaction().commit();
		session.close();
	}
	
	






}
