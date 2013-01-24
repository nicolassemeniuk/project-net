package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnDocContainerDAO;
import net.project.hibernate.model.PnDocContainer;

public class PnDocContainerDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnDocContainerDAO pnDocContainerDAO;
	
	public PnDocContainerDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnDocContainerDAO.getDocContainerWithRecordStatus(Integer)
	 */
	public void testGetDocContainerWithRecordStatus(){
		Integer docContainerId = 556061;
		try {
			PnDocContainer pnDocContainer = pnDocContainerDAO.getDocContainerWithRecordStatus(docContainerId);
			assertNotNull(pnDocContainer);
			assertEquals("A", pnDocContainer.getRecordStatus());
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnDocContainerDAO.getDocContainerWithIsHidden(Integer)
	 */
	public void testGetDocContainerWithIsHidden(){
		Integer documentId = 94151;
		try {
			PnDocContainer pnDocContainer = pnDocContainerDAO.getDocContainerWithIsHidden(documentId);
			assertNotNull(pnDocContainer);
			assertEquals(0, pnDocContainer.getIsHidden());
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
