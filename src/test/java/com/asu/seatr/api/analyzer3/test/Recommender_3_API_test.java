package com.asu.seatr.api.analyzer3.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;


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

import com.asu.seatr.api.AdminAPI;
import com.asu.seatr.api.CommonAPI;
import com.asu.seatr.api.analyzer3.CourseAPI_3;
import com.asu.seatr.api.analyzer3.RecommenderAPI_3;
import com.asu.seatr.api.analyzer3.StudentAPI_3;
import com.asu.seatr.api.analyzer3.StudentTaskAPI_3;
import com.asu.seatr.api.analyzer3.TaskAPI_3;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.rest.models.analyzer1.CAReader1;
import com.asu.seatr.rest.models.analyzer1.STAReader1;
import com.asu.seatr.rest.models.analyzer3.CAReader3;
import com.asu.seatr.rest.models.analyzer3.SAReader3;
import com.asu.seatr.rest.models.analyzer3.STAReader3;
import com.asu.seatr.rest.models.analyzer3.TAReader3;
import com.asu.seatr.utils.Utilities;

public class Recommender_3_API_test extends JerseyTest{

	private static String GETTASKS_3_URL = "analyzer/3/gettasks/";
	private static String CREATE_COURSE_3_URL = "analyzer/3/courses/";
	private static String CREATE_TASK_3_URL = "analyzer/3/tasks/";
	private static String SET_ANALYZER_COURSE = "courses/setanalyzer/";
	private static String CREATE_STUDENT_3_URL = "analyzer/3/students/";
	private static String INIT_TASKS = "inittasks/";
	private static String STUDENT_TASK_URL = "analyzer/3/studenttasks";
	
	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_3.class, RecommenderAPI_3.class, AdminAPI.class, TaskAPI_3.class, CommonAPI.class, StudentAPI_3.class,
				StudentTaskAPI_3.class);
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
			target(GETTASKS_3_URL).queryParam("external_student_id", "1")
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
			final List<String> resList = target(GETTASKS_3_URL).queryParam("external_student_id", "")
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
			final List<String> resList = target(GETTASKS_3_URL).queryParam("external_student_id", "1")
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
			CAReader3 ca = new CAReader3();
			ca.setExternal_course_id("1");
			ca.setS_units(1);
			ca.setD_current_unit_no(1);
			ca.setD_max_n(4);
			ca.setDescription("chemistry");
			ca.setDescription("physics");
			Response resp = target(CREATE_COURSE_3_URL).request()
					.header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			SAReader3 sa = new SAReader3();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			sa.setS_placement_score(20.0);
			sa.setS_year("Grad");
			resp =  target(CREATE_STUDENT_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sa),Response.class);
			assertEquals("student created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			final List<String> resList = target(GETTASKS_3_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "5").request().get(List.class);
			assertEquals("empty tasks", 0,resList.size());

		}  finally {
			Handler.hqlTruncate("S_A3");
			Handler.hqlTruncate("Student");
			Handler.hqlTruncate("C_A3");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
			Utilities.setJUnitTest(false);
		}

	}
	@Test
	public void allNewTasks() {

		Utilities.setJUnitTest(true);
		try {

			/*
			 * UserReader ur = new UserReader(); ur.setUsername("cse310");
			 * ur.setPassword("hello123"); Response rb =
			 * target("superadmin/users").request().post(Entity.json(ur),
			 * Response.class);
			 */
			// first creating dummy course
			CAReader3 ca = new CAReader3();
			ca.setExternal_course_id("1");
			ca.setS_units(1);
			ca.setD_current_unit_no(1);
			ca.setD_max_n(2);
			ca.setDescription("chemistry");
			ca.setDescription("physics");
			Response resp = target(CREATE_COURSE_3_URL).request()
					.header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			SAReader3 sa = new SAReader3();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			sa.setS_placement_score(20.0);
			sa.setS_year("Grad");
			resp =  target(CREATE_STUDENT_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sa),Response.class);
			assertEquals("student created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			TAReader3 ta = new TAReader3();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_is_required(true);
			ta.setS_sequence_no(2);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader3();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("2");
			ta.setS_is_required(true);
			ta.setS_sequence_no(1);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader3();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("3");
			ta.setS_is_required(true);
			ta.setS_sequence_no(4);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader3();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("4");
			ta.setS_is_required(true);
			ta.setS_sequence_no(3);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader3();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("5");
			ta.setS_is_required(true);
			ta.setS_sequence_no(5);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader3();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("6");
			ta.setS_is_required(true);
			ta.setS_sequence_no(5);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader3();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("7");
			ta.setS_is_required(true);
			ta.setS_sequence_no(5);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader3();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("8");
			ta.setS_is_required(true);
			ta.setS_sequence_no(8);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader3();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("9");
			ta.setS_is_required(true);
			ta.setS_sequence_no(7);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader3();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("10");
			ta.setS_is_required(true);
			ta.setS_sequence_no(6);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader3();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("11");
			ta.setS_is_required(true);
			ta.setS_sequence_no(2);
			ta.setS_unit_no(2);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			ta = new TAReader3();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("12");
			ta.setS_is_required(false);
			ta.setS_sequence_no(2);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			
			ta = new TAReader3();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("13");
			ta.setS_is_required(false);
			ta.setS_sequence_no(2);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader3();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("14");
			ta.setS_is_required(false);
			ta.setS_sequence_no(2);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			
			
			List<String> resList = target(GETTASKS_3_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "11").request().get(List.class);
			assertEquals("empyt list returned", 10,resList.size());
			
			assertEquals("first task","2",resList.get(0));
			assertEquals("second task","1",resList.get(1));
			assertEquals("third task","4",resList.get(2));
			assertEquals("fourth task","3",resList.get(3));
			assertTrue("fifth task", resList.get(4).equals("5") || resList.get(4).equals("6") || resList.get(4).equals("7"));
			assertTrue("sixth task", resList.get(5).equals("5") || resList.get(5).equals("6") || resList.get(5).equals("7"));
			assertTrue("seventh task", resList.get(6).equals("5") || resList.get(6).equals("6") || resList.get(6).equals("7"));
			assertEquals("eights task","10",resList.get(7));
			assertEquals("ninth task","9",resList.get(8));
			assertEquals("tenth task","8",resList.get(9));
			//eleventh task will not be returned since it belongs to unit 2
			
			
			//now lets answer some tasks
			STAReader3 sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("1");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());

			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("4");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("5");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("6");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("7");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("8");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("9");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("10");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("10");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("10");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			resList = target(GETTASKS_3_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "11").request().get(List.class);
			assertEquals("empyt list returned", 8,resList.size());
			
			assertEquals("first task","2",resList.get(0));
			assertEquals("second task","3",resList.get(1));
			assertEquals("third task","1",resList.get(2));
			assertEquals("fourth task","4",resList.get(3));
			assertTrue("fiffth task", resList.get(4).equals("6") || resList.get(4).equals("7"));
			assertTrue("sixth task", resList.get(5).equals("6") || resList.get(5).equals("7"));
			assertEquals("seventh task","9",resList.get(6));
			assertEquals("eighth task","8",resList.get(7));
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("2");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("2");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("3");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("3");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("3");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("1");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("4");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("6");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("7");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("8");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("9");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			resList = target(GETTASKS_3_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "11").request().get(List.class);
			assertEquals("empyt list returned", 1,resList.size());
			
			assertEquals("first task","2",resList.get(0));
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("2");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			//now we should get optional tasks
			resList = target(GETTASKS_3_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "11").request().get(List.class);
			assertEquals("number of tasks",3,resList.size());
			assertTrue("first task", resList.get(0).equals("12") || resList.get(0).equals("13") || resList.get(0).equals("14"));
			assertTrue("second task", resList.get(1).equals("12") || resList.get(1).equals("13") || resList.get(1).equals("14"));
			assertTrue("third task", resList.get(2).equals("12") || resList.get(2).equals("13") || resList.get(2).equals("14"));
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("12");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("14");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("13");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());

			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("13");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());

			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("13");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());

			resList = target(GETTASKS_3_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "11").request().get(List.class);
			assertEquals("number of tasks",1,resList.size());
			assertTrue("first task", resList.get(0).equals("13"));
			
			sta = new STAReader3();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("13");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			resList = target(GETTASKS_3_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "11").request().get(List.class);
			assertEquals("number of tasks",0,resList.size());
			
			
		}  finally {
			Handler.hqlTruncate("ST_A3");
			Handler.hqlTruncate("StudentTask");
			Handler.hqlTruncate("S_A3");
			Handler.hqlTruncate("Student");
			Handler.hqlTruncate("T_A3");
			Handler.hqlTruncate("Task");
			Handler.hqlTruncate("C_A3");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
			Utilities.setJUnitTest(false);
		}

	}
}
