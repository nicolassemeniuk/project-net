package net.project.material;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import net.project.hibernate.model.PnMaterialAssignment;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.IXMLPersistence;

public class MaterialAssignmentList implements Serializable, Iterable<MaterialAssignment> {

	private ArrayList<MaterialAssignment> materialAssignments = new ArrayList<MaterialAssignment>();

	public void MaterialAssignmenList() {
		materialAssignments = new ArrayList<MaterialAssignment>();
	}

	public void load(String spaceId) {
		PnMaterialAssignmentList assignmentList = ServiceFactory.getInstance().getPnMaterialAssignmentService().getMaterialsAssignment(spaceId);
		for (PnMaterialAssignment assignee : assignmentList) {
			materialAssignments.add(new MaterialAssignment(assignee));
		}
	}	
	
	public void load(String spaceId, String objectId) {
		PnMaterialAssignmentList assignmentList = ServiceFactory.getInstance().getPnMaterialAssignmentService().getMaterialsAssignment(spaceId, objectId);
		for (PnMaterialAssignment assignee : assignmentList) {
			materialAssignments.add(new MaterialAssignment(assignee));
		}
	}
	
	public void load(String materialId, Date startDate, Date endDate){
		PnMaterialAssignmentList assignmentList = ServiceFactory.getInstance().getPnMaterialAssignmentService().getAssignmentsForMaterial(materialId, startDate, endDate);
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
	
	public Integer size(){
		return materialAssignments.size();
	}

	public boolean containsDisabled(String materialID)
	{
		for(MaterialAssignment assignment : materialAssignments){
			if(assignment.getMaterialId().equals(materialID) && assignment.getRecordStatus().equals("D"))
				return true;
		}
		
		return false;
	}
	
	public void add(MaterialAssignment materialAssignment) {
		materialAssignments.add(materialAssignment);
	}

	public Iterator<MaterialAssignment> getIterator() {
		return materialAssignments.iterator();
	}

	public boolean overAssignationExists() {
		for (MaterialAssignment asignee : materialAssignments) {
			if (asignee.getOverassigned() && asignee.getRecordStatus().equals("A")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Iterator<MaterialAssignment> iterator() {
		return this.materialAssignments.iterator();
	}

	public boolean hasActive()
	{
		for (MaterialAssignment asignee : materialAssignments) {
			if (asignee.getRecordStatus().equals("A"))
				return true;
		}
		
		return false;
	}
	
    /**
     * Converts the object to XML representation.
     * This method returns the object as XML text.
     *
     * @return XML representation
     */
    public String getXML() {
        return (IXMLPersistence.XML_VERSION + getXMLBody());
    }
	
    /**
     * Converts the object to XML representation without the XML version tag.
     * This method returns the object as XML text.
     *
     * @return XML representation
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();

        xml.append("<assignment_list>\n");
        for (int i = 0; i < materialAssignments.size(); i++)
            xml.append(((MaterialAssignment) materialAssignments.get(i)).getXMLBody());
        xml.append("</assignment_list>\n");
        return xml.toString();
    }
    

	
	

}
