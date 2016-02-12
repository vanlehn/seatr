package com.asu.seatr.utilities;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.constants.DatabaseConstants;
import com.asu.seatr.models.interfaces.CourseAnalyzerI;
import com.asu.seatr.persistence.HibernateUtil;

public class CourseAnalyzerHandler {


	public static CourseAnalyzerI save(CourseAnalyzerI courseAnalyzer) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    
	    int id = (int)session.save(courseAnalyzer);
	    courseAnalyzer.setId(id);
	    session.getTransaction().commit();
	    session.close();
	    return courseAnalyzer;
	}
	
	public static CourseAnalyzerI read(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		CourseAnalyzerI courseAnalyzer = (CourseAnalyzerI)session.get(CourseAnalyzerI.class, id);
		session.close();
		return courseAnalyzer;
	}
	
	public static List<CourseAnalyzerI> readAll(String tableName)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		List<CourseAnalyzerI> records = session.createQuery("from " + tableName).list();
		session.close();
		return records;
	}
	
	public static CourseAnalyzerI update(CourseAnalyzerI courseAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(courseAnalyzer);
		session.getTransaction().commit();
		session.close();
		return courseAnalyzer;
	}
	public static void delete(CourseAnalyzerI courseAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(courseAnalyzer);
		session.getTransaction().commit();
		session.close();
	}
	




}
