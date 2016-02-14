package com.asu.seatr.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.exception.ConstraintViolationException;

import com.asu.seatr.models.analyzers.studenttask.ST_A1;
import com.asu.seatr.rest.models.STAReader1;
import com.asu.seatr.utilities.StudentAnalyzerHandler;
import com.asu.seatr.utilities.StudentTaskAnalyzerHandler;
import com.asu.seatr.utilities.StudentTaskHandler;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@Path("/studenttasks")
public class StudentTaskAPI {
	
	@Path("/get/1")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public STAReader1 getStudentTask(
		@QueryParam("external_student_id") String external_student_id, 
		@QueryParam("external_course_id") Integer external_course_id,
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
	
	@Path("/create/1")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createStudentTask(STAReader1 sta){
		
		try
		{
			ST_A1 sta1 = new ST_A1();
			sta1.createStudentTask(sta.getExternal_student_id(),sta.getExternal_course_id(),sta.getExternal_task_id(), 1);
			sta1.setD_status(sta.getD_status());
			sta1.setD_time_lastattempt(sta.getD_time_lastattempt());
			StudentTaskAnalyzerHandler.save(sta1);
			return Response.status(Status.CREATED).build();
		}
		catch (ConstraintViolationException cva){			
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_TASK_ALREADY_PRESENT)).build();			
			throw new WebApplicationException(rb);
		} catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		
	}
	
	@Path("/update/1")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateStudentTask(STAReader1 sta){
		
		try
		{
			ST_A1 sta1 = (ST_A1) StudentTaskAnalyzerHandler.readByExtId(ST_A1.class, sta.getExternal_student_id(), sta.getExternal_course_id(), sta.getExternal_task_id()).get(0);
			sta1.setD_status(sta.getD_status());
			sta1.setD_time_lastattempt(sta.getD_time_lastattempt());
			StudentTaskAnalyzerHandler.update(sta1);
			return Response.status(Status.ACCEPTED).build();
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
			
	@Path("/delete/1")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteStudentTask(STAReader1 sta){
		
		try
		{
			ST_A1 sta1 = (ST_A1) StudentTaskAnalyzerHandler.readByExtId(ST_A1.class, sta.getExternal_student_id(), 
			sta.getExternal_course_id(), sta.getExternal_task_id()).get(0);
			StudentTaskAnalyzerHandler.delete(sta1);
			return Response.status(Status.ACCEPTED).build();
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
	

}
