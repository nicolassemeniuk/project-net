package net.project.hibernate.dao;

import java.math.BigDecimal;
import java.util.List;

import net.project.hibernate.model.PnModule;

public interface IPnModuleDAO extends IDAO<PnModule, BigDecimal> {
	
	public List<PnModule>getModuleIds();
	
	public List<PnModule>getModuleDefaultPermissions(BigDecimal spaceId);
	
}
