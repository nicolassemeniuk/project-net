package net.project.hibernate.dao;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnBusinessSpaceDAO;
import net.project.hibernate.model.PnBusiness;
import net.project.hibernate.model.PnBusinessSpace;

public class PnBusinessSpaceDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnBusinessSpaceDAO pnBusinessSpaceDAO;
	
	public PnBusinessSpaceDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnBusinessSpaceDAO.getBusinessByProjectId(Integer)
	 */
	public void testGetBusinessByProjectId() throws Exception {
		try {
			Integer projectId = 477997;
			PnBusiness pnBusiness = pnBusinessSpaceDAO.getBusinessByProjectId(projectId);
			assertNotNull(pnBusiness);
			assertNotNull(pnBusiness.getBusinessId());
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnBusinessSpaceDAO.getBusinessSpaceById(Integer)
	 */
	public void testGetBusinessSpaceById() throws Exception {
		try {
			Integer businessId = 434709;
			PnBusinessSpace pnBusinessSpace = pnBusinessSpaceDAO.getBusinessSpaceById(businessId);
			assertNotNull(pnBusinessSpace);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
