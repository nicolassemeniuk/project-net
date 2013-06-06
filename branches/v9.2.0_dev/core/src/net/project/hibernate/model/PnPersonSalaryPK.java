package net.project.hibernate.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PnPersonSalaryPK implements Serializable {

	private Integer personId;

	public PnPersonSalaryPK(){
		
	}
	
	public PnPersonSalaryPK(Integer personId) {
		super();
		this.personId = personId;
	}
	
	@Column(name = "PERSON_ID", nullable = false, length = 20)
	public Integer getPersonId() {
		return this.personId;
	}

	public void setPersonId(Integer personId) {
		this.personId = personId;
	}
	
	

}
