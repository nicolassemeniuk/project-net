package net.project.material;

import java.io.Serializable;
import java.sql.SQLException;

import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.PersistenceException;

public class Material implements Serializable, IJDBCPersistence {
	
	private String materialId = null;	
	private String name = null;	
	private String description = null;
	private String cost = null;
	private String materialTypeId = null;
	private String materialTypeName = null;
	
	public Material(){
		
	}
	

	
	

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getMaterialTypeId() {
		return materialTypeId;
	}

	public void setMaterialTypeId(String materialTypeId) {
		this.materialTypeId = materialTypeId;
	}

	public String getMaterialTypeName() {
		return materialTypeName;
	}

	public void setMaterialTypeName(String materialTypeName) {
		this.materialTypeName = materialTypeName;
	}

	@Override
	public void setID(String id) {
		this.materialId = id;		
	}

	@Override
	public void load() throws PersistenceException {
		if(materialId!=null){
			PnMaterial material = ServiceFactory.getInstance().getMaterialService().getMaterial(Integer.valueOf(materialId));
			name = material.getMaterialName();
			description = material.getMaterialDescription();
			cost = String.valueOf(material.getMaterialCost());
			materialTypeId = String.valueOf(material.getPnMaterialType().getMaterialTypeId());
			materialTypeName = String.valueOf(material.getPnMaterialType().getMaterialTypeName());
		}
	}

	@Override
	public void store() throws PersistenceException, SQLException {
		
		
	}

	@Override
	public void remove() throws PersistenceException {
		// TODO Auto-generated method stub
		
	}
	public void clear(){
		this.materialId = null;
		this.name= null;
		this.description = null;
		this.cost = null;
		this.materialTypeId = null;
		this.materialTypeName = null;
	}
	
	

}
