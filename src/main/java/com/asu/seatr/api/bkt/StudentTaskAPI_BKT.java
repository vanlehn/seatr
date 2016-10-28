package com.asu.seatr.api.bkt;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.StudentTaskAnalyzerHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.handlers.analyzer.bkt.RecommTaskHandler_BKT;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.studenttask.StudentTask_BKT;
import com.asu.seatr.rest.models.analyzer.bkt.STAReader_BKT;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

/*Student Tasks for analyzer 2
 * get,update and delete operations have been disabled because a single student can have multiple records of
the same task associated with it. Look at commits before 8/May/2016 to restore get, update and delete if you need it.
 */	  

@Path("analyzer/bkt/studenttasks")
public class StudentTaskAPI_BKT {
	static Logger logger = Logger.getLogger(StudentTaskAPI_BKT.class);
	
	//create student task
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createStudentTask(STAReader_BKT sta){
		
		Long requestTimestamp = System.currentTimeMillis();
		try {
			StudentTask_BKT sta2 = new StudentTask_BKT();
			sta2.createStudentTask(sta.getExternal_student_id(),sta.getExternal_course_id(),sta.getExternal_task_id(), 1);
			sta2.setD_status(sta.getD_status());
			sta2.setType(sta.getType());
			StudentTaskAnalyzerHandler.save(sta2);
			
			Student student = StudentHandler.getByExternalId(sta.getExternal_student_id(), sta.getExternal_course_id());
			Task task = TaskHandler.readByExtId(sta.getExternal_task_id(), sta.getExternal_course_id());
			Course course=CourseHandler.getByExternalId(sta.getExternal_course_id());
			String type = sta.getType();
			if(sta.getD_status().equals("correct"))
				RecommTaskHandler_BKT.completeATask(student, course,task,type,true);
			else if(sta.getD_status().equals("incorrect"))
				RecommTaskHandler_BKT.completeATask(student, course,task,type,false);
			
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_TASK_CREATED)).build();
		}

		catch(CourseException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}
		catch(TaskException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}
		catch(StudentException e) {
			Response rb = Response.status(Status.OK)
					.entity(MyResponse.build(e.getMyStatus(), e.getMyMessage())).build();
			throw new WebApplicationException(rb);
		}
		catch(Exception e){
			e.printStackTrace();
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
