package com.asu.seatr.utils;

import org.json.JSONObject;

public class MyResponse {
	public static String build(MyStatus status, MyMessage message){
		JSONObject json = new JSONObject();
		json.put("status", status.getValue());
		json.put("message", message.getValue());
		return json.toString();
	}
}
