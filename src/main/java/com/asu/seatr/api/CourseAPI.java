package com.asu.seatr.api;

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
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.UserCourseHandler;
import com.asu.seatr.handlers.UserHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.User;
import com.asu.seatr.models.UserCourse;
import com.asu.seatr.models.analyzers.course.C_A1;
import com.asu.seatr.rest.models.CAReader1;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@Path("/analyzer/1/courses")
public class CourseAPI {
	public static final String AUTHENTICATION_HEADER = "Authorization";

	static Logger logger = Logger.getLogger(CourseAPI.class);
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public CAReader1 getCourse(@QueryParam("external_course_id") String external_course_id){
		C_A1 ca1 = null;
		try {
			ca1 = (C_A1)CourseAnalyzerHandler.readByExtId(C_A1.class, external_course_id).get(0);
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
		
			CAReader1 reader=new CAReader1();
			reader.setDescription(ca1.getCourseDesc());
			reader.setExternal_course_id(ca1.getCourseExtId());
			reader.setTeaching_unit(ca1.getTeaching_unit());
			reader.setThreshold(ca1.getThreshold());
			return reader;
		
	}
		
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createCourse(CAReader1 reader, @HeaderParam(AUTHENTICATION_HEADER) String authHeader){
		
		C_A1 ca1 = new C_A1();
		StringTokenizer tokenizer = AuthenticationService.getUsernameAndPassword(authHeader);
		String username = tokenizer.nextToken();
		
		try{
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
			logger.error(e.getStackTrace());
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
								
			
	}
		
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCourse(CAReader1 reader){
		try{
			
			C_A1 ca1 = (C_A1) CourseAnalyzerHandler.readByExtId(C_A1.class, reader.getExternal_course_id()).get(0);
			
			ca1.setTeaching_unit(reader.getTeaching_unit());
			ca1.setThreshold(reader.getThreshold());
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
	}
	
			
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteCourseAnalyzer1(@QueryParam("external_course_id") String external_course_id) {
		
		C_A1 c_a1;
		// delete all course analyzers and then delete the course record
		try {
			c_a1 = (C_A1)CourseAnalyzerHandler.readByExtId(C_A1.class, external_course_id).get(0);
			CourseAnalyzerHandler.delete(c_a1);
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
	}
	
	
		
}
