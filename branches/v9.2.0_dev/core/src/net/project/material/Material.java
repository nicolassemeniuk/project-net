package net.project.material;

import java.io.Serializable;
import java.sql.SQLException;

import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;

public class Material implements Serializable, IJDBCPersistence {
	
	protected String materialId = null;	
	protected String name = null;	
	protected String description = null;
	protected String cost = null;
	protected String materialTypeId = null;
	protected String materialTypeName = null;
	protected String spaceID = null;
	protected User user = null;
	
	public Material(){		
	}
	
	public Material(PnMaterial material)
	{
		if(material != null)
		{
			materialId = String.valueOf(material.getMaterialId());	
			name = material.getMaterialName();
			description = material.getMaterialDescription();
			cost = String.valueOf(material.getMaterialCost());
			materialTypeId = String.valueOf(material.getPnMaterialType().getMaterialTypeId());
			materialTypeName = String.valueOf(material.getPnMaterialType().getMaterialTypeName());
		}
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
	
	public String getSpaceID() {
		return spaceID;
	}

	public void setSpaceID(String spaceID) {
		this.spaceID = spaceID;
	}	

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public void load() throws PersistenceException {
		if(materialId!=null){
			PnMaterial material = ServiceFactory.getInstance().getMaterialService().getMaterial(materialId);
			name = material.getMaterialName();
			description = material.getMaterialDescription();
			cost = String.valueOf(material.getMaterialCost());
			materialTypeId = String.valueOf(material.getPnMaterialType().getMaterialTypeId());
			materialTypeName = String.valueOf(material.getPnMaterialType().getMaterialTypeName());
		}
	}

	@Override
	public void store() throws PersistenceException, SQLException {		
		// TODO Auto-generated method stub
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
		this.spaceID=null;
		this.user= null;
	}
	
	

}
