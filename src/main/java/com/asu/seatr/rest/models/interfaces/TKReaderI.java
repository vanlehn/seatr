package com.asu.seatr.rest.models.interfaces;

public interface TKReaderI {

	public boolean getReplace();
	public void setReplace(boolean replace);
	public TKAReaderI[] getTkaReader();
	public void setTkaReader(TKAReaderI[] tkaReader);
	
}
