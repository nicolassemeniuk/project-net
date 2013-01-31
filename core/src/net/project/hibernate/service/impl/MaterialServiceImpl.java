package net.project.hibernate.service.impl;

import java.sql.Date;

import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.model.PnMaterialType;
import net.project.hibernate.model.PnObject;
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
	public PnMaterialList getMaterialsFromSpace(Integer spaceId){		
		return materialService.getMaterials(this.spaceHasMaterialService.getMaterialsFromSpace(spaceId));
	}
	
	@Override
	public void saveMaterial(MaterialBean materialBean) {
		// TODO Ramiro Chequear la tabla PN_DEFAULT_OBJECT_PERMISSION
	    Integer materialObjectId = objectService.saveObject(new PnObject(PnMaterial.OBJECT_TYPE, new Integer(materialBean.getSpace().getID()), new Date(System.currentTimeMillis()), "A"));	
	    PnMaterialType materialType = materialTypeService.getMaterialTypeById(Integer.valueOf(materialBean.getMaterialTypeId()));
	    
		PnMaterial pnMaterial = new PnMaterial();
		pnMaterial.setMaterialName(materialBean.getName());
		pnMaterial.setMaterialDescription(materialBean.getDescription());
		pnMaterial.setMaterialCost(Float.valueOf(materialBean.getCost()));
		pnMaterial.setPnMaterialType(materialType);
		pnMaterial.setRecordStatus("A");
		pnMaterial.setMaterialId(materialObjectId);	
		
		materialService.saveMaterial(pnMaterial);
	}

	@Override
	public void updateMaterial(MaterialBean materialBean) {
	    PnMaterialType materialType = materialTypeService.getMaterialTypeById(Integer.valueOf(materialBean.getMaterialTypeId()));
		
		PnMaterial pnMaterial = materialService.getMaterial(Integer.valueOf(materialBean.getMaterialId()));
		pnMaterial.setMaterialName(materialBean.getName());
		pnMaterial.setMaterialDescription(materialBean.getDescription());
		pnMaterial.setMaterialCost(Float.valueOf(materialBean.getCost()));
		pnMaterial.setPnMaterialType(materialType);
		pnMaterial.setRecordStatus("A");
		
		materialService.updateMaterial(pnMaterial);
	}

	@Override
	public PnMaterial getMaterial(Integer id) {
		return materialService.getMaterial(id);
	}	

}
