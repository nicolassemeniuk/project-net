package net.project.hibernate.dao.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnPortfolioDAO;
import net.project.hibernate.model.PnPortfolio;

public class PnPortfolioDAOImpl extends AbstractHibernateDAO<PnPortfolio, BigDecimal> implements IPnPortfolioDAO {
	
	public PnPortfolioDAOImpl(){
		super(PnPortfolio.class);
	}



}
