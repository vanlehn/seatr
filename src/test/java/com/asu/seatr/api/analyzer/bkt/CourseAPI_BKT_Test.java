package com.asu.seatr.api.analyzer.bkt;

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

import com.asu.seatr.api.bkt.CourseAPI_BKT;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.rest.models.analyzer.bkt.CAReader_BKT;
import com.asu.seatr.utils.Utilities;

public class CourseAPI_BKT_Test extends JerseyTest{
	
	private static String COURSE_URL = "analyzer/bkt/courses/";
	
	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_BKT.class);
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
			CAReader_BKT ca = new CAReader_BKT();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
			Response resp = target(COURSE_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			CAReader_BKT course =  target(COURSE_URL)
					.queryParam("external_course_id", "1").request()
					.get(CAReader_BKT.class);
			assertEquals("verify  inserted record","1",course.getExternal_course_id().toString());
			
			resp = target(COURSE_URL)
					.queryParam("external_course_id", "1").request()
					.delete(Response.class);
			assertEquals("deleted sucessfully",Status.OK.getStatusCode(),resp.getStatus());
			
			//verify delete
			course =  target(COURSE_URL)
					.queryParam("external_course_id", "1").request()
					.get(CAReader_BKT.class);
			
			
			
		}
		catch(WebApplicationException e)
		{
			assertEquals("course not found once deleted",Status.NOT_FOUND.getStatusCode(),e.getResponse().getStatus());
			throw e;
		}
		finally
		{
			Handler.hqlTruncate("Course_BKT");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
			Utilities.setJUnitTest(false);
		}
	}


	@Test
	public void deleteCourse_failure()
	{
		Utilities.setJUnitTest(true);
		try
		{
		Response resp = target(COURSE_URL)
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
