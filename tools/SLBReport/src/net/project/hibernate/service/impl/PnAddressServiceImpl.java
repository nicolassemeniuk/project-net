package net.project.hibernate.service.impl;
import java.math.BigDecimal;

import net.project.hibernate.dao.IPnAddressDAO;
import net.project.hibernate.model.PnAddress;
import net.project.hibernate.service.IPnAddressService;

public class PnAddressServiceImpl implements IPnAddressService {
	private IPnAddressDAO pnAddressDAO;

	public void setPnAddressDAO(IPnAddressDAO pnAddressDAO) {
		this.pnAddressDAO = pnAddressDAO;
	}

	public PnAddress getAddress(BigDecimal addressId) {
		return pnAddressDAO.findByPimaryKey(addressId);
	}

	public BigDecimal saveAddress(PnAddress pnAddress) {
		return pnAddressDAO.create(pnAddress);
	}

	public void deleteAddress(PnAddress pnAddress) {
		pnAddressDAO.delete(pnAddress);
	}

	public void updateAddress(PnAddress pnAddress) {
		pnAddressDAO.update(pnAddress);
	}

}
