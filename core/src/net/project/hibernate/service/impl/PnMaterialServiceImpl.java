package net.project.hibernate.service.impl;

import java.util.List;

import net.project.hibernate.dao.IPnMaterialDAO;
import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.service.IPnMaterialService;
import net.project.material.PnMaterialList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="pnMaterialService")
public class PnMaterialServiceImpl implements IPnMaterialService {

	/**
	 * PnMaterial data access object
	 */
	@Autowired
	private IPnMaterialDAO pnMaterialDAO;
	
	public void setPnMaterialDAO(IPnMaterialDAO pnMaterialDAO) {
		this.pnMaterialDAO = pnMaterialDAO;
	}

	public IPnMaterialDAO getPnMaterialDAO() {
		return pnMaterialDAO;
	}
	
	@Override
	public PnMaterial getMaterial(Integer materialId) {
		return pnMaterialDAO.getMaterialById(materialId);
	}

	@Override
	public PnMaterialList getMaterials() {
		return pnMaterialDAO.getMaterials();
	}

	public Integer saveMaterial(PnMaterial pnMaterial) {
		return this.pnMaterialDAO.create(pnMaterial);
	}

	public void deleteMaterial(PnMaterial pnMaterial) {
		this.pnMaterialDAO.delete(pnMaterial);
	}

	public void updateMaterial(PnMaterial pnMaterial) {
		this.pnMaterialDAO.update(pnMaterial);
	}

	@Override
	public PnMaterialList getMaterials(List<Integer> materialsId) {
		return this.pnMaterialDAO.getMaterials(materialsId);
	}

}
