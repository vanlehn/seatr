package com.asu.seatr.models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table( name="user")
public class User {
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;

	@Column(name = "username", nullable=false, unique=true)
	private String username;

	@Column(name = "password", nullable=false)
	private String password;

	@OneToMany(mappedBy = "user", cascade=CascadeType.ALL)
	private List<UserCourse> UserCourse;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<UserCourse> getUserCourse() {
		return UserCourse;
	}

	public void setUserCourse(List<UserCourse> userCourse) {
		UserCourse = userCourse;
	}

}