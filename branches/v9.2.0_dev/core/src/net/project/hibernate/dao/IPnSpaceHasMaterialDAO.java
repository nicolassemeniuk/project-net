package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.model.PnSpaceHasMaterial;
import net.project.hibernate.model.PnSpaceHasMaterialPK;

public interface IPnSpaceHasMaterialDAO extends
		IDAO<PnSpaceHasMaterial, PnSpaceHasMaterialPK> {

	public PnSpaceHasMaterial getPnSpaceHasMaterial(Integer spaceId,
			Integer MaterialId);

	public boolean spaceHasMaterial(Integer spaceId, Integer materialId);
	
	public List<Integer> getMaterialsFromSpace(Integer spaceId);
	
	public void associateMaterial(PnSpaceHasMaterial spaceHasMaterial);

}
