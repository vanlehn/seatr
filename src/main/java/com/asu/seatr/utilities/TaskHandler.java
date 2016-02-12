package com.asu.seatr.utilities;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.constants.DatabaseConstants;
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
