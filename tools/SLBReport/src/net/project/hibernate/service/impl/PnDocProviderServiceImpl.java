package net.project.hibernate.service.impl;

import java.math.BigDecimal;
import java.util.List;

import net.project.hibernate.dao.IPnDocProviderDAO;
import net.project.hibernate.model.PnDocProvider;
import net.project.hibernate.service.IPnDocProviderService;

public class PnDocProviderServiceImpl implements IPnDocProviderService {

	private IPnDocProviderDAO pnDocProviderDAO;

	public void setPnDocProviderDAO(IPnDocProviderDAO pnDocProviderDAO) {
		this.pnDocProviderDAO = pnDocProviderDAO;
	}

	public PnDocProvider getDocProvider(BigDecimal docProviderId) {
		return pnDocProviderDAO.findByPimaryKey(docProviderId);
	}

	public BigDecimal saveDocProvider(PnDocProvider pnDocProvider) {
		return pnDocProviderDAO.create(pnDocProvider);
	}

	public void deleteDocProvider(PnDocProvider pnDocProvider) {
		pnDocProviderDAO.delete(pnDocProvider);
	}

	public void updateDocProvider(PnDocProvider pnDocProvider) {
		pnDocProviderDAO.update(pnDocProvider);
	}

	public List<PnDocProvider> getDocProviderIds() {
		return pnDocProviderDAO.getDocProviderIds();
	}

}
