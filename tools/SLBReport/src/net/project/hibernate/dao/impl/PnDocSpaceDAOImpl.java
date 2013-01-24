package net.project.hibernate.dao.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnDocSpaceDAO;
import net.project.hibernate.model.PnDocSpace;

public class PnDocSpaceDAOImpl extends AbstractHibernateDAO<PnDocSpace, BigDecimal> implements IPnDocSpaceDAO {

	public PnDocSpaceDAOImpl() {
		super(PnDocSpace.class);
	}

}
