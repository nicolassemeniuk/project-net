package net.project.hibernate.service.impl;

import java.math.BigDecimal;

import net.project.hibernate.dao.IPnSpaceViewDAO;
import net.project.hibernate.model.PnSpaceView;
import net.project.hibernate.service.IPnSpaceViewService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "pnSpaceViewService")
public class PnSpaceViewServiceImpl implements IPnSpaceViewService {
	
	/**
	 * PnMaterial data access object
	 */
	@Autowired
	private IPnSpaceViewDAO pnSpaceViewDAO;

	@Override
	public PnSpaceView getSpaceView(String spaceId) {
		return this.pnSpaceViewDAO.getSpaceView(new BigDecimal(spaceId));
	}

}
