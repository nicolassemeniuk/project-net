package net.project.hibernate.service;

import java.util.List;

import net.project.hibernate.model.PnMaterial;
import net.project.material.PnMaterialList;

public interface IPnMaterialService {
	
	public PnMaterial getMaterial(Integer materialId);
	
	public PnMaterialList getMaterials();
	
	public Integer saveMaterial(PnMaterial pnMaterial);
	
	public void deleteMaterial(PnMaterial pnMaterial);
	
	public void updateMaterial(PnMaterial pnMaterial);
	
	public PnMaterialList getMaterials(List<Integer> materialsId);

}
