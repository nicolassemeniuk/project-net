package net.project.hibernate.model;

import java.io.Serializable;
import java.util.Date;

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
	
	private Integer personId;
	
	private Date startDate;
	
	private Date endDate;
	
	private Float costByHour;
	
	private String recordStatus; 
	
	public static final String OBJECT_TYPE = "salary";
	
	public PnPersonSalary(){
		
	}

	public PnPersonSalary(PnPersonSalaryPK comp_id, Integer personId, Date startDate, Date endDate, Float costByHour, String recordStatus) {
		super();
		this.comp_id = comp_id;
		this.personId = personId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.costByHour = costByHour;
		this.recordStatus = recordStatus;
	}


	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "personSalaryId", column = @Column(name = "PERSON_SALARY_ID", nullable = false, length = 20)) })	
	public PnPersonSalaryPK getComp_id() {
		return comp_id;
	}

	public void setComp_id(PnPersonSalaryPK comp_id) {
		this.comp_id = comp_id;
	}	

	@Column(name = "PERSON_ID", nullable = false, length = 20)
	public Integer getPersonId() {
		return personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}

	@Column(name = "START_DATE", nullable = false)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name = "END_DATE")
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Column(name = "COST_BY_HOUR", nullable = false, length = 22)
	public Float getCostByHour() {
		return costByHour;
	}

	public void setCostByHour(Float costByHour) {
		this.costByHour = costByHour;
	}

	@Column(name = "RECORD_STATUS", nullable = false, length = 1)
	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

}
