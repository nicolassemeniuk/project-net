package net.project.hibernate.service;

import java.util.List;

import net.project.hibernate.model.PnSpaceHasMaterial;

public interface IPnSpaceHasMaterialService {
	
	public PnSpaceHasMaterial getPnSpaceHasMaterial(Integer spaceId,
			Integer MaterialId);

	public boolean spaceHasMaterial(Integer spaceId, Integer materialId);
	
	public List<Integer> getMaterialsFromSpace(Integer spaceId);

}
