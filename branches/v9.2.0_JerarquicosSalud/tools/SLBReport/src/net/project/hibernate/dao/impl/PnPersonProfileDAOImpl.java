package net.project.hibernate.dao.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnPersonProfileDAO;
import net.project.hibernate.model.PnPersonProfile;

public class PnPersonProfileDAOImpl extends AbstractHibernateDAO<PnPersonProfile, BigDecimal> implements IPnPersonProfileDAO {

	public PnPersonProfileDAOImpl(){
		super(PnPersonProfile.class);
	}
}
