package net.project.schedule;

import java.util.ArrayList;
import java.util.Iterator;

import net.project.hibernate.model.PnMaterialAssignment;
import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.service.ServiceFactory;
import net.project.material.Material;
import net.project.material.PnMaterialAssignmentList;
import net.project.material.PnMaterialList;

public class MaterialAssignmentsHelper {
	
	private ArrayList<MaterialAssignmentHelper> materialsAssigned;
	private String spaceId;
	private String objectId;	
	
	public MaterialAssignmentsHelper(){
		this.materialsAssigned=new ArrayList<MaterialAssignmentHelper>();
	}

	public ArrayList<MaterialAssignmentHelper> getMaterials() {
		return materialsAssigned;
	}

	public void setMaterials(ArrayList<MaterialAssignmentHelper> materialsAssigned) {
		this.materialsAssigned = materialsAssigned;
	}
	
	public void load(){
		PnMaterialList materials = ServiceFactory.getInstance().getMaterialService().getMaterialsFromSpace(spaceId);
		PnMaterialAssignmentList assignmentList = ServiceFactory.getInstance().getPnMaterialAssignmentService().getMaterialsAssignment(spaceId, objectId);
		for(Iterator<PnMaterial> iterator = materials.iterator(); iterator.hasNext();){
			Material material =  new Material(iterator.next());
			
			boolean assigned = false;			
			for(Iterator<PnMaterialAssignment> innerIterator = assignmentList.iterator(); innerIterator.hasNext();)
			{
				if(innerIterator.next().getComp_id().getMaterialId() == Integer.valueOf(material.getMaterialId()))
				{
					assigned = true;
					break;
				}
			}
			
			MaterialAssignmentHelper assignment = new MaterialAssignmentHelper(material, assigned);
			materialsAssigned.add(assignment);
		}
	}

	public ArrayList<MaterialAssignmentHelper> getMaterialsAssigned() {
		return materialsAssigned;
	}

	public String getSpaceId() {
		return spaceId;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setMaterialsAssigned(ArrayList<MaterialAssignmentHelper> materialsAssigned) {
		this.materialsAssigned = materialsAssigned;
	}

	public void setSpaceId(String spaceId) {
		this.spaceId = spaceId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
}
