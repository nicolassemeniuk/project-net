package net.project.hibernate.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PN_PERSON_SALARY")
public class PnPersonSalary implements Serializable {
	
	private PnPersonSalaryPK comp_id;
	
	private Float costByHour;
	
	public PnPersonSalary(){
		
	}

	public PnPersonSalary(PnPersonSalaryPK comp_id, Float costByHour) {
		super();
		this.comp_id = comp_id;
		this.costByHour = costByHour;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "personId", column = @Column(name = "PERSON_ID", nullable = false, length = 20)) })
	
	public PnPersonSalaryPK getComp_id() {
		return comp_id;
	}

	public void setComp_id(PnPersonSalaryPK comp_id) {
		this.comp_id = comp_id;
	}

	@Column(name = "COST_BY_HOUR", nullable = false, length = 22)
	public Float getCostByHour() {
		return costByHour;
	}

	public void setCostByHour(Float costByHour) {
		this.costByHour = costByHour;
	}
	
	

}
