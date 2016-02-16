package com.asu.seatr.api;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.asu.seatr.handlers.CourseAnalyzerMapHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.StudentTaskAnalyzerHandler;
import com.asu.seatr.handlers.StudentTaskHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.models.CourseAnalyzerMap;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.StudentTask;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.student.S_A1;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@Path("/gettasks")
public class RecommenderApi {

	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getRecommendedTasks(
			@QueryParam("external_student_id") String external_student_id,
			@QueryParam("external_course_id") String external_course_id,
			@QueryParam("number_of_tasks") Integer number_of_tasks
			)
	{
		//Student student = StudentHandler.readByExtStudentId_and_ExtCourseId(external_student_id, external_course_id);
		//List<Task> taskList = TaskHandler.readByExtCourseId(external_course_id);
		//List<StudentTask> studentTaskList = StudentTaskHandler.readByStudent(student);
		List<String> resultTaskSet = new ArrayList<String>();
		try
		{
			CourseAnalyzerMap  courseAnalyzerMap = CourseAnalyzerMapHandler.getPrimaryAnalyzerIdFromExtCourseId(external_course_id);
			if(courseAnalyzerMap == null)
			{
				//no primary analyzer exists for course
				//select any 10 questions from tasks belonging to that course
				List<Task> taskList = TaskHandler.readByExtCourseId(external_course_id);
				if(taskList.isEmpty())
				{
					Response rb = Response.status(Status.NOT_FOUND)
							.entity(MyResponse.build(MyStatus.ERROR, MyMessage.NO_TASK_PRESENT)).build();
					throw new WebApplicationException(rb);
				}
				ListIterator<Task> taskListIterator = taskList.listIterator();
				int counter = number_of_tasks;
				while(taskListIterator.hasNext() && counter >0)
					{
						resultTaskSet.add(taskListIterator.next().getExternal_id());
						--counter;
					}
			}
			else
			{
				//select tasks based on analyzer
			}
		}
		catch(WebApplicationException e)
		{
			throw e;
		}
		catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		
		
		return resultTaskSet;
	}
}
