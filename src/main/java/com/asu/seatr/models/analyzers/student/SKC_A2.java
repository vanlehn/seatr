package com.asu.seatr.models.analyzers.student;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.Student;

@Entity
@Table(name = "skc_a2")
public class SKC_A2{
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "student_id", referencedColumnName = "id", nullable=false)//internal student id
	private Student student;
	
	@ManyToOne
	@JoinColumn(name = "kc_id", referencedColumnName = "id", nullable=false)
	private KnowledgeComponent kc;

	@Column(name = "n_in_a_row")
	private Integer number; //  number of correctness in a row

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

	public KnowledgeComponent getKc() {
		return kc;
	}

	public void setKc(KnowledgeComponent kc) {
		this.kc = kc;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}
	

}