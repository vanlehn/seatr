package com.asu.seatr.api.analyzer.unansweredtasks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
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
import com.asu.seatr.handlers.CourseAnalyzerMapHandler;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.handlers.analyzer.required_optional.RecommTaskHandler_Required_Optional;
import com.asu.seatr.handlers.analyzer.unansweredtasks.RecommTaskHandler_UnansweredTasks;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.CourseAnalyzerMap;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.studenttask.RecommTask_UnansweredTasks;
import com.asu.seatr.models.interfaces.RecommTaskI;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

@Path("/")
public class RecommenderAPI_UnansweredTasks {

	static Logger logger = Logger.getLogger(RecommenderAPI_UnansweredTasks.class);
	
	@Path("analyzer/unansweredtasks/inittasks")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response initRecommendedTasks(@QueryParam("number_of_tasks") Integer number_of_tasks){
		RecommTaskHandler_UnansweredTasks.initRecommTasks(number_of_tasks);
		return Response.status(Status.OK)
				.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.RECOMM_TASK_INIT))
				.build();
	}
	
	@Path("analyzer/unansweredtasks/gettasks")
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

			List<String> resultTaskSet = new ArrayList<String>();

			CourseAnalyzerMap  courseAnalyzerMap = CourseAnalyzerMapHandler.getPrimaryAnalyzerIdFromExtCourseId(external_course_id);
			if(courseAnalyzerMap == null)
			{
				//no primary analyzer exists for course
				//select any 10 questions from tasks belonging to that course
				List<Task> taskList = TaskHandler.readByExtCourseId(external_course_id);
				if(taskList.isEmpty())
				{
					Response rb = Response.status(Status.OK)
							.entity(MyResponse.build(MyStatus.ERROR, MyMessage.NO_TASK_PRESENT_FOR_COURSE)).build();
					throw new WebApplicationException(rb);
				}
				ListIterator<Task> taskListIterator = taskList.listIterator();
				while(taskListIterator.hasNext())
				{
					resultTaskSet.add(taskListIterator.next().getExternal_id());
				}
				Collections.shuffle(resultTaskSet);
			}
			else
			{
				//select tasks based on analyzer
				/*Course course = courseAnalyzerMap.getCourse();
				List<TaskAnalyzerI> taskAnalyzerList = TaskAnalyzerHandler.readByCourse(Class.forName("com.asu.seatr.models.analyzers.task.T_" + courseAnalyzerMap.getAnalyzer().toString()), course);

				if(taskAnalyzerList.isEmpty())
				{
					Response rb = Response.status(Status.NOT_FOUND)
							.entity(MyResponse.build(MyStatus.ERROR, MyMessage.NO_TASK_PRESENT_FOR_COURSE)).build();
					throw new WebApplicationException(rb);
				}
				ListIterator<TaskAnalyzerI> taskAnalyzerListIterator = taskAnalyzerList.listIterator();
				while(taskAnalyzerListIterator.hasNext())
				{
					resultTaskSet.add(taskAnalyzerListIterator.next().getTask().getExternal_id());
				}*/

				Student student = StudentHandler.getByExternalId(external_student_id, external_course_id);
				Course course = courseAnalyzerMap.getCourse();
				List<RecommTaskI> taskList=TaskHandler.getRecommTasks(RecommTask_UnansweredTasks.class, student, course);

				if(taskList.isEmpty())
				{
					Response rb = Response.status(Status.OK)
							.entity(MyResponse.build(MyStatus.ERROR, MyMessage.NO_TASK_PRESENT_FOR_COURSE)).build();
					throw new WebApplicationException(rb);
				}
				ListIterator<RecommTaskI> taskListIterator = taskList.listIterator();
				while(taskListIterator.hasNext())
				{
					resultTaskSet.add(taskListIterator.next().getTask().getExternal_id());
				}
			}

			return resultTaskSet.subList(0, (resultTaskSet.size()>number_of_tasks)?number_of_tasks:resultTaskSet.size());

		}

		catch(StudentException e)
		{
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);	
		}
		catch(CourseException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);			
		}
		catch(WebApplicationException e)
		{
			throw new WebApplicationException(e.getResponse());
		}

		catch(Exception e){
			logger.error("Exception while getting tasks", e);
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
	
	@Path("analyzer/unansweredtasks/scaletasks")
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
		Map<String,Integer> TaskMap = new HashMap<String,Integer>();
		List<String> resultTaskList=TaskHandler.getRecommTasksExternalIdInTaskList(RecommTask_UnansweredTasks.class, student, course, tasks);
		for(String t : tasks_list)
		{
			if(resultTaskList.contains(t))
			{
				TaskMap.put(t, 1);
			}
			else
			{
				TaskMap.put(t, 0);
			}
		}
		return TaskMap;
		
		}
		catch(StudentException e)
		{	
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);	
		}
		catch(TaskException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);			
		}
		catch(CourseException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);			
		}
		catch(RecommException e) {
			Response rb = Response.status(Status.OK)
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
