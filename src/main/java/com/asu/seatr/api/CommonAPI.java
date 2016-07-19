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

import org.apache.log4j.Logger;

import com.asu.seatr.exceptions.AnalyzerException;
import com.asu.seatr.exceptions.CourseAnalyzerMapException;
import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.KCException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.factory.TaskKCFactory;
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
import com.asu.seatr.models.analyzers.course.Course_UnansweredTasks;
import com.asu.seatr.models.analyzers.student.Student_UnansweredTasks;
import com.asu.seatr.models.analyzers.task.Task_UnansweredTasks;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_UnansweredTasks;
import com.asu.seatr.models.interfaces.TaskKCAnalyzerI;
import com.asu.seatr.rest.models.TKCAReader;
import com.asu.seatr.utils.Constants;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

@Path("/")
public class CommonAPI {
	// set shouldn't be get..
	static Logger logger = Logger.getLogger(CommonAPI.class);

	// Route to set a particular analyzer for a course as the "Active" analyzer
	// The rest will be set to inactive
	@Path("/courses/setanalyzer")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response setAnalyzer(@QueryParam("external_course_id") String ext_c_id,
			@QueryParam("analyzer_name") String a_id, @QueryParam("active") Boolean active) {
		Long requestTimestamp = System.currentTimeMillis();
		try {
			if (!Utilities.checkExists(ext_c_id)) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}
			if (!Utilities.checkExists(a_id)) {
				throw new AnalyzerException(MyStatus.ERROR, MyMessage.ANALYZER_ID_MISSING);
			}
			if (!Utilities.checkExists(active)) {
				// set default value
				active = false;
			}
			Analyzer az = AnalyzerHandler.getByName(a_id);
			int analyzer_id = az.getId();
			CourseAnalyzerMap ca_map = CourseAnalyzerMapHandler.getByCourseAndAnalyzer(ext_c_id,
					String.valueOf(analyzer_id));
			if (ca_map != null) {
				if (active) {
					CourseAnalyzerMapHandler.deactiveAllAnalyzers(ext_c_id);
					ca_map.setActive(true);
					CourseAnalyzerMapHandler.update(ca_map);
				} else {
					ca_map.setActive(false);
					CourseAnalyzerMapHandler.update(ca_map);
				}
			} else { // no mapping existed
				int id = Integer.valueOf(analyzer_id);
				Analyzer analyzer = AnalyzerHandler.getById(id);
				Course c = CourseHandler.getByExternalId(ext_c_id);
				ca_map = new CourseAnalyzerMap();
				ca_map.setCourse(c);
				ca_map.setAnalyzer(analyzer);
				// ca_map.setActive(active);
				// CourseAnalyzerMapHandler.save(ca_map);
				if (active) {
					CourseAnalyzerMapHandler.deactiveAllAnalyzers(ext_c_id);
					ca_map.setActive(true);
					CourseAnalyzerMapHandler.save(ca_map);
				} else {
					ca_map.setActive(false);
					CourseAnalyzerMapHandler.save(ca_map);
				}

			}
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_ANALYZER_UPDATED)).build();
		} catch (CourseException e) {
			Response rb = Response.status(Status.OK).entity(MyResponse.build(e.getMyStatus(), e.getMyMessage()))
					.build();
			throw new WebApplicationException(rb);
		} catch (AnalyzerException e) {
			Response rb = Response.status(Status.OK).entity(MyResponse.build(e.getMyStatus(), e.getMyMessage()))
					.build();
			throw new WebApplicationException(rb);
		} catch (Exception e) {
			logger.error("Exception while setting analyzer", e);
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		} finally {

			Long responseTimestamp = System.currentTimeMillis();
			Long response = (responseTimestamp - requestTimestamp) / 1000;
			Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp / 1000);

		}
	}

	// Route to delete a particular course along with all its analyzers
	// TODO: When a new analyzer is added, remember to delete that as well
	@Path("/courses")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response delCourse(@QueryParam("external_course_id") String external_course_id) {
		Long requestTimestamp = System.currentTimeMillis();
		Course_UnansweredTasks c_a1;
		// delete all course analyzers and then delete the course record
		try {
			if (!Utilities.checkExists(external_course_id)) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}
			/*
			 * try {
			 * 
			 * c_a1 = (Course_UnansweredTasks)CourseAnalyzerHandler.readByExtId(
			 * Course_UnansweredTasks.class, external_course_id).get(0);
			 * CourseAnalyzerHandler.delete(c_a1); //delete all analyzers here }
			 * catch(CourseException c) { if(!(c.getMyStatus() == MyStatus.ERROR
			 * && c.getMyMessage() == MyMessage.COURSE_ANALYZER_NOT_FOUND)) {
			 * throw c; } }
			 */
			Course course = (Course) CourseHandler.getByExternalId(external_course_id);
			CourseHandler.delete(course);
			return Response.status(Status.OK).entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.COURSE_DELETED))
					.build();
		} catch (CourseException e) {
			Response rb = Response.status(Status.OK).entity(MyResponse.build(e.getMyStatus(), e.getMyMessage()))
					.build();
			throw new WebApplicationException(rb);
		} catch (Exception e) {
			logger.error("Exception while deleting course", e);
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		} finally {

			Long responseTimestamp = System.currentTimeMillis();
			Long response = (responseTimestamp - requestTimestamp) / 1000;
			Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp / 1000);

		}
	}

	// Route to delete a student record along with all its analyzer
	// TODO: When a new analyzer is added, remember to delete that as well
	@Path("/students")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteStudent(@QueryParam("external_student_id") String external_student_id,
			@QueryParam("external_course_id") String external_course_id) {
		Long requestTimestamp = System.currentTimeMillis();
		try {
			if (!Utilities.checkExists(external_course_id)) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}
			if (!Utilities.checkExists(external_student_id)) {
				throw new StudentException(MyStatus.ERROR, MyMessage.STUDENT_ID_MISSING);
			}
			/*
			 * Student_UnansweredTasks s_a1; try { s_a1 =
			 * (Student_UnansweredTasks) StudentAnalyzerHandler.readByExtId
			 * (Student_UnansweredTasks.class, external_student_id,
			 * external_course_id).get(0); //delete all other analyzers here
			 * StudentAnalyzerHandler.delete(s_a1); } catch(StudentException s)
			 * { if(!(s.getMyStatus() == MyStatus.ERROR && s.getMyMessage() ==
			 * MyMessage.STUDENT_ANALYZER_NOT_FOUND)) { throw s; } }
			 */
			Student student = (Student) StudentHandler.getByExternalId(external_student_id, external_course_id);
			// StudentTaskHandler.hqlDeleteByStudent(student);
			StudentHandler.delete(student);
			return Response.status(Status.OK).entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.STUDENT_DELETED))
					.build();
		} catch (CourseException e) {
			Response rb = Response.status(Status.OK).entity(MyResponse.build(e.getMyStatus(), e.getMyMessage()))
					.build();
			throw new WebApplicationException(rb);
		} catch (StudentException e) {
			Response rb = Response.status(Status.OK).entity(MyResponse.build(e.getMyStatus(), e.getMyMessage()))
					.build();
			throw new WebApplicationException(rb);
		} catch (Exception e) {
			logger.error("Exception while deleting student", e);
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		} finally {

			Long responseTimestamp = System.currentTimeMillis();
			Long response = (responseTimestamp - requestTimestamp) / 1000;
			Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp / 1000);

		}
	}

	// Route to delete a task along with all its analyzers
	// TODO: When a new analyzer is added, remember to delete that as well
	@Path("/tasks")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTask(@QueryParam("external_course_id") String external_course_id,
			@QueryParam("external_task_id") String external_task_id) {
		Long requestTimestamp = System.currentTimeMillis();
		try {
			if (!Utilities.checkExists(external_course_id)) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}
			if (!Utilities.checkExists(external_task_id)) {
				throw new TaskException(MyStatus.ERROR, MyMessage.TASK_ID_MISSING);
			}

			try {

				Task_UnansweredTasks t_a1 = (Task_UnansweredTasks) TaskAnalyzerHandler
						.readByExtId(Task_UnansweredTasks.class, external_task_id, external_course_id);
				// delete all other analyzers here
				TaskAnalyzerHandler.delete(t_a1);
			} catch (TaskException e) {
				if ((e.getMyStatus() == MyStatus.ERROR) && (e.getMyMessage() == MyMessage.TASK_ANALYZER_NOT_FOUND)) {
					// do nothing
				} else {
					throw new TaskException(e.getMyStatus(), e.getMyMessage());
				}
			}
			Task task = (Task) TaskHandler.readByExtId(external_task_id, external_course_id);
			// StudentTaskHandler.hqlDeleteByTask(task);
			TaskHandler.delete(task);
			return Response.status(Status.OK).entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.TASK_DELETED))
					.build();
		}

		catch (CourseException e) {
			Response rb = Response.status(Status.OK).entity(MyResponse.build(e.getMyStatus(), e.getMyMessage()))
					.build();
			throw new WebApplicationException(rb);
		} catch (TaskException e) {
			Response rb = Response.status(Status.OK).entity(MyResponse.build(e.getMyStatus(), e.getMyMessage()))
					.build();
			throw new WebApplicationException(rb);
		}

		catch (Exception e) {
			logger.error("Exception while deleting task", e);
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		} finally {

			Long responseTimestamp = System.currentTimeMillis();
			Long response = (responseTimestamp - requestTimestamp) / 1000;
			Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp / 1000);

		}
	}

	// This API copies the KC-Task mapping from one analyzer to another for a
	// course
	// TODO: When a new analyzer is added, add them to the switch case
	@Path("/copykcmap")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response copyKCMap(TKCAReader reader) {
		Long requestTimestamp = System.currentTimeMillis();
		try {

			if (!Utilities.checkExists(reader.getExternal_course_id())) {
				throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_ID_MISSING);
			}
			if (!Utilities.checkExists(reader.getFrom_analyzer())) {
				throw new AnalyzerException(MyStatus.ERROR, MyMessage.FROM_ANALYZER_ID_MISSING);
			}
			if (!Utilities.checkExists(reader.getTo_analyzer())) {
				throw new AnalyzerException(MyStatus.ERROR, MyMessage.TO_ANALYZER_ID_MISSING);
			}
			if (!Utilities.checkExists(reader.getReplace())) {
				// default value for replace
				reader.setReplace(false);
			}

			Analyzer az1 = AnalyzerHandler.getByName(reader.getFrom_analyzer());
			int from_id = az1.getId();

			Analyzer az2 = AnalyzerHandler.getByName(reader.getTo_analyzer());
			int to_id = az2.getId();

			// this call will fail when there's no course analyzer mapping found
			CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId(reader.getExternal_course_id(), from_id);
			CourseAnalyzerMapHandler.getAnalyzerIdFromExtCourseIdAnalyzerId(reader.getExternal_course_id(), to_id);

			// Default behaviour is true
			boolean replace = (reader.getReplace() != null) ? reader.getReplace() : true;
			if (replace) {
				try {
					List<TaskKCAnalyzerI> taskKCList = TaskKCAnalyzerHandler
							.readByExtCourseId(Utilities.getTKClass(to_id), reader.getExternal_course_id());
					TaskKCAnalyzerHandler.batchDelete(taskKCList);
				} catch (KCException kce) {
					if (!(kce.getMyStatus() == MyStatus.ERROR
							&& kce.getMyMessage() == MyMessage.KC_NOT_FOUND_FOR_COURSE)) {
						throw kce;
					}
				}
			}

			List<TaskKCAnalyzerI> taskKCFromList = TaskKCAnalyzerHandler
					.readByExtCourseId(Utilities.getTKClass(from_id), reader.getExternal_course_id());
			TaskKCAnalyzerI taskKCFrom = taskKCFromList.get(0);
			
			List<TaskKCAnalyzerI> taskKCToList = new ArrayList<TaskKCAnalyzerI>();

			TaskKCAnalyzerI taskKCTo = TaskKCFactory.getObject(reader.getTo_analyzer());
			taskKCTo.setKc(taskKCFrom.getKc());
			taskKCTo.setTask(taskKCFrom.getTask());
			taskKCToList.add(taskKCTo);

			TaskKCAnalyzerHandler.batchSaveOrUpdate(taskKCToList);
			return Response.status(Status.CREATED)
					.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.KC_TASK_MAP_COPIED)).build();

		} catch (CourseException | AnalyzerException | CourseAnalyzerMapException | TaskException | KCException e) {
			Response rb = Response.status(Status.OK).entity(MyResponse.build(e.getMyStatus(), e.getMyMessage()))
					.build();
			throw new WebApplicationException(rb);

		} catch (Exception e) {
			logger.error("Exception while copying KC map", e);
			Response rb = Response.status(Status.BAD_REQUEST)
					.entity(MyResponse.build(MyStatus.ERROR, MyMessage.BAD_REQUEST)).build();
			throw new WebApplicationException(rb);
		} finally {

			Long responseTimestamp = System.currentTimeMillis();
			Long response = (responseTimestamp - requestTimestamp) / 1000;
			Utilities.writeToGraphite(Constants.METRIC_RESPONSE_TIME, response, requestTimestamp / 1000);

		}

	}

}
