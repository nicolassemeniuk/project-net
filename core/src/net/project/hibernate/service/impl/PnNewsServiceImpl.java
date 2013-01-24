/**
 * 
 */
package net.project.hibernate.service.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.project.hibernate.dao.IPnNewsDAO;
import net.project.hibernate.model.PnNews;
import net.project.hibernate.service.IPnNewsService;

/**
 * @author 
 *
 */
@Service(value="pnNewsService")
public class PnNewsServiceImpl implements IPnNewsService{
	
	@Autowired
	private IPnNewsDAO pnNewsDAO;
	
	/**
	 * @param pnNewsDAO
	 */
	public void setPnNewsDAO(IPnNewsDAO pnNewsDAO) {
		this.pnNewsDAO = pnNewsDAO;
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.IPnNewsService#getNewsWithRecordStatus(java.math.BigDecimal)
	 */
	public PnNews getNewsWithRecordStatus(BigDecimal newsId){
		return pnNewsDAO.getNewsWithRecordStatus(newsId);
	}

}
