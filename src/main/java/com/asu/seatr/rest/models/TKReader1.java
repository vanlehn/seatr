package com.asu.seatr.rest.models;

import com.asu.seatr.rest.models.interfaces.TKAReaderI;
import com.asu.seatr.rest.models.interfaces.TKReaderI;

public class TKReader1 implements TKReaderI{

	private boolean replace;
	private TKAReaderI[] tkaReader;


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
	public TKAReaderI[] getTkaReader() {
		// TODO Auto-generated method stub
		return tkaReader;
	}

	
	public void setTkaReader(TKAReaderI[] tkaReader) {
		// TODO Auto-generated method stub
		this.tkaReader = tkaReader;
	} 
	
}
