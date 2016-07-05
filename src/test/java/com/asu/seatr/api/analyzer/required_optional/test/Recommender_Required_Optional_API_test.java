package com.asu.seatr.api.analyzer.required_optional.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

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
import com.asu.seatr.api.analyzer.required_optional.CourseAPI_Required_Optional;
import com.asu.seatr.api.analyzer.required_optional.RecommenderAPI_Required_Optional;
import com.asu.seatr.api.analyzer.required_optional.StudentAPI_Required_Optional;
import com.asu.seatr.api.analyzer.required_optional.StudentTaskAPI_Required_Optional;
import com.asu.seatr.api.analyzer.required_optional.TaskAPI_Required_Optional;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.rest.models.analyzer.required_optional.CAReader_Required_Optional;
import com.asu.seatr.rest.models.analyzer.required_optional.SAReader_Required_Optional;
import com.asu.seatr.rest.models.analyzer.required_optional.STAReader_Required_Optional;
import com.asu.seatr.rest.models.analyzer.required_optional.TAReader_Required_Optional;
import com.asu.seatr.rest.models.analyzer.unansweredtasks.CAReader_UnansweredTasks;
import com.asu.seatr.rest.models.analyzer.unansweredtasks.STAReader_UnansweredTasks;
import com.asu.seatr.utils.Utilities;

public class Recommender_Required_Optional_API_test extends JerseyTest{

