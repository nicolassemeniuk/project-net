package net.project.material;

import java.util.ArrayList;
import java.util.Collection;

import net.project.hibernate.model.PnMaterial;

public class PnMaterialList extends ArrayList<PnMaterial>{
	
	public PnMaterialList()	{
		super();
	}
	
	public PnMaterialList(Collection<PnMaterial> collection){
		super(collection);
	}
}
