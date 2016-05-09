package com.asu.seatr.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

// This API is used by the monitoring application to check if the web app is running
// Sanity test
@Path("/hello")
public class TestAPI {

	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getHello()
	{
		return "HELLO WORLD";
	}
}
