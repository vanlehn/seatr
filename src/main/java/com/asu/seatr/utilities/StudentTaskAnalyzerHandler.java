package com.asu.seatr.utilities;

import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.StudentTask;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.interfaces.StudentAnalyzerI;
import com.asu.seatr.models.interfaces.StudentTaskAnalyzerI;
import com.asu.seatr.persistence.HibernateUtil;

public class StudentTaskAnalyzerHandler {

	
	public static List<StudentTaskAnalyzerI> readByExtId(Class typeParameterClass, String external_student_id, Integer external_course_id, String external_task_id) {
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(Course.class);
		cr.add(Restrictions.eq("external_id", external_course_id));
		Course course = (Course) cr.list().get(0);
		cr = session.createCriteria(Student.class);
		cr.add(Restrictions.eq("external_id", external_student_id));
		cr.add(Restrictions.eq("course", course));
		Student student = (Student) cr.list().get(0);
		cr = session.createCriteria(Task.class);
		cr.add(Restrictions.eq("external_id", external_task_id));
		cr.add(Restrictions.eq("course", course));
		Task task = (Task) cr.list().get(0);
		cr = session.createCriteria(StudentTask.class);
		cr.add(Restrictions.eq("student", student));
		cr.add(Restrictions.eq("task", task));
		StudentTask student_task = (StudentTask) cr.list().get(0);		
		cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("studentTask", student_task));		
		
		List<StudentTaskAnalyzerI> result = cr.list(); 
		session.close();
		return result;		
	}
	

	public static List<StudentTaskAnalyzerI> readByCriteria(Class typeParameterClass, HashMap<String, Object> eqRestrictions) {
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.allEq(eqRestrictions));
		List<StudentTaskAnalyzerI> result = cr.list();
		session.close();
		return result;		
	}
	

	public static StudentTaskAnalyzerI save(StudentTaskAnalyzerI studentTaskAnalyzer) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    
	    int id = (int)session.save(studentTaskAnalyzer);
	    studentTaskAnalyzer.setId(id);
	    session.getTransaction().commit();
	    session.close();
	    return studentTaskAnalyzer;
	}
	
	public static StudentTaskAnalyzerI read(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		StudentTaskAnalyzerI studentTaskAnalyzer = (StudentTaskAnalyzerI)session.get(StudentTaskAnalyzerI.class, id);
		session.close();
		return studentTaskAnalyzer;
	}
	
	public static List<StudentTaskAnalyzerI> readAll(String tableName)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		List<StudentTaskAnalyzerI> records = session.createQuery("from " + tableName).list();
		session.close();
		return records;
	}
	
	public static StudentTaskAnalyzerI update(StudentTaskAnalyzerI studentTaskAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.update(studentTaskAnalyzer);
		//session.merge(studentTaskAnalyzer);
		session.getTransaction().commit();
		session.close();
		return studentTaskAnalyzer;
	}
	public static void delete(StudentTaskAnalyzerI studentTaskAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(studentTaskAnalyzer);
		session.getTransaction().commit();
		session.close();
	}
	
}
