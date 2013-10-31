package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.model.PnMaterial;
import net.project.material.PnMaterialList;

public interface IPnMaterialDAO extends IDAO<PnMaterial, Integer> {

	/**
	 * Obtain a certain material by Id.
	 * 
	 * @param materialId
	 *            the id of the material we want to obtain.
	 * @return a material.
	 */
	public PnMaterial getMaterialById(Integer materialId);

	/**
	 * Obtain all of the materials in the application
	 * 
	 * @return a list of materials.
	 */
	public PnMaterialList getMaterials();

	/**
	 * Get a list of materials from a list of materials Id's. Retrieves the
	 * active materials.
	 * 
	 * @param materialsId
	 *            the id's of the materials we want to obtain.
	 * @return a list of materials.
	 */
	public PnMaterialList getMaterials(List<Integer> materialsId);

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
	public PnMaterialList getMaterialsFromCompletedTasksOfSpace(Integer spaceID);

	/**
	 * Get a list of materials from a list of materials Id's. The material name
	 * must match a given search key which can be null. Retrieves the active
	 * materials.
	 * 
	 * @param materialsIds
	 *            the id's of the materials we want to obtain.
	 * @param searchKey
	 *            that the material name must match (can be null).
	 * @param materialTypeId
	 *            to filter a certain type. 0 for all.
	 * @param consumable
	 *            to filter if we only want the consumable materials or not.
	 *            Null for all.
	 * @param minCost
	 *            if we want only the ones that have a cost above or equal to
	 *            this value. Can be null.
	 * @param maxCost
	 *            if we want only the ones that have a cost below or equal to
	 *            this value. Can be null.
	 * @return a list of materials
	 */
	public PnMaterialList getMaterials(List<Integer> materialsIds, String searchKey, Integer materialTypeId, String consumable, Float minCost, Float maxCost);

}
