package com.asu.seatr.handlers;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.constants.DatabaseConstants;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.CourseAnalyzerMap;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

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
	public static List<CourseAnalyzerMap> getAnalyzerIdFromExtCourseId(String external_course_id) throws CourseException
	{
		Course course = CourseHandler.getByExternalId(external_course_id);
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria criteria = session.createCriteria(CourseAnalyzerMap.class);
		criteria.add(Restrictions.eq("course", course));
		List<CourseAnalyzerMap> courseAnalyzerMapList = criteria.list();
		return courseAnalyzerMapList;
	}
	public static CourseAnalyzerMap getPrimaryAnalyzerIdFromExtCourseId(String external_course_id) throws CourseException
	{
		Course course = CourseHandler.getByExternalId(external_course_id);
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria criteria = session.createCriteria(CourseAnalyzerMap.class);
		criteria.add(Restrictions.eq("course", course));
		criteria.add(Restrictions.eq("active", true));
		List<CourseAnalyzerMap> courseAnalyzerMapList = (List<CourseAnalyzerMap>)criteria.list();
		if(courseAnalyzerMapList.size() < 1)
		{
			return null;//a null here indicates that primary analyzer does not exist and a default analyzer will be used
		}
		CourseAnalyzerMap  courseAnalyzerMap = courseAnalyzerMapList.get(0);
		return courseAnalyzerMap;
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
