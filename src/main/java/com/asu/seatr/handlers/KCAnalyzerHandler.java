package com.asu.seatr.handlers;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.hql.internal.ast.QuerySyntaxException;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.KCException;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.interfaces.KCAnalyzerI;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.SessionFactoryUtil;
import com.asu.seatr.utils.Utilities;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class KCAnalyzerHandler {


	public static KCAnalyzerI save(KCAnalyzerI kcAnalyzer) {
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();	    
		int id = (int)session.save(kcAnalyzer);
		kcAnalyzer.setId(id);
		session.getTransaction().commit();
		session.close();
		return kcAnalyzer;
	}

	public static KCAnalyzerI readById(int id)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		KCAnalyzerI kcAnalyzer = (KCAnalyzerI)session.get(KCAnalyzerI.class, id);
		session.close();
		return kcAnalyzer;
	}
	public static List<KCAnalyzerI> readAll(Class typeParameterClass)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		List<KCAnalyzerI> kcAnalyzerList = (List<KCAnalyzerI>)cr.list();
		session.close();
		return kcAnalyzerList;
	}

	public static KCAnalyzerI update(KCAnalyzerI kcAnalyzer)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(kcAnalyzer);
		session.getTransaction().commit();
		session.close();
		return kcAnalyzer;
	}
	public static void delete(KCAnalyzerI kcAnalyzer)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(kcAnalyzer);
		session.getTransaction().commit();
		session.close();
	}
	public static KCAnalyzerI readByExtId(Class typeParameterClass, String external_kc_id, String external_course_id) throws CourseException, KCException
	{
		KnowledgeComponent kc = KnowledgeComponentHandler.readByExtId(external_kc_id, external_course_id);
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("kc_id", kc));
		List<KCAnalyzerI> kcAnalyzerList = (List<KCAnalyzerI>)cr.list();
		session.close();
		if(kcAnalyzerList.size() < 1 )
		{
			throw new KCException(MyStatus.ERROR, MyMessage.NO_ANALYZER_FOR_KC);
		}
		return kcAnalyzerList.get(0);
	}
	public static KCAnalyzerI readByInternalKCId(Class typeParameterClass, int kc_id) throws KCException
	{
		KnowledgeComponent kc = KnowledgeComponentHandler.getByInternalId(kc_id);
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("kc_id", kc));
		List<KCAnalyzerI> kcAnalyzerList = (List<KCAnalyzerI>)cr.list();
		session.close();
		if(kcAnalyzerList.size() < 1 )
		{
			throw new KCException(MyStatus.ERROR, MyMessage.NO_ANALYZER_FOR_KC);
		}
		return kcAnalyzerList.get(0);
	}
	public static KCAnalyzerI readByKC(Class typeParameterClass, KnowledgeComponent kc) throws KCException
	{
		if(kc == null)
		{
			throw new KCException(MyStatus.ERROR, MyMessage.KC_NOT_FOUND);
		}
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("kc_id", kc));
		List<KCAnalyzerI> kcAnalyzerList = (List<KCAnalyzerI>)cr.list();
		session.close();
		if(kcAnalyzerList.size() < 1 )
		{
			throw new KCException(MyStatus.ERROR, MyMessage.NO_ANALYZER_FOR_KC);
		}
		return kcAnalyzerList.get(0);
	}

	/**
	 * 
	 * @param analyzerName Analyzer name like A1, A2
	 * @param courseList 
	 * @param commit a true indicates that you want to commit the transaction in this function itself
	 * 				 a false indicates that you may want to rollback the transaction later on, so the session is returned
	 * 				 Do remember to handle the session(commit/rollback) if commit is false
	 * @return		 session if commit is false
	 * 				 null if commit is true
	 */
	public static Session hqlBatchDeleteByCourse(String analyzerName, List<Course> courseList,boolean commit)
	{
		if(courseList == null || courseList.isEmpty())
		{
			return null;
		}
		try
		{
			SessionFactory sf = Utilities.getSessionFactory();
			Session session = sf.openSession();
			session.beginTransaction();
			String hql = "select T from Task T where T.course in :courseList";
			Query query = session.createQuery(hql).setParameterList("courseList", courseList);
			List<Task> taskList = query.list();
			if(taskList == null || taskList.isEmpty()){return null;}
			hql = "delete from TK_" + analyzerName + " tk where tk.task in :taskList";
			session.createQuery(hql).setParameterList("taskList", taskList).executeUpdate();
			if(commit)
			{
				session.getTransaction().commit();
				session.close();
				return null;
			}
			return session;
		}
		catch(QuerySyntaxException e)
		{
			System.out.println("Table Not Mapped");
			return null;
		}
	}
	/**
	 * 
	 * @param analyzerName Analyzer name like A1, A2
	 * @param course 
	 * @param commit a true indicates that you want to commit the transaction in this function itself
	 * 				 a false indicates that you may want to rollback the transaction later on, so the session is returned
	 * 				 Do remember to handle the session(commit/rollback) if commit is false
	 * @return		 session if commit is false
	 * 				 null if commit is true
	 */
	public static Session hqlDeleteByCourse(String analyzerName, Course course,boolean commit)
	{
		try
		{
			SessionFactory sf = Utilities.getSessionFactory();
			Session session = sf.openSession();
			session.beginTransaction();
			String hql = "select T from Task T where T.course = :course";
			Query query = session.createQuery(hql).setParameter("course", course);
			List<Task> taskList = query.list();
			if(taskList == null || taskList.isEmpty()){return null;}
			hql = "delete from TaskKC_" + analyzerName + " tk where tk.task in :taskList";
			session.createQuery(hql).setParameterList("taskList", taskList).executeUpdate();
			if(commit)
			{
				session.getTransaction().commit();
				session.close();
				return null;
			}
			return session;
		}
		catch(QuerySyntaxException e)
		{
			System.out.println("Table Not Mapped");
			return null;
		}
	}
}
