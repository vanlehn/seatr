package com.asu.seatr.api.analyzer1.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.asu.seatr.api.AdminAPI;
import com.asu.seatr.api.CommonAPI;
import com.asu.seatr.api.RecommenderAPI;
import com.asu.seatr.api.analyzer1.CourseAPI_1;
import com.asu.seatr.api.analyzer1.RecommenderAPI_1;
import com.asu.seatr.api.analyzer1.StudentAPI_1;
import com.asu.seatr.api.analyzer1.StudentTaskAPI_1;
import com.asu.seatr.api.analyzer1.TaskAPI_1;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.models.StudentTask;
import com.asu.seatr.models.analyzers.studenttask.RecommTask_A1;
import com.asu.seatr.models.analyzers.studenttask.ST_A1;
import com.asu.seatr.rest.models.analyzer1.CAReader1;
import com.asu.seatr.rest.models.analyzer1.SAReader1;
import com.asu.seatr.rest.models.analyzer1.STAReader1;
import com.asu.seatr.rest.models.analyzer1.TAReader1;
import com.asu.seatr.utils.Utilities;

public class Recommender_1_API_Test extends JerseyTest {
	
	private static String GETTASKS_1_URL = "analyzer/1/gettasks/";
	private static String CREATE_COURSE_1_URL = "analyzer/1/courses/";
	private static String CREATE_TASK_1_URL = "analyzer/1/tasks/";
	private static String SET_ANALYZER_COURSE = "courses/setanalyzer/";
	private static String CREATE_STUDENT_1_URL = "analyzer/1/students/";
	private static String INIT_TASKS = "inittasks/";
	private static String STUDENT_TASK_URL = "analyzer/1/studenttasks";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_1.class, RecommenderAPI_1.class, AdminAPI.class, TaskAPI_1.class, CommonAPI.class, StudentAPI_1.class,RecommenderAPI.class,
				StudentTaskAPI_1.class);
	}

	@Before
	public void init()
	{
		System.out.println("initialization started.....");
		Utilities.setJUnitTest(true);
		Utilities.clearDatabase();

		Utilities.setJUnitTest(false);
	}
	@Test(expected=WebApplicationException.class)
	public void emptyCourseId() {
		Utilities.setJUnitTest(true);
		try {
			target(GETTASKS_1_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "").queryParam("number_of_tasks", "5").request().get(List.class);
		} catch (WebApplicationException e) {

			assertEquals("empty course id must return 400", Status.BAD_REQUEST.getStatusCode(),
					e.getResponse().getStatus());
			throw e;
		} catch (Exception e) {
			System.out.println("Error in testing");
			e.printStackTrace();
		} finally {
			Utilities.setJUnitTest(false);
		}
	}

	@Test(expected=WebApplicationException.class)
	public void emptyStudentId() {
		Utilities.setJUnitTest(true);
		try {
			final List<String> resList = target(GETTASKS_1_URL).queryParam("external_student_id", "")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "5").request().get(List.class);
		} catch (WebApplicationException e) {

			assertEquals("empty student id must return 400", Status.BAD_REQUEST.getStatusCode(),
					e.getResponse().getStatus());
			throw e;
		} catch (Exception e) {
			System.out.println("Error in testing");
			e.printStackTrace();
		} finally {
			Utilities.setJUnitTest(false);
		}
	}

	@Test(expected=WebApplicationException.class)
	public void invalidCoureId() {

		Utilities.setJUnitTest(true);
		try {
			final List<String> resList = target(GETTASKS_1_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "5").request().get(List.class);
		} catch (WebApplicationException e) {

			assertEquals("course not found must return 400", Status.BAD_REQUEST.getStatusCode(),
					e.getResponse().getStatus());
			throw e;
		} catch (Exception e) {
			System.out.println("Error in testing");
			e.printStackTrace();
		} finally {
			Utilities.setJUnitTest(false);
		}

	}

	@Test(expected=WebApplicationException.class)
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
			throw e;
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
			
			
			resp = target(INIT_TASKS).queryParam("number_of_tasks", 10).request().get(Response.class);
			assertEquals("recomm task table initialized", Status.OK.getStatusCode(),resp.getStatus());
			
			
			final List<String> resList = target(GETTASKS_1_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "5").request().get(List.class);

			assertEquals("5 tasks returned for course", 5, resList.size());
		} finally {
			Handler.hqlTruncate("RecommTask_A1");
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
	
	//creating 6 tasks, then student answers 2 tasks. 1 task is answered twice. still we should get 4 recommended tasks
	@Test
	public void primaryAnalyzer_StudentTasks_taskslessThanRequestedTasks() {
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
			
			resp = target(INIT_TASKS).queryParam("number_of_tasks", 10).request().get(Response.class);
			assertEquals("recomm task table initialized", Status.OK.getStatusCode(),resp.getStatus());
			
			//creating 3 student tasks but for only 2 tasks
			STAReader1 sta;
			sta = new STAReader1();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("1");
			sta.setD_status("done");
			sta.setD_time_lastattempt(127863);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());

			sta = new STAReader1();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("2");
			sta.setD_status("done");
			sta.setD_time_lastattempt(127863);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader1();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("2");
			sta.setD_status("done");
			sta.setD_time_lastattempt(127863);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			

			
			
			final List<String> resList = target(GETTASKS_1_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "5").request().get(List.class);

			assertEquals("4 tasks returned for course", 4, resList.size());
		} finally {
			Handler.hqlTruncate("RecommTask_A1");
			Handler.hqlTruncate("CourseAnalyzerMap");
			Handler.hqlTruncate("ST_A1");
			Handler.hqlTruncate("StudentTask");
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
	
	//creating 6 tasks, then student answers 6 tasks. since all tasks answered no tasks left
	@Test(expected=WebApplicationException.class)
	public void primaryAnalyzer_StudentTasks_noTasksLeft() {
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
			
			resp = target(INIT_TASKS).queryParam("number_of_tasks", 10).request().get(Response.class);
			assertEquals("recomm task table initialized", Status.OK.getStatusCode(),resp.getStatus());
			
			//creating 6 student tasks but for only 2 tasks
			STAReader1 sta;
			sta = new STAReader1();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("1");
			sta.setD_status("done");
			sta.setD_time_lastattempt(127863);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());

			sta = new STAReader1();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("2");
			sta.setD_status("done");
			sta.setD_time_lastattempt(127863);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader1();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("3");
			sta.setD_status("done");
			sta.setD_time_lastattempt(127863);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader1();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("4");
			sta.setD_status("done");
			sta.setD_time_lastattempt(127863);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader1();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("5");
			sta.setD_status("done");
			sta.setD_time_lastattempt(127863);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader1();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("6");
			sta.setD_status("done");
			sta.setD_time_lastattempt(127863);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			

			
			
			final List<String> resList = target(GETTASKS_1_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "5").request().get(List.class);

			assertEquals("0 tasks returned for course", 0, resList.size());
			

		} 
		catch (WebApplicationException e) {
			assertEquals("no tasks and no primary analyzer present for course", Status.NOT_FOUND.getStatusCode(),
					e.getResponse().getStatus());
			throw e;
		}
		finally {
			Handler.hqlTruncate("RecommTask_A1");
			Handler.hqlTruncate("CourseAnalyzerMap");
			Handler.hqlTruncate("ST_A1");
			Handler.hqlTruncate("StudentTask");
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
	

}
