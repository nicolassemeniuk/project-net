package net.project.material;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.hibernate.model.PnMaterialAssignment;
import net.project.hibernate.service.ServiceFactory;

public class MaterialAssignmentList implements Serializable {

	private ArrayList<MaterialAssignment> materialAssignments = new ArrayList<MaterialAssignment>();

	public void MaterialAssignmenList() {
		materialAssignments = new ArrayList<MaterialAssignment>();
	}

	public void load(String spaceId, String objectId) {
		PnMaterialAssignmentList assignmentList = ServiceFactory.getInstance().getPnMaterialAssignmentService().getMaterialsAssignment(spaceId, objectId);
		for (PnMaterialAssignment assignee : assignmentList) {
			materialAssignments.add(new MaterialAssignment(assignee));
		}
	}

	public MaterialAssignment getAssignedMaterial(String materialId) {
		for (MaterialAssignment asignee : materialAssignments) {
			if (asignee.getMaterialId().equals(materialId)) {
				return asignee;
			}
		}
		return new MaterialAssignment();
	}

	public void removeAssignment(MaterialAssignment material) {
		for (MaterialAssignment asignee : materialAssignments) {
			if (asignee.getMaterialId().equals(material.getMaterialId())) {
				materialAssignments.remove(asignee);
				break;
			}
		}
	}

	public void clear() {
		materialAssignments.clear();
	}

	public void add(MaterialAssignment materialAssignment) {
		materialAssignments.add(materialAssignment);
	}

	public Iterator<MaterialAssignment> getIterator() {
		return materialAssignments.iterator();
	}

	public boolean overAssignationExists() {
		for (MaterialAssignment asignee : materialAssignments) {
			if (asignee.getOverassigned()) {
				return true;
			}
		}
		return false;
	}

	public PnMaterialAssignmentList getPnMaterialAssignmentList() {
		PnMaterialAssignmentList materialList = new PnMaterialAssignmentList();
		for (MaterialAssignment assignment : materialAssignments)
			materialList.add(assignment.getPnMaterialAssignment());
		return materialList;
	}
}
