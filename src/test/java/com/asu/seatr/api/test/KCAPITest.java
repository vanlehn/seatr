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
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.asu.seatr.api.analyzer.unansweredtasks.KCAPI_UnansweredTasks;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.KCException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.KCAnalyzerHandler;
import com.asu.seatr.handlers.KnowledgeComponentHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.handlers.TaskKCAnalyzerHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.kc.KC_UnansweredTasks;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_UnansweredTasks;
import com.asu.seatr.models.interfaces.KCAnalyzerI;
import com.asu.seatr.models.interfaces.TaskKCAnalyzerI;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;;

@SuppressWarnings({ "rawtypes", "deprecation" })
@PrepareForTest({KC_UnansweredTasks.class, KCAnalyzerHandler.class, KCAPI_UnansweredTasks.class
	, KnowledgeComponentHandler.class, TaskHandler.class, TaskKC_UnansweredTasks.class, TaskKCAnalyzerHandler.class, CourseHandler.class,KCAnalyzerHandler.class,Session.class,Transaction.class})
@RunWith(PowerMockRunner.class)
public class KCAPITest extends JerseyTest {

	private static String ANALYZER1_CREATEKC_URL="analyzer/1/kc/createkc/";
	private static String ANALYZER1_MAPKC_URL="analyzer/1/kc/mapkctask";

