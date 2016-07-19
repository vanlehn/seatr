package com.asu.seatr.api.analyzer.n_in_a_row;


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
import com.asu.seatr.models.analyzers.student.Student_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.SAReader_N_In_A_Row;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

//Analyzer 2 specific routes for Student APIs
@Path("analyzer/n_in_a_row/students")
public class StudentAPI_N_In_A_Row {

	static Logger logger = Logger.getLogger(StudentAPI_N_In_A_Row.class);
	
	// Gets information about a Student for Analyzer 2
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SAReader_N_In_A_Row getStudent(
			@QueryParam("external_student_id") String external_student_id, 
			@QueryParam("external_course_id") String external_course_id) {		
		
		//handle cases
		
		logger.info("studentapi called");
			Long requestTimestamp = System.currentTimeMillis();
			try {
				StudentAnalyzerHandler.readByExtId(Student_N_In_A_Row.class, external_student_id, external_course_id).get(0);
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
			
			SAReader_N_In_A_Row result  = new SAReader_N_In_A_Row();
			result.setExternal_course_id(external_course_id);
			result.setExternal_student_id(external_student_id);	
			
			return result;
		
				
	}
	
	// Create a student
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createStudent(SAReader_N_In_A_Row sa)
	{
		//input external student id, courseid ,properties
		//populate student table
		//retrieve the analyzer name using courseid, like a1,a2,or a3...
		Long requestTimestamp = System.currentTimeMillis();
		Student_N_In_A_Row s_a2 = new Student_N_In_A_Row();
		
		try {
			s_a2.createStudent(sa.getExternal_student_id(), sa.getExternal_course_id(), 2);			
			StudentAnalyzerHandler.save(s_a2);			
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
	public Response updateStudent(SAReader_N_In_A_Row sa){
		Long requestTimestamp = System.currentTimeMillis();
		try {
			Student_N_In_A_Row s_a2 = (Student_N_In_A_Row) StudentAnalyzerHandler.readByExtId
					(Student_N_In_A_Row.class, sa.getExternal_student_id(), sa.getExternal_course_id()).get(0);
			
			StudentAnalyzerHandler.update(s_a2);
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
			Student_N_In_A_Row s_a2;
			try {
				s_a2 = (Student_N_In_A_Row) StudentAnalyzerHandler.readByExtId
						(Student_N_In_A_Row.class, external_student_id, external_course_id).get(0);			
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
