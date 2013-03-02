package net.project.hibernate.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PN_MATERIAL")
public class PnMaterial implements Serializable {

	private Integer materialId;

	private String materialName;

	private String materialDescription;

	private PnMaterialType materialType;

	private Float materialCost;
	
	private String recordStatus;

	public static final String OBJECT_TYPE = "material";

	public PnMaterial() {
	}

	public PnMaterial(Integer materialId, String materialName, String materialDescription, PnMaterialType materialType, Float materialCost, String recordStatus) {
		this.materialId = materialId;
		this.materialName = materialName;
		this.materialDescription = materialDescription;
		this.materialType = materialType;
		this.materialCost = materialCost;
		this.recordStatus = recordStatus;
	}
	
//	public PnMaterial(Integer materialId, String materialName, String materialDescription, Float materialCost) {
//		this.materialId = materialId;
//		this.materialName = materialName;
//		this.materialDescription = materialDescription;
//		this.materialType = new PnMaterialType(1, "Test");
//		this.materialCost = materialCost;
//		
//	}

	@Id
	@Column(name = "MATERIAL_ID", nullable = false, length = 20)
	public Integer getMaterialId() {
		return materialId;
	}

	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
	}

	@Column(name = "MATERIAL_NAME", nullable = false, length = 40)
	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	@Column(name = "MATERIAL_DESCRIPTION", nullable = false, length = 240)
	public String getMaterialDescription() {
		return materialDescription;
	}

	public void setMaterialDescription(String materialDescription) {
		this.materialDescription = materialDescription;
	}

	@ManyToOne(targetEntity = PnMaterialType.class)
	@JoinColumn(name = "MATERIAL_TYPE_ID")
	public PnMaterialType getPnMaterialType() {
		return materialType;
	}

	public void setPnMaterialType(PnMaterialType materialType) {
		this.materialType = materialType;
	}

	@Column(name = "MATERIAL_COST", nullable = false, length = 22)
	public Float getMaterialCost() {
		return materialCost;
	}

	public void setMaterialCost(Float materialCost) {
		this.materialCost = materialCost;
	}

	@Column(name = "RECORD_STATUS", nullable = false, length = 1)
	public String getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	
	

}
