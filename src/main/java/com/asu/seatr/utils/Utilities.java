package com.asu.seatr.utils;

import com.asu.seatr.models.analyzers.task_kc.TK_A1;

public class Utilities {

	private static boolean isJUnitTest;
	public static Boolean checkExists(String st) {
		if(st == null || st.equals("")) {
			return false;
		} else {
			return true;
		}
	}

	public static Boolean checkExists(Boolean b) {
		return (b != null);
	}
	public static Boolean checkExists(Integer b) {
		return (b != null);
	}
	public static Boolean checkExists(Double b) {
		return (b != null);
	}

	public static String getTKTableNameByAnalyzerId(Integer analyzer_id) {
		String tka = "TK_A";
		return tka + analyzer_id.toString();
	}

	public static boolean isInteger(String s, int radix) {
		if(s.isEmpty()) return false;
		for(int i = 0; i < s.length(); i++) {
			if(i == 0 && s.charAt(i) == '-') {
				if(s.length() == 1) return false;
				else continue;
			}
			if(Character.digit(s.charAt(i),radix) < 0) return false;
		}
		return true;
	}

	public static Class getTKClass(Integer from_analyzer_id) {

		switch(from_analyzer_id) {
		case 1: return TK_A1.class;
		default: return null;
		}

	}

	public static boolean isJUnitTest() {
		return isJUnitTest;
	}

	public static void setJUnitTest(boolean pIsJUnitTest) {
		isJUnitTest = pIsJUnitTest;
	}
	

}
