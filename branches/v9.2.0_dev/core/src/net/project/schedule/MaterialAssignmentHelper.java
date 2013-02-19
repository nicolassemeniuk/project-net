package net.project.schedule;

import net.project.material.Material;

public class MaterialAssignmentHelper {
	
	Material material;
	Boolean assigned;	
	
	public MaterialAssignmentHelper(Material material, Boolean assigned){
		this.material = material;
		this.assigned = assigned;
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
}
