package com.asu.seatr.models.analyzers.studenttask;

import java.util.HashMap;

import com.asu.seatr.models.StudentTask;
import com.asu.seatr.models.interfaces.StudentTaskAnalyzerI;

public class ST_A1 implements StudentTaskAnalyzerI{
	
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createStudentTask(int student_id, int course_id, int task_id, int analyzer_id,
			HashMap<String, String> properties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteStudentTask(int student_id, int course_id, int task_id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateStudentTask(int student_ext_id, int course_id, int task_id, int analyzer_id,
			HashMap<String, String> properties) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public StudentTask getStudentTask(int student_ext_id, int course_id, int analyzer_id) {
		// TODO Auto-generated method stub
		return null;
	}



}
