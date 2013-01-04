package net.project.hibernate.dao.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnPersonDAO;
import net.project.hibernate.model.PnPerson;

public class PnPersonDAOImpl extends AbstractHibernateDAO<PnPerson, BigDecimal> implements IPnPersonDAO {

	public PnPersonDAOImpl() {
		super(PnPerson.class);
	}

}
