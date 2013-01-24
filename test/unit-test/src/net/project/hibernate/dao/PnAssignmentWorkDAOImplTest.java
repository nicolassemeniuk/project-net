package net.project.hibernate.dao;

import java.util.Date;
import java.util.List;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnAssignmentWorkDAO;
import net.project.hibernate.model.PnAssignmentWork;
import net.project.base.hibernate.TestBase;

public class PnAssignmentWorkDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnAssignmentWorkDAO pnAssignmentWorkDAO;
	
	private TestBase testBase; 
	
	public PnAssignmentWorkDAOImplTest() {
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnAssignmentWorkDAO.getTotalWorkByDate(Integer[], Date, Date, Integer)
	 */
	public void testGetTotalWorkByDate() {
		testBase = new TestBase();
		Integer[] personIds = {497434}; 
		Date startDate = testBase.createDate(2009,1,18);
		Date endDate = testBase.createDate(2009,1,19);
		Integer spaceId = null;
		try {
			List<PnAssignmentWork> list = pnAssignmentWorkDAO.getTotalWorkByDate(personIds, startDate, endDate, spaceId);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnAssignmentWorkDAO.getWorkDetailsById(Integer)
	 */
	public void testGetWorkDetailsById() {
		Integer assignmentWorkId = 902209;
		try {
			PnAssignmentWork pnAssignmentWork = pnAssignmentWorkDAO.getWorkDetailsById(assignmentWorkId);
			assertNotNull(pnAssignmentWork);
			assertNotNull(pnAssignmentWork.getWork());
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
