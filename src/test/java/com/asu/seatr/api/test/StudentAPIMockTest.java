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

import com.asu.seatr.api.StudentAPI;
import com.asu.seatr.exceptions.CourseNotFoundException1;
import com.asu.seatr.exceptions.StudentNotFoundException1;
import com.asu.seatr.handlers.StudentAnalyzerHandler;
import com.asu.seatr.models.analyzers.student.S_A1;
import com.asu.seatr.models.interfaces.StudentAnalyzerI;
import com.asu.seatr.rest.models.SAReader1;

@PrepareForTest({StudentAnalyzerHandler.class})
@RunWith(PowerMockRunner.class)
public class StudentAPIMockTest extends JerseyTest {
	
	@Override
    protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(StudentAPI.class);
    }
	
	@Test	
	public void getStudentTest() throws CourseNotFoundException1, StudentNotFoundException1 {
		S_A1 sa1 = new S_A1();
		sa1.setId(1);
		sa1.setS_placement_score(34.45);
		sa1.setS_year("freshman");
		List<StudentAnalyzerI> sa1List = new ArrayList<StudentAnalyzerI>();
		sa1List.add(sa1);
		PowerMockito.mockStatic(StudentAnalyzerHandler.class);
		PowerMockito.when(StudentAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenReturn(sa1List);
		
		final SAReader1 resp = target("students/1").queryParam("external_student_id", "23")
        		.queryParam("external_course_id", "35").request().get(SAReader1.class);
        assertEquals(new String("23"), resp.getExternal_student_id());
        assertEquals(new String("35"), resp.getExternal_course_id());        
        assertEquals(new Double(34.45), resp.getS_placement_score());
        assertEquals(new String("freshman"), resp.getS_year());          
		
	}
}
