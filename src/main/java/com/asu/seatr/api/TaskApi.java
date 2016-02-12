package com.asu.seatr.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/tasks")
public class TaskApi {
	
	@Path("/get")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getTask()
	{
		return null;
	}
	
	@Path("/create")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void createTask(String createTicket)
	{
		
	}
	@Path("/update")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateTask(String updateTicket)
	{
		
	}
	@Path("/delete")
	@DELETE
	public void deleteTask()
	{
		
	}
	
	
}
