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
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.handlers.CourseAnalyzerMapHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.TaskAnalyzerHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.models.Analyzer;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.CourseAnalyzerMap;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.task.T_A1;
import com.asu.seatr.models.interfaces.TaskAnalyzerI;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@PrepareForTest({StudentHandler.class,CourseAnalyzerMapHandler.class,TaskHandler.class,TaskAnalyzerHandler.class})
@RunWith(PowerMockRunner.class)
public class RecommenderAPITest extends JerseyTest{

	private static String GETTASKS_URL="gettasks/";
	
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
		final List<String> resList = target(GETTASKS_URL).queryParam("external_student_id",1).queryParam("external_course_id", 35).queryParam("number_of_tasks", 10).request().get(List.class);
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
		final List<String> resp = target(GETTASKS_URL).queryParam("external_student_id",1).queryParam("external_course_id", 35).queryParam("number_of_tasks", 10).request().get(List.class);
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
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		List<TaskAnalyzerI> taskAnalyzerList = new ArrayList<TaskAnalyzerI>();
		T_A1 ta1 = new T_A1();
		ta1.setId(1);
		Task task = new Task();
		task.setExternal_id("10");
		ta1.setTask(task);
		taskAnalyzerList.add(ta1);
		PowerMockito.when(TaskAnalyzerHandler.readByCourse(Mockito.any(Class.class),(Course)Mockito.anyObject())).thenReturn(taskAnalyzerList);
		final List<String> resList = target(GETTASKS_URL).queryParam("external_student_id",1).queryParam("external_course_id", 35).queryParam("number_of_tasks", 10).request().get(List.class);
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
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		List<TaskAnalyzerI> taskAnalyzerList = new ArrayList<TaskAnalyzerI>();
		PowerMockito.when(TaskAnalyzerHandler.readByCourse(Mockito.any(Class.class),(Course)Mockito.anyObject())).thenReturn(taskAnalyzerList);
		try
		{
		final List<String> resp = target(GETTASKS_URL).queryParam("external_student_id",1).queryParam("external_course_id", 35).queryParam("number_of_tasks", 10).request().get(List.class);
		}
		catch(WebApplicationException rb)
		{
			assertEquals(Status.NOT_FOUND.getStatusCode(),rb.getResponse().getStatus());
			assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.NO_TASK_PRESENT_FOR_COURSE),rb.getResponse().readEntity(String.class));
		}
		
	}
}