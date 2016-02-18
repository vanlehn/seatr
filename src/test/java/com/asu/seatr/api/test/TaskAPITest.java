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
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.asu.seatr.api.StudentAPI;
import com.asu.seatr.api.TaskApi;
import com.asu.seatr.rest.models.SAReader1;
import com.asu.seatr.rest.models.TAReader1;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaskAPITest extends JerseyTest {
	@Override
    protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(TaskApi.class);
    }
	
	@Test
	public void test1TaskCreate() {
		
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
				resp.readEntity(String.class));
		
	}

	
	@Test
    public void test2TaskGet() {
        final TAReader1 resp = target("tasks/1").queryParam("external_task_id", "43")
        		.queryParam("external_course_id", "35").request().get(TAReader1.class);
        assertEquals(new String("43"), resp.getExternal_task_id());
        assertEquals(new Integer(35), resp.getExternal_course_id());        
        assertEquals(new Integer(10), resp.getS_difficulty_level());           
    }
    
	@Test
    public void test3TaskUpdate() {
    	Map<String, String> data = new HashMap<String, String>();
		data.put("external_task_id","43");
		data.put("external_course_id", "35");
		data.put("s_difficulty_level", "10");
		
		final Response resp  = target("tasks/1")
								.request().put(Entity.json(data), Response.class);
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_UPDATED), 
				resp.readEntity(String.class));
		
		final TAReader1 respGet = target("tasks/1").queryParam("external_task_id", "43")
        		.queryParam("external_course_id", "35").request().get(TAReader1.class);
        assertEquals(new String("43"), respGet.getExternal_task_id());
        assertEquals(new Integer(35), respGet.getExternal_course_id());        
        assertEquals(new Integer(10), respGet.getS_difficulty_level());    	
    }
	
	@Test
	public void test4TaskDelete() {
		Response resp = target("tasks")
				.queryParam("external_task_id", "43")
				.queryParam("external_course_id", "35")
				.request().delete(Response.class);
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_DELETED), 
				resp.readEntity(String.class));
		
		final Response respGet = target("tasks/1").queryParam("external_task_id", "43")
        		.queryParam("external_course_id", "35").request().get(Response.class);
		assertEquals(Status.NOT_FOUND.getStatusCode(), respGet.getStatus());
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND), 
				respGet.readEntity(String.class));
	}
}

