package com.asu.seatr.api.analyzer.required_optional;

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
import com.asu.seatr.exceptions.StudentTaskException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.StudentTaskAnalyzerHandler;
import com.asu.seatr.models.analyzers.studenttask.StudentTask_Required_Optional;
import com.asu.seatr.rest.models.analyzer.required_optional.STAReader_Required_Optional;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

/* Student Tasks
 * get,update and delete operations have been disabled because a single student can have multiple records of
 * the same task associated with it.
 */
@Path("analyzer/required_optional/studenttasks")
public class StudentTaskAPI_Required_Optional {
	static Logger logger = Logger.getLogger(StudentTaskAPI_Required_Optional.class);

	//create student task - is a transaction API which records every student task request
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createStudentTask(STAReader_Required_Optional sta){
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
			StudentTask_Required_Optional sta3 = new StudentTask_Required_Optional();

			try {
				StudentTask_Required_Optional sta3Recent = (StudentTask_Required_Optional) StudentTaskAnalyzerHandler.readOrderByTimestamp(StudentTask_Required_Optional.class, sta.getExternal_student_id(), sta.getExternal_course_id(), sta.getExternal_task_id()).get(0);
				System.out.println(sta3Recent.getId());
				sta3.setD_current_n(sta3Recent.getD_current_n()+1);				

			} catch (StudentTaskException ste) {
				if (ste.getMyMessage() == MyMessage.STUDENT_TASK_NOT_FOUND || ste.getMyMessage() == MyMessage.STUDENT_TASK_ANALYZER_NOT_FOUND) {
					sta3.setD_current_n(1);
				}
			}
			sta3.createStudentTask(sta.getExternal_student_id(),sta.getExternal_course_id(),sta.getExternal_task_id(), 3);
			//sta3.setD_current_n(sta.getD_current_n());
			//List<StudentTask> stList = StudentTaskHandler.readByExtId(sta.getExternal_student_id(), sta.getExternal_course_id(), sta.getExternal_task_id());

			//sta3.setD_timestsamp(System.currentTimeMillis());
			sta3.setD_is_answered(sta.getD_is_answered());

			StudentTaskAnalyzerHandler.save(sta3);


			//Student student = StudentHandler.getByExternalId(sta.getExternal_student_id(), sta.getExternal_course_id());
			//Task task = TaskHandler.readByExtId(sta.getExternal_task_id(), sta.getExternal_course_id());
			//Course course=CourseHandler.getByExternalId(sta.getExternal_course_id());
			//if(sta.getD_status().equals("done"))
			//	RecommTaskHandler.completeATask(student, course,task);

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
