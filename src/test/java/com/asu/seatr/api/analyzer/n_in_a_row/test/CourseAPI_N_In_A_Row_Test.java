package com.asu.seatr.api.analyzer.n_in_a_row.test;

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

import com.asu.seatr.handlers.Handler;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.CAReader_N_In_A_Row;
import com.asu.seatr.utils.Utilities;

public class CourseAPI_N_In_A_Row_Test extends JerseyTest{
	
	private static String COURSE_3_URL = "analyzer/n_in_a_row/courses/";
	
	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_N_In_A_Row_Test.class);
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
			CAReader_N_In_A_Row ca = new CAReader_N_In_A_Row();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
			Response resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			CAReader_N_In_A_Row course =  target(COURSE_3_URL)
					.queryParam("external_course_id", "1").request()
					.get(CAReader_N_In_A_Row.class);
			assertEquals("verify  inserted record","1",course.getExternal_course_id().toString());
			
			CAReader_N_In_A_Row ca1 = new CAReader_N_In_A_Row();
			ca1.setExternal_course_id("1");
			ca1.setDescription("chemistry");
			
			resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.put(Entity.json(ca1), Response.class);
			assertEquals("course updated", Status.OK.getStatusCode(), resp.getStatus());
			
			course =  target(COURSE_3_URL)
					.queryParam("external_course_id", "1").request()
					.get(CAReader_N_In_A_Row.class);
			assertEquals("verify updated description","2",course.getDescription().toString());
			
			resp = target(COURSE_3_URL)
					.queryParam("external_course_id", "1").request()
					.delete(Response.class);
			assertEquals("deleted sucessfully",Status.OK.getStatusCode(),resp.getStatus());
			
			//verify delete
			course =  target(COURSE_3_URL)
					.queryParam("external_course_id", "1").request()
					.get(CAReader_N_In_A_Row.class);
			
			
			
		}
		catch(WebApplicationException e)
		{
			assertEquals("course not found once deleted",Status.NOT_FOUND.getStatusCode(),e.getResponse().getStatus());
			throw e;
		}
		finally
		{
			Handler.hqlTruncate("Course_N_In_A_Row");
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
			CAReader_N_In_A_Row ca1 = new CAReader_N_In_A_Row();
			ca1.setExternal_course_id("1");
			ca1.setDescription("chemistry");
			Response resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
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
		Response resp = target(COURSE_3_URL)
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
