package com.asu.seatr.handlers;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.asu.seatr.constants.DatabaseConstants;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.interfaces.CourseAnalyzerI;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

public class CourseAnalyzerHandler {

	
	public static List<CourseAnalyzerI> readByExtId(Class typeParameterClass, String external_course_id) throws CourseException {
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(Course.class);
		cr.add(Restrictions.eq("external_id", external_course_id));
		List<Course> courseList = cr.list();
		if (courseList.size() == 0) {

			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);
		}		
		Course course = courseList.get(0);
		cr = session.createCriteria(typeParameterClass);		
		cr.add(Restrictions.eq("course", course));
		List<CourseAnalyzerI> result = cr.list();
		session.close();
		if (result.size() == 0) {
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ANALYZER_NOT_FOUND);
		}
		
		return result;	

	}
	
	public static CourseAnalyzerI save(CourseAnalyzerI courseAnalyzer) throws CourseException {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    try {
	    int id = (int)session.save(courseAnalyzer);
	    courseAnalyzer.setId(id);
	    session.getTransaction().commit();
	    } catch(ConstraintViolationException cve) {
	    	throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ANALYZER_ALREADY_PRESENT);
	    } finally {
	    	session.close();
	    }	    
	    
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