	private static String GETTASKS_3_URL = "analyzer/required_optional/gettasks/";
	private static String CREATE_COURSE_3_URL = "analyzer/required_optional/courses/";
	private static String CREATE_TASK_3_URL = "analyzer/required_optional/tasks/";
	private static String SET_ANALYZER_COURSE = "courses/setanalyzer/";
	private static String CREATE_STUDENT_3_URL = "analyzer/required_optional/students/";
	private static String INIT_TASKS = "inittasks/";
	private static String STUDENT_TASK_URL = "analyzer/required_optional/studenttasks";
	private static String SCALE_TASKS = "analyzer/required_optional/scaletasks";
	
	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_Required_Optional.class, RecommenderAPI_Required_Optional.class, AdminAPI.class, TaskAPI_Required_Optional.class, CommonAPI.class, StudentAPI_Required_Optional.class,
				StudentTaskAPI_Required_Optional.class);
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
	public void emptyCourseIdGetTasks() {
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
	public void emptyStudentIdGetTasks() {
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
	public void invalidCoureIdGetTasks() {

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
	public void noTasksForCourseGetTasks() {

		Utilities.setJUnitTest(true);
		try {

			
			 // UserReader ur = new UserReader(); ur.setUsername("cse310");
			 //ur.setPassword("hello123"); Response rb =
			 //target("superadmin/users").request().post(Entity.json(ur),
			 //Response.class);
			 
			// first creating dummy course
			CAReader_Required_Optional ca = new CAReader_Required_Optional();
			ca.setExternal_course_id("1");
			ca.setS_units(1);
			ca.setD_current_unit_no(1);
			ca.setD_max_n(4);
			ca.setDescription("chemistry");
			ca.setDescription("physics");
			Response resp = target(CREATE_COURSE_3_URL).request()
					.header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			SAReader_Required_Optional sa = new SAReader_Required_Optional();
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
			Handler.hqlTruncate("Student_Required_Optional");
			Handler.hqlTruncate("Student");
			Handler.hqlTruncate("Course_Required_Optional");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
			Utilities.setJUnitTest(false);
		}

	}
	@Test
	public void allTestsGetTasks() {

		Utilities.setJUnitTest(true);
		try {

			
			  //UserReader ur = new UserReader(); ur.setUsername("cse310");
			  //ur.setPassword("hello123"); Response rb =
			  //target("superadmin/users").request().post(Entity.json(ur),
			 // Response.class);
			 
			// first creating dummy course
			CAReader_Required_Optional ca = new CAReader_Required_Optional();
			ca.setExternal_course_id("1");
			ca.setS_units(1);
			ca.setD_current_unit_no(1);
			ca.setD_max_n(2);
			ca.setDescription("chemistry");
			ca.setDescription("physics");
			Response resp = target(CREATE_COURSE_3_URL).request()
					.header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			SAReader_Required_Optional sa = new SAReader_Required_Optional();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			sa.setS_placement_score(20.0);
			sa.setS_year("Grad");
			resp =  target(CREATE_STUDENT_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sa),Response.class);
			assertEquals("student created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			TAReader_Required_Optional ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_is_required(true);
			ta.setS_sequence_no(2);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("2");
			ta.setS_is_required(true);
			ta.setS_sequence_no(1);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("3");
			ta.setS_is_required(true);
			ta.setS_sequence_no(4);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("4");
			ta.setS_is_required(true);
			ta.setS_sequence_no(3);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("5");
			ta.setS_is_required(true);
			ta.setS_sequence_no(5);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("6");
			ta.setS_is_required(true);
			ta.setS_sequence_no(5);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("7");
			ta.setS_is_required(true);
			ta.setS_sequence_no(5);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("8");
			ta.setS_is_required(true);
			ta.setS_sequence_no(8);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("9");
			ta.setS_is_required(true);
			ta.setS_sequence_no(7);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("10");
			ta.setS_is_required(true);
			ta.setS_sequence_no(6);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("11");
			ta.setS_is_required(true);
			ta.setS_sequence_no(2);
			ta.setS_unit_no(2);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("12");
			ta.setS_is_required(false);
			ta.setS_sequence_no(2);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("13");
			ta.setS_is_required(false);
			ta.setS_sequence_no(2);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
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
			STAReader_Required_Optional sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("1");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());

			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("4");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("5");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("6");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("7");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("8");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("9");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("10");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("10");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
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
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("2");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("2");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("3");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("3");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("3");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("1");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("4");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("6");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("7");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("8");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
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
			
			sta = new STAReader_Required_Optional();
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
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("12");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("14");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("13");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());

			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("13");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());

			sta = new STAReader_Required_Optional();
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
			
			sta = new STAReader_Required_Optional();
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
			Handler.hqlTruncate("StudentTask_Required_Optional");
			Handler.hqlTruncate("StudentTask");
			Handler.hqlTruncate("Student_Required_Optional");
			Handler.hqlTruncate("Student");
			Handler.hqlTruncate("Task_Required_Optional");
			Handler.hqlTruncate("Task");
			Handler.hqlTruncate("Course_Required_Optional");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
			Utilities.setJUnitTest(false);
		}

	}
	@Test(expected=WebApplicationException.class)
	public void emptyCourseIdScaleTasks() {
		Utilities.setJUnitTest(true);
		try {
			target(SCALE_TASKS).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "").queryParam("tasks_list", "1").request().get(Map.class);
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
	public void emptyStudentIdScaleTasks() {
		Utilities.setJUnitTest(true);
		try {
			target(SCALE_TASKS).queryParam("external_student_id", "")
					.queryParam("external_course_id", "1").queryParam("tasks_list", "1").request().get(Map.class);
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
	public void invalidCoureIdScaleTasks() {

		Utilities.setJUnitTest(true);
		try {
			target(SCALE_TASKS).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("tasks_list", "1").request().get(Map.class);
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
	public void noTasksForCourseScaleTasks() {

		Utilities.setJUnitTest(true);
		try {

			
			 // UserReader ur = new UserReader(); ur.setUsername("cse310");
			 //ur.setPassword("hello123"); Response rb =
			 //target("superadmin/users").request().post(Entity.json(ur),
			 //Response.class);
			 
			// first creating dummy course
			CAReader_Required_Optional ca = new CAReader_Required_Optional();
			ca.setExternal_course_id("1");
			ca.setS_units(1);
			ca.setD_current_unit_no(1);
			ca.setD_max_n(4);
			ca.setDescription("chemistry");
			ca.setDescription("physics");
			Response resp = target(CREATE_COURSE_3_URL).request()
					.header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			SAReader_Required_Optional sa = new SAReader_Required_Optional();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			sa.setS_placement_score(20.0);
			sa.setS_year("Grad");
			resp =  target(CREATE_STUDENT_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sa),Response.class);
			assertEquals("student created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			target(SCALE_TASKS).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("tasks_list", "1").request().get(Map.class);


		}
		catch (WebApplicationException e) {

			assertEquals("tasks not found must return 400", Status.BAD_REQUEST.getStatusCode(),
					e.getResponse().getStatus());
			throw e;
		} catch (Exception e) {
			System.out.println("Error in testing");
			e.printStackTrace();
		}finally {
			Handler.hqlTruncate("Student_Required_Optional");
			Handler.hqlTruncate("Student");
			Handler.hqlTruncate("Course_Required_Optional");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
			Utilities.setJUnitTest(false);
		}

	}
	
	@Test
	public void allTestsScaleTasks() {

		Utilities.setJUnitTest(true);
		try {

			
			  //UserReader ur = new UserReader(); ur.setUsername("cse310");
			  //ur.setPassword("hello123"); Response rb =
			  //target("superadmin/users").request().post(Entity.json(ur),
			 // Response.class);
			 
			// first creating dummy course
			CAReader_Required_Optional ca = new CAReader_Required_Optional();
			ca.setExternal_course_id("1");
			ca.setS_units(1);
			ca.setD_current_unit_no(1);
			ca.setD_max_n(2);
			ca.setDescription("chemistry");
			ca.setDescription("physics");
			Response resp = target(CREATE_COURSE_3_URL).request()
					.header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			SAReader_Required_Optional sa = new SAReader_Required_Optional();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			sa.setS_placement_score(20.0);
			sa.setS_year("Grad");
			resp =  target(CREATE_STUDENT_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sa),Response.class);
			assertEquals("student created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			TAReader_Required_Optional ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_is_required(true);
			ta.setS_sequence_no(2);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("2");
			ta.setS_is_required(true);
			ta.setS_sequence_no(1);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("3");
			ta.setS_is_required(true);
			ta.setS_sequence_no(4);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("4");
			ta.setS_is_required(true);
			ta.setS_sequence_no(3);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("5");
			ta.setS_is_required(true);
			ta.setS_sequence_no(5);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("6");
			ta.setS_is_required(true);
			ta.setS_sequence_no(5);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("7");
			ta.setS_is_required(true);
			ta.setS_sequence_no(5);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("8");
			ta.setS_is_required(true);
			ta.setS_sequence_no(8);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("9");
			ta.setS_is_required(true);
			ta.setS_sequence_no(7);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("10");
			ta.setS_is_required(true);
			ta.setS_sequence_no(6);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("11");
			ta.setS_is_required(true);
			ta.setS_sequence_no(2);
			ta.setS_unit_no(2);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("12");
			ta.setS_is_required(false);
			ta.setS_sequence_no(2);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("13");
			ta.setS_is_required(false);
			ta.setS_sequence_no(2);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("14");
			ta.setS_is_required(false);
			ta.setS_sequence_no(2);
			ta.setS_unit_no(1);
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			
			
			Map<String,Integer> resMap = target(SCALE_TASKS).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("tasks_list", "1").queryParam("tasks_list", "2")
					.queryParam("tasks_list", "3").queryParam("tasks_list", "4").queryParam("tasks_list", "5")
					.queryParam("tasks_list", "6").queryParam("tasks_list", "7").queryParam("tasks_list", "8")
					.queryParam("tasks_list", "9").queryParam("tasks_list", "10").queryParam("tasks_list", "11")
					.queryParam("tasks_list", "12").queryParam("tasks_list", "14").request().get(Map.class);
			assertEquals("task scaling", new Long(0), resMap.get("1"));
			assertEquals("task scaling", new Long(1), resMap.get("2"));
			assertEquals("task scaling", new Long(0), resMap.get("3"));
			assertEquals("task scaling", new Long(0), resMap.get("4"));
			assertEquals("task scaling", new Long(0), resMap.get("5"));
			assertEquals("task scaling", new Long(0), resMap.get("6"));
			assertEquals("task scaling", new Long(0), resMap.get("7"));
			assertEquals("task scaling", new Long(0), resMap.get("8"));
			assertEquals("task scaling", new Long(0), resMap.get("9"));
			assertEquals("task scaling", new Long(0), resMap.get("10"));
			assertEquals("task scaling", new Long(0), resMap.get("11"));
			assertEquals("task scaling", new Long(0), resMap.get("12"));
			assertEquals("task scaling", new Long(0), resMap.get("14"));
			
			//now lets answer some tasks
			STAReader_Required_Optional sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("1");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("1");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("1");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			//now lets answer some tasks
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("2");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			resMap = target(SCALE_TASKS).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("tasks_list", "1").queryParam("tasks_list", "2")
					.queryParam("tasks_list", "3").queryParam("tasks_list", "4").queryParam("tasks_list", "5")
					.queryParam("tasks_list", "6").queryParam("tasks_list", "7").queryParam("tasks_list", "8")
					.queryParam("tasks_list", "9").queryParam("tasks_list", "10").queryParam("tasks_list", "11")
					.queryParam("tasks_list", "12").queryParam("tasks_list", "14").request().get(Map.class);
			assertEquals("task scaling", new Long(0), resMap.get("1"));
			assertEquals("task scaling", new Long(0), resMap.get("2"));
			assertEquals("task scaling", new Long(0), resMap.get("3"));
			assertEquals("task scaling", new Long(1), resMap.get("4"));
			assertEquals("task scaling", new Long(0), resMap.get("5"));
			assertEquals("task scaling", new Long(0), resMap.get("6"));
			assertEquals("task scaling", new Long(0), resMap.get("7"));
			assertEquals("task scaling", new Long(0), resMap.get("8"));
			assertEquals("task scaling", new Long(0), resMap.get("9"));
			assertEquals("task scaling", new Long(0), resMap.get("10"));
			assertEquals("task scaling", new Long(0), resMap.get("11"));
			assertEquals("task scaling", new Long(0), resMap.get("12"));
			assertEquals("task scaling", new Long(0), resMap.get("14"));
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("3");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("4");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			resMap = target(SCALE_TASKS).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("tasks_list", "1").queryParam("tasks_list", "2")
					.queryParam("tasks_list", "3").queryParam("tasks_list", "4").queryParam("tasks_list", "5")
					.queryParam("tasks_list", "6").queryParam("tasks_list", "7").queryParam("tasks_list", "8")
					.queryParam("tasks_list", "9").queryParam("tasks_list", "10").queryParam("tasks_list", "11")
					.queryParam("tasks_list", "12").queryParam("tasks_list", "14").request().get(Map.class);
			assertEquals("task scaling", new Long(0), resMap.get("1"));
			assertEquals("task scaling", new Long(0), resMap.get("2"));
			assertEquals("task scaling", new Long(0), resMap.get("3"));
			assertEquals("task scaling", new Long(0), resMap.get("4"));
			assertEquals("task scaling", new Long(1), resMap.get("5"));
			assertEquals("task scaling", new Long(1), resMap.get("6"));
			assertEquals("task scaling", new Long(1), resMap.get("7"));
			assertEquals("task scaling", new Long(0), resMap.get("8"));
			assertEquals("task scaling", new Long(0), resMap.get("9"));
			assertEquals("task scaling", new Long(0), resMap.get("10"));
			assertEquals("task scaling", new Long(0), resMap.get("11"));
			assertEquals("task scaling", new Long(0), resMap.get("12"));
			assertEquals("task scaling", new Long(0), resMap.get("14"));
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("5");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("6");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("7");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("8");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("9");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("10");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("11");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			resMap = target(SCALE_TASKS).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("tasks_list", "1").queryParam("tasks_list", "2")
					.queryParam("tasks_list", "3").queryParam("tasks_list", "4").queryParam("tasks_list", "5")
					.queryParam("tasks_list", "6").queryParam("tasks_list", "7").queryParam("tasks_list", "8")
					.queryParam("tasks_list", "9").queryParam("tasks_list", "10").queryParam("tasks_list", "11")
					.queryParam("tasks_list", "12").queryParam("tasks_list", "13").queryParam("tasks_list", "14").request().get(Map.class);
			assertEquals("task scaling", new Long(0), resMap.get("1"));
			assertEquals("task scaling", new Long(1), resMap.get("2"));
			assertEquals("task scaling", new Long(0), resMap.get("3"));
			assertEquals("task scaling", new Long(0), resMap.get("4"));
			assertEquals("task scaling", new Long(0), resMap.get("5"));
			assertEquals("task scaling", new Long(0), resMap.get("6"));
			assertEquals("task scaling", new Long(0), resMap.get("7"));
			assertEquals("task scaling", new Long(0), resMap.get("8"));
			assertEquals("task scaling", new Long(0), resMap.get("9"));
			assertEquals("task scaling", new Long(0), resMap.get("10"));
			assertEquals("task scaling", new Long(0), resMap.get("11"));
			assertEquals("task scaling", new Long(0), resMap.get("12"));
			assertEquals("task scaling", new Long(0), resMap.get("13"));
			assertEquals("task scaling", new Long(0), resMap.get("14"));
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("2");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			resMap = target(SCALE_TASKS).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("tasks_list", "1").queryParam("tasks_list", "2")
					.queryParam("tasks_list", "3").queryParam("tasks_list", "4").queryParam("tasks_list", "5")
					.queryParam("tasks_list", "6").queryParam("tasks_list", "7").queryParam("tasks_list", "8")
					.queryParam("tasks_list", "9").queryParam("tasks_list", "10").queryParam("tasks_list", "11")
					.queryParam("tasks_list", "12").queryParam("tasks_list", "13").queryParam("tasks_list", "14").request().get(Map.class);
			assertEquals("task scaling", new Long(0), resMap.get("1"));
			assertEquals("task scaling", new Long(0), resMap.get("2"));
			assertEquals("task scaling", new Long(1), resMap.get("3"));
			assertEquals("task scaling", new Long(0), resMap.get("4"));
			assertEquals("task scaling", new Long(0), resMap.get("5"));
			assertEquals("task scaling", new Long(0), resMap.get("6"));
			assertEquals("task scaling", new Long(0), resMap.get("7"));
			assertEquals("task scaling", new Long(0), resMap.get("8"));
			assertEquals("task scaling", new Long(0), resMap.get("9"));
			assertEquals("task scaling", new Long(0), resMap.get("10"));
			assertEquals("task scaling", new Long(0), resMap.get("11"));
			assertEquals("task scaling", new Long(0), resMap.get("12"));
			assertEquals("task scaling", new Long(0), resMap.get("13"));
			assertEquals("task scaling", new Long(0), resMap.get("14"));
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("3");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			resMap = target(SCALE_TASKS).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("tasks_list", "1").queryParam("tasks_list", "2")
					.queryParam("tasks_list", "3").queryParam("tasks_list", "4").queryParam("tasks_list", "5")
					.queryParam("tasks_list", "6").queryParam("tasks_list", "7").queryParam("tasks_list", "8")
					.queryParam("tasks_list", "9").queryParam("tasks_list", "10").queryParam("tasks_list", "11")
					.queryParam("tasks_list", "12").queryParam("tasks_list", "13").queryParam("tasks_list", "14").request().get(Map.class);
			assertEquals("task scaling", new Long(0), resMap.get("1"));
			assertEquals("task scaling", new Long(0), resMap.get("2"));
			assertEquals("task scaling", new Long(0), resMap.get("3"));
			assertEquals("task scaling", new Long(0), resMap.get("4"));
			assertEquals("task scaling", new Long(0), resMap.get("5"));
			assertEquals("task scaling", new Long(0), resMap.get("6"));
			assertEquals("task scaling", new Long(0), resMap.get("7"));
			assertEquals("task scaling", new Long(0), resMap.get("8"));
			assertEquals("task scaling", new Long(0), resMap.get("9"));
			assertEquals("task scaling", new Long(0), resMap.get("10"));
			assertEquals("task scaling", new Long(0), resMap.get("11"));
			assertEquals("task scaling", new Long(1), resMap.get("12"));
			assertEquals("task scaling", new Long(1), resMap.get("13"));
			assertEquals("task scaling", new Long(1), resMap.get("14"));
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("12");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("12");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("12");
			sta.setD_is_answered(false);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			resMap = target(SCALE_TASKS).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("tasks_list", "1").queryParam("tasks_list", "2")
					.queryParam("tasks_list", "3").queryParam("tasks_list", "4").queryParam("tasks_list", "5")
					.queryParam("tasks_list", "6").queryParam("tasks_list", "7").queryParam("tasks_list", "8")
					.queryParam("tasks_list", "9").queryParam("tasks_list", "10").queryParam("tasks_list", "11")
					.queryParam("tasks_list", "12").queryParam("tasks_list", "13").queryParam("tasks_list", "14").request().get(Map.class);
			assertEquals("task scaling", new Long(0), resMap.get("1"));
			assertEquals("task scaling", new Long(0), resMap.get("2"));
			assertEquals("task scaling", new Long(0), resMap.get("3"));
			assertEquals("task scaling", new Long(0), resMap.get("4"));
			assertEquals("task scaling", new Long(0), resMap.get("5"));
			assertEquals("task scaling", new Long(0), resMap.get("6"));
			assertEquals("task scaling", new Long(0), resMap.get("7"));
			assertEquals("task scaling", new Long(0), resMap.get("8"));
			assertEquals("task scaling", new Long(0), resMap.get("9"));
			assertEquals("task scaling", new Long(0), resMap.get("10"));
			assertEquals("task scaling", new Long(0), resMap.get("11"));
			assertEquals("task scaling", new Long(1), resMap.get("12"));
			assertEquals("task scaling", new Long(1), resMap.get("13"));
			assertEquals("task scaling", new Long(1), resMap.get("14"));
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("12");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("13");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_Required_Optional();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("14");
			sta.setD_is_answered(true);
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			resMap = target(SCALE_TASKS).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("tasks_list", "1").queryParam("tasks_list", "2")
					.queryParam("tasks_list", "3").queryParam("tasks_list", "4").queryParam("tasks_list", "5")
					.queryParam("tasks_list", "6").queryParam("tasks_list", "7").queryParam("tasks_list", "8")
					.queryParam("tasks_list", "9").queryParam("tasks_list", "10").queryParam("tasks_list", "11")
					.queryParam("tasks_list", "12").queryParam("tasks_list", "13").queryParam("tasks_list", "14").request().get(Map.class);
			assertEquals("task scaling", new Long(0), resMap.get("1"));
			assertEquals("task scaling", new Long(0), resMap.get("2"));
			assertEquals("task scaling", new Long(0), resMap.get("3"));
			assertEquals("task scaling", new Long(0), resMap.get("4"));
			assertEquals("task scaling", new Long(0), resMap.get("5"));
			assertEquals("task scaling", new Long(0), resMap.get("6"));
			assertEquals("task scaling", new Long(0), resMap.get("7"));
			assertEquals("task scaling", new Long(0), resMap.get("8"));
			assertEquals("task scaling", new Long(0), resMap.get("9"));
			assertEquals("task scaling", new Long(0), resMap.get("10"));
			assertEquals("task scaling", new Long(0), resMap.get("11"));
			assertEquals("task scaling", new Long(0), resMap.get("12"));
			assertEquals("task scaling", new Long(0), resMap.get("13"));
			assertEquals("task scaling", new Long(0), resMap.get("14"));
			
			
			
		}  finally {
			Handler.hqlTruncate("StudentTask_Required_Optional");
			Handler.hqlTruncate("StudentTask");
			Handler.hqlTruncate("Student_Required_Optional");
			Handler.hqlTruncate("Student");
			Handler.hqlTruncate("Task_Required_Optional");
			Handler.hqlTruncate("Task");
			Handler.hqlTruncate("Course_Required_Optional");
			Handler.hqlTruncate("UserCourse");
			Handler.hqlTruncate("Course");
			Utilities.setJUnitTest(false);
		}

	}
}
