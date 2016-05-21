package com.asu.seatr.api.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Test;

import com.asu.seatr.api.CommonAPI;
import com.asu.seatr.api.analyzer.required_optional.CourseAPI_Required_Optional;
import com.asu.seatr.api.analyzer.required_optional.StudentAPI_Required_Optional;
import com.asu.seatr.api.analyzer.required_optional.TaskAPI_Required_Optional;
import com.asu.seatr.api.analyzer.unansweredtasks.CourseAPI_UnansweredTasks;
import com.asu.seatr.api.analyzer.unansweredtasks.KCAPI_UnansweredTasks;
import com.asu.seatr.api.analyzer.unansweredtasks.StudentAPI_UnansweredTasks;
import com.asu.seatr.api.analyzer.unansweredtasks.TaskAPI_UnansweredTasks;
import com.asu.seatr.exceptions.CourseAnalyzerMapException;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.CourseAnalyzerHandler;
import com.asu.seatr.handlers.CourseAnalyzerMapHandler;
import com.asu.seatr.handlers.StudentAnalyzerHandler;
import com.asu.seatr.handlers.TaskAnalyzerHandler;
import com.asu.seatr.handlers.TaskKCAnalyzerHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.CourseAnalyzerMap;
import com.asu.seatr.models.analyzers.course.Course_Required_Optional;
import com.asu.seatr.models.analyzers.course.Course_UnansweredTasks;
import com.asu.seatr.models.analyzers.student.Student_UnansweredTasks;
import com.asu.seatr.models.analyzers.task.Task_UnansweredTasks;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_Required_Optional;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_UnansweredTasks;
import com.asu.seatr.models.interfaces.CourseAnalyzerI;
import com.asu.seatr.models.interfaces.TaskKCAnalyzerI;
import com.asu.seatr.rest.models.TKCAReader;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.CAReader_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.required_optional.CAReader_Required_Optional;
import com.asu.seatr.rest.models.analyzer.required_optional.SAReader_Required_Optional;
import com.asu.seatr.rest.models.analyzer.required_optional.TAReader_Required_Optional;
import com.asu.seatr.rest.models.analyzer.unansweredtasks.CAReader_UnansweredTasks;
import com.asu.seatr.rest.models.analyzer.unansweredtasks.KAReader_UnansweredTasks;
import com.asu.seatr.rest.models.analyzer.unansweredtasks.SAReader_UnansweredTasks;
import com.asu.seatr.rest.models.analyzer.unansweredtasks.TAReader_UnansweredTasks;
import com.asu.seatr.rest.models.analyzer.unansweredtasks.TKAReader_UnansweredTasks;
import com.asu.seatr.rest.models.analyzer.unansweredtasks.TKReader_UnansweredTasks;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.Utilities;

public class CommonAPITest extends JerseyTest {

