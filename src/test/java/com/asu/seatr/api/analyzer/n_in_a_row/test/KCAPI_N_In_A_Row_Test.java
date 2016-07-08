package com.asu.seatr.api.analyzer.n_in_a_row.test;


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

import com.asu.seatr.api.analyzer.n_in_a_row.CourseAPI_N_In_A_Row;
import com.asu.seatr.api.analyzer.n_in_a_row.KCAPI_N_In_A_Row;
import com.asu.seatr.api.analyzer.n_in_a_row.TaskAPI_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.CAReader_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.KAReader_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.TAReader_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.TKAReader_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.TKReader_N_In_A_Row;
import com.asu.seatr.utils.Utilities;

public class KCAPI_N_In_A_Row_Test extends JerseyTest {

	private static String COURSE_URL = "analyzer/n_in_a_row/courses/";
	private static String KC_URL = "analyzer/n_in_a_row/kc/createkc/";
	private static String MAP_KC_URL = "analyzer/n_in_a_row/kc/mapkctask/";
	private static String TASK_URL = "analyzer/n_in_a_row/tasks/";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(KCAPI_N_In_A_Row.class, CourseAPI_N_In_A_Row.class,
				TaskAPI_N_In_A_Row.class);
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
			CAReader_N_In_A_Row ca = new CAReader_N_In_A_Row();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
			Response resp = target(COURSE_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kcreader
			KAReader_N_In_A_Row ka = new KAReader_N_In_A_Row();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
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
			CAReader_N_In_A_Row ca = new CAReader_N_In_A_Row();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
			Response resp = target(COURSE_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kcreader invalid kc id
			KAReader_N_In_A_Row ka = new KAReader_N_In_A_Row();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("");
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc", Status.OK.getStatusCode(), resp.getStatus());

			// create kcreader invalid course id
			ka = new KAReader_N_In_A_Row();
			ka.setExternal_course_id("");
			ka.setExternal_kc_id("2");
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc ", Status.OK.getStatusCode(), resp.getStatus());

			// create kc
			ka = new KAReader_N_In_A_Row();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc ", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kcreader duplicate
			ka = new KAReader_N_In_A_Row();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc ", Status.OK.getStatusCode(), resp.getStatus());

			// create kcreader with non existing course id
			ka = new KAReader_N_In_A_Row();
			ka.setExternal_course_id("2");
			ka.setExternal_kc_id("3");
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
			CAReader_N_In_A_Row ca = new CAReader_N_In_A_Row();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
			ca.setDescription("physics");
			Response resp = target(COURSE_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kc
			KAReader_N_In_A_Row ka = new KAReader_N_In_A_Row();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kcreader
			ka = new KAReader_N_In_A_Row();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("2");
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			TAReader_N_In_A_Row ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			resp = target(TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("2");
			resp = target(TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

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
			CAReader_N_In_A_Row ca = new CAReader_N_In_A_Row();
			ca.setExternal_course_id("1");
			ca.setDescription("physics");
			Response resp = target(COURSE_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ca), Response.class);
			assertEquals("course created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kc
			KAReader_N_In_A_Row ka = new KAReader_N_In_A_Row();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("1");
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create kcreader
			ka = new KAReader_N_In_A_Row();
			ka.setExternal_course_id("1");
			ka.setExternal_kc_id("2");
			resp = target(KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz").post(Entity.json(ka),
					Response.class);
			assertEquals("kc created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			TAReader_N_In_A_Row ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("1");
			resp = target(TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

			// create task
			ta = new TAReader_N_In_A_Row();
			ta.setExternal_course_id("1");
			ta.setExternal_task_id("2");
			resp = target(TASK_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(ta), Response.class);
			assertEquals("task created", Status.CREATED.getStatusCode(), resp.getStatus());

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
			tk1.setExternal_task_id("2");
			tkaReaderArray[1] = tk1;

			tkReader.setTkaReader(tkaReaderArray);

			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map created", Status.CREATED.getStatusCode(), resp.getStatus());

			// duplicate kc map with replace false should lead to error
			tkReader = new TKReader_N_In_A_Row();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(false);
			tkaReaderArray = new TKAReader_N_In_A_Row[1];

			tk0 = new TKAReader_N_In_A_Row();
			tk0.setExternal_kc_id("1");
			tk0.setExternal_task_id("1");
			tkaReaderArray[0] = tk0;


			tkReader.setTkaReader(tkaReaderArray);

			resp = target(MAP_KC_URL).request().header("Authorization", "Basic Y3NlMzEwOmhlbGxvMTIz")
					.post(Entity.json(tkReader), Response.class);
			assertEquals("kc map already present", Status.OK.getStatusCode(), resp.getStatus());
			
			// duplicate kc map with replace true shuld be created
			tkReader = new TKReader_N_In_A_Row();
			tkReader.setExternal_course_id("1");
			tkReader.setReplace(true);
			tkaReaderArray = new TKAReader_N_In_A_Row[1];

			tk0 = new TKAReader_N_In_A_Row();
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

