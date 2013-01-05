package net.project.hibernate.dao;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnProjectSpaceMetaPropDAO;
import net.project.hibernate.model.PnProjectSpaceMetaProp;

public class PnProjectSpaceMetaPropDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnProjectSpaceMetaPropDAO pnProjectSpaceMetaPropDAO;
	
	public PnProjectSpaceMetaPropDAOImplTest() {
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for
	 * @see net.project.hibernate.dao.IPnProjectSpaceMetaPropDAO#getProjectSpaceMetaPropByName(String)
	 */
	public void testGetProjectSpaceMetaPropByName() {
		String propertyName = "ProjectManager";
		try {
			PnProjectSpaceMetaProp pnProjectSpaceMetaProp = pnProjectSpaceMetaPropDAO.getProjectSpaceMetaPropByName(propertyName);
			assertNotNull(pnProjectSpaceMetaProp);
		} catch (Exception e) {
			assertTrue(false);
		}
	}
}
