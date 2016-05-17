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
import com.asu.seatr.api.analyzer1.StudentAPI_1;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.rest.models.analyzer1.CAReader1;
import com.asu.seatr.rest.models.analyzer1.SAReader1;
import com.asu.seatr.utils.Utilities;

public class StudentAPI_1_Test extends JerseyTest {

	private static String STUDENT_1_URL = "analyzer/1/students/";
	private static String COURSE_1_URL = "analyzer/1/courses/";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_1.class, StudentAPI_1.class);
	}

	@Before
	public void init() {
		Utilities.setJUnitTest(true);
		try {
			Handler.hqlTruncate("S_A1");
			Handler.hqlTruncate("Student");
			Handler.hqlTruncate("C_A1");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
		} finally {
			Utilities.setJUnitTest(false);
		}
	}

	@Test(expected = WebApplicationException.class)
	public void create_Update_Get_Delete_Student_Sucess() {
		Utilities.setJUnitTest(true);
		try {

			// create course
			CAReader1 ca = new CAReader1();
			ca.setExternal_course_id("1");
			ca.setTeaching_unit("1");
			ca.setThreshold(2.0);
			ca.setDescription("physics");
			Response resp = target(COURSE_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create student
			SAReader1 sa = new SAReader1();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			sa.setS_placement_score(3.0);
			sa.setS_year("Senior");
			resp = target(STUDENT_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(sa), Response.class);
			assertEquals("student created", Status.CREATED.getStatusCode(), resp.getStatus());

			// get student
			SAReader1 student = target(STUDENT_1_URL).queryParam("external_course_id", "1")
					.queryParam("external_student_id", "1").request().get(SAReader1.class);
			assertEquals("verify  inserted record", "Senior", student.getS_year());

			// update student
			sa = new SAReader1();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			sa.setS_placement_score(3.0);
			sa.setS_year("Junior");
			resp = target(STUDENT_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.put(Entity.json(sa), Response.class);
			assertEquals("student updated", Status.OK.getStatusCode(), resp.getStatus());

			// get student
			student = target(STUDENT_1_URL).queryParam("external_course_id", "1").queryParam("external_student_id", "1")
					.request().get(SAReader1.class);
			assertEquals("verify  inserted record", "Junior", student.getS_year());

			// delete student
			resp = target(STUDENT_1_URL).queryParam("external_course_id", "1").queryParam("external_student_id", "1")
					.request().delete(Response.class);
			assertEquals("deleted successfully", Status.OK.getStatusCode(), resp.getStatus());

			// get student
			student = target(STUDENT_1_URL).queryParam("external_course_id", "1").queryParam("external_student_id", "1")
					.request().get(SAReader1.class);

		} catch (WebApplicationException e) {
			assertEquals("student not found once deleted", Status.NOT_FOUND.getStatusCode(),
					e.getResponse().getStatus());
			throw e;
		}

		finally {
			Handler.hqlTruncate("S_A1");
			Handler.hqlTruncate("Student");
			Handler.hqlTruncate("C_A1");
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
			CAReader1 ca = new CAReader1();
			ca.setExternal_course_id("1");
			ca.setTeaching_unit("1");
			ca.setThreshold(2.0);
			ca.setDescription("physics");
			Response resp = target(COURSE_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// update non existing student
			SAReader1 sa = new SAReader1();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			sa.setS_placement_score(3.0);
			sa.setS_year("Junior");
			resp = target(STUDENT_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
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
			CAReader1 ca = new CAReader1();
			ca.setExternal_course_id("1");
			ca.setTeaching_unit("1");
			ca.setThreshold(2.0);
			ca.setDescription("physics");
			Response resp = target(COURSE_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			//delete non existing student
			resp = target(STUDENT_1_URL).queryParam("external_course_id", "1").queryParam("external_student_id", "1")
					.request().delete(Response.class);
			assertEquals("student not found", Status.NOT_FOUND.getStatusCode(), resp.getStatus());
		} finally {
			Utilities.setJUnitTest(false);
		}
	}
}
