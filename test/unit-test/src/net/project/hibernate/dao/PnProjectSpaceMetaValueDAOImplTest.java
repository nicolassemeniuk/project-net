package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnProjectSpaceMetaValueDAO;
import net.project.hibernate.model.PnProjectSpaceMetaCombo;
import net.project.hibernate.model.PnProjectSpaceMetaValue;

public class PnProjectSpaceMetaValueDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnProjectSpaceMetaValueDAO pnProjectSpaceMetaValueDAO;
	
	public PnProjectSpaceMetaValueDAOImplTest() {
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnProjectSpaceMetaValueDAO.getMetaValuesByProjectId(Integer)
	 */
	public void testGetMetaValuesByProjectId(){
		try {
			Integer projectId = 477997;
			List<PnProjectSpaceMetaValue> list = pnProjectSpaceMetaValueDAO.getMetaValuesByProjectId(projectId);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnProjectSpaceMetaValueDAO.getValuesOptionListForProperty(Integer)
	 */
	public void testGetValuesOptionListForProperty() {
		Integer propertyId = 6;
		try {
			List<PnProjectSpaceMetaCombo> list = pnProjectSpaceMetaValueDAO.getValuesOptionListForProperty(propertyId);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
