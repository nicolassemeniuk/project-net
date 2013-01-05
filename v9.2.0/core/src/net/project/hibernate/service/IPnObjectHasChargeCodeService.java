/**
 * 
 */
package net.project.hibernate.service;

import java.util.List;

import net.project.hibernate.model.PnChargeCode;
import net.project.hibernate.model.PnObjectHasChargeCode;

/**
 * @author  Ritesh S
 *
 */
public interface IPnObjectHasChargeCodeService {

	/**
	 * Persist id of charge code which is applied on this object
	 * if objectid and chargecodsId already exist it will be updated.
	 * @param objectId
	 * @param chargeCodeId
	 */
	public void save(Integer objectId, Integer chargeCodeId, Integer spaceId);
	
	/**
	 * Get List of PnObjectHasChargeCode 
	 * containing person id of members assigned a charge code in parent business
	 * @param spaceId
	 * @param childSpaceType
	 * @return
	 */
	public List<PnObjectHasChargeCode> getChargeCodeAssignedPersonFromParentBusiness(Integer spaceId, String childSpaceType);

	/**
	 * Get List of PnObjectHasChargeCode 
	 * containing person id of members assigned a charge code in parent project
	 * @param spaceId
	 * @return
	 */
	public List<PnObjectHasChargeCode> getChargeCodeAssignedPersonFromParentProject(Integer spaceId);
	
}
