package net.project.hibernate.model;

import java.io.Serializable;

import javax.persistence.Column;

public class PnAssignmentMaterialPK implements Serializable {
	
	private Integer spaceId;

	private Integer materialId;

	private Integer objectId;

	public PnAssignmentMaterialPK() {
	}

	public PnAssignmentMaterialPK(Integer spaceId, Integer materialId, Integer objectId) {
		this.spaceId = spaceId;
		this.materialId = materialId;
		this.objectId = objectId;
	}

	@Column(name = "SPACE_ID", nullable = false, length = 20)
	public Integer getSpaceId() {
		return this.spaceId;
	}

	public void setSpaceId(Integer spaceId) {
		this.spaceId = spaceId;
	}

	@Column(name = "MATERIAL_ID", nullable = false, length = 20)
	public Integer getMaterialId() {
		return this.materialId;
	}

	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
	}

	@Column(name = "OBJECT_ID", nullable = false, length = 20)
	public Integer getObjectId() {
		return this.objectId;
	}

	public void setObjectId(Integer objectId) {
		this.objectId = objectId;
	}

}
