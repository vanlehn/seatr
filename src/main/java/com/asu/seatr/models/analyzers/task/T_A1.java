package com.asu.seatr.models.analyzers.task;

import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.annotations.GenericGenerator;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.interfaces.TaskAnalyzerI;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyResponse;
import com.asu.seatr.utils.MyStatus;

@Entity
@Table(name = "t_a1", uniqueConstraints = @UniqueConstraint(columnNames = {"task_id","course_id"}))
public class T_A1 implements TaskAnalyzerI{
	
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "task_id", referencedColumnName = "id", nullable=false)
	private Task task;
	
	@ManyToOne
	@JoinColumn(name = "course_id", referencedColumnName = "id", nullable=false)
	private Course course;
	
	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	//properties that do not change
	@Column(name = "s_difficulty_level")
	private Integer s_difficulty_level;
	
	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub
		this.id = id;
		
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Integer getS_difficulty_level() {
		return s_difficulty_level;
	}

	public void setS_difficulty_level(Integer s_difficulty_level) {
		this.s_difficulty_level = s_difficulty_level;
	}

	@Override
	public void createTask(String task_ext_id, String external_course_id, int analyzer_id) throws CourseException, TaskException {
		// TODO Auto-generated method stub
		Course course = CourseHandler.getByExternalId(external_course_id);
		if(course == null) {
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);
		}
		Task task = new Task();
		task.setExternal_id(task_ext_id);
		task.setCourse(course);
		try {
			task = TaskHandler.save(task);
		} catch(ConstraintViolationException e) {
			throw new TaskException(MyStatus.ERROR, MyMessage.TASK_ALREADY_PRESENT);
		}
		this.task = task;
		this.course = course;
		
	}

	@Override
	public void deleteTask(String task_ext_id, String course_id, int analyzer_id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateTask(String task_ext_id, String course_id, int analyzer_id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Task getTask(String task_ext_id, String course_id, int analyzer_id) {
		// TODO Auto-generated method stub
		return null;
	}

}
