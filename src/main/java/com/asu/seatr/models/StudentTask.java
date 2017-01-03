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

import com.asu.seatr.models.analyzers.studenttask.StudentTask_UnansweredTasks;
import com.asu.seatr.models.analyzers.studenttask.StudentTask_BKT;
import com.asu.seatr.models.analyzers.studenttask.StudentTask_N_In_A_Row;
import com.asu.seatr.models.analyzers.studenttask.StudentTask_Required_Optional;

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
	private List<StudentTask_UnansweredTasks> ST_A1;

	@OneToMany(mappedBy = "studentTask", cascade=CascadeType.ALL)
	private List<StudentTask_N_In_A_Row> ST_A2;
	
	@OneToMany(mappedBy = "studentTask", cascade=CascadeType.ALL)
	private List<StudentTask_Required_Optional> ST_A3;
	
	@OneToMany(mappedBy = "studentTask", cascade=CascadeType.ALL)
	private List<StudentTask_BKT> ST_BKT;


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
	public List<StudentTask_UnansweredTasks> getST_A1() {
		return ST_A1;
	}
	public void setST_A1(List<StudentTask_UnansweredTasks> sT_A1) {
		ST_A1 = sT_A1;
	}
	public List<StudentTask_N_In_A_Row> getST_A2() {
		return ST_A2;
	}
	public void setST_A2(List<StudentTask_N_In_A_Row> sT_A2) {
		ST_A2 = sT_A2;
	}
	public List<StudentTask_Required_Optional> getST_A3() {
		return ST_A3;
	}
	public void setST_A3(List<StudentTask_Required_Optional> sT_A3) {
		ST_A3 = sT_A3;
	}
	public List<StudentTask_BKT> getST_BKT() {
		return ST_BKT;
	}
	public void setST_BKT(List<StudentTask_BKT> sT_BKT) {
		ST_BKT = sT_BKT;
	}

}
