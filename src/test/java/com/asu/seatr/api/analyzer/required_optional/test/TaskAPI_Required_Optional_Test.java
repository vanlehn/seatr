package com.asu.seatr.api.analyzer.required_optional.test;

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

import com.asu.seatr.api.analyzer.required_optional.CourseAPI_Required_Optional;
import com.asu.seatr.api.analyzer.required_optional.TaskAPI_Required_Optional;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.rest.models.analyzer.required_optional.CAReader_Required_Optional;
import com.asu.seatr.rest.models.analyzer.required_optional.TAReader_Required_Optional;
import com.asu.seatr.utils.Utilities;

public class TaskAPI_Required_Optional_Test extends JerseyTest {

	private static String TASK_3_URL =  "analyzer/required_optional/tasks/";
	private static String COURSE_3_URL = "analyzer/required_optional/courses/";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_Required_Optional.class, TaskAPI_Required_Optional.class);
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
	public void create_Update_Get_Delete_Task_Sucess() {
		Utilities.setJUnitTest(true);
		try {

			// create course
			CAReader_Required_Optional ca = new CAReader_Required_Optional();
			ca.setExternal_course_id("1");
			ca.setD_current_unit_no(1);
			ca.setD_max_n(3);
			ca.setDescription("physics");
			ca.setS_units(6);
			Response resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			TAReader_Required_Optional ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_is_required(true);
			ta.setS_sequence_no(1);
			ta.setS_unit_no(1);
			resp = target(TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// get task
			TAReader_Required_Optional task = target(TASK_3_URL).queryParam("external_course_id", "1")
					.queryParam("external_task_id", "1").request().get(TAReader_Required_Optional.class);
			assertEquals("verify  inserted record", "1", task.getS_unit_no().toString());

			// update task
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_is_required(true);
			ta.setS_sequence_no(1);
			ta.setS_unit_no(2);
			resp = target(TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.put(Entity.json(ta), Response.class);
			assertEquals("task updated", Status.OK.getStatusCode(), resp.getStatus());

			// get task
			task = target(TASK_3_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().get(TAReader_Required_Optional.class);
			assertEquals("verify  inserted record", "2", task.getS_unit_no().toString());

			// delete task
			resp = target(TASK_3_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().delete(Response.class);
			assertEquals("deleted successfully", Status.OK.getStatusCode(), resp.getStatus());

			// get task
			task = target(TASK_3_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().get(TAReader_Required_Optional.class);

		} catch (WebApplicationException e) {
			assertEquals("task not found once deleted", Status.NOT_FOUND.getStatusCode(),
					e.getResponse().getStatus());
			throw e;
		}

		finally {
			Handler.hqlTruncate("Task_Required_Optional");
			Handler.hqlTruncate("Task");
			Handler.hqlTruncate("Course_Required_Optional");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
			Utilities.setJUnitTest(false);
		}
	}

	@Test
	public void updateTask_failure() {
		Utilities.setJUnitTest(true);
		try {

			// create course
			CAReader_Required_Optional ca = new CAReader_Required_Optional();
			ca.setExternal_course_id("1");
			ca.setD_current_unit_no(1);
			ca.setD_max_n(3);
			ca.setDescription("physics");
			ca.setS_units(6);
			Response resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// update non existing task
			TAReader_Required_Optional ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_is_required(true);
			ta.setS_sequence_no(1);
			ta.setS_unit_no(1);
			resp = target(TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.put(Entity.json(ta), Response.class);

			assertEquals("task not found", Status.NOT_FOUND.getStatusCode(), resp.getStatus());
		} finally {
			Utilities.setJUnitTest(false);
		}
	}

	@Test
	public void deleteTask_failure() {
		Utilities.setJUnitTest(true);
		try {

			// create course
			CAReader_Required_Optional ca = new CAReader_Required_Optional();
			ca.setExternal_course_id("1");
			ca.setD_current_unit_no(1);
			ca.setD_max_n(3);
			ca.setDescription("physics");
			ca.setS_units(6);
			Response resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			//delete non existing task
			resp = target(TASK_3_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().delete(Response.class);
			assertEquals("task not found", Status.NOT_FOUND.getStatusCode(), resp.getStatus());
		} finally {
			Utilities.setJUnitTest(false);
		}
	}
}

