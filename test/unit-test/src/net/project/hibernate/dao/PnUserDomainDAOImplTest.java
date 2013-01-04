package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnUserDomainDAO;
import net.project.hibernate.model.PnUserDomain;

public class PnUserDomainDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnUserDomainDAO pnUserDomainDAO;
	
	public PnUserDomainDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnUserDomainDAO.getUserDomain(Integer)
	 */
	public void testGetMetaValuesByProjectId(){
		Integer userDomainId = 1000;
		try {
			PnUserDomain pnUserDomain = pnUserDomainDAO.getUserDomain(userDomainId);
			assertNotNull(pnUserDomain);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
