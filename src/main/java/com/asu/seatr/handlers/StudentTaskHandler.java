package com.asu.seatr.handlers;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.constants.DatabaseConstants;
import com.asu.seatr.exceptions.CourseNotFoundException;
import com.asu.seatr.exceptions.StudentNotFoundException;
import com.asu.seatr.exceptions.TaskNotFoundException;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.StudentTask;
import com.asu.seatr.models.Task;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

public class StudentTaskHandler {

	public static StudentTask save(StudentTask studentTask) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    
	    int id = (int)session.save(studentTask);
	    studentTask.setId(id);
	    session.getTransaction().commit();
	    session.close();
	    return studentTask;
	}
	
	public static StudentTask read(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		StudentTask studentTask = (StudentTask)session.get(StudentTask.class, id);
		session.close();
		return studentTask;
	}
	
	public static List<StudentTask> readAll()
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		List<StudentTask> records = session.createQuery("from " + DatabaseConstants.STUDENT_TASK_TABLE_NAME).list();
		session.close();
		return records;
	}
	public static List<StudentTask> readByStudent(Student student)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(StudentTask.class);
		cr.add(Restrictions.eq("student", student));
		List<StudentTask> studentTaskList= cr.list();
		return studentTaskList;
	}
	public static List<StudentTask> readByStudent_Task(Student student, Task task) throws StudentNotFoundException, TaskNotFoundException
	{
		if(student == null)
		{
			Response rb = Response.status(Status.NOT_FOUND).
					entity(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND)).build();
			throw new StudentNotFoundException(rb);
		}
		if(task == null)
		{
			Response rb = Response.status(Status.NOT_FOUND).
					entity(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND)).build();
			throw new TaskNotFoundException(rb);
		}
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(StudentTask.class);
		cr.add(Restrictions.eq("student", student));
		cr.add(Restrictions.eq("task", task));
		List<StudentTask> studentTaskList = cr.list();
		return studentTaskList;
		
	}
	public static List<StudentTask> readByExtId(String external_student_id, String external_course_id, String external_task_id) throws CourseNotFoundException, TaskNotFoundException
	{
		Student student = StudentHandler.getByExternalId(external_student_id, external_course_id);
		Task task = TaskHandler.readByExtId(external_task_id, external_course_id);
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(StudentTask.class);
		cr.add(Restrictions.eq("student", student));
		cr.add(Restrictions.eq("task", task));
		List<StudentTask> studentTaskList = cr.list();
		return studentTaskList;
	}
	public static StudentTask update(StudentTask studentTask)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(studentTask);
		session.getTransaction().commit();
		session.close();
		return studentTask;
	}
	
	public static void delete(StudentTask studentTask)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(studentTask);
		session.getTransaction().commit();
		session.close();
	}
	



}