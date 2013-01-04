package net.project.hibernate.dao;

import java.math.BigDecimal;
import java.util.List;

import net.project.hibernate.model.PnDocProvider;

public interface IPnDocProviderDAO extends IDAO<PnDocProvider, BigDecimal> {

	public List<PnDocProvider> getDocProviderIds();
	
}
