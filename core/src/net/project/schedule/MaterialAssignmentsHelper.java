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
	private String parentBusinessID;
	private String objectId;

	public MaterialAssignmentsHelper() {
		this.materialsAssigned = new ArrayList<MaterialAssignmentHelper>();
	}

	public ArrayList<MaterialAssignmentHelper> getMaterials() {
		return materialsAssigned;
	}

	public void setMaterials(ArrayList<MaterialAssignmentHelper> materialsAssigned) {
		this.materialsAssigned = materialsAssigned;
	}

	public void load() {
		PnMaterialList materials = ServiceFactory.getInstance().getMaterialService().getMaterialsFromSpace(spaceId);

		PnMaterialAssignmentList assignmentList;

		// If objectId is not present load all material assignments for this
		// space
		if (this.objectId == null)
			assignmentList = ServiceFactory.getInstance().getPnMaterialAssignmentService().getMaterialsAssignment(spaceId);
		else
			assignmentList = ServiceFactory.getInstance().getPnMaterialAssignmentService().getMaterialsAssignment(spaceId, objectId);

		for (PnMaterial pnMaterial : materials) {

			//Only the active Materials
			if (pnMaterial.getRecordStatus().equals("A")) {
				Material material = new Material(pnMaterial);

				boolean assigned = false;
				boolean enabledForAssignment = true;

				// Obtain ALL the assignments for the material
				PnMaterialAssignmentList assignmentsForMaterial = ServiceFactory.getInstance().getPnMaterialAssignmentService()
						.getAssignmentsForMaterial(material.getMaterialId());

				// Is consumable and there are any assignments for that
				// material?
				if (material.getConsumable() && assignmentsForMaterial.size() > 0) {

					for (PnMaterialAssignment assignmentMaterial : assignmentsForMaterial) {

						String statusAssignment = assignmentMaterial.getRecordStatus();

						// Is the task we are in? is it active?
						if (objectId != null && assignmentMaterial.getComp_id().getObjectId().equals(Integer.valueOf(objectId)) && statusAssignment.equals("A"))
							enabledForAssignment = true;
						// Is assigned on other task (the assignation is ACTIVE)
						else if (statusAssignment.equals("A"))
							enabledForAssignment = false;
					}
				} else {
					enabledForAssignment = true;
				}

				for (Iterator<PnMaterialAssignment> innerIterator = assignmentList.iterator(); innerIterator.hasNext();) {
					PnMaterialAssignment assignment = innerIterator.next();
					Integer id = assignment.getComp_id().getMaterialId();
					Integer id2 = Integer.valueOf(material.getMaterialId());
					String status = assignment.getRecordStatus();
					if (id.equals(id2) && status.equals("A")) {
						assigned = true;
						break;
					}
				}

				MaterialAssignmentHelper assignment = new MaterialAssignmentHelper(material, assigned, enabledForAssignment);
				materialsAssigned.add(assignment);

			}

		}
	}
	
	public void loadForBusiness() {
		PnMaterialList materials = ServiceFactory.getInstance().getMaterialService().getMaterialsFromSpace(parentBusinessID);

		PnMaterialAssignmentList assignmentList;

		// If objectId is not present load all material assignments for this
		// space
		if (this.objectId == null)
			assignmentList = ServiceFactory.getInstance().getPnMaterialAssignmentService().getMaterialsAssignment(spaceId);
		else
			assignmentList = ServiceFactory.getInstance().getPnMaterialAssignmentService().getMaterialsAssignment(spaceId, objectId);

		for (PnMaterial pnMaterial : materials) {

			//Only the active Materials
			if (pnMaterial.getRecordStatus().equals("A")) {
				Material material = new Material(pnMaterial);

				boolean assigned = false;
				boolean enabledForAssignment = true;

				// Obtain ALL the assignments for the material
				PnMaterialAssignmentList assignmentsForMaterial = ServiceFactory.getInstance().getPnMaterialAssignmentService()
						.getAssignmentsForMaterial(material.getMaterialId());

				// Is consumable and there are any assignments for that
				// material?
				if (material.getConsumable() && assignmentsForMaterial.size() > 0) {

					for (PnMaterialAssignment assignmentMaterial : assignmentsForMaterial) {

						String statusAssignment = assignmentMaterial.getRecordStatus();

						// Is the task we are in? is it active?
						if (objectId != null && assignmentMaterial.getComp_id().getObjectId().equals(Integer.valueOf(objectId)) && statusAssignment.equals("A"))
							enabledForAssignment = true;
						// Is assigned on other task (the assignation is ACTIVE)
						else if (statusAssignment.equals("A"))
							enabledForAssignment = false;
					}
				} else {
					enabledForAssignment = true;
				}

				for (Iterator<PnMaterialAssignment> innerIterator = assignmentList.iterator(); innerIterator.hasNext();) {
					PnMaterialAssignment assignment = innerIterator.next();
					Integer id = assignment.getComp_id().getMaterialId();
					Integer id2 = Integer.valueOf(material.getMaterialId());
					String status = assignment.getRecordStatus();
					if (id.equals(id2) && status.equals("A")) {
						assigned = true;
						break;
					}
				}

				MaterialAssignmentHelper assignment = new MaterialAssignmentHelper(material, assigned, enabledForAssignment);
				materialsAssigned.add(assignment);

			}

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

	public String getParentBusinessID() {
		return parentBusinessID;
	}

	public void setParentBusinessID(String parentBusinessID) {
		this.parentBusinessID = parentBusinessID;
	}
	
	
}
