package net.project.hibernate.service.impl;

import java.util.Date;

import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.model.PnMaterialType;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.service.IMaterialService;
import net.project.hibernate.service.IPnMaterialService;
import net.project.hibernate.service.IPnMaterialTypeService;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnSpaceHasMaterialService;
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
	public PnMaterialList getMaterialsFromSpace(Integer spaceId){		
		return materialService.getMaterials(this.spaceHasMaterialService.getMaterialsFromSpace(spaceId));
	}
	
//	@Override
//	public void saveMaterial(MaterialCreateWizard materialWizard) {
//		// TODO Ramiro Chequear la tabla PN_DEFAULT_OBJECT_PERMISSION
//	    Integer materialObjectId = objectService.saveObject(new PnObject(PnMaterial.OBJECT_TYPE, new Integer(materialWizard.getUser().getID()), new Date(System.currentTimeMillis()), "A"));	
//	    PnMaterialType materialType = materialTypeService.getMaterialTypeById(Integer.valueOf(materialWizard.getMaterialTypeId()));
//	    
//		PnMaterial pnMaterial = new PnMaterial();
//		pnMaterial.setMaterialName(materialWizard.getName());
//		pnMaterial.setMaterialDescription(materialWizard.getDescription());
//		pnMaterial.setMaterialCost(Float.valueOf(materialWizard.getCost()));
//		pnMaterial.setPnMaterialType(materialType);
//		pnMaterial.setRecordStatus("A");
//		pnMaterial.setMaterialId(materialObjectId);	
//		
//		materialService.saveMaterial(pnMaterial);
//	}
//
//	@Override
//	public void updateMaterial(MaterialSpaceBean materialSpace) {
//	    PnMaterialType materialType = materialTypeService.getMaterialTypeById(Integer.valueOf(materialSpace.getMaterialTypeId()));
//		
//		PnMaterial pnMaterial = materialService.getMaterial(Integer.valueOf(materialSpace.getID()));
//		pnMaterial.setMaterialName(materialSpace.getName());
//		pnMaterial.setMaterialDescription(materialSpace.getDescription());
//		pnMaterial.setMaterialCost(Float.valueOf(materialSpace.getCost()));
//		pnMaterial.setPnMaterialType(materialType);
//		pnMaterial.setRecordStatus("A");
//		
//		materialService.updateMaterial(pnMaterial);
//	}

	@Override
	public PnMaterial getMaterial(Integer id) {
		return materialService.getMaterial(id);
	}	

}
