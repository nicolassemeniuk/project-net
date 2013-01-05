package net.project.hibernate.dao;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnDirectoryHasPersonDAO;
import net.project.hibernate.model.PnDirectoryHasPerson;
import net.project.hibernate.model.PnDirectoryHasPersonPK;

public class PnDirectoryHasPersonDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnDirectoryHasPersonDAO pnDirectoryHasPersonDAO;
	
	public PnDirectoryHasPersonDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/*
	 * Test method for 'net.project.hibernate.dao.IPnDirectoryHasPersonDAO.findByPrimary(PnDirectoryHasPersonPK)'
	 */	
	public void testFindByPrimaryKey(){
		Integer directoryId = 100;
		Integer personId = 1;
		try {
			PnDirectoryHasPersonPK pnDirectoryHasPersonPK = new PnDirectoryHasPersonPK();
			pnDirectoryHasPersonPK.setDirectoryId(directoryId);
			pnDirectoryHasPersonPK.setPersonId(personId);
			PnDirectoryHasPerson pnDirectoryHasPerson = pnDirectoryHasPersonDAO.findByPimaryKey(pnDirectoryHasPersonPK);
			assertNotNull(pnDirectoryHasPerson);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
