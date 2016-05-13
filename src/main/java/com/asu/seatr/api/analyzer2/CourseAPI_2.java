package com.asu.seatr.api.analyzer2;

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

import com.asu.seatr.auth.AuthenticationService;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.UserException;
import com.asu.seatr.handlers.CourseAnalyzerHandler;
import com.asu.seatr.handlers.UserCourseHandler;
import com.asu.seatr.models.analyzers.course.C_A2;
import com.asu.seatr.rest.models.analyzer2.CAReader2;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

//Analyzer 2 specific routes
@Path("/analyzer/2/courses")
public class CourseAPI_2 {
	public static final String AUTHENTICATION_HEADER = "Authorization";

	static Logger logger = Logger.getLogger(CourseAPI_2.class);
	
	// Get course details of this analyzer given its external_course_id
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public CAReader2 getCourse(@QueryParam("external_course_id") String external_course_id){
		Long requestTimestamp = System.currentTimeMillis();
		C_A2 ca2 = null;
		try {
			ca2 = (C_A2)CourseAnalyzerHandler.readByExtId(C_A2.class, external_course_id).get(0);
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
		
			CAReader2 reader=new CAReader2();
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
	public Response createCourse(CAReader2 reader, @HeaderParam(AUTHENTICATION_HEADER) String authHeader){
		
		Long requestTimestamp = System.currentTimeMillis();
		C_A2 ca2 = new C_A2();
		StringTokenizer tokenizer = AuthenticationService.getUsernameAndPassword(authHeader);
		String username = tokenizer.nextToken();
		
		try{
			ca2.createCourse(reader.getExternal_course_id(), reader.getDescription());
			CourseAnalyzerHandler.save(ca2);
			
			UserCourseHandler.save(username, reader.getExternal_course_id());
			
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
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCourse(CAReader2 reader){
		Long requestTimestamp = System.currentTimeMillis();
		try{
			
			C_A2 ca1 = (C_A2) CourseAnalyzerHandler.readByExtId(C_A2.class, reader.getExternal_course_id()).get(0);
			
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
	}
	
	// Deletes only the analyzer 2 for the given external_course_id
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteCourseAnalyzer1(@QueryParam("external_course_id") String external_course_id) {
		
		Long requestTimestamp = System.currentTimeMillis();
		C_A2 c_a2;
		// delete all course analyzers and then delete the course record
		try {
			c_a2 = (C_A2)CourseAnalyzerHandler.readByExtId(C_A2.class, external_course_id).get(0);
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

