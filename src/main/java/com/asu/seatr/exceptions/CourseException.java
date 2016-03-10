package com.asu.seatr.exceptions;

import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

public class CourseException extends AbstractMyException {

	public CourseException(MyStatus status, MyMessage message) {
		super(status, message);
	}
	
}
