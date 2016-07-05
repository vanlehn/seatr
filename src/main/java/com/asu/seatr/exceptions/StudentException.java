package com.asu.seatr.exceptions;

import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

@SuppressWarnings("serial")
public class StudentException extends AbstractMyException {

	public StudentException(MyStatus status, MyMessage message) {
		super(status, message);
	}

}
