package com.asu.seatr.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.RecommException;
import com.asu.seatr.exceptions.StudentException;
import com.asu.seatr.handlers.CourseAnalyzerMapHandler;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.StudentHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.handlers.analyzer1.RecommTaskHandler;
import com.asu.seatr.handlers.analyzer3.RecommTaskHandler_3;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.CourseAnalyzerMap;
import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.interfaces.RecommTaskI;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;
import com.asu.seatr.utils.Utilities;

@Path("/")
public class RecommenderAPI {

	static Logger logger = Logger.getLogger(RecommenderAPI.class);

	@Path("inittasks")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response initRecommendedTasks(@QueryParam("number_of_tasks") Integer number_of_tasks){
		RecommTaskHandler.initRecommTasks(number_of_tasks);
		return Response.status(Status.OK)
				.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.RECOMM_TASK_INIT))
				.build();
	}
}
