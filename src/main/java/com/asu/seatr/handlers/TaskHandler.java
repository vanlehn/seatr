package com.asu.seatr.handlers;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.constants.DatabaseConstants;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Task;
import com.asu.seatr.persistence.HibernateUtil;

public class TaskHandler {


	public static Task save(Task task) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    
	    int id = (int)session.save(task);
	    task.setId(id);
	    session.getTransaction().commit();
	    session.close();
	    return task;
	}
	
	public static Task read(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Task student = (Task)session.get(Task.class, id);
		session.close();
		return student;
	}
	public static List<Task> readByExtCourseId(String external_course_id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr= session.createCriteria(Course.class);
		cr.add(Restrictions.eq("external_id", external_course_id));
		Course course = (Course)cr.list().get(0);
		cr = session.createCriteria(Task.class);
		cr.add(Restrictions.eq("course", course));
		List<Task> taskList = cr.list();
		return taskList;
	}
	public static Task readByExtId(String external_task_id, String external_course_id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(Course.class);
		cr.add(Restrictions.eq("external_id", external_course_id));
		Course course = (Course)cr.list().get(0);
		cr = session.createCriteria(Task.class);
		cr.add(Restrictions.eq("external_id", external_task_id));
		cr.add(Restrictions.eq("course", course));
		Task task = (Task) cr.list().get(0);
		return task;
	}
	
	public static List<Task> readAll()
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		List<Task> records = session.createQuery("from " + DatabaseConstants.TASK_TABLE_NAME).list();
		session.close();
		return records;
	}
	
	public static Task update(Task task)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(task);
		session.getTransaction().commit();
		session.close();
		return task;
	}
	public static void delete(Task task)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(task);
		session.getTransaction().commit();
		session.close();
	}
	

}
