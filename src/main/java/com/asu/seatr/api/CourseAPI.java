package com.asu.seatr.api;

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

import com.asu.seatr.exceptions.AnalyzerException;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.handlers.AnalyzerHandler;
import com.asu.seatr.handlers.C_A1_Handler;
import com.asu.seatr.handlers.CourseAnalyzerHandler;
import com.asu.seatr.handlers.CourseAnalyzerMapHandler;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.models.Analyzer;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.CourseAnalyzerMap;
import com.asu.seatr.models.analyzers.course.C_A1;
import com.asu.seatr.rest.models.CAReader1;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@Path("/courses")
public class CourseAPI {
	
	//set shouldn't be get..
	@Path("/setanalyzer")
	@GET
	public Response setAnalyzer(@QueryParam("external_course_id") String ext_c_id, @QueryParam("analyzer_id") String a_id, @QueryParam("active") Boolean active){
		try{
			CourseAnalyzerMap ca_map=CourseAnalyzerMapHandler.getByCourseAndAnalyzer(ext_c_id, a_id);
			if(ca_map!=null){
				if(active){
					CourseAnalyzerMapHandler.deactiveAllAnalyzers(ext_c_id);
					ca_map.setActive(true);
					CourseAnalyzerMapHandler.update(ca_map);
				}
				else{
					ca_map.setActive(false);
					CourseAnalyzerMapHandler.update(ca_map);
				}	
			}
			else{ //no mapping existed
				int id=Integer.valueOf(a_id);
				Analyzer analyzer=AnalyzerHandler.getById(id);
				Course c=CourseHandler.getByExternalId(ext_c_id);
				ca_map=new CourseAnalyzerMap();
				ca_map.setCourse(c);
				ca_map.setAnalyzer(analyzer);
				ca_map.setActive(active);
				CourseAnalyzerMapHandler.save(ca_map);
				
			}
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_ANALYZER_UPDATED)).build();
		}
		catch (CourseException e) {
				Response rb = Response.status(Status.OK)
						.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
				throw new WebApplicationException(rb);
			} 
		catch (AnalyzerException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} 
	}
	{
		
	}
	
	@Path("/1")
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
	
	//create
	@Path("/1")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addCourse(CAReader1 reader){
		
		C_A1 ca1 = new C_A1();
		
		try{
			ca1.createCourse(reader.getExternal_course_id(), reader.getDescription());
			ca1.setTeaching_unit(reader.getTeaching_unit());
			ca1.setThreshold(reader.getThreshold());
			CourseAnalyzerHandler.save(ca1);
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_CREATED)).build();
		
		} catch (CourseException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} 		
								
			
	}
	
	@Path("/1")
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
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
	}
	
	@Path("/")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delCourse(@QueryParam("external_course_id") String external_course_id){
		C_A1 c_a1;
		// delete all course analyzers and then delete the course record
		try {
			c_a1 = (C_A1)CourseAnalyzerHandler.readByExtId(C_A1.class, external_course_id).get(0);
			CourseAnalyzerHandler.delete(c_a1);
			//delete all analyzers here
			
			Course course =(Course)CourseHandler.getByExternalId(external_course_id);
			CourseHandler.delete(course);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_DELETED)).build();
		} catch (CourseException e) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
			throw new WebApplicationException(rb);
		} catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
	}
	
	@Path("/1")
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
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
	}
	
	
		
}
