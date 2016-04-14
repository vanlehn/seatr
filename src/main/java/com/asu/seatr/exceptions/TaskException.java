package com.asu.seatr.exceptions;

import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

public class TaskException extends AbstractMyException{

	public TaskException(MyStatus status, MyMessage message) {
		super(status, message);	
	}	
}
