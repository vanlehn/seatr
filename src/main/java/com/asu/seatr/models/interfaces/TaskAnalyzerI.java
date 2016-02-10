package com.asu.seatr.models.interfaces;

import java.util.HashMap;

import com.asu.seatr.models.Task;

public interface TaskAnalyzerI {
	public void createTask(int task_ext_id,int course_id,int analyzer_id,HashMap<String,String> properties);
	public void deleteTask(int task_ext_id,int course_id,int analyzer_id);
	public void updateTask(int task_ext_id,int course_id,int analyzer_id,HashMap<String,String> properties);
	public Task getTask(int task_ext_id,int course_id,int analyzer_id);
}
