package com.asu.seatr.api.analyzer.bkt;


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

import com.asu.seatr.api.bkt.CourseAPI_BKT;
import com.asu.seatr.api.bkt.KCAPI_BKT;
import com.asu.seatr.api.bkt.TaskAPI_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.CAReader_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.KAReader_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.TAReader_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.TKAReader_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.TKReader_BKT;
import com.asu.seatr.utils.Utilities;

public class KCAPI_BKT_Test extends JerseyTest {

	private static String COURSE_URL = "analyzer/bkt/courses/";
	private static String KC_URL = "analyzer/bkt/kc/createkc/";
	private static String MAP_KC_URL = "analyzer/bkt/kc/mapkctask/";
	private static String TASK_URL = "analyzer/bkt/tasks/";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(KCAPI_BKT.class, CourseAPI_BKT.class,
				TaskAPI_BKT.class);
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
			CAReader_BKT ca = new CAReader_BKT();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
			Response resp = target(COURSE_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kcreader
			KAReader_BKT ka = new KAReader_BKT();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			ka.setInit_p(0.4);
			ka.setUtility(2.0);
			ka.setLearning_rate(0.2);
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
			CAReader_BKT ca = new CAReader_BKT();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
			Response resp = target(COURSE_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kcreader invalid kc id
			KAReader_BKT ka = new KAReader_BKT();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("");
			ka.setInit_p(0.6);
			ka.setUtility(3.0);
			ka.setLearning_rate(0.1);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc", Status.OK.getStatusCode(), resp.getStatus());

			// create kcreader invalid course id
			ka = new KAReader_BKT();
			ka.setExternal_course_id("");
			ka.setExternal_kc_id("2");
			ka.setInit_p(0.6);
			ka.setUtility(3.0);
			ka.setLearning_rate(0.5);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc ", Status.OK.getStatusCode(), resp.getStatus());

			// create kc
			ka = new KAReader_BKT();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			ka.setInit_p(0.6);
			ka.setUtility(3.0);
			ka.setLearning_rate(0.2);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc ", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kcreader duplicate
			ka = new KAReader_BKT();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			ka.setInit_p(0.6);
			ka.setUtility(3.0);
			ka.setLearning_rate(0.3);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc ", Status.OK.getStatusCode(), resp.getStatus());

			// create kcreader with non existing course id
			ka = new KAReader_BKT();
			ka.setExternal_course_id("2");
			ka.setExternal_kc_id("3");
			ka.setInit_p(0.6);
			ka.setUtility(3.0);
			ka.setLearning_rate(0.4);
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
			CAReader_BKT ca = new CAReader_BKT();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
			ca.setDescription("physics");
			Response resp = target(COURSE_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kc
			KAReader_BKT ka = new KAReader_BKT();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			ka.setInit_p(0.3);
			ka.setUtility(1.0);
			ka.setLearning_rate(0.6);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kcreader
			ka = new KAReader_BKT();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("2");
			ka.setUtility(2.0);
			ka.setInit_p(0.4);
			ka.setLearning_rate(0.1);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			TAReader_BKT ta = new TAReader_BKT();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setType("multiple-choice");
			ta.setDifficulty(0.2);
			resp = target(TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			ta = new TAReader_BKT();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("2");
			ta.setType("typed-input");
			ta.setDifficulty(0.5);
			resp = target(TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// map kc
			TKReader_BKT tkReader = new TKReader_BKT();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(true);
			TKAReader_BKT tkaReaderArray[] = new TKAReader_BKT[2];

			TKAReader_BKT tk0 = new TKAReader_BKT();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("1");
			tkaReaderArray[0] = tk0;

			TKAReader_BKT tk1 = new TKAReader_BKT();
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
			CAReader_BKT ca = new CAReader_BKT();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
			Response resp = target(COURSE_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kc
			KAReader_BKT ka = new KAReader_BKT();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			ka.setInit_p(0.6);
			ka.setUtility(3.0);
			ka.setLearning_rate(0.1);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kcreader
			ka = new KAReader_BKT();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("2");
			ka.setInit_p(0.6);
			ka.setUtility(3.0);
			ka.setLearning_rate(0.2);
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			TAReader_BKT ta = new TAReader_BKT();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			ta.setDifficulty(0.8);
			ta.setType("self-report");
			resp = target(TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			ta = new TAReader_BKT();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("2");
			ta.setDifficulty(0.2);
			ta.setType("structure-input");
			resp = target(TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// map kc
			TKReader_BKT tkReader = new TKReader_BKT();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(true);
			TKAReader_BKT tkaReaderArray[] = new TKAReader_BKT[2];

			TKAReader_BKT tk0 = new TKAReader_BKT();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("1");
			tkaReaderArray[0] = tk0;

			TKAReader_BKT tk1 = new TKAReader_BKT();
			tk1.setExternal_kc_id("2");
			tk1.setExternal_task_id("2");
			tkaReaderArray[1] = tk1;

			tkReader.setTkaReader(tkaReaderArray);

			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());

			// duplicate kc map with replace false should lead to error
			tkReader = new TKReader_BKT();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(false);
			tkaReaderArray = new TKAReader_BKT[1];

			tk0 = new TKAReader_BKT();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("1");
			tkaReaderArray[0] = tk0;


			tkReader.setTkaReader(tkaReaderArray);

			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map already present", Status.OK.getStatusCode(), resp.getStatus());
			
			// duplicate kc map with replace true should be created
			tkReader = new TKReader_BKT();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(true);
			tkaReaderArray = new TKAReader_BKT[1];

			tk0 = new TKAReader_BKT();
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

