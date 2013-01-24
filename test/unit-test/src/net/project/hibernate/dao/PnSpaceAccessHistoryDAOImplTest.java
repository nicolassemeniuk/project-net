package net.project.hibernate.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnSpaceAccessHistoryDAO;

public class PnSpaceAccessHistoryDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnSpaceAccessHistoryDAO pnSpaceAccessHistoryDAO;
	
	public PnSpaceAccessHistoryDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnSpaceAccessHistoryDAO.getSpaceHistory(Integer, Integer)
	 */
	public void testGetSpaceHistory(){
		Integer spaceId = 477997;
		Integer userId = 497434;
		try {
			Date accessDate = pnSpaceAccessHistoryDAO.getSpaceHistory(spaceId, userId);
			assertNotNull(accessDate);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
