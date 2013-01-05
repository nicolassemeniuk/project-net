package net.project.hibernate.dao.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnDocContainerDAO;
import net.project.hibernate.model.PnDocContainer;

public class PnDocContainerDAOImpl extends AbstractHibernateDAO<PnDocContainer, BigDecimal> implements IPnDocContainerDAO {

	public PnDocContainerDAOImpl() {
		super(PnDocContainer.class);
	}

}
