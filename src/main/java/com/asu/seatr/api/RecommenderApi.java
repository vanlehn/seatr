package com.asu.seatr.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.StudentTaskAnalyzerHandler;
import com.asu.seatr.handlers.StudentTaskHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.StudentTask;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.student.S_A1;

@Path("/getrecommended")
public class RecommenderApi {

	@Path("/tasks/1")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getRecommendedTasks(
			@QueryParam("external_student_id") String external_student_id,
			@QueryParam("external_course_id") Integer external_course_id,
			@QueryParam("number_of_tasks") Integer number_of_tasks
			)
	{
		Student student = StudentHandler.readByExtStudentId_and_ExtCourseId(external_student_id, external_course_id);
		List<Task> taskList = TaskHandler.readByExtCourseId(external_course_id);
		//List<StudentTask> studentTaskList = StudentTaskHandler.readByStudent(student);
		
		
		return null;
	}
}
