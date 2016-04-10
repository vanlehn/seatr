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

import com.asu.seatr.api.StudentAPI_1;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.handlers.StudentAnalyzerHandler;
import com.asu.seatr.models.analyzers.student.S_A1;
import com.asu.seatr.models.interfaces.StudentAnalyzerI;
import com.asu.seatr.rest.models.SAReader1;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@PrepareForTest({S_A1.class, StudentAnalyzerHandler.class, StudentAPI_1.class})
@RunWith(PowerMockRunner.class)
public class StudentAPIMockTest extends JerseyTest {
	
	private static String ANALYZER1_URL="analyzer/1/students";
	
	@Override
    protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(StudentAPI_1.class);
    }
	
	@Test
	public void getStudentTest_Success() throws CourseException, StudentException {
		S_A1 sa1 = new S_A1();
		sa1.setId(1);		
		sa1.setS_placement_score(34.45);
		sa1.setS_year("freshman");
		List<StudentAnalyzerI> sa1List = new ArrayList<StudentAnalyzerI>();
		sa1List.add(sa1);
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);
		PowerMockito.when(StudentAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenReturn(sa1List);
		
		final SAReader1 resp = target(ANALYZER1_URL).queryParam("external_student_id", "23")
        		.queryParam("external_course_id", "35").request().get(SAReader1.class);
        assertEquals(new String("23"), resp.getExternal_student_id());
        assertEquals(new String("35"), resp.getExternal_course_id());        
        assertEquals(new Double(34.45), resp.getS_placement_score());
        assertEquals(new String("freshman"), resp.getS_year());          
		
	}
	
	@Test
	public void getStudentTest_FailsWithCourseNotFound() throws CourseException, StudentException {
		S_A1 sa1 = new S_A1();
		sa1.setId(1);		
		sa1.setS_placement_score(34.45);
		sa1.setS_year("freshman");
		List<StudentAnalyzerI> sa1List = new ArrayList<StudentAnalyzerI>();
		sa1List.add(sa1);
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);
		PowerMockito.when(StudentAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString()))
			.thenThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND));
		
		final Response resp = target(ANALYZER1_URL).queryParam("external_student_id", "23")
        		.queryParam("external_course_id", "35").request().get(Response.class);
        
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND), 
				resp.readEntity(String.class));
        		
	}
	
	@Test
	public void getStudentTest_FailsWithStudentNotFound() throws CourseException, StudentException {
		S_A1 sa1 = new S_A1();
		sa1.setId(1);		
		sa1.setS_placement_score(34.45);
		sa1.setS_year("freshman");
		List<StudentAnalyzerI> sa1List = new ArrayList<StudentAnalyzerI>();
		sa1List.add(sa1);
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);
		PowerMockito.when(StudentAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString()))
			.thenThrow(new CourseException(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND));
		
		final Response resp = target(ANALYZER1_URL).queryParam("external_student_id", "23")
        		.queryParam("external_course_id", "35").request().get(Response.class);
        
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND), 
				resp.readEntity(String.class));
        		
	}
	
	@Test
	public void getStudentTest_FailsWithStudentAnalyzerNotFound() throws CourseException, StudentException {
		S_A1 sa1 = new S_A1();
		sa1.setId(1);		
		sa1.setS_placement_score(34.45);
		sa1.setS_year("freshman");
		List<StudentAnalyzerI> sa1List = new ArrayList<StudentAnalyzerI>();
		sa1List.add(sa1);
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);
		PowerMockito.when(StudentAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString()))
			.thenThrow(new CourseException(MyStatus.ERROR, MyMessage.STUDENT_ANALYZER_NOT_FOUND));
		
		final Response resp = target(ANALYZER1_URL).queryParam("external_student_id", "23")
        		.queryParam("external_course_id", "35").request().get(Response.class);
        
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_ANALYZER_NOT_FOUND), 
				resp.readEntity(String.class));
        		
	}		
	
	@Test
	public void createStudentTest() throws Exception   {
		
		S_A1 s_a1 = PowerMockito.mock(S_A1.class);						
		PowerMockito.whenNew(S_A1.class).withNoArguments().thenReturn(s_a1);
		Mockito.stubVoid(s_a1).toReturn().on().createStudent(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);				
		PowerMockito.when(StudentAnalyzerHandler.save((StudentAnalyzerI)Mockito.anyObject())).thenReturn(s_a1);			
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id","23");
		data.put("external_course_id", "35");
		data.put("s_placement_score", "34.45");
		data.put("s_year", "freshman");
		
		final Response resp  = target(ANALYZER1_URL)
								.request().post(Entity.json(data), Response.class);
		
		
		PowerMockito.verifyNew(S_A1.class).withNoArguments();
		assertEquals(Status.CREATED.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_CREATED), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void createStudentTest_FailsWithStudentPropertyNull() throws Exception   {
		
		S_A1 s_a1 = PowerMockito.mock(S_A1.class);						
		PowerMockito.whenNew(S_A1.class).withNoArguments().thenReturn(s_a1);
		Mockito.stubVoid(s_a1).toThrow(new StudentException(MyStatus.ERROR, MyMessage.STUDENT_PROPERTY_NULL))
		.on().createStudent(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);				
		PowerMockito.when(StudentAnalyzerHandler.save((StudentAnalyzerI)Mockito.anyObject())).thenReturn(s_a1);			
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id","23");
		data.put("external_course_id", "35");
		data.put("s_placement_score", "34.45");
		data.put("s_year", "freshman");
		
		final Response resp  = target(ANALYZER1_URL)
								.request().post(Entity.json(data), Response.class);
		
		
		PowerMockito.verifyNew(S_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_PROPERTY_NULL), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void createStudentTest_FailsWithCourseNotFound() throws Exception   {
		
		S_A1 s_a1 = PowerMockito.mock(S_A1.class);						
		PowerMockito.whenNew(S_A1.class).withNoArguments().thenReturn(s_a1);
		Mockito.stubVoid(s_a1).toThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND))
		.on().createStudent(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);				
		PowerMockito.when(StudentAnalyzerHandler.save((StudentAnalyzerI)Mockito.anyObject())).thenReturn(s_a1);			
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id","23");
		data.put("external_course_id", "35");
		data.put("s_placement_score", "34.45");
		data.put("s_year", "freshman");
		
		final Response resp  = target(ANALYZER1_URL)
								.request().post(Entity.json(data), Response.class);
		
		
		PowerMockito.verifyNew(S_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void createStudentTest_FailsWithStudentAnalyzerAlreadyPresent() throws Exception   {
		
		S_A1 s_a1 = PowerMockito.mock(S_A1.class);						
		PowerMockito.whenNew(S_A1.class).withNoArguments().thenReturn(s_a1);
		Mockito.stubVoid(s_a1).toThrow(new StudentException(MyStatus.ERROR, MyMessage.STUDENT_ANALYZER_ALREADY_PRESENT))
		.on().createStudent(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt());
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);				
		PowerMockito.when(StudentAnalyzerHandler.save((StudentAnalyzerI)Mockito.anyObject())).thenReturn(s_a1);			
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id","23");
		data.put("external_course_id", "35");
		data.put("s_placement_score", "34.45");
		data.put("s_year", "freshman");
		
		final Response resp  = target(ANALYZER1_URL)
								.request().post(Entity.json(data), Response.class);
		
		
		PowerMockito.verifyNew(S_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_ANALYZER_ALREADY_PRESENT), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void updateStudentTest() throws Exception {
		S_A1 sa1 = new S_A1();
		sa1.setId(1);
		sa1.setS_placement_score(34.45);
		sa1.setS_year("freshman");
		List<StudentAnalyzerI> sa1List = new ArrayList<StudentAnalyzerI>();
		sa1List.add(sa1);
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);
		PowerMockito.when(StudentAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenReturn(sa1List);
		
		//PowerMockito.mockStatic(StudentAnalyzerHandler.class);				
		PowerMockito.when(StudentAnalyzerHandler.update((StudentAnalyzerI)Mockito.anyObject())).thenReturn(sa1);			
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id","23");
		data.put("external_course_id", "35");
		data.put("s_placement_score", "34.78");
		data.put("s_year", "sophomore");
		
		final Response resp  = target(ANALYZER1_URL)
								.request().put(Entity.json(data), Response.class);
		
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_UPDATED), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void updateStudentTest_FailsWithCourseNotFound() throws Exception {
		S_A1 sa1 = new S_A1();
		sa1.setId(1);
		sa1.setS_placement_score(34.45);
		sa1.setS_year("freshman");
		List<StudentAnalyzerI> sa1List = new ArrayList<StudentAnalyzerI>();
		sa1List.add(sa1);
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);
		PowerMockito.when(StudentAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND));
		
		//PowerMockito.mockStatic(StudentAnalyzerHandler.class);				
		PowerMockito.when(StudentAnalyzerHandler.update((StudentAnalyzerI)Mockito.anyObject())).thenReturn(sa1);			
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id","23");
		data.put("external_course_id", "35");
		data.put("s_placement_score", "34.78");
		data.put("s_year", "sophomore");
		
		final Response resp  = target(ANALYZER1_URL)
								.request().put(Entity.json(data), Response.class);
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void updateStudentTest_FailsWithStudentNotFound() throws Exception {
		S_A1 sa1 = new S_A1();
		sa1.setId(1);
		sa1.setS_placement_score(34.45);
		sa1.setS_year("freshman");
		List<StudentAnalyzerI> sa1List = new ArrayList<StudentAnalyzerI>();
		sa1List.add(sa1);
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);
		PowerMockito.when(StudentAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new StudentException(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND));
		
		//PowerMockito.mockStatic(StudentAnalyzerHandler.class);				
		PowerMockito.when(StudentAnalyzerHandler.update((StudentAnalyzerI)Mockito.anyObject())).thenReturn(sa1);			
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id","23");
		data.put("external_course_id", "35");
		data.put("s_placement_score", "34.78");
		data.put("s_year", "sophomore");
		
		final Response resp  = target(ANALYZER1_URL)
								.request().put(Entity.json(data), Response.class);
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND), 
				resp.readEntity(String.class));
	}
	
	public void updateStudentTest_FailsWithStudentAnalyzerNotFound() throws Exception {
		S_A1 sa1 = new S_A1();
		sa1.setId(1);
		sa1.setS_placement_score(34.45);
		sa1.setS_year("freshman");
		List<StudentAnalyzerI> sa1List = new ArrayList<StudentAnalyzerI>();
		sa1List.add(sa1);
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);
		PowerMockito.when(StudentAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString()))
				.thenThrow(new StudentException(MyStatus.ERROR, MyMessage.STUDENT_ANALYZER_NOT_FOUND));
		
		//PowerMockito.mockStatic(StudentAnalyzerHandler.class);				
		PowerMockito.when(StudentAnalyzerHandler.update((StudentAnalyzerI)Mockito.anyObject())).thenReturn(sa1);			
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_student_id","23");
		data.put("external_course_id", "35");
		data.put("s_placement_score", "34.78");
		data.put("s_year", "sophomore");
		
		final Response resp  = target(ANALYZER1_URL)
								.request().put(Entity.json(data), Response.class);
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_ANALYZER_NOT_FOUND), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void deleteStudentAnalyzerTest() throws Exception {
		S_A1 sa1 = new S_A1();
		sa1.setId(1);
		sa1.setS_placement_score(34.45);
		sa1.setS_year("freshman");
		List<StudentAnalyzerI> sa1List = new ArrayList<StudentAnalyzerI>();
		sa1List.add(sa1);
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);
		PowerMockito.when(StudentAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenReturn(sa1List);
		PowerMockito.doNothing().when(StudentAnalyzerHandler.class, "delete", (StudentAnalyzerI)Mockito.anyObject());
		
		final Response resp = target(ANALYZER1_URL).queryParam("external_student_id", "23")
        		.queryParam("external_course_id", "35").request().delete(Response.class);
		
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_ANALYZER_DELETED), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void deleteStudentAnalyzerTest_FailsWithCourseNotFound() throws Exception {
		S_A1 sa1 = new S_A1();
		sa1.setId(1);
		sa1.setS_placement_score(34.45);
		sa1.setS_year("freshman");
		List<StudentAnalyzerI> sa1List = new ArrayList<StudentAnalyzerI>();
		sa1List.add(sa1);
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);
		PowerMockito.when(StudentAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString()))
			.thenThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND));
		PowerMockito.doNothing().when(StudentAnalyzerHandler.class, "delete", (StudentAnalyzerI)Mockito.anyObject());
		
		final Response resp = target(ANALYZER1_URL).queryParam("external_student_id", "23")
        		.queryParam("external_course_id", "35").request().delete(Response.class);
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void deleteStudentAnalyzerTest_FailsWithStudentNotFound() throws Exception {
		S_A1 sa1 = new S_A1();
		sa1.setId(1);
		sa1.setS_placement_score(34.45);
		sa1.setS_year("freshman");
		List<StudentAnalyzerI> sa1List = new ArrayList<StudentAnalyzerI>();
		sa1List.add(sa1);
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);
		PowerMockito.when(StudentAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString()))
			.thenThrow(new StudentException(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND));
		PowerMockito.doNothing().when(StudentAnalyzerHandler.class, "delete", (StudentAnalyzerI)Mockito.anyObject());
		
		final Response resp = target(ANALYZER1_URL).queryParam("external_student_id", "23")
        		.queryParam("external_course_id", "35").request().delete(Response.class);
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void deleteStudentAnalyzerTest_FailsWithStudentAnalyzerNotFound() throws Exception {
		S_A1 sa1 = new S_A1();
		sa1.setId(1);
		sa1.setS_placement_score(34.45);
		sa1.setS_year("freshman");
		List<StudentAnalyzerI> sa1List = new ArrayList<StudentAnalyzerI>();
		sa1List.add(sa1);
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);
		PowerMockito.when(StudentAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString()))
			.thenThrow(new StudentException(MyStatus.ERROR, MyMessage.STUDENT_ANALYZER_NOT_FOUND));
		PowerMockito.doNothing().when(StudentAnalyzerHandler.class, "delete", (StudentAnalyzerI)Mockito.anyObject());
		
		final Response resp = target(ANALYZER1_URL).queryParam("external_student_id", "23")
        		.queryParam("external_course_id", "35").request().delete(Response.class);
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_ANALYZER_NOT_FOUND), 
				resp.readEntity(String.class));
	}
}
