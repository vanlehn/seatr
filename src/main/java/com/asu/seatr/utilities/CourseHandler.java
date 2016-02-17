package com.asu.seatr.utilities;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.models.Course;
import com.asu.seatr.persistence.HibernateUtil;

public class CourseHandler {
	public static void save(Course course) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    
	    session.save(course);
	    session.getTransaction().commit();
	    session.close();
	}
	
	public static Course readById(int id)
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
	
	public static Course getByExternalId(String external_id){
		SessionFactory sf=HibernateUtil.getSessionFactory();
		Session session=sf.openSession();
		Query query=session.createQuery("FROM Course where external_id=:external_id");
		@SuppressWarnings("unchecked")
		List<Course> results=query.setString("external_id", external_id).list();
		session.close();
		return results.get(0);
		
	}
	
	public static void updateCourseByExternalID(String external_id,Course course){
		SessionFactory sf=HibernateUtil.getSessionFactory();
		Session session=sf.openSession();
		session.beginTransaction();
		Query query=session.createQuery("update Course set description=:desc where external_id=:external_id");
		query.setParameter("desc", course.getDescription());
		query.setParameter("external_id", course.getExternal_id());
		query.executeUpdate();
		session.getTransaction().commit();
		session.close();
	}
}
