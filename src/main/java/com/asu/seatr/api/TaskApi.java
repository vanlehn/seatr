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

import com.asu.seatr.handlers.StudentAnalyzerHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.TaskAnalyzerHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.student.S_A1;
import com.asu.seatr.models.analyzers.task.T_A1;
import com.asu.seatr.rest.models.TAReader1;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@Path("/tasks")
public class TaskApi {
	
	@Path("/1")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TAReader1 getTask(
	@QueryParam("external_task_id") String external_task_id,
	@QueryParam("external_course_id") String external_course_id
			)
	{
		try{
			T_A1 tal = (T_A1)TaskAnalyzerHandler.readByExtId(T_A1.class, external_task_id, external_course_id).get(0);
			TAReader1 result = new TAReader1();
			result.setExternal_task_id(external_task_id);
			result.setExternal_course_id(external_course_id);
			result.setS_difficulty_level(tal.getS_difficulty_level());
			return result;
		}
		catch(IndexOutOfBoundsException iob) {			
			Response rb = Response.status(Status.NOT_FOUND).
					entity(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND)).build();
			throw new WebApplicationException(rb);
		} catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
	}
	//create
	@Path("/1")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createTask(TAReader1 taReader1)
	{
		try
		{
			T_A1 t_a1 = new T_A1(); 
			t_a1.createTask(taReader1.getExternal_task_id(), taReader1.getExternal_course_id(), 1);
			t_a1.setS_difficulty_level(taReader1.getS_difficulty_level());
			TaskAnalyzerHandler.save(t_a1);
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_CREATED)).build();
		}
		catch (ConstraintViolationException cva){			
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_ALREADY_PRESENT)).build();			
			throw new WebApplicationException(rb);
		} catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
	}
	//update
	@Path("/1")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateTask(TAReader1 taReader1)
	{
		try
		{
			T_A1 t_a1 = (T_A1)TaskAnalyzerHandler.readByExtId(T_A1.class, taReader1.getExternal_task_id(), taReader1.getExternal_course_id()).get(0);
			if(taReader1.getS_difficulty_level()!=null)
				{
					t_a1.setS_difficulty_level(taReader1.getS_difficulty_level());
				}
			TaskAnalyzerHandler.update(t_a1);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_UPDATED))
					.build();
		}
		catch(IndexOutOfBoundsException iob) {			 
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND))
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
	@Path("/1")
	@DELETE
	public Response deleteTask1Analyzer(
			@QueryParam("external_course_id") String external_course_id,
			@QueryParam("external_task_id") String external_task_id
			)
	{
		try
		{
		System.out.println("delete operation");
		T_A1 t_a1 = (T_A1)TaskAnalyzerHandler.readByExtId(T_A1.class, external_task_id, external_course_id).get(0);
		TaskAnalyzerHandler.delete(t_a1);
		return Response.status(Status.OK)
				.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_ANALYZER_DELETED)).build();
		}
		catch(IndexOutOfBoundsException iob) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND))
					.build();
			throw new WebApplicationException(rb);
		}
		catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
	}
	@Path("/")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTask(
			@QueryParam("external_course_id") String external_course_id,
			@QueryParam("external_task_id") String external_task_id
			)
	{

		try {
			// implement this
			T_A1 t_a1 = (T_A1) TaskAnalyzerHandler.readByExtId
					(T_A1.class, external_task_id, external_course_id).get(0);
			//delete all other analyzers here			
			TaskAnalyzerHandler.delete(t_a1);
			Task task = (Task)TaskHandler.readByExtId(external_task_id, external_course_id);
			TaskHandler.delete(task);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_DELETED)).build();
		} catch(IndexOutOfBoundsException iob) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND))
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
