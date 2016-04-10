package com.asu.seatr.api;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.analyzer3.RecommTaskHandler_3;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;

@Path("/demo")
public class DemoAPI {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getInfo()
	{
		try
		{
		System.out.println("request received");
		Course course = CourseHandler.getByExternalId("74");
		Student student = StudentHandler.getByExternalId("9999", "74");
		return RecommTaskHandler_3.getTasks(course,student,10);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}