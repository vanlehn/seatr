package com.asu.seatr.handlers.analyzer.bkt;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.asu.seatr.models.Course;
import com.asu.seatr.models.analyzers.kc.KC_BKT;
import com.asu.seatr.utils.Utilities;

public class KC_BKT_Handler {

	public static List<KC_BKT> readByExtCourse(Course course)
	{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		String hql = "from KC_BKT where kc.course = :pCourse";
		Query query =  session.createQuery(hql).setParameter("pCourse", course);
		List<KC_BKT> kcList = query.list();
		session.close();
		return kcList;
	}

}
