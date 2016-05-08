package com.asu.seatr.rest.models.analyzer1;

import com.asu.seatr.rest.models.interfaces.TKAReaderI;
import com.asu.seatr.rest.models.interfaces.TKReaderI;

public class TKReader1 implements TKReaderI{

	private boolean replace;
	private String external_course_id;
	private TKAReader1[] tkaReader;
	@Override
	public boolean getReplace() {
		// TODO Auto-generated method stub
		return replace;
	}

	@Override
	public void setReplace(boolean replace) {
		// TODO Auto-generated method stub
		this.replace = replace;
	}


	public TKAReader1[] getTkaReader() {
		// TODO Auto-generated method stub
		return tkaReader;
	}



	public void setTkaReader(TKAReader1[] tkaReader) {


		// TODO Auto-generated method stub
		this.tkaReader = tkaReader;
	}

	public String getExternal_course_id() {
		return external_course_id;
	}

	public void setExternal_course_id(String external_course_id) {
		this.external_course_id = external_course_id;
	}

}
