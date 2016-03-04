package com.asu.seatr.api.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
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

import com.asu.seatr.api.KCAPI;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.KCException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.KCAnalyzerHandler;
import com.asu.seatr.handlers.KnowledgeComponentHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.handlers.TaskKCAnalyzerHandler;
import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.kc.K_A1;
import com.asu.seatr.models.analyzers.task_kc.TK_A1;
import com.asu.seatr.models.interfaces.KCAnalyzerI;
import com.asu.seatr.models.interfaces.TaskKCAnalyzerI;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;;

@PrepareForTest({K_A1.class, KCAnalyzerHandler.class, KCAPI.class
	, KnowledgeComponentHandler.class, TaskHandler.class, TK_A1.class, TaskKCAnalyzerHandler.class})
@RunWith(PowerMockRunner.class)
public class KCAPITest extends JerseyTest {

	@Override
    protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(KCAPI.class);
    }

	@Test
	public void createKCTest_Success() throws Exception {
		K_A1 ka1 = Mockito.mock(K_A1.class);
		Mockito.stubVoid(ka1).toReturn().on().createKC(Mockito.anyString(), Mockito.anyString());
		PowerMockito.whenNew(K_A1.class).withNoArguments().thenReturn(ka1);
		PowerMockito.mockStatic(KCAnalyzerHandler.class);
		PowerMockito.when(KCAnalyzerHandler.save((KCAnalyzerI)Mockito.anyObject())).thenReturn(ka1);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_kc_id", "10");
		data.put("external_course_id", "35");
		data.put("s_unit", "45");
		
		final Response resp  = target("kc/createkc/1")
				.request().post(Entity.json(data), Response.class);
		
		PowerMockito.verifyNew(K_A1.class).withNoArguments();
		assertEquals(Status.CREATED.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.KC_CREATED), 
				resp.readEntity(String.class));
		
	}
	
	@Test
	public void createKCTest_FailsWithCourseNotFound() throws Exception {
		K_A1 ka1 = Mockito.mock(K_A1.class);
		Mockito.stubVoid(ka1).toThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND))
			.on().createKC(Mockito.anyString(), Mockito.anyString());
		PowerMockito.whenNew(K_A1.class).withNoArguments().thenReturn(ka1);		
		PowerMockito.mockStatic(KCAnalyzerHandler.class);
		PowerMockito.when(KCAnalyzerHandler.save((KCAnalyzerI)Mockito.anyObject())).thenReturn(ka1);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_kc_id", "10");
		data.put("external_course_id", "35");
		data.put("s_unit", "45");
		
		final Response resp  = target("kc/createkc/1")
				.request().post(Entity.json(data), Response.class);
		
		PowerMockito.verifyNew(K_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND), 
				resp.readEntity(String.class));
		
	}
	
	@Test
	public void createKCTest_FailsWithKCAlreadyPresent() throws Exception {
		K_A1 ka1 = Mockito.mock(K_A1.class);
		Mockito.stubVoid(ka1).toThrow(new KCException(MyStatus.ERROR, MyMessage.KC_ALREADY_PRESENT))
			.on().createKC(Mockito.anyString(), Mockito.anyString());
		PowerMockito.whenNew(K_A1.class).withNoArguments().thenReturn(ka1);		
		PowerMockito.mockStatic(KCAnalyzerHandler.class);
		PowerMockito.when(KCAnalyzerHandler.save((KCAnalyzerI)Mockito.anyObject())).thenReturn(ka1);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_kc_id", "10");
		data.put("external_course_id", "35");
		data.put("s_unit", "45");
		
		final Response resp  = target("kc/createkc/1")
				.request().post(Entity.json(data), Response.class);
		
		PowerMockito.verifyNew(K_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.KC_ALREADY_PRESENT), 
				resp.readEntity(String.class));
		
	}
	
	@Test
	public void createKCTest_FailsWithKCPropertyNull() throws Exception {
		K_A1 ka1 = Mockito.mock(K_A1.class);
		Mockito.stubVoid(ka1).toThrow(new KCException(MyStatus.ERROR, MyMessage.KC_PROPERTY_NULL))
			.on().createKC(Mockito.anyString(), Mockito.anyString());
		PowerMockito.whenNew(K_A1.class).withNoArguments().thenReturn(ka1);		
		PowerMockito.mockStatic(KCAnalyzerHandler.class);
		PowerMockito.when(KCAnalyzerHandler.save((KCAnalyzerI)Mockito.anyObject())).thenReturn(ka1);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_kc_id", "10");
		data.put("external_course_id", "35");
		data.put("s_unit", "45");
		
		final Response resp  = target("kc/createkc/1")
				.request().post(Entity.json(data), Response.class);
		
		PowerMockito.verifyNew(K_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.KC_PROPERTY_NULL), 
				resp.readEntity(String.class));
		
	}
	
	@Test
	public void mapKcTaskTest_Success() throws Exception {
		TK_A1 tka1 = Mockito.mock(TK_A1.class);
		PowerMockito.whenNew(TK_A1.class).withNoArguments().thenReturn(tka1);
		
		PowerMockito.mockStatic(KnowledgeComponentHandler.class);
		KnowledgeComponent kc = Mockito.mock(KnowledgeComponent.class);
		PowerMockito.when(KnowledgeComponentHandler.readByExtId(Mockito.anyString(), Mockito.anyString())).thenReturn(kc);
		
		PowerMockito.mockStatic(TaskHandler.class);
		Task task = Mockito.mock(Task.class);
		PowerMockito.when(TaskHandler.readByExtId(Mockito.anyString(),Mockito.anyString())).thenReturn(task);
		
		PowerMockito.mockStatic(TaskKCAnalyzerHandler.class);
		PowerMockito.when(TaskKCAnalyzerHandler.save((TaskKCAnalyzerI)Mockito.anyObject())).thenReturn(tka1);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_kc_id", "10");
		data.put("external_course_id", "35");
		data.put("external_task_id", "25");
		data.put("min_mastery_level", "10");
		
		final Response resp  = target("kc/mapkctask/1")
				.request().post(Entity.json(data), Response.class);
		
		PowerMockito.verifyNew(TK_A1.class).withNoArguments();
		assertEquals(Status.CREATED.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.KC_TASK_CREATED), 
				resp.readEntity(String.class));
		
	}
	
	@Test
	public void mapKcTaskTest_FailsWithCourseNotFound() throws Exception {
		TK_A1 tka1 = Mockito.mock(TK_A1.class);
		PowerMockito.whenNew(TK_A1.class).withNoArguments().thenReturn(tka1);
		
		PowerMockito.mockStatic(KnowledgeComponentHandler.class);
		KnowledgeComponent kc = Mockito.mock(KnowledgeComponent.class);
		PowerMockito.when(KnowledgeComponentHandler.readByExtId(Mockito.anyString(), Mockito.anyString()))
			.thenThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND));
		
		PowerMockito.mockStatic(TaskHandler.class);
		Task task = Mockito.mock(Task.class);
		PowerMockito.when(TaskHandler.readByExtId(Mockito.anyString(),Mockito.anyString())).thenReturn(task);
		
		PowerMockito.mockStatic(TaskKCAnalyzerHandler.class);
		PowerMockito.when(TaskKCAnalyzerHandler.save((TaskKCAnalyzerI)Mockito.anyObject())).thenReturn(tka1);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_kc_id", "10");
		data.put("external_course_id", "35");
		data.put("external_task_id", "25");
		data.put("min_mastery_level", "10");
		
		final Response resp  = target("kc/mapkctask/1")
				.request().post(Entity.json(data), Response.class);
		
		PowerMockito.verifyNew(TK_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND), 
				resp.readEntity(String.class));
		
	}
	
	@Test
	public void mapKcTaskTest_FailsWithKCNotFound() throws Exception {
		TK_A1 tka1 = Mockito.mock(TK_A1.class);
		PowerMockito.whenNew(TK_A1.class).withNoArguments().thenReturn(tka1);
		
		PowerMockito.mockStatic(KnowledgeComponentHandler.class);
		KnowledgeComponent kc = Mockito.mock(KnowledgeComponent.class);
		PowerMockito.when(KnowledgeComponentHandler.readByExtId(Mockito.anyString(), Mockito.anyString()))
			.thenThrow(new KCException(MyStatus.ERROR, MyMessage.KC_NOT_FOUND));
		
		PowerMockito.mockStatic(TaskHandler.class);
		Task task = Mockito.mock(Task.class);
		PowerMockito.when(TaskHandler.readByExtId(Mockito.anyString(),Mockito.anyString())).thenReturn(task);
		
		PowerMockito.mockStatic(TaskKCAnalyzerHandler.class);
		PowerMockito.when(TaskKCAnalyzerHandler.save((TaskKCAnalyzerI)Mockito.anyObject())).thenReturn(tka1);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_kc_id", "10");
		data.put("external_course_id", "35");
		data.put("external_task_id", "25");
		data.put("min_mastery_level", "10");
		
		final Response resp  = target("kc/mapkctask/1")
				.request().post(Entity.json(data), Response.class);
		
		PowerMockito.verifyNew(TK_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.KC_NOT_FOUND), 
				resp.readEntity(String.class));
		
	}
	
	@Test
	public void mapKcTaskTest_FailsWithTaskNotFound() throws Exception {
		TK_A1 tka1 = Mockito.mock(TK_A1.class);
		PowerMockito.whenNew(TK_A1.class).withNoArguments().thenReturn(tka1);
		
		PowerMockito.mockStatic(KnowledgeComponentHandler.class);
		KnowledgeComponent kc = Mockito.mock(KnowledgeComponent.class);
		PowerMockito.when(KnowledgeComponentHandler.readByExtId(Mockito.anyString(), Mockito.anyString())).thenReturn(kc);

		PowerMockito.mockStatic(TaskHandler.class);
		Task task = Mockito.mock(Task.class);
		PowerMockito.when(TaskHandler.readByExtId(Mockito.anyString(),Mockito.anyString()))
			.thenThrow(new TaskException(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND));
		
		PowerMockito.mockStatic(TaskKCAnalyzerHandler.class);
		PowerMockito.when(TaskKCAnalyzerHandler.save((TaskKCAnalyzerI)Mockito.anyObject())).thenReturn(tka1);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_kc_id", "10");
		data.put("external_course_id", "35");
		data.put("external_task_id", "25");
		data.put("min_mastery_level", "10");
		
		final Response resp  = target("kc/mapkctask/1")
				.request().post(Entity.json(data), Response.class);
		
		PowerMockito.verifyNew(TK_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.TASK_NOT_FOUND), 
				resp.readEntity(String.class));
		
	}
	
}
