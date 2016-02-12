package com.asu.seatr.utilities;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.models.Course;
import com.asu.seatr.persistence.HibernateUtil;

public class CourseHandler {
	public static int save(Course course) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    
	    int id=(int)session.save(course);
	    session.getTransaction().commit();
	    session.close();
	    return id;
	}
	
	public static Course read(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Course course = (Course)session.get(Course.class, id);
		session.close();
		return course;
	}
	
	public static void delete(Course course)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(course);
		session.getTransaction().commit();
		session.close();
	}
}
