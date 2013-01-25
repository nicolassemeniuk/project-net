package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnMaterialTypeDAO;
import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.model.PnMaterialType;
import net.project.hibernate.service.IPnMaterialTypeService;
import net.project.material.PnMaterialTypeList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="pnMaterialTypeService")
public class PnMaterialTypeServiceImpl implements IPnMaterialTypeService {

	/**
	 * PnMaterialType data access object
	 */
	@Autowired
	private IPnMaterialTypeDAO pnMaterialTypeDAO;
	
	public void setPnMaterialTypeDAO(IPnMaterialTypeDAO pnMaterialTypeDAO) {
		this.pnMaterialTypeDAO = pnMaterialTypeDAO;
	}

	public IPnMaterialTypeDAO getPnMaterialTypeDAO() {
		return pnMaterialTypeDAO;
	}
	
	@Override
	public PnMaterialType getMaterialTypeById(Integer id) {
		return pnMaterialTypeDAO.getMaterialTypeById(id);
	}

	@Override
	public PnMaterialTypeList getMaterialTypes() {
		return pnMaterialTypeDAO.getMaterialTypes();
	}

	@Override
	public Integer saveMaterialType(PnMaterial pnMaterial) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteMaterialType(PnMaterial pnMaterial) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateMaterialType(PnMaterial pnMaterial) {
		// TODO Auto-generated method stub

	}

}
