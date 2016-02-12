package com.asu.seatr.utilities;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.constants.DatabaseConstants;
import com.asu.seatr.models.Student;
import com.asu.seatr.persistence.HibernateUtil;

public class StudentHandler {


	public static Student save(Student student) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    
	    int id = (int)session.save(student);
	    student.setId(id);
	    session.getTransaction().commit();
	    session.close();
	    return student;
	}
	
	public static Student read(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Student student = (Student)session.get(Student.class, id);
		session.close();
		return student;
	}
	
	public static List<Student> readAll()
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		List<Student> records = session.createQuery("from " + DatabaseConstants.STUDENT_TABLE_NAME).list();
		session.close();
		return records;
	}
	
	public static Student update(Student student)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(student);
		session.getTransaction().commit();
		session.close();
		return student;
	}
	public static void delete(Student student)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(student);
		session.getTransaction().commit();
		session.close();
	}
	

}
