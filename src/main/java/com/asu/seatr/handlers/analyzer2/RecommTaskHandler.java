package com.asu.seatr.handlers.analyzer2;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;

import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.KnowledgeComponentHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.student.SKC_A2;
import com.asu.seatr.models.analyzers.studenttask.RecommTask_A2;
import com.asu.seatr.models.analyzers.studenttask.STU_A2;
import com.asu.seatr.models.analyzers.task_kc.TK_A2;
import com.asu.seatr.persistence.HibernateUtil;

public class RecommTaskHandler {
	public static int numOfRecomm=1;
	public static int n_in_a_row=3;
	
	private static void fillRecommTask(Student stu,Course course, int numToFilled,double maxUtility){
		SessionFactory sf=HibernateUtil.getSessionFactory();
		Session session=sf.openSession();
		String utility_cr=" ";
		if(maxUtility!=-1)
			utility_cr+="and utility<"+maxUtility+" ";
		String sql="select task.id as tid, stu_a2.utility as utility "
				+ "from stu_a2, task "
				+ "where stu_a2.task_id=task.id and stu_a2.student_id="+stu.getId()+" and task.course_id="+course.getId()+utility_cr
				+ "order by utility desc";
		Criteria cr=session.createCriteria(STU_A2.class);
		SQLQuery sqlQuery=session.createSQLQuery(sql);
		sqlQuery.addScalar("tid", IntegerType.INSTANCE);
		sqlQuery.addScalar("utility", DoubleType.INSTANCE);
		sqlQuery.setMaxResults(numToFilled);
		List<Object[]> result_list=sqlQuery.list();
		
		session.beginTransaction();
		for(Object[] result:result_list){
			RecommTask_A2 recomm=new RecommTask_A2();
			recomm.setStudent(stu);
			recomm.setCourse(course);
			Task task=TaskHandler.read((int)result[0]);
			recomm.setTask(task);
			recomm.setUtility((double)result[1]);
			session.save(recomm);
		}
		session.getTransaction().commit();
		session.close();
	}
	
	public static void initRecommTasks(int num){
		SessionFactory sf=HibernateUtil.getSessionFactory();
		Session session=sf.openSession();
		session.beginTransaction();
		Query q=session.createQuery("delete from RecommTask_A2");
		q.executeUpdate();
		session.getTransaction().commit();
		
		List<Student> stu_list=StudentHandler.readAll();
		
		numOfRecomm=num;
		session.close();
		for(Student stu: stu_list){
				fillRecommTask(stu,stu.getCourse(),numOfRecomm,-1);
		}
	}
	
	public static void initStudentKC(){  //create student kc for each student kc pair
		List<Student> stu_list=StudentHandler.readAll();
		List<KnowledgeComponent> kc_list=KnowledgeComponentHandler.readAll();
		SessionFactory sf=HibernateUtil.getSessionFactory();
		Session session=sf.openSession();
		session.beginTransaction();
		for(Student stu : stu_list){
			for(KnowledgeComponent kc : kc_list){
				SKC_A2 skc=new SKC_A2();
				skc.setStudent(stu);
				skc.setKc(kc);
				skc.setNumber(0);
				session.save(skc);
			}
		}
		session.getTransaction().commit();
		session.close();
	}
	
