package com.asu.seatr.handlers;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.KCNotFoundException;
import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.interfaces.KCAnalyzerI;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

public class KCAnalyzerHandler {


	public static KCAnalyzerI save(KCAnalyzerI kcAnalyzer) {
	    SessionFactory sf = HibernateUtil.getSessionFactory();
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
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		KCAnalyzerI kcAnalyzer = (KCAnalyzerI)session.get(KCAnalyzerI.class, id);
		session.close();
		return kcAnalyzer;
	}
	@SuppressWarnings("unchecked")
	public static List<KCAnalyzerI> readAll(Class typeParameterClass)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		List<KCAnalyzerI> kcAnalyzerList = (List<KCAnalyzerI>)cr.list();
		session.close();
		return kcAnalyzerList;
	}
	
	public static KCAnalyzerI update(KCAnalyzerI kcAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(kcAnalyzer);
		session.getTransaction().commit();
		session.close();
		return kcAnalyzer;
	}
	public static void delete(KCAnalyzerI kcAnalyzer)
	{
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(kcAnalyzer);
		session.getTransaction().commit();
		session.close();
	}
	public static KCAnalyzerI readByExtId(Class typeParameterClass, String external_kc_id, String external_course_id) throws CourseException, KCNotFoundException
	{
		KnowledgeComponent kc = KnowledgeComponentHandler.readByExtId(external_kc_id, external_course_id);
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("kc_id", kc));
		List<KCAnalyzerI> kcAnalyzerList = (List<KCAnalyzerI>)cr.list();
		if(kcAnalyzerList.size() < 1 )
		{
			throw new KCNotFoundException(MyStatus.ERROR, MyMessage.NO_ANALYZER_FOR_KC);
		}
		return kcAnalyzerList.get(0);
	}
	public static KCAnalyzerI readByInternalKCId(Class typeParameterClass, int kc_id) throws KCNotFoundException
	{
		KnowledgeComponent kc = KnowledgeComponentHandler.getByInternalId(kc_id);
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("kc_id", kc));
		List<KCAnalyzerI> kcAnalyzerList = (List<KCAnalyzerI>)cr.list();
		if(kcAnalyzerList.size() < 1 )
		{
			throw new KCNotFoundException(MyStatus.ERROR, MyMessage.NO_ANALYZER_FOR_KC);
		}
		return kcAnalyzerList.get(0);
	}
	public static KCAnalyzerI readByKC(Class typeParameterClass, KnowledgeComponent kc) throws KCNotFoundException
	{
		if(kc == null)
		{
			throw new KCNotFoundException(MyStatus.ERROR, MyMessage.KC_NOT_FOUND);
		}
		SessionFactory sf = HibernateUtil.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(typeParameterClass);
		cr.add(Restrictions.eq("kc_id", kc));
		List<KCAnalyzerI> kcAnalyzerList = (List<KCAnalyzerI>)cr.list();
		if(kcAnalyzerList.size() < 1 )
		{
			throw new KCNotFoundException(MyStatus.ERROR, MyMessage.NO_ANALYZER_FOR_KC);
		}
		return kcAnalyzerList.get(0);
	}
}
