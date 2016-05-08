package com.asu.seatr.api.analyzer3;

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

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.TaskAnalyzerHandler;
import com.asu.seatr.models.analyzers.task.T_A3;
import com.asu.seatr.rest.models.analyzer3.TAReader3;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

@Path("analyzer/3/tasks")
public class TaskAPI_3 {
	static Logger logger = Logger.getLogger(TaskAPI_3.class);
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TAReader3 getTask(
			@QueryParam("external_task_id") String external_task_id,
			@QueryParam("external_course_id") String external_course_id
			)
	{
		try{
			if(!Utilities.checkExists(external_course_id)) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}			
			if(!Utilities.checkExists(external_task_id)) {
				throw new TaskException(MyStatus.ERROR, MyMessage.TASK_ID_MISSING);
			}
			T_A3 ta3 = (T_A3)TaskAnalyzerHandler.readByExtId(T_A3.class, external_task_id, external_course_id);
			TAReader3 result = new TAReader3();
			result.setExternal_task_id(external_task_id);
			result.setExternal_course_id(external_course_id);
			result.setS_is_required(ta3.getS_is_required());
			result.setS_sequence_no(ta3.getS_sequence_no());
			result.setS_unit_no(ta3.getS_unit_no());

			return result;
		}

		catch(CourseException e) {
			Response rb = Response.status(Status.NOT_FOUND).
					entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}
		catch(TaskException e) {
			Response rb = Response.status(Status.NOT_FOUND).
					entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}
		catch(Exception e) {
			logger.error("Exception while getting task - analyzer 3", e);			
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);

		}
	}
	//create
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createTask(TAReader3 taReader3)
	{
		try {
			if(!Utilities.checkExists(taReader3.getExternal_course_id())) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}			
			if(!Utilities.checkExists(taReader3.getExternal_task_id())) {
				throw new TaskException(MyStatus.ERROR, MyMessage.TASK_ID_MISSING);
			}
			if(taReader3.getS_is_required() && !Utilities.checkExists(taReader3.getS_sequence_no())) {
				throw new TaskException(MyStatus.ERROR, MyMessage.SEQUENCE_NO_MISSING);
			}
			T_A3 t_a3 = new T_A3();									
			
			t_a3.setS_is_required(taReader3.getS_is_required());
			t_a3.setS_sequence_no(taReader3.getS_sequence_no());
			t_a3.setS_unit_no(taReader3.getS_unit_no());
			t_a3.createTask(taReader3.getExternal_task_id(), taReader3.getExternal_course_id(), 1);
			TaskAnalyzerHandler.save(t_a3);
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_CREATED)).build();
		}
		catch(CourseException e) {
			Response rb = Response.status(Status.OK).
					entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}
		catch(TaskException e) {
			Response rb = Response.status(Status.OK).
					entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}
		catch(ConstraintViolationException cve) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_ANALYZER_ALREADY_PRESENT)).build();
			throw new WebApplicationException(rb);
		}
		catch(Exception e){
			logger.error("Exception while creating task - analyzer 3", e);
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
	}
	//update
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateTask(TAReader3 taReader3)
	{
		try
		{
			if(!Utilities.checkExists(taReader3.getExternal_course_id())) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}			
			if(!Utilities.checkExists(taReader3.getExternal_task_id())) {
				throw new TaskException(MyStatus.ERROR, MyMessage.TASK_ID_MISSING);
			}
			if(taReader3.getS_is_required() && !Utilities.checkExists(taReader3.getS_sequence_no())) {
				throw new TaskException(MyStatus.ERROR, MyMessage.SEQUENCE_NO_MISSING);
			}
			T_A3 t_a3 = (T_A3)TaskAnalyzerHandler.readByExtId(T_A3.class, taReader3.getExternal_task_id(), taReader3.getExternal_course_id());
			if(Utilities.checkExists(taReader3.getS_is_required())) {
				t_a3.setS_is_required(taReader3.getS_is_required());
			}
			if(Utilities.checkExists(taReader3.getS_sequence_no())) {
				t_a3.setS_sequence_no(taReader3.getS_sequence_no());
			}
			if(Utilities.checkExists(taReader3.getS_unit_no())) {
				t_a3.setS_unit_no(taReader3.getS_unit_no());
			}

			TaskAnalyzerHandler.update(t_a3);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_UPDATED))
					.build();
		}

		catch(CourseException e) {
			Response rb = Response.status(Status.OK).
					entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}
		catch(TaskException e) {
			Response rb = Response.status(Status.OK).
					entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}

		catch(Exception e){		
			logger.error("Exception while updating task - analyzer 3", e);
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST))
					.build();
			throw new WebApplicationException(rb);
		}
	}

	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteTask1Analyzer(
			@QueryParam("external_course_id") String external_course_id,
			@QueryParam("external_task_id") String external_task_id
			)
	{

		try {
			if(!Utilities.checkExists(external_course_id)) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}			
			if(!Utilities.checkExists(external_task_id)) {
				throw new TaskException(MyStatus.ERROR, MyMessage.TASK_ID_MISSING);
			}
			T_A3 t_a3 = (T_A3)TaskAnalyzerHandler.readByExtId(T_A3.class, external_task_id, external_course_id);
			TaskAnalyzerHandler.delete(t_a3);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_ANALYZER_DELETED)).build();
		}

		catch(CourseException e) {
			Response rb = Response.status(Status.OK).
					entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}
		catch(TaskException e) {
			Response rb = Response.status(Status.OK).
					entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}		
		catch(Exception e){
			logger.error("Exception while deleting task - analyzer 3", e);
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
	}

}
