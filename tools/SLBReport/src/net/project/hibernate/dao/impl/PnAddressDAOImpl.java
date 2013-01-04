package net.project.hibernate.dao.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnAddressDAO;
import net.project.hibernate.model.PnAddress;

public class PnAddressDAOImpl extends AbstractHibernateDAO<PnAddress, BigDecimal> implements IPnAddressDAO {

	public PnAddressDAOImpl(){
		super(PnAddress.class);
	}
	
}
