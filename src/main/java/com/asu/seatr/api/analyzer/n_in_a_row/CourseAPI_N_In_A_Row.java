package com.asu.seatr.api.analyzer.n_in_a_row;

import java.util.StringTokenizer;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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

import com.asu.seatr.auth.AuthenticationService;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.UserException;
import com.asu.seatr.handlers.CourseAnalyzerHandler;
import com.asu.seatr.handlers.UserCourseHandler;
import com.asu.seatr.models.analyzers.course.Course_N_In_A_Row;
import com.asu.seatr.rest.models.analyzer.n_in_a_row.CAReader_N_In_A_Row;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

//Analyzer 2 specific routes
@Path("/analyzer/n_in_a_row/courses")
public class CourseAPI_N_In_A_Row {
	public static final String AUTHENTICATION_HEADER = "Authorization";

	static Logger logger = Logger.getLogger(CourseAPI_N_In_A_Row.class);
	
	// Get course details of this analyzer given its external_course_id
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public CAReader_N_In_A_Row getCourse(@QueryParam("external_course_id") String external_course_id){
		Long requestTimestamp = System.currentTimeMillis();
		Course_N_In_A_Row ca2 = null;
		try {
			ca2 = (Course_N_In_A_Row)CourseAnalyzerHandler.readByExtId(Course_N_In_A_Row.class, external_course_id).get(0);
		} catch (CourseException e) {
			Response rb = Response.status(Status.NOT_FOUND)
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
		
			CAReader_N_In_A_Row reader=new CAReader_N_In_A_Row();
			reader.setDescription(ca2.getCourseDesc());
			reader.setExternal_course_id(ca2.getCourseExtId());
			return reader;
		
	}
	
	// create course
	// Once the course is created, we need to add a record into the UserCourseMap so that
	// the user who created the course will get access to the course
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createCourse(CAReader_N_In_A_Row reader, @HeaderParam(AUTHENTICATION_HEADER) String authHeader){
		
		Long requestTimestamp = System.currentTimeMillis();
		Course_N_In_A_Row ca2 = new Course_N_In_A_Row();
		StringTokenizer tokenizer = AuthenticationService.getUsernameAndPassword(authHeader);
		String username = tokenizer.nextToken();
		
		try{
			ca2.createCourse(reader.getExternal_course_id(), reader.getDescription());
			CourseAnalyzerHandler.save(ca2);
			
			try
			{
			UserCourseHandler.save(username, reader.getExternal_course_id());
			}
			catch(ConstraintViolationException e)
			{
				logger.info("trying to create analyzer for an existing course: " + reader.getExternal_course_id());
			}
			
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_CREATED)).build();
		
		} catch (CourseException  e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		}  catch (UserException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		}
		catch(Exception e){
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
		
	// Update the course details for analyzer 2
	/*@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCourse(CAReader_N_In_A_Row reader){
		Long requestTimestamp = System.currentTimeMillis();
		try{
			
			Course_N_In_A_Row ca1 = (Course_N_In_A_Row) CourseAnalyzerHandler.readByExtId(Course_N_In_A_Row.class, reader.getExternal_course_id()).get(0);
			
			CourseAnalyzerHandler.update(ca1);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_UPDATED))
					.build();
		} catch (CourseException e) {
			Response rb = Response.status(Status.NOT_FOUND)
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
	}*/
	
	// Deletes only the analyzer 2 for the given external_course_id
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteCourseAnalyzer1(@QueryParam("external_course_id") String external_course_id) {
		
		Long requestTimestamp = System.currentTimeMillis();
		Course_N_In_A_Row c_a2;
		// delete all course analyzers and then delete the course record
		try {
			c_a2 = (Course_N_In_A_Row)CourseAnalyzerHandler.readByExtId(Course_N_In_A_Row.class, external_course_id).get(0);
			CourseAnalyzerHandler.delete(c_a2);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_ANALYZER_DELETED)).build();
		} catch (CourseException e) {
			Response rb = Response.status(Status.NOT_FOUND)
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

