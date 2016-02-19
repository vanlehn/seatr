package com.asu.seatr.exceptions;

import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

public class StudentNotFoundException1 extends AbstractMyException {

	public StudentNotFoundException1(MyStatus status, MyMessage message) {
		super(status, message);
	}
	
}
