package com.asu.seatr.api.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.asu.seatr.api.CommonAPI;
import com.asu.seatr.api.KCAPI;
import com.asu.seatr.exceptions.AnalyzerException;
import com.asu.seatr.exceptions.CourseAnalyzerMapException;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.KCException;
import com.asu.seatr.handlers.CourseAnalyzerMapHandler;
import com.asu.seatr.handlers.Handler;
import com.asu.seatr.handlers.KCAnalyzerHandler;
import com.asu.seatr.handlers.KnowledgeComponentHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.handlers.TaskKCAnalyzerHandler;
import com.asu.seatr.models.analyzers.kc.K_A1;
import com.asu.seatr.models.analyzers.task_kc.TK_A1;
import com.asu.seatr.models.interfaces.TaskKCAnalyzerI;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

@PrepareForTest({K_A1.class, KCAnalyzerHandler.class, KCAPI.class
	, KnowledgeComponentHandler.class, TaskHandler.class, TK_A1.class, TaskKCAnalyzerHandler.class, Handler.class,
	CourseAnalyzerMapHandler.class, Utilities.class})
@RunWith(PowerMockRunner.class)
public class CommonAPITest extends JerseyTest{
	private static String COPYKCMAP_URL="copykcmap";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(CommonAPI.class);
	}

	@Test
	public void copyKCMapTest_Sucess() throws Exception {
		PowerMockito.mockStatic(CourseAnalyzerMapHandler.class);
		PowerMockito.when(CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(null);

		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.getTKClass(Mockito.anyInt())).thenReturn(TK_A1.class);

		List<TaskKCAnalyzerI> taskKCList = new ArrayList<TaskKCAnalyzerI>();
		TK_A1 tka1 = new TK_A1();
		taskKCList.add(tka1);

		PowerMockito.mockStatic(TaskKCAnalyzerHandler.class);
		PowerMockito.when(TaskKCAnalyzerHandler.readByExtCourseId((Class)Mockito.any(), Mockito.anyString())).thenReturn(taskKCList);
		PowerMockito.doNothing().when(TaskKCAnalyzerHandler.class, "batchSaveOrUpdate", (List<TaskKCAnalyzerI>)Mockito.any());

		Map<String, String> data = new HashMap<String, String>();
		data.put("replace", "true");
		data.put("external_course_id", "37");
		data.put("from_analyzer_id", "1");
		data.put("to_analyzer_id", "2");

		final Response resp = target(COPYKCMAP_URL).request()
				.post(Entity.json(data), Response.class);

		assertEquals(Status.CREATED.getStatusCode(), resp.getStatus());
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.KC_TASK_MAP_COPIED), 
				resp.readEntity(String.class));


	}

	@Test
	public void copyKCMapTest_CourseNotFound() throws Exception {
		PowerMockito.mockStatic(CourseAnalyzerMapHandler.class);
		PowerMockito.when(CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId(Mockito.anyString(), Mockito.anyInt()))
		.thenThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND));

		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.getTKClass(Mockito.anyInt())).thenReturn(TK_A1.class);

		List<TaskKCAnalyzerI> taskKCList = new ArrayList<TaskKCAnalyzerI>();
		TK_A1 tka1 = new TK_A1();
		taskKCList.add(tka1);

		PowerMockito.mockStatic(TaskKCAnalyzerHandler.class);
		PowerMockito.when(TaskKCAnalyzerHandler.readByExtCourseId((Class)Mockito.any(), Mockito.anyString())).thenReturn(taskKCList);
		PowerMockito.doNothing().when(TaskKCAnalyzerHandler.class, "batchSaveOrUpdate", (List<TaskKCAnalyzerI>)Mockito.any());

		Map<String, String> data = new HashMap<String, String>();
		data.put("replace", "true");
		data.put("external_course_id", "37");
		data.put("from_analyzer_id", "1");
		data.put("to_analyzer_id", "2");

		final Response resp = target(COPYKCMAP_URL).request()
				.post(Entity.json(data), Response.class);

		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND), 
				resp.readEntity(String.class));


	}

	@Test
	public void copyKCMapTest_AnalyzerNotFound() throws Exception {
		PowerMockito.mockStatic(CourseAnalyzerMapHandler.class);
		PowerMockito.when(CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId(Mockito.anyString(), Mockito.anyInt()))
		.thenThrow(new AnalyzerException(MyStatus.ERROR, MyMessage.ANALYZER_NOT_FOUND));

		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.getTKClass(Mockito.anyInt())).thenReturn(TK_A1.class);

		List<TaskKCAnalyzerI> taskKCList = new ArrayList<TaskKCAnalyzerI>();
		TK_A1 tka1 = new TK_A1();
		taskKCList.add(tka1);

		PowerMockito.mockStatic(TaskKCAnalyzerHandler.class);
		PowerMockito.when(TaskKCAnalyzerHandler.readByExtCourseId((Class)Mockito.any(), Mockito.anyString())).thenReturn(taskKCList);
		PowerMockito.doNothing().when(TaskKCAnalyzerHandler.class, "batchSaveOrUpdate", (List<TaskKCAnalyzerI>)Mockito.any());

		Map<String, String> data = new HashMap<String, String>();
		data.put("replace", "true");
		data.put("external_course_id", "37");
		data.put("from_analyzer_id", "1");
		data.put("to_analyzer_id", "2");

		final Response resp = target(COPYKCMAP_URL).request()
				.post(Entity.json(data), Response.class);

		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.ANALYZER_NOT_FOUND), 
				resp.readEntity(String.class));


	}

	@Test
	public void copyKCMapTest_CourseAnalyzerMapNotFound() throws Exception {
		PowerMockito.mockStatic(CourseAnalyzerMapHandler.class);
		PowerMockito.when(CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId(Mockito.anyString(), Mockito.anyInt()))
		.thenThrow(new CourseAnalyzerMapException(MyStatus.ERROR, MyMessage.COURSE_ANALYZER_MAP_NOT_FOUND));

		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.getTKClass(Mockito.anyInt())).thenReturn(TK_A1.class);

		List<TaskKCAnalyzerI> taskKCList = new ArrayList<TaskKCAnalyzerI>();
		TK_A1 tka1 = new TK_A1();
		taskKCList.add(tka1);

		PowerMockito.mockStatic(TaskKCAnalyzerHandler.class);
		PowerMockito.when(TaskKCAnalyzerHandler.readByExtCourseId((Class)Mockito.any(), Mockito.anyString())).thenReturn(taskKCList);
		PowerMockito.doNothing().when(TaskKCAnalyzerHandler.class, "batchSaveOrUpdate", (List<TaskKCAnalyzerI>)Mockito.any());

		Map<String, String> data = new HashMap<String, String>();
		data.put("replace", "true");
		data.put("external_course_id", "37");
		data.put("from_analyzer_id", "1");
		data.put("to_analyzer_id", "2");

		final Response resp = target(COPYKCMAP_URL).request()
				.post(Entity.json(data), Response.class);

		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_ANALYZER_MAP_NOT_FOUND), 
				resp.readEntity(String.class));


	}

	@Test
	public void copyKCMapTest_KCNotFoundForCourse() throws Exception {
		PowerMockito.mockStatic(CourseAnalyzerMapHandler.class);
		PowerMockito.when(CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(null);

		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.getTKClass(Mockito.anyInt())).thenReturn(TK_A1.class);

		List<TaskKCAnalyzerI> taskKCList = new ArrayList<TaskKCAnalyzerI>();
		TK_A1 tka1 = new TK_A1();
		taskKCList.add(tka1);

		PowerMockito.mockStatic(TaskKCAnalyzerHandler.class);
		PowerMockito.when(TaskKCAnalyzerHandler.readByExtCourseId((Class)Mockito.any(), Mockito.anyString()))
		.thenThrow(new KCException(MyStatus.ERROR, MyMessage.KC_NOT_FOUND_FOR_COURSE));
		PowerMockito.doNothing().when(TaskKCAnalyzerHandler.class, "batchSaveOrUpdate", (List<TaskKCAnalyzerI>)Mockito.any());

		Map<String, String> data = new HashMap<String, String>();
		data.put("replace", "true");
		data.put("external_course_id", "37");
		data.put("from_analyzer_id", "1");
		data.put("to_analyzer_id", "2");

		final Response resp = target(COPYKCMAP_URL).request()
				.post(Entity.json(data), Response.class);

		assertEquals(Status.OK.getStatusCode(), resp.getStatus());
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.KC_NOT_FOUND_FOR_COURSE), 
				resp.readEntity(String.class));		
	}

}
