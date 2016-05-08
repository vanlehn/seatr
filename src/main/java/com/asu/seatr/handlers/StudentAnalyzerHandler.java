package com.asu.seatr.handlers;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.interfaces.StudentAnalyzerI;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.SessionFactoryUtil;
import com.asu.seatr.utils.Utilities;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class StudentAnalyzerHandler {

	public static List<StudentAnalyzerI> readByExtId(Class typeParameterClass, String external_student_id, String external_course_id) throws CourseException, StudentException{
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session = sf.openSession();
		List<StudentAnalyzerI> result;
		try
		{
			Criteria cr = session.createCriteria(Course.class);
			cr.add(Restrictions.eq("external_id", external_course_id));
			List<Course> courseList = cr.list();
			if (courseList.size() == 0) {

				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);
			}		
			Course course = courseList.get(0);
			cr = session.createCriteria(Student.class);
			cr.add(Restrictions.eq("external_id", external_student_id));
			cr.add(Restrictions.eq("course", course));
			List<Student> studentList = cr.list();

			if (studentList.size() == 0) {
				throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND);
			}
			Student student = (Student) cr.list().get(0);
			cr = session.createCriteria(typeParameterClass);
			cr.add(Restrictions.eq("student", student));
			cr.add(Restrictions.eq("course", course));
			result = cr.list(); 

			if (result.size() == 0) {
				throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_ANALYZER_NOT_FOUND);
			}
		}
		finally
		{
			session.close();
		}

		return result;		
	}


	public static List<StudentAnalyzerI> readByCriteria(Class typeParameterClass, HashMap<String, Object> eqRestrictions) {
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.allEq(eqRestrictions));
		List<StudentAnalyzerI> result = cr.list();
		session.close();
		return result;		
	}


	public static StudentAnalyzerI save(StudentAnalyzerI studentAnalyzer) throws StudentException {
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session = sf.openSession();
		session.beginTransaction();
		try {
			int id = (int)session.save(studentAnalyzer);
			studentAnalyzer.setId(id);
			session.getTransaction().commit();
		} catch (ConstraintViolationException cve) {
			throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_ANALYZER_ALREADY_PRESENT);
		} finally {	    
			session.close();
		}
		return studentAnalyzer;
	}

	public static StudentAnalyzerI read(int id)
	{
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session = sf.openSession();
		StudentAnalyzerI studentAnalyzer = (StudentAnalyzerI)session.get(StudentAnalyzerI.class, id);
		session.close();
		return studentAnalyzer;
	}

	public static List<StudentAnalyzerI> readAll(String tableName)
	{
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session = sf.openSession();
		List<StudentAnalyzerI> records = session.createQuery("from " + tableName).list();
		session.close();
		return records;
	}

	public static StudentAnalyzerI update(StudentAnalyzerI studentAnalyzer)
	{
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session = sf.openSession();
		session.beginTransaction();
		//session.update(studentAnalyzer);
		session.merge(studentAnalyzer);
		session.getTransaction().commit();
		session.close();
		return studentAnalyzer;
	}
	public static void delete(StudentAnalyzerI studentAnalyzer)
	{
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(studentAnalyzer);
		session.getTransaction().commit();
		session.close();
	}

}
