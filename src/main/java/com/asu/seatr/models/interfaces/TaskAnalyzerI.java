package com.asu.seatr.models.interfaces;

import java.util.HashMap;

import com.asu.seatr.exceptions.CourseNotFoundException;
import com.asu.seatr.models.Task;

public interface TaskAnalyzerI {
	public int getId();
	public void setId(int id);
	public void createTask(String task_ext_id,String course_id,int analyzer_id) throws CourseNotFoundException;
	public void deleteTask(String task_ext_id,String course_id,int analyzer_id);
	public void updateTask(String task_ext_id,String course_id,int analyzer_id);
	public Task getTask(String task_ext_id,String course_id,int analyzer_id);
}
