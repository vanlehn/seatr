package com.asu.seatr.exceptions;

import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

public class CourseNotFoundException extends AbstractMyException {

	public CourseNotFoundException(MyStatus status, MyMessage message) {
		super(status, message);
	}
	
}
