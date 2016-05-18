package com.asu.seatr.api.analyzer3.test;

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

import com.asu.seatr.api.analyzer3.CourseAPI_3;
import com.asu.seatr.api.analyzer3.StudentAPI_3;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.rest.models.analyzer3.CAReader3;
import com.asu.seatr.rest.models.analyzer3.SAReader3;
import com.asu.seatr.utils.Utilities;

public class StudentAPI_3_Test extends JerseyTest {

	private static String STUDENT_3_URL = "analyzer/3/students/";
	private static String COURSE_3_URL = "analyzer/3/courses/";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_3.class, StudentAPI_3.class);
	}

	@Before
	public void init() {
		Utilities.setJUnitTest(true);
		try {
			Utilities.clearDatabase();
		} finally {
			Utilities.setJUnitTest(false);
		}
	}

	@Test(expected = WebApplicationException.class)
	public void create_Update_Get_Delete_Student_Sucess() {
		Utilities.setJUnitTest(true);
		try {

			// create course
			CAReader3 ca = new CAReader3();
			ca.setExternal_course_id("1");
			ca.setD_current_unit_no(1);
			ca.setD_max_n(3);
			ca.setDescription("physics");
			ca.setS_units(6);
			Response resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create student
			SAReader3 sa = new SAReader3();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			sa.setS_placement_score(3.0);
			sa.setS_year("Senior");
			resp = target(STUDENT_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(sa), Response.class);
			assertEquals("student created", Status.CREATED.getStatusCode(), resp.getStatus());

			// get student
			SAReader3 student = target(STUDENT_3_URL).queryParam("external_course_id", "1")
					.queryParam("external_student_id", "1").request().get(SAReader3.class);
			assertEquals("verify  inserted record", "Senior", student.getS_year());

			// update student
			sa = new SAReader3();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			sa.setS_placement_score(3.0);
			sa.setS_year("Junior");
			resp = target(STUDENT_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.put(Entity.json(sa), Response.class);
			assertEquals("student updated", Status.OK.getStatusCode(), resp.getStatus());

			// get student
			student = target(STUDENT_3_URL).queryParam("external_course_id", "1").queryParam("external_student_id", "1")
					.request().get(SAReader3.class);
			assertEquals("verify  inserted record", "Junior", student.getS_year());

			// delete student
			resp = target(STUDENT_3_URL).queryParam("external_course_id", "1").queryParam("external_student_id", "1")
					.request().delete(Response.class);
			assertEquals("deleted successfully", Status.OK.getStatusCode(), resp.getStatus());

			// get student
			student = target(STUDENT_3_URL).queryParam("external_course_id", "1").queryParam("external_student_id", "1")
					.request().get(SAReader3.class);

		} catch (WebApplicationException e) {
			assertEquals("student not found once deleted", Status.NOT_FOUND.getStatusCode(),
					e.getResponse().getStatus());
			throw e;
		}

		finally {
			Handler.hqlTruncate("S_A3");
			Handler.hqlTruncate("Student");
			Handler.hqlTruncate("C_A3");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
			Utilities.setJUnitTest(false);
		}
	}

	@Test
	public void updateStudent_failure() {
		Utilities.setJUnitTest(true);
		try {

			// create course
			CAReader3 ca = new CAReader3();
			ca.setExternal_course_id("1");
			ca.setD_current_unit_no(1);
			ca.setD_max_n(3);
			ca.setDescription("physics");
			ca.setS_units(6);
			Response resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// update non existing student
			SAReader3 sa = new SAReader3();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			sa.setS_placement_score(3.0);
			sa.setS_year("Junior");
			resp = target(STUDENT_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.put(Entity.json(sa), Response.class);

			assertEquals("student not found", Status.NOT_FOUND.getStatusCode(), resp.getStatus());
		} finally {
			Utilities.setJUnitTest(false);
		}
	}

	@Test
	public void deleteStudent_failure() {
		Utilities.setJUnitTest(true);
		try {

			// create course
			CAReader3 ca = new CAReader3();
			ca.setExternal_course_id("1");
			ca.setD_current_unit_no(1);
			ca.setD_max_n(3);
			ca.setDescription("physics");
			ca.setS_units(6);
			Response resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			//delete non existing student
			resp = target(STUDENT_3_URL).queryParam("external_course_id", "1").queryParam("external_student_id", "1")
					.request().delete(Response.class);
			assertEquals("student not found", Status.NOT_FOUND.getStatusCode(), resp.getStatus());
		} finally {
			Utilities.setJUnitTest(false);
		}
	}
}
