package com.asu.seatr.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.json.JSONObject;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

import com.asu.seatr.api.StudentAPI;
import com.asu.seatr.rest.models.SAReader1;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StudentAPITest extends JerseyTest {
	@Override
    protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(StudentAPI.class);
    }
	
	@Test
	public void test1StudentCreate() {
		
		target("students")
					.queryParam("external_student_id", "23")
					.queryParam("external_course_id", "35")
					.request().delete(Response.class);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id","23");
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
    public void test2StudentGet() {
        final SAReader1 resp = target("students/1").queryParam("external_student_id", "23")
        		.queryParam("external_course_id", "35").request().get(SAReader1.class);
        assertEquals(new String("23"), resp.getExternal_student_id());
        assertEquals(new Integer(35), resp.getExternal_course_id());        
        assertEquals(new Double(34.45), resp.getS_placement_score());
        assertEquals(new String("freshman"), resp.getS_year());                
    }
    
	@Test
    public void test3StudentUpdate() {
    	Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id","23");
		data.put("external_course_id", "35");
		data.put("s_placement_score", "31.55");
		data.put("s_year", "sophomore");
		
		final Response resp  = target("students/1")
								.request().put(Entity.json(data), Response.class);
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_UPDATED), 
				resp.readEntity(String.class));
		
		final SAReader1 respGet = target("students/1").queryParam("external_student_id", "23")
        		.queryParam("external_course_id", "35").request().get(SAReader1.class);
        assertEquals(new String("23"), respGet.getExternal_student_id());
        assertEquals(new Integer(35), respGet.getExternal_course_id());        
        assertEquals(new Double(31.55), respGet.getS_placement_score());
        assertEquals(new String("sophomore"), respGet.getS_year());     	
    }
	
	@Test
	public void test4StudentDelete() {
		Response resp = target("students")
				.queryParam("external_student_id", "23")
				.queryParam("external_course_id", "35")
				.request().delete(Response.class);
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_DELETED), 
				resp.readEntity(String.class));
		
		final Response respGet = target("students/1").queryParam("external_student_id", "23")
        		.queryParam("external_course_id", "35").request().get(Response.class);
		assertEquals(Status.NOT_FOUND.getStatusCode(), respGet.getStatus());
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND), 
				respGet.readEntity(String.class));
	}
}
