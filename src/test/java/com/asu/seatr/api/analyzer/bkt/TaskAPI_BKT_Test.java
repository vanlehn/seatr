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
import com.asu.seatr.api.bkt.TaskAPI_BKT;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.rest.models.analyzer.bkt.CAReader_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.TAReader_BKT;
import com.asu.seatr.utils.Utilities;

public class TaskAPI_BKT_Test extends JerseyTest {

	private static String TASK_3_URL =  "analyzer/bkt/tasks/";
	private static String COURSE_3_URL = "analyzer/bkt/courses/";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_BKT.class, TaskAPI_BKT.class);
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
			CAReader_BKT ca = new CAReader_BKT();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
			Response resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			TAReader_BKT ta = new TAReader_BKT();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setDifficulty(0.2);
			ta.setType("multiple-choice");
			resp = target(TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// get task
			TAReader_BKT task = target(TASK_3_URL).queryParam("external_course_id", "1")
					.queryParam("external_task_id", "1").request().get(TAReader_BKT.class);
			assertEquals("verify  inserted record", "0.2", String.valueOf(task.getDifficulty()));
			assertEquals("verify  inserted record", "multiple-choice", String.valueOf(task.getType()));

			// update task
			ta = new TAReader_BKT();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setDifficulty(0.3);
			ta.setType("structure-input");
			resp = target(TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.put(Entity.json(ta), Response.class);
			assertEquals("task updated", Status.OK.getStatusCode(), resp.getStatus());

			// get task
			task = target(TASK_3_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().get(TAReader_BKT.class);
			assertEquals("verify  inserted record", "0.3", String.valueOf(task.getDifficulty()));
			assertEquals("verify  inserted record", "structure-input", String.valueOf(task.getType()));
			
			// delete task
			resp = target(TASK_3_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().delete(Response.class);
			assertEquals("deleted successfully", Status.OK.getStatusCode(), resp.getStatus());

			// get task
			task = target(TASK_3_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().get(TAReader_BKT.class);

		} catch (WebApplicationException e) {
			assertEquals("task not found once deleted", Status.NOT_FOUND.getStatusCode(),
					e.getResponse().getStatus());
			throw e;
		}

		finally {
			Handler.hqlTruncate("Task_BKT");
			Handler.hqlTruncate("Task");
			Handler.hqlTruncate("Course_BKT");
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
			CAReader_BKT ca = new CAReader_BKT();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
			Response resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// update non existing task
			TAReader_BKT ta = new TAReader_BKT();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setDifficulty(0.1);
			ta.setType("multiple-choice");
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
			CAReader_BKT ca = new CAReader_BKT();
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