	@Override
	protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(KCAPI_UnansweredTasks.class);
	}

	@Test
	public void createKCTest_Success() throws Exception {
		KC_UnansweredTasks ka1 = Mockito.mock(KC_UnansweredTasks.class);
		Mockito.stubVoid(ka1).toReturn().on().createKC(Mockito.anyString(), Mockito.anyString());
		PowerMockito.whenNew(KC_UnansweredTasks.class).withNoArguments().thenReturn(ka1);
		PowerMockito.mockStatic(KCAnalyzerHandler.class);
		PowerMockito.when(KCAnalyzerHandler.save((KCAnalyzerI)Mockito.anyObject())).thenReturn(ka1);

		Map<String, String> data = new HashMap<String, String>();
		data.put("external_kc_id", "10");
		data.put("external_course_id", "35");
		data.put("s_unit", "45");

		final Response resp  = target(ANALYZER1_CREATEKC_URL)
				.request().post(Entity.json(data), Response.class);

		PowerMockito.verifyNew(KC_UnansweredTasks.class).withNoArguments();
		assertEquals(Status.CREATED.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.KC_CREATED), 
				resp.readEntity(String.class));

	}

	@Test
	public void createKCTest_FailsWithCourseNotFound() throws Exception {
		KC_UnansweredTasks ka1 = Mockito.mock(KC_UnansweredTasks.class);
		Mockito.stubVoid(ka1).toThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND))
		.on().createKC(Mockito.anyString(), Mockito.anyString());
		PowerMockito.whenNew(KC_UnansweredTasks.class).withNoArguments().thenReturn(ka1);		
		PowerMockito.mockStatic(KCAnalyzerHandler.class);
		PowerMockito.when(KCAnalyzerHandler.save((KCAnalyzerI)Mockito.anyObject())).thenReturn(ka1);

		Map<String, String> data = new HashMap<String, String>();
		data.put("external_kc_id", "10");
		data.put("external_course_id", "35");
		data.put("s_unit", "45");

		final Response resp  = target(ANALYZER1_CREATEKC_URL)
				.request().post(Entity.json(data), Response.class);

		PowerMockito.verifyNew(KC_UnansweredTasks.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND), 
				resp.readEntity(String.class));

	}

	@Test
	public void createKCTest_FailsWithKCAlreadyPresent() throws Exception {
		KC_UnansweredTasks ka1 = Mockito.mock(KC_UnansweredTasks.class);
		Mockito.stubVoid(ka1).toThrow(new KCException(MyStatus.ERROR, MyMessage.KC_ALREADY_PRESENT))
		.on().createKC(Mockito.anyString(), Mockito.anyString());
		PowerMockito.whenNew(KC_UnansweredTasks.class).withNoArguments().thenReturn(ka1);		
		PowerMockito.mockStatic(KCAnalyzerHandler.class);
		PowerMockito.when(KCAnalyzerHandler.save((KCAnalyzerI)Mockito.anyObject())).thenReturn(ka1);

		Map<String, String> data = new HashMap<String, String>();
		data.put("external_kc_id", "10");
		data.put("external_course_id", "35");
		data.put("s_unit", "45");

		final Response resp  = target(ANALYZER1_CREATEKC_URL)
				.request().post(Entity.json(data), Response.class);

		PowerMockito.verifyNew(KC_UnansweredTasks.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.KC_ALREADY_PRESENT), 
				resp.readEntity(String.class));

	}

	@Test
	public void createKCTest_FailsWithKCPropertyNull() throws Exception {
		KC_UnansweredTasks ka1 = Mockito.mock(KC_UnansweredTasks.class);
		Mockito.stubVoid(ka1).toThrow(new KCException(MyStatus.ERROR, MyMessage.KC_PROPERTY_NULL))
		.on().createKC(Mockito.anyString(), Mockito.anyString());
		PowerMockito.whenNew(KC_UnansweredTasks.class).withNoArguments().thenReturn(ka1);		
		PowerMockito.mockStatic(KCAnalyzerHandler.class);
		PowerMockito.when(KCAnalyzerHandler.save((KCAnalyzerI)Mockito.anyObject())).thenReturn(ka1);

		Map<String, String> data = new HashMap<String, String>();
		data.put("external_kc_id", "10");
		data.put("external_course_id", "35");
		data.put("s_unit", "45");

		final Response resp  = target(ANALYZER1_CREATEKC_URL)
				.request().post(Entity.json(data), Response.class);

		PowerMockito.verifyNew(KC_UnansweredTasks.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.KC_PROPERTY_NULL), 
				resp.readEntity(String.class));

	}

	@Test
	public void mapKcTaskTest_with_Replace_true_Success() throws Exception {
		TaskKC_UnansweredTasks tka1 = Mockito.mock(TaskKC_UnansweredTasks.class);
		PowerMockito.whenNew(TaskKC_UnansweredTasks.class).withNoArguments().thenReturn(tka1);

		//Integer affectedRows = new Integer(2);
		//PowerMockito.mockStatic(Handler.class);
		//PowerMockito.when(Handler.hqlTruncate(Mockito.anyString())).thenReturn(affectedRows);
		List<Course> courseList = new ArrayList<Course>();
		PowerMockito.mockStatic(CourseHandler.class);
		PowerMockito.when(CourseHandler.getCourseList(Matchers.anySetOf(String.class))).thenReturn(courseList);

		Session mockedSession = Mockito.mock(Session.class);
		Transaction mockedTransaction = Mockito.mock(Transaction.class);
		PowerMockito.mockStatic(KCAnalyzerHandler.class);
		PowerMockito.when(KCAnalyzerHandler.hqlBatchDeleteByCourse(Mockito.anyString(), Matchers.anyListOf(Course.class), Mockito.anyBoolean())).thenReturn(mockedSession);

		PowerMockito.mockStatic(KnowledgeComponentHandler.class);
		KnowledgeComponent kc = Mockito.mock(KnowledgeComponent.class);
		PowerMockito.when(KnowledgeComponentHandler.readByExtId(Mockito.anyString(), Mockito.anyString())).thenReturn(kc);

		PowerMockito.mockStatic(TaskHandler.class);
		Task task = Mockito.mock(Task.class);
		PowerMockito.when(TaskHandler.readByExtId(Mockito.anyString(),Mockito.anyString())).thenReturn(task);

		PowerMockito.mockStatic(TaskKCAnalyzerHandler.class);
		PowerMockito.when(TaskKCAnalyzerHandler.batchSave(Mockito.any(TaskKCAnalyzerI[].class),Mockito.anyBoolean(),Mockito.any(Session.class))).thenReturn(mockedSession);

		Mockito.when(mockedSession.beginTransaction()).thenReturn(mockedTransaction);
		Mockito.when(mockedSession.getTransaction()).thenReturn(mockedTransaction);
		Mockito.doNothing().when(mockedSession).flush();
		Mockito.doNothing().when(mockedSession).close();
		Mockito.doNothing().when(mockedTransaction).commit();
		Mockito.doNothing().when(mockedTransaction).rollback();

		Map<String, Object> requestMessage = new HashMap<String, Object>();
		requestMessage.put("replace", "true");
		Map<String,String> data1 = new HashMap<String,String>();
		data1.put("external_kc_id", "211");
		data1.put("external_course_id", "36");
		data1.put("external_task_id", "45");
		data1.put("min_mastery_level", "10");
		Map<String,String> data2 = new HashMap<String,String>();
		data2.put("external_kc_id", "211");
		data2.put("external_course_id", "36");
		data2.put("external_task_id", "45");
		data2.put("min_mastery_level", "10");
		Map dataArray[] = new Map[2];
		dataArray[0] = data1;
		dataArray[1]= data2;
		requestMessage.put("tkaReader", dataArray);

		final Response resp  = target(ANALYZER1_MAPKC_URL)
				.request().post(Entity.json(requestMessage), Response.class);

		//PowerMockito.verifyNew(TK_A1.class).withNoArguments();
		assertEquals(Status.CREATED.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.KC_TASK_CREATED), 
				resp.readEntity(String.class));

	}

	@Test
	public void mapKcTaskTest_with_Replace_false_Success() throws Exception {
		TaskKC_UnansweredTasks tka1 = Mockito.mock(TaskKC_UnansweredTasks.class);
		PowerMockito.whenNew(TaskKC_UnansweredTasks.class).withNoArguments().thenReturn(tka1);

		Session mockedSession = Mockito.mock(Session.class);
		Transaction mockedTransaction = Mockito.mock(Transaction.class);

		PowerMockito.mockStatic(KnowledgeComponentHandler.class);
		KnowledgeComponent kc = Mockito.mock(KnowledgeComponent.class);
		PowerMockito.when(KnowledgeComponentHandler.readByExtId(Mockito.anyString(), Mockito.anyString())).thenReturn(kc);

		PowerMockito.mockStatic(TaskHandler.class);
		Task task = Mockito.mock(Task.class);
		PowerMockito.when(TaskHandler.readByExtId(Mockito.anyString(),Mockito.anyString())).thenReturn(task);

		PowerMockito.mockStatic(TaskKCAnalyzerHandler.class);
		PowerMockito.when(TaskKCAnalyzerHandler.batchSave(Mockito.any(TaskKCAnalyzerI[].class),Mockito.anyBoolean(),Mockito.any(Session.class))).thenReturn(mockedSession);

		Mockito.when(mockedSession.beginTransaction()).thenReturn(mockedTransaction);
		Mockito.when(mockedSession.getTransaction()).thenReturn(mockedTransaction);
		Mockito.doNothing().when(mockedSession).flush();
		Mockito.doNothing().when(mockedSession).close();
		Mockito.doNothing().when(mockedTransaction).commit();
		Mockito.doNothing().when(mockedTransaction).rollback();

		Map<String, Object> requestMessage = new HashMap<String, Object>();
		requestMessage.put("replace", "false");
		Map<String,String> data1 = new HashMap<String,String>();
		data1.put("external_kc_id", "211");
		data1.put("external_course_id", "36");
		data1.put("external_task_id", "45");
		data1.put("min_mastery_level", "10");
		Map<String,String> data2 = new HashMap<String,String>();
		data2.put("external_kc_id", "211");
		data2.put("external_course_id", "36");
		data2.put("external_task_id", "45");
		data2.put("min_mastery_level", "10");
		Map dataArray[] = new Map[2];
		dataArray[0] = data1;
		dataArray[1]= data2;
		requestMessage.put("tkaReader", dataArray);

		final Response resp  = target(ANALYZER1_MAPKC_URL)
				.request().post(Entity.json(requestMessage), Response.class);

		//PowerMockito.verifyNew(TK_A1.class).withNoArguments();
		assertEquals(Status.CREATED.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.KC_TASK_CREATED), 
				resp.readEntity(String.class));

	}

	@Test
	public void mapKcTaskTest_FailsWithCourseNotFound() throws Exception {
		TaskKC_UnansweredTasks tka1 = Mockito.mock(TaskKC_UnansweredTasks.class);
		PowerMockito.whenNew(TaskKC_UnansweredTasks.class).withNoArguments().thenReturn(tka1);

		//Integer affectedRows = new Integer(2);
		//PowerMockito.mockStatic(Handler.class);
		//PowerMockito.when(Handler.hqlTruncate(Mockito.anyString())).thenReturn(affectedRows);
		List<Course> courseList = new ArrayList<Course>();
		PowerMockito.mockStatic(CourseHandler.class);
		PowerMockito.when(CourseHandler.getCourseList(Matchers.anySetOf(String.class))).thenReturn(courseList);

		Session mockedSession = Mockito.mock(Session.class);
		Transaction mockedTransaction = Mockito.mock(Transaction.class);
		PowerMockito.mockStatic(KCAnalyzerHandler.class);
		PowerMockito.when(KCAnalyzerHandler.hqlBatchDeleteByCourse(Mockito.anyString(), Matchers.anyListOf(Course.class), Mockito.anyBoolean())).thenReturn(mockedSession);


		PowerMockito.mockStatic(KnowledgeComponentHandler.class);	
		PowerMockito.when(KnowledgeComponentHandler.readByExtId(Mockito.anyString(), Mockito.anyString()))
		.thenThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND));

		PowerMockito.mockStatic(TaskHandler.class);
		Task task = Mockito.mock(Task.class);
		PowerMockito.when(TaskHandler.readByExtId(Mockito.anyString(),Mockito.anyString())).thenReturn(task);

		PowerMockito.mockStatic(TaskKCAnalyzerHandler.class);
		PowerMockito.when(TaskKCAnalyzerHandler.batchSave(Mockito.any(TaskKCAnalyzerI[].class),Mockito.anyBoolean(),Mockito.any(Session.class))).thenReturn(mockedSession);

		Mockito.when(mockedSession.beginTransaction()).thenReturn(mockedTransaction);
		Mockito.when(mockedSession.getTransaction()).thenReturn(mockedTransaction);
		Mockito.doNothing().when(mockedSession).flush();
		Mockito.doNothing().when(mockedSession).close();
		Mockito.doNothing().when(mockedTransaction).commit();
		Mockito.doNothing().when(mockedTransaction).rollback();

		Map<String, Object> requestMessage = new HashMap<String, Object>();
		requestMessage.put("replace", "true");
		Map<String,String> data1 = new HashMap<String,String>();
		data1.put("external_kc_id", "211");
		data1.put("external_course_id", "36");
		data1.put("external_task_id", "45");
		data1.put("min_mastery_level", "10");
		Map<String,String> data2 = new HashMap<String,String>();
		data2.put("external_kc_id", "211");
		data2.put("external_course_id", "36");
		data2.put("external_task_id", "45");
		data2.put("min_mastery_level", "10");
		Map dataArray[] = new Map[2];
		dataArray[0] = data1;
		dataArray[1]= data2;
		requestMessage.put("tkaReader", dataArray);

		final Response resp  = target(ANALYZER1_MAPKC_URL)
				.request().post(Entity.json(requestMessage), Response.class);

		//PowerMockito.verifyNew(TK_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND), 
				resp.readEntity(String.class));

	}

	@Test
	public void mapKcTaskTest_FailsWithKCNotFound() throws Exception {
		TaskKC_UnansweredTasks tka1 = Mockito.mock(TaskKC_UnansweredTasks.class);
		PowerMockito.whenNew(TaskKC_UnansweredTasks.class).withNoArguments().thenReturn(tka1);

		//Integer affectedRows = new Integer(2);
		//PowerMockito.mockStatic(Handler.class);
		//PowerMockito.when(Handler.hqlTruncate(Mockito.anyString())).thenReturn(affectedRows);
		List<Course> courseList = new ArrayList<Course>();
		PowerMockito.mockStatic(CourseHandler.class);
		PowerMockito.when(CourseHandler.getCourseList(Matchers.anySetOf(String.class))).thenReturn(courseList);

		Session mockedSession = Mockito.mock(Session.class);
		Transaction mockedTransaction = Mockito.mock(Transaction.class);
		PowerMockito.mockStatic(KCAnalyzerHandler.class);
		PowerMockito.when(KCAnalyzerHandler.hqlBatchDeleteByCourse(Mockito.anyString(), Matchers.anyListOf(Course.class), Mockito.anyBoolean())).thenReturn(mockedSession);


		PowerMockito.mockStatic(KnowledgeComponentHandler.class);
		PowerMockito.when(KnowledgeComponentHandler.readByExtId(Mockito.anyString(), Mockito.anyString()))
		.thenThrow(new KCException(MyStatus.ERROR, MyMessage.KC_NOT_FOUND));

		PowerMockito.mockStatic(TaskHandler.class);
		Task task = Mockito.mock(Task.class);
		PowerMockito.when(TaskHandler.readByExtId(Mockito.anyString(),Mockito.anyString())).thenReturn(task);

		PowerMockito.mockStatic(TaskKCAnalyzerHandler.class);
		PowerMockito.when(TaskKCAnalyzerHandler.batchSave(Mockito.any(TaskKCAnalyzerI[].class),Mockito.anyBoolean(),Mockito.any(Session.class))).thenReturn(mockedSession);

		Mockito.when(mockedSession.beginTransaction()).thenReturn(mockedTransaction);
		Mockito.when(mockedSession.getTransaction()).thenReturn(mockedTransaction);
		Mockito.doNothing().when(mockedSession).flush();
		Mockito.doNothing().when(mockedSession).close();
		Mockito.doNothing().when(mockedTransaction).commit();
		Mockito.doNothing().when(mockedTransaction).rollback();

		Map<String, Object> requestMessage = new HashMap<String, Object>();
		requestMessage.put("replace", "true");
		Map<String,String> data1 = new HashMap<String,String>();
		data1.put("external_kc_id", "211");
		data1.put("external_course_id", "36");
		data1.put("external_task_id", "45");
		data1.put("min_mastery_level", "10");
		Map<String,String> data2 = new HashMap<String,String>();
		data2.put("external_kc_id", "211");
		data2.put("external_course_id", "36");
		data2.put("external_task_id", "45");
		data2.put("min_mastery_level", "10");
		Map dataArray[] = new Map[2];
		dataArray[0] = data1;
		dataArray[1]= data2;
		requestMessage.put("tkaReader", dataArray);

		final Response resp  = target(ANALYZER1_MAPKC_URL)
				.request().post(Entity.json(requestMessage), Response.class);

		//PowerMockito.verifyNew(TK_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.KC_NOT_FOUND), 
				resp.readEntity(String.class));

	}

	@Test
	public void mapKcTaskTest_FailsWithTaskNotFound() throws Exception {
		TaskKC_UnansweredTasks tka1 = Mockito.mock(TaskKC_UnansweredTasks.class);
		PowerMockito.whenNew(TaskKC_UnansweredTasks.class).withNoArguments().thenReturn(tka1);

		//Integer affectedRows = new Integer(2);
		//PowerMockito.mockStatic(Handler.class);
		//PowerMockito.when(Handler.hqlTruncate(Mockito.anyString())).thenReturn(affectedRows);
		List<Course> courseList = new ArrayList<Course>();
		PowerMockito.mockStatic(CourseHandler.class);
		PowerMockito.when(CourseHandler.getCourseList(Matchers.anySetOf(String.class))).thenReturn(courseList);

		Session mockedSession = Mockito.mock(Session.class);
		Transaction mockedTransaction = Mockito.mock(Transaction.class);
		PowerMockito.mockStatic(KCAnalyzerHandler.class);
		PowerMockito.when(KCAnalyzerHandler.hqlBatchDeleteByCourse(Mockito.anyString(), Matchers.anyListOf(Course.class), Mockito.anyBoolean())).thenReturn(mockedSession);


		PowerMockito.mockStatic(KnowledgeComponentHandler.class);
		KnowledgeComponent kc = Mockito.mock(KnowledgeComponent.class);
		PowerMockito.when(KnowledgeComponentHandler.readByExtId(Mockito.anyString(), Mockito.anyString())).thenReturn(kc);

		PowerMockito.mockStatic(TaskHandler.class);
		PowerMockito.when(TaskHandler.readByExtId(Mockito.anyString(),Mockito.anyString()))
		.thenThrow(new TaskException(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND));

		PowerMockito.mockStatic(TaskKCAnalyzerHandler.class);
		PowerMockito.when(TaskKCAnalyzerHandler.batchSave(Mockito.any(TaskKCAnalyzerI[].class),Mockito.anyBoolean(),Mockito.any(Session.class))).thenReturn(mockedSession);

		Mockito.when(mockedSession.beginTransaction()).thenReturn(mockedTransaction);
		Mockito.when(mockedSession.getTransaction()).thenReturn(mockedTransaction);
		Mockito.doNothing().when(mockedSession).flush();
		Mockito.doNothing().when(mockedSession).close();
		Mockito.doNothing().when(mockedTransaction).commit();
		Mockito.doNothing().when(mockedTransaction).rollback();

		Map<String, Object> requestMessage = new HashMap<String, Object>();
		requestMessage.put("replace", "true");
		Map<String,String> data1 = new HashMap<String,String>();
		data1.put("external_kc_id", "211");
		data1.put("external_course_id", "36");
		data1.put("external_task_id", "45");
		data1.put("min_mastery_level", "10");
		Map<String,String> data2 = new HashMap<String,String>();
		data2.put("external_kc_id", "211");
		data2.put("external_course_id", "36");
		data2.put("external_task_id", "45");
		data2.put("min_mastery_level", "10");
		Map dataArray[] = new Map[2];
		dataArray[0] = data1;
		dataArray[1]= data2;
		requestMessage.put("tkaReader", dataArray);

		final Response resp  = target(ANALYZER1_MAPKC_URL)
				.request().post(Entity.json(requestMessage), Response.class);

		//PowerMockito.verifyNew(TK_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND), 
				resp.readEntity(String.class));

	}


}
