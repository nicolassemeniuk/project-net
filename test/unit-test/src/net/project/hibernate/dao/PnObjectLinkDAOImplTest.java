package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnObjectLinkDAO;
import net.project.hibernate.model.PnObjectLink;

public class PnObjectLinkDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnObjectLinkDAO pnObjectLinkDAO;
	
	public PnObjectLinkDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnObjectLinkDAO.getObjectsById(Integer, Integer)
	 */
	public void testGetObjectsById(){
		Integer parentId = 5488;
		Integer contextId = 10;
		try {
			List<PnObjectLink> list = pnObjectLinkDAO.getObjectsById(parentId, contextId);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnObjectLinkDAO.getObjectLinksByParent(Integer)
	 */
	public void testGetObjectLinksByParent(){
		Integer fromObjectId = 5488;
		try {
			List<PnObjectLink> list = pnObjectLinkDAO.getObjectLinksByParent(fromObjectId);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnObjectLinkDAO.getObjectLinksByChild(Integer)
	 */
	public void testGetObjectLinksByChild(){
		Integer toObjectId = 5214;
		try {
			List<PnObjectLink> list = pnObjectLinkDAO.getObjectLinksByChild(toObjectId);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
