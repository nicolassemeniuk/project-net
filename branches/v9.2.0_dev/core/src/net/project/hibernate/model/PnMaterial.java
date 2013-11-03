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

	private PnMaterialType pnMaterialType;

	private Float materialCost;
	
	private String recordStatus;
	
	private String materialConsumable;

	public static final String OBJECT_TYPE = "material";

	public PnMaterial() {
	}
	
	public PnMaterial(Integer materialId, Float materialCost){
		this.materialId = materialId;
		this.materialCost = materialCost;
	}

	public PnMaterial(Integer materialId, String materialName, String materialDescription, PnMaterialType materialType, Float materialCost, String recordStatus, String materialConsumable) {
		this.materialId = materialId;
		this.materialName = materialName;
		this.materialDescription = materialDescription;
		this.pnMaterialType = materialType;
		this.materialCost = materialCost;
		this.recordStatus = recordStatus;
		this.materialConsumable = materialConsumable;
	}

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
		return pnMaterialType;
	}

	public void setPnMaterialType(PnMaterialType materialType) {
		this.pnMaterialType = materialType;
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

	@Column(name = "MATERIAL_CONSUMABLE", nullable = false, length = 5)
	public String getMaterialConsumable() {
		return materialConsumable;
	}

	public void setMaterialConsumable(String materialConsumable) {
		this.materialConsumable = materialConsumable;
	}
	
	
	
	

}
