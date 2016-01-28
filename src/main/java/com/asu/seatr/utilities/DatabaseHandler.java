package com.asu.seatr.utilities;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.hibernate.*;
public class DatabaseHandler {

	public static Demo save(Demo demo) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    
	    int id = (int)session.save(demo);
	    demo.setId(id);
	    session.getTransaction().commit();
	    session.close();
	    return demo;
	}
	
	public static Demo read(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Demo demo = (Demo)session.get(Demo.class, id);
		session.close();
		return demo;
	}
	
	public static List readAll()
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		List records = session.createQuery("from Demo").list();
		session.close();
		return records;
	}
	
	public static Demo update(Demo demo)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(demo);
		session.getTransaction().commit();
		session.close();
		return demo;
	}
	public static void delete(Demo demo)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(demo);
		session.getTransaction().commit();
		session.close();
	}
	
}
