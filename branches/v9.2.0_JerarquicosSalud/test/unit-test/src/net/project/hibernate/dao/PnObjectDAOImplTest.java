package net.project.hibernate.dao;

import net.project.base.ObjectType;
import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnObjectDAO;
import net.project.hibernate.model.PnObject;

public class PnObjectDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnObjectDAO pnObjectDAO;
	
	public PnObjectDAOImplTest() {
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnObjectDAO.generateNewId()
	 */
	public void testGenerateNewId() throws Exception {
		try{
			Integer objectId = pnObjectDAO.generateNewId();
			assertNotNull(objectId);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnObjectDAO.getObjectByObjectId(Integer)
	 */
	public void testGetObjectByObjectId() throws Exception {
		try {
			Integer objectId = 47950;
			PnObject pnObject = pnObjectDAO.getObjectByObjectId(objectId);
			assertNotNull(pnObject);
			assertEquals(ObjectType.TASK, pnObject.getPnObjectType().getObjectType());
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
