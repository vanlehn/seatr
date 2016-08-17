package com.asu.seatr.api.analyzer.bkt;

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
import com.asu.seatr.api.bkt.CourseAPI_BKT;
import com.asu.seatr.api.bkt.KCAPI_BKT;
import com.asu.seatr.api.bkt.RecommenderAPI_BKT;
import com.asu.seatr.api.bkt.StudentAPI_BKT;
import com.asu.seatr.api.bkt.StudentTaskAPI_BKT;
import com.asu.seatr.api.bkt.TaskAPI_BKT;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.rest.models.analyzer.bkt.CAReader_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.KAReader_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.SAReader_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.STAReader_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.TAReader_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.TKAReader_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.TKReader_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.TLReader_BKT;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.TKReader_N_In_A_Row;
import com.asu.seatr.utils.Utilities;

public class Recommender_BKT_test extends JerseyTest{

	private static String GETTASKS_BKT_URL = "analyzer/bkt/get_recomm_tasks/";
	private static String GETUTILITY_BKT_URL = "analyzer/bkt/get_utility";
	private static String CREATE_COURSE_BKT_URL = "analyzer/bkt/courses/";
	private static String CREATE_TASK_BKT_URL = "analyzer/bkt/tasks/";
	private static String CREATE_STUDENT_BKT_URL = "analyzer/bkt/students/";
	//private static String INIT_TASKS_URL = "analyzer/bkt/initutility";
	private static String STUDENT_TASK_URL = "analyzer/bkt/studenttasks";
	private static String KC_URL = "analyzer/bkt/kc/createkc/";
	private static String MAP_KC_URL = "analyzer/bkt/kc/mapkctask/";
	
	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CourseAPI_BKT.class, RecommenderAPI_BKT.class, AdminAPI.class, TaskAPI_BKT.class, CommonAPI.class, StudentAPI_BKT.class,
				StudentTaskAPI_BKT.class, KCAPI_BKT.class);
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
			target(GETTASKS_BKT_URL).queryParam("external_student_id", "1")
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
			final List<String> resList = target(GETTASKS_BKT_URL).queryParam("external_student_id", "")
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
			final List<String> resList = target(GETTASKS_BKT_URL).queryParam("external_student_id", "1")
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
			CAReader_BKT ca = new CAReader_BKT();
			ca.setExternal_course_id("1");
			ca.setDescription("chemistry");
			Response resp = target(CREATE_COURSE_BKT_URL).request()
					.header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			SAReader_BKT sa = new SAReader_BKT();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			resp =  target(CREATE_STUDENT_BKT_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sa),Response.class);
			assertEquals("student created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			TAReader_BKT ta = new TAReader_BKT();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setType("multiple-choice");
			ta.setDifficulty(0.4);
			resp = target(CREATE_TASK_BKT_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_BKT();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("2");
			ta.setType("simple-input");
			ta.setDifficulty(0.4);
			resp = target(CREATE_TASK_BKT_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_BKT();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("3");
			ta.setType("normal-input");
			ta.setDifficulty(0.4);
			resp = target(CREATE_TASK_BKT_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_BKT();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("4");
			ta.setType("complex-input");
			ta.setDifficulty(0.4);
			resp = target(CREATE_TASK_BKT_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_BKT();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("5");
			ta.setType("self-report");
			ta.setDifficulty(0.4);
			resp = target(CREATE_TASK_BKT_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_BKT();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("6");
			ta.setType("multiple-choice");
			ta.setDifficulty(0.4);
			resp = target(CREATE_TASK_BKT_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ta = new TAReader_BKT();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("7");
			ta.setType("multiple-choice");
			ta.setDifficulty(0.4);
			resp = target(CREATE_TASK_BKT_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			
			//created KCs
			KAReader_BKT ka = new KAReader_BKT();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			ka.setInit_p(0.2);
			ka.setLearning_rate(0.3);
			ka.setUtility(2.0);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());

			ka = new KAReader_BKT();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("2");
			ka.setInit_p(0.2);
			ka.setLearning_rate(0.3);
			ka.setUtility(2.0);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			ka = new KAReader_BKT();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("3");
			ka.setInit_p(0.2);
			ka.setLearning_rate(0.3);
			ka.setUtility(2.0);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			// map kc
			TKReader_BKT tkReader = new TKReader_BKT();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(false);
			TKAReader_BKT tkaReaderArray[] = new TKAReader_BKT[2];

			TKAReader_BKT tk0 = new TKAReader_BKT();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("1");
			tkaReaderArray[0] = tk0;

			TKAReader_BKT tk1 = new TKAReader_BKT();
			tk1.setExternal_kc_id("2");
			tk1.setExternal_task_id("1");
			tkaReaderArray[1] = tk1;

			tkReader.setTkaReader(tkaReaderArray);
			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			
			tkReader = new TKReader_BKT();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(false);
			tkaReaderArray = new TKAReader_BKT[2];

			tk0 = new TKAReader_BKT();
			tk0.setExternal_kc_id("2");
			tk0.setExternal_task_id("2");
			tkaReaderArray[0] = tk0;

			tk1 = new TKAReader_BKT();
			tk1.setExternal_kc_id("3");
			tk1.setExternal_task_id("2");
			tkaReaderArray[1] = tk1;

			tkReader.setTkaReader(tkaReaderArray);
			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			
			tkReader = new TKReader_BKT();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(false);
			tkaReaderArray = new TKAReader_BKT[2];

			tk0 = new TKAReader_BKT();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("3");
			tkaReaderArray[0] = tk0;

			tk1 = new TKAReader_BKT();
			tk1.setExternal_kc_id("3");
			tk1.setExternal_task_id("3");
			tkaReaderArray[1] = tk1;

			tkReader.setTkaReader(tkaReaderArray);
			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			
			tkReader = new TKReader_BKT();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(false);
			tkaReaderArray = new TKAReader_BKT[1];

			tk0 = new TKAReader_BKT();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("4");
			tkaReaderArray[0] = tk0;

			tkReader.setTkaReader(tkaReaderArray);
			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());



			tkReader = new TKReader_BKT();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(false);
			tkaReaderArray = new TKAReader_BKT[3];

			tk0 = new TKAReader_BKT();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("5");
			tkaReaderArray[0] = tk0;
			
			tk1 = new TKAReader_BKT();
			tk1.setExternal_kc_id("2");
			tk1.setExternal_task_id("5");
			tkaReaderArray[1] = tk1;
			
			TKAReader_BKT tk2 = new TKAReader_BKT();
			tk2.setExternal_kc_id("3");
			tk2.setExternal_task_id("5");
			tkaReaderArray[2] = tk2;

			tkReader.setTkaReader(tkaReaderArray);
			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			
			tkReader = new TKReader_BKT();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(false);
			tkaReaderArray = new TKAReader_BKT[1];

			tk0 = new TKAReader_BKT();
			tk0.setExternal_kc_id("2");
			tk0.setExternal_task_id("6");
			tkaReaderArray[0] = tk0;

			tkReader.setTkaReader(tkaReaderArray);
			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			
			tkReader = new TKReader_BKT();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(false);
			tkaReaderArray = new TKAReader_BKT[1];

			tk0 = new TKAReader_BKT();
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
			 *    5			1,2,3
			 *    6			2
			 *    7			3
			 */
			
			//resp = target(INIT_TASKS_URL).queryParam("external_course_id", "1").request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").get(Response.class);
			//assertEquals("initialization finished", Status.OK.getStatusCode(),resp.getStatus());
			
			
			//now lets answer some tasks
			STAReader_BKT sta = new STAReader_BKT();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("4");
			sta.setD_status("correct");
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());

			sta = new STAReader_BKT();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("4");
			sta.setD_status("correct");
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_BKT();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("3");
			sta.setD_status("correct");
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			
			sta = new STAReader_BKT();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("7");
			sta.setD_status("incorrect");
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			sta = new STAReader_BKT();
			sta.setExternal_course_id("1");
			sta.setExternal_student_id("1");
			sta.setExternal_task_id("7");
			sta.setD_status("correct");
			resp = target(STUDENT_TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(sta),Response.class);
			assertEquals("student task created", Status.CREATED.getStatusCode(),resp.getStatus());
			
			//now both of kc1 and kc3 are mastered
			
			@SuppressWarnings("unchecked")
			List<String> resList = 
				target(GETTASKS_BKT_URL).queryParam("external_student_id", "1").queryParam("external_course_id", "1").queryParam("number_of_tasks", "7").request().get(List.class);
			//don't check the order of the recommend list
			assertEquals("task length","7",String.valueOf(resList.size()));
			
			@SuppressWarnings("unchecked")
			TLReader_BKT task_list=new TLReader_BKT();
			task_list.setExternal_student_id("1");
			task_list.setExternal_course_id("1");
			task_list.setExternal_task_ids(new String[]{"1","2","3","4","5"});
			List<String> utilityList = 
				target(GETUTILITY_BKT_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").put(Entity.json(task_list),List.class);
			
			assertEquals("length of task","5",String.valueOf(utilityList.size()));
			
			task_list=new TLReader_BKT();
			task_list.setExternal_student_id("1");
			task_list.setExternal_course_id("1");
			task_list.setExternal_task_ids(new String[]{"1","2","3","4","5","6","7"});
			utilityList = 
				target(GETUTILITY_BKT_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").put(Entity.json(task_list),List.class);
			
			assertEquals("length of task","7",String.valueOf(utilityList.size()));
				
			
		}  finally {
			Handler.hqlTruncate("SKC_BKT");
			Handler.hqlTruncate("StuTaskUtility_BKT");
			Handler.hqlTruncate("StudentTask_BKT");
			Handler.hqlTruncate("StudentTask");
			Handler.hqlTruncate("Student_BKT");
			Handler.hqlTruncate("Student");
			Handler.hqlTruncate("Task_BKT");
			//Handler.hqlTruncate("Task");
			Handler.hqlTruncate("Course_BKT");
			Handler.hqlTruncate("UserCourse");
			//Handler.hqlTruncate("Course");
			Utilities.setJUnitTest(false);
		}

	}
}
