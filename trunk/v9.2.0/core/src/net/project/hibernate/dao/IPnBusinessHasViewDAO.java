/**
 * 
 */
package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.model.PnBusinessHasView;
import net.project.hibernate.model.PnBusinessHasViewPK;

/**
 * Interface for accessing PnSpaceViewContext database object
 * 
 * @author Ritesh S
 *
 */
public interface IPnBusinessHasViewDAO extends IDAO<PnBusinessHasView, PnBusinessHasViewPK>{

	public List<PnBusinessHasView> getSharedViewByBusiness(Integer businessId);

	public List<PnBusinessHasView> getBusinessByView(Integer viewId);

}