package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.model.PnSpaceHasMaterial;
import net.project.hibernate.model.PnSpaceHasMaterialPK;

public interface IPnSpaceHasMaterialDAO extends
		IDAO<PnSpaceHasMaterial, PnSpaceHasMaterialPK> {

	/**
	 * Gets a space/material association.
	 * @param spaceId the id from the space.
	 * @param MaterialId the id from the material.
	 * @return a space/material association.
	 */
	public PnSpaceHasMaterial getPnSpaceHasMaterial(Integer spaceId,
			Integer MaterialId);

	/**
	 * Checks if a material belongs to a certain space.
	 * @param spaceId the id from the space.
	 * @param materialId the id from the material.
	 * @return true in case the material is on that space, false otherwise.
	 */
	public boolean spaceHasMaterial(Integer spaceId, Integer materialId);
	
	/**
	 * Obtain a list of material id's from the space.
	 * @param spaceId the id from the space.
	 * @return a list of materials id's.
	 */
	public List<Integer> getMaterialsFromSpace(Integer spaceId);
	
	/**
	 * Creates a new association between a space and a material.
	 * @param spaceHasMaterial the class representing the association.
	 */
	public void associateMaterial(PnSpaceHasMaterial spaceHasMaterial);

}
