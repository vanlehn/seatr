package com.asu.seatr.exceptions;

import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

public class CourseAnalyzerMapException extends AbstractMyException {

	public CourseAnalyzerMapException(MyStatus status, MyMessage message) {
		super(status, message);
	}

}