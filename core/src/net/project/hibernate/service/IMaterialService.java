package net.project.hibernate.service;

import net.project.hibernate.model.PnMaterial;
import net.project.material.PnMaterialList;

public interface IMaterialService {

	public PnMaterialList getMaterials();
	
	public PnMaterial getMaterial(Integer id);
	
//	public void saveMaterial(MaterialCreateWizard materialWizard);
//	
//	public void updateMaterial(MaterialSpaceBean materialSpace);
	
	public PnMaterialList getMaterialsFromSpace(Integer spaceId);
	
}
