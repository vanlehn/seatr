package com.asu.seatr.utilities;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.constants.DatabaseConstants;
import com.asu.seatr.models.CourseAnalyzer;
import com.asu.seatr.persistence.HibernateUtil;

public class CourseAnalyzerHandler {

	public static CourseAnalyzer save(CourseAnalyzer courseAnalyzer) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    
	    int id = (int)session.save(courseAnalyzer);
	    courseAnalyzer.setId(id);
	    session.getTransaction().commit();
	    session.close();
	    return courseAnalyzer;
	}
	
	public static CourseAnalyzer read(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		CourseAnalyzer courseAnalyzer = (CourseAnalyzer)session.get(CourseAnalyzer.class, id);
		session.close();
		return courseAnalyzer;
	}
	
	public static List<CourseAnalyzer> readAll()
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		List<CourseAnalyzer> records = session.createQuery("from " + DatabaseConstants.COURSE_ANALYZER_TABLE_NAME).list();
		session.close();
		return records;
	}
	
	public static CourseAnalyzer update(CourseAnalyzer courseAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(courseAnalyzer);
		session.getTransaction().commit();
		session.close();
		return courseAnalyzer;
	}
	public static void delete(CourseAnalyzer courseAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(courseAnalyzer);
		session.getTransaction().commit();
		session.close();
	}
	


}
