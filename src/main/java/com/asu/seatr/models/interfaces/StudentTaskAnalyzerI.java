package com.asu.seatr.models.interfaces;

import java.util.HashMap;

import com.asu.seatr.models.StudentTask;

public interface StudentTaskAnalyzerI {
	public void createStudentTask(int student_id, int course_id, int task_id, int analyzer_id, HashMap<String,String> properties);
	public void deleteStudentTask(int student_id, int course_id, int task_id);
	public void updateStudentTask(int student_ext_id,int course_id, int task_id, int analyzer_id, HashMap<String,String> properties);
	public StudentTask getStudentTask(int student_ext_id,int course_id, int analyzer_id);
}
