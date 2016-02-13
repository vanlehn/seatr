package com.asu.seatr.utilities;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.models.Course;
import com.asu.seatr.models.Task;
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
	public static List<TaskAnalyzerI> readByExtId(Class typeParameterClass, String external_task_id, Integer external_course_id)
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
		cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("task", task));
		cr.add(Restrictions.eq("course", course));
		List<TaskAnalyzerI> result = cr.list();
		session.close();
		return result;
		
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
