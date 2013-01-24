package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnDocContainerListViewDAO;
import net.project.hibernate.model.PnDocContainerListView;

public class PnDocContainerListViewDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnDocContainerListViewDAO docContainerListViewDAO;
	
	public PnDocContainerListViewDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnDocContainerListViewDAO.getAllContainersDocument()
	 */
	public void testGetAllContainersDocument() {
		try {
			String spaceID = "477997";
			List<PnDocContainerListView> list = docContainerListViewDAO.getAllContainersDocument(spaceID);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
