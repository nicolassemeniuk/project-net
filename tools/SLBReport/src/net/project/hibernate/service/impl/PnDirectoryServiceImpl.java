package net.project.hibernate.service.impl;

import java.math.BigDecimal;
import java.util.List;

import net.project.hibernate.dao.IPnDirectoryDAO;
import net.project.hibernate.model.PnDirectory;
import net.project.hibernate.service.IPnDirectoryService;

public class PnDirectoryServiceImpl implements IPnDirectoryService {

	private IPnDirectoryDAO pnDirectoryDAO;

	public void setPnDirectoryDAO(IPnDirectoryDAO pnDirectoryDAO) {
		this.pnDirectoryDAO = pnDirectoryDAO;
	}

	public PnDirectory getDirectory(BigDecimal directoryId) {
		return pnDirectoryDAO.findByPimaryKey(directoryId);
	}

	public BigDecimal saveDirectory(PnDirectory pnDirectory) {
		return pnDirectoryDAO.create(pnDirectory);
	}

	public void deleteDirectory(PnDirectory pnDirectory) {
		pnDirectoryDAO.delete(pnDirectory);

	}

	public void updateDirectory(PnDirectory pnDirectory) {
		pnDirectoryDAO.update(pnDirectory);

	}

	public List<PnDirectory> getDefaultDirectory() {
		return pnDirectoryDAO.getDefaultDirectory();
	}

}
