package com.asu.seatr.exceptions;

import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

public class KCNotFoundException extends AbstractMyException{

	public KCNotFoundException(MyStatus status, MyMessage message) {
		super(status, message);
		// TODO Auto-generated constructor stub
	}

}
