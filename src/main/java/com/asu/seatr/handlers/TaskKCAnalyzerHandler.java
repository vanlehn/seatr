package com.asu.seatr.handlers;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.KCException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.interfaces.TaskKCAnalyzerI;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

public class TaskKCAnalyzerHandler {
	
	
	public static TaskKCAnalyzerI save(TaskKCAnalyzerI tkc) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    
	    int id = (int)session.save(tkc);
	    tkc.setId(id);
	    session.getTransaction().commit();
	    session.close();
	    return tkc;
	}
	/**
	 * used to save multiple task_kc maps in database
	 * @param tkcArray array of task_kc objects that need to be stored
	 * @return task_kc objects stored in databases updated with their id
	 */
	public static TaskKCAnalyzerI[] batchSave(TaskKCAnalyzerI tkcArray[])
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
	    Session session = sf.openSession();
	    session.beginTransaction();
	    for(int i = 0; i < tkcArray.length; i++)
	    {
	    	TaskKCAnalyzerI tkc = tkcArray[i];
	    	int id = (int)session.save(tkc);
	    	tkc.setId(id);
	    }
	    session.getTransaction().commit();
	    session.close();
	    return tkcArray;
	}
	
	public static void batchSaveOrUpdate(List<TaskKCAnalyzerI> tkcArray)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
	    
	    
	    for(TaskKCAnalyzerI tkc: tkcArray)
	    {	Session session = null;
	    	try {
	    		session = sf.openSession();
	    		session.beginTransaction();
	    		session.save(tkc);
	    		session.getTransaction().commit();
	    		
	    		
	    	} catch(ConstraintViolationException cve) {
	    		// entry already present so do nothing
	    	} finally {
	    		session.close();
	    	}
	    }
	    
	    	    
	}
	public static TaskKCAnalyzerI readById(int id)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		TaskKCAnalyzerI t_kcAnalyzer = (TaskKCAnalyzerI)session.get(TaskKCAnalyzerI.class, id);
		session.close();
		return t_kcAnalyzer;
	}
	@SuppressWarnings("unchecked")
	public static List<TaskKCAnalyzerI> readAll(Class typeParameterClass)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		List<TaskKCAnalyzerI> t_kcAnalyzerList = (List<TaskKCAnalyzerI>)cr.list();
		session.close();
		return t_kcAnalyzerList;
	}
	
	public static TaskKCAnalyzerI update(TaskKCAnalyzerI t_kcAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(t_kcAnalyzer);
		session.getTransaction().commit();
		session.close();
		return t_kcAnalyzer;
	}
	public static void delete(TaskKCAnalyzerI kcAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(kcAnalyzer);
		session.getTransaction().commit();
		session.close();
	}
	
	public static void batchDelete(List<TaskKCAnalyzerI> kcAnalyzerList)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		for(TaskKCAnalyzerI kcAnalyzer: kcAnalyzerList) {
			session.delete(kcAnalyzer);
		}
		session.getTransaction().commit();
		session.close();
	}
	
	
	public static TaskKCAnalyzerI readByExtId(Class typeParameterClass, String external_kc_id, String external_course_id, String external_task_id) throws CourseException, TaskException, KCException
	{
		Task task = TaskHandler.readByExtId(external_task_id, external_course_id);
		KnowledgeComponent kc = KnowledgeComponentHandler.readByExtId(external_kc_id, external_course_id);
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("task_id", task));
		cr.add(Restrictions.eq("kc_id", kc));
		List<TaskKCAnalyzerI> t_kcAnalyzerList = (List<TaskKCAnalyzerI>)cr.list();
		session.close();
		if(t_kcAnalyzerList.size() < 1)
		{
			throw new KCException(MyStatus.ERROR, MyMessage.KC_NOT_FOUND_FOR_TASK);
		}
		TaskKCAnalyzerI t_kc = t_kcAnalyzerList.get(0);
		return t_kc;
	}
	
	public static List<TaskKCAnalyzerI> readByExtCourseId(Class typeParameterClass, String external_course_id) throws CourseException, TaskException, KCException
	{
		
		List<KnowledgeComponent> kcList = KnowledgeComponentHandler.getByExtCourseId(external_course_id);
		List<TaskKCAnalyzerI> t_kcAnalyzerList = new ArrayList<TaskKCAnalyzerI>();
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		for(KnowledgeComponent kc: kcList) {
			Criteria cr = session.createCriteria(typeParameterClass);		
			cr.add(Restrictions.eq("kc", kc));
			t_kcAnalyzerList.addAll((List<TaskKCAnalyzerI>)cr.list());
		}
		 
		session.close();
		if(t_kcAnalyzerList.size() == 0)
		{
			throw new KCException(MyStatus.ERROR, MyMessage.KC_NOT_FOUND_FOR_COURSE);
		}
		//TaskKCAnalyzerI t_kc = t_kcAnalyzerList.get(0);
		//return t_kc;
		return t_kcAnalyzerList;
	}
	
	public static TaskKCAnalyzerI readByKC_Task(Class typeParameterClass, KnowledgeComponent kc, Task task) throws TaskException, KCException
	{
		if(task == null)
		{
			throw new TaskException(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND);
		}
		if(kc == null)
		{
			throw new KCException(MyStatus.ERROR, MyMessage.KC_NOT_FOUND);
		}
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("task_id", task));
		cr.add(Restrictions.eq("kc_id", kc));
		List<TaskKCAnalyzerI> t_kcAnalyzerList = (List<TaskKCAnalyzerI>)cr.list();
		session.close();
		if(t_kcAnalyzerList.size() < 1)
		{
			throw new KCException(MyStatus.ERROR, MyMessage.KC_NOT_FOUND_FOR_TASK);
		}
		TaskKCAnalyzerI t_kc = t_kcAnalyzerList.get(0);
		return t_kc;
		
	}
}
