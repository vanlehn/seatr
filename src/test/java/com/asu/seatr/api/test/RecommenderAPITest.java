package com.asu.seatr.api.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.asu.seatr.api.RecommenderAPI;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.RecommException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.handlers.CourseAnalyzerMapHandler;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.TaskAnalyzerHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.handlers.analyzer.required_optional.RecommTaskHandler_Required_Optional;
import com.asu.seatr.models.Analyzer;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.CourseAnalyzerMap;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.studenttask.RecommTask_UnansweredTasks;
import com.asu.seatr.models.interfaces.RecommTaskI;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@PrepareForTest({StudentHandler.class,CourseAnalyzerMapHandler.class,TaskHandler.class,TaskAnalyzerHandler.class, CourseHandler.class,
	RecommTaskHandler_Required_Optional.class})
@RunWith(PowerMockRunner.class)
public class RecommenderAPITest extends JerseyTest{

	private static String GETTASKS_1_URL="analyzer/1/gettasks/";
	private static String GETTASKS_3_URL="analyzer/3/gettasks/";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(RecommenderAPI.class);
	}
	@Test
	public void getRecommendedTasks1Test() throws StudentException, CourseException
	{
		Student student = new Student();
		Task task = new Task();
		task.setId(1);
		task.setExternal_id("10");
		List<Task> taskList = new ArrayList<Task>();
		taskList.add(task);
		PowerMockito.mockStatic(StudentHandler.class);
		PowerMockito.when(StudentHandler.getByExternalId(Mockito.anyString(), Mockito.anyString())).thenReturn(student);
		PowerMockito.mockStatic(CourseAnalyzerMapHandler.class);
		PowerMockito.when(CourseAnalyzerMapHandler.getPrimaryAnalyzerIdFromExtCourseId(Mockito.anyString())).thenReturn(null);
		PowerMockito.mockStatic(TaskHandler.class);	
		PowerMockito.when(TaskHandler.readByExtCourseId(Mockito.anyString())).thenReturn(taskList);
		final List<String> resList = target(GETTASKS_1_URL).queryParam("external_student_id",1).queryParam("external_course_id", 35).queryParam("number_of_tasks", 10).request().get(List.class);
		assertEquals("10",resList.get(0));
	}
	@Test
	public void getRecommendedTasks2Test() throws StudentException, CourseException
	{
		Student student = new Student();
		List<Task> taskList = new ArrayList<Task>();
		PowerMockito.mockStatic(StudentHandler.class);
		PowerMockito.when(StudentHandler.getByExternalId(Mockito.anyString(), Mockito.anyString())).thenReturn(student);
		PowerMockito.mockStatic(CourseAnalyzerMapHandler.class);
		PowerMockito.when(CourseAnalyzerMapHandler.getPrimaryAnalyzerIdFromExtCourseId(Mockito.anyString())).thenReturn(null);
		PowerMockito.mockStatic(TaskHandler.class);	
		PowerMockito.when(TaskHandler.readByExtCourseId(Mockito.anyString())).thenReturn(taskList);
		PowerMockito.when(TaskHandler.readByExtCourseId(Mockito.anyString())).thenReturn(taskList);
		try
		{
			final List<String> resp = target(GETTASKS_1_URL).queryParam("external_student_id",1).queryParam("external_course_id", 35).queryParam("number_of_tasks", 10).request().get(List.class);
		}
		catch(WebApplicationException rb)
		{
			assertEquals(Status.NOT_FOUND.getStatusCode(),rb.getResponse().getStatus());
			assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.NO_TASK_PRESENT_FOR_COURSE),rb.getResponse().readEntity(String.class));
		}

	}
	@Test
	public void getRecommendedTasks3Test() throws StudentException, CourseException
	{

		Student student = new Student();
		CourseAnalyzerMap cam = new CourseAnalyzerMap();
		Analyzer a = new Analyzer();
		a.setName("A1");
		a.setId(1);
		cam.setAnalyzer(a);
		Course c = new Course();
		c.setExternal_id("35");
		c.setId(1);
		cam.setCourse(c);
		PowerMockito.mockStatic(StudentHandler.class);
		PowerMockito.when(StudentHandler.getByExternalId(Mockito.anyString(), Mockito.anyString())).thenReturn(student);
		PowerMockito.mockStatic(CourseAnalyzerMapHandler.class);
		PowerMockito.when(CourseAnalyzerMapHandler.getPrimaryAnalyzerIdFromExtCourseId(Mockito.anyString())).thenReturn(cam);
		PowerMockito.mockStatic(TaskHandler.class);
		List<RecommTaskI> recommTaskList = new ArrayList<RecommTaskI>();
		RecommTask_UnansweredTasks ta1 = new RecommTask_UnansweredTasks();
		ta1.setId(1);
		Task task = new Task();
		task.setExternal_id("10");
		ta1.setTask(task);
		recommTaskList.add(ta1);
		PowerMockito.when(TaskHandler.getRecommTasks(Mockito.any(Class.class),(Student)Mockito.anyObject(), (Course)Mockito.anyObject())).thenReturn(recommTaskList);
		final List<String> resList = target(GETTASKS_1_URL).queryParam("external_student_id",1).queryParam("external_course_id", 35).queryParam("number_of_tasks", 10).request().get(List.class);
		assertEquals("10",resList.get(0));
	}
	@Test
	public void getRecommendedTasks4Test() throws StudentException, CourseException
	{

		Student student = new Student();
		CourseAnalyzerMap cam = new CourseAnalyzerMap();
		Analyzer a = new Analyzer();
		a.setName("A1");
		a.setId(1);
		cam.setAnalyzer(a);
		Course c = new Course();
		c.setExternal_id("35");
		c.setId(1);
		cam.setCourse(c);
		PowerMockito.mockStatic(StudentHandler.class);
		PowerMockito.when(StudentHandler.getByExternalId(Mockito.anyString(), Mockito.anyString())).thenReturn(student);
		PowerMockito.mockStatic(CourseAnalyzerMapHandler.class);
		PowerMockito.when(CourseAnalyzerMapHandler.getPrimaryAnalyzerIdFromExtCourseId(Mockito.anyString())).thenReturn(cam);
		PowerMockito.mockStatic(TaskHandler.class);
		List<RecommTaskI> recommTaskList = new ArrayList<RecommTaskI>();
		PowerMockito.when(TaskHandler.getRecommTasks(Mockito.any(Class.class),(Student)Mockito.anyObject(), (Course)Mockito.anyObject())).thenReturn(recommTaskList);
		try
		{
			final List<String> resp = target(GETTASKS_1_URL).queryParam("external_student_id",1).queryParam("external_course_id", 35).queryParam("number_of_tasks", 10).request().get(List.class);
		}
		catch(WebApplicationException rb)
		{
			assertEquals(Status.NOT_FOUND.getStatusCode(),rb.getResponse().getStatus());
			assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.NO_TASK_PRESENT_FOR_COURSE),rb.getResponse().readEntity(String.class));
		}

	}

	@Test
	public void getRecommended3TasksTest_success() throws StudentException, CourseException, RecommException {

		Student student = new Student();
		student.setId(1);

		Course course = new Course();
		course.setExternal_id("35");
		course.setId(1);
		PowerMockito.mockStatic(CourseHandler.class);
		PowerMockito.mockStatic(StudentHandler.class);
		PowerMockito.mockStatic(RecommTaskHandler_Required_Optional.class);

		PowerMockito.when(StudentHandler.getByExternalId(Mockito.anyString(), Mockito.anyString())).thenReturn(student);
		PowerMockito.when(CourseHandler.getByExternalId(Mockito.anyString())).thenReturn(course);


		List<String> l = new ArrayList<String>();
		l.add("1");
		l.add("2");
		l.add("3");
		PowerMockito.when(RecommTaskHandler_Required_Optional.getTasks((Course)Mockito.anyObject(), (Student)Mockito.anyObject(), 
				Mockito.anyInt())).thenReturn(l);

		final List<String> resp = target(GETTASKS_3_URL).queryParam("external_student_id",1).queryParam("external_course_id", 35).
				queryParam("number_of_tasks", 10).request().get(List.class);
		assertEquals("1", l.get(0));
		assertEquals("2", l.get(1));
		assertEquals("3", l.get(2));

	}

	@Test
	public void getRecommended3TasksTest_CourseException() throws StudentException, CourseException, RecommException {

		Student student = new Student();
		student.setId(1);

		Course course = new Course();
		course.setExternal_id("35");
		course.setId(1);
		PowerMockito.mockStatic(CourseHandler.class);
		PowerMockito.mockStatic(StudentHandler.class);
		PowerMockito.mockStatic(RecommTaskHandler_Required_Optional.class);

		PowerMockito.when(StudentHandler.getByExternalId(Mockito.anyString(), Mockito.anyString())).thenReturn(student);
		PowerMockito.when(CourseHandler.getByExternalId(Mockito.anyString())).thenThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND));


		List<String> l = new ArrayList<String>();
		l.add("1");
		l.add("2");
		l.add("3");
		PowerMockito.when(RecommTaskHandler_Required_Optional.getTasks((Course)Mockito.anyObject(), (Student)Mockito.anyObject(), 
				Mockito.anyInt())).thenReturn(l);

		try {
			final List<String> resp = target(GETTASKS_3_URL).queryParam("external_student_id",1).queryParam("external_course_id", 35).
					queryParam("number_of_tasks", 10).request().get(List.class);
		} catch (WebApplicationException rb) {
			assertEquals(Status.BAD_REQUEST.getStatusCode(), rb.getResponse().getStatus());
			assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND),rb.getResponse().readEntity(String.class));
		}

	}

	public void getRecommended3TasksTest_StudentException() throws StudentException, CourseException, RecommException {

		Student student = new Student();
		student.setId(1);

		Course course = new Course();
		course.setExternal_id("35");
		course.setId(1);
		PowerMockito.mockStatic(CourseHandler.class);
		PowerMockito.mockStatic(StudentHandler.class);
		PowerMockito.mockStatic(RecommTaskHandler_Required_Optional.class);

		PowerMockito.when(StudentHandler.getByExternalId(Mockito.anyString(), Mockito.anyString())).thenThrow(new StudentException(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND));
		PowerMockito.when(CourseHandler.getByExternalId(Mockito.anyString())).thenReturn(course);


		List<String> l = new ArrayList<String>();
		l.add("1");
		l.add("2");
		l.add("3");
		PowerMockito.when(RecommTaskHandler_Required_Optional.getTasks((Course)Mockito.anyObject(), (Student)Mockito.anyObject(), 
				Mockito.anyInt())).thenReturn(l);

		try {
			final List<String> resp = target(GETTASKS_3_URL).queryParam("external_student_id",1).queryParam("external_course_id", 35).
					queryParam("number_of_tasks", 10).request().get(List.class);
		} catch (WebApplicationException rb) {
			assertEquals(Status.BAD_REQUEST.getStatusCode(), rb.getResponse().getStatus());
			assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND),rb.getResponse().readEntity(String.class));
		}

	}

	public void getRecommended3TasksTest_RecommException() throws StudentException, CourseException, RecommException {

		Student student = new Student();
		student.setId(1);

		Course course = new Course();
		course.setExternal_id("35");
		course.setId(1);
		PowerMockito.mockStatic(CourseHandler.class);
		PowerMockito.mockStatic(StudentHandler.class);
		PowerMockito.mockStatic(RecommTaskHandler_Required_Optional.class);

		PowerMockito.when(StudentHandler.getByExternalId(Mockito.anyString(), Mockito.anyString())).thenReturn(student);
		PowerMockito.when(CourseHandler.getByExternalId(Mockito.anyString())).thenReturn(course);


		List<String> l = new ArrayList<String>();
		l.add("1");
		l.add("2");
		l.add("3");
		PowerMockito.when(RecommTaskHandler_Required_Optional.getTasks((Course)Mockito.anyObject(), (Student)Mockito.anyObject(), 
				Mockito.anyInt())).thenThrow(new RecommException(MyStatus.ERROR, MyMessage.RECOMMENDATION_ERROR));

		try {
			final List<String> resp = target(GETTASKS_3_URL).queryParam("external_student_id",1).queryParam("external_course_id", 35).
					queryParam("number_of_tasks", 10).request().get(List.class);
		} catch (WebApplicationException rb) {
			assertEquals(Status.BAD_REQUEST.getStatusCode(), rb.getResponse().getStatus());
			assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.RECOMMENDATION_ERROR),rb.getResponse().readEntity(String.class));
		}

	}
}