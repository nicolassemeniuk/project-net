package net.project.material;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import net.project.calendar.PnCalendar;
import net.project.hibernate.model.PnMaterialAssignment;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.PnTask;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.util.time.ITimeRangeValue;
import net.project.xml.XMLUtils;

public class MaterialAssignment implements Serializable, ITimeRangeValue {

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

	/** true if it's overassigned */
	private Boolean overassigned;
	private String materialName;
	private String taskName;
	private String spaceName;

	public MaterialAssignment() {
	}

	public MaterialAssignment(PnMaterialAssignment assignedMaterial) {
		this.spaceId = String.valueOf(assignedMaterial.getComp_id().getSpaceId());
		this.objectId = String.valueOf(assignedMaterial.getComp_id().getObjectId());
		this.materialId = String.valueOf(assignedMaterial.getComp_id().getMaterialId());
		this.percentAssigned = assignedMaterial.getPercentAllocated();
		this.recordStatus = assignedMaterial.getRecordStatus();
		this.startDate = assignedMaterial.getStartDate();
		this.endDate = assignedMaterial.getEndDate();
		this.dateCreated = assignedMaterial.getDateCreated();
		this.modifiedDate = assignedMaterial.getModifiedDate();
		this.modifiedBy = String.valueOf(assignedMaterial.getModifiedBy());
		this.assignorId = String.valueOf(assignedMaterial.getPnAssignor().getPersonId());
		this.overassigned = ServiceFactory.getInstance().getPnMaterialAssignmentService().isOverassigned(startDate, endDate, spaceId, materialId, objectId);
		this.materialName = ServiceFactory.getInstance().getPnMaterialService().getMaterial(Integer.valueOf(materialId)).getMaterialName();
		PnTask task = ServiceFactory.getInstance().getPnTaskService().getTaskById(Integer.valueOf(assignedMaterial.getComp_id().getObjectId()));
		this.taskName = task.getTaskName();
		PnProjectSpace space = ServiceFactory.getInstance().getPnProjectSpaceService()
				.getProjectSpace(Integer.valueOf(assignedMaterial.getComp_id().getSpaceId()));
		this.spaceName = space.getProjectName();
	}

	public void load() {
		PnMaterialAssignment assignedMaterial = ServiceFactory.getInstance().getPnMaterialAssignmentService()
				.getMaterialAssignment(spaceId, materialId, objectId);
		this.spaceId = String.valueOf(assignedMaterial.getComp_id().getSpaceId());
		this.objectId = String.valueOf(assignedMaterial.getComp_id().getObjectId());
		this.materialId = String.valueOf(assignedMaterial.getComp_id().getMaterialId());
		this.percentAssigned = assignedMaterial.getPercentAllocated();
		this.recordStatus = assignedMaterial.getRecordStatus();
		this.startDate = assignedMaterial.getStartDate();
		this.endDate = assignedMaterial.getEndDate();
		this.dateCreated = assignedMaterial.getDateCreated();
		this.modifiedDate = assignedMaterial.getModifiedDate();
		this.modifiedBy = String.valueOf(assignedMaterial.getModifiedBy());
		this.assignorId = String.valueOf(assignedMaterial.getPnAssignor().getPersonId());
		this.overassigned = ServiceFactory.getInstance().getPnMaterialAssignmentService().isOverassigned(startDate, endDate, spaceId, materialId, objectId);
		this.materialName = ServiceFactory.getInstance().getPnMaterialService().getMaterial(Integer.valueOf(materialId)).getMaterialName();
		PnTask task = ServiceFactory.getInstance().getPnTaskService().getTaskById(Integer.valueOf(assignedMaterial.getComp_id().getObjectId()));
		this.taskName = task.getTaskName();
		PnProjectSpace space = ServiceFactory.getInstance().getPnProjectSpaceService()
				.getProjectSpace(Integer.valueOf(assignedMaterial.getComp_id().getSpaceId()));
		this.spaceName = space.getProjectName();
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

	public Boolean getOverassigned() {
		return overassigned;
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

	public void setOverassigned(Boolean overassigned) {
		this.overassigned = overassigned;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	/**
	 * This has to be because we implement the ITimeRangeValue interface. In our
	 * case we don't need the allocation value, because a material can only be
	 * assigned to one task at a time. So we return 100%.
	 */
	@Override
	public BigDecimal getValue() {
		return BigDecimal.valueOf(100.00);
	}

	public Object clone() {
		MaterialAssignment clone = new MaterialAssignment();

		clone.setAssignorId(this.getAssignorId());
		clone.setDateCreated(this.getDateCreated());
		clone.setEndDate(this.getEndDate());
		clone.setMaterialId(this.getMaterialId());
		clone.setModifiedBy(this.getModifiedBy());
		clone.setModifiedDate(this.getModifiedDate());
		clone.setObjectId(this.getObjectId());
		clone.setOverassigned(this.getOverassigned());
		clone.setPercentAssigned(this.getPercentAssigned());
		clone.setRecordStatus(this.getRecordStatus());
		clone.setSpaceId(this.getSpaceId());
		clone.setStartDate(this.getStartDate());
		clone.setMaterialName(this.getMaterialName());

		return clone;
	}

	/**
	 * Converts the object to XML representation without the XML version tag.
	 * This method returns the object as XML text.
	 * 
	 * @return XML representation
	 */
	public String getXMLBody() {
		StringBuffer xml = new StringBuffer();
		xml.append("<assignment>\n");
		xml.append(getXMLElements());
		xml.append("</assignment>\n");
		return xml.toString();
	}

	/**
	 * Returns the elements contained within an <code>&lt;Assignment&gt;</code>.
	 * 
	 * @return the XML elements
	 */
	protected String getXMLElements() {

		StringBuffer xml = new StringBuffer();

		xml.append("<object_name>" + XMLUtils.escape(taskName) + "</object_name>\n");
		xml.append("<space_name>" + XMLUtils.escape(spaceName) + "</space_name>\n");
		xml.append("<percent_assigned>" + XMLUtils.escape(String.valueOf(percentAssigned)) + "</percent_assigned>\n");
		xml.append("<start_time>" + XMLUtils.formatISODateTime(getStartDate()) + "</start_time>");
		xml.append("<end_time>" + XMLUtils.formatISODateTime(getEndDate()) + "</end_time>");
		
        PnCalendar cal = new PnCalendar(SessionManager.getUser());
        if (getStartDate() != null && cal.startOfDay(getStartDate()).equals(cal.startOfDay(getEndDate()))) {
            xml.append("<one_day_assignment/>");
        }

		return xml.toString();
	}
}
