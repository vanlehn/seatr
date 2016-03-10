package com.asu.seatr.handlers;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.constants.DatabaseConstants;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

public class StudentHandler {


	public static Student getByExternalId(String external_student_id, String external_course_id) throws StudentException, CourseException{
		Course course = CourseHandler.getByExternalId(external_course_id);
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(Student.class);
		cr.add(Restrictions.eq("external_id", external_student_id));
		cr.add(Restrictions.eq("course", course));
		
		List<Student> studentList = (List<Student>)cr.list();
		if(studentList.size() == 0)
		{
			throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND);
		}
		session.close();
		Student student = studentList.get(0);
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
	
	/*
	public static Student readByExtStudentId_and_ExtCourseId(String external_student_id, String external_course_id) throws CourseException, StudentException
	{
		Course course = CourseHandler.getByExternalId(external_course_id);
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(Student.class);
		cr.add(Restrictions.eq("external_id", external_student_id));
		cr.add(Restrictions.eq("course", course));
		List<Student> studentList = (List<Student>)cr.list();
		if(studentList.size() == 0)
		{
			throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND);
		}
		Student student = studentList.get(0);
		return student;
	}
	*/
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
