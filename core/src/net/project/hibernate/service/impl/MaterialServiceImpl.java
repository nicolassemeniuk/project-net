package net.project.hibernate.service.impl;

import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.service.IMaterialService;
import net.project.hibernate.service.IPnMaterialService;
import net.project.hibernate.service.IPnMaterialTypeService;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnSpaceHasMaterialService;
import net.project.material.MaterialBean;
import net.project.material.PnMaterialList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "materialService")
public class MaterialServiceImpl implements IMaterialService {

	@Autowired
	private IPnMaterialService materialService;
	
	@Autowired
	private IPnMaterialTypeService materialTypeService;
	
	@Autowired
	private IPnSpaceHasMaterialService spaceHasMaterialService;
	

	@Autowired
	private IPnObjectService objectService;	

	public void setMaterialService(IPnMaterialService materialService) {
		this.materialService = materialService;
	}
	
	public void setObjectService(IPnObjectService objectService) {
		this.objectService = objectService;
	}
	
	public void setSpaceHasMaterialService(IPnSpaceHasMaterialService spaceHasMaterialService){
		this.spaceHasMaterialService = spaceHasMaterialService;
	}

	@Override
	public PnMaterialList getMaterials() {
		return materialService.getMaterials();
	}
	
	@Override
	public PnMaterialList getMaterialsFromSpace(String spaceId){
		return materialService.getMaterials(this.spaceHasMaterialService.getMaterialsFromSpace(spaceId));
	}
	
	@Override
	public void saveMaterial(MaterialBean materialBean) {			
		Integer materialId = materialService.saveMaterial(materialBean);
		
		//Associate to the space
		if(materialId != null){
			spaceHasMaterialService.associateMaterial(materialBean.getSpaceID(), String.valueOf(materialId));
		}		
	}

	@Override
	public void updateMaterial(MaterialBean materialBean) {		
		materialService.updateMaterial(materialBean);
	}

	@Override
	public PnMaterial getMaterial(String materialId) {
		return materialService.getMaterial(Integer.valueOf(materialId));
	}	
	
	@Override
	public void disableMaterial(String materialId){
		materialService.disableMaterial(Integer.valueOf(materialId));
	}

}
