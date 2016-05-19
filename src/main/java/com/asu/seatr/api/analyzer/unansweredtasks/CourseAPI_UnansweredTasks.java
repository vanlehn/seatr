package com.asu.seatr.api.analyzer.unansweredtasks;

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
import com.asu.seatr.models.analyzers.course.Course_UnansweredTasks;
import com.asu.seatr.rest.models.analyzer.unansweredtasks.CAReader_UnansweredTasks;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

// Analyzer 1 specific routes
@Path("/analyzer/unansweredtasks/courses")
public class CourseAPI_UnansweredTasks {
	public static final String AUTHENTICATION_HEADER = "Authorization";

	static Logger logger = Logger.getLogger(CourseAPI_UnansweredTasks.class);
	
	// Get course details of this analyzer given its external_course_id
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public CAReader_UnansweredTasks getCourse(@QueryParam("external_course_id") String external_course_id){
		Long requestTimestamp = System.currentTimeMillis();
		try {
			if(!Utilities.checkExists(external_course_id)) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}

			Course_UnansweredTasks ca1 = (Course_UnansweredTasks)CourseAnalyzerHandler.readByExtId(Course_UnansweredTasks.class, external_course_id).get(0);
			CAReader_UnansweredTasks reader=new CAReader_UnansweredTasks();
			reader.setDescription(ca1.getCourseDesc());
			reader.setExternal_course_id(ca1.getCourseExtId());
			reader.setTeaching_unit(ca1.getTeaching_unit());
			reader.setThreshold(ca1.getThreshold());
			return reader;
		} catch (CourseException e) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch(Exception e){
			logger.error("Exception while getting course -analyzer 1", e);			
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
	public Response createCourse(CAReader_UnansweredTasks reader, @HeaderParam(AUTHENTICATION_HEADER) String authHeader){
		Long requestTimestamp = System.currentTimeMillis();
		try{
			if(!Utilities.checkExists(reader.getExternal_course_id())) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}		

			Course_UnansweredTasks ca1 = new Course_UnansweredTasks();
			StringTokenizer tokenizer = AuthenticationService.getUsernameAndPassword(authHeader);
			String username = tokenizer.nextToken();		

			ca1.createCourse(reader.getExternal_course_id(), reader.getDescription());
			ca1.setTeaching_unit(reader.getTeaching_unit());
			ca1.setThreshold(reader.getThreshold());
			CourseAnalyzerHandler.save(ca1);

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

	// Update the course details for analyzer 1
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCourse(CAReader_UnansweredTasks reader){
		Long requestTimestamp = System.currentTimeMillis();
		try{
			if(!Utilities.checkExists(reader.getExternal_course_id())) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}


			Course_UnansweredTasks ca1 = (Course_UnansweredTasks) CourseAnalyzerHandler.readByExtId(Course_UnansweredTasks.class, reader.getExternal_course_id()).get(0);

			if(Utilities.checkExists(reader.getTeaching_unit())) {
				ca1.setTeaching_unit(reader.getTeaching_unit());
			}	
			if(Utilities.checkExists(reader.getThreshold())) {
				ca1.setThreshold(reader.getThreshold());
			}

			CourseAnalyzerHandler.update(ca1);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_UPDATED))
					.build();
		} catch (CourseException e) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch(Exception e){
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


	// Deletes only the analyzer 1 for the given external_course_id
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteCourseAnalyzer1(@QueryParam("external_course_id") String external_course_id) {
		Long requestTimestamp = System.currentTimeMillis();
		try {
			if(!Utilities.checkExists(external_course_id)) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}
			Course_UnansweredTasks c_a1 = (Course_UnansweredTasks)CourseAnalyzerHandler.readByExtId(Course_UnansweredTasks.class, external_course_id).get(0);
			CourseAnalyzerHandler.delete(c_a1);
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
