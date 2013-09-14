package net.project.hibernate.service.impl;

import java.util.List;

import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.model.PnMaterialAssignment;
import net.project.hibernate.service.IMaterialService;
import net.project.hibernate.service.IPnMaterialAssignmentService;
import net.project.hibernate.service.IPnMaterialService;
import net.project.hibernate.service.IPnMaterialTypeService;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnSpaceHasMaterialService;
import net.project.hibernate.service.IPnTaskService;
import net.project.material.MaterialBean;
import net.project.material.PnMaterialAssignmentList;
import net.project.material.PnMaterialList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "materialService")
public class MaterialServiceImpl implements IMaterialService {

	@Autowired
	private IPnMaterialService pnMaterialService;
	
	@Autowired
	private IPnMaterialTypeService materialTypeService;
	
	@Autowired
	private IPnSpaceHasMaterialService spaceHasMaterialService;
	
	@Autowired
	private IPnMaterialAssignmentService materialAssignmentService;
	
	@Autowired
	private IPnTaskService taskService;
	
	@Autowired
	private IPnObjectService objectService;	

	public void setMaterialService(IPnMaterialService materialService) {
		this.pnMaterialService = materialService;
	}
	
	public void setObjectService(IPnObjectService objectService) {
		this.objectService = objectService;
	}
	
	public void setSpaceHasMaterialService(IPnSpaceHasMaterialService spaceHasMaterialService){
		this.spaceHasMaterialService = spaceHasMaterialService;
	}

	@Override
	public PnMaterialList getMaterials() {
		return pnMaterialService.getMaterials();
	}
	
	@Override
	public PnMaterialList getMaterialsFromSpace(String spaceId){
		return pnMaterialService.getMaterials(this.spaceHasMaterialService.getMaterialsFromSpace(spaceId));
	}
	
	@Override
	public void saveMaterial(MaterialBean materialBean) {			
		Integer materialId = pnMaterialService.saveMaterial(materialBean);
		
		//Associate to the space
		if(materialId != null){
			spaceHasMaterialService.associateMaterial(materialBean.getSpaceID(), String.valueOf(materialId));
		}		
	}

	@Override
	public void updateMaterial(MaterialBean materialBean) {		
		pnMaterialService.updateMaterial(materialBean);
	}

	@Override
	public PnMaterial getMaterial(String materialId) {
		return pnMaterialService.getMaterial(Integer.valueOf(materialId));
	}	
	
	@Override
	public void disableMaterial(String materialId){
		pnMaterialService.disableMaterial(Integer.valueOf(materialId));
	}

	@Override
	public PnMaterialList getMaterialsFromCompletedTasksOfSpace(String spaceId) {
		return pnMaterialService.getMaterialsFromCompletedTasksOfSpace(Integer.valueOf(spaceId));
	}

	@Override
	public void disableMaterialsAssignmentsFromBusiness(String businessId, String projectId) {
		List<Integer> materialIds = spaceHasMaterialService.getMaterialsFromSpace(businessId);
		
		for(Integer id : materialIds){
			PnMaterialAssignmentList materialAssignments = materialAssignmentService.getAssignmentsForMaterial(String.valueOf(id));
			for(PnMaterialAssignment materialAssignment : materialAssignments){
				String spaceId = String.valueOf(materialAssignment.getComp_id().getSpaceId());
				String materialId = String.valueOf(materialAssignment.getComp_id().getMaterialId());
				String objectId = String.valueOf(materialAssignment.getComp_id().getObjectId());
				materialAssignmentService.disableAssignment(spaceId, materialId, objectId);
			}
		}
	}

	@Override
	public PnMaterialList getMaterialsFromSpace(String spaceId, String searchKey) {
		List<Integer> spaceMaterialsList = this.spaceHasMaterialService.getMaterialsFromSpace(spaceId);
		return pnMaterialService.getMaterials(spaceMaterialsList, searchKey);
	}


}
