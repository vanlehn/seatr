package com.asu.seatr.exceptions;

import javax.ws.rs.core.Response;

public class StudentNotFoundException extends Exception{
	


	private final Response response;
	
	public StudentNotFoundException(String message)
	{
		this(message, null, Response.Status.INTERNAL_SERVER_ERROR);
	}
	
	public StudentNotFoundException(final Response response)
	{
		this((Throwable) null, response);
	}
	
	public StudentNotFoundException(final Throwable cause, final Response response)
	{
		this(computeExceptionMessage(response), cause, response);
	}
	
	public StudentNotFoundException(final String message, final Throwable cause, final Response.Status status)
	{
		this(message, cause, Response.status(status).build());
	}
	
	public StudentNotFoundException(final String message, final Throwable cause, final Response response)
	{
		super(message, cause);
		if(response == null)
		{
			this.response = Response.serverError().build();
		}
		else
		{
			this.response = response;
		}
	}
	
	private static String computeExceptionMessage(Response response)
	{
		final Response.StatusType statusInfo;
		if(response != null)
		{
			statusInfo = response.getStatusInfo();
		}
		else
		{
			statusInfo = Response.Status.INTERNAL_SERVER_ERROR;
		}
		 return "HTTP " + statusInfo.getStatusCode() + ' ' + statusInfo.getReasonPhrase();
	}
	
	public Response getResponse()
	{
		return response;
	}
	

}
