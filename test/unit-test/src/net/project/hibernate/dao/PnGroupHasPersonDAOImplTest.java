package net.project.hibernate.dao;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnGroupHasPersonDAO;
import net.project.hibernate.model.PnGroupHasPerson;
import net.project.hibernate.model.PnGroupHasPersonPK;

public class PnGroupHasPersonDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnGroupHasPersonDAO pnGroupHasPersonDAO;
	
	public PnGroupHasPersonDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/*
	 * Test method for 'net.project.hibernate.dao.IPnGroupHasPersonDAO.findByPrimary(PnGroupHasPersonPK))'
	 */	
	public void testFindByPrimary(){
		try {
			PnGroupHasPersonPK pnGroupHasPersonPK = new PnGroupHasPersonPK();
			pnGroupHasPersonPK.setGroupId(584277);
			pnGroupHasPersonPK.setPersonId(497434);
			PnGroupHasPerson pnGroupHasPerson = pnGroupHasPersonDAO.findByPimaryKey(pnGroupHasPersonPK);
			assertNotNull(pnGroupHasPerson);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
