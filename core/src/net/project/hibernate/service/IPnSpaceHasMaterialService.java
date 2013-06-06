package net.project.hibernate.service;

import java.util.List;

import net.project.hibernate.model.PnSpaceHasMaterial;

public interface IPnSpaceHasMaterialService {
	
	/**
	 * Obtain a space/material relation.
	 * @param spaceId the id from the space.
	 * @param MaterialId the id from the material.
	 * @return a space/material relation.
	 */
	public PnSpaceHasMaterial getPnSpaceHasMaterial(String spaceId,
			String MaterialId);

	/**
	 * Obtain if a Material is part of a space.
	 * @param spaceId the id from the space.
	 * @param materialId the id from the material.
	 * @return true if it's associated.
	 */
	public boolean spaceHasMaterial(String spaceId, String materialId);
	
	/**
	 * Get a list of id's from materials of a certain space.
	 * @param spaceId the id of the space.
	 * @return a list of materials id.
	 */
	public List<Integer> getMaterialsFromSpace(String spaceId);
	
	/**
	 * Save a new association between a space and a material.
	 * @param spaceId the id of the space.
	 * @param material the material to associate.
	 */
	public void associateMaterial(String spaceId, String materialId);

}
