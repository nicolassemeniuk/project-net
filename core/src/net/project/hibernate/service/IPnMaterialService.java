package net.project.hibernate.service;

import java.util.List;

import net.project.hibernate.model.PnMaterial;
import net.project.material.MaterialBean;
import net.project.material.PnMaterialList;

public interface IPnMaterialService {

	/**
	 * Returns a material given an id.
	 * 
	 * @param materialId
	 *            the Id from the material.
	 * @return a material.
	 */
	public PnMaterial getMaterial(Integer materialId);

	/**
	 * Returns the list of all materials on the application.
	 * 
	 * @return a list of materials.
	 */
	public PnMaterialList getMaterials();

	/**
	 * Saves a new material.
	 * 
	 * @param materialBean
	 *            the material to save.
	 * @return the id of the material.
	 */
	public Integer saveMaterial(MaterialBean materialBean);

	/**
	 * Deletes a material.
	 * 
	 * @param pnMaterial
	 *            the material to delete.
	 */
	public void deleteMaterial(PnMaterial pnMaterial);

	/**
	 * Updates the changes on a material.
	 * 
	 * @param materialBean
	 *            a modified material to update.
	 */
	public void updateMaterial(MaterialBean materialBean);

	/**
	 * Gets a List of Materials given a list of id's. Retrieves the active
	 * materials.
	 * 
	 * @param materialsId
	 *            a list of materials id's.
	 * @return a list of materials.
	 */
	public PnMaterialList getMaterials(List<Integer> materialsId);

	/**
	 * Disables a certain material. This means that the status attribute is set
	 * to "D".
	 * 
	 * @param material
	 *            the material to be disabled.
	 */
	public void disableMaterial(Integer materialId);

	/**
	 * Obtain the materials from completed tasks of a certain space. A task is
	 * considered completed based on a certain percent of completion. This is a
	 * property that is set on the database
	 * (prm.global.taskcompletedpercentage).
	 * 
	 * @param spaceId
	 *            the id from the Space.
	 * @return a list of materials.
	 */
	public PnMaterialList getMaterialsFromCompletedTasksOfSpace(Integer spaceId);

	/**
	 * Gets a List of Materials given a list of id's. The material name must
	 * match the given search key which can be null. Retrieves the active
	 * materials.
	 * 
	 * @param materialsIds
	 *            a list of materials id's.
	 * @param searchKey
	 *            the search key to filter the name.
	 * @returna list of materials.
	 */
	public PnMaterialList getMaterials(List<Integer> materialsIds, String searchKey);

}
