/**
 * 
 */
package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.model.PnChargeCode;

/**
 * 
 * @author Ritesh S
 *
 */
public interface IPnChargeCodeDAO extends IDAO<PnChargeCode, Integer>{

	public List<PnChargeCode> getChargeCodeByBusinessId(Integer businessId);

	public List<PnChargeCode> getRootBusinessChargeCodeBySubBusinessId(Integer businessId);
	
	public List<PnChargeCode> getChargeCodeByProjectId(Integer projectId);
	
	public PnChargeCode getChargeCodeAppliedOnPersonInSpace(Integer personId, Integer spaceId);
	
	public PnChargeCode getChargeCodeApliedOnTask(Integer taskId, Integer spaceId);

}
