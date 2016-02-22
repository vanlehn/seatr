package com.asu.seatr.exceptions;

import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

public class StudentNotFoundException extends AbstractMyException {

	public StudentNotFoundException(MyStatus status, MyMessage message) {
		super(status, message);
	}
	
}
