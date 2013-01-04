package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnWeblogEntryAttributeDAO;
import net.project.hibernate.model.PnWeblogEntryAttribute;

public class PnWeblogEntryAttributeDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnWeblogEntryAttributeDAO pnWeblogEntryAttributeDAO;
	
	public PnWeblogEntryAttributeDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogEntryAttributeDAO.getTaskIdsFromTaskBlogEntries(Integer)
	 */
	public void testGetTaskIdsFromTaskBlogEntries(){
		Integer spaceId = 477997;
		try {
			List list = pnWeblogEntryAttributeDAO.getTaskIdsFromTaskBlogEntries(spaceId);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogEntryAttributeDAO.getWeblogEntryAtribute(Integer, String)
	 */
	public void testGetWeblogEntryAtribute(){
		Integer weblogEntryId =  1201021;
		String name = "taskId";
		try {
			PnWeblogEntryAttribute pnWeblogEntryAttribute = pnWeblogEntryAttributeDAO.getWeblogEntryAtribute(weblogEntryId, name);
			assertNotNull(pnWeblogEntryAttribute);
			assertTrue(StringUtils.isNotEmpty(pnWeblogEntryAttribute.getValue()));
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogEntryAttributeDAO.getWeblogEntryAtributesByEntryId(Integer)
	 */
	public void testGetWeblogEntryAtributesByEntryId(){
		Integer weblogEntryId =  1201021;
		try {
			List<PnWeblogEntryAttribute> list = pnWeblogEntryAttributeDAO.getWeblogEntryAtributesByEntryId(weblogEntryId);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
