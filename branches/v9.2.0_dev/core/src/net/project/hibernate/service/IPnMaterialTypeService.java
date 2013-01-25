package net.project.hibernate.service;

import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.model.PnMaterialType;
import net.project.material.PnMaterialTypeList;

public interface IPnMaterialTypeService {
	
	public PnMaterialType getMaterialTypeById(Integer id);
	
	public PnMaterialTypeList getMaterialTypes();
	
	public Integer saveMaterialType(PnMaterial pnMaterial);
	
	public void deleteMaterialType(PnMaterial pnMaterial);
	
	public void updateMaterialType(PnMaterial pnMaterial);	

}
