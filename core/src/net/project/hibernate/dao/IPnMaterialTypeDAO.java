package net.project.hibernate.dao;

import net.project.hibernate.model.PnMaterialType;
import net.project.material.PnMaterialTypeList;

public interface IPnMaterialTypeDAO extends IDAO<PnMaterialType, Integer> {

	public PnMaterialType getMaterialTypeById(Integer MaterialTypeId);
	
	public PnMaterialTypeList getMaterialTypes();

}
