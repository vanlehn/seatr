package com.asu.seatr.api.analyzer.required_optional;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.RecommException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.handlers.analyzer.required_optional.RecommTaskHandler_Required_Optional;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.task.Task_Required_Optional;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

@Path("/")
public class RecommenderAPI_Required_Optional {

	static Logger logger = Logger.getLogger(RecommenderAPI_Required_Optional.class);

	@Path("analyzer/required_optional/gettasks")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getRecommendedTasks_3(
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
			return RecommTaskHandler_Required_Optional.getTasks(course, student, number_of_tasks);
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
		catch(RecommException e) {
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
	
	@Path("analyzer/required_optional/scaletasks")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Map<String,Integer> scaleTasks_3(
			@QueryParam("external_student_id") String external_student_id,
			@QueryParam("external_course_id") String external_course_id,
			@QueryParam("tasks_list") List<String> tasks_list
			)
	{
		try
		{
		if(!Utilities.checkExists(external_course_id)) {
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
		}
		if(!Utilities.checkExists(external_student_id)) {
			throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_ID_MISSING);
		}
		if(!Utilities.checkExists(tasks_list)) {
			throw new RecommException(MyStatus.ERROR, MyMessage.TASK_ID_MISSING);
		}
		Course course = CourseHandler.getByExternalId(external_course_id);
		Student student = StudentHandler.getByExternalId(external_student_id, external_course_id);
		List<Task> tasks = TaskHandler.readByExtTaskList_Course(tasks_list, course);
		Map<String,Integer> TaskMap = RecommTaskHandler_Required_Optional.scaleTasks(course,student,tasks);
		for(String t : tasks_list)
		{
			if(!TaskMap.containsKey(t))
			{
				TaskMap.put(t, 0);
			}
		}
		return TaskMap;
		
		}
		catch(StudentException e)
		{	
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);	
		}
		catch(TaskException e) {
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);			
		}
		catch(CourseException e) {
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);			
		}
		catch(RecommException e) {
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
		
	}
}
