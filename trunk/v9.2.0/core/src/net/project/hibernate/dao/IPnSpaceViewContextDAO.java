/**
 * 
 */
package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.model.PnSpaceViewContext;
import net.project.hibernate.model.PnSpaceViewContextPK;

/**
 * Interface for accessing PnSpaceViewContext database object
 * 
 * @author Ritesh S
 *
 */
public interface IPnSpaceViewContextDAO extends IDAO<PnSpaceViewContext, PnSpaceViewContextPK>{
	
	/**
	 * Get shared views by spaceId which dosen't contains views created by current user
	 * @param spaceId
	 * @return
	 */
    public List<PnSpaceViewContext> getSharedViewByPerson(Integer spaceId);

}
