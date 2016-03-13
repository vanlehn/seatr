package com.asu.seatr.api;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.asu.seatr.exceptions.AnalyzerException;
import com.asu.seatr.exceptions.CourseAnalyzerMapException;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.KCException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.AnalyzerHandler;
import com.asu.seatr.handlers.CourseAnalyzerHandler;
import com.asu.seatr.handlers.CourseAnalyzerMapHandler;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.StudentAnalyzerHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.TaskAnalyzerHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.handlers.TaskKCAnalyzerHandler;
import com.asu.seatr.models.Analyzer;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.CourseAnalyzerMap;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.course.C_A1;
import com.asu.seatr.models.analyzers.student.S_A1;
import com.asu.seatr.models.analyzers.task.T_A1;
import com.asu.seatr.models.analyzers.task_kc.TK_A1;
import com.asu.seatr.models.analyzers.task_kc.TK_A2;
import com.asu.seatr.models.interfaces.TaskKCAnalyzerI;
import com.asu.seatr.rest.models.TKCAReader;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

@Path("/")
public class CommonAPI {
//set shouldn't be get..
	@Path("/courses/setanalyzer")
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
	
	
	@Path("/courses")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delCourse(@QueryParam("external_course_id") String external_course_id){
		C_A1 c_a1;
		// delete all course analyzers and then delete the course record
		try {
			try {
			
				c_a1 = (C_A1)CourseAnalyzerHandler.readByExtId(C_A1.class, external_course_id).get(0);
				CourseAnalyzerHandler.delete(c_a1);
				//delete all analyzers here
			} catch(CourseException c) {
				if(!(c.getMyStatus() == MyStatus.ERROR && c.getMyMessage() == MyMessage.COURSE_ANALYZER_NOT_FOUND)) {
					throw c;
				}
			}
			
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
	
	
	@Path("/students")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteStudent(
			@QueryParam("external_student_id") String external_student_id, 
			@QueryParam("external_course_id") String external_course_id){
		
		
			S_A1 s_a1;
			try {
				try {
					s_a1 = (S_A1) StudentAnalyzerHandler.readByExtId (S_A1.class, external_student_id, external_course_id).get(0);			
					//delete all other analyzers here			
					StudentAnalyzerHandler.delete(s_a1);
				} catch(StudentException s) {
					if(!(s.getMyStatus() == MyStatus.ERROR && s.getMyMessage() == MyMessage.STUDENT_ANALYZER_NOT_FOUND)) {
						throw s;
					}
				}
				
				Student student = (Student)StudentHandler
						.getByExternalId(external_student_id, external_course_id);
				StudentHandler.delete(student);
				return Response.status(Status.OK)
						.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_DELETED)).build();
			} catch (CourseException e) {
				Response rb = Response.status(Status.NOT_FOUND)
						.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
				throw new WebApplicationException(rb);
			} catch (StudentException e) {
				Response rb = Response.status(Status.NOT_FOUND)
						.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();			
				throw new WebApplicationException(rb);
			} catch(Exception e){
				Response rb = Response.status(Status.BAD_REQUEST)
						.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
				throw new WebApplicationException(rb);
			}	
	}
	
	
	@Path("/tasks")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTask(
			@QueryParam("external_course_id") String external_course_id,
			@QueryParam("external_task_id") String external_task_id
			)
	{

		try {
			try
			{
				
				T_A1 t_a1 = (T_A1) TaskAnalyzerHandler.readByExtId
						(T_A1.class, external_task_id, external_course_id);
				//delete all other analyzers here			
				TaskAnalyzerHandler.delete(t_a1);
			}
			catch(TaskException e)
			{
				if((e.getMyStatus() == MyStatus.ERROR)&&(e.getMyMessage() == MyMessage.TASK_ANALYZER_NOT_FOUND))
				{
					//do nothing
				}
				else
				{
					throw new TaskException(e.getMyStatus(),e.getMyMessage());
				}
			}
			Task task = (Task)TaskHandler.readByExtId(external_task_id, external_course_id);
			TaskHandler.delete(task);
			return Response.status(Status.OK)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_DELETED)).build();
		} 

		catch(CourseException e) {
			Response rb = Response.status(Status.NOT_FOUND).
					entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}
		catch(TaskException e) {
			Response rb = Response.status(Status.NOT_FOUND).
					entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}
		
		catch(Exception e){
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}
	}
	
	@Path("/copykcmap")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response copyKCMap(TKCAReader reader)  
	{		
		
		try {
			
			// this call will fail when there's no course analyzer mapping found
			CourseAnalyzerMapHandler
					.getAnalyzerIdFromExtCourseIdAnalyzerId(reader.getExternal_course_id(), reader.getFrom_analyzer_id());
			CourseAnalyzerMapHandler
					.getAnalyzerIdFromExtCourseIdAnalyzerId(reader.getExternal_course_id(), reader.getTo_analyzer_id());

			// Default behaviour is true
			boolean replace = (reader.getReplace() != null) ? reader.getReplace() : true;			
			if(replace) {
				try {
					List<TaskKCAnalyzerI> taskKCList = TaskKCAnalyzerHandler.readByExtCourseId
						(Utilities.getTKClass(reader.getTo_analyzer_id()), reader.getExternal_course_id());
					TaskKCAnalyzerHandler.batchDelete(taskKCList);
				} catch (KCException kce) {
					if (!(kce.getMyStatus() == MyStatus.ERROR && kce.getMyMessage() == MyMessage.KC_NOT_FOUND_FOR_COURSE)) {
						throw kce;
					}
				}
			}
			
			List<TaskKCAnalyzerI> taskKCFromList = TaskKCAnalyzerHandler.readByExtCourseId
					(Utilities.getTKClass(reader.getFrom_analyzer_id()), reader.getExternal_course_id());
			List<TaskKCAnalyzerI> taskKCToList = new ArrayList<TaskKCAnalyzerI>();
			switch(reader.getTo_analyzer_id()) {
				case 1: 
					for (TaskKCAnalyzerI taskKC: taskKCFromList) {
						TK_A1 tka = new TK_A1();
						tka.setKc(taskKC.getKc());
						tka.setTask(taskKC.getTask());
						taskKCToList.add(tka);
					}
					break;
				case 2: 
					for (TaskKCAnalyzerI taskKC: taskKCFromList) {
						TK_A2 tka = new TK_A2();
						tka.setKc(taskKC.getKc());
						tka.setTask(taskKC.getTask());
						taskKCToList.add(tka);
					}
					break;						
					
			}
			TaskKCAnalyzerHandler.batchSaveOrUpdate(taskKCToList);
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.KC_TASK_MAP_COPIED)).build();			
		
		} catch (CourseException | AnalyzerException | CourseAnalyzerMapException | TaskException | KCException e) {
			Response rb = Response.status(Status.OK).
					entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
			
		} catch (Exception e) {
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		}		
		
	}
	
	
}
