package com.asu.seatr.rest.models.interfaces;

import com.asu.seatr.rest.models.TKAReader1;

public interface TKReaderI {

	public boolean getReplace();
	public void setReplace(boolean replace);
	public TKAReader1[] getTkaReader();
	public void setTkaReader(TKAReader1[] tkaReader);
	
}
