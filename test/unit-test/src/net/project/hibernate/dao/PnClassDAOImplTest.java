package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnClassDAO;
import net.project.hibernate.model.PnClass;

public class PnClassDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnClassDAO pnClassDAO;
	
	public PnClassDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnClassDAO.getFormNamesForSpace(String)
	 */
	public void testGetFormNamesForSpace(){
		String spaceId = "477997";
		try {
			List<PnClass> list = pnClassDAO.getFormNamesForSpace(spaceId);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnClassDAO.getFormWithRecordStatus(Integer)
	 */
	public void testGetFormWithRecordStatus(){
		Integer classId = 486733;
		try {
			PnClass pnClass = pnClassDAO.getFormWithRecordStatus(classId);
			assertNotNull(pnClass);
			assertEquals("A", pnClass.getRecordStatus());
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
