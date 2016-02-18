package com.asu.seatr.exceptions;

import javax.ws.rs.core.Response;

public class TaskNotFoundException extends Exception{

	private final Response response;
	
	public TaskNotFoundException(String message)
	{
		this(message, null, Response.Status.INTERNAL_SERVER_ERROR);
	}
	
	public TaskNotFoundException(final Response response)
	{
		this((Throwable) null, response);
	}
	
	public TaskNotFoundException(final Throwable cause, final Response response)
	{
		this(computeExceptionMessage(response), cause, response);
	}
	
	public TaskNotFoundException(final String message, final Throwable cause, final Response.Status status)
	{
		this(message, cause, Response.status(status).build());
	}
	
	public TaskNotFoundException(final String message, final Throwable cause, final Response response)
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
