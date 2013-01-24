package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnDocProviderDAO;
import net.project.hibernate.model.PnDocProvider;

public class PnDocProviderDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnDocProviderDAO pnDocProviderDAO;
	
	public PnDocProviderDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnDocProviderDAO.getDocProviderIds()
	 */
	public void testGetDocProviderIds() {
		try {
			List<PnDocProvider> list = pnDocProviderDAO.getDocProviderIds();
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
