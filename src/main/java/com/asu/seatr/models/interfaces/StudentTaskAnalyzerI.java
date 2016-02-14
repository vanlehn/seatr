package com.asu.seatr.models.interfaces;

import java.util.HashMap;

import com.asu.seatr.models.StudentTask;

public interface StudentTaskAnalyzerI {
	public int getId();
	public void setId(int id);
	public void createStudentTask(String external_student_id, Integer external_course_id, String external_task_id, int analyzer_id);
	public void deleteStudentTask(String external_student_id, Integer external_course_id, int task_id);
	public void updateStudentTask(String external_student_id, Integer external_course_id, String external_task_id, int analyzer_id);
	public StudentTask getStudentTask(String external_student_id, Integer external_course_id, int task_id);
}
