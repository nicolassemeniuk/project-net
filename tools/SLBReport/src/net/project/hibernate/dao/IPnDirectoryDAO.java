package net.project.hibernate.dao;

import java.math.BigDecimal;
import java.util.List;

import net.project.hibernate.model.PnDirectory;

public interface IPnDirectoryDAO extends IDAO<PnDirectory, BigDecimal> {
	
	public List<PnDirectory> getDefaultDirectory();

}
