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
import com.asu.seatr.api.CommonAPI;
import com.asu.seatr.api.RecommenderAPI;
import com.asu.seatr.api.analyzer1.CourseAPI_1;
import com.asu.seatr.api.analyzer1.RecommenderAPI_1;
import com.asu.seatr.api.analyzer1.StudentAPI_1;
import com.asu.seatr.api.analyzer1.TaskAPI_1;
import com.asu.seatr.api.analyzer2.CourseAPI;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.models.CourseAnalyzerMap;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.UserCourse;
import com.asu.seatr.models.analyzers.student.S_A1;
import com.asu.seatr.models.analyzers.task.T_A1;
import com.asu.seatr.rest.models.UserReader;
import com.asu.seatr.rest.models.analyzer1.CAReader1;
import com.asu.seatr.rest.models.analyzer1.SAReader1;
import com.asu.seatr.rest.models.analyzer1.STAReader1;
import com.asu.seatr.rest.models.analyzer1.TAReader1;
import com.asu.seatr.utils.Utilities;

import static org.junit.Assert.assertEquals;

import java.util.List;

public class RecommenderAPITest extends JerseyTest {

	private static String RECOMMENDER1_URL = "analyzer/1/gettasks/";
	private static String GETTASKS_1_URL = "analyzer/1/gettasks/";
	private static String CREATE_COURSE_1_URL = "analyzer/1/courses/";
	private static String CREATE_TASK_1_URL = "analyzer/1/tasks/";
	private static String SET_ANALYZER_COURSE = "courses/setanalyzer/";
	private static String CREATE_STUDENT_1_URL = "analyzer/1/students/";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_1.class, RecommenderAPI_1.class, AdminAPI.class, TaskAPI_1.class, CommonAPI.class, StudentAPI_1.class);
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
	public void invalidCoureId() {

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
	public void noTasksForCourse() {

		Utilities.setJUnitTest(true);
		try {

			/*
			 * UserReader ur = new UserReader(); ur.setUsername("cse310");
			 * ur.setPassword("hello123"); Response rb =
			 * target("superadmin/users").request().post(Entity.json(ur),
			 * Response.class);
			 */
			// first creating dummy course
			CAReader1 ca = new CAReader1();
			ca.setExternal_course_id("1");
			ca.setTeaching_unit("1");
			ca.setThreshold(2.0);
			ca.setDescription("physics");

			final Response resp = target(CREATE_COURSE_1_URL).request()
					.header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ca), Response.class);

			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			final List<String> resList = target(GETTASKS_1_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "5").request().get(List.class);

		} catch (WebApplicationException e) {
			assertEquals("no tasks and no primary analyzer present for course", Status.NOT_FOUND.getStatusCode(),
					e.getResponse().getStatus());
		} catch (Exception e) {
			System.out.println("Error in testing");
			e.printStackTrace();
		} finally {
			Handler.hqlTruncate("C_A1");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
			Utilities.setJUnitTest(false);
		}

	}

	@Test
	public void noPrimaryAnalyzer_tasksLessThanRequestedTasks() {
		Utilities.setJUnitTest(true);
		try {
			CAReader1 ca = new CAReader1();
			ca.setExternal_course_id("1");
			ca.setTeaching_unit("1");
			ca.setThreshold(2.0);
			ca.setDescription("physics");
			Response resp = target(CREATE_COURSE_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			TAReader1 ta = new TAReader1();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_difficulty_level(1);
			resp = target(CREATE_TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			ta = new TAReader1();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("2");
			ta.setS_difficulty_level(2);
			resp = target(CREATE_TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			ta = new TAReader1();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("3");
			ta.setS_difficulty_level(3);
			resp = target(CREATE_TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			final List<String> resList = target(GETTASKS_1_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "5").request().get(List.class);

			assertEquals("only 3 tasks present for course", 3, resList.size());
		} finally {
			Handler.hqlTruncate("T_A1");
			Handler.hqlTruncate("Task");
			Handler.hqlTruncate("C_A1");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
			Utilities.setJUnitTest(false);
		}

	}

	@Test
	public void noPrimaryAnalyzer_tasksMoreThanRequestedTasks() {
		Utilities.setJUnitTest(true);
		try {
			CAReader1 ca = new CAReader1();
			ca.setExternal_course_id("1");
			ca.setTeaching_unit("1");
			ca.setThreshold(2.0);
			ca.setDescription("physics");
			Response resp = target(CREATE_COURSE_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			TAReader1 ta = new TAReader1();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_difficulty_level(1);
			resp = target(CREATE_TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			ta = new TAReader1();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("2");
			ta.setS_difficulty_level(2);
			resp = target(CREATE_TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			ta = new TAReader1();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("3");
			ta.setS_difficulty_level(3);
			resp = target(CREATE_TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader1();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("4");
			ta.setS_difficulty_level(4);
			resp = target(CREATE_TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader1();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("5");
			ta.setS_difficulty_level(5);
			resp = target(CREATE_TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader1();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("6");
			ta.setS_difficulty_level(6);
			resp = target(CREATE_TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			

			final List<String> resList = target(GETTASKS_1_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "5").request().get(List.class);

			assertEquals("5 tasks returned for course", 5, resList.size());
		} finally {
			Handler.hqlTruncate("T_A1");
			Handler.hqlTruncate("Task");
			Handler.hqlTruncate("C_A1");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
			Utilities.setJUnitTest(false);
		}

	}
/*
	@Test
	public void primaryAnalyzer_noStudentTasks_tasksMoreThanRequestedTasks() {
		Utilities.setJUnitTest(true);
		try {
			
			//create course
			CAReader1 ca = new CAReader1();
			ca.setExternal_course_id("1");
			ca.setTeaching_unit("1");
			ca.setThreshold(2.0);
			ca.setDescription("physics");
			Response resp = target(CREATE_COURSE_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			//create course analyzer map
			resp = target(SET_ANALYZER_COURSE).queryParam("external_course_id", "1").queryParam("analyzer_id", "1").queryParam("active", true).request().get(Response.class);
			assertEquals("course analyzer map", Status.OK.getStatusCode(),resp.getStatus());
			
			//create student
			SAReader1 sa = new SAReader1();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			sa.setS_placement_score(3.0);
			sa.setS_year("Senior");
			resp =  target(CREATE_STUDENT_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sa),Response.class);
			assertEquals("student created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			//create task
			TAReader1 ta = new TAReader1();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_difficulty_level(1);
			resp = target(CREATE_TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			ta = new TAReader1();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("2");
			ta.setS_difficulty_level(2);
			resp = target(CREATE_TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			ta = new TAReader1();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("3");
			ta.setS_difficulty_level(3);
			resp = target(CREATE_TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader1();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("4");
			ta.setS_difficulty_level(4);
			resp = target(CREATE_TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader1();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("5");
			ta.setS_difficulty_level(5);
			resp = target(CREATE_TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader1();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("6");
			ta.setS_difficulty_level(6);
			resp = target(CREATE_TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			

			final List<String> resList = target(GETTASKS_1_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "5").request().get(List.class);

			assertEquals("5 tasks returned for course", 5, resList.size());
		} finally {
			Handler.hqlTruncate("CourseAnalyzerMap");
			Handler.hqlTruncate("T_A1");
			Handler.hqlTruncate("Task");
			Handler.hqlTruncate("S_A1");
			Handler.hqlTruncate("Student");
			Handler.hqlTruncate("C_A1");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
			Utilities.setJUnitTest(false);
		}

	}
*/
}
