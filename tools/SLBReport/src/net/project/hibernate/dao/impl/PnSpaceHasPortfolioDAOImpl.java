package net.project.hibernate.dao.impl;

import net.project.hibernate.dao.IPnSpaceHasPortfolioDAO;
import net.project.hibernate.model.PnSpaceHasPortfolio;
import net.project.hibernate.model.PnSpaceHasPortfolioPK;

public class PnSpaceHasPortfolioDAOImpl extends AbstractHibernateDAO<PnSpaceHasPortfolio, PnSpaceHasPortfolioPK> implements IPnSpaceHasPortfolioDAO {
	
	public PnSpaceHasPortfolioDAOImpl(){
		super(PnSpaceHasPortfolio.class);
	}


}
