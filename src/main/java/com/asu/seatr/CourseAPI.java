package com.asu.seatr;



import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.asu.seatr.models.Course;
import com.asu.seatr.utilities.CourseHandler;

//Sets the path to base URL + /hello
@Path("/course")
public class CourseAPI {
	@Context UriInfo uriInfo;
	String container;
	
	@Path("/find")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Course getCourse(@QueryParam("id") int id){
		Course course = CourseHandler.read(id);
		return course;
	}
	
	@Path("/delete")
	@GET
	public void delCourse(@QueryParam("id") int id){
		CourseHandler.delete(CourseHandler.read(id));
		//Course course = CourseHandler.delete(course);
	}
	
	@Path("/add")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void addCourse(Course c){
		CourseHandler.save(c);
	}
	
	
}
