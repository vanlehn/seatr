package com.asu.seatr.handlers;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.models.Course;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

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
	
	public static Course getByExternalId(String external_course_id) throws CourseException{
		
		SessionFactory sf=HibernateUtil.getSessionFactory();
		Session session=sf.openSession();
		Criteria cr = session.createCriteria(Course.class);
		cr.add(Restrictions.eq("external_id", external_course_id));
		List<Course> courseList = (List<Course>)cr.list();
		if(courseList.size() == 0){
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);
		}
		Course course = courseList.get(0);
		session.close();
		return course;
		
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
