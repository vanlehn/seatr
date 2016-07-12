package com.asu.seatr.api.analyzer.n_in_a_row;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;

import com.asu.seatr.api.analyzer.required_optional.RecommenderAPI_Required_Optional;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.RecommException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.analyzer.n_in_a_row.RecommTaskHandler_N_In_A_Row;
import com.asu.seatr.handlers.analyzer.required_optional.RecommTaskHandler_Required_Optional;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.studenttask.STU_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.TUReader_N_In_A_Row;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

@Path("/")
public class RecommenderAPI_N_In_A_Row {
	
	static Logger logger = Logger.getLogger(RecommenderAPI_Required_Optional.class);
	
	@Path("analyzer/n_in_a_row/initutility")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response initUtility(){
		RecommTaskHandler_N_In_A_Row.initStudentTaskUtility();
		return Response.status(Status.OK)
				.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.RECOMM_TASK_INIT))
				.build();
	}
	
	@Path("analyzer/n_in_a_row/gettasks")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getRecommendedTasks(
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
			logger.error("Exception while getting tasks - analyzer 3", e);
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
	
	
	@Path("analyzer/n_in_a_row/gettasks_utility")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<TUReader_N_In_A_Row> getTasksUtility(
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
			return getUtilities(student, course, number_of_tasks);
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
			logger.error("Exception while getting tasks - analyzer 3", e);
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
	
	
	public static List<String> getRecommTasks(Student stu, Course course,int num) throws CourseException, StudentException
	{
		if(stu == null)
		{
			throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND);
		}
		if(course == null)
		{
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);
		}
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		
		String sql="SELECT task.external_id, stu_a2.utility FROM stu_a2,task "
				+ "where stu_a2.task_id=task.id and "
				+ "task.course_id="+course.getId()+" and stu_a2.student_id="+stu.getId()
				+ " order by utility desc, timestamp asc";
		SQLQuery sqlQuery=session.createSQLQuery(sql);
		sqlQuery.addScalar("task.external_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("utility", DoubleType.INSTANCE);
		sqlQuery.setMaxResults(num);
		List<Object[]> result_list=sqlQuery.list();
		session.close();
		List<String> tasklist=new LinkedList<String>();
		int i=0;
		for (Object[] stu_task_u: result_list){
			tasklist.add(String.valueOf(stu_task_u[0]));
		}
		return tasklist;
	}
	
	public static List<TUReader_N_In_A_Row> getUtilities(Student stu, Course course, int num) throws StudentException, CourseException
	{
		if(stu == null)
		{
			throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND);
		}
		if(course == null)
		{
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);
		}
		SessionFactory sf = Utilities.getSessionFactory();
		Session session = sf.openSession();
		
		String sql="SELECT task.external_id, stu_a2.utility FROM stu_a2,task "
				+ "where stu_a2.task_id=task.id and "
				+ "task.course_id="+course.getId()+" and stu_a2.student_id="+stu.getId()
				+ " order by utility desc, timestamp asc";
		SQLQuery sqlQuery=session.createSQLQuery(sql);
		sqlQuery.addScalar("task.external_id", IntegerType.INSTANCE);
		sqlQuery.addScalar("utility", DoubleType.INSTANCE);
		sqlQuery.setMaxResults(num);
		List<Object[]> result_list=sqlQuery.list();
		session.close();
		List<TUReader_N_In_A_Row> task_u_list=new LinkedList<TUReader_N_In_A_Row>();
		int i=0;
		for (Object[] stu_task_u: result_list){
			TUReader_N_In_A_Row task_u_reader=new TUReader_N_In_A_Row();
			task_u_reader.setExternal_task_id(String.valueOf(stu_task_u[0]));
			task_u_reader.setUtility(String.valueOf(stu_task_u[1]));
			task_u_list.add(task_u_reader);
		}
		return task_u_list;
	}
	
	
}
