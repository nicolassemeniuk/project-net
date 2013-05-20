package net.project.hibernate.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PN_FINANCIAL_SPACE")
public class PnFinancialSpace implements Serializable {
	
	private Integer financialId;

	private String financialName;
	
	private String recordStatus;
	
	public static final String OBJECT_TYPE = "financial_space";
	
	public PnFinancialSpace(){
		
	}
	
	public PnFinancialSpace(Integer financialId, String financialName){
		this.financialId = financialId;
		this.financialName = financialName;
	}

	@Id
	@Column(name = "FINANCIAL_SPACE_ID", nullable = false, length = 20)
	public Integer getFinancialSpaceId() {
		return financialId;
	}

	public void setFinancialSpaceId(Integer financialId) {
		this.financialId = financialId;
	}

	@Column(name = "FINANCIAL_SPACE_NAME", nullable = false, length = 40)
	public String getFinancialSpaceName() {
		return financialName;
	}

	public void setFinancialSpaceName(String financialName) {
		this.financialName = financialName;
	}
	
	@Column(name = "RECORD_STATUS", nullable = false, length = 1)
	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	
	

}
