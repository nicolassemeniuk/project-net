package net.project.hibernate.dao;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnNewsDAO;
import net.project.hibernate.model.PnNews;

public class PnNewsDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnNewsDAO pnNewsDAO;
	
	public PnNewsDAOImplTest() {
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnNewsDAO.getNewsWithRecordStatus(Integer)
	 */
	public void testGetNewsWithRecordStatus() throws Exception {
		try {
			BigDecimal newsId = BigDecimal.valueOf(1089398);
			PnNews pnNews = pnNewsDAO.getNewsWithRecordStatus(newsId);
			assertNotNull(pnNews);
			assertTrue(StringUtils.isNotEmpty(pnNews.getRecordStatus()));
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
