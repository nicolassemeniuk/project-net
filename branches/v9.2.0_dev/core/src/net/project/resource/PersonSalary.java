package net.project.resource;

import java.util.Date;

import net.project.hibernate.model.PnPersonSalary;
import net.project.hibernate.service.ServiceFactory;

public class PersonSalary {
	
	private String personSalaryId=null;
	
	private String personId=null;
	
	private Date startDate=null;
	
	private Date endDate=null;
	
	private String costByHour=null;
	
	private String recordStatus=null;
	
	public PersonSalary(){		
	}

	public PersonSalary(String personSalaryId, String personId, Date startDate, Date endDate, String costByHour, String recordStatus) {
		super();
		this.personSalaryId = personSalaryId;
		this.personId = personId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.costByHour = costByHour;
		this.recordStatus = recordStatus;
	}
	
	public PersonSalary(String personId){
		PnPersonSalary pnPersonSalary = ServiceFactory.getInstance().getPnPersonSalaryService().getCurrentPersonSalaryByPersonId(Integer.valueOf(personId));
		this.personSalaryId = String.valueOf(pnPersonSalary.getComp_id().getPersonSalaryId());
		this.personId = String.valueOf(pnPersonSalary.getPersonId());
		this.startDate = pnPersonSalary.getStartDate();
		this.endDate = pnPersonSalary.getEndDate();
		this.costByHour = String.valueOf(pnPersonSalary.getCostByHour());
		this.recordStatus = pnPersonSalary.getRecordStatus();
	}
	
	public PersonSalary(PnPersonSalary pnPersonSalary){
		this.personSalaryId = String.valueOf(pnPersonSalary.getComp_id().getPersonSalaryId());
		this.personId = String.valueOf(pnPersonSalary.getPersonId());
		this.startDate = pnPersonSalary.getStartDate();
		this.endDate = pnPersonSalary.getEndDate();
		this.costByHour = String.valueOf(pnPersonSalary.getCostByHour());
		this.recordStatus = pnPersonSalary.getRecordStatus();
	}
	
	public void load(){
		PnPersonSalary pnPersonSalary = ServiceFactory.getInstance().getPnPersonSalaryService().getPersonSalaryById(this.personSalaryId);
		this.personSalaryId = String.valueOf(pnPersonSalary.getComp_id().getPersonSalaryId());
		this.personId = String.valueOf(pnPersonSalary.getPersonId());
		this.startDate = pnPersonSalary.getStartDate();
		this.endDate = pnPersonSalary.getEndDate();
		this.costByHour = String.valueOf(pnPersonSalary.getCostByHour());
		this.recordStatus = pnPersonSalary.getRecordStatus();
	}

	public String getPersonSalaryId() {
		return personSalaryId;
	}

	public void setPersonSalaryId(String personSalaryId) {
		this.personSalaryId = personSalaryId;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getCostByHour() {
		return costByHour;
	}

	public void setCostByHour(String costByHour) {
		this.costByHour = costByHour;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	} 
	
	

}
