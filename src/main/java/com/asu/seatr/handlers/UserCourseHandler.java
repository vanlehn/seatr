package com.asu.seatr.handlers;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.UserException;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.User;
import com.asu.seatr.models.UserCourse;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.SessionFactoryUtil;
import com.asu.seatr.utils.Utilities;

@SuppressWarnings("unchecked")
public class UserCourseHandler {


	public static UserCourse save(UserCourse userCourse){
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session = sf.openSession();
		session.beginTransaction();
		int id = (int)session.save(userCourse);
		userCourse.setId(id);
		session.getTransaction().commit();
		session.close();
		return userCourse;
	}

	public static UserCourse read(int id)
	{
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session = sf.openSession();
		UserCourse user = (UserCourse)session.get(UserCourse.class, id);
		session.close();
		return user;
	}

	public static UserCourse read(String username, String external_course_id) throws UserException, CourseException
	{

		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session = sf.openSession();		
		Course course = CourseHandler.getByExternalId(external_course_id);
		User user = UserHandler.read(username);
		session.update(course);
		session.update(user);
		Criteria cr = session.createCriteria(UserCourse.class);
		cr.add(Restrictions.eq("user", user));
		cr.add(Restrictions.eq("course", course));
		List<UserCourse> userList = (List<UserCourse>)cr.list();
		if(userList.size() == 0) {
			throw new UserException(MyStatus.ERROR, MyMessage.USER_NO_ACCESS_TO_COURSE);
		}		
		session.close();
		return userList.get(0);
	}

	public static List<UserCourse> readAll()
	{
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(UserCourse.class);
		List<UserCourse> userList = (List<UserCourse>)cr.list();
		session.close();
		return userList;
	}

	public static UserCourse update(UserCourse userCourse)
	{
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(userCourse);
		session.getTransaction().commit();
		session.close();
		return userCourse;
	}
	public static void delete(UserCourse userCourse)
	{
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(userCourse);
		session.getTransaction().commit();
		session.close();
	}

	public static UserCourse save(String username, String external_course_id) throws CourseException, UserException {
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session = sf.openSession();
		session.beginTransaction();
		Course course = CourseHandler.getByExternalId(external_course_id);
		User user = UserHandler.read(username);
		session.update(course);
		session.update(user);
		UserCourse userCourse = new UserCourse();
		userCourse.setCourse(course);
		userCourse.setUser(user);
		int id = (int)session.save(userCourse);
		userCourse.setId(id);
		session.getTransaction().commit();
		session.close();
		return userCourse;

	}


}
