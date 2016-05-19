package com.asu.seatr.api.analyzer.unansweredtasks.test;

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

import com.asu.seatr.api.analyzer.unansweredtasks.CourseAPI_UnansweredTasks;
import com.asu.seatr.api.analyzer.unansweredtasks.TaskAPI_UnansweredTasks;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.rest.models.analyzer.unansweredtasks.CAReader_UnansweredTasks;
import com.asu.seatr.rest.models.analyzer.unansweredtasks.TAReader_UnansweredTasks;
import com.asu.seatr.utils.Utilities;

public class TaskAPI_UnansweredTasks_Test extends JerseyTest {

	private static String TASK_1_URL =  "analyzer/unansweredtasks/tasks/";
	private static String COURSE_1_URL = "analyzer/unansweredtasks/courses/";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_UnansweredTasks.class, TaskAPI_UnansweredTasks.class);
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
			CAReader_UnansweredTasks ca = new CAReader_UnansweredTasks();
			ca.setExternal_course_id("1");
			ca.setTeaching_unit("1");
			ca.setThreshold(2.0);
			ca.setDescription("physics");
			Response resp = target(COURSE_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			TAReader_UnansweredTasks ta = new TAReader_UnansweredTasks();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_difficulty_level(1);
			resp = target(TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// get task
			TAReader_UnansweredTasks task = target(TASK_1_URL).queryParam("external_course_id", "1")
					.queryParam("external_task_id", "1").request().get(TAReader_UnansweredTasks.class);
			assertEquals("verify  inserted record", "1", task.getS_difficulty_level().toString());

			// update task
			ta = new TAReader_UnansweredTasks();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_difficulty_level(2);
			resp = target(TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.put(Entity.json(ta), Response.class);
			assertEquals("task updated", Status.OK.getStatusCode(), resp.getStatus());

			// get task
			task = target(TASK_1_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().get(TAReader_UnansweredTasks.class);
			assertEquals("verify  inserted record", "2", task.getS_difficulty_level().toString());

			// delete task
			resp = target(TASK_1_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().delete(Response.class);
			assertEquals("deleted successfully", Status.OK.getStatusCode(), resp.getStatus());

			// get task
			task = target(TASK_1_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().get(TAReader_UnansweredTasks.class);

		} catch (WebApplicationException e) {
			assertEquals("task not found once deleted", Status.NOT_FOUND.getStatusCode(),
					e.getResponse().getStatus());
			throw e;
		}

		finally {
			Handler.hqlTruncate("Task_UnansweredTasks");
			Handler.hqlTruncate("Task");
			Handler.hqlTruncate("Course_UnansweredTasks");
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
			CAReader_UnansweredTasks ca = new CAReader_UnansweredTasks();
			ca.setExternal_course_id("1");
			ca.setTeaching_unit("1");
			ca.setThreshold(2.0);
			ca.setDescription("physics");
			Response resp = target(COURSE_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// update non existing task
			TAReader_UnansweredTasks ta = new TAReader_UnansweredTasks();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_difficulty_level(1);
			resp = target(TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
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
			CAReader_UnansweredTasks ca = new CAReader_UnansweredTasks();
			ca.setExternal_course_id("1");
			ca.setTeaching_unit("1");
			ca.setThreshold(2.0);
			ca.setDescription("physics");
			Response resp = target(COURSE_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			//delete non existing task
			resp = target(TASK_1_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().delete(Response.class);
			assertEquals("task not found", Status.NOT_FOUND.getStatusCode(), resp.getStatus());
		} finally {
			Utilities.setJUnitTest(false);
		}
	}
}

