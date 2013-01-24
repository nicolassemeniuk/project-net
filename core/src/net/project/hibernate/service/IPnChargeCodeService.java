/**
 * 
 */
package net.project.hibernate.service;

import java.util.List;

import net.project.hibernate.model.PnChargeCode;

/**
 * @author Ritesh S
 *
 */
public interface IPnChargeCodeService {
	
	public int save(PnChargeCode pnChargeCode);

	public void delete(PnChargeCode pnChargeCode);

	public void update(PnChargeCode pnChargeCode);
	
	public List<PnChargeCode> getChargeCodeByBusinessId(Integer businessId);
	
	public List<PnChargeCode> getRootBusinessChargeCodeBySubBusinessId(Integer businessId);
	
	public List<PnChargeCode> getChargeCodeByProjectId(Integer projectId);
	
	public PnChargeCode getChargeCodeAppliedOnPersonInSpace(Integer personId, Integer spaceId);
	
	public PnChargeCode getChargeCodeApliedOnTask(Integer taskId, Integer spaceId);
	
}
