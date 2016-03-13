package com.asu.seatr.api;

import java.util.ArrayList;
import java.util.Collections;
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

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.handlers.CourseAnalyzerMapHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.TaskAnalyzerHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.CourseAnalyzerMap;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.interfaces.RecommTaskI;
import com.asu.seatr.models.interfaces.TaskAnalyzerI;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@Path("/gettasks")
public class RecommenderAPI {

	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getRecommendedTasks(
			@QueryParam("external_student_id") String external_student_id,
			@QueryParam("external_course_id") String external_course_id,
			@QueryParam("number_of_tasks") Integer number_of_tasks
			)
	{
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
				List<RecommTaskI> taskList=TaskHandler.getRecommTasks(Class.forName("com.asu.seatr.models.analyzers.studenttask.RecommTask_" + courseAnalyzerMap.getAnalyzer().toString()), student, course);
				
				if(taskList.isEmpty())
				{
					Response rb = Response.status(Status.NOT_FOUND)
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
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);	
		}
		catch(CourseException e) {
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);			
		}
		catch(WebApplicationException e)
		{
			throw new WebApplicationException(e.getResponse());
		}
		
		catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
	}
}
