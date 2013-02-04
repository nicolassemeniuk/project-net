package net.project.hibernate.service;

import java.util.List;

import net.project.hibernate.model.PnMaterial;
import net.project.material.MaterialBean;
import net.project.material.PnMaterialList;

public interface IPnMaterialService {
	
	/**
	 * Returns a material given an id.
	 * @param materialId the Id from the material.
	 * @return a material.
	 */
	public PnMaterial getMaterial(Integer materialId);
	
	/**
	 * Returns the list of materials.
	 * @return
	 */
	public PnMaterialList getMaterials();
	
	/**
	 * Saves a new material.
	 * @param materialBean the material to save. 
	 * @return the id of the material.
	 */
	public Integer saveMaterial(MaterialBean materialBean);
	
	/**
	 * Deletes a material.
	 * @param pnMaterial the material to delete.
	 */
	public void deleteMaterial(PnMaterial pnMaterial);
	
	/**
	 * Updates the changes on a material.
	 * @param materialBean a modified material to update.
	 */
	public void updateMaterial(MaterialBean materialBean);
	
	/**
	 * Gets a List of Materials given a list of id's.
	 * @param materialsId a list of materials id's.
	 * @return a list of materials.
	 */
	public PnMaterialList getMaterials(List<Integer> materialsId);
	
	/**
	 * Disables a certain material. This means that the status attribute is set to "D".
	 * @param material the material to be disabled.
	 */
	public void disableMaterial(Integer materialId);

}
