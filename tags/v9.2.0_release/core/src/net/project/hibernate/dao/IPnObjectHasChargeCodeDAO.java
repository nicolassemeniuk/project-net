/**
 * 
 */
package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.model.PnChargeCode;
import net.project.hibernate.model.PnObjectHasChargeCode;
import net.project.hibernate.model.PnObjectHasChargeCodePK;

/**
 * Interface for accessing PnObjectHasChargeCode database object
 * 
 * @author  Ritesh S
 *
 */
public interface IPnObjectHasChargeCodeDAO extends IDAO<PnObjectHasChargeCode, PnObjectHasChargeCodePK>{

	public List<PnObjectHasChargeCode> getChargeCodeAssignedPersonFromParentBusiness(Integer spaceId, String childSpaceType);
	
	public List<PnObjectHasChargeCode> getChargeCodeAssignedPersonFromParentProject(Integer spaceId);
	
}
