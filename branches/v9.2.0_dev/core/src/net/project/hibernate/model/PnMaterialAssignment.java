package net.project.hibernate.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PN_MATERIAL_ASSIGNMENT")
public class PnMaterialAssignment{
	
	private PnMaterialAssignmentPK comp_id;
	
	private BigDecimal percentAllocated;	

	private String recordStatus;
	
	private Date startDate;

	private Date endDate;
	
	private Date dateCreated;

	private Date modifiedDate;

	private Integer modifiedBy;	

	private PnPerson pnAssignor;
	
	public PnMaterialAssignment() {
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "spaceId", column = @Column(name = "SPACE_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "materialId", column = @Column(name = "MATERIAL_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "objectId", column = @Column(name = "OBJECT_ID", nullable = false, length = 20)) })
	public PnMaterialAssignmentPK getComp_id() {
		return comp_id;
	}

	@Column(name = "PERCENT_ALLOCATED", length = 22)
	public BigDecimal getPercentAllocated() {
		return percentAllocated;
	}

	@Column(name = "RECORD_STATUS", nullable = false, length = 1)
	public String getRecordStatus() {
		return recordStatus;
	}

	@Column(name = "START_DATE", length = 7)
	public Date getStartDate() {
		return startDate;
	}

	@Column(name = "END_DATE", length = 7)
	public Date getEndDate() {
		return endDate;
	}

	@Column(name = "DATE_CREATED", length = 7)
	public Date getDateCreated() {
		return dateCreated;
	}

	@Column(name = "MODIFIED_DATE", length = 7)
	public Date getModifiedDate() {
		return modifiedDate;
	}

	@Column(name = "MODIFIED_BY", length = 20)
	public Integer getModifiedBy() {
		return modifiedBy;
	}

	@ManyToOne(targetEntity = PnPerson.class)
	@JoinColumn(name = "ASSIGNOR_ID")
	public PnPerson getPnAssignor() {
		return pnAssignor;
	}

	public void setComp_id(PnMaterialAssignmentPK comp_id) {
		this.comp_id = comp_id;
	}

	public void setPercentAllocated(BigDecimal percentAllocated) {
		this.percentAllocated = percentAllocated;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setPnAssignor(PnPerson pnAssignor) {
		this.pnAssignor = pnAssignor;
	}


}
