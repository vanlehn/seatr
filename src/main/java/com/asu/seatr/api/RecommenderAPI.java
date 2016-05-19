package com.asu.seatr.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.asu.seatr.handlers.analyzer.unansweredtasks.RecommTaskHandler_UnansweredTasks;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@Path("/")
public class RecommenderAPI {

	static Logger logger = Logger.getLogger(RecommenderAPI.class);

	@Path("inittasks")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response initRecommendedTasks(@QueryParam("number_of_tasks") Integer number_of_tasks){
		RecommTaskHandler_UnansweredTasks.initRecommTasks(number_of_tasks);
		return Response.status(Status.OK)
				.entity(MyResponse.build(MyStatus.SUCCESS, MyMessage.RECOMM_TASK_INIT))
				.build();
	}
}
