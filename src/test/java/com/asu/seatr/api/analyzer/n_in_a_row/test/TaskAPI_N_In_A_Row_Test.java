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

import com.asu.seatr.api.analyzer.n_in_a_row.CourseAPI_N_In_A_Row;
import com.asu.seatr.api.analyzer.n_in_a_row.TaskAPI_N_In_A_Row;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.CAReader_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.TAReader_N_In_A_Row;
import com.asu.seatr.utils.Utilities;

public class TaskAPI_N_In_A_Row_Test extends JerseyTest {

	private static String TASK_3_URL =  "analyzer/n_in_a_row/tasks/";
	private static String COURSE_3_URL = "analyzer/n_in_a_row/courses/";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_N_In_A_Row.class, TaskAPI_N_In_A_Row.class);
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
			CAReader_N_In_A_Row ca = new CAReader_N_In_A_Row();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
			Response resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			TAReader_N_In_A_Row ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			resp = target(TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// get task
			TAReader_N_In_A_Row task = target(TASK_3_URL).queryParam("external_course_id", "1")
					.queryParam("external_task_id", "1").request().get(TAReader_N_In_A_Row.class);
			assertEquals("verify  inserted record", "1", task.getExternal_course_id());

			// update task
			ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			resp = target(TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.put(Entity.json(ta), Response.class);
			assertEquals("task updated", Status.OK.getStatusCode(), resp.getStatus());

			// get task
			task = target(TASK_3_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().get(TAReader_N_In_A_Row.class);
			assertEquals("verify  inserted record", "1", task.getExternal_task_id());

			// delete task
			resp = target(TASK_3_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().delete(Response.class);
			assertEquals("deleted successfully", Status.OK.getStatusCode(), resp.getStatus());

			// get task
			task = target(TASK_3_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().get(TAReader_N_In_A_Row.class);

		} catch (WebApplicationException e) {
			assertEquals("task not found once deleted", Status.NOT_FOUND.getStatusCode(),
					e.getResponse().getStatus());
			throw e;
		}

		finally {
			Handler.hqlTruncate("Task_N_In_A_Row");
			Handler.hqlTruncate("Task");
			Handler.hqlTruncate("Course_N_In_A_Row");
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
			CAReader_N_In_A_Row ca = new CAReader_N_In_A_Row();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
			Response resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// update non existing task
			TAReader_N_In_A_Row ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");

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
			CAReader_N_In_A_Row ca = new CAReader_N_In_A_Row();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
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

