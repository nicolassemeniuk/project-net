package net.project.hibernate.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnPersonPropertyDAO;
import net.project.hibernate.model.PnPersonProperty;

public class PnPersonPropertyDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnPersonPropertyDAO pnPersonPropertyDAO;
	
	public PnPersonPropertyDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnPersonPropertyDAO.getPersonProperties(Integer, Integer, String)
	 */
	public void testGetPersonProperties(){
		Integer spaceId = 477997; 
		Integer personId = 497434; 
		String property = "node693562expanded";
		try {
			List<PnPersonProperty> list = pnPersonPropertyDAO.getPersonProperties(spaceId, personId, property);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
