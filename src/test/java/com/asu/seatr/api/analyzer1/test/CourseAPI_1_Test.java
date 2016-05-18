package com.asu.seatr.api.analyzer1.test;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Test;

import com.asu.seatr.api.analyzer1.CourseAPI_1;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.rest.models.analyzer1.CAReader1;
import com.asu.seatr.utils.Utilities;

public class CourseAPI_1_Test extends JerseyTest{
	
	private static String COURSE_1_URL = "analyzer/1/courses/";
	
	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_1.class);
	}
	
	@Before
	public void init()
	{
		Utilities.setJUnitTest(true);
		try
		{
			Utilities.clearDatabase();
		}
		finally
		{
			Utilities.setJUnitTest(false);
		}
	}
	@Test(expected=WebApplicationException.class)
	public void create_Update_Get_Delete_Course_Sucess()
	{
		Utilities.setJUnitTest(true);
		try
		{
			CAReader1 ca = new CAReader1();
			ca.setExternal_course_id("1");
			ca.setTeaching_unit("1");
			ca.setThreshold(2.0);
			ca.setDescription("physics");
			Response resp = target(COURSE_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			CAReader1 course =  target(COURSE_1_URL)
					.queryParam("external_course_id", "1").request()
					.get(CAReader1.class);
			assertEquals("verify  inserted record","1",course.getTeaching_unit());
			
			CAReader1 ca1 = new CAReader1();
			ca1.setExternal_course_id("1");
			ca1.setTeaching_unit("2");
			ca1.setThreshold(2.0);
			ca1.setDescription("physics");		
			
			resp = target(COURSE_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.put(Entity.json(ca1), Response.class);
			assertEquals("course updated", Status.OK.getStatusCode(), resp.getStatus());
			
			course =  target(COURSE_1_URL)
					.queryParam("external_course_id", "1").request()
					.get(CAReader1.class);
			assertEquals("verify updated teaching unit","2",course.getTeaching_unit());
			
			resp = target(COURSE_1_URL)
					.queryParam("external_course_id", "1").request()
					.delete(Response.class);
			assertEquals("deleted sucessfully",Status.OK.getStatusCode(),resp.getStatus());
			
			//verify delete
			course =  target(COURSE_1_URL)
					.queryParam("external_course_id", "1").request()
					.get(CAReader1.class);
			
			
			
		}
		catch(WebApplicationException e)
		{
			assertEquals("course not found once deleted",Status.NOT_FOUND.getStatusCode(),e.getResponse().getStatus());
			throw e;
		}
		finally
		{
			Handler.hqlTruncate("C_A1");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
			Utilities.setJUnitTest(false);
		}
	}

	@Test
	public void updateCourse_failure()
	{
		Utilities.setJUnitTest(true);
		try
		{
			CAReader1 ca1 = new CAReader1();
			ca1.setExternal_course_id("1");
			ca1.setTeaching_unit("2");
			ca1.setThreshold(2.0);
			ca1.setDescription("physics");
			Response resp = target(COURSE_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
				.put(Entity.json(ca1), Response.class);
			assertEquals("course not found",Status.NOT_FOUND.getStatusCode(),resp.getStatus());
		}
		finally
		{
			Utilities.setJUnitTest(false);
		}
	}
	
	@Test
	public void deleteCourse_failure()
	{
		Utilities.setJUnitTest(true);
		try
		{
		Response resp = target(COURSE_1_URL)
				.queryParam("external_course_id", "1").request()
				.delete(Response.class);
		assertEquals("course not found",Status.NOT_FOUND.getStatusCode(),resp.getStatus());
		}
		finally
		{
			Utilities.setJUnitTest(false);
		}
	}
	

}
