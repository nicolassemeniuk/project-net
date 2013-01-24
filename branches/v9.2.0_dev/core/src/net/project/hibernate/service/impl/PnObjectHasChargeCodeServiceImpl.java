/**
 * 
 */
package net.project.hibernate.service.impl;

import java.util.List;

import net.project.hibernate.dao.IPnObjectHasChargeCodeDAO;
import net.project.hibernate.model.PnChargeCode;
import net.project.hibernate.model.PnObjectHasChargeCode;
import net.project.hibernate.model.PnObjectHasChargeCodePK;
import net.project.hibernate.service.IPnObjectHasChargeCodeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * PnObjectHasChargeCode database service implementation 
 * 
 * @author  Ritesh S
 *
 */
@Service(value="pnObjectHasChargeCodeService")
public class PnObjectHasChargeCodeServiceImpl implements IPnObjectHasChargeCodeService{

	@Autowired
	IPnObjectHasChargeCodeDAO pnObjectHasChargeCodeDAO;
	
	public void setPnObjectHasChargeCodeDAO(IPnObjectHasChargeCodeDAO pnObjectHasChargeCodeDAO) {
		this.pnObjectHasChargeCodeDAO = pnObjectHasChargeCodeDAO;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnObjectHasChargeCodeService#save(java.lang.Integer, java.lang.Integer)
	 */
	public void save(Integer objectId, Integer chargeCodeId, Integer spaceId) {
		PnObjectHasChargeCodePK objectHasChargeCodePK = new PnObjectHasChargeCodePK(objectId, spaceId);
		PnObjectHasChargeCode objectHasChargeCode = new PnObjectHasChargeCode(objectHasChargeCodePK, chargeCodeId);
		pnObjectHasChargeCodeDAO.createOrUpdate(objectHasChargeCode);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnObjectHasChargeCodeService#getChargeCodeAssignedPersonFromParentBusiness(java.lang.Integer, java.lang.String)
	 */
	public List<PnObjectHasChargeCode> getChargeCodeAssignedPersonFromParentBusiness(Integer spaceId, String childSpaceType) {
		return pnObjectHasChargeCodeDAO.getChargeCodeAssignedPersonFromParentBusiness(spaceId, childSpaceType);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnObjectHasChargeCodeService#getChargeCodeAssignedPersonFromParentProject(java.lang.Integer)
	 */
	public List<PnObjectHasChargeCode> getChargeCodeAssignedPersonFromParentProject(Integer spaceId){
		return pnObjectHasChargeCodeDAO.getChargeCodeAssignedPersonFromParentProject(spaceId);
	}

}
