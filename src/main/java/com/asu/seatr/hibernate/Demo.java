package com.asu.seatr.hibernate;

public class Demo {
	private int id;
	private String message;
	public Demo()
	{
		
	}
	public Demo(String message)
	{
		this.message = message;
	}
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
	public String getMessage()
	{
		return message;
	}
	public void setMessage(String message)
	{
		this.message = message;
	}
}
