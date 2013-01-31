package net.project.hibernate.service;

import net.project.hibernate.model.PnMaterial;
import net.project.material.MaterialBean;
import net.project.material.PnMaterialList;

public interface IMaterialService {

	public PnMaterialList getMaterials();
	
	public PnMaterial getMaterial(Integer id);
	
	public void saveMaterial(MaterialBean materialBean);
	
	public void updateMaterial(MaterialBean materialBean);
	
	public PnMaterialList getMaterialsFromSpace(Integer spaceId);
	
}
