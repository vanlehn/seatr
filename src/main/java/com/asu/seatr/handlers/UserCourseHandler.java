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

public class UserCourseHandler {


	public static UserCourse save(UserCourse user){
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    int id = (int)session.save(user);
	    user.setId(id);
	    session.getTransaction().commit();
	    session.close();
	    return user;
	}
	
	public static UserCourse read(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		UserCourse user = (UserCourse)session.get(UserCourse.class, id);
		session.close();
		return user;
	}
	
	public static UserCourse read(String username, String external_course_id) throws UserException, CourseException
	{
		Course course = CourseHandler.getByExternalId(external_course_id);
		User user = UserHandler.read(username);
		
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();		
		
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

	@SuppressWarnings("unchecked")
	public static List<UserCourse> readAll()
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(UserCourse.class);
		List<UserCourse> userList = (List<UserCourse>)cr.list();
		session.close();
		return userList;
	}
	
	public static UserCourse update(UserCourse user)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(user);
		session.getTransaction().commit();
		session.close();
		return user;
	}
	public static void delete(UserCourse user)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(user);
		session.getTransaction().commit();
		session.close();
	}
	

}
