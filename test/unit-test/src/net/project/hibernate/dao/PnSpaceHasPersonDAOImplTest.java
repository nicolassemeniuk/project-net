package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnSpaceHasPersonDAO;
import net.project.hibernate.model.PnSpaceHasPerson;

public class PnSpaceHasPersonDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnSpaceHasPersonDAO pnSpaceHasPersonDAO;
	
	public PnSpaceHasPersonDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnSpaceHasPersonDAO.getPnSpaceHasPersonBySecureKey(String)
	 */
	public void testGetMetaValuesByProjectId(){
		String secureKey = "3F10564319E769BC50C439D437743F03";
		try {
			PnSpaceHasPerson pnSpaceHasPerson = pnSpaceHasPersonDAO.getPnSpaceHasPersonBySecureKey(secureKey);
			assertNotNull(pnSpaceHasPerson);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnSpaceHasPersonDAO.getSpaceHasPersonByProjectandPerson(Integer[], Integer)
	 */
	public void testGetSpaceHasPersonByProjectandPerson(){
		Integer spaceIds[] = {477997};
		Integer personId = 497434;
		try {
			List<PnSpaceHasPerson> list = pnSpaceHasPersonDAO.getSpaceHasPersonByProjectandPerson(spaceIds, personId);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
