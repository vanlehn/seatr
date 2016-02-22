package com.asu.seatr.models.analyzers.kc;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.asu.seatr.models.KnowledgeComponent;
import com.asu.seatr.models.interfaces.KCAnalyzerI;

@Entity
@Table(name = "k_a1")
public class K_A1 implements KCAnalyzerI{
	
	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	private int id;

	@ManyToOne
	@JoinColumn(name = "kc_id", referencedColumnName = "id")
	private KnowledgeComponent kc;
	
	//properties that do not change
	@Column(name = "s_unit")
	private Integer s_unit;
	@Override
	public int getId() {
		return id;
	}
	@Override
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public KnowledgeComponent getKc() {
		return kc;
	}
	@Override
	public void setKc(KnowledgeComponent kc) {
		this.kc = kc;
	}

	public Integer getS_unit() {
		return s_unit;
	}

	public void setS_unit(Integer s_unit) {
		this.s_unit = s_unit;
	}
	

	


}
