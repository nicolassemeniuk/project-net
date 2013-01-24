package net.project.hibernate.dao;

import java.math.BigDecimal;

import net.project.hibernate.model.PnGroup;

public interface IPnGroupDAO extends IDAO<PnGroup, BigDecimal>{
	
	public BigDecimal getGroupId(BigDecimal spaceId, BigDecimal personId);

}
