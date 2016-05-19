package com.asu.seatr.api.analyzer.required_optional;

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
import com.asu.seatr.models.analyzers.course.Course_Required_Optional;
import com.asu.seatr.rest.models.analyzer.required_optional.CAReader_Required_Optional;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

//Analyzer 3 specific routes
@Path("/analyzer/required_optional/courses")
public class CourseAPI_Required_Optional {
	public static final String AUTHENTICATION_HEADER = "Authorization";

	static Logger logger = Logger.getLogger(CourseAPI_Required_Optional.class);
	
	// Get course details of this analyzer given its external_course_id
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public CAReader_Required_Optional getCourse(@QueryParam("external_course_id") String external_course_id){	
		Long requestTimestamp = System.currentTimeMillis();
		try {
			if(!Utilities.checkExists(external_course_id)) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}
			Course_Required_Optional ca3  = (Course_Required_Optional)CourseAnalyzerHandler.readByExtId(Course_Required_Optional.class, external_course_id).get(0);
			CAReader_Required_Optional reader=new CAReader_Required_Optional();
			reader.setDescription(ca3.getCourseDesc());
			reader.setExternal_course_id(ca3.getCourseExtId());
			reader.setD_current_unit_no(ca3.getD_current_unit_no());
			reader.setD_max_n(ca3.getD_max_n());
			reader.setS_units(ca3.getS_units());

			return reader;
		} catch (CourseException e) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch(Exception e){
			logger.error("Exception while getting course - analyzer 3", e);			
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
		
	// create course
	// Once the course is created, we need to add a record into the UserCourseMap so that
	// the user who created the course will get access to the course
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createCourse(CAReader_Required_Optional reader, @HeaderParam(AUTHENTICATION_HEADER) String authHeader){

		Long requestTimestamp = System.currentTimeMillis();
		try{
			if(!Utilities.checkExists(reader.getExternal_course_id())) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}

			Course_Required_Optional ca3 = new Course_Required_Optional();
			StringTokenizer tokenizer = AuthenticationService.getUsernameAndPassword(authHeader);
			String username = tokenizer.nextToken();

			ca3.createCourse(reader.getExternal_course_id(), reader.getDescription());
			ca3.setD_current_unit_no(reader.getD_current_unit_no());
			ca3.setD_max_n(reader.getD_max_n());
			ca3.setS_units(reader.getS_units());
			CourseAnalyzerHandler.save(ca3);

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
			logger.error("Exception while creating course", e);
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

	// Update the course details for analyzer 3
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCourse(CAReader_Required_Optional reader){
		Long requestTimestamp = System.currentTimeMillis();
		try{
			if(!Utilities.checkExists(reader.getExternal_course_id())) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}


			Course_Required_Optional ca3 = (Course_Required_Optional) CourseAnalyzerHandler.readByExtId(Course_Required_Optional.class, reader.getExternal_course_id()).get(0);

			if(Utilities.checkExists(reader.getD_current_unit_no())) {
				ca3.setD_current_unit_no(reader.getD_current_unit_no());
			}
			if(Utilities.checkExists(reader.getD_max_n())) {
				ca3.setD_max_n(reader.getD_max_n());
			}
			if(Utilities.checkExists(reader.getS_units())) {
				ca3.setS_units(reader.getS_units());
			}

			CourseAnalyzerHandler.update(ca3);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_UPDATED))
					.build();
		} catch (CourseException e) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} 
		catch(Exception e){
			logger.error("Exception while updating course", e);
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


	// Deletes only the analyzer 3 for the given external_course_id
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteCourseAnalyzer1(@QueryParam("external_course_id") String external_course_id) {
		
		Long requestTimestamp = System.currentTimeMillis();

		try {
			if(!Utilities.checkExists(external_course_id)) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}
			Course_Required_Optional c_a3 = (Course_Required_Optional)CourseAnalyzerHandler.readByExtId(Course_Required_Optional.class, external_course_id).get(0);
			CourseAnalyzerHandler.delete(c_a3);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_ANALYZER_DELETED)).build();
		} catch (CourseException e) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch(Exception e){
			logger.error("Exception while deleting course analyzer", e);
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
