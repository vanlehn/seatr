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


import com.asu.seatr.api.TaskApi;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.TaskAnalyzerHandler;
import com.asu.seatr.models.analyzers.task.T_A1;
import com.asu.seatr.models.interfaces.TaskAnalyzerI;
import com.asu.seatr.rest.models.TAReader1;

@PrepareForTest({TaskAnalyzerHandler.class})
@RunWith(PowerMockRunner.class)
public class TaskAPIMockTest extends JerseyTest{
	

	@Override
    protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
        return new ResourceConfig(TaskApi.class);
    }
	@Test
	public void getStudentTest() throws CourseException, TaskException
	{
		T_A1 ta1 = new T_A1();
		ta1.setId(1);
		ta1.setS_difficulty_level(10);
		List<TaskAnalyzerI> listta1 = new ArrayList<TaskAnalyzerI>();
		listta1.add(ta1);
		PowerMockito.mockStatic(TaskAnalyzerHandler.class);
		PowerMockito.when(TaskAnalyzerHandler.readByExtId(Mockito.any(Class.class), Mockito.anyString(), Mockito.anyString())).thenReturn(listta1);
		final TAReader1 resp = target("tasks/1").queryParam("external_task_id", "1")
        		.queryParam("external_course_id", "35").request().get(TAReader1.class);
        assertEquals(resp.getExternal_task_id(), new String("1"));
        assertEquals(resp.getExternal_course_id(), new String("35"));        
        assertEquals(resp.getS_difficulty_level(), new Integer(10));
	}
	
}
