package com.asu.seatr.models.interfaces;

import java.util.HashMap;

import com.asu.seatr.models.Course;


public interface CourseAnalyzerI {
	public int getId();
	public void setId(int id);
	public void createCourse(String course_id);
	public void deleteCourse(int course_id, int analyzer_id);
	public void updateCourse(int course_id, int analyzer_id);
	public Course getCourse(int course_id, int analyzer_id);
}
