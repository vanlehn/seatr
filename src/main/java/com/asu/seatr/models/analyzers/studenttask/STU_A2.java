package com.asu.seatr.models.analyzers.studenttask;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.asu.seatr.models.Student;
import com.asu.seatr.models.Task;


@Entity
@Table(name = "stu_a2")
public class STU_A2{
	//record student-task-utility
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "student_id", referencedColumnName = "id", nullable=false)//internal student id
	private Student student;
	
	@ManyToOne
	@JoinColumn(name = "task_id", referencedColumnName = "id", nullable=false)
	private Task task;

	@Column(name = "utility")
	private double utility;
	
	@Column(name ="timestamp")
	private long timestamp;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public double getUtility() {
		return utility;
	}

	public void setUtility(double utility) {
		this.utility = utility;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	

}
