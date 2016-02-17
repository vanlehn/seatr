package com.asu.seatr.api;



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
	
	@Path("/test")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Course test(){
		Course course = new Course();
		course.setDescription("test course");
		CourseHandler.save(course);
		return course;
	}
	
	@Path("/find")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Course getCourse(@QueryParam("external_id") String external_idStr){
		Course course=null;
		if(external_idStr!=null)
			course=CourseHandler.getByExternalId(external_idStr);
		return course;
	}
	
	@Path("/delete")
	@GET
	public void delCourse(@QueryParam("id") String idStr,@QueryParam("external_id") String external_idStr){
		if(idStr!=null)
			CourseHandler.delete(CourseHandler.readById(Integer.valueOf(idStr)));
		else if(external_idStr!=null)
			CourseHandler.delete(CourseHandler.getByExternalId(external_idStr));
		//Course course = CourseHandler.delete(course);
	}
	
	@Path("/add")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void addCourse(Course c){
		CourseHandler.save(c);
	}
	
	@Path("/update")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateCourse(@QueryParam("external_id") String external_id,Course c ){
		CourseHandler.updateCourseByExternalID(external_id, c);
	}
	
	
}
