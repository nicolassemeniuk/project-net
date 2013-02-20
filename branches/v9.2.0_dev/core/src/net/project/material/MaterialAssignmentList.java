package net.project.material;

import java.io.Serializable;
import java.util.ArrayList;

import net.project.hibernate.model.PnAssignmentMaterial;
import net.project.hibernate.service.ServiceFactory;

public class MaterialAssignmentList extends ArrayList<MaterialAssignment> implements Serializable {	

	public void MaterialAssignmenList(){		
	}

	
	public void load(String spaceId, String objectId){
		PnAssignmentMaterialList assignmentList = ServiceFactory.getInstance().getPnAssignmentMaterialService().getAssignmentMaterials(spaceId, objectId);
		for(PnAssignmentMaterial assignee : assignmentList){
			this.add(new MaterialAssignment(assignee));
		}
	}
	
	public MaterialAssignment getAssignedMaterial(String materialId){
		for(MaterialAssignment asignee : this){
			if(asignee.getMaterialId().equals(materialId)){
				return asignee;
			}
		}
		return new MaterialAssignment();
	}
	
	public void removeAssignment(MaterialAssignment material){
		for(MaterialAssignment asignee : this){
			if(asignee.getMaterialId().equals(material.getMaterialId())){
				this.remove(asignee);
				break;
			}
		}
	}


}
