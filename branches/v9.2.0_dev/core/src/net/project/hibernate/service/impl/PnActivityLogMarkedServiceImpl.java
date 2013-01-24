/**
 * 
 */
package net.project.hibernate.service.impl;

import java.util.Date;
import java.util.List;

import net.project.hibernate.dao.IPnActivityLogMarkedDAO;
import net.project.hibernate.model.PnActivityLogMarked;
import net.project.hibernate.model.PnActivityLogMarkedPK;
import net.project.hibernate.service.IPnActivityLogMarkedService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="pnActivityLogMarkedService")
public class PnActivityLogMarkedServiceImpl implements IPnActivityLogMarkedService {
	
	@Autowired
	private IPnActivityLogMarkedDAO pnActivityLogMarkedDAO;
	
	/**
	 * @param pnActivityLogMarkedDAO the pnActivityLogMarkedDAO to set
	 */
	public void setPnActivityLogMarkedDAO(IPnActivityLogMarkedDAO pnActivityLogMarkedDAO) {
		this.pnActivityLogMarkedDAO = pnActivityLogMarkedDAO;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnActivityLogMarkedService#save(net.project.hibernate.model.PnActivityLogMarked)
	 */
	public PnActivityLogMarkedPK save(PnActivityLogMarked pnActivityLogMarked){
		return pnActivityLogMarkedDAO.create(pnActivityLogMarked);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnActivityLogMarkedService#delete(net.project.hibernate.model.PnActivityLogMarked)
	 */
	public void delete(PnActivityLogMarked pnActivityLogMarked){
		pnActivityLogMarkedDAO.delete(pnActivityLogMarked);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnActivityLogMarkedService#deleteByActivityIds(java.util.List)
	 */
	public void deleteByActivityIds(java.util.List activityIds){
		pnActivityLogMarkedDAO.deleteByActivityIds(activityIds);
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnActivityLogMarkedService#getMarkedByPersonId(java.lang.Integer, java.util.Date, java.util.Date, java.util.List)
	 */
	public String getMarkedByPersonId(Integer personId, Date startDate, Date endDate, List activityIdsPerPage) {
		return pnActivityLogMarkedDAO.getMarkedByPersonId(personId, startDate, endDate, activityIdsPerPage);
	}
}
