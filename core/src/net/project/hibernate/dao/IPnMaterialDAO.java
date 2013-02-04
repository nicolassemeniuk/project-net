package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.model.PnMaterial;
import net.project.material.PnMaterialList;

public interface IPnMaterialDAO extends IDAO<PnMaterial, Integer> {
	
	public PnMaterial getMaterialById(Integer materialId);
	
	public PnMaterialList getMaterials();
	
	public PnMaterialList getMaterials(List<Integer> materialsId);
	
	public void disableMaterial(PnMaterial material);
		
}
