package com.asu.seatr.api.bkt;

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
import com.asu.seatr.models.analyzers.course.Course_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.CAReader_BKT;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

@Path("/analyzer/bkt/courses")
public class CourseAPI_BKT {
	public static final String AUTHENTICATION_HEADER = "Authorization";

	static Logger logger = Logger.getLogger(CourseAPI_BKT.class);
	
	// Get course details of this analyzer given its external_course_id
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public CAReader_BKT getCourse(@QueryParam("external_course_id") String external_course_id){
		Long requestTimestamp = System.currentTimeMillis();
		Course_BKT ca = null;
		try {
			ca = (Course_BKT)CourseAnalyzerHandler.readByExtId(Course_BKT.class, external_course_id).get(0);
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
		
			CAReader_BKT reader=new CAReader_BKT();
			reader.setDescription(ca.getCourseDesc());
			reader.setExternal_course_id(ca.getCourseExtId());
			return reader;
		
	}
	
	// create course
	// Once the course is created, we need to add a record into the UserCourseMap so that
	// the user who created the course will get access to the course
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createCourse(CAReader_BKT reader, @HeaderParam(AUTHENTICATION_HEADER) String authHeader){
		
		Long requestTimestamp = System.currentTimeMillis();
		Course_BKT ca = new Course_BKT();
		StringTokenizer tokenizer = AuthenticationService.getUsernameAndPassword(authHeader);
		String username = tokenizer.nextToken();
		
		try{
			ca.createCourse(reader.getExternal_course_id(), reader.getDescription());
			CourseAnalyzerHandler.save(ca);
			
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
		
	
	// Deletes only the analyzer 2 for the given external_course_id
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteCourseAnalyzer1(@QueryParam("external_course_id") String external_course_id) {
		
		Long requestTimestamp = System.currentTimeMillis();
		Course_BKT c_a;
		// delete all course analyzers and then delete the course record
		try {
			c_a = (Course_BKT)CourseAnalyzerHandler.readByExtId(Course_BKT.class, external_course_id).get(0);
			CourseAnalyzerHandler.delete(c_a);
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

