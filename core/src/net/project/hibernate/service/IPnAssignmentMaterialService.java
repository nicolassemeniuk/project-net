package net.project.hibernate.service;

import java.util.Date;

import net.project.hibernate.model.PnAssignmentMaterial;
import net.project.material.PnAssignmentMaterialList;

public interface IPnAssignmentMaterialService {

	/**
	 * Returns an assigned Material object given an certain Space, Material and
	 * Object to which the material is associated.
	 * 
	 * @param spaceId
	 *            the Id from the space.
	 * @param materialId
	 *            the Id form the material.
	 * @param objectId
	 *            the Id from the object.
	 * @return an Assignment from a material.
	 */
	public PnAssignmentMaterial getAssignmentMaterial(String spaceId, String materialId, String objectId);

	/**
	 * Returns the assigned Materials from that Space and Object.
	 * 
	 * @param spaceId
	 *            the Id from the Space.
	 * @param objectId
	 *            the Id from the Object.
	 * @return a list of assignments.
	 */
	public PnAssignmentMaterialList getAssignmentMaterials(String spaceId, String objectId);

	/**
	 * 
	 * Returns the assignments for a certain Material in a determined Space. A
	 * material can only be on one space.
	 * 
	 * @param materialId
	 *            the id from the Material we want to obtain the assignments.
	 * @param spaceId
	 *            the id from the Space the Material is assigned to.
	 * @return a list of assignments for that Material in that space.
	 */
	public PnAssignmentMaterialList getAssignmentsForMaterial(String spaceId, String materialId);

	/**
	 * Returns if a material is over assigned. This means that the material is
	 * on more than one task at the same time (day). For that we have to exclude
	 * the assignment we need to compare from the assignment list for the space
	 * and material.
	 * 
	 * @param startDate
	 *            the initial date we have to compare to other assignments.
	 * @param endDate
	 *            the final date we have to compare to other assignments.
	 * @param spaceId
	 *            the id from the Space the Material is assigned to.
	 * @param materialId
	 *            the id from the Material we want to obtain the assignments.
	 * @param objectId
	 *            the id of the object this material is assigned to. This is
	 *            required because we have to exclude this particular assignment
	 *            from the list.
	 * @return true in case the Material is on more than one task in the same
	 *         day. False otherwise.
	 */
	public boolean isOverassigned(Date startDate, Date endDate, String spaceId, String materialId, String objectId);

	/**
	 * Returns if a material is over assigned. This means that the material is
	 * on more than one task at the same time (day). We use this method when we
	 * create a new assignment. What we do is obtain if a new
	 * assignment conflicts with existent assignments for that material.
	 * 
	 * @param startDate
	 *            the initial date we have to compare to other assignments.
	 * @param endDate
	 *            the final date we have to compare to other assignments.
	 * @param spaceId
	 *            the id from the Space the Material is assigned to.
	 * @param materialId
	 *            the id from the Material we want to obtain the assignments.
	 * @return true in case the Material new dates for the assignment conflicts with existent assignments.
	 */
	public boolean isOverassigned(Date startDate, Date endDate, String spaceId, String materialId);

}
