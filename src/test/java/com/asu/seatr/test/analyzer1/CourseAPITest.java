package com.asu.seatr.test.analyzer1;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.asu.seatr.api.CourseAPI;
import com.asu.seatr.api.analyzer.Analyzer1API;
import com.asu.seatr.models.analyzers.course.C_A1;
import com.asu.seatr.rest.models.CAReader1;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;


public class CourseAPITest extends JerseyTest{
	protected Application configure(){
		return new ResourceConfig(Analyzer1API.class);
	}
	
	private String ext_course_id="23";
	
	@Test 
	public void test(){
		//test1CourseCreate();
		test2CourseGet();
		test3CourseUpdate();
		test4CourseDelete();
	}
	
	private void test1CourseCreate() {
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_course_id",ext_course_id);
		data.put("description", "test course");
		data.put("threshold", "0.8");
		data.put("teaching_unit", "unit 1");
		
		final Response resp  = target("analyzer1/course")
								.request().post(Entity.json(data), Response.class);
		assertEquals(Status.CREATED.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_CREATED), 
				resp.readEntity(String.class));
		
	}
	
	private void test2CourseGet() {
        final CAReader1 resp = target("analyzer1/course").queryParam("external_course_id", ext_course_id)
        		.request().get(CAReader1.class);
        assertEquals(new String("23"), resp.getExternal_course_id());
        assertEquals(new String("test course"), resp.getDescription());        
        assertEquals(new Double(0.8), new Double(resp.getThreshold()));
        assertEquals(new String("unit 1"), resp.getTeaching_unit());                
    }
	
	private void test3CourseUpdate() {
    	Map<String, String> data = new HashMap<String, String>();
		data.put("external_course_id",ext_course_id);
		data.put("description", "test course update");
		data.put("threshold", "0.9");
		data.put("teaching_unit", "unit 2");
		
		final Response resp  = target("analyzer1/course")
								.request().put(Entity.json(data), Response.class);
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_UPDATED), 
				resp.readEntity(String.class));  	
    }

	private void test4CourseDelete() {
		Response resp = target("students")
				.queryParam("external_course_id", ext_course_id)
				.request().delete(Response.class);
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_DELETED), 
				resp.readEntity(String.class));
	}
	
	
}
