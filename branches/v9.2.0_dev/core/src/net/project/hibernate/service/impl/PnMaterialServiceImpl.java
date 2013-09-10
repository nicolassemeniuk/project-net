package net.project.hibernate.service.impl;

import java.sql.Date;
import java.util.List;

import net.project.hibernate.dao.IPnMaterialDAO;
import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.model.PnMaterialType;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.service.IPnMaterialAssignmentService;
import net.project.hibernate.service.IPnMaterialService;
import net.project.hibernate.service.IPnMaterialTypeService;
import net.project.hibernate.service.IPnObjectService;
import net.project.material.MaterialBean;
import net.project.material.PnMaterialList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "pnMaterialService")
public class PnMaterialServiceImpl implements IPnMaterialService {

	/**
	 * PnMaterial data access object
	 */
	@Autowired
	private IPnMaterialDAO pnMaterialDAO;

	
	@Autowired
	private IPnMaterialTypeService materialTypeService;

	@Autowired
	private IPnObjectService objectService;
	
	@Autowired
	private IPnMaterialAssignmentService materialAssignmentService;

//	public void setPnMaterialDAO(IPnMaterialDAO pnMaterialDAO) {
//		this.pnMaterialDAO = pnMaterialDAO;
//	}
//
//	public IPnMaterialDAO getPnMaterialDAO() {
//		return pnMaterialDAO;
//	}

	@Override
	public PnMaterial getMaterial(Integer materialId) {
		return pnMaterialDAO.getMaterialById(materialId);
	}

	@Override
	public PnMaterialList getMaterials() {
		return pnMaterialDAO.getMaterials();
	}

	public Integer saveMaterial(MaterialBean materialBean) {
		Integer materialObjectId = objectService.saveObject(new PnObject(PnMaterial.OBJECT_TYPE, new Integer(materialBean.getUser().getID()), new Date(System
				.currentTimeMillis()), "A"));
		PnMaterialType materialType = materialTypeService.getMaterialTypeById(Integer.valueOf(materialBean.getMaterialTypeId()));

		PnMaterial pnMaterial = new PnMaterial();
		pnMaterial.setMaterialName(materialBean.getName());
		pnMaterial.setMaterialDescription(materialBean.getDescription());
		pnMaterial.setMaterialCost(Float.valueOf(materialBean.getCost()));
		pnMaterial.setPnMaterialType(materialType);
		pnMaterial.setRecordStatus("A");
		pnMaterial.setMaterialId(materialObjectId);
		if (materialBean.getConsumable() != null)
			pnMaterial.setMaterialConsumable(String.valueOf(materialBean.getConsumable()));
		else
			pnMaterial.setMaterialConsumable("false");
		return this.pnMaterialDAO.create(pnMaterial);
	}

	public void deleteMaterial(PnMaterial pnMaterial) {
		this.pnMaterialDAO.delete(pnMaterial);
	}

	public void updateMaterial(MaterialBean materialBean) {
		PnMaterialType materialType = materialTypeService.getMaterialTypeById(Integer.valueOf(materialBean.getMaterialTypeId()));
		PnMaterial pnMaterial = this.getMaterial(Integer.valueOf(materialBean.getMaterialId()));

		// If found, update
		if (pnMaterial != null) {
			pnMaterial.setMaterialName(materialBean.getName());
			pnMaterial.setMaterialDescription(materialBean.getDescription());
			pnMaterial.setMaterialCost(Float.valueOf(materialBean.getCost()));
			pnMaterial.setPnMaterialType(materialType);
			pnMaterial.setRecordStatus("A");
			if (materialBean.getConsumable() != null)
				pnMaterial.setMaterialConsumable(String.valueOf(materialBean.getConsumable()));
			else
				pnMaterial.setMaterialConsumable("false");
			this.pnMaterialDAO.update(pnMaterial);
		}
	}

	@Override
	public PnMaterialList getMaterials(List<Integer> materialsId) {
		return this.pnMaterialDAO.getMaterials(materialsId);
	}

	@Override
	public void disableMaterial(Integer materialId) {
		PnMaterial pnMaterial = this.getMaterial(materialId);

		// if found, disable
		if (pnMaterial != null) {
			pnMaterial.setRecordStatus("D");
			this.pnMaterialDAO.update(pnMaterial);
			//Disable this material assignments
			this.materialAssignmentService.disableAssignments(String.valueOf(pnMaterial.getMaterialId()));
		}
	}
	
	@Override
	public PnMaterialList getMaterialsFromCompletedTasksOfSpace(Integer spaceID){
		return this.pnMaterialDAO.getMaterialsFromCompletedTasksOfSpace(spaceID);
	}

}
