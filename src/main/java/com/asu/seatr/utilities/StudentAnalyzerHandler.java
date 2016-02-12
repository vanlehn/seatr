package com.asu.seatr.utilities;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.models.interfaces.StudentAnalyzerI;
import com.asu.seatr.persistence.HibernateUtil;

public class StudentAnalyzerHandler {




	public static StudentAnalyzerI save(StudentAnalyzerI studentAnalyzer) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    
	    int id = (int)session.save(studentAnalyzer);
	    studentAnalyzer.setId(id);
	    session.getTransaction().commit();
	    session.close();
	    return studentAnalyzer;
	}
	
	public static StudentAnalyzerI read(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		StudentAnalyzerI studentAnalyzer = (StudentAnalyzerI)session.get(StudentAnalyzerI.class, id);
		session.close();
		return studentAnalyzer;
	}
	
	public static List<StudentAnalyzerI> readAll(String tableName)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		List<StudentAnalyzerI> records = session.createQuery("from " + tableName).list();
		session.close();
		return records;
	}
	
	public static StudentAnalyzerI update(StudentAnalyzerI studentAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(studentAnalyzer);
		session.getTransaction().commit();
		session.close();
		return studentAnalyzer;
	}
	public static void delete(StudentAnalyzerI studentAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(studentAnalyzer);
		session.getTransaction().commit();
		session.close();
	}
	








}
