package net.project.hibernate.service;

import net.project.hibernate.model.PnMaterial;
import net.project.material.MaterialBean;
import net.project.material.PnMaterialList;

public interface IMaterialService {

	/**
	 * Gets a list of materials.
	 * 
	 * @return a list of materials.
	 */
	public PnMaterialList getMaterials();

	/**
	 * Gets a material given a certain id.
	 * 
	 * @param id
	 *            from the material to obtain.
	 * @return the material for the id given.
	 */
	public PnMaterial getMaterial(String id);

	/**
	 * Saves a new material and associate it to the space where it was created.
	 * 
	 * @param materialBean
	 *            the Bean representing a material.
	 */
	public void saveMaterial(MaterialBean materialBean);

	/**
	 * Updates a material data.
	 * 
	 * @param materialBean
	 *            the Bean representing a material.
	 */
	public void updateMaterial(MaterialBean materialBean);

	/**
	 * Obtains the materials from a given space. Retrieves the active materials.
	 * 
	 * @param spaceId
	 *            the id of the space from wich we want to obtain the materials.
	 * @return a list of materials.
	 */
	public PnMaterialList getMaterialsFromSpace(String spaceId);

	/**
	 * Obtains the materials from a given space and certain selectors. Retrieves
	 * the active materials only.
	 * 
	 * @param spaceId
	 *            the id of the space.
	 * @param searchKey
	 *            a string to compare the name with.
	 * @param materialTypeId
	 *            the id of the type of material to filter (0 for all).
	 * @param consumable
	 *            if the material should be consumable or no (can be null for
	 *            all). 'true' or 'false' otherwise.
	 * @param minCost
	 *            the minimum cost for the materials to meet the criteria (null for all).
	 * @param maxCost
	 *            the maximum cost for the materials to meet the criteria (null for all).
	 * @return a list of materials.
	 */
	public PnMaterialList getMaterialsFromSpace(String spaceId, String searchKey, String materialTypeId, String consumable, String minCost, String maxCost);

	/**
	 * Disable a certain material from the database. This means setting his
	 * status to "D".
	 * 
	 * @param MaterialId
	 *            the material to disable.
	 */
	public void disableMaterial(String MaterialId);

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
	public PnMaterialList getMaterialsFromCompletedTasksOfSpace(String spaceId);

	/**
	 * Disable all the assignments of the business materials of a project. This
	 * is used in case we change the owner business of a project.
	 * 
	 * @param businessId
	 *            the id of the business.
	 * @param projectId
	 *            the id of the project.
	 */
	public void disableMaterialsAssignmentsFromBusiness(String businessId, String projectId);

	/**
	 * Get all the materials that are not assigned on any task in the project.
	 * 
	 * @param spaceID
	 *            the id of the space.
	 * @return a list of materials.
	 */
	public PnMaterialList getMaterialsFromProjectWithoutAssignments(String spaceID);

}
