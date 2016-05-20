package com.asu.seatr.api.analyzer.required_optional.test;


import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Before;
import org.junit.Test;

import com.asu.seatr.api.analyzer.required_optional.CourseAPI_Required_Optional;
import com.asu.seatr.api.analyzer.required_optional.KCAPI_Required_Optional;
import com.asu.seatr.api.analyzer.required_optional.TaskAPI_Required_Optional;
import com.asu.seatr.rest.models.analyzer.required_optional.CAReader_Required_Optional;
import com.asu.seatr.rest.models.analyzer.required_optional.KAReader_Required_Optional;
import com.asu.seatr.rest.models.analyzer.required_optional.TAReader_Required_Optional;
import com.asu.seatr.rest.models.analyzer.required_optional.TKAReader_Required_Optional;
import com.asu.seatr.rest.models.analyzer.required_optional.TKReader_Required_Optional;
import com.asu.seatr.utils.Utilities;

public class KCAPI_Required_Optional_Test extends JerseyTest {

	private static String COURSE_URL = "analyzer/required_optional/courses/";
	private static String KC_URL = "analyzer/required_optional/kc/createkc/";
	private static String MAP_KC_URL = "analyzer/required_optional/kc/mapkctask/";
	private static String TASK_URL = "analyzer/required_optional/tasks/";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(KCAPI_Required_Optional.class, CourseAPI_Required_Optional.class,
				TaskAPI_Required_Optional.class);
	}

	@Before
	public void init() {
		System.out.println("initialization started.....");
		Utilities.setJUnitTest(true);
		Utilities.clearDatabase();

		Utilities.setJUnitTest(false);
	}

	@Test
	public void createKCSuccess() {
		Utilities.setJUnitTest(true);
		try {
			// create course
			CAReader_Required_Optional ca = new CAReader_Required_Optional();
			ca.setExternal_course_id("1");
			ca.setD_current_unit_no(1);
			ca.setD_max_n(3);
			ca.setDescription("physics");
			ca.setS_units(6);
			ca.setDescription("physics");
			Response resp = target(COURSE_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kcreader
			KAReader_Required_Optional ka = new KAReader_Required_Optional();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			ka.setS_unit(10);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());
		} finally {
			Utilities.clearDatabase();
			Utilities.setJUnitTest(false);
		}
	}

	@Test
	public void createKC_Fail() {
		Utilities.setJUnitTest(true);
		try {
			// create course
			CAReader_Required_Optional ca = new CAReader_Required_Optional();
			ca.setExternal_course_id("1");
			ca.setD_current_unit_no(1);
			ca.setD_max_n(3);
			ca.setDescription("physics");
			ca.setS_units(6);
			ca.setDescription("physics");
			Response resp = target(COURSE_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kcreader invalid kc id
			KAReader_Required_Optional ka = new KAReader_Required_Optional();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("");
			ka.setS_unit(10);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc", Status.OK.getStatusCode(), resp.getStatus());

			// create kcreader invalid course id
			ka = new KAReader_Required_Optional();
			ka.setExternal_course_id("");
			ka.setExternal_kc_id("2");
			ka.setS_unit(10);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc ", Status.OK.getStatusCode(), resp.getStatus());

			// create kc
			ka = new KAReader_Required_Optional();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			ka.setS_unit(10);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc ", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kcreader duplicate
			ka = new KAReader_Required_Optional();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			ka.setS_unit(10);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc ", Status.OK.getStatusCode(), resp.getStatus());

			// create kcreader with non existing course id
			ka = new KAReader_Required_Optional();
			ka.setExternal_course_id("2");
			ka.setExternal_kc_id("3");
			ka.setS_unit(10);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc ", Status.OK.getStatusCode(), resp.getStatus());

		} finally {
			Utilities.clearDatabase();
			Utilities.setJUnitTest(false);
		}
	}

	@Test
	public void mapKCSuccess() {
		Utilities.setJUnitTest(true);
		try {
			// create course
			CAReader_Required_Optional ca = new CAReader_Required_Optional();
			ca.setExternal_course_id("1");
			ca.setD_current_unit_no(1);
			ca.setD_max_n(3);
			ca.setDescription("physics");
			ca.setS_units(6);
			ca.setDescription("physics");
			Response resp = target(COURSE_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kc
			KAReader_Required_Optional ka = new KAReader_Required_Optional();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			ka.setS_unit(10);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kcreader
			ka = new KAReader_Required_Optional();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("2");
			ka.setS_unit(10);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			TAReader_Required_Optional ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_is_required(true);
			ta.setS_sequence_no(1);
			ta.setS_unit_no(1);
			resp = target(TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("2");
			ta.setS_is_required(true);
			ta.setS_sequence_no(1);
			ta.setS_unit_no(1);
			resp = target(TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// map kc
			TKReader_Required_Optional tkReader = new TKReader_Required_Optional();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(true);
			TKAReader_Required_Optional tkaReaderArray[] = new TKAReader_Required_Optional[2];

			TKAReader_Required_Optional tk0 = new TKAReader_Required_Optional();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("1");
			tkaReaderArray[0] = tk0;

			TKAReader_Required_Optional tk1 = new TKAReader_Required_Optional();
			tk1.setExternal_kc_id("2");
			tk1.setExternal_task_id("2");
			tkaReaderArray[1] = tk1;

			tkReader.setTkaReader(tkaReaderArray);

			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());



		} finally {
			Utilities.clearDatabase();
			Utilities.setJUnitTest(false);
		}
	}
	
	@Test
	public void mapKCFail() {
		Utilities.setJUnitTest(true);
		try {
			// create course
			CAReader_Required_Optional ca = new CAReader_Required_Optional();
			ca.setExternal_course_id("1");
			ca.setD_current_unit_no(1);
			ca.setD_max_n(3);
			ca.setDescription("physics");
			ca.setS_units(6);
			ca.setDescription("physics");
			Response resp = target(COURSE_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kc
			KAReader_Required_Optional ka = new KAReader_Required_Optional();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			ka.setS_unit(10);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kcreader
			ka = new KAReader_Required_Optional();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("2");
			ka.setS_unit(10);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			TAReader_Required_Optional ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setS_is_required(true);
			ta.setS_sequence_no(1);
			ta.setS_unit_no(1);
			resp = target(TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			ta = new TAReader_Required_Optional();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("2");
			ta.setS_is_required(true);
			ta.setS_sequence_no(1);
			ta.setS_unit_no(1);
			resp = target(TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// map kc
			TKReader_Required_Optional tkReader = new TKReader_Required_Optional();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(true);
			TKAReader_Required_Optional tkaReaderArray[] = new TKAReader_Required_Optional[2];

			TKAReader_Required_Optional tk0 = new TKAReader_Required_Optional();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("1");
			tkaReaderArray[0] = tk0;

			TKAReader_Required_Optional tk1 = new TKAReader_Required_Optional();
			tk1.setExternal_kc_id("2");
			tk1.setExternal_task_id("2");
			tkaReaderArray[1] = tk1;

			tkReader.setTkaReader(tkaReaderArray);

			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());

			// duplicate kc map with replace false should lead to error
			tkReader = new TKReader_Required_Optional();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(false);
			tkaReaderArray = new TKAReader_Required_Optional[1];

			tk0 = new TKAReader_Required_Optional();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("1");
			tkaReaderArray[0] = tk0;


			tkReader.setTkaReader(tkaReaderArray);

			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map already present", Status.OK.getStatusCode(), resp.getStatus());
			
			// duplicate kc map with replace true shuld be created
			tkReader = new TKReader_Required_Optional();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(true);
			tkaReaderArray = new TKAReader_Required_Optional[1];

			tk0 = new TKAReader_Required_Optional();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("1");
			tkaReaderArray[0] = tk0;


			tkReader.setTkaReader(tkaReaderArray);

			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map already present", Status.CREATED.getStatusCode(), resp.getStatus());

		} finally {
			Utilities.clearDatabase();
			Utilities.setJUnitTest(false);
		}
	}

}

