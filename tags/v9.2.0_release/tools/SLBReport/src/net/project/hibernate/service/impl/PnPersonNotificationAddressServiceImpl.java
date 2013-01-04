package net.project.hibernate.service.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnPersonNotificationAddressDAO;
import net.project.hibernate.model.PnPersonNotificationAddress;
import net.project.hibernate.service.IPnPersonNotificationAddressService;

public class PnPersonNotificationAddressServiceImpl implements IPnPersonNotificationAddressService {

	private IPnPersonNotificationAddressDAO pnPersonNotificationAddressDAO;

	public void setPnPersonNotificationAddressDAO(IPnPersonNotificationAddressDAO pnPersonNotificationAddressDAO) {
		this.pnPersonNotificationAddressDAO = pnPersonNotificationAddressDAO;
	}

	public PnPersonNotificationAddress getPersonNotificationAddress(BigDecimal personNotificationAddressId) {
		return pnPersonNotificationAddressDAO.findByPimaryKey(personNotificationAddressId);
	}

	public BigDecimal savePersonNotificationAddress(PnPersonNotificationAddress pnPersonNotificationAddress) {
		return pnPersonNotificationAddressDAO.create(pnPersonNotificationAddress);
	}

	public void deletePersonNotificationAddress(PnPersonNotificationAddress pnPersonNotificationAddress) {
		pnPersonNotificationAddressDAO.delete(pnPersonNotificationAddress);

	}

	public void updatePersonNotificationAddress(PnPersonNotificationAddress pnPersonNotificationAddress) {
		pnPersonNotificationAddressDAO.update(pnPersonNotificationAddress);
	}

}
