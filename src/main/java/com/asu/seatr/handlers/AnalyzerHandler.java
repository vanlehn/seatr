package com.asu.seatr.handlers;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.asu.seatr.exceptions.AnalyzerException;
import com.asu.seatr.models.Analyzer;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

// DB Calls related to the table "analyzer"
@SuppressWarnings("unchecked")
public class AnalyzerHandler {
	// get an analyzer based on its name
	public static Analyzer getByName(String name) throws AnalyzerException{

		SessionFactory sf = Utilities.getSessionFactory();
		
		Session session = sf.openSession();
		try
		{
			Criteria cr = session.createCriteria(Analyzer.class);
			cr.add(Restrictions.eq("name", name));
			List<Analyzer> result = cr.list();
			//session.close();
			if (result.size() == 0) {
				throw new AnalyzerException(MyStatus.ERROR, MyMessage.ANALYZER_NOT_FOUND);
			}
			else 
				return result.get(0);
		}
		finally{
			session.close();
		}
	}

	// get an analyzer by its id
	public static Analyzer getById(int id) throws AnalyzerException{
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		try{
			Analyzer analyzer=session.get(Analyzer.class, id);
			//session.close();
			if(analyzer==null)
				throw new AnalyzerException(MyStatus.ERROR, MyMessage.ANALYZER_NOT_FOUND);
			else
				return analyzer;
		}
		finally{
			session.close();
		}
	}
	public static void main(String args[]) throws AnalyzerException
	{
		System.out.println(getByName("UnansweredTasks").getName());
	}

}
