package com.asu.seatr.api.analyzer.unansweredtasks;

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
import com.asu.seatr.models.analyzers.task.Task_UnansweredTasks;
import com.asu.seatr.rest.models.analyzer.unansweredtasks.TAReader_UnansweredTasks;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

// Routes for Tasks for analyzer 1
@Path("analyzer/unansweredtasks/tasks")
public class TaskAPI_UnansweredTasks {
	static Logger logger = Logger.getLogger(TaskAPI_UnansweredTasks.class);
	// Get a task given external_task_id and external_course_id. 
	// A task is unique within only a course. Hence, you've to give both
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TAReader_UnansweredTasks getTask(
			@QueryParam("external_task_id") String external_task_id,
			@QueryParam("external_course_id") String external_course_id
			)
	{
		Long requestTimestamp = System.currentTimeMillis();
		try{
			if(!Utilities.checkExists(external_course_id)) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}			
			if(!Utilities.checkExists(external_task_id)) {
				throw new TaskException(MyStatus.ERROR, MyMessage.TASK_ID_MISSING);
			}
			Task_UnansweredTasks tal = (Task_UnansweredTasks)TaskAnalyzerHandler.readByExtId(Task_UnansweredTasks.class, external_task_id, external_course_id);
			TAReader_UnansweredTasks result = new TAReader_UnansweredTasks();
			result.setExternal_task_id(external_task_id);
			result.setExternal_course_id(external_course_id);
			result.setS_difficulty_level(tal.getS_difficulty_level());
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
			logger.error("Exception while getting task - analyzer 1", e);
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);

		}
		finally
		{
			Long responseTimestamp = System.currentTimeMillis();
			Long response = (responseTimestamp -  requestTimestamp)/1000;
			Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp/1000);		
		}
	}
	//create a task
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createTask(TAReader_UnansweredTasks taReader1)
	{
		Long requestTimestamp = System.currentTimeMillis();
		try
		{
			if(!Utilities.checkExists(taReader1.getExternal_course_id())) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}			
			if(!Utilities.checkExists(taReader1.getExternal_task_id())) {
				throw new TaskException(MyStatus.ERROR, MyMessage.TASK_ID_MISSING);
			}
			Task_UnansweredTasks t_a1 = new Task_UnansweredTasks();
			// Handle this better..
			t_a1.createTask(taReader1.getExternal_task_id(), taReader1.getExternal_course_id(), 1);
			t_a1.setS_difficulty_level(taReader1.getS_difficulty_level());
			TaskAnalyzerHandler.save(t_a1);
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
			logger.error("Exception while creating task - analyzer 1", e);
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		finally
		{
			Long responseTimestamp = System.currentTimeMillis();
			Long response = (responseTimestamp -  requestTimestamp)/1000;
			Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp/1000);		
		}
	}
	//update a task attribute for analyzer 1
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateTask(TAReader_UnansweredTasks taReader1)
	{
		Long requestTimestamp = System.currentTimeMillis();
		try
		{
			if(!Utilities.checkExists(taReader1.getExternal_course_id())) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}			
			if(!Utilities.checkExists(taReader1.getExternal_task_id())) {
				throw new TaskException(MyStatus.ERROR, MyMessage.TASK_ID_MISSING);
			}
			Task_UnansweredTasks t_a1 = (Task_UnansweredTasks)TaskAnalyzerHandler.readByExtId(Task_UnansweredTasks.class, taReader1.getExternal_task_id(), taReader1.getExternal_course_id());
			if(Utilities.checkExists(taReader1.getS_difficulty_level())) {
				t_a1.setS_difficulty_level(taReader1.getS_difficulty_level());
			}

			TaskAnalyzerHandler.update(t_a1);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_UPDATED))
					.build();
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

		catch(Exception e){		
			logger.error("Exception while updating task - analyzer 1", e);
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST))
					.build();
			throw new WebApplicationException(rb);
		}
		finally
		{
			Long responseTimestamp = System.currentTimeMillis();
			Long response = (responseTimestamp -  requestTimestamp)/1000;
			Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp/1000);		
		}
	}

	// Delete record from task analyzer 1
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteTask1Analyzer(
			@QueryParam("external_course_id") String external_course_id,
			@QueryParam("external_task_id") String external_task_id
			)
	{
		Long requestTimestamp = System.currentTimeMillis();
		try {
			if(!Utilities.checkExists(external_course_id)) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}			
			if(!Utilities.checkExists(external_task_id)) {
				throw new TaskException(MyStatus.ERROR, MyMessage.TASK_ID_MISSING);
			}

			Task_UnansweredTasks t_a1 = (Task_UnansweredTasks)TaskAnalyzerHandler.readByExtId(Task_UnansweredTasks.class, external_task_id, external_course_id);
			TaskAnalyzerHandler.delete(t_a1);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_ANALYZER_DELETED)).build();
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
		catch(Exception e){
			logger.error("Exception while deleting task - analyzer 1", e);
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		finally
		{
			Long responseTimestamp = System.currentTimeMillis();
			Long response = (responseTimestamp -  requestTimestamp)/1000;
			Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp/1000);		
		}
	}

}
