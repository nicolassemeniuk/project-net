package net.project.material;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.model.PnAssignmentMaterial;
import net.project.hibernate.service.ServiceFactory;

public class MaterialAssignmentList implements Serializable {	

    private ArrayList<MaterialAssignment> materialAssignments = new ArrayList();	
	
	public void MaterialAssignmenList(){		
	}
	
	public void load(String spaceId, String objectId)
	{
		PnAssignmentMaterialList assignmentList = ServiceFactory.getInstance().getPnAssignmentMaterialService().getAssignmentMaterials(spaceId, objectId);
		for(PnAssignmentMaterial assignee : assignmentList){
			materialAssignments.add(new MaterialAssignment(assignee));
		}
	}
	
	public MaterialAssignment getAssignedMaterial(String materialId)
	{
		for(MaterialAssignment asignee : materialAssignments){
			if(asignee.getMaterialId().equals(materialId)){
				return asignee;
			}
		}
		return new MaterialAssignment();
	}
	
	public void removeAssignment(MaterialAssignment material)
	{
		for(MaterialAssignment asignee : materialAssignments){
			if(asignee.getMaterialId().equals(material.getMaterialId())){
				materialAssignments.remove(asignee);
				break;
			}
		}
	}

	public void clear()
	{
		materialAssignments =  new ArrayList();
	}
	
	public void add(MaterialAssignment materialAssignment)
	{
		materialAssignments.add(materialAssignment);
	}
	
    public Iterator<MaterialAssignment> getIterator()
    {
        return materialAssignments.iterator();
    }
}
