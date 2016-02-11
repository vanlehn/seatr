package com.asu.seatr.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "student_task",uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "task_id"}))
public class StudentTask {
	
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increement", strategy = "increment")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "student_id", referencedColumnName = "id")//internal student id
	private int student_id;
	
	@ManyToOne
	@JoinColumn(name = "task_id", referencedColumnName = "id")
	private int task_id;	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStudent_id() {
		return student_id;
	}
	public void setStudent_id(int student_id) {
		this.student_id = student_id;
	}
	public int getTask_id() {
		return task_id;
	}
	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}
}
