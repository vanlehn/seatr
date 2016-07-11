package com.asu.seatr.api.analyzer.n_in_a_row;

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
import com.asu.seatr.models.analyzers.task.Task_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.TAReader_N_In_A_Row;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

//Routes for Tasks for analyzer 2
@Path("analyzer/n_in_a_row/tasks")
public class TaskAPI_N_In_A_Row {
	static Logger logger = Logger.getLogger(TaskAPI_N_In_A_Row.class);
	
	// Get a task given external_task_id and external_course_id. A task is unique within only a course. 
	// Hence, you've to give both
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public TAReader_N_In_A_Row getTask(
	@QueryParam("external_task_id") String external_task_id,
	@QueryParam("external_course_id") String external_course_id
			)
	{
		Long requestTimestamp = System.currentTimeMillis();
		try{
			TaskAnalyzerHandler.readByExtId(Task_N_In_A_Row.class, external_task_id, external_course_id);
			TAReader_N_In_A_Row result = new TAReader_N_In_A_Row();
			result.setExternal_task_id(external_task_id);
			result.setExternal_course_id(external_course_id);
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
			logger.error(e.getStackTrace());
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
	//create a  task
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createTask(TAReader_N_In_A_Row taReader2)
	{
		
			Task_N_In_A_Row t_a2 = new Task_N_In_A_Row(); 
		Long requestTimestamp = System.currentTimeMillis();
		try
			{
			// Handle this better..
			t_a2.createTask(taReader2.getExternal_task_id(), taReader2.getExternal_course_id(), 2);
			TaskAnalyzerHandler.save(t_a2);
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
	    	logger.error(e.getStackTrace());
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
	//update a task attribute for analyzer 2
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateTask(TAReader_N_In_A_Row taReader2)
	{
		Long requestTimestamp = System.currentTimeMillis();
		try
		{
			Task_N_In_A_Row t_a2 = (Task_N_In_A_Row)TaskAnalyzerHandler.readByExtId(Task_N_In_A_Row.class, taReader2.getExternal_task_id(), taReader2.getExternal_course_id());
			TaskAnalyzerHandler.update(t_a2);
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
			logger.error(e.getStackTrace());
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
	
	// Delete record from task analyzer 2
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteTask2Analyzer(
			@QueryParam("external_course_id") String external_course_id,
			@QueryParam("external_task_id") String external_task_id
			)
	{

		Long requestTimestamp = System.currentTimeMillis();
		try {
			Task_N_In_A_Row t_a2 = (Task_N_In_A_Row)TaskAnalyzerHandler.readByExtId(Task_N_In_A_Row.class, external_task_id, external_course_id);
			TaskAnalyzerHandler.delete(t_a2);
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
			logger.error(e.getStackTrace());
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
