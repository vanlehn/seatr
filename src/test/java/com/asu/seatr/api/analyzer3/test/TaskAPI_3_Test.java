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
import com.asu.seatr.api.analyzer3.TaskAPI_3;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.rest.models.analyzer3.CAReader3;
import com.asu.seatr.rest.models.analyzer3.TAReader3;
import com.asu.seatr.utils.Utilities;

public class TaskAPI_3_Test extends JerseyTest {

	private static String TASK_3_URL =  "analyzer/3/tasks/";
	private static String COURSE_3_URL = "analyzer/3/courses/";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_3.class, TaskAPI_3.class);
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
			CAReader3 ca = new CAReader3();
			ca.setExternal_course_id("1");
			ca.setD_current_unit_no(1);
			ca.setD_max_n(3);
			ca.setDescription("physics");
			ca.setS_units(6);
			Response resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			TAReader3 ta = new TAReader3();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_is_required(true);
			ta.setS_sequence_no(1);
			ta.setS_unit_no(1);
			resp = target(TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// get task
			TAReader3 task = target(TASK_3_URL).queryParam("external_course_id", "1")
					.queryParam("external_task_id", "1").request().get(TAReader3.class);
			assertEquals("verify  inserted record", "1", task.getS_unit_no().toString());

			// update task
			ta = new TAReader3();
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
					.request().get(TAReader3.class);
			assertEquals("verify  inserted record", "2", task.getS_unit_no().toString());

			// delete task
			resp = target(TASK_3_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().delete(Response.class);
			assertEquals("deleted successfully", Status.OK.getStatusCode(), resp.getStatus());

			// get task
			task = target(TASK_3_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().get(TAReader3.class);

		} catch (WebApplicationException e) {
			assertEquals("task not found once deleted", Status.NOT_FOUND.getStatusCode(),
					e.getResponse().getStatus());
			throw e;
		}

		finally {
			Handler.hqlTruncate("T_A3");
			Handler.hqlTruncate("Task");
			Handler.hqlTruncate("C_A3");
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
			CAReader3 ca = new CAReader3();
			ca.setExternal_course_id("1");
			ca.setD_current_unit_no(1);
			ca.setD_max_n(3);
			ca.setDescription("physics");
			ca.setS_units(6);
			Response resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// update non existing task
			TAReader3 ta = new TAReader3();
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
			CAReader3 ca = new CAReader3();
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

