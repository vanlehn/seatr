package com.asu.seatr;


import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.asu.seatr.models.Course;
import com.asu.seatr.utilities.CourseHandler;

//Sets the path to base URL + /hello
@Path("/course")
public class CourseAPI {
	@Path("/get")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Course getCourse(){
		int id=1;
		Course course = CourseHandler.read(id);
		return course;
	}
	
	@Path("/delete")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public void delCourse(){
		int id=1;
		//Course course = CourseHandler.delete(course);
	}
	
	@Path("/add")
	@PUT
	public void addCourse(){
		
	}
	
	
}
