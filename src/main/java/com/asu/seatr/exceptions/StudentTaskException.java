package com.asu.seatr.exceptions;

import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

public class StudentTaskException extends AbstractMyException {

	public StudentTaskException(MyStatus status, MyMessage message) {
		super(status, message);
	}
	
}
