package net.project.hibernate.service.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnGroupDAO;
import net.project.hibernate.model.PnGroup;
import net.project.hibernate.service.IPnGroupService;

public class PnGroupServiceImpl implements IPnGroupService {

	private IPnGroupDAO pnGroupDAO;

	public void setPnGroupDAO(IPnGroupDAO pnGroupDAO) {
		this.pnGroupDAO = pnGroupDAO;
	}

	public PnGroup getGroup(BigDecimal groupId) {
		return pnGroupDAO.findByPimaryKey(groupId);
	}

	public BigDecimal saveGroup(PnGroup pnGroup) {
		return pnGroupDAO.create(pnGroup);
	}

	public void deleteGroup(PnGroup pnGroup) {
		pnGroupDAO.delete(pnGroup);
	}

	public void updateGroup(PnGroup pnGroup) {
		pnGroupDAO.update(pnGroup);
	}

	public BigDecimal getGroupId(BigDecimal spaceId, BigDecimal personId) {
		return pnGroupDAO.getGroupId(spaceId, personId);
	}

}
