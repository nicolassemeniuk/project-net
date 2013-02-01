package net.project.hibernate.service.impl;

import java.util.List;

import net.project.hibernate.dao.IPnSpaceHasMaterialDAO;
import net.project.hibernate.model.PnMaterial;
import net.project.hibernate.model.PnSpaceHasMaterial;
import net.project.hibernate.model.PnSpaceHasMaterialPK;
import net.project.hibernate.service.IPnSpaceHasMaterialService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "pnSpaceHasMaterialService")
public class PnSpaceHasMaterialServiceImpl implements IPnSpaceHasMaterialService {
	
	/**
	 * PnSpaceHasMaterial data access object
	 */
	@Autowired
	private IPnSpaceHasMaterialDAO pnSpaceHasMaterialDAO;
	
	public IPnSpaceHasMaterialDAO getPnSpaceHasMaterialDAO() {
		return pnSpaceHasMaterialDAO;
	}

	public void setPnSpaceHasMaterialDAO(
			IPnSpaceHasMaterialDAO pnSpaceHasMaterialDAO) {
		this.pnSpaceHasMaterialDAO = pnSpaceHasMaterialDAO;
	}

	@Override
	public PnSpaceHasMaterial getPnSpaceHasMaterial(Integer spaceId,
			Integer MaterialId) {
		return this.pnSpaceHasMaterialDAO.getPnSpaceHasMaterial(spaceId, MaterialId);		
	}

	@Override
	public boolean spaceHasMaterial(Integer spaceId, Integer materialId) {
		return this.pnSpaceHasMaterialDAO.spaceHasMaterial(spaceId, materialId);
	}

	@Override
	public List<Integer> getMaterialsFromSpace(Integer spaceId) {
		return this.pnSpaceHasMaterialDAO.getMaterialsFromSpace(spaceId);
	}

	@Override
	public void associateMaterial(Integer spaceId, PnMaterial material) {
		PnSpaceHasMaterialPK spaceHasMaterialPK = new PnSpaceHasMaterialPK(spaceId, material.getMaterialId());
		PnSpaceHasMaterial spaceHasMaterial = new PnSpaceHasMaterial();
		spaceHasMaterial.setComp_id(spaceHasMaterialPK);
		spaceHasMaterial.setRecordStatus("A");
		this.pnSpaceHasMaterialDAO.create(spaceHasMaterial);	
	}

}
