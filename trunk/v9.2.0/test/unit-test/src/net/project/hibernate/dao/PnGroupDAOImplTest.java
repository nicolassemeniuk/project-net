package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnGroupDAO;
import net.project.hibernate.model.PnGroup;

public class PnGroupDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnGroupDAO pnGroupDAO;
	
	public PnGroupDAOImplTest() {
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.impl.IPnGroupDAO.getPrincipalGroupForSpaceAndPerson(Integer, Integer)
	 */
	public void testGetPrincipalGroupForSpaceAndPerson() throws Exception {
		try{
			Integer spaceId = 477997;
			Integer personId = 460196;
			PnGroup pnGroup = pnGroupDAO.getPrincipalGroupForSpaceAndPerson(spaceId, personId);
			assertNotNull(pnGroup);
			assertNotNull(pnGroup.getGroupId().intValue());
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
