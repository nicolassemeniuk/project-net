/**
 * 
 */
package net.project.hibernate.service.impl;

import java.util.List;

import net.project.hibernate.dao.IPnSpaceViewContextDAO;
import net.project.hibernate.model.PnSpaceViewContext;
import net.project.hibernate.model.PnSpaceViewContextPK;
import net.project.hibernate.service.IPnSpaceViewContextService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * PnSpaceViewContext database service implementation 
 * 
 * @author Ritesh S
 *
 */
@Service(value="pnSpaceViewContextService")
public class PnSpaceViewContextServiceImpl implements IPnSpaceViewContextService{

	@Autowired
	private IPnSpaceViewContextDAO pnViewHasPersonDAO;

	public void setPnSpaceViewContextDAO(IPnSpaceViewContextDAO pnViewHasPersonDAO) {
		this.pnViewHasPersonDAO = pnViewHasPersonDAO;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnSpaceViewContexService#save(java.lang.Integer, java.lang.Integer)
	 */
	public void save(Integer spaceId, Integer viewId) {
		PnSpaceViewContextPK  pnSpaceViewContextPK   = new PnSpaceViewContextPK(spaceId, viewId);
		PnSpaceViewContext pnSpaceViewContext= new PnSpaceViewContext(pnSpaceViewContextPK);
		pnViewHasPersonDAO.createOrUpdate(pnSpaceViewContext);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnSpaceViewContexService#getSharedViewByPerson(java.lang.Integer)
	 */
	public List<PnSpaceViewContext> getSharedViewByPerson(Integer spaceId) {
		return pnViewHasPersonDAO.getSharedViewByPerson(spaceId);
	}

}
