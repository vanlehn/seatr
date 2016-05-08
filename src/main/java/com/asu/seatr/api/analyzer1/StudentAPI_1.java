package com.asu.seatr.api.analyzer1;


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

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.handlers.StudentAnalyzerHandler;
import com.asu.seatr.models.analyzers.student.S_A1;
import com.asu.seatr.rest.models.analyzer1.SAReader1;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

@Path("analyzer/1/students")
public class StudentAPI_1 {

	static Logger logger = Logger.getLogger(StudentAPI_1.class);


	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SAReader1 getStudent(
			@QueryParam("external_student_id") String external_student_id, 
			@QueryParam("external_course_id") String external_course_id) {				

		try {
			if(!Utilities.checkExists(external_course_id)) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}
			if(!Utilities.checkExists(external_student_id)) {
				throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_ID_MISSING);
			}				

			S_A1 sa1 = (S_A1)StudentAnalyzerHandler.readByExtId(S_A1.class, external_student_id, external_course_id).get(0);
			SAReader1 result  = new SAReader1();
			result.setExternal_course_id(external_course_id);
			result.setExternal_student_id(external_student_id);
			result.setS_placement_score(sa1.getS_placement_score());
			result.setS_year(sa1.getS_year());		

			return result;
		} catch (CourseException e) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch (StudentException e) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch(Exception e){
			logger.error("Error while getting student - analyzer 1", e);				
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createStudent(SAReader1 sa)
	{
		//input external student id, courseid ,properties
		//populate student table
		//retrieve the analyzer name using courseid, like a1,a2,or a3...

		try {
			if(!Utilities.checkExists(sa.getExternal_course_id())) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}
			if(!Utilities.checkExists(sa.getExternal_student_id())) {
				throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_ID_MISSING);
			}

			S_A1 s_a1 = new S_A1();
			s_a1.createStudent(sa.getExternal_student_id(), sa.getExternal_course_id(), 1);		
			s_a1.setS_placement_score(sa.getS_placement_score());
			s_a1.setS_year(sa.getS_year());			
			StudentAnalyzerHandler.save(s_a1);			
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_CREATED)).build();

		} catch (CourseException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch (StudentException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} 
		catch(ConstraintViolationException cve) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.STUDENT_ALREADY_PRESENT)).build();
			throw new WebApplicationException(rb);
		}
		catch(Exception e){
			logger.error("Exception while creating student - analyzer 1", e);			
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}

	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateStudent(SAReader1 sa){
		try {
			if(!Utilities.checkExists(sa.getExternal_course_id())) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}
			if(!Utilities.checkExists(sa.getExternal_student_id())) {
				throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_ID_MISSING);
			}

			S_A1 s_a1 = (S_A1) StudentAnalyzerHandler.readByExtId
					(S_A1.class, sa.getExternal_student_id(), sa.getExternal_course_id()).get(0);

			if(Utilities.checkExists(sa.getS_placement_score())) {
				s_a1.setS_placement_score(sa.getS_placement_score());
			}
			if(Utilities.checkExists(sa.getS_year())) {
				s_a1.setS_year(sa.getS_year());
			}

			StudentAnalyzerHandler.update(s_a1);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_UPDATED))
					.build();
		} catch (CourseException e) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch (StudentException e) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch(Exception e){
			logger.error("Exception while updating student - analyzer 1", e);
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}

	}


	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteStudent1Analyzer(
			@QueryParam("external_student_id") String external_student_id, 
			@QueryParam("external_course_id") String external_course_id){	

		try {
			if(!Utilities.checkExists(external_course_id)) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}
			if(!Utilities.checkExists(external_student_id)) {
				throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_ID_MISSING);
			}	
			S_A1 s_a1 = (S_A1) StudentAnalyzerHandler.readByExtId
					(S_A1.class, external_student_id, external_course_id).get(0);			
			StudentAnalyzerHandler.delete(s_a1);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_ANALYZER_DELETED))
					.build();
		} catch (CourseException e) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch (StudentException e) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch(Exception e){
			logger.error("Exception while deleting student - analyzer 1", e);
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
	}


}
