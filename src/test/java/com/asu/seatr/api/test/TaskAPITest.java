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


import com.asu.seatr.api.TaskApi;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.TaskAnalyzerHandler;
import com.asu.seatr.models.analyzers.task.T_A1;
import com.asu.seatr.rest.models.TAReader1;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;


@PrepareForTest({TaskAnalyzerHandler.class,T_A1.class})
@RunWith(PowerMockRunner.class)
public class TaskAPITest extends JerseyTest
{
	
	@Override
    protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(TaskApi.class);
    }
/*	
	@Test
	public void getTaskTest() throws CourseException, TaskException
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
	}*/
	
	@Test
	public void createTaskTest() throws CourseException, TaskException
	{
		T_A1 ta1 = Mockito.mock(T_A1.class);
		ta1.setId(1);
		ta1.setS_difficulty_level(10);
		//PowerMockito.mockStatic(T_A1.class);
		Mockito.doNothing().doThrow(new TaskException(MyStatus.ERROR,MyMessage.TASK_NOT_FOUND)).doThrow(new CourseException(MyStatus.ERROR,MyMessage.COURSE_NOT_FOUND)).when(ta1).createTask(new String("10"), new String("20"), new Integer(1));
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		//PowerMockito.doNothing().when(TaskAnalyzerHandler.class);
		//TaskAnalyzerHandler.save(ta1);
		PowerMockito.when(TaskAnalyzerHandler.save(ta1)).thenReturn(ta1);
		
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_course_id","20");
		data.put("external_task_id", "10");
		data.put("s_difficulty_level", "10");
		final Response resp  = target("tasks/1")
				.request().post(Entity.json(data), Response.class);
		assertEquals(Status.CREATED.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_CREATED), 
				resp.readEntity(String.class));


		/*
		target("tasks")
					.queryParam("external_task_id", "43")
					.queryParam("external_course_id", "35")
					.request().delete(Response.class);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_task_id","43");
		data.put("external_course_id", "35");
		data.put("s_difficulty_level", "10");
		
		final Response resp  = target("tasks/1")
								.request().post(Entity.json(data), Response.class);
		assertEquals(Status.CREATED.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_CREATED), 
				resp.readEntity(String.class));*/
		
	
		
	}
}