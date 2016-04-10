package com.asu.seatr.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
import com.asu.seatr.handlers.analyzer1.RecommTaskHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.studenttask.ST_A1;
import com.asu.seatr.rest.models.STAReader1;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@Path("analyzer/1/studenttasks")
public class StudentTaskAPI_1 {
	static Logger logger = Logger.getLogger(StudentTaskAPI_1.class);
	/*get,update and delete operations have been disabled because a single student can have multiple records of
	the same task associated with it.
	*/
	/*
	//read student task
	@Path("/1")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public STAReader1 getStudentTask(
		@QueryParam("external_student_id") String external_student_id, 
		@QueryParam("external_course_id") String external_course_id,
		@QueryParam("external_task_id") String external_task_id
		) {
		try
		{
			ST_A1 sta = (ST_A1) StudentTaskAnalyzerHandler.readByExtId(ST_A1.class, external_student_id, external_course_id, external_task_id).get(0);
			STAReader1 star = new STAReader1();
			star.setExternal_course_id(external_course_id);
			star.setExternal_student_id(external_student_id);
			star.setExternal_task_id(external_task_id);
			star.setD_status(sta.getD_status());
			star.setD_time_lastattempt(sta.getD_time_lastattempt());
			return star;
		}
		catch(IndexOutOfBoundsException iob) {			
			Response rb = Response.status(Status.NOT_FOUND).
					entity(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_TASK_NOT_FOUND)).build();
			throw new WebApplicationException(rb);
		} catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
	}
	*/
	//create student task
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createStudentTask(STAReader1 sta){
		
		try {
			ST_A1 sta1 = new ST_A1();
			sta1.createStudentTask(sta.getExternal_student_id(),sta.getExternal_course_id(),sta.getExternal_task_id(), 1);
			sta1.setD_status(sta.getD_status());
			sta1.setD_time_lastattempt(sta.getD_time_lastattempt());
			StudentTaskAnalyzerHandler.save(sta1);
			
			Student student = StudentHandler.getByExternalId(sta.getExternal_student_id(), sta.getExternal_course_id());
			Task task = TaskHandler.readByExtId(sta.getExternal_task_id(), sta.getExternal_course_id());
			Course course=CourseHandler.getByExternalId(sta.getExternal_course_id());
			if(sta.getD_status().equals("done"))
				RecommTaskHandler.completeATask(student, course,task);
			
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
