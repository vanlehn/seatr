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

import org.hibernate.annotations.GenericGenerator;

import com.asu.seatr.models.analyzers.kc.KC_UnansweredTasks;
import com.asu.seatr.models.analyzers.task_kc.TaskKC_UnansweredTasks;

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
	private List<KC_UnansweredTasks> K_A1;

	@OneToMany(mappedBy = "kc", cascade=CascadeType.ALL)
	private List<TaskKC_UnansweredTasks> TK_A1;

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

	public List<KC_UnansweredTasks> getK_A1() {
		return K_A1;
	}

	public void setK_A1(List<KC_UnansweredTasks> k_A1) {
		K_A1 = k_A1;
	}

	public List<TaskKC_UnansweredTasks> getTK_A1() {
		return TK_A1;
	}

	public void setTK_A1(List<TaskKC_UnansweredTasks> tK_A1) {
		TK_A1 = tK_A1;
	}




}
