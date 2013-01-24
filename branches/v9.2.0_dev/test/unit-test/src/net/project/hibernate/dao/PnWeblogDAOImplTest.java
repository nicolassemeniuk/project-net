package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnWeblogDAO;
import net.project.hibernate.model.PnWeblog;

public class PnWeblogDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnWeblogDAO pnWeblogDAO;
	
	public PnWeblogDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogDAO.getByUserAndSpaceId(Integer, Integer)
	 */
	public void testGetByUserAndSpaceId(){
		Integer userId = 497434;
		Integer spaceId = 477997;
		try {
			PnWeblog pnWeblog = pnWeblogDAO.getByUserAndSpaceId(userId, spaceId);
			assertNotNull(pnWeblog);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogDAO.getBySpaceId(Integer, boolean)
	 */
	public void testGetBySpaceId(){
		Integer spaceId = 477997;
		boolean initializePersonObject = false;
		try {
			PnWeblog pnWeblog = pnWeblogDAO.getBySpaceId(spaceId, initializePersonObject);
			assertNotNull(pnWeblog);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogDAO.getBySpaceId(Integer)
	 */
	public void testGetBySpaceIdWithPersonEntity(){
		Integer spaceId = 477997;
		try {
			PnWeblog pnWeblog = pnWeblogDAO.getBySpaceId(spaceId);
			assertNotNull(pnWeblog);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogDAO.getPnWeblogById(int)
	 */
	public void testGetPnWeblogById(){
		int weblogId = 1200007;
		try {
			PnWeblog pnWeblog = pnWeblogDAO.getPnWeblogById(weblogId);
			assertNotNull(pnWeblog);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
