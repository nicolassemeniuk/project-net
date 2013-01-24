package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.base.ObjectType;
import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnObjectTypeDAO;
import net.project.hibernate.model.PnObjectType;

public class PnObjectTypeDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnObjectTypeDAO pnObjectTypeDAO;
	
	public PnObjectTypeDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	

	/* Test method for 
	 * @see net.project.hibernate.dao.IPnObjectTypeDAO.findObjectTypes()
	 */
	public void testFindObjectTypes(){
		Integer objectId = 114414;
		try {
			List<PnObjectType> list = pnObjectTypeDAO.findObjectTypes();
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnObjectTypeDAO.getObjectTypeByObjectId(Integer)
	 */
	public void testGetObjectTypeByObjectId(){
		Integer objectId = 114414;
		try {
			PnObjectType pnObjectType = pnObjectTypeDAO.getObjectTypeByObjectId(objectId);
			assertNotNull(pnObjectType);
			assertEquals(ObjectType.TASK, pnObjectType.getObjectType());
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
