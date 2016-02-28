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

import com.asu.seatr.api.StudentTaskAPI;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.StudentTaskAnalyzerHandler;
import com.asu.seatr.models.analyzers.studenttask.ST_A1;
import com.asu.seatr.models.interfaces.StudentTaskAnalyzerI;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;


@PrepareForTest({StudentTaskAnalyzerHandler.class,ST_A1.class,StudentTaskAPI.class})
@RunWith(PowerMockRunner.class)
public class StudentTaskAPITest extends JerseyTest
{
	
	@Override
    protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(StudentTaskAPI.class);
    }
	
	@Test
	public void createStudentTaskTestSuccess() throws Exception
	{
		ST_A1 st_a1 = Mockito.mock(ST_A1.class);
		PowerMockito.whenNew(ST_A1.class).withNoArguments().thenReturn(st_a1);
		Mockito.doNothing().when(st_a1).createStudentTask(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		PowerMockito.mockStatic(StudentTaskAnalyzerHandler.class);
		PowerMockito.when(StudentTaskAnalyzerHandler.save((StudentTaskAnalyzerI)Mockito.anyObject())).thenReturn(st_a1);
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id", "43");
		data.put("external_task_id","10");
		data.put("external_course_id", "35");
		data.put("d_status", "10");
		data.put("d_time_lastattempt", "83681");
		final Response resp  = target("studenttasks/1")
				.request().post(Entity.json(data), Response.class);
		PowerMockito.verifyNew(ST_A1.class).withNoArguments();
		assertEquals(Status.CREATED.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_TASK_CREATED), 
				resp.readEntity(String.class));
	}
	@Test
	public void createStudentTaskTest_FailWithCourseNotFound() throws Exception
	{
		ST_A1 st_a1 = Mockito.mock(ST_A1.class);
		PowerMockito.whenNew(ST_A1.class).withNoArguments().thenReturn(st_a1);
		Mockito.doThrow(new CourseException(MyStatus.ERROR,MyMessage.COURSE_NOT_FOUND)).when(st_a1).createStudentTask(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		PowerMockito.mockStatic(StudentTaskAnalyzerHandler.class);
		PowerMockito.when(StudentTaskAnalyzerHandler.save((StudentTaskAnalyzerI)Mockito.anyObject())).thenReturn(st_a1);
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id", "43");
		data.put("external_task_id","10");
		data.put("external_course_id", "35");
		data.put("d_status", "10");
		data.put("d_time_lastattempt", "83681");
		final Response resp  = target("studenttasks/1")
				.request().post(Entity.json(data), Response.class);
		PowerMockito.verifyNew(ST_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND), 
				resp.readEntity(String.class));
	}
	@Test
	public void createStudentTaskTest_FailWithTaskNotFound() throws Exception
	{
		ST_A1 st_a1 = Mockito.mock(ST_A1.class);
		PowerMockito.whenNew(ST_A1.class).withNoArguments().thenReturn(st_a1);
		Mockito.doThrow(new TaskException(MyStatus.ERROR,MyMessage.TASK_NOT_FOUND)).when(st_a1).createStudentTask(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		PowerMockito.mockStatic(StudentTaskAnalyzerHandler.class);
		PowerMockito.when(StudentTaskAnalyzerHandler.save((StudentTaskAnalyzerI)Mockito.anyObject())).thenReturn(st_a1);
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id", "43");
		data.put("external_task_id","10");
		data.put("external_course_id", "35");
		data.put("d_status", "10");
		data.put("d_time_lastattempt", "83681");
		final Response resp  = target("studenttasks/1")
				.request().post(Entity.json(data), Response.class);
		PowerMockito.verifyNew(ST_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND), 
				resp.readEntity(String.class));
	}
	@Test
	public void createStudentTaskTest_FailWithStudentNotFound() throws Exception
	{
		ST_A1 st_a1 = Mockito.mock(ST_A1.class);
		PowerMockito.whenNew(ST_A1.class).withNoArguments().thenReturn(st_a1);
		Mockito.doThrow(new StudentException(MyStatus.ERROR,MyMessage.STUDENT_NOT_FOUND)).when(st_a1).createStudentTask(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		PowerMockito.mockStatic(StudentTaskAnalyzerHandler.class);
		PowerMockito.when(StudentTaskAnalyzerHandler.save((StudentTaskAnalyzerI)Mockito.anyObject())).thenReturn(st_a1);
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id", "43");
		data.put("external_task_id","10");
		data.put("external_course_id", "35");
		data.put("d_status", "10");
		data.put("d_time_lastattempt", "83681");
		final Response resp  = target("studenttasks/1")
				.request().post(Entity.json(data), Response.class);
		PowerMockito.verifyNew(ST_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND), 
				resp.readEntity(String.class));
	}
	@Test
	public void createStudentTaskTest_FailWithStudentTaskPropertyNull() throws Exception
	{
		ST_A1 st_a1 = Mockito.mock(ST_A1.class);
		PowerMockito.whenNew(ST_A1.class).withNoArguments().thenReturn(st_a1);
		Mockito.doThrow(new TaskException(MyStatus.ERROR,MyMessage.STUDENT_TASK_PROPERTY_NULL)).when(st_a1).createStudentTask(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		PowerMockito.mockStatic(StudentTaskAnalyzerHandler.class);
		PowerMockito.when(StudentTaskAnalyzerHandler.save((StudentTaskAnalyzerI)Mockito.anyObject())).thenReturn(st_a1);
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id", "43");
		data.put("external_task_id","10");
		data.put("external_course_id", "35");
		data.put("d_status", "10");
		data.put("d_time_lastattempt", "83681");
		final Response resp  = target("studenttasks/1")
				.request().post(Entity.json(data), Response.class);
		PowerMockito.verifyNew(ST_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_TASK_PROPERTY_NULL), 
				resp.readEntity(String.class));
	}
	
}