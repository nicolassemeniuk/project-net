package net.project.material;

import java.util.ArrayList;
import java.util.Collection;

import net.project.hibernate.model.PnMaterialAssignment;

public class PnMaterialAssignmentList extends ArrayList<PnMaterialAssignment> {
	
	public PnMaterialAssignmentList()	{
		super();
	}
	
	public PnMaterialAssignmentList(Collection<PnMaterialAssignment> collection){
		super(collection);
	}
}
