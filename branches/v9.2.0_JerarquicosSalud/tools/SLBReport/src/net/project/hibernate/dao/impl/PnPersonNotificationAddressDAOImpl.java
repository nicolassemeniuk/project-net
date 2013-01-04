package net.project.hibernate.dao.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnPersonNotificationAddressDAO;
import net.project.hibernate.model.PnPersonNotificationAddress;

public class PnPersonNotificationAddressDAOImpl extends AbstractHibernateDAO<PnPersonNotificationAddress, BigDecimal> implements IPnPersonNotificationAddressDAO {

	public PnPersonNotificationAddressDAOImpl() {
		super(PnPersonNotificationAddress.class);
	}

}
