package com.asu.seatr.api.analyzer.n_in_a_row.test;

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
import com.asu.seatr.api.analyzer.n_in_a_row.CourseAPI_N_In_A_Row;
import com.asu.seatr.api.analyzer.n_in_a_row.RecommenderAPI_N_In_A_Row;
import com.asu.seatr.api.analyzer.n_in_a_row.StudentAPI_N_In_A_Row;
import com.asu.seatr.api.analyzer.n_in_a_row.StudentTaskAPI_N_In_A_Row;
import com.asu.seatr.api.analyzer.n_in_a_row.TaskAPI_N_In_A_Row;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.CAReader_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.KAReader_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.SAReader_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.STAReader_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.TAReader_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.TKAReader_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.TKReader_N_In_A_Row;
import com.asu.seatr.utils.Utilities;

public class Recommender_N_In_A_Row_API_test extends JerseyTest{

	private static String GETTASKS_3_URL = "analyzer/n_in_a_row/gettasks/";
	private static String CREATE_COURSE_3_URL = "analyzer/n_in_a_row/courses/";
	private static String CREATE_TASK_3_URL = "analyzer/n_in_a_row/tasks/";
	private static String CREATE_STUDENT_3_URL = "analyzer/n_in_a_row/students/";
	private static String INIT_TASKS = "inittasks/";
	private static String STUDENT_TASK_URL = "analyzer/n_in_a_row/studenttasks";
	private static String KC_URL = "analyzer/n_in_a_row/kc/createkc/";
	private static String MAP_KC_URL = "analyzer/n_in_a_row/kc/mapkctask/";
	
	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_N_In_A_Row.class, RecommenderAPI_N_In_A_Row.class, AdminAPI.class, TaskAPI_N_In_A_Row.class, CommonAPI.class, StudentAPI_N_In_A_Row.class,
				StudentTaskAPI_N_In_A_Row.class);
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
			CAReader_N_In_A_Row ca = new CAReader_N_In_A_Row();
			ca.setExternal_course_id("1");;
			ca.setDescription("chemistry");
			Response resp = target(CREATE_COURSE_3_URL).request()
					.header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			SAReader_N_In_A_Row sa = new SAReader_N_In_A_Row();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			resp =  target(CREATE_STUDENT_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sa),Response.class);
			assertEquals("student created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			final List<String> resList = target(GETTASKS_3_URL).queryParam("external_student_id", "1")
					.queryParam("external_course_id", "1").queryParam("number_of_tasks", "5").request().get(List.class);
			assertEquals("empty tasks", 0,resList.size());

		}  finally {
			Handler.hqlTruncate("Student_N_In_A_Row");
			Handler.hqlTruncate("Student");
			Handler.hqlTruncate("Course_N_In_A_Row");
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
			CAReader_N_In_A_Row ca = new CAReader_N_In_A_Row();
			ca.setExternal_course_id("1");
			ca.setDescription("chemistry");
			ca.setDescription("physics");
			Response resp = target(CREATE_COURSE_3_URL).request()
					.header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			SAReader_N_In_A_Row sa = new SAReader_N_In_A_Row();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			resp =  target(CREATE_STUDENT_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sa),Response.class);
			assertEquals("student created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			TAReader_N_In_A_Row ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("2");;
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("3");
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("4");
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("5");
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("6");
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("7");
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("8");
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("9");
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("10");
			resp = target(CREATE_TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			
			//created KCs
			KAReader_N_In_A_Row ka = new KAReader_N_In_A_Row();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());

			ka = new KAReader_N_In_A_Row();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("2");
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ka = new KAReader_N_In_A_Row();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("3");
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			// map kc
			TKReader_N_In_A_Row tkReader = new TKReader_N_In_A_Row();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(true);
			TKAReader_N_In_A_Row tkaReaderArray[] = new TKAReader_N_In_A_Row[2];

			TKAReader_N_In_A_Row tk0 = new TKAReader_N_In_A_Row();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("1");
			tkaReaderArray[0] = tk0;

			TKAReader_N_In_A_Row tk1 = new TKAReader_N_In_A_Row();
			tk1.setExternal_kc_id("2");
			tk1.setExternal_task_id("1");
			tkaReaderArray[1] = tk1;

			tkReader.setTkaReader(tkaReaderArray);
			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			
			tkReader = new TKReader_N_In_A_Row();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(true);
			tkaReaderArray = new TKAReader_N_In_A_Row[2];

			tk0 = new TKAReader_N_In_A_Row();
			tk0.setExternal_kc_id("2");
			tk0.setExternal_task_id("2");
			tkaReaderArray[0] = tk0;

			tk1 = new TKAReader_N_In_A_Row();
			tk1.setExternal_kc_id("3");
			tk1.setExternal_task_id("2");
			tkaReaderArray[1] = tk1;

			tkReader.setTkaReader(tkaReaderArray);
			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			
			tkReader = new TKReader_N_In_A_Row();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(true);
			tkaReaderArray = new TKAReader_N_In_A_Row[2];

			tk0 = new TKAReader_N_In_A_Row();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("3");
			tkaReaderArray[0] = tk0;

			tk1 = new TKAReader_N_In_A_Row();
			tk1.setExternal_kc_id("3");
			tk1.setExternal_task_id("3");
			tkaReaderArray[1] = tk1;

			tkReader.setTkaReader(tkaReaderArray);
			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			
			tkReader = new TKReader_N_In_A_Row();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(true);
			tkaReaderArray = new TKAReader_N_In_A_Row[1];

			tk0 = new TKAReader_N_In_A_Row();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("4");
			tkaReaderArray[0] = tk0;

			tkReader.setTkaReader(tkaReaderArray);
			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());



			tkReader = new TKReader_N_In_A_Row();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(true);
			tkaReaderArray = new TKAReader_N_In_A_Row[1];

			tk0 = new TKAReader_N_In_A_Row();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("5");
			tkaReaderArray[0] = tk0;
			
			tk1 = new TKAReader_N_In_A_Row();
			tk1.setExternal_kc_id("2");
			tk1.setExternal_task_id("5");
			tkaReaderArray[1] = tk1;

			tkReader.setTkaReader(tkaReaderArray);
			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			
			tkReader = new TKReader_N_In_A_Row();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(true);
			tkaReaderArray = new TKAReader_N_In_A_Row[1];

			tk0 = new TKAReader_N_In_A_Row();
			tk0.setExternal_kc_id("2");
			tk0.setExternal_task_id("6");
			tkaReaderArray[0] = tk0;

			tkReader.setTkaReader(tkaReaderArray);
			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			
			tkReader = new TKReader_N_In_A_Row();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(true);
			tkaReaderArray = new TKAReader_N_In_A_Row[1];

			tk0 = new TKAReader_N_In_A_Row();
			tk0.setExternal_kc_id("3");
			tk0.setExternal_task_id("7");
			tkaReaderArray[0] = tk0;

			tkReader.setTkaReader(tkaReaderArray);
			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			
			/*   Task		KC
			 *    1			1,2
			 *    2			2,3
			 *    3			1,3
			 *    4			1
			 *    5			1,2
			 *    6			2
			 *    7			3
			 */
			
			
			//now lets answer some tasks
			STAReader_N_In_A_Row sta = new STAReader_N_In_A_Row();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("4");
			sta.setD_status("correct");
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());

			sta = new STAReader_N_In_A_Row();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("4");
			sta.setD_status("correct");
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_N_In_A_Row();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("3");
			sta.setD_status("correct");
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			//now  kc1 is mastered and proficiency for kc3 is 1
			
			sta = new STAReader_N_In_A_Row();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("7");
			sta.setD_status("correct");
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_N_In_A_Row();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("7");
			sta.setD_status("correct");
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			//now both of kc1 and kc3 are mastered
			
			//To-Do
			/*resList = target(GETTASKS_3_URL).queryParam("external_student_id", "1")
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
			*/
			
			
			
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
