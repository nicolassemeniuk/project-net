/**
 * 
 */
package net.project.hibernate.dao;

import java.math.BigDecimal;

import net.project.hibernate.model.PnNews;

/**
 * @author 
 *
 */
public interface IPnNewsDAO extends IDAO<PnNews, BigDecimal>{
	
	 /**
	 * Get news with topic and record status. 
	 * @param java.lang.BigDecimal
	 * @return Object of PnNews
	 */
	public PnNews getNewsWithRecordStatus(BigDecimal newsId);
}
