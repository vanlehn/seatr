package com.asu.seatr.api.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Test;

import com.asu.seatr.api.CommonAPI;
import com.asu.seatr.api.analyzer.unansweredtasks.CourseAPI_UnansweredTasks;
import com.asu.seatr.exceptions.CourseAnalyzerMapException;
import com.asu.seatr.handlers.CourseAnalyzerMapHandler;
import com.asu.seatr.models.CourseAnalyzerMap;
import com.asu.seatr.rest.models.analyzer.unansweredtasks.CAReader_UnansweredTasks;
import com.asu.seatr.utils.Utilities;

public class CommonAPITest2  extends JerseyTest{

	private static String SET_ANALYZER = "courses/setanalyzer/";
	private static String COURSE_URL = "analyzer/unansweredtasks/courses/";
	
	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CommonAPI.class,CourseAPI_UnansweredTasks.class);
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
	
	@Test
	public void setAnalyzer_success()
	{
		Utilities.setJUnitTest(true);
		try
		{
			
			CAReader_UnansweredTasks ca = new CAReader_UnansweredTasks();
			ca.setExternal_course_id("1");
			ca.setTeaching_unit("1");
			ca.setThreshold(2.0);
			ca.setDescription("physics");
			Response resp = target(COURSE_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			
			resp = target(SET_ANALYZER)
					.queryParam("external_course_id", "1").queryParam("analyzer_name", "UnansweredTasks").queryParam("active", true).request()
					.get(Response.class);
			assertEquals("mapping created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			//now set analyzer 2 as inactive for the course
			resp = target(SET_ANALYZER)
					.queryParam("external_course_id", "1").queryParam("analyzer_name", "N_In_A_Row").queryParam("active", false).request()
					.get(Response.class);
			assertEquals("mapping created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			CourseAnalyzerMap cam = CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId("1", 1);
			assertEquals("this analyzer should be active",true,cam.isActive());
			
			cam = CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId("1", 2);
			assertEquals("this analyzer should be active",false,cam.isActive());
			
			//now map analyzer 3 as active for the course
			resp = target(SET_ANALYZER)
					.queryParam("external_course_id", "1").queryParam("analyzer_name", "Required_Optional").queryParam("active", true).request()
					.get(Response.class);
			assertEquals("mapping created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			cam = CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId("1", 3);
			assertEquals("this analyzer should be active",true,cam.isActive());
			
			cam = CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId("1", 1);
			assertEquals("this analyzer should be inactive",false,cam.isActive());
			
			cam = CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId("1", 2);
			assertEquals("this analyzer should be inactive",false,cam.isActive());
			
			//set analyzer 1 as active
			resp = target(SET_ANALYZER)
					.queryParam("external_course_id", "1").queryParam("analyzer_name", "UnansweredTasks").queryParam("active", true).request()
					.get(Response.class);
			assertEquals("mapping created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			cam = CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId("1", 3);
			assertEquals("this analyzer should be inactive",false,cam.isActive());
			
			cam = CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId("1", 1);
			assertEquals("this analyzer should be active",true,cam.isActive());
			
			cam = CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId("1", 2);
			assertEquals("this analyzer should be inactive",false,cam.isActive());
			

		}
		catch(Exception e)
		{
			e.printStackTrace();
			fail("execution shuld not come here");
		}
		finally
		{
			Utilities.clearDatabase();
			Utilities.setJUnitTest(false);
		}
	}
	
	@Test
	public void setAnalyzer_fail()
	{
		Utilities.setJUnitTest(true);
		try
		{
			//empty analyzer_id
			Response resp = target(SET_ANALYZER)
							.queryParam("external_course_id", "1").queryParam("analyzer_name", "").queryParam("active", true).request()
							.get(Response.class);
			assertEquals("invalid analyzer id",Status.OK.getStatusCode(),resp.getStatus());
			
			//empty course
			resp = target(SET_ANALYZER)
							.queryParam("external_course_id", "").queryParam("analyzer_name", "UnansweredTasks").queryParam("active", true).request()
							.get(Response.class);
			assertEquals("invalid course id",Status.OK.getStatusCode(),resp.getStatus());
			
			//invalid analyzer
			resp = target(SET_ANALYZER)
							.queryParam("external_course_id", "1").queryParam("analyzer_name", "abc").queryParam("active", true).request()
							.get(Response.class);
			assertEquals("invalid analyzer id",Status.OK.getStatusCode(),resp.getStatus());
			
			//invalid course
			resp = target(SET_ANALYZER)
							.queryParam("external_course_id", "2").queryParam("analyzer_name", "UnansweredTasks").queryParam("active", true).request()
							.get(Response.class);
			assertEquals("invalid course id",Status.OK.getStatusCode(),resp.getStatus());
		}
		finally
		{
			Utilities.clearDatabase();
			Utilities.setJUnitTest(false);
		}
	}
	
}