	private static String SET_ANALYZER = "courses/setanalyzer/";
	private static String COURSE_1_URL = "analyzer/unansweredtasks/courses/";
	private static String COURSE_3_URL = "analyzer/required_optional/courses/";
	private static String COURSE_URL = "courses/";
	private static String STUDENT_URL = "students/";
	private static String TASK_URL = "tasks/";
	private static String STUDENT_1_URL = "analyzer/unansweredtasks/students/";
	private static String STUDENT_3_URL = "analyzer/required_optional/students/";
	private static String TASK_3_URL =  "analyzer/required_optional/tasks/";
	private static String TASK_1_URL =  "analyzer/unansweredtasks/tasks/";
	private static String KC_1_URL = "analyzer/unansweredtasks/kc/createkc/";
	private static String MAP_KC_1_URL = "analyzer/unansweredtasks/kc/mapkctask/";
	private static String COPY_KC_MAP_URL = "copykcmap/";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CommonAPI.class, CourseAPI_UnansweredTasks.class, CourseAPI_Required_Optional.class,
				StudentAPI_UnansweredTasks.class, StudentAPI_Required_Optional.class, TaskAPI_UnansweredTasks.class, TaskAPI_Required_Optional.class,
				KCAPI_UnansweredTasks.class);
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

	@Test
	public void setAnalyzer_success() {
		Utilities.setJUnitTest(true);
		try {

			CAReader_UnansweredTasks ca = new CAReader_UnansweredTasks();
			ca.setExternal_course_id("1");
			ca.setTeaching_unit("1");
			ca.setThreshold(2.0);
			ca.setDescription("physics");
			Response resp = target(COURSE_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			resp = target(SET_ANALYZER).queryParam("external_course_id", "1")
					.queryParam("analyzer_name", "UnansweredTasks").queryParam("active", true).request()
					.get(Response.class);
			assertEquals("mapping created", Status.CREATED.getStatusCode(), resp.getStatus());

			// now set analyzer 2 as inactive for the course
			resp = target(SET_ANALYZER).queryParam("external_course_id", "1").queryParam("analyzer_name", "N_In_A_Row")
					.queryParam("active", false).request().get(Response.class);
			assertEquals("mapping created", Status.CREATED.getStatusCode(), resp.getStatus());

			CourseAnalyzerMap cam = CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId("1", 1);
			assertEquals("this analyzer should be active", true, cam.isActive());

			cam = CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId("1", 2);
			assertEquals("this analyzer should be active", false, cam.isActive());

			// now map analyzer 3 as active for the course
			resp = target(SET_ANALYZER).queryParam("external_course_id", "1")
					.queryParam("analyzer_name", "Required_Optional").queryParam("active", true).request()
					.get(Response.class);
			assertEquals("mapping created", Status.CREATED.getStatusCode(), resp.getStatus());

			cam = CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId("1", 3);
			assertEquals("this analyzer should be active", true, cam.isActive());

			cam = CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId("1", 1);
			assertEquals("this analyzer should be inactive", false, cam.isActive());

			cam = CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId("1", 2);
			assertEquals("this analyzer should be inactive", false, cam.isActive());

			// set analyzer 1 as active
			resp = target(SET_ANALYZER).queryParam("external_course_id", "1")
					.queryParam("analyzer_name", "UnansweredTasks").queryParam("active", true).request()
					.get(Response.class);
			assertEquals("mapping created", Status.CREATED.getStatusCode(), resp.getStatus());

			cam = CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId("1", 3);
			assertEquals("this analyzer should be inactive", false, cam.isActive());

			cam = CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId("1", 1);
			assertEquals("this analyzer should be active", true, cam.isActive());

			cam = CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId("1", 2);
			assertEquals("this analyzer should be inactive", false, cam.isActive());

		} catch (Exception e) {
			e.printStackTrace();
			fail("execution shuld not come here");
		} finally {
			Utilities.clearDatabase();
			Utilities.setJUnitTest(false);
		}
	}

	@Test
	public void setAnalyzer_fail() {
		Utilities.setJUnitTest(true);
		try {
			// empty analyzer_id
			Response resp = target(SET_ANALYZER).queryParam("external_course_id", "1").queryParam("analyzer_name", "")
					.queryParam("active", true).request().get(Response.class);
			assertEquals("invalid analyzer id", Status.OK.getStatusCode(), resp.getStatus());

			// empty course
			resp = target(SET_ANALYZER).queryParam("external_course_id", "")
					.queryParam("analyzer_name", "UnansweredTasks").queryParam("active", true).request()
					.get(Response.class);
			assertEquals("invalid course id", Status.OK.getStatusCode(), resp.getStatus());

			// invalid analyzer
			resp = target(SET_ANALYZER).queryParam("external_course_id", "1").queryParam("analyzer_name", "abc")
					.queryParam("active", true).request().get(Response.class);
			assertEquals("invalid analyzer id", Status.OK.getStatusCode(), resp.getStatus());

			// invalid course
			resp = target(SET_ANALYZER).queryParam("external_course_id", "2")
					.queryParam("analyzer_name", "UnansweredTasks").queryParam("active", true).request()
					.get(Response.class);
			assertEquals("invalid course id", Status.OK.getStatusCode(), resp.getStatus());
		} finally {
			Utilities.clearDatabase();
			Utilities.setJUnitTest(false);
		}
	}

	@Test
	public void deleteCourse_success() {
		Utilities.setJUnitTest(true);
		try {
			CAReader_UnansweredTasks ca = new CAReader_UnansweredTasks();
			ca.setExternal_course_id("1");
			ca.setTeaching_unit("1");
			ca.setThreshold(2.0);
			ca.setDescription("physics");
			Response resp = target(COURSE_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			CAReader_Required_Optional ca1 = new CAReader_Required_Optional();
			ca1.setExternal_course_id("2");
			ca1.setDescription("physics");
			ca1.setD_current_unit_no(1);
			ca1.setD_max_n(2);
			ca1.setS_units(10);

			resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca1), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			resp = target(COURSE_URL).queryParam("external_course_id", "1").request().delete(Response.class);
			assertEquals("deleted sucessfully", Status.OK.getStatusCode(), resp.getStatus());

			resp = target(COURSE_3_URL).queryParam("external_course_id", "2").request().delete(Response.class);
			assertEquals("deleted sucessfully", Status.OK.getStatusCode(), resp.getStatus());

			try {
				CourseAnalyzerHandler.readByExtId(Course_UnansweredTasks.class, "1");
				fail("should not come here as course is deleted");
			} catch (CourseException ce) {
				assertEquals("course not found", MyMessage.COURSE_NOT_FOUND, ce.getMyMessage());
			}
			try {
				CourseAnalyzerHandler.readByExtId(Course_Required_Optional.class, "2");
				fail("should not come here as course is deleted");
			} catch (CourseException ce) {
				assertEquals("course not found", MyMessage.COURSE_ANALYZER_NOT_FOUND, ce.getMyMessage());
			}
			resp = target(COURSE_URL).queryParam("external_course_id", "2").request().delete(Response.class);
			assertEquals("deleted sucessfully", Status.OK.getStatusCode(), resp.getStatus());

		} finally {
			Utilities.clearDatabase();
			Utilities.setJUnitTest(false);
		}
	}

	@Test
	public void deleteStudent_success() {
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

			// create course
			CAReader_Required_Optional ca1 = new CAReader_Required_Optional();
			ca1.setExternal_course_id("2");
			ca1.setDescription("physics");
			ca1.setD_current_unit_no(1);
			ca1.setD_max_n(2);
			ca1.setS_units(10);

			resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca1), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create student
			SAReader_UnansweredTasks sa = new SAReader_UnansweredTasks();
			sa.setExternal_course_id("1");
			sa.setExternal_student_id("1");
			sa.setS_placement_score(3.0);
			sa.setS_year("Senior");
			resp = target(STUDENT_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(sa), Response.class);
			assertEquals("student created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create student
			SAReader_Required_Optional sa1 = new SAReader_Required_Optional();
			sa1.setExternal_course_id("2");
			sa1.setExternal_student_id("1");
			sa1.setS_placement_score(3.0);
			sa1.setS_year("Senior");
			resp = target(STUDENT_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(sa1), Response.class);
			assertEquals("student created", Status.CREATED.getStatusCode(), resp.getStatus());

			resp = target(STUDENT_URL).queryParam("external_course_id", "1").queryParam("external_student_id", "1")
					.request().delete(Response.class);
			assertEquals("deleted sucessfully", Status.OK.getStatusCode(), resp.getStatus());

			resp = target(STUDENT_3_URL).queryParam("external_course_id", "2").queryParam("external_student_id", "1")
					.request().delete(Response.class);
			assertEquals("deleted sucessfully", Status.OK.getStatusCode(), resp.getStatus());

			try {
				StudentAnalyzerHandler.readByExtId(Student_UnansweredTasks.class, "1", "1");
				fail("should not come here ");
			} catch (StudentException ce) {
				assertEquals("student not found", MyMessage.STUDENT_NOT_FOUND, ce.getMyMessage());
			} catch (CourseException ce) {
				fail("should not come here");
			}
			try {
				StudentAnalyzerHandler.readByExtId(Student_UnansweredTasks.class, "1", "2");
				fail("should not come here ");
			} catch (StudentException ce) {
				assertEquals("course not found", MyMessage.STUDENT_ANALYZER_NOT_FOUND, ce.getMyMessage());
			} catch (CourseException ce) {
				fail("should not come here");
			}
			resp = target(STUDENT_URL).queryParam("external_course_id", "2").queryParam("external_student_id", "1")
					.request().delete(Response.class);
			assertEquals("deleted sucessfully", Status.OK.getStatusCode(), resp.getStatus());

		} finally {
			Utilities.clearDatabase();
			Utilities.setJUnitTest(false);
		}
	}

	@Test
	public void deleteTask_success() {
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

			// create course
			CAReader_Required_Optional ca1 = new CAReader_Required_Optional();
			ca1.setExternal_course_id("2");
			ca1.setDescription("physics");
			ca1.setD_current_unit_no(1);
			ca1.setD_max_n(2);
			ca1.setS_units(10);

			resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca1), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			TAReader_UnansweredTasks ta = new TAReader_UnansweredTasks();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_difficulty_level(1);
			resp = target(TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			TAReader_Required_Optional ta1 = new TAReader_Required_Optional();
			ta1.setExternal_course_id("2");
			ta1.setExternal_task_id("1");
			ta1.setS_is_required(true);
			ta1.setS_sequence_no(1);
			ta1.setS_unit_no(1);
			resp = target(TASK_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta1), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			resp = target(TASK_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1")
					.request().delete(Response.class);
			assertEquals("deleted sucessfully", Status.OK.getStatusCode(), resp.getStatus());

			resp = target(TASK_3_URL).queryParam("external_course_id", "2").queryParam("external_task_id", "1")
					.request().delete(Response.class);
			assertEquals("deleted sucessfully", Status.OK.getStatusCode(), resp.getStatus());

			try {
				TaskAnalyzerHandler.readByExtId(Task_UnansweredTasks.class, "1", "1");
				fail("should not come here ");
			} catch (TaskException ce) {
				assertEquals("task not found", MyMessage.TASK_NOT_FOUND, ce.getMyMessage());
			} catch (CourseException ce) {
				fail("should not come here");
			}
			try {
				TaskAnalyzerHandler.readByExtId(Task_UnansweredTasks.class, "1", "2");
				fail("should not come here ");
			} catch (TaskException ce) {
				assertEquals("course not found", MyMessage.TASK_ANALYZER_NOT_FOUND, ce.getMyMessage());
			} catch (CourseException ce) {
				fail("should not come here");
			}
			resp = target(TASK_URL).queryParam("external_course_id", "2").queryParam("external_task_id", "1")
					.request().delete(Response.class);
			assertEquals("deleted sucessfully", Status.OK.getStatusCode(), resp.getStatus());

		} finally {
			Utilities.clearDatabase();
			Utilities.setJUnitTest(false);
		}
	}
	
	@Test
	public void deleteCourse_fail() {
		Utilities.setJUnitTest(true);
		try {
			Response resp = target(COURSE_URL).queryParam("external_course_id", "1").request().delete(Response.class);
			assertEquals("deleted sucessfully", Status.NOT_FOUND.getStatusCode(), resp.getStatus());
		} finally {
			Utilities.clearDatabase();
			Utilities.setJUnitTest(false);
		}
	}
	@Test
	public void deleteStudent_fail() {
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
						
			resp = target(STUDENT_URL).queryParam("external_course_id", "1").queryParam("external_student_id", "1").request().delete(Response.class);
			assertEquals("deleted sucessfully", Status.NOT_FOUND.getStatusCode(), resp.getStatus());
		} finally {
			Utilities.clearDatabase();
			Utilities.setJUnitTest(false);
		}
	}
	@Test
	public void deleteTask_fail() {
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
						
						
			resp = target(TASK_URL).queryParam("external_course_id", "1").queryParam("external_task_id", "1").request().delete(Response.class);
			assertEquals("deleted sucessfully", Status.NOT_FOUND.getStatusCode(), resp.getStatus());
		} finally {
			Utilities.clearDatabase();
			Utilities.setJUnitTest(false);
		}
	}
	@Test
	public void copyKCMap_success() {
		Utilities.setJUnitTest(true);
		try {
			//create kc task mapping
			// create course
			CAReader_UnansweredTasks ca = new CAReader_UnansweredTasks();
			ca.setExternal_course_id("1");
			ca.setTeaching_unit("1");
			ca.setThreshold(2.0);
			ca.setDescription("physics");
			Response resp = target(COURSE_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			//create same course for different analyzer
			CAReader_Required_Optional ca1 = new CAReader_Required_Optional();
			ca1.setExternal_course_id("1");
			ca1.setDescription("physics");
			ca1.setD_current_unit_no(1);
			ca1.setD_max_n(2);
			ca1.setS_units(10);
			resp = target(COURSE_3_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca1), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			resp = target(SET_ANALYZER).queryParam("external_course_id", "1")
					.queryParam("analyzer_name", "UnansweredTasks").queryParam("active", true).request()
					.get(Response.class);
			assertEquals("mapping created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			resp = target(SET_ANALYZER).queryParam("external_course_id", "1")
					.queryParam("analyzer_name", "Required_Optional").queryParam("active", true).request()
					.get(Response.class);
			assertEquals("mapping created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			

			// create kc
			KAReader_UnansweredTasks ka = new KAReader_UnansweredTasks();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			ka.setS_unit(10);
			resp = target(KC_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kcreader
			ka = new KAReader_UnansweredTasks();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("2");
			ka.setS_unit(10);
			resp = target(KC_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			TAReader_UnansweredTasks ta = new TAReader_UnansweredTasks();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_difficulty_level(1);
			resp = target(TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			ta = new TAReader_UnansweredTasks();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("2");
			ta.setS_difficulty_level(2);
			resp = target(TASK_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// map kc
			TKReader_UnansweredTasks tkReader = new TKReader_UnansweredTasks();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(true);
			TKAReader_UnansweredTasks tkaReaderArray[] = new TKAReader_UnansweredTasks[2];

			TKAReader_UnansweredTasks tk0 = new TKAReader_UnansweredTasks();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("1");
			tk0.setMin_mastery_level(3);
			tkaReaderArray[0] = tk0;

			TKAReader_UnansweredTasks tk1 = new TKAReader_UnansweredTasks();
			tk1.setExternal_kc_id("2");
			tk1.setExternal_task_id("2");
			tk1.setMin_mastery_level(5);
			tkaReaderArray[1] = tk1;

			tkReader.setTkaReader(tkaReaderArray);

			resp = target(MAP_KC_1_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());
			
			//copy kc mapping from unanswered tasks analyzer to required_optional analyzer for course 1
			TKCAReader tkca = new TKCAReader();
			tkca.setExternal_course_id("1");
			tkca.setFrom_analyzer("UnansweredTasks");
			tkca.setTo_analyzer("Required_Optional");
			tkca.setReplace(true);
			resp = target(COPY_KC_MAP_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(tkca),Response.class);
			assertEquals("kc map copied", Status.CREATED.getStatusCode(), resp.getStatus());	
			
			try
			{
			List<TaskKCAnalyzerI> tklist = TaskKCAnalyzerHandler.readByExtCourseId(TaskKC_UnansweredTasks.class, "1");
			assertTrue("",tklist.get(0).getKc().getExternal_id().equals("1") || tklist.get(0).getKc().getExternal_id().equals("2"));
			}
			catch(Exception e)
			{
				fail("should not come here");
			}
			try
			{
			List<TaskKCAnalyzerI> tklist = TaskKCAnalyzerHandler.readByExtCourseId(TaskKC_Required_Optional.class, "1");
			assertTrue("",tklist.get(0).getKc().getExternal_id().equals("1") || tklist.get(0).getKc().getExternal_id().equals("2"));
			}
			catch(Exception e)
			{
				fail("should not come here");
			}
			
			
		}
	 finally {
		Utilities.clearDatabase();
		Utilities.setJUnitTest(false);
	}
}
	
	
}
