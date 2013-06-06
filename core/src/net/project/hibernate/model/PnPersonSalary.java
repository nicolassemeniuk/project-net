package net.project.hibernate.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PN_PERSON_SALARY")
public class PnPersonSalary implements Serializable {
	
	private Integer personId;
	
	private Float costByHour;
	
	public PnPersonSalary(){
		
	}

	public PnPersonSalary(Integer personId, Float costByHour) {
		super();
		this.personId = personId;
		this.costByHour = costByHour;
	}

	@Id
	@ManyToOne(targetEntity = PnPerson.class)
	@JoinColumn(name = "PERSON_ID")
	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	@Column(name = "COST_BY_HOUR", nullable = false, length = 22)
	public Float getCostByHour() {
		return costByHour;
	}

	public void setCostByHour(Float costByHour) {
		this.costByHour = costByHour;
	}
	
	

}
