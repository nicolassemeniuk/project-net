package net.project.hibernate.dao.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnSpaceViewDAO;
import net.project.hibernate.model.PnSpaceView;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class PnSpaceViewDAOImpl extends AbstractHibernateAnnotatedDAO<PnSpaceView, BigDecimal> implements IPnSpaceViewDAO {
	
	private static Logger log = Logger.getLogger(PnSpaceViewDAOImpl.class);

	public PnSpaceViewDAOImpl() {
		super(PnSpaceView.class);
	}

	@Override
	public PnSpaceView getSpaceView(BigDecimal spaceId) {
		try {
			SessionFactory factory = getHibernateTemplate().getSessionFactory();
			Session session = factory.openSession();

			PnSpaceView pnSpaceView = (PnSpaceView) session.get(PnSpaceView.class, spaceId);
			session.close();
			return pnSpaceView;
		} catch (Exception e) {
			log.error("Error occurred while getting space view by id " + e.getMessage());
		}
		return null;
	}

}
