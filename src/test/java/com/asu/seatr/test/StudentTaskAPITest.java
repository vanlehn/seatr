package com.asu.seatr.test;
/*
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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.asu.seatr.api.StudentTaskAPI;
import com.asu.seatr.api.TaskApi;
import com.asu.seatr.rest.models.STAReader1;
import com.asu.seatr.rest.models.TAReader1;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StudentTaskAPITest extends JerseyTest {

	@Override
    protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(StudentTaskAPI.class);
    }
	
	@Test
	public void test1StudentCreate() {
		
		target("students")
					.queryParam("external_student_id", "24")
					.queryParam("external_course_id", "35")
					.request().delete(Response.class);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id","24");
		data.put("external_course_id", "35");
		data.put("s_placement_score", "34.45");
		data.put("s_year", "freshman");
		
		final Response resp  = target("students/1")
								.request().post(Entity.json(data), Response.class);
		assertEquals(Status.CREATED.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_CREATED), 
				resp.readEntity(String.class));
		
	}

	@Test
	public void test2StudentTaskCreate() {

		
		target("studenttasks")
					.queryParam("external_student_id", "24")
					.queryParam("external_course_id", "35")
					.queryParam("external_task_id", "44")
					.request().delete(Response.class);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id", "24");
		data.put("external_task_id","44");
		data.put("external_course_id", "35");
		data.put("d_status", "correct");
		data.put("d_time_lastattempt", "120");
		
		final Response resp  = target("studenttasks/1")
								.request().post(Entity.json(data), Response.class);
		assertEquals(Status.CREATED.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_TASK_CREATED), 
				resp.readEntity(String.class));
		
	}
	
	@Test
    public void test3StudentTaskGet() {
        final STAReader1 resp = target("studenttasks/1").queryParam("external_student_id", "24")
        		.queryParam("external_course_id", "35").queryParam("external_task_id", "44").request().get(STAReader1.class);
        assertEquals(new String("24"), resp.getExternal_student_id());
        assertEquals(new Integer(35), resp.getExternal_course_id()); 
        assertEquals(new String("44"), resp.getExternal_task_id());
        assertEquals(new String("correct"), resp.getD_status());
        assertEquals(new Integer(120), resp.getD_time_lastattempt());
    }
    
	@Test
    public void test4StudentTaskUpdate() {
    	Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id","24");
		data.put("external_course_id", "35");
		data.put("external_task_id","44");
		data.put("d_status", "correct");
		data.put("d_time_lastattempt","120");
		
		final Response resp  = target("studenttasks/1")
								.request().put(Entity.json(data), Response.class);
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_TASK_UPDATED), 
				resp.readEntity(String.class));
		
		final STAReader1 respGet = target("studenttasks/1").queryParam("external_student_id", "24")
        		.queryParam("external_course_id", "35").queryParam("external_task_id", "44").request().get(STAReader1.class);
        assertEquals(new String("24"), respGet.getExternal_student_id());
        assertEquals(new Integer(35), respGet.getExternal_course_id());
        assertEquals(new String("44"), respGet.getExternal_task_id());
        assertEquals(new String("correct"), respGet.getD_status());
        assertEquals(new Integer(120), respGet.getD_time_lastattempt());
    }
	
	@Test
	public void test5StudentTaskDelete() {
		Response resp = target("studenttasks")
				.queryParam("external_student_id", "24")
				.queryParam("external_course_id", "35")
				.queryParam("external_task_id", "44")
				.request().delete(Response.class);
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_TASK_DELETED), 
				resp.readEntity(String.class));
		
		final Response respGet = target("studenttasks/1").queryParam("external_student_id", "24")
        		.queryParam("external_course_id", "35").queryParam("external_task_id", "44").request().get(Response.class);
		assertEquals(Status.NOT_FOUND.getStatusCode(), respGet.getStatus());
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_TASK_NOT_FOUND), 
				respGet.readEntity(String.class));
		

	}


}
*/

