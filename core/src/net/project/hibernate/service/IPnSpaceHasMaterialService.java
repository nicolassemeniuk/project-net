package net.project.hibernate.service;

import java.util.List;

import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.model.PnSpaceHasMaterial;

public interface IPnSpaceHasMaterialService {
	
	public PnSpaceHasMaterial getPnSpaceHasMaterial(Integer spaceId,
			Integer MaterialId);

	/**
	 * Obtain if a Material is part of a space.
	 * @param spaceId the id from the space.
	 * @param materialId the id from the material.
	 * @return true if it's associated.
	 */
	public boolean spaceHasMaterial(Integer spaceId, Integer materialId);
	
	/**
	 * Get a list of id's from materials of a certain space.
	 * @param spaceId the id of the space.
	 * @return a list of materials id.
	 */
	public List<Integer> getMaterialsFromSpace(Integer spaceId);
	
	/**
	 * Save a new association between a space and a material.
	 * @param spaceId the id of the space.
	 * @param material the material to associate.
	 */
	public void associateMaterial(Integer spaceId, PnMaterial material);

}
