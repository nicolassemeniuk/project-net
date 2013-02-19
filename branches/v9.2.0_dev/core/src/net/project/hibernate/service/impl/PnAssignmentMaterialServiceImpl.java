package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnAssignmentMaterialDAO;
import net.project.hibernate.model.PnAssignmentMaterial;
import net.project.hibernate.service.IPnAssignmentMaterialService;
import net.project.material.PnAssignmentMaterialList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="pnAssignmentMaterialService")
public class PnAssignmentMaterialServiceImpl implements IPnAssignmentMaterialService {
	
	@Autowired
	private IPnAssignmentMaterialDAO pnMaterialDAO;

	@Override
	public PnAssignmentMaterial getAssignmentMaterial(String spaceId, String materialId, String objectId) {
		return pnMaterialDAO.getPnAssignmentMaterial(Integer.valueOf(spaceId), Integer.valueOf(materialId), Integer.valueOf(objectId));
	}

	@Override
	public PnAssignmentMaterialList getAssignmentMaterials(String spaceId, String objectId) {
		return pnMaterialDAO.getAssignments(Integer.valueOf(spaceId), Integer.valueOf(objectId));
	}

}
