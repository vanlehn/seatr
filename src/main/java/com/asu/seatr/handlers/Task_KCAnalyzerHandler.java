package com.asu.seatr.handlers;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.KCNotFoundException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.interfaces.KCAnalyzerI;
import com.asu.seatr.models.interfaces.Task_KCAnalyzerI;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

public class Task_KCAnalyzerHandler {
	
	
	public static Task_KCAnalyzerI save(Task_KCAnalyzerI tkc) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    
	    int id = (int)session.save(tkc);
	    tkc.setId(id);
	    session.getTransaction().commit();
	    session.close();
	    return tkc;
	}
	public static Task_KCAnalyzerI readById(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Task_KCAnalyzerI t_kcAnalyzer = (Task_KCAnalyzerI)session.get(Task_KCAnalyzerI.class, id);
		session.close();
		return t_kcAnalyzer;
	}
	@SuppressWarnings("unchecked")
	public static List<Task_KCAnalyzerI> readAll(Class typeParameterClass)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		List<Task_KCAnalyzerI> t_kcAnalyzerList = (List<Task_KCAnalyzerI>)cr.list();
		session.close();
		return t_kcAnalyzerList;
	}
	
	public static Task_KCAnalyzerI update(Task_KCAnalyzerI t_kcAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(t_kcAnalyzer);
		session.getTransaction().commit();
		session.close();
		return t_kcAnalyzer;
	}
	public static void delete(Task_KCAnalyzerI kcAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(kcAnalyzer);
		session.getTransaction().commit();
		session.close();
	}
	public static Task_KCAnalyzerI readByExtId(Class typeParameterClass, String external_kc_id, String external_course_id, String external_task_id) throws CourseException, TaskException, KCNotFoundException
	{
		Task task = TaskHandler.readByExtId(external_task_id, external_course_id);
		KnowledgeComponent kc = KnowledgeComponentHandler.readByExtId(external_kc_id, external_course_id);
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("task_id", task));
		cr.add(Restrictions.eq("kc_id", kc));
		List<Task_KCAnalyzerI> t_kcAnalyzerList = (List<Task_KCAnalyzerI>)cr.list();
		session.close();
		if(t_kcAnalyzerList.size() < 1)
		{
			throw new KCNotFoundException(MyStatus.ERROR, MyMessage.KC_NOT_FOUND_FOR_TASK);
		}
		Task_KCAnalyzerI t_kc = t_kcAnalyzerList.get(0);
		return t_kc;
	}
	public static Task_KCAnalyzerI readByKC_Task(Class typeParameterClass, KnowledgeComponent kc, Task task) throws TaskException, KCNotFoundException
	{
		if(task == null)
		{
			throw new TaskException(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND);
		}
		if(kc == null)
		{
			throw new KCNotFoundException(MyStatus.ERROR, MyMessage.KC_NOT_FOUND);
		}
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("task_id", task));
		cr.add(Restrictions.eq("kc_id", kc));
		List<Task_KCAnalyzerI> t_kcAnalyzerList = (List<Task_KCAnalyzerI>)cr.list();
		session.close();
		if(t_kcAnalyzerList.size() < 1)
		{
			throw new KCNotFoundException(MyStatus.ERROR, MyMessage.KC_NOT_FOUND_FOR_TASK);
		}
		Task_KCAnalyzerI t_kc = t_kcAnalyzerList.get(0);
		return t_kc;
		
	}
}
