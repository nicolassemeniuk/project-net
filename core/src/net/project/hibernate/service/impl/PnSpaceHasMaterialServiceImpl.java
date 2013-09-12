package net.project.hibernate.service.impl;

import java.util.List;

import net.project.hibernate.dao.IPnSpaceHasMaterialDAO;
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
	public PnSpaceHasMaterial getPnSpaceHasMaterial(String spaceId,
			String MaterialId) {
		return this.pnSpaceHasMaterialDAO.getPnSpaceHasMaterial(Integer.valueOf(spaceId), Integer.valueOf(MaterialId));		
	}

	@Override
	public boolean spaceHasMaterial(String spaceId, String materialId) {
		return this.pnSpaceHasMaterialDAO.spaceHasMaterial(Integer.valueOf(spaceId), Integer.valueOf(materialId));
	}

	@Override
	public List<Integer> getMaterialsFromSpace(String spaceId) {
		return this.pnSpaceHasMaterialDAO.getMaterialsFromSpace(Integer.valueOf(spaceId));
	}

	@Override
	public void associateMaterial(String spaceId, String materialId) {
		PnSpaceHasMaterialPK spaceHasMaterialPK = new PnSpaceHasMaterialPK(Integer.valueOf(spaceId), Integer.valueOf(materialId));
		PnSpaceHasMaterial spaceHasMaterial = new PnSpaceHasMaterial();
		spaceHasMaterial.setComp_id(spaceHasMaterialPK);
		spaceHasMaterial.setRecordStatus("A");
		this.pnSpaceHasMaterialDAO.create(spaceHasMaterial);	
	}

	@Override
	public Integer getSpaceOfMaterial(String materialId) {
		return this.pnSpaceHasMaterialDAO.getSpaceOfMaterial(Integer.valueOf(materialId));
	}

	
	

}
