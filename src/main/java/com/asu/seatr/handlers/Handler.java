package com.asu.seatr.handlers;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.persistence.HibernateUtil;

/*
 * Common DBHandler class for generic db operations
 */
public class Handler {

	/**
	 * Truncates a Table
	 * Only to be used if you do not have anything to cascade
	 * if you have cascades, iterate the collection and delete each one individually
	 * @param myTable name of the table to be truncated
	 * @return number of affected entries
	 */
	public static int hqlTruncate(String myTable){
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		int result;
		try
			{
			session.beginTransaction();
		    String hql = String.format("delete from %s",myTable);
		    Query query = session.createQuery(hql);
		    result = query.executeUpdate();
		    session.getTransaction().commit();
			}
		finally{
			session.close();
		}
		return result;
	}
}
