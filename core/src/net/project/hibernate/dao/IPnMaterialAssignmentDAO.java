package net.project.hibernate.dao;

import java.util.Date;

import net.project.hibernate.model.PnMaterialAssignment;
import net.project.hibernate.model.PnMaterialAssignmentPK;
import net.project.material.PnMaterialAssignmentList;

public interface IPnMaterialAssignmentDAO extends IDAO<PnMaterialAssignment, PnMaterialAssignmentPK> {

	/**
	 * This method returns an specific assignment.
	 * 
	 * @param spaceId
	 *            the Id from the Space we want to obtain the assignments.
	 * @param materialId
	 *            the Id from the Material we want to obtain the assignments.
	 * @param objectId
	 *            the Id from the object we want to obtain the assignments.
	 * @return
	 */
	public PnMaterialAssignment getPnAssignmentMaterial(Integer spaceId, Integer materialId, Integer objectId);



	/**
	 * This method returns a list of assignments for a certain Space.
	 * 
	 * @param spaceId
	 *            the Id from the Space we want to obtain the assignments.
	 * @return a list of assignments
	 */
	public PnMaterialAssignmentList getAssignments(Integer spaceId);

	/**
	 * This method returns a list of assignments for a certain Space and Object.
	 * 
	 * @param spaceId
	 *            the Id from the Space we want to obtain the assignments.
	 * @param objectId
	 *            the Id from the Object we want to obtain the assignments.
	 * @return a list of assignments
	 */
	public PnMaterialAssignmentList getAssignments(Integer spaceId, Integer objectId);

	/**
	 * This method returns a list of assignments for a Material in a Space.
	 * EXCEPT the one for the object which id is objectId.
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
	public PnMaterialAssignmentList getAssignments(Integer spaceId, Integer materialId, Integer objectId);
	
	/**
	 * This method returns a list of assignments for a material.
	 * 
	 * @param materialId
	 *            the Id from the Material we want to obtain the assignments.
	 * @return a list of assignments.
	 */
	public PnMaterialAssignmentList getAssignmentsForMaterial(Integer materialId);

	/**
	 * This method returns a list of assignments for a material in a space.
	 * 
	 * @param spaceId
	 *            the Id from the Space we want to obtain the assignments.
	 * @param materialId
	 *            the Id from the Material we want to obtain the assignments.
	 * @return a list of assignments.
	 */
	public PnMaterialAssignmentList getAssignmentsForMaterial(Integer spaceId, Integer materialId);

	/**
	 * This method returns a list of assignments for a material in a range of
	 * dates.
	 * 
	 * @param materialId
	 *            the Id from the Material we want to obtain the assignments.
	 * @param startDate
	 *            the beginning date for the range.
	 * @param endDate
	 *            the end date for the range.
	 * @return a list of assignments.
	 */
	public PnMaterialAssignmentList getAssignmentsForMaterial(Integer materialId, Date startDate, Date endDate);


	/**
	 * Returns the assigned Materials from an Object.
	 * 
	 * @param objectId
	 *            the id of the object.
	 * @return a list of assignments.
	 */
	public PnMaterialAssignmentList getMaterialsAssignmentsForObject(Integer objectId);

}
