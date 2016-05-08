package com.asu.seatr.exceptions;

import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

@SuppressWarnings("serial")
public class KCException extends AbstractMyException{

	public KCException(MyStatus status, MyMessage message) {
		super(status, message);
	}

}
