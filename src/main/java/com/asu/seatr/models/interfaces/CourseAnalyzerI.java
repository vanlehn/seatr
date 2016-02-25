package com.asu.seatr.models.interfaces;

import java.util.HashMap;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.models.Course;


public interface CourseAnalyzerI {
	public int getId();
	public void setId(int id);
	public void createCourse(String external_course_id, String description) throws CourseException;
	public void deleteCourse(String external_course_id, int analyzer_id);
	public void updateCourse(String external_course_id, String description) throws CourseException;
	public Course getCourse(String external_course_id, int analyzer_id);
}
