package com.asu.seatr.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.asu.seatr.models.analyzers.studenttask.ST_A1;
import com.asu.seatr.models.analyzers.studenttask.ST_A3;

@Entity
@Table(name = "student_task")
public class StudentTask {

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

	@Column(name = "timestamp")
	private Long timestamp;

	@OneToMany(mappedBy = "studentTask", cascade=CascadeType.ALL)
	private List<ST_A1> ST_A1;

	@OneToMany(mappedBy = "studentTask", cascade=CascadeType.ALL)
	private List<ST_A3> ST_A3;


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
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public List<ST_A1> getST_A1() {
		return ST_A1;
	}
	public void setST_A1(List<ST_A1> sT_A1) {
		ST_A1 = sT_A1;
	}

}
