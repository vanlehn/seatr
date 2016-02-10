package com.asu.seatr.models.interfaces;

import java.util.HashMap;

import com.asu.seatr.models.Course;


public interface CourseAnalyzerI {
	public void createCourse(int course_id,int analyzer_id,HashMap<String,String> properties);
	public void deleteCourse(int course_id, int analyzer_id);
	public void updateCourse(int course_id, int analyzer_id, HashMap<String,String> properties);
	public Course getCourse(int course_id, int analyzer_id);
}
