package com.asu.seatr.api.bkt;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.handlers.analyzer.bkt.RecommTaskHandler_BKT;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.rest.models.analyzer.bkt.TLReader_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.TUReader_BKT;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

@Path("/")
public class RecommenderAPI_BKT {
	
	static Logger logger = Logger.getLogger(RecommenderAPI_BKT.class);
	
	@Path("analyzer/bkt/initutility")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response initUtility(@QueryParam("external_course_id") String external_course_id) throws CourseException{
		if(!Utilities.checkExists(external_course_id)) {
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
		}
		Course c=CourseHandler.getByExternalId(external_course_id);
		RecommTaskHandler_BKT.initStudentTaskUtility(c.getId());
		return Response.status(Status.OK)
				.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.RECOMM_TASK_INIT))
				.build();
	}
		
	//returns the prediction of whether a student will solve a question as right or wrong
	@Path("analyzer/bkt/accuracy_predict")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Double getStudentResponsePrediction(
			@QueryParam("external_course_id") String external_course_id,
			@QueryParam("external_student_id") String external_student_id,
			@QueryParam("external_task_id") String external_task_id
			)
	{
		/*
		Random r = new Random();
		Integer dummy_ans = r.nextInt(2);
		return dummy_ans;
		*/
		Long requestTimestamp = System.currentTimeMillis();
		try
		{
			if(!Utilities.checkExists(external_course_id)) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}
			if(!Utilities.checkExists(external_student_id)) {
				throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_ID_MISSING);
			}
			if(!Utilities.checkExists(external_task_id)) {
				throw new TaskException(MyStatus.ERROR, MyMessage.TASK_ID_MISSING);
			}
			Student student = StudentHandler.getByExternalId(external_student_id, external_course_id);
			Task task = TaskHandler.readByExtId(external_task_id, external_course_id);
			Course course=CourseHandler.getByExternalId(external_course_id);
			List<Double> p_list = RecommTaskHandler_BKT.getKcMasteryList(student, course, task);
			Double average = 0.0;
			if(p_list == null || p_list.size()==0)
			{
				return average;
			}
			for(double p : p_list)
			{
				average += p;
			}
			average = average/p_list.size();
			return average;
		}
		catch(StudentException e)
		{	
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);	
		}
		catch(CourseException e) {
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);			
		}
		catch(TaskException e) {
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}
		catch(Exception e)
		{
			logger.error("Exception while getting tasks - analyzer bkt", e);
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		finally
		{
			Long responseTimestamp = System.currentTimeMillis();
			Double response = (responseTimestamp -  requestTimestamp)/1000d;
			Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp/1000);		
		}
		
	}
	
	@Path("analyzer/bkt/get_recomm_tasks")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<TUReader_BKT> getRecommTasks(
			@QueryParam("external_student_id") String external_student_id,
			@QueryParam("external_course_id") String external_course_id,
			@QueryParam("number_of_tasks") Integer number_of_tasks
			)
	{
		Long requestTimestamp = System.currentTimeMillis();
		try
		{
			if(!Utilities.checkExists(external_course_id)) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}
			if(!Utilities.checkExists(external_student_id)) {
				throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_ID_MISSING);
			}
			if(!Utilities.checkExists(number_of_tasks)) {
				//default number of tasks
				number_of_tasks = 5;
			}
			Course course = CourseHandler.getByExternalId(external_course_id);
			Student student = StudentHandler.getByExternalId(external_student_id, external_course_id);
			return getRecommTasks(student, course, number_of_tasks);
		}
		catch(StudentException e)
		{	
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);	
		}
		catch(CourseException e) {
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);			
		}

		catch(Exception e)
		{
			logger.error("Exception while getting tasks - analyzer bkt", e);
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		finally
		{
			Long responseTimestamp = System.currentTimeMillis();
			Double response = (responseTimestamp -  requestTimestamp)/1000d;
			Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp/1000);		
		}
	}
	
	@Path("analyzer/bkt/get_utility")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<TUReader_BKT> getTasksUtility(TLReader_BKT taskList
			)
	{
		Long requestTimestamp = System.currentTimeMillis();
		try
		{
			Course course = CourseHandler.getByExternalId(taskList.getExternal_course_id());
			Student student = StudentHandler.getByExternalId(taskList.getExternal_student_id(), taskList.getExternal_course_id());
			return getScale(student, course, taskList.getExternal_task_ids());
		}
		catch(StudentException e)
		{	
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);	
		}
		catch(CourseException e) {
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);			
		}
		catch(TaskException e) {
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);			
		}

		catch(Exception e)
		{
			logger.error("Exception while getting tasks - analyzer bkt", e);
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		finally
		{
			Long responseTimestamp = System.currentTimeMillis();
			Double response = (responseTimestamp -  requestTimestamp)/1000d;
			Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp/1000);		
		}
	}
	
	private static List<TUReader_BKT> getScale(Student stu, Course course, String[] externalTaskIds) throws StudentException, CourseException, TaskException{
		if(stu == null || stu.getS_BKT()==null)
		{
			throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND);
		}
		if(course == null)
		{
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);
		}
		HashSet<String> unProcessedEIds=new HashSet<String>();
		for (String eid:externalTaskIds)
			unProcessedEIds.add(eid);
		HashSet<String> allEIds=(HashSet<String>) unProcessedEIds.clone();
		SessionFactory sf = Utilities.getSessionFactory();
		
		boolean already_init_stu=false;
		boolean already_init_tasks=false;
		List<TUReader_BKT> task_u_list=new LinkedList<TUReader_BKT>();
		int count=0;
		do{
			task_u_list=new LinkedList<TUReader_BKT>();
			Session session = sf.openSession();
			String sql="SELECT task.external_id, stu_bkt.utility FROM stu_bkt,task "
					+ "where stu_bkt.task_id=task.id and "
					+ "task.course_id="+course.getId()+" and stu_bkt.student_id="+stu.getId()
					+ " order by utility desc";
			SQLQuery sqlQuery=session.createSQLQuery(sql);
			sqlQuery.addScalar("task.external_id", StringType.INSTANCE);
			sqlQuery.addScalar("utility", DoubleType.INSTANCE);
			List<Object[]> result_list=sqlQuery.list();
			List<String> taskIds=new LinkedList<String>();
			List<Double> utilities=new LinkedList<Double>();
			double max=0.0001;
			for (Object[] stu_task_u: result_list){
				taskIds.add((String)stu_task_u[0]);
				utilities.add((Double) stu_task_u[1]);
				if ((Double) stu_task_u[1]>max)
					max=(Double) stu_task_u[1];
				unProcessedEIds.remove((String)stu_task_u[0]);
			}
			DecimalFormat df=new DecimalFormat("0.000");
			for (int i=0;i<taskIds.size();i++){
				if (!allEIds.contains(taskIds.get(i)))
					continue;
				TUReader_BKT task_u_reader=new TUReader_BKT();
				task_u_reader.setExternal_task_id(taskIds.get(i));
				double scale=utilities.get(i)/max;
				scale=scale>1?1:scale;
				scale=scale<0?0:scale;
				task_u_reader.setUtility(df.format(scale));
				task_u_list.add(task_u_reader);
			}
			
			if(task_u_list.isEmpty() && !already_init_stu){
				RecommTaskHandler_BKT.initOneStudent(String.valueOf(stu.getId()), stu.getCourse().getId());
				already_init_stu=true;
			}
			else if(!unProcessedEIds.isEmpty() && !already_init_tasks){
				for (String eid:unProcessedEIds){
					Task task=TaskHandler.readByExtId(eid, course.getExternal_id());
					RecommTaskHandler_BKT.initOneTask(String.valueOf(task.getId()));
				}
				already_init_tasks=true;
			}
			session.close();
			count++;
		}while((task_u_list.isEmpty() || !unProcessedEIds.isEmpty()) && count<2);
		return task_u_list;
	}
	
	private static List<TUReader_BKT> getUtility(Student stu, Course course, String[] externalTaskIds) throws StudentException, CourseException, TaskException{
		if(stu == null || stu.getS_BKT()==null)
		{
			throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND);
		}
		if(course == null)
		{
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);
		}
		HashSet<String> unProcessedEIds=new HashSet<String>();
		for (String eid:externalTaskIds)
			unProcessedEIds.add(eid);
		SessionFactory sf = Utilities.getSessionFactory();
		StringBuilder idset=new StringBuilder();
		idset.append('(');
		for (String id : externalTaskIds){
			idset.append(id+",");
		}
		idset.setCharAt(idset.length()-1, ')');
		
		boolean already_init_stu=false;
		boolean already_init_tasks=false;
		List<TUReader_BKT> task_u_list=new LinkedList<TUReader_BKT>();
		int count=0;
		do{
			task_u_list=new LinkedList<TUReader_BKT>();
			Session session = sf.openSession();
			String sql="SELECT task.external_id, stu_bkt.utility FROM stu_bkt,task "
					+ "where stu_bkt.task_id=task.id and "
					+ "task.course_id="+course.getId()+" and stu_bkt.student_id="+stu.getId()
					+ " and task.external_id in "+ idset
					+ " order by utility desc";
			SQLQuery sqlQuery=session.createSQLQuery(sql);
			sqlQuery.addScalar("task.external_id", IntegerType.INSTANCE);
			sqlQuery.addScalar("utility", DoubleType.INSTANCE);
			List<Object[]> result_list=sqlQuery.list();
			DecimalFormat df=new DecimalFormat("0.000");
			for (Object[] stu_task_u: result_list){
				TUReader_BKT task_u_reader=new TUReader_BKT();
				task_u_reader.setExternal_task_id(String.valueOf(stu_task_u[0]));
				task_u_reader.setUtility(df.format(stu_task_u[1]));
				task_u_list.add(task_u_reader);
				unProcessedEIds.remove(String.valueOf(stu_task_u[0]));
			}
			if(task_u_list.isEmpty() && !already_init_stu){
				RecommTaskHandler_BKT.initOneStudent(String.valueOf(stu.getId()), stu.getCourse().getId());
				already_init_stu=true;
			}
			else if(!unProcessedEIds.isEmpty() && !already_init_tasks){
				for (String eid:unProcessedEIds){
					Task task=TaskHandler.readByExtId(eid, course.getExternal_id());
					RecommTaskHandler_BKT.initOneTask(String.valueOf(task.getId()));
				}
				already_init_tasks=true;
			}
			session.close();
			count++;
		}while((task_u_list.isEmpty() || !unProcessedEIds.isEmpty()) && count<2);
		return task_u_list;
	}
	
	private static List<TUReader_BKT> getRecommTasks(Student stu, Course course, int num) throws StudentException, CourseException
	{
		if(stu == null || stu.getS_BKT()==null)
		{
			throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND);
		}
		if(course == null)
		{
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);
		}
		SessionFactory sf = Utilities.getSessionFactory();
		
		
		boolean already_init_stu=false;
		List<TUReader_BKT> task_u_list=new LinkedList<TUReader_BKT>();
		int count=0;
		do{
			Session session = sf.openSession();
			String sql="SELECT task.external_id, stu_bkt.utility FROM stu_bkt,task,t_bkt "
					+ "where stu_bkt.task_id=task.id and task.id = t_bkt.task_id and "
					+ "task.course_id="+course.getId()+" and stu_bkt.student_id="+stu.getId()
					+ " order by utility desc";
			SQLQuery sqlQuery=session.createSQLQuery(sql);
			sqlQuery.addScalar("task.external_id", IntegerType.INSTANCE);
			sqlQuery.addScalar("utility", DoubleType.INSTANCE);
			sqlQuery.setMaxResults(num);
			List<Object[]> result_list=sqlQuery.list();
			
			DecimalFormat df=new DecimalFormat("0.000");
			for (Object[] stu_task_u: result_list){
				TUReader_BKT task_u_reader=new TUReader_BKT();
				task_u_reader.setExternal_task_id(String.valueOf(stu_task_u[0]));
				task_u_reader.setUtility(df.format(stu_task_u[1]));
				task_u_list.add(task_u_reader);
			}
			if (task_u_list.isEmpty() && !already_init_stu){
				RecommTaskHandler_BKT.initOneStudent(String.valueOf(stu.getId()), stu.getCourse().getId());
				already_init_stu=true;
			}
			session.close();
			count++;
		} while(task_u_list.isEmpty() && count<2);
		
		return task_u_list;
	}
	
	
}