package net.project.hibernate.dao;

import net.project.hibernate.model.PnMaterialType;
import net.project.material.PnMaterialTypeList;

public interface IPnMaterialTypeDAO extends IDAO<PnMaterialType, Integer> {

	/**
	 * Get a material type by a certain id.
	 * @param MaterialTypeId the id of the material type.
	 * @return a material type
	 */
	public PnMaterialType getMaterialTypeById(Integer MaterialTypeId);
	
	/**
	 * Get a list of all materials types on the application.
	 * @return a list of material types.
	 */
	public PnMaterialTypeList getMaterialTypes();

}
