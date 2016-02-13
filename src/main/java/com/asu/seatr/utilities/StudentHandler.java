package com.asu.seatr.utilities;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.constants.DatabaseConstants;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.persistence.HibernateUtil;

public class StudentHandler {


	public static Student getByExternalId(String external_student_id, int external_course_id){
		SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    Criteria cr = session.createCriteria(Course.class);
		cr.add(Restrictions.eq("external_id", external_course_id));
		Course course = (Course) cr.list().get(0);
		cr = session.createCriteria(Student.class);
		cr.add(Restrictions.eq("external_id", external_student_id));
		cr.add(Restrictions.eq("course", course));
		Student student = (Student) cr.list().get(0);
		return student;
	}
	
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