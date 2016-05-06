package com.asu.seatr.handlers.analyzer1;

import java.util.List;
import java.util.Random;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;

import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.studenttask.RecommTask_A1;
import com.asu.seatr.persistence.HibernateUtil;
import com.asu.seatr.utils.SessionFactoryUtil;
import com.asu.seatr.utils.Utilities;

public class RecommTaskHandler {
	public static int numOfRecomm=3;

	private static void fillRecommTask(Student stu,Course course, int numToFilled){
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
		try{
			String sql="select task.id from task where course_id="+course.getId()+" and task.id not in "
					+ "(select task_id from student_task, st_a1 "
					+ "where student_task.student_id="+stu.getId()+" and student_task.id=st_a1.student_task_id and st_a1.d_status='done' "
					+ "union "
					+ "select task_id from recomm_task_a1 where student_id="+stu.getId()+")";
			List<Integer> l=session.createSQLQuery(sql).addScalar("task.id", IntegerType.INSTANCE).list();
			Random rand=new Random();
			session.beginTransaction();
			for(int i=0;i<numToFilled;i++){
				if(l.size()==0)
					break;
				int index=rand.nextInt(l.size());
				int taskid=l.remove(index);
				RecommTask_A1 recom=new RecommTask_A1();
				recom.setStudent(stu);
				recom.setTask(TaskHandler.read(taskid));
				recom.setCourse(course);
				session.save(recom);
			}
			session.getTransaction().commit();
		}
		finally
		{
			session.close();
		}
	}

	public static void initRecommTasks(int num){
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
		Query q=session.createQuery("delete from RecommTask_A1");
		q.executeUpdate();
		session.getTransaction().commit();

		List<Student> stu_list=StudentHandler.readAll();

		numOfRecomm=num;
		session.close();
		for(Student stu: stu_list){
			fillRecommTask(stu,stu.getCourse(),numOfRecomm);
		}
	}

	public static void completeATask(Student stu, Course course, Task task){
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
		Criteria cr=session.createCriteria(RecommTask_A1.class);
		cr.add(Restrictions.eq("student", stu));
		cr.add(Restrictions.eq("task", task));
		cr.add(Restrictions.eq("course", course));
		List<RecommTask_A1> recommList=cr.list();
		if(recommList.size()>0){
			try{
				session.beginTransaction();
				session.delete(recommList.get(0));
				session.getTransaction().commit();
				session.close();
				fillRecommTask(stu,course,1);
			}
			finally
			{
				if(session.isOpen())
					session.close();	
			}

		}
		else{
			try{
				cr=session.createCriteria(RecommTask_A1.class);
				cr.add(Restrictions.eq("student", stu));
				cr.add(Restrictions.eq("course", course));
				recommList=cr.list();
				session.close();
				if(recommList.size()<numOfRecomm)
					fillRecommTask(stu,course,numOfRecomm-recommList.size());
			}
			finally
			{
				if(session.isOpen())
					session.close();	
			}

		}

	}
}
