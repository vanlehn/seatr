package com.asu.seatr.handlers;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.common.*;
import com.asu.seatr.persistence.HibernateUtil;
public class TryHandler {

	public static Try save(Try trry) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    
	    int id = (int)session.save(trry);
	    trry.setId(id);
	    session.getTransaction().commit();
	    session.close();
	    return trry;
	}
	
	public static Try read(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Try trry = (Try)session.get(Try.class, id);
		session.close();
		return trry;
	}
	
	public static List<Try> readAll()
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		List<Try> records = session.createQuery("from Try").list();
		session.close();
		return records;
	}
	
	public static Try update(Try trry)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(trry);
		session.getTransaction().commit();
		session.close();
		return trry;
	}
	public static void delete(Try trry)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(trry);
		session.getTransaction().commit();
		session.close();
	}
	
}
