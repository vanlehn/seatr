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

import com.asu.seatr.api.CourseAPI;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.handlers.CourseAnalyzerHandler;
import com.asu.seatr.handlers.CourseAnalyzerMapHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.analyzers.course.C_A1;
import com.asu.seatr.models.interfaces.CourseAnalyzerI;
import com.asu.seatr.rest.models.CAReader1;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@PrepareForTest({CourseAPI.class, CourseAnalyzerMapHandler.class, C_A1.class, 
	CourseAnalyzerHandler.class})
@RunWith(PowerMockRunner.class)

public class CourseAPITest extends JerseyTest {
	@Override
    protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(CourseAPI.class);
    }
	
	@Test
	public void getCourseTest_Success() throws CourseException {
		Course course = new Course();
		course.setDescription("description");
		course.setExternal_id("35");
		course.setId(1);
		
		C_A1 ca1 = new C_A1();
		ca1.setId(1);
		ca1.setCourse(course);
		ca1.setTeaching_unit("second");
		ca1.setThreshold(45.34);
		
		List<CourseAnalyzerI> ca1list = new ArrayList<CourseAnalyzerI>();
		ca1list.add(ca1);
		
		PowerMockito.mockStatic(CourseAnalyzerHandler.class);		
		PowerMockito.when(CourseAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString()))
			.thenReturn(ca1list);
		
		final CAReader1 resp =  target("courses/1")
        		.queryParam("external_course_id", "35").request()
        		.get(CAReader1.class);
		
		assertEquals(new String("35"), resp.getExternal_course_id());
		assertEquals(new String("second"), resp.getTeaching_unit());
		assertEquals(new Double(45.34), resp.getThreshold());
		assertEquals(new String("description"), resp.getDescription());
	}
	
	@Test
	public void getCourseTest_FailsWithCourseNotFound() throws CourseException {
		Course course = new Course();
		course.setDescription("description");
		course.setExternal_id("35");
		course.setId(1);
		
		C_A1 ca1 = new C_A1();
		ca1.setId(1);
		ca1.setCourse(course);
		ca1.setTeaching_unit("second");
		ca1.setThreshold(45.34);
		
		List<CourseAnalyzerI> ca1list = new ArrayList<CourseAnalyzerI>();
		ca1list.add(ca1);
		
		PowerMockito.mockStatic(CourseAnalyzerHandler.class);		
		PowerMockito.when(CourseAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString()))
			.thenThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND));
		
		final Response resp =  target("courses/1")
        		.queryParam("external_course_id", "35").request()
        		.get(Response.class);
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void getCourseTest_FailsWithCourseAnalyzerNotFound() throws CourseException {
		Course course = new Course();
		course.setDescription("description");
		course.setExternal_id("35");
		course.setId(1);
		
		C_A1 ca1 = new C_A1();
		ca1.setId(1);
		ca1.setCourse(course);
		ca1.setTeaching_unit("second");
		ca1.setThreshold(45.34);
		
		List<CourseAnalyzerI> ca1list = new ArrayList<CourseAnalyzerI>();
		ca1list.add(ca1);
		
		PowerMockito.mockStatic(CourseAnalyzerHandler.class);		
		PowerMockito.when(CourseAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString()))
			.thenThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_ANALYZER_NOT_FOUND));
		
		final Response resp =  target("courses/1")
        		.queryParam("external_course_id", "35").request()
        		.get(Response.class);
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_ANALYZER_NOT_FOUND), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void createCourseTest_Success() throws Exception {
		C_A1 ca1 = Mockito.mock(C_A1.class);
		PowerMockito.whenNew(C_A1.class).withNoArguments().thenReturn(ca1);
		
		Mockito.stubVoid(ca1).toReturn().on().createCourse(Mockito.anyString(), Mockito.anyString());
		PowerMockito.mockStatic(CourseAnalyzerHandler.class);
		PowerMockito.when(CourseAnalyzerHandler.save((CourseAnalyzerI)Mockito.anyObject()))
			.thenReturn(ca1);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_course_id", "35");
		data.put("teaching_unit", "second");
		data.put("description", "description");
		data.put("threshold", "20");
		
		final Response resp = target("courses/1").request()
					.post(Entity.json(data), Response.class);
		
		PowerMockito.verifyNew(C_A1.class).withNoArguments();
		assertEquals(Status.CREATED.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_CREATED), 
				resp.readEntity(String.class));
		
	}
	
	@Test
	public void createCourseTest_FailsWithPropertyValueNull() throws Exception {
		C_A1 ca1 = Mockito.mock(C_A1.class);
		PowerMockito.whenNew(C_A1.class).withNoArguments().thenReturn(ca1);
		
		Mockito.stubVoid(ca1).toThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_PROPERTY_NULL))
			.on().createCourse(Mockito.anyString(), Mockito.anyString());
		PowerMockito.mockStatic(CourseAnalyzerHandler.class);
		PowerMockito.when(CourseAnalyzerHandler.save((CourseAnalyzerI)Mockito.anyObject()))
			.thenReturn(ca1);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_course_id", "35");
		data.put("teaching_unit", "second");
		data.put("description", "description");
		data.put("threshold", "20");
		
		final Response resp = target("courses/1").request()
					.post(Entity.json(data), Response.class);
		
		PowerMockito.verifyNew(C_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_PROPERTY_NULL), 
				resp.readEntity(String.class));
		
	}
	
	@Test
	public void createCourseTest_FailsWithCourseAnalyzerAlreadyPresent() throws Exception {
		C_A1 ca1 = Mockito.mock(C_A1.class);
		PowerMockito.whenNew(C_A1.class).withNoArguments().thenReturn(ca1);
		
		Mockito.stubVoid(ca1).toReturn().on().createCourse(Mockito.anyString(), Mockito.anyString());

		PowerMockito.mockStatic(CourseAnalyzerHandler.class);
		PowerMockito.when(CourseAnalyzerHandler.save((CourseAnalyzerI)Mockito.anyObject()))
			.thenThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_ANALYZER_ALREADY_PRESENT));
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_course_id", "35");
		data.put("teaching_unit", "second");
		data.put("description", "description");
		data.put("threshold", "20");
		
		final Response resp = target("courses/1").request()
					.post(Entity.json(data), Response.class);
		
		PowerMockito.verifyNew(C_A1.class).withNoArguments();
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_ANALYZER_ALREADY_PRESENT), 
				resp.readEntity(String.class));
		
	}
	
	@Test
	public void updateCourseTest_Success() throws Exception {
		Course course = new Course();
		course.setDescription("description");
		course.setExternal_id("35");
		course.setId(1);
		
		C_A1 ca1 = new C_A1();
		ca1.setId(1);
		ca1.setCourse(course);
		ca1.setTeaching_unit("second");
		ca1.setThreshold(45.34);
		
		List<CourseAnalyzerI> ca1list = new ArrayList<CourseAnalyzerI>();
		ca1list.add(ca1);
		
		PowerMockito.mockStatic(CourseAnalyzerHandler.class);		
		PowerMockito.when(CourseAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString()))
			.thenReturn(ca1list);
		
		PowerMockito.when(CourseAnalyzerHandler.update((CourseAnalyzerI)Mockito.anyObject()))
			.thenReturn(ca1);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_course_id", "35");
		data.put("teaching_unit", "second");
		data.put("description", "description");
		data.put("threshold", "20");
		
		final Response resp = target("courses/1").request()
					.put(Entity.json(data), Response.class);
		
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_UPDATED), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void updateCourseTest_FailsWithCourseNotFound() throws Exception {
		Course course = new Course();
		course.setDescription("description");
		course.setExternal_id("35");
		course.setId(1);
		
		C_A1 ca1 = new C_A1();
		ca1.setId(1);
		ca1.setCourse(course);
		ca1.setTeaching_unit("second");
		ca1.setThreshold(45.34);
		
		List<CourseAnalyzerI> ca1list = new ArrayList<CourseAnalyzerI>();
		ca1list.add(ca1);
		
		PowerMockito.mockStatic(CourseAnalyzerHandler.class);		
		PowerMockito.when(CourseAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString()))
			.thenThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND));
		
		PowerMockito.when(CourseAnalyzerHandler.update((CourseAnalyzerI)Mockito.anyObject()))
			.thenReturn(ca1);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_course_id", "35");
		data.put("teaching_unit", "second");
		data.put("description", "description");
		data.put("threshold", "20");
		
		final Response resp = target("courses/1").request()
					.put(Entity.json(data), Response.class);
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND), 
				resp.readEntity(String.class));
	}
	
	@Test
	public void updateCourseTest_FailsWithCourseAnalyzerNotFound() throws Exception {
		Course course = new Course();
		course.setDescription("description");
		course.setExternal_id("35");
		course.setId(1);
		
		C_A1 ca1 = new C_A1();
		ca1.setId(1);
		ca1.setCourse(course);
		ca1.setTeaching_unit("second");
		ca1.setThreshold(45.34);
		
		List<CourseAnalyzerI> ca1list = new ArrayList<CourseAnalyzerI>();
		ca1list.add(ca1);
		
		PowerMockito.mockStatic(CourseAnalyzerHandler.class);		
		PowerMockito.when(CourseAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString()))
			.thenThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_ANALYZER_NOT_FOUND));
		
		PowerMockito.when(CourseAnalyzerHandler.update((CourseAnalyzerI)Mockito.anyObject()))
			.thenReturn(ca1);
		
		Map<String, String> data = new HashMap<String, String>();
		data.put("external_course_id", "35");
		data.put("teaching_unit", "second");
		data.put("description", "description");
		data.put("threshold", "20");
		
		final Response resp = target("courses/1").request()
					.put(Entity.json(data), Response.class);
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_ANALYZER_NOT_FOUND), 
				resp.readEntity(String.class));
	}	
	
	@Test
	public void deleteCourseTest_Success() throws Exception {
		Course course = new Course();
		course.setDescription("description");
		course.setExternal_id("35");
		course.setId(1);
		
		C_A1 ca1 = new C_A1();
		ca1.setId(1);
		ca1.setCourse(course);
		ca1.setTeaching_unit("second");
		ca1.setThreshold(45.34);
		
		List<CourseAnalyzerI> ca1list = new ArrayList<CourseAnalyzerI>();
		ca1list.add(ca1);
		
		PowerMockito.mockStatic(CourseAnalyzerHandler.class);		
		PowerMockito.when(CourseAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString()))
			.thenReturn(ca1list);
		
		PowerMockito.doNothing().when(CourseAnalyzerHandler.class, "delete", (CourseAnalyzerI)Mockito.anyObject());
		
		final Response resp = target("courses/1")
        		.queryParam("external_course_id", "35").request().delete(Response.class);
		
		assertEquals(Status.OK.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_ANALYZER_DELETED), 
				resp.readEntity(String.class));
		
	}
	
	@Test
	public void deleteCourseTest_FailsWithCourseNotFound() throws Exception {
		Course course = new Course();
		course.setDescription("description");
		course.setExternal_id("35");
		course.setId(1);
		
		C_A1 ca1 = new C_A1();
		ca1.setId(1);
		ca1.setCourse(course);
		ca1.setTeaching_unit("second");
		ca1.setThreshold(45.34);
		
		List<CourseAnalyzerI> ca1list = new ArrayList<CourseAnalyzerI>();
		ca1list.add(ca1);
		
		PowerMockito.mockStatic(CourseAnalyzerHandler.class);		
		PowerMockito.when(CourseAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString()))
		.thenThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND));
		
		PowerMockito.doNothing().when(CourseAnalyzerHandler.class, "delete", (CourseAnalyzerI)Mockito.anyObject());
		
		final Response resp = target("courses/1")
        		.queryParam("external_course_id", "35").request().delete(Response.class);
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND), 
				resp.readEntity(String.class));
		
	}
	
	@Test
	public void deleteCourseTest_FailsWithCourseAnalyzerNotFound() throws Exception {
		Course course = new Course();
		course.setDescription("description");
		course.setExternal_id("35");
		course.setId(1);
		
		C_A1 ca1 = new C_A1();
		ca1.setId(1);
		ca1.setCourse(course);
		ca1.setTeaching_unit("second");
		ca1.setThreshold(45.34);
		
		List<CourseAnalyzerI> ca1list = new ArrayList<CourseAnalyzerI>();
		ca1list.add(ca1);
		
		PowerMockito.mockStatic(CourseAnalyzerHandler.class);		
		PowerMockito.when(CourseAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString()))
		.thenThrow(new CourseException(MyStatus.ERROR, MyMessage.COURSE_ANALYZER_NOT_FOUND));
		
		PowerMockito.doNothing().when(CourseAnalyzerHandler.class, "delete", (CourseAnalyzerI)Mockito.anyObject());
		
		final Response resp = target("courses/1")
        		.queryParam("external_course_id", "35").request().delete(Response.class);
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), resp.getStatus());		
		assertEquals(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_ANALYZER_NOT_FOUND), 
				resp.readEntity(String.class));
		
	}
}
