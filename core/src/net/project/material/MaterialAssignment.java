package net.project.material;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import net.project.hibernate.model.PnAssignmentMaterial;
import net.project.hibernate.service.ServiceFactory;

public class MaterialAssignment implements Serializable{
	
	private String spaceId;
	private String objectId;
	private String materialId;
	private BigDecimal percentAssigned;
	private String recordStatus;
	private Date startDate;
	private Date endDate;
	private Date dateCreated;
	private Date modifiedDate;
	private String modifiedBy;
	private String assignorId;
	
	public MaterialAssignment(){		
	}
	
	public MaterialAssignment(PnAssignmentMaterial assignedMaterial){
		this.percentAssigned = assignedMaterial.getPercentAllocated();
		this.recordStatus = assignedMaterial.getRecordStatus();
		this.startDate = assignedMaterial.getStartDate();
		this.endDate = assignedMaterial.getEndDate();
		this.dateCreated = assignedMaterial.getDateCreated();
		this.modifiedDate = assignedMaterial.getModifiedDate();
		this.modifiedBy = String.valueOf(assignedMaterial.getModifiedBy());
		this.assignorId = String.valueOf(assignedMaterial.getPnAssignor().getPersonId());	
	}
	
	public void load(){
		PnAssignmentMaterial assignedMaterial = ServiceFactory.getInstance().getPnAssignmentMaterialService().getAssignmentMaterial(spaceId, materialId, objectId);
		this.percentAssigned = assignedMaterial.getPercentAllocated();
		this.recordStatus = assignedMaterial.getRecordStatus();
		this.startDate = assignedMaterial.getStartDate();
		this.endDate = assignedMaterial.getEndDate();
		this.dateCreated = assignedMaterial.getDateCreated();
		this.modifiedDate = assignedMaterial.getModifiedDate();
		this.modifiedBy = String.valueOf(assignedMaterial.getModifiedBy());
		this.assignorId = String.valueOf(assignedMaterial.getPnAssignor().getPersonId());		
	}

	public String getSpaceId() {
		return spaceId;
	}

	public String getObjectId() {
		return objectId;
	}

	public String getMaterialId() {
		return materialId;
	}

	public BigDecimal getPercentAssigned() {
		return percentAssigned;
	}

	public String getRecordStatus() {
		return recordStatus;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public String getAssignorId() {
		return assignorId;
	}

	public void setSpaceId(String spaceId) {
		this.spaceId = spaceId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public void setPercentAssigned(BigDecimal percentAssigned) {
		this.percentAssigned = percentAssigned;
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

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setAssignorId(String assignorId) {
		this.assignorId = assignorId;
	}
	
	

}
