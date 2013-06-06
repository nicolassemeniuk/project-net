package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.model.PnMaterial;
import net.project.material.PnMaterialList;

public interface IPnMaterialDAO extends IDAO<PnMaterial, Integer> {
	
	/**
	 * Obtain a certain material by Id.
	 * @param materialId the id of the material we want to obtain.
	 * @return a material.
	 */
	public PnMaterial getMaterialById(Integer materialId);
	
	/**
	 * Obtain all of the materials in the application
	 * @return a list of materials.
	 */
	public PnMaterialList getMaterials();
	
	/**
	 * Get a list of materials from a list of materials Id's.
	 * @param materialsId the ids of the materials we want to obtain.
	 * @return a list of materials.
	 */
	public PnMaterialList getMaterials(List<Integer> materialsId);
		
}
