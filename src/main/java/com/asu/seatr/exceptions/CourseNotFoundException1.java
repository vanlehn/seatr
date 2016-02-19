package com.asu.seatr.exceptions;

import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

public class CourseNotFoundException1 extends AbstractMyException {

	public CourseNotFoundException1(MyStatus status, MyMessage message) {
		super(status, message);
	}
	
}
