package net.project.material;

import java.util.ArrayList;
import java.util.Collection;

import net.project.hibernate.model.PnAssignmentMaterial;

public class PnAssignmentMaterialList extends ArrayList<PnAssignmentMaterial> {
	
	public PnAssignmentMaterialList()	{
		super();
	}
	
	public PnAssignmentMaterialList(Collection<PnAssignmentMaterial> collection){
		super(collection);
	}

}
