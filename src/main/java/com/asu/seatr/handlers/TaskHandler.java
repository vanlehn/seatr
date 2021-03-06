package com.asu.seatr.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.models.Analyzer;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.CourseAnalyzerMap;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.interfaces.RecommTaskI;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class TaskHandler {


	public static Task save(Task task){
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		try
		{
			session.beginTransaction();
			int id = (int)session.save(task);
			task.setId(id);
			session.getTransaction().commit();
		}
		finally
		{
			session.close();
		}
		return task;
	}

	public static Task read(int id)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Task task = (Task)session.get(Task.class, id);
		session.close();
		return task;
	}
	public static List<Task> readByExtCourseId(String external_course_id) throws CourseException
	{
		Course course = CourseHandler.getByExternalId(external_course_id);
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(Task.class);
		cr.add(Restrictions.eq(Task.p_course, course));
		List<Task> taskList = cr.list();
		session.close();
		return taskList;
	}

	public static Task readByExtTaskId_Course(String external_task_id, Course course) throws TaskException, CourseException
	{

		if(course == null){
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);			
		}
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(Task.class);
		cr.add(Restrictions.eq(Task.p_external_id, external_task_id));
		cr.add(Restrictions.eq(Task.p_course, course));
		List<Task> taskList = (List<Task>) cr.list();
		session.close();
		if(taskList.size() == 0){
			throw new TaskException(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND);		
		}
		Task task = taskList.get(0);

		return task;
	}
	public static List<Task> readByExtTaskList_Course(List<String> external_task_id_list, Course course) throws TaskException, CourseException
	{
		if(course == null){
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);			
		}
	SessionFactory sf = Utilities.getSessionFactory();
	Session session = sf.openSession();
	Criteria cr = session.createCriteria(Task.class);
	cr.add(Restrictions.in(Task.p_external_id, external_task_id_list));
	cr.add(Restrictions.eq(Task.p_course, course));
	List<Task> taskList = (List<Task>) cr.list();
	session.close();
	if(taskList.size() != external_task_id_list.size()){
		throw new TaskException(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND);		
	}
	return taskList;
	}
	public static Task readByExtId(String external_task_id, String external_course_id) throws CourseException, TaskException
	{
		Course course = CourseHandler.getByExternalId(external_course_id);
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		List<Task> taskList;
		try
		{
			Criteria cr = session.createCriteria(Task.class);
			cr.add(Restrictions.eq(Task.p_external_id, external_task_id));
			cr.add(Restrictions.eq(Task.p_course, course));
			taskList = (List<Task>) cr.list();
		}
		finally
		{
			session.close();
		}
		if(taskList.size() == 0){
			throw new TaskException(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND);			
		}
		Task task = taskList.get(0);

		return task;
	}

	public static List<Task> readAll()
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(Task.class);
		List<Task> taskList = (List<Task>)cr.list();
		session.close();
		return taskList;
	}

	public static List<RecommTaskI> getRecommTasks(Class typeParameterClass, Student stu, Course course) throws CourseException, StudentException
	{
		if(stu == null)
		{
			throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND);
		}
		if(course == null)
		{
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);
		}
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("course", course));
		cr.add(Restrictions.eq("student", stu));
		//cr.add(Restrictions.gt("utility", 0.0));
		List<RecommTaskI> taskList = (List<RecommTaskI>)cr.list();
		session.close();
		return taskList;
	}
	
	public static List<String> getRecommTasksExternalIdInTaskList(Class typeParameterClass, Student stu, Course course, List<Task> tasks) throws CourseException, StudentException
	{
		if(stu == null)
		{
			throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND);
		}
		if(course == null)
		{
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);
		}
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("course", course));
		cr.add(Restrictions.eq("student", stu));
		cr.add(Restrictions.in("task", tasks));
		//cr.add(Restrictions.gt("utility", 0.0));
		List<RecommTaskI> taskList = (List<RecommTaskI>)cr.list();
		session.close();
		List<String> extTaskList = new ArrayList<String>();
		for(RecommTaskI r : taskList)
		{
			extTaskList.add(r.getTask().getExternal_id());
		}
		return extTaskList;
	}

	public static Task update(Task task)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		try
		{
			session.beginTransaction();
			session.merge(task);
			session.getTransaction().commit();
		}
		finally
		{
			session.close();
		}
		return task;
	}
	public static void delete(Task task)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		try
		{
			session.beginTransaction();
			session.delete(task);
			session.getTransaction().commit();
		}
		finally
		{
			session.close();
		}
	}
	public static List<Analyzer> getAnalyzerList(Task task) throws CourseException
	{
		List<CourseAnalyzerMap> courseAnalyzerMapList = CourseAnalyzerMapHandler.getAnalyzerIdFromCourse(task.getCourse());
		ListIterator<CourseAnalyzerMap> li = courseAnalyzerMapList.listIterator();
		List<Analyzer> analyzerList = new ArrayList<Analyzer>();
		while(li.hasNext())
		{
			analyzerList.add(li.next().getAnalyzer());
		}
		return analyzerList;

	}


}
