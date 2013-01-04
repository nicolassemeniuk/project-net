/**
 * 
 */
package net.project.hibernate.service.impl;

import java.util.List;

import net.project.hibernate.dao.IPnChargeCodeDAO;
import net.project.hibernate.model.PnChargeCode;
import net.project.hibernate.service.IPnChargeCodeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Ritesh S
 *
 */
@Service(value="pnChargeCodeService")
public class PnChargeCodeServiceImpl implements IPnChargeCodeService{

	@Autowired
	private IPnChargeCodeDAO pnChargeCodeDAO;

	public void setPnBusinessHasViewDAO(IPnChargeCodeDAO pnChargeCodeDAO) {
		this.pnChargeCodeDAO = pnChargeCodeDAO;
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnChargeCodeService#save(net.project.hibernate.model.PnChargeCode)
	 */
	public int save(PnChargeCode pnChargeCode) {
		return pnChargeCodeDAO.create(pnChargeCode);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnChargeCodeService#delete(net.project.hibernate.model.PnChargeCode)
	 */
	public void delete(PnChargeCode pnChargeCode) {
		pnChargeCodeDAO.delete(pnChargeCode);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnChargeCodeService#update(net.project.hibernate.model.PnChargeCode)
	 */
	public void update(PnChargeCode pnChargeCode) {
		pnChargeCodeDAO.update(pnChargeCode);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnChargeCodeService#getChargeCodeByBusinessId(java.lang.Integer)
	 */
	public List<PnChargeCode> getChargeCodeByBusinessId(Integer businessId) {
		return pnChargeCodeDAO.getChargeCodeByBusinessId(businessId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnChargeCodeService#getRootBusinessChargeCodeBySubbusinessId(java.lang.Integer)
	 */
	public List<PnChargeCode> getRootBusinessChargeCodeBySubBusinessId(Integer businessId){
		return pnChargeCodeDAO.getRootBusinessChargeCodeBySubBusinessId(businessId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnChargeCodeService#getChargeCodeByProjectId(java.lang.Integer)
	 */
	public List<PnChargeCode> getChargeCodeByProjectId(Integer projectId) {
		return pnChargeCodeDAO.getChargeCodeByProjectId(projectId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnChargeCodeService#getChargeCodeAppliedOnPersonInSpace(java.lang.Integer, java.lang.Integer)
	 */
	public PnChargeCode getChargeCodeAppliedOnPersonInSpace(Integer personId, Integer spaceId) {
		return pnChargeCodeDAO.getChargeCodeAppliedOnPersonInSpace(personId, spaceId);
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnChargeCodeService#getChargeCodeApliedOnTask(java.lang.Integer, java.lang.Integer)
	 */
	public PnChargeCode getChargeCodeApliedOnTask(Integer taskId, Integer spaceId) {
		return pnChargeCodeDAO.getChargeCodeApliedOnTask(taskId, spaceId);
	}

}
