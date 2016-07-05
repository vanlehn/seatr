package com.asu.seatr.exceptions;

import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

// This is an abstract class for any sort of exception class
// Specific exception classes helps us to easily read which type of error we are expecting 
// even though their class fields, methods contents do not change
@SuppressWarnings("serial")
public abstract class AbstractMyException extends Exception {

	MyStatus myStatus;
	MyMessage myMessage;

	public AbstractMyException(MyStatus status, MyMessage message) {
		super(message.value);
		this.myStatus = status;
		this.myMessage = message;
	}
	public MyStatus getMyStatus() {
		return myStatus;
	}

	public void setMyStatus(MyStatus myStatus) {
		this.myStatus = myStatus;
	}

	public MyMessage getMyMessage() {
		return myMessage;
	}

	public void setMyMessage(MyMessage myMessage) {
		this.myMessage = myMessage;
	}


}
