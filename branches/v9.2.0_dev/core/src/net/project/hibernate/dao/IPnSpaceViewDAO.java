package net.project.hibernate.dao;

import java.math.BigDecimal;

import net.project.hibernate.model.PnSpaceView;


public interface IPnSpaceViewDAO extends IDAO<PnSpaceView, BigDecimal> {
	
	/**
	 * Returns a space view for the given space id.
	 * @param spaceId the id of the space.
	 * @return a Space View with space data.
	 */
	public PnSpaceView getSpaceView(BigDecimal spaceId);

}
