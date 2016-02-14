package com.asu.seatr.api;

import java.lang.annotation.Annotation;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONObject;

import com.asu.seatr.models.Student;
import com.asu.seatr.models.analyzers.student.S_A1;
import com.asu.seatr.rest.models.SAReader1;
import com.asu.seatr.utilities.StudentAnalyzerHandler;
import com.asu.seatr.utilities.StudentHandler;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@Path("/students")
public class StudentAPI {
	// /students/{analyzer_id}
	@Path("/1")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SAReader1 getStudent(
			@QueryParam("external_student_id") String external_student_id, 
			@QueryParam("external_course_id") Integer external_course_id) {		
		
		//handle cases
		try {
			S_A1 sa1 = (S_A1)StudentAnalyzerHandler.readByExtId(S_A1.class, external_student_id, external_course_id).get(0);
			
			SAReader1 result  = new SAReader1();
			result.setExternal_course_id(external_course_id);
			result.setExternal_student_id(external_student_id);
			result.setS_placement_score(sa1.getS_placement_score());
			result.setS_year(sa1.getS_year());		
			
			return result;
		} catch(IndexOutOfBoundsException iob) {			
			Response rb = Response.status(Status.NOT_FOUND).
					entity(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND)).build();
			throw new WebApplicationException(rb);
		} catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
				
	}
	
	//create
	@Path("/1")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createStudent(SAReader1 sa)
	{
		//input external student id, courseid ,properties
		//populate student table
		//retrieve the analyzer name using courseid, like a1,a2,or a3...
		
		try {
			S_A1 s_a1 = new S_A1();
			
			s_a1.createStudent(sa.getExternal_student_id(), sa.getExternal_course_id(), 1);
			s_a1.setS_placement_score(sa.getS_placement_score());
			s_a1.setS_year(sa.getS_year());			
			StudentAnalyzerHandler.save(s_a1);			
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_CREATED)).build();
		} catch (ConstraintViolationException cva){			
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_ALREADY_PRESENT)).build();			
			throw new WebApplicationException(rb);
		} catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		
	}
	
	//update	
	@Path("/1")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateStudent(SAReader1 sa){
		try {
			S_A1 s_a1 = (S_A1) StudentAnalyzerHandler.readByExtId
					(S_A1.class, sa.getExternal_student_id(), sa.getExternal_course_id()).get(0);
			
			s_a1.setS_placement_score(sa.getS_placement_score());
			s_a1.setS_year(sa.getS_year());
			StudentAnalyzerHandler.update(s_a1);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_UPDATED))
					.build();
		} catch(IndexOutOfBoundsException iob) {			 
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND))
					.build();
			throw new WebApplicationException(rb);
		}		
		catch(Exception e){			
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST))
					.build();
			throw new WebApplicationException(rb);
		}
		
	}
	
	@Path("/")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteStudent(SAReader1 sa){
		try {
			// implement this
			S_A1 s_a1 = (S_A1) StudentAnalyzerHandler.readByExtId
					(S_A1.class, sa.getExternal_student_id(), sa.getExternal_course_id()).get(0);
			//delete all other analyzers here			
			StudentAnalyzerHandler.delete(s_a1);
			Student student = (Student)StudentHandler.getByExternalId(sa.getExternal_student_id(), sa.getExternal_course_id());
			StudentHandler.delete(student);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_DELETED)).build();
		} catch(IndexOutOfBoundsException iob) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND))
					.build();
			throw new WebApplicationException(rb);
		}
		catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		
		
	}
	
	//delete
	@Path("/1")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteStudent1Analyzer(SAReader1 sa){	
		try {
			S_A1 s_a1 = (S_A1) StudentAnalyzerHandler.readByExtId
					(S_A1.class, sa.getExternal_student_id(), sa.getExternal_course_id()).get(0);
			StudentAnalyzerHandler.delete(s_a1);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_ANALYZER_DELETED)).build();
		} catch(IndexOutOfBoundsException iob) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_NOT_FOUND))
					.build();
			throw new WebApplicationException(rb);
		}
		catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
	}


}
