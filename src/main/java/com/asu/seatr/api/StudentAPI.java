package com.asu.seatr.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.json.JSONObject;

import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.analyzers.student.S_A1;
import com.asu.seatr.models.interfaces.StudentAnalyzerI;
import com.asu.seatr.rest.models.SAReader1;
import com.asu.seatr.utilities.AnalyzerHandler;
import com.asu.seatr.utilities.CourseHandler;
import com.asu.seatr.utilities.StudentAnalyzerHandler;
import com.asu.seatr.utilities.StudentHandler;
import com.asu.seatr.utils.AnalyzersMap;

@Path("/students")
public class StudentAPI {

	@Path("/get/1")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SAReader1 getStudent(
			@QueryParam("external_student_id") String external_student_id, 
			@QueryParam("external_course_id") Integer external_course_id ) {
		
		
		//handle cases
		S_A1 sa1 = (S_A1)StudentAnalyzerHandler.readByExtId(S_A1.class, external_student_id, external_course_id).get(0);
		
		SAReader1 result  = new SAReader1();
		result.setExternal_course_id(external_course_id);
		result.setExternal_student_id(external_student_id);
		result.setS_placement_score(sa1.getS_placement_score());
		result.setS_year(sa1.getS_year());		
		
		return result;
		
	}
	
	@Path("/create/1")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void createStudent(SAReader1 sa)
	{
		//input external student id, courseid ,properties
		//populate student table
		//retrieve the analyzer name using courseid, like a1,a2,or a3...
		
	
		S_A1 s_a1 = new S_A1();
		
		s_a1.createStudent(sa.getExternal_student_id(), sa.getExternal_course_id(), 1);
		s_a1.setS_placement_score(sa.getS_placement_score());
		s_a1.setS_year(sa.getS_year());
		
		AnalyzerHandler.getInstance().save(s_a1);
		
	}
	
	
	@Path("/update/1")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateStudent(SAReader1 sa){
		//AnalyzerHandler.getInstance().
	}
	@Path("/delete")
	@DELETE
	public void deleteStudent()
	{
		
	}
	
	
	


}
