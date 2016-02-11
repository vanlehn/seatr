package com.asu.seatr.test;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import com.asu.seatr.CourseAPI;


public class CourseAPITest extends JerseyTest{
	protected Application configure(){
		return new ResourceConfig(CourseAPI.class);
	}
	
	/*@Test
	public void test(){
		final String test=target("course/get").queryParam("id", 1).request().get(String.class);
		assertEquals("test",test);
	}*/
	
}
