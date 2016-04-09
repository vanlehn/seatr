package com.asu.seatr.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.analyzer3.RecommTaskHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;

@Path("/demo")
public class DemoAPI {

	@GET
	public void getInfo()
	{
		try
		{
		System.out.println("request received");
		Course course = CourseHandler.getByExternalId("74");
		Student student = StudentHandler.getByExternalId("9999", "74");
		RecommTaskHandler.getTasks(course,student,10);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}