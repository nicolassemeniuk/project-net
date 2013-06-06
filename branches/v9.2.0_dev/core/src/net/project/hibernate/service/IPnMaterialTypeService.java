package net.project.hibernate.service;

import net.project.hibernate.model.PnMaterialType;
import net.project.material.PnMaterialTypeList;

public interface IPnMaterialTypeService {
	
	/**
	 * Obtain a material type from a certain id.
	 * @param materialTypeId the id from the material type.
	 * @return a material type.
	 */
	public PnMaterialType getMaterialTypeById(Integer materialTypeId);
	
	/**
	 * Obtain all the material types on the application.
	 * @return a list of materials types.
	 */
	public PnMaterialTypeList getMaterialTypes();
	

}
