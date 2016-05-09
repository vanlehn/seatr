package com.asu.seatr.handlers;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.asu.seatr.exceptions.UserException;
import com.asu.seatr.models.User;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

@SuppressWarnings("unchecked")
public class UserHandler {


	public static User save(User user) throws UserException{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		try {
			session.beginTransaction();
			int id = (int)session.save(user);
			user.setId(id);
			session.getTransaction().commit();
		} catch(ConstraintViolationException cve) {
			throw new UserException(MyStatus.ERROR, MyMessage.USER_ALREADY_PRESENT);
		} finally {
			session.close();
		}		    
		return user;
	}

	public static User read(int id)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		User user = (User)session.get(User.class, id);
		session.close();
		return user;
	}

	public static User read(String username) throws UserException
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(User.class);
		cr.add(Restrictions.eq("username", username));
		List<User> userList = (List<User>)cr.list();
		if(userList.size() == 0) {
			throw new UserException(MyStatus.ERROR, MyMessage.USER_NOT_FOUND);
		}		
		session.close();
		return userList.get(0);
	}

	public static List<User> readAll()
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(User.class);
		List<User> userList = (List<User>)cr.list();
		session.close();
		return userList;
	}

	public static User update(User user)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(user);
		session.getTransaction().commit();
		session.close();
		return user;
	}
	public static void delete(User user)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(user);
		session.getTransaction().commit();
		session.close();
	}


}
