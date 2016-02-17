package com.asu.seatr.api.analyzer;

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

import com.asu.seatr.models.Course;
import com.asu.seatr.models.analyzers.course.C_A1;
import com.asu.seatr.rest.models.CAReader1;
import com.asu.seatr.utilities.C_A1_Handler;
import com.asu.seatr.utilities.CourseHandler;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@Path("/analyzer1")
public class Analyzer1API {
	
	@Path("/course")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addCourse(CAReader1 reader){
		try{
			Course c=CourseHandler.getByExternalId(reader.getExternal_course_id());
			if(c==null){
				c=new Course();
				c.setExternal_id(reader.getExternal_course_id());
				c.setDescription(reader.getDescription());
				CourseHandler.save(c);
			}		
			C_A1_Handler.save(reader.getExternal_course_id(), reader.getThreshold(),reader.getTeaching_unit());
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_CREATED)).build();
		}catch(ConstraintViolationException e){
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_ALREADY_PRESENT)).build();			
			throw new WebApplicationException(rb);
		}catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
			
	}
	
	@Path("/course")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCourse(CAReader1 reader){
		try{
			if(reader.getDescription()!=null)
			{
				Course c=CourseHandler.getByExternalId(reader.getExternal_course_id());
				c.setDescription(reader.getDescription());
				CourseHandler.updateCourseByExternalID(c.getExternal_id(), c);
			}
			C_A1 c_a=C_A1_Handler.readByC_Ext_id(reader.getExternal_course_id());
			c_a.setTeaching_unit(reader.getTeaching_unit());
			c_a.setThreshold(reader.getThreshold());
			C_A1_Handler.update(c_a);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_UPDATED))
					.build();
		}
		catch(IndexOutOfBoundsException iob) {			 
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND))
					.build();
			throw new WebApplicationException(rb);
		}		
		catch(Exception e){			
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST))
					.build();
			throw new WebApplicationException(rb);
		}
	}
	
	@Path("/course")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delCourse(@QueryParam("external_course_id") String ext_c_id){
		try{
			C_A1_Handler.deleteByC_Ext_id(ext_c_id);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_DELETED)).build();
		}catch(IndexOutOfBoundsException iob) {
			Response rb = Response.status(Status.NOT_FOUND)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND))
					.build();
			throw new WebApplicationException(rb);
		}
		catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
	}
	
	@Path("/course")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public CAReader1 getCourse(@QueryParam("external_course_id") String ext_c_id){
		try{
			C_A1 c_a=C_A1_Handler.readByC_Ext_id(ext_c_id);
			CAReader1 reader=new CAReader1();
			reader.setDescription(c_a.getCourseDesc());
			reader.setExternal_course_id(c_a.getCourseExtId());
			reader.setTeaching_unit(c_a.getTeaching_unit());
			reader.setThreshold(c_a.getThreshold());
			return reader;
		}
		catch(IndexOutOfBoundsException iob) {			
			Response rb = Response.status(Status.NOT_FOUND).
					entity(MyResponse.build(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND)).build();
			throw new WebApplicationException(rb);
		} /*catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}*/
	}
}
