package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnSpaceHasDocProviderDAO;
import net.project.hibernate.model.PnSpaceHasDocProvider;

public class PnSpaceHasDocProviderDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnSpaceHasDocProviderDAO pnSpaceHasDocProviderDAO;
	
	public PnSpaceHasDocProviderDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnSpaceHasDocProviderDAO.findDefaultSpaceHasDocProviderBySpace(Integer)
	 */
	public void testFindDefaultSpaceHasDocProviderBySpace(){
		Integer spaceId = 412323;
		try {
			PnSpaceHasDocProvider pnSpaceHasDocProvider = pnSpaceHasDocProviderDAO.findDefaultSpaceHasDocProviderBySpace(spaceId);
			assertNotNull(pnSpaceHasDocProvider);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
