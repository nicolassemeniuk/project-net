package net.project.hibernate.service;

import net.project.hibernate.model.PnMaterial;
import net.project.material.MaterialBean;
import net.project.material.PnMaterialList;

public interface IMaterialService {

	/**
	 * Gets a list of materials.
	 * @return a list of materials.
	 */
	public PnMaterialList getMaterials();
	
	/**
	 * Gets a material given a certain id.
	 * @param id from the material to obtain.
	 * @return the material for the id given.
	 */
	public PnMaterial getMaterial(String id);
	
	/**
	 * Saves a new material and associate it to the space where it was created.
	 * @param materialBean the Bean representing a material.
	 */
	public void saveMaterial(MaterialBean materialBean);
	
	/**
	 * Updates a material data.
	 * @param materialBean the Bean representing a material.
	 */
	public void updateMaterial(MaterialBean materialBean);
	
	/**
	 * Obtains the materials from a given space.
	 * @param spaceId the id of the space from wich we want to obtain the materials.
	 * @return a list of materials.
	 */
	public PnMaterialList getMaterialsFromSpace(String spaceId);
	
	
	/**
	 * Disable a certain material from the database. This means setting his status to "D".
	 * @param MaterialId the material to disable.
	 */
	public void disableMaterial(String MaterialId);
	
}
