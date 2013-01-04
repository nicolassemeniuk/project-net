package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.model.PnObjectType;

public interface IPnObjectTypeDAO extends IDAO<PnObjectType, String> {
	
	public List<PnObjectType> findObjectTypes();
	
}
