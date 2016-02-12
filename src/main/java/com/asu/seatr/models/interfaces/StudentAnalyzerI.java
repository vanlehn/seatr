package com.asu.seatr.models.interfaces;

import java.util.HashMap;

import com.asu.seatr.models.Student;

public interface StudentAnalyzerI {
	public int getId();
	public void setId(int id);
	public void createStudent(int student_ext_id,int course_id,int analyzer_id,HashMap<String,String> properties);
	public void deleteStudent(int student_ext_id,int course_id, int analyzer_id);
	public void updateStudent(int student_ext_id,int course_id, int analyzer_id,HashMap<String,String> properties);
	public Student getStudent(int student_ext_id,int course_id, int analyzer_id);
}
