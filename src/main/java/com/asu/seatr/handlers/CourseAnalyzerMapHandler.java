package com.asu.seatr.handlers;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.constants.DatabaseConstants;
import com.asu.seatr.exceptions.AnalyzerException;
import com.asu.seatr.exceptions.CourseAnalyzerMapException;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.models.Analyzer;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.CourseAnalyzerMap;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

@SuppressWarnings("unchecked")
public class CourseAnalyzerMapHandler {

	public static CourseAnalyzerMap save(CourseAnalyzerMap courseAnalyzer) {
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		try
		{
			int id = (int)session.save(courseAnalyzer);
			courseAnalyzer.setId(id);
			session.getTransaction().commit();
		}
		finally
		{
			session.close();
		}
		return courseAnalyzer;
	}

	public static CourseAnalyzerMap read(int id)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		CourseAnalyzerMap courseAnalyzer;
		try{
			courseAnalyzer = (CourseAnalyzerMap)session.get(CourseAnalyzerMap.class, id);
		}
		finally
		{
			session.close();
		}
		return courseAnalyzer;
	}

	public static List<CourseAnalyzerMap> readAll()
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		List<CourseAnalyzerMap> records = session.createQuery("from " + DatabaseConstants.COURSE_ANALYZER_TABLE_NAME).list();
		session.close();
		return records;
	}
	public static List<CourseAnalyzerMap> getAnalyzerIdFromExtCourseId(String external_course_id) throws CourseException
	{
		Course course = CourseHandler.getByExternalId(external_course_id);
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria criteria = session.createCriteria(CourseAnalyzerMap.class);
		criteria.add(Restrictions.eq("course", course));
		List<CourseAnalyzerMap> courseAnalyzerMapList = criteria.list();
		session.close();
		return courseAnalyzerMapList;
	}

	public static List<CourseAnalyzerMap> getAnalyzerIdFromCourse(Course course) throws CourseException
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria criteria = session.createCriteria(CourseAnalyzerMap.class);
		criteria.add(Restrictions.eq("course", course));
		List<CourseAnalyzerMap> courseAnalyzerMapList = criteria.list();
		session.close();
		return courseAnalyzerMapList;
	}

	public static CourseAnalyzerMap getAnalyzerIdFromExtCourseIdAnalyzerId(String external_course_id, Integer analyzer_id) throws CourseException, AnalyzerException, CourseAnalyzerMapException
	{
		Course course = CourseHandler.getByExternalId(external_course_id);
		Analyzer analyzer = AnalyzerHandler.getById(analyzer_id);
		SessionFactory sf = Utilities.getSessionFactory();
		List<CourseAnalyzerMap> courseAnalyzerMapList;
		Session session = sf.openSession();
		try
		{
			Criteria criteria = session.createCriteria(CourseAnalyzerMap.class);
			criteria.add(Restrictions.eq("course", course));
			criteria.add(Restrictions.eq("analyzer", analyzer));
			courseAnalyzerMapList = criteria.list();
			if(courseAnalyzerMapList.size() == 0) {
				throw new CourseAnalyzerMapException(MyStatus.ERROR, MyMessage.COURSE_ANALYZER_MAP_NOT_FOUND);
			}
		}
		finally
		{
			session.close();
		}
		return courseAnalyzerMapList.get(0);
	}


	public static CourseAnalyzerMap getPrimaryAnalyzerIdFromExtCourseId(String external_course_id) throws CourseException
	{
		Course course = CourseHandler.getByExternalId(external_course_id);
		SessionFactory sf = Utilities.getSessionFactory();
		
		Session session = sf.openSession();
		CourseAnalyzerMap  courseAnalyzerMap;
		try
		{
			Criteria criteria = session.createCriteria(CourseAnalyzerMap.class);
			criteria.add(Restrictions.eq("course", course));
			criteria.add(Restrictions.eq("active", true));
			List<CourseAnalyzerMap> courseAnalyzerMapList = (List<CourseAnalyzerMap>)criteria.list();
			if(courseAnalyzerMapList.size() < 1)
			{
				return null;//a null here indicates that primary analyzer does not exist and a default analyzer will be used
			}
			courseAnalyzerMap = courseAnalyzerMapList.get(0);
		}
		finally
		{
			session.close();
		}
		return courseAnalyzerMap;
	}

	public static CourseAnalyzerMap getByCourseAndAnalyzer(String external_course_id, String analyzer_id) throws CourseException {
		Course course=CourseHandler.getByExternalId(external_course_id);
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		List<CourseAnalyzerMap> ca_l;
		try
		{
			Query q=session.createQuery("from CourseAnalyzerMap where course_id=:c_id and analyzer_id=:a_id");
			q.setParameter("c_id", course.getId());
			q.setParameter("a_id", Integer.valueOf(analyzer_id));
			ca_l=q.list();
		}
		finally
		{
			session.close();
		}
		if(ca_l.size()<1)
			return null;
		else
			return ca_l.get(0);

	}

	public static void deactiveAllAnalyzers(String external_course_id) throws CourseException{
		Course course=CourseHandler.getByExternalId(external_course_id);
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		try
		{
			session.beginTransaction();
			Query q=session.createQuery("update CourseAnalyzerMap set active=false where course_id=:c_id");
			q.setParameter("c_id", course.getId());
			q.executeUpdate();
			session.getTransaction().commit();
		}
		finally
		{
			session.close();
		}

	}

	public static CourseAnalyzerMap update(CourseAnalyzerMap courseAnalyzer)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		try
		{
			session.beginTransaction();
			session.merge(courseAnalyzer);
			session.getTransaction().commit();
		}
		finally
		{
			session.close();
		}
		return courseAnalyzer;
	}
	public static void delete(CourseAnalyzerMap courseAnalyzer)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		try
		{
			session.beginTransaction();
			session.delete(courseAnalyzer);
			session.getTransaction().commit();
		}
		finally
		{
			session.close();
		}
	}



}
