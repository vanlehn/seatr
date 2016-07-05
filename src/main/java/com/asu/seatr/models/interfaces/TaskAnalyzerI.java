package com.asu.seatr.models.interfaces;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.models.Task;

public interface TaskAnalyzerI {
	public int getId();
	public void setId(int id);
	public void createTask(String task_ext_id,String course_id,int analyzer_id) throws CourseException, TaskException;
	public void deleteTask(String task_ext_id,String course_id,int analyzer_id);
	public void updateTask(String task_ext_id,String course_id,int analyzer_id);
	public Task getTask(String task_ext_id,String course_id,int analyzer_id);
	public Task getTask();
	public void setTask(Task task);
}
