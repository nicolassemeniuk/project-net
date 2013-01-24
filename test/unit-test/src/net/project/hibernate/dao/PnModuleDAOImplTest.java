package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnModuleDAO;
import net.project.hibernate.model.PnModule;

public class PnModuleDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnModuleDAO pnModuleDAO;
	
	public PnModuleDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnModuleDAO.getModuleDefaultPermissions(Integer)
	 */
	public void testGetModuleDefaultPermissions(){
		Integer spaceId = 477997;
		try {
			List<PnModule> list = pnModuleDAO.getModuleDefaultPermissions(spaceId);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnModuleDAO.getModuleIds()
	 */
	public void testGetModuleIds(){
		Integer spaceId = 477997;
		try {
			List<PnModule> list = pnModuleDAO.getModuleIds();
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnModuleDAO.getModulesForSpace(Integer)
	 */
	public void testGetModulesForSpace(){
		Integer spaceId = 477997;
		try {
			List<PnModule> list = pnModuleDAO.getModulesForSpace(spaceId);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
