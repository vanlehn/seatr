package com.asu.seatr.utils;

public enum MyStatus {
	ERROR("error"),
	SUCCESS("success");

	public String value;

	private MyStatus(String value) {
		this.value = value;
	}	

	public String getValue() {
		return this.value;
	}
}
