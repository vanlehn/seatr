package com.asu.seatr.api.analyzer1;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
import com.asu.seatr.handlers.analyzer1.RecommTaskHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.analyzers.studenttask.ST_A1;
import com.asu.seatr.rest.models.analyzer1.STAReader1;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

/* Student Tasks
 * get,update and delete operations have been disabled because a single student can have multiple records of
 * the same task associated with it.
 */
@Path("analyzer/1/studenttasks")
public class StudentTaskAPI_1 {
	static Logger logger = Logger.getLogger(StudentTaskAPI_1.class);	
	
	//create student task - is a transaction API which records every student task request
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createStudentTask(STAReader1 sta){
		Long requestTimestamp = System.currentTimeMillis();
		try {
			if(!Utilities.checkExists(sta.getExternal_course_id())) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}
			if(!Utilities.checkExists(sta.getExternal_student_id())) {
				throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_ID_MISSING);
			}
			if(!Utilities.checkExists(sta.getExternal_task_id())) {
				throw new TaskException(MyStatus.ERROR, MyMessage.TASK_ID_MISSING);
			}
			ST_A1 sta1 = new ST_A1();
			sta1.createStudentTask(sta.getExternal_student_id(),sta.getExternal_course_id(),sta.getExternal_task_id(), 1);
			sta1.setD_status(sta.getD_status());
			sta1.setD_time_lastattempt(sta.getD_time_lastattempt());
			StudentTaskAnalyzerHandler.save(sta1);

			Student student = StudentHandler.getByExternalId(sta.getExternal_student_id(), sta.getExternal_course_id());
			Task task = TaskHandler.readByExtId(sta.getExternal_task_id(), sta.getExternal_course_id());
			Course course=CourseHandler.getByExternalId(sta.getExternal_course_id());
			if(sta.getD_status().equals("done"))
				RecommTaskHandler.completeATask(student, course,task);

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
			logger.error("Exception while creating student", e);
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
