package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnDirectoryDAO;
import net.project.hibernate.model.PnDirectory;

public class PnDirectoryDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnDirectoryDAO pnDirectoryDAO;
	
	public PnDirectoryDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnDirectoryDAO.getDefaultDirectory()
	 */
	public void testGetDefaultDirectory() {
		try{
			List<PnDirectory> list = pnDirectoryDAO.getDefaultDirectory();
			assertNotNull(list);
			assertTrue(list.size() > 0);
		}catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