	public static void initStudentTaskUtility(){
		//init utlity for each task: init STU_A2
		String sql="select skc_a2.student_id as student_id, tk_a2.task_id as task_id,skc_a2.kc_id as kc_id,skc_a2.n_in_a_row as number "
				+ "from tk_a2,skc_a2 where tk_a2.kc_id=skc_a2.kc_id order by student_id,task_id";
		SessionFactory sf=HibernateUtil.getSessionFactory();
		Session session=sf.openSession();
		SQLQuery sqlQuery=session.createSQLQuery(sql);
		sqlQuery.addScalar("student_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("task_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("kc_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("number", IntegerType.INSTANCE);
		List<Object[]> result=sqlQuery.list();
		
		session.beginTransaction();
		Query q=session.createQuery("delete from STU_A2");
		q.executeUpdate();
		session.getTransaction().commit();
		List<Integer> correctNum_list=new LinkedList<Integer>();
		int curtaskid=(int) result.get(0)[1];
		int curstuid=(int) result.get(0)[0];		
		for(Object[] tkc:result){
			int tkcid=(int) tkc[1];
			int stuid=(int) tkc[0];
			if(tkcid!=curtaskid || curstuid!=stuid){
				double utility=calUtility(correctNum_list);
				correctNum_list.clear();
				Student stu=StudentHandler.read(curstuid);
				Task task=TaskHandler.read(curtaskid);
				STU_A2 stu_a2=new STU_A2();
				stu_a2.setStudent(stu);
				stu_a2.setTask(task);
				stu_a2.setUtility(utility);
				session.beginTransaction();
				session.save(stu_a2);
				session.getTransaction().commit();
			}
			curtaskid=tkcid;
			curstuid=stuid;
			correctNum_list.add((int) tkc[3]);
		}
		double utility=calUtility(correctNum_list);
		correctNum_list.clear();
		Student stu=StudentHandler.read(curstuid);
		Task task=TaskHandler.read(curtaskid);
		STU_A2 stu_a2=new STU_A2();
		stu_a2.setStudent(stu);
		stu_a2.setTask(task);
		stu_a2.setUtility(utility);
		session.beginTransaction();
		session.save(stu_a2);
		session.getTransaction().commit();
		
		session.close();
	}
	
	private static double calUtility(List<Integer> nlist){
		int total=nlist.size();
		int master=0;
		for(int n: nlist){
			if(n>=n_in_a_row)
				master++;
		}
		if(master==total)
			return 0;
		else
			return (1+master)*1.0/(total+1);
	}
	
	public static void completeATask(Student stu, Course course, Task task, boolean correct){
		SessionFactory sf=HibernateUtil.getSessionFactory();
		Session session=sf.openSession();
		Criteria cr=session.createCriteria(TK_A2.class);
		cr.add(Restrictions.eq("task", task));
		List<TK_A2> task_kc_list=cr.list();
		List<KnowledgeComponent> affectedKCs=new LinkedList<KnowledgeComponent>();
		String kcidSet="(";
		for(TK_A2 task_kc:task_kc_list){
			affectedKCs.add(task_kc.getKc());
			kcidSet+=task_kc.getKc().getId()+",";
		}
		kcidSet=kcidSet.substring(0, kcidSet.length()-1)+")";
		Criteria cr_skc=session.createCriteria(SKC_A2.class);
		cr_skc.add(Restrictions.in("kc", affectedKCs));
		cr_skc.add(Restrictions.eq("student", stu));
		List<SKC_A2> skc_list=cr_skc.list();
		session.beginTransaction();
		if(correct){
			for(SKC_A2 skc:skc_list){
				skc.setNumber(skc.getNumber()+1);
				session.save(skc);
			}
		}
		else{
			for(SKC_A2 skc:skc_list){
				if(skc.getNumber()<n_in_a_row){
					skc.setNumber(0);
					session.save(skc);
				}
			}
		}
		session.getTransaction().commit();
		
		
		String sql="select tk_a2.task_id as task_id,skc_a2.kc_id as kc_id,skc_a2.n_in_a_row as number "
				+ "from tk_a2,skc_a2 where tk_a2.kc_id=skc_a2.kc_id and "
				+ "skc_a2.student_id="+stu.getId()+" and "
				+ "tk_a2.task_id in (select task_id from tk_a2 where kc_id in "+kcidSet+")";
		SQLQuery sqlQuery=session.createSQLQuery(sql);
		sqlQuery.addScalar("task_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("kc_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("number", IntegerType.INSTANCE);
		List<Object[]> result=sqlQuery.list();
		
		int curtaskid=(int) result.get(0)[0];
		List<Integer> affected_taskid_list=new LinkedList<Integer>();
		List<Double> utility_list=new LinkedList<Double>();
		List<Integer> correctNum_list=new LinkedList<Integer>();
		
		for(Object[] tkc:result){
			int tkcid=(int) tkc[0];
			if(tkcid!=curtaskid){
				double utility=calUtility(correctNum_list);
				affected_taskid_list.add(curtaskid);
				utility_list.add(utility);
				correctNum_list.clear();
				//update the utility in the database: STU_A2
				Criteria cr_tmp=session.createCriteria(STU_A2.class);
				cr_tmp.add(Restrictions.eq("student", stu));
				Task affected_task=TaskHandler.read(curtaskid);
				cr_tmp.add(Restrictions.eq("task",affected_task));
				STU_A2 stu_a2=(STU_A2) cr_tmp.list().get(0);
				stu_a2.setUtility(utility);
				session.beginTransaction();
				session.save(stu_a2);
				session.getTransaction().commit();
			}
			curtaskid=tkcid;
			correctNum_list.add((int) tkc[2]);
		}
		double utility_tmp=calUtility(correctNum_list);
		affected_taskid_list.add(curtaskid);
		utility_list.add(utility_tmp);
		correctNum_list.clear();
		//update the utility in the database: STU_A2
		Criteria cr_tmp=session.createCriteria(STU_A2.class);
		cr_tmp.add(Restrictions.eq("student", stu));
		Task affected_task=TaskHandler.read(curtaskid);
		cr_tmp.add(Restrictions.eq("task",affected_task));
		STU_A2 stu_a2=(STU_A2) cr_tmp.list().get(0);
		stu_a2.setUtility(utility_tmp);
		session.beginTransaction();
		session.save(stu_a2);
		session.getTransaction().commit();
		
		Criteria cr_recomm=session.createCriteria(RecommTask_A2.class);
		cr_recomm.add(Restrictions.eq("student", stu));
		cr_recomm.addOrder(Order.desc("utility"));
		List<RecommTask_A2> recomm_list=cr_recomm.list();
		int delnum=0;
		double min_utility=recomm_list.get(recomm_list.size()-1).getUtility();
		session.beginTransaction();
		for(RecommTask_A2 recomm:recomm_list){
			for(int i=0;i<affected_taskid_list.size();i++){
				int affected_taskid=affected_taskid_list.get(i);
				if(affected_taskid==recomm.getTask().getId()){
					double utility=utility_list.get(i);
					if(utility>=min_utility){
						//update the utility in database
						recomm.setUtility(utility);
						session.save(recomm);
					}
					else{
						//remove the recommended task 
						session.delete(recomm);
						delnum++;
					}
					break;
				}
			}
		}
		session.getTransaction().commit();
		if(delnum>0){
			fillRecommTask(stu,course,delnum,min_utility);
		}
		
		
	}
}
