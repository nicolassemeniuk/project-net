package net.project.hibernate.service;

import net.project.hibernate.model.PnAssignmentMaterial;
import net.project.material.PnAssignmentMaterialList;

public interface IPnAssignmentMaterialService {
	
	/**
	 * Returns an assigned material object given an certain space, material and object to wich the material is associated. 
	 * @param spaceId the Id from the space.
	 * @param materialId the Id form the material.
	 * @param objectId the Id from the object.
	 * @return an Assignment from a material.
	 */
	public PnAssignmentMaterial getAssignmentMaterial(String spaceId, String materialId, String objectId);
	
	/**
	 * Returns the assigned materials from that Space and Object.
	 * @param spaceId the Id from the Space.
	 * @param objectId the Id from the Object.
	 * @return a list of assignments.
	 */
	public PnAssignmentMaterialList getAssignmentMaterials(String spaceId, String objectId);

}
