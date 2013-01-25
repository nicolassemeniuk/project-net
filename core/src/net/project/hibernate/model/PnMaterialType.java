package net.project.hibernate.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.project.gui.html.IHTMLOption;

@Entity
@Table(name = "PN_MATERIAL_TYPE")
public class PnMaterialType implements Serializable, IHTMLOption {

	private Integer materialTypeId;

	private String materialTypeName;

	public PnMaterialType() {
	}

	public PnMaterialType(Integer materialTypeId, String materialTypeName) {
		this.materialTypeId = materialTypeId;
		this.materialTypeName = materialTypeName;
	}

	@Id
	@Column(name = "MATERIAL_TYPE_ID", nullable = false, length = 20)	
	public Integer getMaterialTypeId() {
		return materialTypeId;
	}

	public void setMaterialTypeId(Integer materialTypeId) {
		this.materialTypeId = materialTypeId;
	}

	@Column(name = "MATERIAL_TYPE_NAME", nullable = false, length = 40)
	public String getMaterialTypeName() {
		return materialTypeName;
	}

	public void setMaterialTypeName(String materialTypeName) {
		this.materialTypeName = materialTypeName;
	}

	@Override
	@Transient	
	public String getHtmlOptionValue() {
		return String.valueOf(this.materialTypeId);
	}
	
	@Override
	@Transient
	public String getHtmlOptionDisplay() {
		return this.materialTypeName;
	}

}
