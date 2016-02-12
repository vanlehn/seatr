package com.asu.seatr.utilities;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.models.interfaces.TaskAnalyzerI;
import com.asu.seatr.persistence.HibernateUtil;

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
	
	public static TaskAnalyzerI read(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		TaskAnalyzerI taskAnalyzer = (TaskAnalyzerI)session.get(TaskAnalyzerI.class, id);
		session.close();
		return taskAnalyzer;
	}
	
	public static List<TaskAnalyzerI> readAll(String tableName)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		List<TaskAnalyzerI> records = session.createQuery("from " + tableName).list();
		session.close();
		return records;
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
