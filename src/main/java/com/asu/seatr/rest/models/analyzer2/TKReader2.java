package com.asu.seatr.rest.models.analyzer2;

import java.io.Serializable;

import com.asu.seatr.rest.models.interfaces.TKAReaderI;
import com.asu.seatr.rest.models.interfaces.TKReaderI;

public class TKReader2 implements TKReaderI,Serializable {

	private boolean replace;
	private TKAReader2[] tkaReader;
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

	
	public void setTkaReader(TKAReader2[] tkaReader) {
		// TODO Auto-generated method stub
		this.tkaReader = (TKAReader2[]) tkaReader;
	}

}
