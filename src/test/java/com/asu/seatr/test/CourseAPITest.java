package com.asu.seatr.test;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.asu.seatr.CourseAPI;
import com.asu.seatr.models.Course;


public class CourseAPITest extends JerseyTest{
	protected Application configure(){
		return new ResourceConfig(CourseAPI.class);
	}
	/*
	@Test void test(){
		int id=testAdd();
		testGet(id);
	}
	
	private void testGet(int id){
		System.out.println(target("course/get").queryParam("id", id).request().get());
	}
	
	private int testAdd(){
		Course c=new Course();
		c.setDescription("new course");
		Entity<Course> courseEntity=Entity.entity(c,MediaType.APPLICATION_JSON);
		Response response=target("course/add").request().put(courseEntity);
		response.getEntity().
		assertEquals("new course", response.readEntity(Course.class).getDescription());
	}*/
	
}
