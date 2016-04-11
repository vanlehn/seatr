package com.asu.seatr.handlers.analyzer3.test;


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

import static org.junit.Assert.assertEquals;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.asu.seatr.exceptions.RecommException;
import com.asu.seatr.handlers.analyzer3.RecommTaskHandler_3;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.task.T_A3;
import com.asu.seatr.persistence.HibernateUtil;

//@PrepareForTest({SessionFactory.class,HibernateUtil.class,Session.class,Transaction.class,Query.class})
@PrepareForTest({SessionFactory.class,HibernateUtil.class})
@RunWith(PowerMockRunner.class)
public class RecommTaskHandler_3Test extends JerseyTest{

	@Override
    protected Application configure() {
		enable(TestProperties.DUMP_ENTITY);
		return new ResourceConfig(RecommTaskHandler_3.class);
	}
	@Test
	public void getRecommendedTasksNotSolvedSuccess() throws RecommException
	{
		SessionFactory mockedSessionFactory = Mockito.mock(SessionFactory.class);
		//PowerMockito.mockStatic(HibernateUtil.class);
		//PowerMockito.when(HibernateUtil.getSessionFactory()).thenReturn(mockedSessionFactory);
		/*Session mockedSession = Mockito.mock(Session.class);
		Mockito.when(mockedSessionFactory.openSession()).thenReturn(mockedSession);
		Transaction mockedTransaction = Mockito.mock(Transaction.class);
		Mockito.when(mockedSession.beginTransaction()).thenReturn(mockedTransaction);
		Query mockedQuery = Mockito.mock(Query.class);
		Mockito.when(mockedSession.createQuery(Mockito.anyString())).thenReturn(mockedQuery);
		
		Task t1 = new Task();
		t1.setExternal_id("1");
		t1.setId(1);
		T_A3 t_a3 = new T_A3();
		t_a3.setTask(t1);
		List<T_A3> taskList = new ArrayList<T_A3>();
		taskList.add(t_a3);
		
		Mockito.when(mockedQuery.list()).thenReturn(taskList);*/
		Course mockedCourse = new Course();
		Student mockedStudent = new Student();
		assertEquals(null,RecommTaskHandler_3.getTasks(mockedCourse, mockedStudent, 1));
	}
}
