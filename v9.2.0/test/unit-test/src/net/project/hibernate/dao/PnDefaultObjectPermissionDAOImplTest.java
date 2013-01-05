package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.base.ObjectType;
import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnDefaultObjectPermissionDAO;
import net.project.hibernate.model.PnDefaultObjectPermission;

public class PnDefaultObjectPermissionDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnDefaultObjectPermissionDAO pnDefaultObjectPermissionDAO;
	
	public PnDefaultObjectPermissionDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnDefaultObjectPermissionDAO.getDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup(Integer, String)
	 */
	public void testGetDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup(){
		Integer spaceId = 477997;
		String objectType = ObjectType.TASK;
		try {
			List<PnDefaultObjectPermission> list = pnDefaultObjectPermissionDAO.getDefaultObjectPermisionsBySpaceAndObjectTypeForNonPrincipalGroup(spaceId, objectType);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
