package com.asu.seatr.handlers.analyzer.bkt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import com.asu.seatr.exceptions.KCException;
import com.asu.seatr.handlers.KnowledgeComponentHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.kc.KC_BKT;
import com.asu.seatr.models.analyzers.student.SKC_BKT;
import com.asu.seatr.models.analyzers.studenttask.STU_N_In_A_Row;
import com.asu.seatr.models.analyzers.studenttask.StuTaskUtility_BKT;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_BKT;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.SessionFactoryUtil;
import com.asu.seatr.utils.Utilities;

@SuppressWarnings("unchecked")
public class RecommTaskHandler_BKT {
	
	public static void initOneStudent(String stuId,int course_id){  //internal student id
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		
		//init student kcs
		Session session=sf.openSession();
		Criteria cr = session.createCriteria(KC_BKT.class);
		List<KC_BKT> kc_list = (List<KC_BKT>)cr.list();
		if(kc_list==null || kc_list.isEmpty()){		
			session.close();
			return;
		}
		session.beginTransaction();
		String sql="delete from skc_bkt where skc_bkt.student_id = "+ stuId;
		Query q=session.createSQLQuery(sql);
		q.executeUpdate();
		session.getTransaction().commit();
		
		Student stu=StudentHandler.read(Integer.valueOf(stuId));
		for(KC_BKT kc : kc_list){
			SKC_BKT skc=new SKC_BKT();
			skc.setStudent(stu);
			skc.setKc(kc.getKc());
			skc.setProficiency(kc.getInit_p());
			session.beginTransaction();
			session.saveOrUpdate(skc);
			session.getTransaction().commit();
		}
		
		//init all task utilities for the student 
		sql="select skc_bkt.student_id as student_id, "
				+ "tk_bkt.task_id as task_id,"
				+ "skc_bkt.kc_id as kc_id,"
				+ "skc_bkt.proficiency as proficiency,"
				+ "k_bkt.learning_rate as l,"
				+ "k_bkt.utility as utility, "
				+ "t_bkt.type as type "
				+ "from tk_bkt,skc_bkt,k_bkt,t_bkt "
				+ "where tk_bkt.kc_id=skc_bkt.kc_id and k_bkt.kc_id=skc_bkt.kc_id and t_bkt.task_id=tk_bkt.task_id "
				+ "and t_bkt.course_id="+String.valueOf(course_id)
				+ " and skc_bkt.student_id="+stuId
				+ " order by task_id";
		SQLQuery sqlQuery=session.createSQLQuery(sql);
		sqlQuery.addScalar("student_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("task_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("kc_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("proficiency", DoubleType.INSTANCE);
		sqlQuery.addScalar("l", DoubleType.INSTANCE);
		sqlQuery.addScalar("utility", DoubleType.INSTANCE);
		sqlQuery.addScalar("type", StringType.INSTANCE);
		List<Object[]> result=sqlQuery.list();
		if(result==null || result.isEmpty()){
			session.close();
			return;
		}
		
		session.beginTransaction();
		sql="delete from stu_bkt where stu_bkt.student_id = "+stuId;
		q=session.createSQLQuery(sql);
		q.executeUpdate();
		session.getTransaction().commit();
		List<Double> kc_p_list=new LinkedList<Double>();
		List<Double> kc_l_list=new LinkedList<Double>();
		List<Double> kc_u_list=new LinkedList<Double>();
		int curtaskid=(int) result.get(0)[1];
		String curTaskType=(String) result.get(0)[6];
		for(Object[] tkc:result){
			int tkcid=(int) tkc[1];
			if(tkcid!=curtaskid){
				TaskFeature taskFeature=Calculation_BKT.getTaskFeature(curTaskType);
				double utility=Calculation_BKT.task_utility_kc(kc_p_list, kc_l_list, kc_u_list, taskFeature.slip, taskFeature.guess);
				kc_p_list.clear();
				kc_l_list.clear();
				kc_u_list.clear();

				Task task=TaskHandler.read(curtaskid);
				StuTaskUtility_BKT stuTaskUtility=new StuTaskUtility_BKT();
				stuTaskUtility.setStudent(stu);
				stuTaskUtility.setTask(task);
				stuTaskUtility.setUtility(utility);
				session.beginTransaction();
				session.saveOrUpdate(stuTaskUtility);
				session.getTransaction().commit();

			}
			curtaskid=tkcid;
			curTaskType=(String)tkc[6];
			kc_p_list.add((double)tkc[3]);
			kc_l_list.add((double)tkc[4]);
			kc_u_list.add((double)tkc[5]);
		}
		TaskFeature taskFeature=Calculation_BKT.getTaskFeature(curTaskType);
		double utility=Calculation_BKT.task_utility_kc(kc_p_list, kc_l_list, kc_u_list, taskFeature.slip, taskFeature.guess);
		kc_p_list.clear();
		kc_l_list.clear();
		kc_u_list.clear();
		Task task=TaskHandler.read(curtaskid);
		StuTaskUtility_BKT stuTaskUtility=new StuTaskUtility_BKT();
		stuTaskUtility.setStudent(stu);
		stuTaskUtility.setTask(task);
		stuTaskUtility.setUtility(utility);
		session.beginTransaction();
		session.saveOrUpdate(stuTaskUtility);
		session.getTransaction().commit();
		
		session.close();
	}
	
	public static void initOneTask(String taskId){  //internal task id
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session=sf.openSession();
		
		session.beginTransaction();
		String sql="delete from stu_bkt where stu_bkt.task_id = "+ taskId;
		Query q=session.createSQLQuery(sql);
		q.executeUpdate();
		
		sql="select skc_bkt.student_id as student_id,"
				+ "tk_bkt.task_id as task_id,"
				+ "skc_bkt.kc_id as kc_id,"
				+ "skc_bkt.proficiency as proficiency,"
				+ "k_bkt.learning_rate as l,"
				+ "k_bkt.utility as utility, "
				+ "t_bkt.type as type "
				+ "from tk_bkt,skc_bkt,k_bkt,t_bkt "
				+ "where tk_bkt.kc_id=skc_bkt.kc_id and k_bkt.kc_id=skc_bkt.kc_id and t_bkt.task_id=tk_bkt.task_id "
				+ "and t_bkt.task_id="+taskId
				+ " order by student_id";
		SQLQuery sqlQuery=session.createSQLQuery(sql);
		sqlQuery=session.createSQLQuery(sql);
		sqlQuery.addScalar("student_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("kc_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("proficiency", DoubleType.INSTANCE);
		sqlQuery.addScalar("l", DoubleType.INSTANCE);
		sqlQuery.addScalar("utility", DoubleType.INSTANCE);
		sqlQuery.addScalar("type", StringType.INSTANCE);
		List<Object[]> result=sqlQuery.list();
		
		if(result==null || result.isEmpty()){
			session.close();
			return;
		}
		
		int curstuid=(int) result.get(0)[0];
		String taskType=(String) result.get(0)[5];
		List<Double> kc_p_list=new LinkedList<Double>();
		List<Double> kc_l_list=new LinkedList<Double>();
		List<Double> kc_u_list=new LinkedList<Double>();
		Task task=TaskHandler.read(Integer.valueOf(taskId));
		TaskFeature taskFeature=Calculation_BKT.getTaskFeature(taskType);
		for(Object[] tkc:result){
			int stuid=(int) tkc[0];
			if(stuid!=curstuid){			
				double utility=Calculation_BKT.task_utility_kc(kc_p_list, kc_l_list, kc_u_list, taskFeature.slip, taskFeature.guess);
				kc_p_list.clear();
				kc_l_list.clear();
				kc_u_list.clear();
				Student stu=StudentHandler.read(curstuid);
				StuTaskUtility_BKT stuTaskUtility=new StuTaskUtility_BKT();
				stuTaskUtility.setStudent(stu);
				stuTaskUtility.setTask(task);
				stuTaskUtility.setUtility(utility);
				session.beginTransaction();
				session.saveOrUpdate(stuTaskUtility);
				session.getTransaction().commit();
			}
			curstuid=stuid;
			kc_p_list.add((double)tkc[2]);
			kc_l_list.add((double)tkc[3]);
			kc_u_list.add((double)tkc[4]);
		}
		double utility=Calculation_BKT.task_utility_kc(kc_p_list, kc_l_list, kc_u_list, taskFeature.slip, taskFeature.guess);
		kc_p_list.clear();
		kc_l_list.clear();
		kc_u_list.clear();
		Student stu=StudentHandler.read(curstuid);
		StuTaskUtility_BKT stuTaskUtility=new StuTaskUtility_BKT();
		stuTaskUtility.setStudent(stu);
		stuTaskUtility.setTask(task);
		stuTaskUtility.setUtility(utility);
		session.beginTransaction();
		session.saveOrUpdate(stuTaskUtility);
		session.getTransaction().commit();
		
		session.close();
	}
	
	public static void initOneKC(KC_BKT kc){  
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session=sf.openSession();
		
		session.beginTransaction();
		String sql="delete from skc_bkt where skc_bkt.kc_id = "+ String.valueOf(kc.getKc().getId());
		Query q=session.createSQLQuery(sql);
		q.executeUpdate();
		session.getTransaction().commit();
		
		List<Student> stu_list=StudentHandler.readAll();
		for(Student stu : stu_list){
			SKC_BKT skc=new SKC_BKT();
			skc.setStudent(stu);
			skc.setKc(kc.getKc());
			skc.setProficiency(kc.getInit_p());
			session.beginTransaction();
			session.save(skc);
			session.getTransaction().commit();
		}	
		session.close();
	}
	
	private static void initStudentKC(String[] stuIds){  //create student kc for each student kc pair
		StringBuilder idset_str=new StringBuilder();
		HashSet<String> idset=new HashSet<String>();
		idset_str.append('(');
		for (String id:stuIds){
			idset_str.append(String.valueOf(id)+",");
			idset.add(String.valueOf(id));
		}
		idset_str.setCharAt(idset_str.length()-1, ')');
		
		List<Student> stu_list=StudentHandler.readAll();
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session=sf.openSession();
		Criteria cr = session.createCriteria(KC_BKT.class);
		List<KC_BKT> kc_list = (List<KC_BKT>)cr.list();
		session.beginTransaction();
		String sql="delete from skc_bkt where skc_bkt.student_id in "+ idset_str;
		Query q=session.createSQLQuery(sql);
		q.executeUpdate();
		session.getTransaction().commit();
		
		for(Student stu : stu_list){
			if (!idset.contains(String.valueOf(stu.getId())))
				continue;
			for(KC_BKT kc : kc_list){
				SKC_BKT skc=new SKC_BKT();
				skc.setStudent(stu);
				skc.setKc(kc.getKc());
				skc.setProficiency(kc.getInit_p());
				session.beginTransaction();
				session.saveOrUpdate(skc);
				session.getTransaction().commit();
			}
		}	
		session.close();
	}
	
	private static String[] getAllStudentsIn(int course_id){
		String sql="SELECT s_bkt.student_id as sid FROM s_bkt where course_id="+String.valueOf(course_id);
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session=sf.openSession();
		SQLQuery sqlQuery=session.createSQLQuery(sql);
		sqlQuery.addScalar("sid", IntegerType.INSTANCE);
		List<Integer> result=sqlQuery.list();
		String[] ids=new String[result.size()];
		int i=0;
		for (Integer id:result)
			ids[i++]=String.valueOf(id);
		session.close();
		return ids;
	}
	
	public static void initStudentTaskUtility(int course_id){
		String[] stuIds=getAllStudentsIn(course_id);
		
		initStudentKC(stuIds);
		
		StringBuilder idset_str=new StringBuilder();
		HashSet<String> idset=new HashSet<String>();
		idset_str.append('(');
		for (String id:stuIds){
			idset_str.append(String.valueOf(id)+",");
			idset.add(String.valueOf(id));
		}
		idset_str.setCharAt(idset_str.length()-1, ')');
		
		//init utlity for each task: 
		String sql="select skc_bkt.student_id as student_id, "
				+ "tk_bkt.task_id as task_id,"
				+ "skc_bkt.kc_id as kc_id,"
				+ "skc_bkt.proficiency as proficiency,"
				+ "k_bkt.learning_rate as l,"
				+ "k_bkt.utility as utility, "
				+ "t_bkt.type as type "
				+ "from tk_bkt,skc_bkt,k_bkt,t_bkt "
				+ "where tk_bkt.kc_id=skc_bkt.kc_id and k_bkt.kc_id=skc_bkt.kc_id and t_bkt.task_id=tk_bkt.task_id "
				+ "and t_bkt.course_id="+String.valueOf(course_id)
				+ " order by student_id,task_id";
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		Session session=sf.openSession();
		SQLQuery sqlQuery=session.createSQLQuery(sql);
		sqlQuery.addScalar("student_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("task_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("kc_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("proficiency", DoubleType.INSTANCE);
		sqlQuery.addScalar("l", DoubleType.INSTANCE);
		sqlQuery.addScalar("utility", DoubleType.INSTANCE);
		sqlQuery.addScalar("type", StringType.INSTANCE);
		List<Object[]> result=sqlQuery.list();
		
		session.beginTransaction();
		sql="delete from stu_bkt where stu_bkt.student_id in "+idset_str;
		Query q=session.createSQLQuery(sql);
		q.executeUpdate();
		session.getTransaction().commit();
		List<Double> kc_p_list=new LinkedList<Double>();
		List<Double> kc_l_list=new LinkedList<Double>();
		List<Double> kc_u_list=new LinkedList<Double>();
		int curtaskid=(int) result.get(0)[1];
		int curstuid=(int) result.get(0)[0];
		String curTaskType=(String) result.get(0)[6];
		for(Object[] tkc:result){
			int tkcid=(int) tkc[1];
			int stuid=(int) tkc[0];
			if(tkcid!=curtaskid || curstuid!=stuid){
				TaskFeature taskFeature=Calculation_BKT.getTaskFeature(curTaskType);
				double utility=Calculation_BKT.task_utility_kc(kc_p_list, kc_l_list, kc_u_list, taskFeature.slip, taskFeature.guess);
				kc_p_list.clear();
				kc_l_list.clear();
				kc_u_list.clear();
				if (idset.contains(String.valueOf(curstuid))){
					Student stu=StudentHandler.read(curstuid);
					Task task=TaskHandler.read(curtaskid);
					StuTaskUtility_BKT stuTaskUtility=new StuTaskUtility_BKT();
					stuTaskUtility.setStudent(stu);
					stuTaskUtility.setTask(task);
					stuTaskUtility.setUtility(utility);
					session.beginTransaction();
					session.saveOrUpdate(stuTaskUtility);
					session.getTransaction().commit();
				}
			}
			curtaskid=tkcid;
			curTaskType=(String)tkc[6];
			curstuid=stuid;
			kc_p_list.add((double)tkc[3]);
			kc_l_list.add((double)tkc[4]);
			kc_u_list.add((double)tkc[5]);
		}
		TaskFeature taskFeature=Calculation_BKT.getTaskFeature(curTaskType);
		double utility=Calculation_BKT.task_utility_kc(kc_p_list, kc_l_list, kc_u_list, taskFeature.slip, taskFeature.guess);
		kc_p_list.clear();
		kc_l_list.clear();
		kc_u_list.clear();
		Student stu=StudentHandler.read(curstuid);
		Task task=TaskHandler.read(curtaskid);
		StuTaskUtility_BKT stuTaskUtility=new StuTaskUtility_BKT();
		stuTaskUtility.setStudent(stu);
		stuTaskUtility.setTask(task);
		stuTaskUtility.setUtility(utility);
		session.beginTransaction();
		session.saveOrUpdate(stuTaskUtility);
		session.getTransaction().commit();
		
		session.close();
	}
	
	public static void completeATask(Student stu, Course course, Task task,String st_type, boolean correct){
		SessionFactory sf;
		if(Utilities.isJUnitTest())
		{
			sf = SessionFactoryUtil.getSessionFactory();
		}
		else
		{	
			sf = HibernateUtil.getSessionFactory();
		}
		
		//get the current probability of mastery of all the related KCs to the task
		Session session=sf.openSession();
		String sql="select k_bkt.kc_id as kc_id, skc_bkt.proficiency as p, k_bkt.learning_rate as l "
				+ "from skc_bkt,k_bkt,tk_bkt,t_bkt "
				+ "where skc_bkt.kc_id=k_bkt.kc_id and tk_bkt.kc_id=k_bkt.kc_id and tk_bkt.task_id=t_bkt.task_id and "
				+ "t_bkt.task_id="+task.getId()+" "
				+ "and t_bkt.course_id="+course.getId()+" "
				+ "and skc_bkt.student_id="+stu.getId();
		SQLQuery sqlQuery=session.createSQLQuery(sql);
		sqlQuery.addScalar("kc_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("p", DoubleType.INSTANCE);
		sqlQuery.addScalar("l", DoubleType.INSTANCE);
		//String taskType=task.getT_BKT().getType();
		String taskType = st_type;//the student may have mc as well as an input choice and depending on what the student chose we will use the appropriate slip and guess
		TaskFeature taskFeature=Calculation_BKT.getTaskFeature(taskType);
		List<Object[]> kc_list=sqlQuery.list();
		if (kc_list==null || kc_list.isEmpty()){
			session.close();
			return;
		}
		List<Integer> kc_id_list=new LinkedList<Integer>();
		List<Double> kc_p_list=new LinkedList<Double>();
		List<Double> kc_l_list=new LinkedList<Double>();
		for (Object[] kc:kc_list){
			kc_id_list.add((Integer)kc[0]);
			kc_p_list.add((Double) kc[1]);
			kc_l_list.add((Double) kc[2]);
		}
		
		//update the probablity of mastery
		kc_p_list=Calculation_BKT.update_kc_p(kc_p_list, kc_l_list, taskFeature.slip, taskFeature.guess, correct);
		HashMap<Integer, Double> kc_newp=new HashMap<Integer, Double>();
		for (int i=0;i<kc_id_list.size();i++)
			kc_newp.put(kc_id_list.get(i), kc_p_list.get(i));
		
		//write the new probability back to database 
		String kcidSet="(";
		for(int kc_id:kc_id_list){
			kcidSet+=kc_id+",";
		}
		kcidSet=kcidSet.substring(0, kcidSet.length()-1)+")";
		Criteria cr_skc=session.createCriteria(SKC_BKT.class);
		cr_skc.add(Restrictions.eq("student", stu));
		List<SKC_BKT> skc_list=cr_skc.list();
		session.beginTransaction();
		for(SKC_BKT skc:skc_list){
			if(kc_newp.containsKey(skc.getKc().getId())){
				skc.setProficiency(kc_newp.get(skc.getKc().getId()));
				session.saveOrUpdate(skc);
			}
		}
		session.getTransaction().commit();
		
		//update the task utility
		sql="select tk_bkt.task_id as task_id,"
				+ "skc_bkt.kc_id as kc_id,"
				+ "skc_bkt.proficiency as proficiency,"
				+ "k_bkt.learning_rate as l,"
				+ "k_bkt.utility as utility, "
				+ "t_bkt.type as type "
				+ "from tk_bkt,skc_bkt,k_bkt,t_bkt "
				+ "where tk_bkt.kc_id=skc_bkt.kc_id and k_bkt.kc_id=skc_bkt.kc_id and t_bkt.task_id=tk_bkt.task_id "
				+ "and skc_bkt.student_id="+stu.getId()+" "
				+ "and tk_bkt.task_id in (select task_id from tk_bkt where kc_id in "+kcidSet+") "
				+ "order by task_id";
		
		sqlQuery=session.createSQLQuery(sql);
		sqlQuery.addScalar("task_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("kc_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("proficiency", DoubleType.INSTANCE);
		sqlQuery.addScalar("l", DoubleType.INSTANCE);
		sqlQuery.addScalar("utility", DoubleType.INSTANCE);
		sqlQuery.addScalar("type", StringType.INSTANCE);
		List<Object[]> result=sqlQuery.list();
		
		int curtaskid=(int) result.get(0)[0];
		String curTaskType=(String) result.get(0)[5];
		kc_p_list=new LinkedList<Double>();
		kc_l_list=new LinkedList<Double>();
		List<Double> kc_u_list=new LinkedList<Double>();
		
		for(Object[] tkc:result){
			int tkcid=(int) tkc[0];
			if(tkcid!=curtaskid){
				taskFeature=Calculation_BKT.getTaskFeature(curTaskType);
				double utility=Calculation_BKT.task_utility_kc(kc_p_list, kc_l_list, kc_u_list, taskFeature.slip, taskFeature.guess);
				kc_p_list.clear();
				kc_l_list.clear();
				kc_u_list.clear();
				Task curtask=TaskHandler.read(curtaskid);
				
				Criteria cr_tmp=session.createCriteria(StuTaskUtility_BKT.class);
				cr_tmp.add(Restrictions.eq("student", stu));
				cr_tmp.add(Restrictions.eq("task",curtask));
				List<StuTaskUtility_BKT> stuTaskUList= cr_tmp.list();
				if (stuTaskUList.isEmpty())
				{
					session.close();
					return;
				}
				StuTaskUtility_BKT stuTaskUtility=stuTaskUList.get(0);
				stuTaskUtility.setUtility(utility);
				session.beginTransaction();
				session.saveOrUpdate(stuTaskUtility);
				session.getTransaction().commit();
			}
			curtaskid=tkcid;
			curTaskType=(String)tkc[5];
			kc_p_list.add((double)tkc[2]);
			kc_l_list.add((double)tkc[3]);
			kc_u_list.add((double)tkc[4]);
		}
		taskFeature=Calculation_BKT.getTaskFeature(curTaskType);
		double utility=Calculation_BKT.task_utility_kc(kc_p_list, kc_l_list, kc_u_list, taskFeature.slip, taskFeature.guess);
		kc_p_list.clear();
		kc_l_list.clear();
		kc_u_list.clear();
		Task curtask=TaskHandler.read(curtaskid);
		
		Criteria cr_tmp=session.createCriteria(StuTaskUtility_BKT.class);
		cr_tmp.add(Restrictions.eq("student", stu));
		cr_tmp.add(Restrictions.eq("task",curtask));
		List<StuTaskUtility_BKT> stuTaskUList= cr_tmp.list();
		if (stuTaskUList.isEmpty())
		{
			session.close();
			return;
		}
		StuTaskUtility_BKT stuTaskUtility=stuTaskUList.get(0);
		stuTaskUtility.setUtility(utility);
		session.beginTransaction();
		session.saveOrUpdate(stuTaskUtility);
		session.getTransaction().commit();
		
		session.close();
	}
}
