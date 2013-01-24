package net.project.hibernate.dao;

import java.util.List;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnObjectPermissionDAO;
import net.project.hibernate.model.PnObjectPermission;

public class PnObjectPermissionDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnObjectPermissionDAO pnObjectPermissionDAO;
	
	public PnObjectPermissionDAOImplTest() {
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnObjectPermissionDAO.getObjectPermissionsForGroup()
	 */
	public void testGetObjectPermissionsForGroup() {
		Integer groupId = 7304;
		try {
			List<PnObjectPermission> list = pnObjectPermissionDAO.getObjectPermissionsForGroup(groupId);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnObjectPermissionDAO.getObjectPermissionsForObject()
	 */
	public void testGetObjectPermissionsForObject() {
		Integer objectId = 71914;
		try {
			List<PnObjectPermission> list = pnObjectPermissionDAO.getObjectPermissionsForObject(objectId);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
