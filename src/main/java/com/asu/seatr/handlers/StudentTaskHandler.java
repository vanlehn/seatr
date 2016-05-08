package com.asu.seatr.handlers;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.constants.DatabaseConstants;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.StudentTask;
import com.asu.seatr.models.Task;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.SessionFactoryUtil;
import com.asu.seatr.utils.Utilities;

@SuppressWarnings("unchecked")
public class StudentTaskHandler {

	public static StudentTask save(StudentTask studentTask) {
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
		try
		{
			session.beginTransaction();

			int id = (int)session.save(studentTask);
			studentTask.setId(id);
			session.getTransaction().commit();
		}
		finally{
			session.close();}
		return studentTask;
	}

	public static StudentTask read(int id)
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
		StudentTask studentTask = (StudentTask)session.get(StudentTask.class, id);
		session.close();
		return studentTask;
	}

	public static List<StudentTask> readAll()
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
		List<StudentTask> records = session.createQuery("from " + DatabaseConstants.STUDENT_TASK_TABLE_NAME).list();
		session.close();
		return records;
	}
	public static List<StudentTask> readByStudent(Student student)
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
		Criteria cr = session.createCriteria(StudentTask.class);
		cr.add(Restrictions.eq("student", student));
		List<StudentTask> studentTaskList= cr.list();
		session.close();
		return studentTaskList;
	}
	public static List<StudentTask> readByTask(Task task)
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
		Criteria cr = session.createCriteria(StudentTask.class);
		cr.add(Restrictions.eq("task", task));
		List<StudentTask> studentTaskList= cr.list();
		session.close();
		return studentTaskList;
	}
	public static List<StudentTask> readByStudent_Task(Student student, Task task) throws StudentException, TaskException
	{
		if(student == null)
		{
			throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND);
		}
		if(task == null)
		{
			throw new TaskException(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND);
		}
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
		Criteria cr = session.createCriteria(StudentTask.class);
		cr.add(Restrictions.eq("student", student));
		cr.add(Restrictions.eq("task", task));
		List<StudentTask> studentTaskList = cr.list();
		session.close();
		return studentTaskList;

	}
	public static List<StudentTask> readByExtId(String external_student_id, String external_course_id, String external_task_id) throws CourseException, TaskException, StudentException
	{
		Student student = StudentHandler.getByExternalId(external_student_id, external_course_id);
		Task task = TaskHandler.readByExtId(external_task_id, external_course_id);
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
		Criteria cr = session.createCriteria(StudentTask.class);
		cr.add(Restrictions.eq("student", student));
		cr.add(Restrictions.eq("task", task));
		List<StudentTask> studentTaskList = cr.list();
		session.close();
		return studentTaskList;
	}

	public static List<StudentTask> readByExtIdOrderByTime(String external_student_id, String external_course_id, String external_task_id) throws CourseException, TaskException, StudentException
	{
		Student student = StudentHandler.getByExternalId(external_student_id, external_course_id);
		Task task = TaskHandler.readByExtId(external_task_id, external_course_id);
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
		Criteria cr = session.createCriteria(StudentTask.class);
		cr.add(Restrictions.eq("student", student));
		cr.add(Restrictions.eq("task", task));
		cr.addOrder(Order.desc("timestamp"));
		List<StudentTask> studentTaskList = cr.list();
		session.close();
		return studentTaskList;
	}

	public static StudentTask update(StudentTask studentTask)
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
		session.merge(studentTask);
		session.getTransaction().commit();
		session.close();
		return studentTask;
	}

	public static void delete(StudentTask studentTask)
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
		session.delete(studentTask);
		session.getTransaction().commit();
		session.close();
	}
}
