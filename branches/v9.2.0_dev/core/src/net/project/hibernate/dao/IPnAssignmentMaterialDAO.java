package net.project.hibernate.dao;

import net.project.hibernate.model.PnAssignmentMaterial;
import net.project.hibernate.model.PnAssignmentMaterialPK;
import net.project.material.PnAssignmentMaterialList;

public interface IPnAssignmentMaterialDAO extends IDAO<PnAssignmentMaterial, PnAssignmentMaterialPK> {

	public PnAssignmentMaterial getPnAssignmentMaterial(Integer spaceId, Integer materialId, Integer objectId);

	public PnAssignmentMaterialList getAssignments(Integer spaceId, Integer objectId);

	/**
	 * This method returns a list of assignments for a material in a space.
	 * 
	 * @param spaceId
	 *            the Id from the Space we want to obtain the assignments.
	 * @param materialId
	 *            the Id from the Material we want to obtain the assignments.
	 * @return a list of assignments.
	 */
	public PnAssignmentMaterialList getAssignmentsForMaterial(Integer spaceId, Integer materialId);

	/**
	 * This method returns a list of assignments for a Material in a Space.
	 * Except the one for the object which id is objectId.
	 * 
	 * @param spaceId
	 *            the Id from the Space we want to obtain the assignments.
	 * @param materialId
	 *            the Id from the Material we want to obtain the assignments.
	 * @param objectId
	 *            the id of the object which assignment we want to exclude from
	 *            the list.
	 * @return a list of assignments.
	 */
	public PnAssignmentMaterialList getAssignmentsForMaterial(Integer spaceId, Integer materialId, Integer objectId);

}
