package com.asu.seatr.utilities;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.constants.DatabaseConstants;
import com.asu.seatr.models.CourseAnalyzerMap;
import com.asu.seatr.persistence.HibernateUtil;

public class CourseAnalyzerMapHandler {

	public static CourseAnalyzerMap save(CourseAnalyzerMap courseAnalyzer) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    
	    int id = (int)session.save(courseAnalyzer);
	    courseAnalyzer.setId(id);
	    session.getTransaction().commit();
	    session.close();
	    return courseAnalyzer;
	}
	
	public static CourseAnalyzerMap read(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		CourseAnalyzerMap courseAnalyzer = (CourseAnalyzerMap)session.get(CourseAnalyzerMap.class, id);
		session.close();
		return courseAnalyzer;
	}
	
	public static List<CourseAnalyzerMap> readAll()
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		List<CourseAnalyzerMap> records = session.createQuery("from " + DatabaseConstants.COURSE_ANALYZER_TABLE_NAME).list();
		session.close();
		return records;
	}
	
	public static CourseAnalyzerMap update(CourseAnalyzerMap courseAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(courseAnalyzer);
		session.getTransaction().commit();
		session.close();
		return courseAnalyzer;
	}
	public static void delete(CourseAnalyzerMap courseAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(courseAnalyzer);
		session.getTransaction().commit();
		session.close();
	}
	


}
