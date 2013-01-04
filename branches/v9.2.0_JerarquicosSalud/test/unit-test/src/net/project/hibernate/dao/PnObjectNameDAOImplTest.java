package net.project.hibernate.dao;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnObjectNameDAO;

public class PnObjectNameDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnObjectNameDAO pnObjectNameDAO;
	
	public PnObjectNameDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnObjectNameDAO.getNameForObject()
	 */
	public void testGetNameForObject() throws Exception {
		try{
			Integer objectId = 47950;
			String objectName = pnObjectNameDAO.getNameFofObject(objectId);
			assertTrue(StringUtils.isNotEmpty(objectName));
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
