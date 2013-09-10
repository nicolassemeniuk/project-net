package net.project.hibernate.service;

import net.project.hibernate.model.PnSpaceView;

public interface IPnSpaceViewService {
	
	/**
	 * Get a space view from an space id.
	 * @param spaceId the id of the space.
	 * @return a Space View with space data.
	 */
	public PnSpaceView getSpaceView(String spaceId);

}
