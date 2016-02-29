package com.asu.seatr.api.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

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

@PrepareForTest({CourseAPI.class, CourseAnalyzerMapHandler.class, C_A1.class, CourseAnalyzerHandler.class})
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
	public void createCourseTest_Success() throws CourseException {
		
	}
}
