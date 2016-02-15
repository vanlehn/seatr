package com.asu.seatr.utilities;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.interfaces.StudentAnalyzerI;
import com.asu.seatr.persistence.HibernateUtil;

public class StudentAnalyzerHandler {

	
	public static List<StudentAnalyzerI> readByExtId(Class typeParameterClass, String external_student_id, String external_course_id) {
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(Course.class);
		cr.add(Restrictions.eq("external_id", external_course_id));
		Course course = (Course) cr.list().get(0);
		cr = session.createCriteria(Student.class);
		cr.add(Restrictions.eq("external_id", external_student_id));
		cr.add(Restrictions.eq("course", course));
		Student student = (Student) cr.list().get(0);
		cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("student", student));
		cr.add(Restrictions.eq("course", course));
		List<StudentAnalyzerI> result = cr.list(); 
		session.close();
		return result;		
	}
	

	public static List<StudentAnalyzerI> readByCriteria(Class typeParameterClass, HashMap<String, Object> eqRestrictions) {
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.allEq(eqRestrictions));
		List<StudentAnalyzerI> result = cr.list();
		session.close();
		return result;		
	}
	

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
		//session.update(studentAnalyzer);
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
