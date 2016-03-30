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
import javax.persistence.UniqueConstraint;

import com.asu.seatr.models.analyzers.kc.*;
import com.asu.seatr.models.analyzers.task_kc.TK_A1;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "kc", uniqueConstraints = @UniqueConstraint(columnNames = {"external_id","course_id"}))
public class KnowledgeComponent {
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;
	
	@Column(name = "external_id")
	private String external_id;
	
	@ManyToOne(targetEntity = Course.class)
	@JoinColumn(name = "course_id", referencedColumnName = "id")
	private Course course;

	@OneToMany(mappedBy = "kc", cascade=CascadeType.ALL)
	private List<K_A1> K_A1;
	
	@OneToMany(mappedBy = "kc", cascade=CascadeType.ALL)
	private List<TK_A1> TK_A1;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getExternal_id() {
		return external_id;
	}

	public void setExternal_id(String external_id) {
		this.external_id = external_id;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public List<K_A1> getK_A1() {
		return K_A1;
	}

	public void setK_A1(List<K_A1> k_A1) {
		K_A1 = k_A1;
	}

	public List<TK_A1> getTK_A1() {
		return TK_A1;
	}

	public void setTK_A1(List<TK_A1> tK_A1) {
		TK_A1 = tK_A1;
	}
	
	


}
