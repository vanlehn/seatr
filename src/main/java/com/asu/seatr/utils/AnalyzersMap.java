package com.asu.seatr.utils;

import java.util.HashMap;
import java.util.Map;

import com.asu.seatr.models.analyzers.student.S_A1;

public class AnalyzersMap {
	public static Map<Integer, Class> studentAnalyzers = new HashMap<Integer, Class>(){{
		put(1, S_A1.class);		
	}};
}
