package com.asu.seatr.api.analyzer2;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.StudentTaskAnalyzerHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.handlers.analyzer2.RecommTaskHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.studenttask.ST_A2;
import com.asu.seatr.rest.models.analyzer2.STAReader2;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@Path("analyzer/2/studenttasks")
public class StudentTaskAPI {
	static Logger logger = Logger.getLogger(StudentTaskAPI.class);
	
	@Path("/inittasks")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response initRecommendedTasks(@QueryParam("number_of_tasks") Integer number_of_tasks){
		RecommTaskHandler.initRecommTasks(number_of_tasks);
		return Response.status(Status.OK)
				.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.RECOMM_TASK_INIT))
				.build();
	}
	
	@Path("/initstukc")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response initRecommendedTasks(){
		RecommTaskHandler.initStudentKC();
		return Response.status(Status.OK)
				.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.RECOMM_TASK_INIT))
				.build();
	}
	
	@Path("/initutility")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response initUtility(){
		RecommTaskHandler.initStudentTaskUtility();
		return Response.status(Status.OK)
				.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.RECOMM_TASK_INIT))
				.build();
	}
	
	
	//create student task
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createStudentTask(STAReader2 sta){
		
		try {
			ST_A2 sta2 = new ST_A2();
			sta2.createStudentTask(sta.getExternal_student_id(),sta.getExternal_course_id(),sta.getExternal_task_id(), 1);
			sta2.setD_status(sta.getD_status());
			StudentTaskAnalyzerHandler.save(sta2);
			
			Student student = StudentHandler.getByExternalId(sta.getExternal_student_id(), sta.getExternal_course_id());
			Task task = TaskHandler.readByExtId(sta.getExternal_task_id(), sta.getExternal_course_id());
			Course course=CourseHandler.getByExternalId(sta.getExternal_course_id());
			long timestamp;
			timestamp=1;
			if(sta.getD_status().equals("correct"))
				RecommTaskHandler.completeATask(student, course,task,true,timestamp);
			else if(sta.getD_status().equals("incorrect"))
				RecommTaskHandler.completeATask(student, course,task,false,timestamp);
			
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_TASK_CREATED)).build();
		}

		catch(CourseException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}
		catch(TaskException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}
		catch(StudentException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}
		catch(Exception e){
			e.printStackTrace();
			logger.error(e.getStackTrace());
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		
	}
	/*
	//update
	@Path("/1")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateStudentTask(STAReader1 sta){
		
		try
		{
			ST_A1 sta1 = (ST_A1) StudentTaskAnalyzerHandler.readByExtId(ST_A1.class, sta.getExternal_student_id(), sta.getExternal_course_id(), sta.getExternal_task_id()).get(0);
			sta1.setD_status(sta.getD_status());
			sta1.setD_time_lastattempt(sta.getD_time_lastattempt());
			StudentTaskAnalyzerHandler.update(sta1);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_TASK_UPDATED))
					.build();
		}
		catch(IndexOutOfBoundsException iob) {			 
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_TASK_NOT_FOUND))
					.build();
			throw new WebApplicationException(rb);
		}		
		catch(Exception e){			
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST))
					.build();
			throw new WebApplicationException(rb);
		}
		
	}
	//delete analyzer	
	@Path("/1")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteStudentTask1Analyzer(
			@QueryParam("external_student_id") String external_student_id,
			@QueryParam("external_course_id") String external_course_id,
			@QueryParam("external_task_id") String external_task_id
			){
		
		try
		{
			ST_A1 sta1 = (ST_A1) StudentTaskAnalyzerHandler.readByExtId(ST_A1.class,external_student_id, 
			external_course_id, external_task_id).get(0);
			StudentTaskAnalyzerHandler.delete(sta1);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_TASK_ANALYZER_DELETED)).build();
		}
		catch(IndexOutOfBoundsException iob) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_TASK_NOT_FOUND))
					.build();
			throw new WebApplicationException(rb);
		}
		catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		
	}
	//delete student task
	@Path("/")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteStudentTask(
			@QueryParam("external_student_id") String external_student_id,
			@QueryParam("external_course_id") String external_course_id,
			@QueryParam("external_task_id") String external_task_id
			)
	{

		try {
			// implement this
			ST_A1 st_a1 = (ST_A1) StudentTaskAnalyzerHandler.readByExtId
					(ST_A1.class, external_student_id, external_course_id,external_task_id).get(0);
			//delete all other analyzers here			
			StudentTaskAnalyzerHandler.delete(st_a1);
			StudentTask student_task = (StudentTask)StudentTaskHandler.readByExtId(external_student_id, external_course_id,external_task_id);
			StudentTaskHandler.delete(student_task);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_TASK_DELETED)).build();
		} catch(IndexOutOfBoundsException iob) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_TASK_NOT_FOUND))
					.build();
			throw new WebApplicationException(rb);
		}
		catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		
		
	
	}
	*/
	

}
