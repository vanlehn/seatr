package com.asu.seatr;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.asu.seatr.hibernate.*;

import com.asu.seatr.utilities.*;


//Sets the path to base URL + /hello
@Path("/hello")
public class API {
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Sample> sayJSONHello() {
		
		Sample ts = new Sample();
        ts.setId("1");
        ts.setVal("Hello World");
        
        Sample ts2 = new Sample();
        ts2.setId("2");
        ts2.setVal("Hello Arizona");

        ArrayList<Sample> availSamples = new ArrayList<Sample>();
        availSamples.add(ts);
        availSamples.add(ts2);

        return availSamples;
	}
	
	@Path("/plain")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello() {
		return "Hello World!";
	}
	
	@Path("/db")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List getDB(){
		List list = DatabaseHandler.readAll();
		return list;
	}
 

} 