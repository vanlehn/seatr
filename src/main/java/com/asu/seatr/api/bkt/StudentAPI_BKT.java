package com.asu.seatr.api.bkt;


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
import com.asu.seatr.handlers.analyzer.bkt.RecommTaskHandler_BKT;
import com.asu.seatr.models.analyzers.student.Student_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.SAReader_BKT;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

@Path("analyzer/bkt/students")
public class StudentAPI_BKT {

	static Logger logger = Logger.getLogger(StudentAPI_BKT.class);
	
	// Gets information about a Student for Analyzer 2
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SAReader_BKT getStudent(
			@QueryParam("external_student_id") String external_student_id, 
			@QueryParam("external_course_id") String external_course_id) {		
		
		//handle cases
		
		logger.info("studentapi called");
			Long requestTimestamp = System.currentTimeMillis();
			try {
				StudentAnalyzerHandler.readByExtId(Student_BKT.class, external_student_id, external_course_id).get(0);
			} catch (CourseException e) {
				Response rb = Response.status(Status.OK)
						.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
				throw new WebApplicationException(rb);
			} catch (StudentException e) {
				Response rb = Response.status(Status.OK)
						.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
				throw new WebApplicationException(rb);
			} catch(Exception e){
				logger.error(e.getStackTrace());
				System.out.println(e.getMessage());
				Response rb = Response.status(Status.BAD_REQUEST)
						.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
				throw new WebApplicationException(rb);
			}
			finally
			{
				Long responseTimestamp = System.currentTimeMillis();
				Long response = (responseTimestamp -  requestTimestamp)/1000;
				Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp/1000);		
			}
			
			SAReader_BKT result  = new SAReader_BKT();
			result.setExternal_course_id(external_course_id);
			result.setExternal_student_id(external_student_id);	
			
			return result;
		
				
	}
	
	// Create a student
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createStudent(SAReader_BKT sa)
	{
		//input external student id, courseid ,properties
		//populate student table
		//retrieve the analyzer name using courseid, like a1,a2,or a3...
		Long requestTimestamp = System.currentTimeMillis();
		Student_BKT s_a = new Student_BKT();
		
		try {
			s_a.createStudent(sa.getExternal_student_id(), sa.getExternal_course_id(), 4);			
			StudentAnalyzerHandler.save(s_a);	
			RecommTaskHandler_BKT.initOneStudent(String.valueOf(s_a.getStudent().getId()), s_a.getCourse());
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
			logger.error(e.getStackTrace());
			System.out.println(e.getMessage());
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		finally
		{
			Long responseTimestamp = System.currentTimeMillis();
			Long response = (responseTimestamp -  requestTimestamp)/1000;
			Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp/1000);		
		}
		
	}
	
	// update student details
	// Logic here and everywhere is that, when an attribute which is not required is not present in the 
	// request body, then that attribute is not set to empty. The values of only those that are present are changed
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateStudent(SAReader_BKT sa){
		Long requestTimestamp = System.currentTimeMillis();
		try {
			Student_BKT s_a2 = (Student_BKT) StudentAnalyzerHandler.readByExtId
					(Student_BKT.class, sa.getExternal_student_id(), sa.getExternal_course_id()).get(0);
			
			StudentAnalyzerHandler.update(s_a2);
			RecommTaskHandler_BKT.initOneStudent(String.valueOf(s_a2.getStudent().getId()), s_a2.getCourse());
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_UPDATED))
					.build();
		} catch (CourseException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch (StudentException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch(Exception e){
			logger.error(e.getStackTrace());
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
		finally
		{
			Long responseTimestamp = System.currentTimeMillis();
			Long response = (responseTimestamp -  requestTimestamp)/1000;
			Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp/1000);		
		}
		
	}
	
	// Delete student analyzer record for that student
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteStudentAnalyzer(
			@QueryParam("external_student_id") String external_student_id, 
			@QueryParam("external_course_id") String external_course_id){	
			Long requestTimestamp = System.currentTimeMillis();
			Student_BKT s_a2;
			try {
				s_a2 = (Student_BKT) StudentAnalyzerHandler.readByExtId
						(Student_BKT.class, external_student_id, external_course_id).get(0);			
				StudentAnalyzerHandler.delete(s_a2);
				return Response.status(Status.OK)
						.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_ANALYZER_DELETED))
						.build();
			} catch (CourseException e) {
				Response rb = Response.status(Status.OK)
						.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
				throw new WebApplicationException(rb);
			} catch (StudentException e) {
				Response rb = Response.status(Status.OK)
						.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
				throw new WebApplicationException(rb);
			} catch(Exception e){
				logger.error(e.getStackTrace());
				Response rb = Response.status(Status.BAD_REQUEST)
						.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
				throw new WebApplicationException(rb);
			}
			finally
			{
				Long responseTimestamp = System.currentTimeMillis();
				Long response = (responseTimestamp -  requestTimestamp)/1000;
				Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp/1000);		
			}
	}


}
