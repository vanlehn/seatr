package com.asu.seatr.exceptions;

import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

public class UserException extends AbstractMyException{

	public UserException(MyStatus status, MyMessage message) {
		super(status, message);	
	}	
}
