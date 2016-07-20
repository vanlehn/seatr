package com.asu.seatr.models.analyzers.task;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.PropertyValueException;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.exception.ConstraintViolationException;

import com.asu.seatr.exceptions.CourseException;
import com.asu.seatr.exceptions.TaskException;
import com.asu.seatr.handlers.CourseHandler;
import com.asu.seatr.handlers.TaskHandler;
import com.asu.seatr.models.Course;
import com.asu.seatr.models.Task;
import com.asu.seatr.models.interfaces.TaskAnalyzerI;
import com.asu.seatr.utils.MyMessage;
import com.asu.seatr.utils.MyStatus;

@Entity
@Table(name = "t_bkt", uniqueConstraints = @UniqueConstraint(columnNames = {"task_id","course_id"}))
public class Task_BKT implements TaskAnalyzerI{
	
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
	
	@Column(name = "difficulty")
	private double difficulty;
	
	@Column(name = "type")
	private String type;
	
	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
		
	}
	@Override
	public Task getTask() {
		return task;
	}
	@Override
	public void setTask(Task task) {
		this.task = task;
	}

	public double getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(double difficulty) {
		this.difficulty = difficulty;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void createTask(String external_task_id, String external_course_id, int analyzer_id) throws CourseException, TaskException {
		Course course = CourseHandler.getByExternalId(external_course_id);

		if(course == null) {
			throw new CourseException(MyStatus.ERROR, MyMessage.COURSE_NOT_FOUND);
		}
		Task task = new Task();
		task.setExternal_id(external_task_id);
		task.setCourse(course);
		try {
			task = TaskHandler.save(task);
		}catch(ConstraintViolationException cve) {
			task = TaskHandler.readByExtTaskId_Course(external_task_id, course);
			//throw new TaskException(MyStatus.ERROR, MyMessage.TASK_ALREADY_PRESENT);

		}
		catch(PropertyValueException pve) {
			throw new TaskException(MyStatus.ERROR, MyMessage.TASK_PROPERTY_NULL);
		}
		this.task = task;
		this.course = course;
		
	}

	@Override
	public void deleteTask(String task_ext_id, String course_id, int analyzer_id) {
		
	}

	@Override
	public void updateTask(String task_ext_id, String course_id, int analyzer_id) {
		
	}

	@Override
	public Task getTask(String task_ext_id, String course_id, int analyzer_id) {
		return null;
	}

}
