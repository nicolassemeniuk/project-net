/**
 * 
 */
package net.project.hibernate.service.impl;

import java.util.List;

import net.project.hibernate.dao.IPnBusinessHasViewDAO;
import net.project.hibernate.model.PnBusinessHasView;
import net.project.hibernate.model.PnBusinessHasViewPK;
import net.project.hibernate.service.IPnBusinessHasViewService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
  * PnSpaceViewContext database service implementation 
 * 
 * @author Ritesh S
 *
 */
@Service(value="pnBusinessHasViewService")
public class PnBusinessHasViewServiceImpl implements IPnBusinessHasViewService{

	@Autowired
	private IPnBusinessHasViewDAO pnBusinessHasViewDAO;

	public void setPnBusinessHasViewDAO(IPnBusinessHasViewDAO pnBusinessHasViewDAO) {
		this.pnBusinessHasViewDAO = pnBusinessHasViewDAO;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnBusinessHasViewService#save(java.lang.Integer, java.lang.Integer)
	 */
	public void save(Integer businessId, Integer viewId) {
		PnBusinessHasViewPK  pnBusinessHasViewPK   = new PnBusinessHasViewPK(businessId, viewId);
		PnBusinessHasView pnBusinessHasView = new PnBusinessHasView(pnBusinessHasViewPK);
		pnBusinessHasViewDAO.createOrUpdate(pnBusinessHasView);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnBusinessHasViewService#getSharedViewByBusiness(java.lang.Integer)
	 */
	public List<PnBusinessHasView> getSharedViewByBusiness(Integer businessId) {
		return pnBusinessHasViewDAO.getSharedViewByBusiness(businessId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnBusinessHasViewService#getBusinessByView(java.lang.Integer)
	 */
	public List<PnBusinessHasView> getBusinessByView(Integer viewId) {
		return pnBusinessHasViewDAO.getBusinessByView(viewId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnBusinessHasViewService#delete(java.lang.Integer, java.lang.Integer)
	 */
	public void delete(Integer businessId, Integer viewId) {
		PnBusinessHasViewPK  pnBusinessHasViewPK   = new PnBusinessHasViewPK(businessId, viewId);
		PnBusinessHasView pnBusinessHasView = new PnBusinessHasView(pnBusinessHasViewPK);
		pnBusinessHasViewDAO.delete(pnBusinessHasView);
	}

}
