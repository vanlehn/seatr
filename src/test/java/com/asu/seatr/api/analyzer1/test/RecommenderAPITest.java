package com.asu.seatr.api.analyzer1.test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.FixMethodOrder;
import org.junit.Test;

import com.asu.seatr.api.AdminAPI;
import com.asu.seatr.api.CourseAPI_1;
import com.asu.seatr.api.RecommenderAPI;
import com.asu.seatr.api.analyzer2.CourseAPI;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.models.UserCourse;
import com.asu.seatr.rest.models.CAReader1;
import com.asu.seatr.rest.models.UserReader;
import com.asu.seatr.utils.Utilities;

import static org.junit.Assert.assertEquals;

import java.util.List;


public class RecommenderAPITest extends JerseyTest {

	private static String RECOMMENDER1_URL = "analyzer/1/gettasks/";
	private static String GETTASKS_1_URL = "analyzer/1/gettasks/";
	private static String CREATE_COURSE_1_URL = "analyzer/1/courses/";
	
	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_1.class,RecommenderAPI.class,AdminAPI.class);
	}

	@Test
	public void emptyCourseId() {
		Utilities.setJUnitTest(true);
		try {
			final List<String> resList = target(GETTASKS_1_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "").queryParam("number_of_tasks", "5").request().get(List.class);
		} catch (WebApplicationException e) {

			assertEquals("empty course id must return 400", Status.BAD_REQUEST.getStatusCode(),
					e.getResponse().getStatus());
		} catch (Exception e) {
			System.out.println("Error in testing");
			e.printStackTrace();
		} finally {
			Utilities.setJUnitTest(false);
		}
	}
	@Test
	public void emptyStudentId() {
		Utilities.setJUnitTest(true);
		try {
			final List<String> resList = target(GETTASKS_1_URL).queryParam("external_student_id", "")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "5").request().get(List.class);
		} catch (WebApplicationException e) {

			assertEquals("empty student id must return 400", Status.BAD_REQUEST.getStatusCode(),
					e.getResponse().getStatus());
		} catch (Exception e) {
			System.out.println("Error in testing");
			e.printStackTrace();
		} finally {
			Utilities.setJUnitTest(false);
		}
	}
	@Test
	public void invalidCoureId()
	{

		Utilities.setJUnitTest(true);
		try {
			final List<String> resList = target(GETTASKS_1_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "5").request().get(List.class);
		} catch (WebApplicationException e) {

			assertEquals("course not found must return 400", Status.BAD_REQUEST.getStatusCode(),
					e.getResponse().getStatus());
		} catch (Exception e) {
			System.out.println("Error in testing");
			e.printStackTrace();
		} finally {
			Utilities.setJUnitTest(false);
		}
	
	}
	@Test
	public void invalidStudentId()
	{

		Utilities.setJUnitTest(true);
		try {
			
			/*UserReader ur = new UserReader();
			ur.setUsername("cse310");
			ur.setPassword("hello123");
			Response rb = target("superadmin/users").request().post(Entity.json(ur),Response.class);*/
			//first creating dummy course
			CAReader1 ca = new CAReader1();
			ca.setExternal_course_id("1");
			ca.setTeaching_unit("1");
			ca.setThreshold(2.0);
			ca.setDescription("physics");
			
			final Response resp = target(CREATE_COURSE_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ca), Response.class);
			
			assertEquals("course created",Status.CREATED.getStatusCode(),resp.getStatus());
			
			final List<String> resList = target(GETTASKS_1_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "5").request().get(List.class);
			
			//clear all changes done by this test case
			Handler.hqlTruncate("C_A1");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
		} catch (WebApplicationException e) {

			Handler.hqlTruncate("C_A1");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
		} catch (Exception e) {
			System.out.println("Error in testing");
			e.printStackTrace();
		} finally {
			Utilities.setJUnitTest(false);
		}
	
	}

}
