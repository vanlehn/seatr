package com.asu.seatr.exceptions;

import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

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
