package com.asu.seatr.rest.models;

import com.asu.seatr.rest.models.interfaces.TKAReaderI;
import com.asu.seatr.rest.models.interfaces.TKReaderI;

public class TKReader implements TKReaderI{

	private boolean replace;
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

	@Override
	public TKAReader1[] getTkaReader() {
		// TODO Auto-generated method stub
		return tkaReader;
	}

	@Override
	public void setTkaReader(TKAReader1[] tkaReader) {
		// TODO Auto-generated method stub
		this.tkaReader = tkaReader;
	}

}
