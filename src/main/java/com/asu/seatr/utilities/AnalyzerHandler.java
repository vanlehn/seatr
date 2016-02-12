package com.asu.seatr.utilities;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;

import com.asu.seatr.models.interfaces.CourseAnalyzerI;
import com.asu.seatr.persistence.HibernateUtil;

public class AnalyzerHandler<T> {



	public int save(T analyzer) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    
	    int id = (int)session.save(analyzer);
	    //courseAnalyzer.setId(id);
	    session.getTransaction().commit();
	    session.close();
	    return id;
	}
	
	public T read(int id, Class <T> typeParameterClass)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		@SuppressWarnings("unchecked")
		T analyzer = (T)session.get(CourseAnalyzerI.class, id);
		session.close();
		return analyzer;
	}
	
	public List<T> readAll(String tableName)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		@SuppressWarnings("unchecked")
		List<T> records = session.createQuery("from " + tableName).list();
		session.close();
		return records;
	}
	
	public T update(T analyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(analyzer);
		session.getTransaction().commit();
		session.close();
		return analyzer;
	}
	public void delete(T analyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(analyzer);
		session.getTransaction().commit();
		session.close();
	}
	public String[] getColumnNames(Class<T> typeParameterClass)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		ClassMetadata classMetadata = sf.getClassMetadata(typeParameterClass);
		String propertyNames[] = classMetadata.getPropertyNames();
		return propertyNames;
	}
}
