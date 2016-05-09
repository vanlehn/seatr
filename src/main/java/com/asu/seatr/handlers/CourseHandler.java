package com.asu.seatr.handlers;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.hql.internal.ast.QuerySyntaxException;

import com.asu.seatr.constants.DatabaseConstants;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.models.Course;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

@SuppressWarnings("unchecked")
public class CourseHandler {
	public static void save(Course course) {
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		try{
			session.beginTransaction();

			session.save(course);
			session.getTransaction().commit();
		}
		finally
		{
			session.close();
		}
	}

	public static List<Course> readAll()
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		List<Course> records = session.createQuery("from " + DatabaseConstants.COURSE_TABLE_NAME).list();
		session.close();
		return records;
	}

	public static Course readById(int id)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Course course;
		try{
			course = (Course)session.get(Course.class, id);
		}
		finally
		{
			session.close();
		}
		return course;
	}

	public static void delete(Course course)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		try
		{
			session.beginTransaction();
			session.delete(course);
			session.getTransaction().commit();
		}
		finally
		{
			session.close();
		}
	}

	public static Course getByExternalId(String external_course_id) throws CourseException{

		SessionFactory sf = Utilities.getSessionFactory();
		Session session=sf.openSession();
		Course course;
		try
		{
			Criteria cr = session.createCriteria(Course.class);
			cr.add(Restrictions.eq("external_id", external_course_id));
			List<Course> courseList = (List<Course>)cr.list();

			if(courseList.size() == 0){
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);
			}
			course = courseList.get(0);
		}
		finally
		{
			session.close();
		}
		return course;

	}

	public static void updateCourseByExternalID(String external_id,Course course){
		SessionFactory sf = Utilities.getSessionFactory();
		Session session=sf.openSession();
		try{
			session.beginTransaction();
			Query query=session.createQuery("update Course set description=:desc where external_id=:external_id");
			query.setParameter("desc", course.getDescription());
			query.setParameter("external_id", course.getExternal_id());
			query.executeUpdate();
			session.getTransaction().commit();
		}
		finally
		{
			session.close();
		}
	}

	public static List<Integer> getInternalCourseList(Set<String> courseIdList)
	{
		if(courseIdList == null || courseIdList.isEmpty())
		{
			return null;
		}
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		try
		{

			session.beginTransaction();
			String hql = "select c.id from Course c where c.external_id in :courseIdList";
			Query query = session.createQuery(hql).setParameterList("courseIdList", courseIdList);
			List<Integer> internal_course_list = query.list();
			session.getTransaction().commit();
			session.close();
			return internal_course_list;
		}
		catch(QuerySyntaxException e)
		{
			System.out.println("Table Not Mapped");
			return null;
		}
		finally
		{
			session.close();
		}
	}
	public static List<Course> getCourseList(Set<String> courseIdList)
	{
		if(courseIdList == null || courseIdList.isEmpty())
		{
			return null;
		}

		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		try
		{
			session.beginTransaction();
			String hql = "select c from Course c where c.external_id in :courseIdList";
			Query query = session.createQuery(hql).setParameterList("courseIdList", courseIdList);
			List<Course> internal_course_list = query.list();
			session.getTransaction().commit();
			//session.close();
			return internal_course_list;
		}
		catch(QuerySyntaxException e)
		{
			System.out.println("Table Not Mapped");
			return null;
		}
		finally
		{
			session.close();
		}
	}
}
