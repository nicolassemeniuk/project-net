package net.project.material;

import java.io.Serializable;
import java.sql.SQLException;

import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.model.PnMaterialAssignment;
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
	protected Boolean consumable = null;
	protected User user = null;
	
	protected Boolean consumableAndAssigned = null;

	public Material() {
	}

	public Material(PnMaterial material) {
		if (material != null) {
			materialId = String.valueOf(material.getMaterialId());
			name = material.getMaterialName();
			description = material.getMaterialDescription();
			cost = String.valueOf(material.getMaterialCost());
			materialTypeId = String.valueOf(material.getPnMaterialType().getMaterialTypeId());
			materialTypeName = String.valueOf(material.getPnMaterialType().getMaterialTypeName());
			consumable = Boolean.valueOf(material.getMaterialConsumable());
			
			consumableAndAssigned = new Boolean(false);
			
			if(consumable){
				PnMaterialAssignmentList assignments = ServiceFactory.getInstance().getPnMaterialAssignmentService().getAssignmentsForMaterial(materialId);
				for(PnMaterialAssignment assignment : assignments){
					//If we have 1 active assignment, we have to block the modification
					if(assignment.getRecordStatus()=="A"){
						consumableAndAssigned = new Boolean(true);
						break;
					}
				}
			} 
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

	public Boolean getConsumable() {
		return consumable;
	}

	public void setConsumable(Boolean consumable) {
		this.consumable = consumable;
	}
	
	public Boolean getConsumableAndAssigned() {
		return consumableAndAssigned;
	}

	public void setConsumableAndAssigned(Boolean consumableAndAssigned) {
		this.consumableAndAssigned = consumableAndAssigned;
	}

	public String getChecked(){
		return consumable ? "checked" : "";
	}

	@Override
	public void load() throws PersistenceException {
		if (materialId != null) {
			PnMaterial material = ServiceFactory.getInstance().getMaterialService().getMaterial(materialId);
			name = material.getMaterialName();
			description = material.getMaterialDescription();
			cost = String.valueOf(material.getMaterialCost());
			materialTypeId = String.valueOf(material.getPnMaterialType().getMaterialTypeId());
			materialTypeName = String.valueOf(material.getPnMaterialType().getMaterialTypeName());
			consumable = Boolean.valueOf(material.getMaterialConsumable());
			
			consumableAndAssigned = false;
			
			if(consumable){
				PnMaterialAssignmentList assignments = ServiceFactory.getInstance().getPnMaterialAssignmentService().getAssignmentsForMaterial(materialId);
				for(PnMaterialAssignment assignment : assignments){
					//If we have 1 active assignment, we have to block the modification
					if(assignment.getRecordStatus().equals("A")){
						consumableAndAssigned = true;
						break;
					}
				}
			}
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

	public void clear() {
		this.materialId = null;
		this.name = null;
		this.description = null;
		this.cost = null;
		this.materialTypeId = null;
		this.materialTypeName = null;
		this.spaceID = null;
		this.user = null;
		this.consumable = null;
		this.consumableAndAssigned = null;
	}

}
