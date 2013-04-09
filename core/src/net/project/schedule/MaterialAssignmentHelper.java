package net.project.schedule;

import net.project.material.Material;

public class MaterialAssignmentHelper {
	
	Material material;
	Boolean assigned;	
	
	/** This allow us to tell if we can assign the material or not,
	 * if it's consumable and has been assigned, we can't assign it twice.
	 * And this would be FALSE.
	 */
	Boolean enabledForAssignment;
	
	public MaterialAssignmentHelper(Material material, Boolean assigned, Boolean enabledForAssignment){
		this.material = material;
		this.assigned = assigned;
		this.enabledForAssignment = enabledForAssignment;
	}	
	
	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
	public Boolean getAssigned() {
		return assigned;
	}
	public void setAssigned(Boolean assigned) {
		this.assigned = assigned;
	}
	
	public boolean isAssigned()
	{
		return this.assigned;
	}
	
	public String getDisplayName()
	{
		return material.getName();
	}
	
	public String isAssignedMaterialChecked()
	{
		if(assigned)
			return "checked";
		else
			return "";
	}
	
	public String isAssignedMaterialEnabled(){
		if(enabledForAssignment)
			return "";
		else
			return "disabled";
		
	}
}
