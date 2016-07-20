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
import com.asu.seatr.api.bkt.StudentAPI_BKT;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.rest.models.analyzer.bkt.CAReader_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.SAReader_BKT;
import com.asu.seatr.utils.Utilities;

public class StudentAPI_BKT_Test extends JerseyTest {

	private static String STUDENT_3_URL = "analyzer/bkt/students/";
	private static String COURSE_3_URL = "analyzer/bkt/courses/";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_BKT.class, StudentAPI_BKT.class);
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
			CAReader_BKT ca = new CAReader_BKT();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
			Response resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create student
			SAReader_BKT sa = new SAReader_BKT();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			resp = target(STUDENT_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(sa), Response.class);
			assertEquals("student created", Status.CREATED.getStatusCode(), resp.getStatus());

			// get student
			SAReader_BKT student = target(STUDENT_3_URL).queryParam("external_course_id", "1")
					.queryParam("external_student_id", "1").request().get(SAReader_BKT.class);
			assertEquals("verify  inserted record", "1", student.getExternal_student_id());

			// update student
			sa = new SAReader_BKT();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			resp = target(STUDENT_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.put(Entity.json(sa), Response.class);
			assertEquals("student updated", Status.OK.getStatusCode(), resp.getStatus());

			// get student
			student = target(STUDENT_3_URL).queryParam("external_course_id", "1").queryParam("external_student_id", "1")
					.request().get(SAReader_BKT.class);
			assertEquals("verify  inserted record", "1", student.getExternal_student_id());

			// delete student
			resp = target(STUDENT_3_URL).queryParam("external_course_id", "1").queryParam("external_student_id", "1")
					.request().delete(Response.class);
			assertEquals("deleted successfully", Status.OK.getStatusCode(), resp.getStatus());

			// get student
			student = target(STUDENT_3_URL).queryParam("external_course_id", "1").queryParam("external_student_id", "1")
					.request().get(SAReader_BKT.class);

		} catch (WebApplicationException e) {
			assertEquals("student not found once deleted", Status.NOT_FOUND.getStatusCode(),
					e.getResponse().getStatus());
			throw e;
		}

		finally {
			Handler.hqlTruncate("Student_BKT");
			Handler.hqlTruncate("Student");
			Handler.hqlTruncate("Course_BKT");
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
			CAReader_BKT ca = new CAReader_BKT();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
			Response resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// update non existing student
			SAReader_BKT sa = new SAReader_BKT();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
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
			CAReader_BKT ca = new CAReader_BKT();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
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
