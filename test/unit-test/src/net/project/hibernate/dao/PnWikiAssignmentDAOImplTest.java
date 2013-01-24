package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnWikiAssignmentDAO;
import net.project.hibernate.model.PnWikiAssignment;

public class PnWikiAssignmentDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnWikiAssignmentDAO pnWikiAssignmentDAO;
	
	public PnWikiAssignmentDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWikiAssignmentDAO.getWikiAssignmentByObjectId(Integer)
	 */
	public void testGetWikiAssignmentByObjectId(){
		Integer objectId = 414475;
		try {
			PnWikiAssignment pnWikiAssignment = pnWikiAssignmentDAO.getWikiAssignmentByObjectId(objectId);
			assertNotNull(pnWikiAssignment);
			assertEquals(1045020, pnWikiAssignment.getWikiPageId().intValue());
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
