package com.asu.seatr.api.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
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

import com.asu.seatr.api.TaskAPI;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.TaskAnalyzerHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.task.T_A1;
import com.asu.seatr.models.interfaces.TaskAnalyzerI;
import com.asu.seatr.rest.models.TAReader1;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;


@PrepareForTest({TaskAnalyzerHandler.class,T_A1.class,TaskAPI.class,TaskHandler.class})
@RunWith(PowerMockRunner.class)
public class TaskAPITest extends JerseyTest
{
	
	@Override
    protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(TaskAPI.class);
    }

	@Test
	public void getTaskTestSuccess() throws CourseException, TaskException
	{
		T_A1 ta1 = new T_A1();
		ta1.setId(1);
		ta1.setS_difficulty_level(10);
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenReturn(ta1);
		final TAReader1 tareader = target("tasks/1").queryParam("external_task_id", 10).queryParam("external_course_id", 20).request().get(TAReader1.class);
		assertEquals(new String("20"),tareader.getExternal_course_id());
		assertEquals(new String("10"), tareader.getExternal_task_id());
		assertEquals(new Integer(10), tareader.getS_difficulty_level());
	}
	@Test
	public void getTaskTest_FailsWithCourseNotFound() throws CourseException, TaskException
	{
		T_A1 ta1 = new T_A1();
		ta1.setId(1);
		ta1.setS_difficulty_level(10);
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenThrow(new CourseException(MyStatus.ERROR,MyMessage.COURSE_NOT_FOUND));
		Response resp = target("tasks/1").queryParam("external_task_id", 10).queryParam("external_course_id", 20).request().get(Response.class);
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void getTaskTest_FailsWithTaskNotFound() throws CourseException, TaskException
	{
		T_A1 ta1 = new T_A1();
		ta1.setId(1);
		ta1.setS_difficulty_level(10);
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenThrow(new TaskException(MyStatus.ERROR,MyMessage.TASK_NOT_FOUND));
		Response resp = target("tasks/1").queryParam("external_task_id", 10).queryParam("external_course_id", 20).request().get(Response.class);
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND), 
				resp.readEntity(String.class));
	}
	@Test
	public void getTaskTest_FailsWithTaskAnalyzerNotFound() throws CourseException, TaskException
	{
		T_A1 ta1 = new T_A1();
		ta1.setId(1);
		ta1.setS_difficulty_level(10);
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenThrow(new TaskException(MyStatus.ERROR,MyMessage.TASK_ANALYZER_NOT_FOUND));
		Response resp = target("tasks/1").queryParam("external_task_id", 10).queryParam("external_course_id", 20).request().get(Response.class);
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_ANALYZER_NOT_FOUND), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void createTaskTestSuccess() throws Exception
	{
		T_A1 ta1 = Mockito.mock(T_A1.class);
		PowerMockito.whenNew(T_A1.class).withNoArguments().thenReturn(ta1);
		//Mockito.stubVoid(ta1).toReturn().on().createTask(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		Mockito.doNothing().when(ta1).createTask(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.save((TaskAnalyzerI)Mockito.anyObject())).thenReturn(ta1);
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_task_id","10");
		data.put("external_course_id", "35");
		data.put("s_difficulty_level", "10");
		final Response resp  = target("tasks/1")
				.request().post(Entity.json(data), Response.class);
		PowerMockito.verifyNew(T_A1.class).withNoArguments();
		assertEquals(Status.CREATED.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_CREATED), 
				resp.readEntity(String.class));
	}
	@Test
	public void createTaskTest_FailsWithPropertyNull() throws Exception
	{
		T_A1 ta1 = Mockito.mock(T_A1.class);
		PowerMockito.whenNew(T_A1.class).withNoArguments().thenReturn(ta1);
		//Mockito.stubVoid(ta1).toReturn().on().createTask(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		Mockito.doThrow(new TaskException(MyStatus.ERROR,MyMessage.TASK_PROPERTY_NULL)).when(ta1).createTask(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.save((TaskAnalyzerI)Mockito.anyObject())).thenReturn(ta1);
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_task_id","10");
		data.put("external_course_id", "35");
		data.put("s_difficulty_level", "10");
		final Response resp  = target("tasks/1")
				.request().post(Entity.json(data), Response.class);
		PowerMockito.verifyNew(T_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_PROPERTY_NULL), 
				resp.readEntity(String.class));
	}
	@Test
	public void createTaskTest_FailsWithCourseNotFound() throws Exception
	{
		T_A1 ta1 = Mockito.mock(T_A1.class);
		PowerMockito.whenNew(T_A1.class).withNoArguments().thenReturn(ta1);
		//Mockito.stubVoid(ta1).toReturn().on().createTask(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		Mockito.doThrow(new CourseException(MyStatus.ERROR,MyMessage.COURSE_NOT_FOUND)).when(ta1).createTask(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.save((TaskAnalyzerI)Mockito.anyObject())).thenReturn(ta1);
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_task_id","10");
		data.put("external_course_id", "35");
		data.put("s_difficulty_level", "10");
		final Response resp  = target("tasks/1")
				.request().post(Entity.json(data), Response.class);
		PowerMockito.verifyNew(T_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND), 
				resp.readEntity(String.class));
	}
	@Test
	public void createTaskTest_FailsWithTaskAnalyzerAlreadyPresent() throws Exception
	{
		T_A1 ta1 = Mockito.mock(T_A1.class);
		PowerMockito.whenNew(T_A1.class).withNoArguments().thenReturn(ta1);
		//Mockito.stubVoid(ta1).toReturn().on().createTask(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		Mockito.doNothing().when(ta1).createTask(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.save((TaskAnalyzerI)Mockito.anyObject())).thenThrow(new TaskException(MyStatus.ERROR, MyMessage.TASK_ANALYZER_ALREADY_PRESENT));
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_task_id","10");
		data.put("external_course_id", "35");
		data.put("s_difficulty_level", "10");
		final Response resp  = target("tasks/1")
				.request().post(Entity.json(data), Response.class);
		PowerMockito.verifyNew(T_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_ANALYZER_ALREADY_PRESENT), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void updateTaskTestSuccess() throws CourseException, TaskException
	{
		T_A1 ta1 = new T_A1();
		ta1.setId(1);
		ta1.setS_difficulty_level(10);
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenReturn(ta1);
		PowerMockito.when(TaskAnalyzerHandler.update((TaskAnalyzerI)Mockito.anyObject())).thenReturn(ta1);
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_task_id","10");
		data.put("external_course_id", "35");
		data.put("s_difficulty_level", "10");
		final Response resp = target("tasks/1").request().put(Entity.json(data),Response.class);
		assertEquals(Status.OK.getStatusCode(),resp.getStatus());
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_UPDATED),resp.readEntity(String.class));	
	}
	
	@Test
	public void updateTaskTest_FailsWithCourseNotFound() throws CourseException, TaskException
	{
		T_A1 ta1 = new T_A1();
		ta1.setId(1);
		ta1.setS_difficulty_level(10);
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenThrow(new CourseException(MyStatus.ERROR,MyMessage.COURSE_NOT_FOUND));
		PowerMockito.when(TaskAnalyzerHandler.update((TaskAnalyzerI)Mockito.anyObject())).thenReturn(ta1);
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_task_id","10");
		data.put("external_course_id", "35");
		data.put("s_difficulty_level", "10");
		final Response resp = target("tasks/1").request().put(Entity.json(data),Response.class);
		assertEquals(Status.OK.getStatusCode(),resp.getStatus());
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND),resp.readEntity(String.class));	
	}
	@Test
	public void updateTaskTest_FailsWithTaskNotFound() throws CourseException, TaskException
	{
		T_A1 ta1 = new T_A1();
		ta1.setId(1);
		ta1.setS_difficulty_level(10);
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenThrow(new TaskException(MyStatus.ERROR,MyMessage.TASK_NOT_FOUND));
		PowerMockito.when(TaskAnalyzerHandler.update((TaskAnalyzerI)Mockito.anyObject())).thenReturn(ta1);
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_task_id","10");
		data.put("external_course_id", "35");
		data.put("s_difficulty_level", "10");
		final Response resp = target("tasks/1").request().put(Entity.json(data),Response.class);
		assertEquals(Status.OK.getStatusCode(),resp.getStatus());
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND),resp.readEntity(String.class));	
	}
	@Test
	public void updateTaskTest_FailsWithTaskAnalyzerNotFound() throws CourseException, TaskException
	{
		T_A1 ta1 = new T_A1();
		ta1.setId(1);
		ta1.setS_difficulty_level(10);
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenThrow(new TaskException(MyStatus.ERROR,MyMessage.TASK_ANALYZER_NOT_FOUND));
		PowerMockito.when(TaskAnalyzerHandler.update((TaskAnalyzerI)Mockito.anyObject())).thenReturn(ta1);
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_task_id","10");
		data.put("external_course_id", "35");
		data.put("s_difficulty_level", "10");
		final Response resp = target("tasks/1").request().put(Entity.json(data),Response.class);
		assertEquals(Status.OK.getStatusCode(),resp.getStatus());
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_ANALYZER_NOT_FOUND),resp.readEntity(String.class));	
	}
	
	@Test
	public void deleteTask1AnalyzerTestSuccess() throws Exception
	{
		T_A1 ta1= new T_A1();
		ta1.setId(1);
		ta1.setS_difficulty_level(10);
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenReturn(ta1);
		//PowerMockito.when(TaskAnalyzerHandler.delete((TaskAnalyzerI)Mockito.anyObject()));
		PowerMockito.doNothing().when(TaskAnalyzerHandler.class,"delete",(TaskAnalyzerI)Mockito.anyObject());
		final Response resp = target("tasks/1").queryParam("external_task_id", 10).queryParam("external_course_id", 20).request().delete(Response.class);
		assertEquals(Status.OK.getStatusCode(),resp.getStatus());
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_ANALYZER_DELETED),resp.readEntity(String.class));
	}
	@Test
	public void deleteTask1AnalyzerTest_FailWithCourseNotFound() throws Exception
	{
		T_A1 ta1= new T_A1();
		ta1.setId(1);
		ta1.setS_difficulty_level(10);
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenThrow(new CourseException(MyStatus.ERROR,MyMessage.COURSE_NOT_FOUND));
		//PowerMockito.when(TaskAnalyzerHandler.delete((TaskAnalyzerI)Mockito.anyObject()));
		PowerMockito.doNothing().when(TaskAnalyzerHandler.class,"delete",(TaskAnalyzerI)Mockito.anyObject());
		final Response resp = target("tasks/1").queryParam("external_task_id", 10).queryParam("external_course_id", 20).request().delete(Response.class);
		assertEquals(Status.OK.getStatusCode(),resp.getStatus());
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND),resp.readEntity(String.class));
	}
	@Test
	public void deleteTask1AnalyzerTest_FailWithTaskNotFound() throws Exception
	{
		T_A1 ta1= new T_A1();
		ta1.setId(1);
		ta1.setS_difficulty_level(10);
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenThrow(new TaskException(MyStatus.ERROR,MyMessage.TASK_NOT_FOUND));
		//PowerMockito.when(TaskAnalyzerHandler.delete((TaskAnalyzerI)Mockito.anyObject()));
		PowerMockito.doNothing().when(TaskAnalyzerHandler.class,"delete",(TaskAnalyzerI)Mockito.anyObject());
		final Response resp = target("tasks/1").queryParam("external_task_id", 10).queryParam("external_course_id", 20).request().delete(Response.class);
		assertEquals(Status.OK.getStatusCode(),resp.getStatus());
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND),resp.readEntity(String.class));
	}
	@Test
	public void deleteTask1AnalyzerTest_FailWithTaskAnalyzerNotFound() throws Exception
	{
		T_A1 ta1= new T_A1();
		ta1.setId(1);
		ta1.setS_difficulty_level(10);
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenThrow(new TaskException(MyStatus.ERROR,MyMessage.TASK_ANALYZER_NOT_FOUND));
		//PowerMockito.when(TaskAnalyzerHandler.delete((TaskAnalyzerI)Mockito.anyObject()));
		PowerMockito.doNothing().when(TaskAnalyzerHandler.class,"delete",(TaskAnalyzerI)Mockito.anyObject());
		final Response resp = target("tasks/1").queryParam("external_task_id", 10).queryParam("external_course_id", 20).request().delete(Response.class);
		assertEquals(Status.OK.getStatusCode(),resp.getStatus());
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_ANALYZER_NOT_FOUND),resp.readEntity(String.class));
	}
	
	@Test
	public void deleteTask() throws Exception
	{
		T_A1 ta1= new T_A1();
		ta1.setId(1);
		ta1.setS_difficulty_level(10);
		Task task = new Task();
		task.setId(1);
		task.setExternal_id("10");
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenReturn(ta1);
		PowerMockito.doNothing().when(TaskAnalyzerHandler.class,"delete",(TaskAnalyzerI)Mockito.anyObject());
		PowerMockito.mockStatic(TaskHandler.class);
		PowerMockito.when(TaskHandler.readByExtId(Mockito.anyString(), Mockito.anyString())).thenReturn(task);
		PowerMockito.doNothing().when(TaskHandler.class,"delete",(Task)Mockito.anyObject());
		final Response resp = target("tasks/").queryParam("external_task_id", 10).queryParam("external_course_id", 20).request().delete(Response.class);
		assertEquals(Status.OK.getStatusCode(),resp.getStatus());
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_DELETED),resp.readEntity(String.class));
		
	}
}