package net.project.hibernate.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PN_FINANCIAL_SPACE")
public class PnFinancialSpace implements Serializable {
	
	private Integer financialSpaceId;

	private String financialSpaceName;
	
	private String financialSpaceDescription;
	
	private String recordStatus;
	
	public static final String OBJECT_TYPE = "financial";
	
	public PnFinancialSpace(){
		
	}
	
	public PnFinancialSpace(Integer financialId, String financialName, String financialSpaceDescription){
		this.financialSpaceId = financialId;
		this.financialSpaceName = financialName;
		this.financialSpaceDescription = financialSpaceDescription;
	}

	@Id
	@Column(name = "FINANCIAL_SPACE_ID", nullable = false, length = 20)
	public Integer getFinancialSpaceId() {
		return financialSpaceId;
	}

	public void setFinancialSpaceId(Integer financialId) {
		this.financialSpaceId = financialId;
	}

	@Column(name = "FINANCIAL_SPACE_NAME", nullable = false, length = 40)
	public String getFinancialSpaceName() {
		return financialSpaceName;
	}

	public void setFinancialSpaceName(String financialName) {
		this.financialSpaceName = financialName;
	}
	
	@Column(name = "FINANCIAL_SPACE_DESCRIPTION", nullable = false, length = 240)
	public String getFinancialSpaceDescription() {
		return financialSpaceDescription;
	}

	public void setFinancialSpaceDescription(String financialDescription) {
		this.financialSpaceDescription = financialDescription;
	}
	
	
	@Column(name = "RECORD_STATUS", nullable = false, length = 1)
	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	
	

}
