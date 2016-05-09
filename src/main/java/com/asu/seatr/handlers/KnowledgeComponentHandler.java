package com.asu.seatr.handlers;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.KCException;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.SessionFactoryUtil;
import com.asu.seatr.utils.Utilities;

@SuppressWarnings("unchecked")
public class KnowledgeComponentHandler {


	public static KnowledgeComponent save(KnowledgeComponent kc) {
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();

		int id = (int)session.save(kc);
		kc.setId(id);
		session.getTransaction().commit();
		session.close();
		return kc;
	}

	public static KnowledgeComponent getByInternalId(int id) throws KCException
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		KnowledgeComponent kc = (KnowledgeComponent)session.get(KnowledgeComponent.class, id);
		session.close();
		if(kc == null)
		{
			throw new KCException(MyStatus.ERROR, MyMessage.KC_NOT_FOUND);
		}
		return kc;
	}

	public static List<KnowledgeComponent> getByExtCourseId(String external_course_id) throws CourseException
	{
		Course course = CourseHandler.getByExternalId(external_course_id);
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(KnowledgeComponent.class);
		cr.add(Restrictions.eq("course", course));
		List<KnowledgeComponent> kcList = cr.list();
		session.close();
		return kcList;
	}

	public static List<KnowledgeComponent> getByCourse(Course course) throws CourseException
	{
		if(course == null)
		{
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);
		}
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(KnowledgeComponent.class);
		cr.add(Restrictions.eq("course", course));
		List<KnowledgeComponent> kcList = cr.list();
		session.close();
		return kcList;
	}

	public static KnowledgeComponent getByExtKCId_Course(String external_kc_id, Course course) throws CourseException, KCException
	{
		if(course == null)
		{
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);
		}
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(KnowledgeComponent.class);
		cr.add(Restrictions.eq("external_id", external_kc_id));
		cr.add(Restrictions.eq("course", course));
		List<KnowledgeComponent> kcList = (List<KnowledgeComponent>) cr.list();
		session.close();
		if(kcList.size() < 1)
		{
			throw new KCException(MyStatus.ERROR, MyMessage.KC_NOT_FOUND);
		}
		KnowledgeComponent kc = kcList.get(0);

		return kc;
	}


	public static KnowledgeComponent readByExtId(String external_kc_id, String external_course_id) throws CourseException, KCException
	{
		Course course = CourseHandler.getByExternalId(external_course_id);
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(KnowledgeComponent.class);
		cr.add(Restrictions.eq("external_id", external_kc_id));
		cr.add(Restrictions.eq("course", course));
		List<KnowledgeComponent> kcList = (List<KnowledgeComponent>) cr.list();
		session.close();
		if(kcList.size() < 1)
		{
			throw new KCException(MyStatus.ERROR, MyMessage.KC_NOT_FOUND);
		}
		KnowledgeComponent kc = kcList.get(0);
		return kc;
	}

	public static List<KnowledgeComponent> readAll()
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		Criteria cr = session.createCriteria(KnowledgeComponent.class);
		List<KnowledgeComponent> kcList = (List<KnowledgeComponent>)cr.list();
		session.close();
		return kcList;
	}

	public static KnowledgeComponent update(KnowledgeComponent kc)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.merge(kc);
		session.getTransaction().commit();
		session.close();
		return kc;
	}
	public static void delete(KnowledgeComponent kc)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		session.beginTransaction();
		session.delete(kc);
		session.getTransaction().commit();
		session.close();
	}

}
