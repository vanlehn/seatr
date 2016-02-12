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
	
	
	@Test
	public void testGet(){
		System.out.println(target("course/get").queryParam("id", 1).request().get());
	}
	
	@Test
	public void testAdd(){
		Course c=new Course();
		c.setDescription("new course");
		Entity<Course> courseEntity=Entity.entity(c,MediaType.APPLICATION_JSON);
		target("course/add").request().put(courseEntity);
		Response response=target("course/find").queryParam("id", 1).request().get();
		assertEquals("new course", response.readEntity(Course.class).getDescription());
	}
	
}
