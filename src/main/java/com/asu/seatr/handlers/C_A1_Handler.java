package com.asu.seatr.handlers;


import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.analyzers.course.C_A1;
import com.asu.seatr.persistence.HibernateUtil;

public class C_A1_Handler {


	public static void save(String c_ext_id, double threshold, String teaching_unit) throws CourseException {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    try
	    {
		    session.beginTransaction();
		    
		    C_A1 c_a1=new C_A1();
		    c_a1.setCourseByExt_id(c_ext_id);
		    c_a1.setThreshold(threshold);
		    c_a1.setTeaching_unit(teaching_unit);
		    int id = (int)session.save(c_a1);
		    c_a1.setId(id);
		    session.getTransaction().commit();
	    }
	    finally
	    {
	    	session.close();
	    }
	}
	
	public static C_A1 read(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		C_A1 c_a1;
		try
		{
			c_a1 = (C_A1)session.get(C_A1.class, id);
		}
		finally
		{
			session.close();
		}
		return c_a1;
	}
	
	public static C_A1 readByC_Ext_id(String ext_id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		
		Criteria cr = session.createCriteria(Course.class);
		cr.add(Restrictions.eq("external_id", ext_id));
		Course course = (Course) cr.list().get(0);
		cr = session.createCriteria(C_A1.class);
		cr.add(Restrictions.eq("course", course));
		C_A1 result = (C_A1) cr.list().get(0);	
		session.close();
		return result;
	}
	
	public static void update(C_A1 c_a1)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(c_a1);
		session.getTransaction().commit();
		session.close();
	}
	public static void deleteByC_Ext_id(String ext_id) throws CourseException
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		try
			{
			session.beginTransaction();
			Course c=CourseHandler.getByExternalId(ext_id);
			Query query=session.createQuery("delete from C_A1 where course_id=:course_id");
			query.setParameter("course_id", c.getId());
			query.executeUpdate();
			session.getTransaction().commit();
			//session.close();
			}
		finally
		{
			session.close();
		}
	}
	




}
