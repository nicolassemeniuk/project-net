package net.project.hibernate.dao;

import net.project.hibernate.model.PnAssignmentMaterial;
import net.project.hibernate.model.PnAssignmentMaterialPK;
import net.project.material.PnAssignmentMaterialList;

public interface IPnAssignmentMaterialDAO extends IDAO<PnAssignmentMaterial, PnAssignmentMaterialPK> {

	public PnAssignmentMaterial getPnAssignmentMaterial(Integer spaceId, Integer materialId, Integer objectId);
	
	public PnAssignmentMaterialList getAssignments(Integer spaceId, Integer objectId);
	
}
