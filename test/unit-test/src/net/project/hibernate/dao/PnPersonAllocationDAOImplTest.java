package net.project.hibernate.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnPersonAllocationDAO;
import net.project.hibernate.model.PnPersonAllocation;
import net.project.base.hibernate.TestBase;

public class PnPersonAllocationDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnPersonAllocationDAO pnPersonAllocationDAO;
	
	private TestBase testBase;
	
	public PnPersonAllocationDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnPersonAllocationDAO.getResourceAllocationEntryByPerson(Integer, Integer, Date, Date)
	 */
	public void testGetResourceAllocationEntryByPerson(){
		testBase = new TestBase();
		Integer businesId = null;
		Integer personId = 497434;
		Date startDate = testBase.createDate(2009,1,15);
		Date endDate = testBase.createDate(2009,6,15);
		try {
			List list = pnPersonAllocationDAO.getResourceAllocationEntryByPerson(businesId, personId, startDate, endDate);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnPersonAllocationDAO.getResourceAllocationEntryByProject(Integer, Integer, Date, Date)
	 */
	public void testGetResourceAllocationEntryByProject(){
		testBase = new TestBase();
		Integer businesId = 1;
		Integer projectId = 477997;
		Date startDate = testBase.createDate(2009,1,15);
		Date endDate = testBase.createDate(2009,6,15);
		try {
			List list = pnPersonAllocationDAO.getResourceAllocationEntryByProject(businesId, projectId, startDate, endDate);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnPersonAllocationDAO.getResourceAllocationSumary(Integer, Integer, Date, Date)
	 */
	public void testGetResourceAllocationSumary(){
		testBase = new TestBase();
		Integer businesId = null;
		Integer resourceId = 497434;
		Date startDate = testBase.createDate(2009,1,15);
		Date endDate = testBase.createDate(2009,6,15);
		try {
			List list = pnPersonAllocationDAO.getResourceAllocationSumary(resourceId, businesId, startDate, endDate);
			assertNotNull(list);
			assertTrue(list.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnPersonAllocationDAO.getResourceAllocationDetails(Integer, Integer, Date, Date)
	 */
	public void testGetResourceAllocationDetails(){
		testBase = new TestBase();
		Integer projectId = 477997;
		Integer resourceId = 497434;
		Date startDate = testBase.createDate(2009,1,15);
		Date endDate = testBase.createDate(2009,6,15);
		try {
			PnPersonAllocation pnPersonAllocation = pnPersonAllocationDAO.getResourceAllocationDetails(resourceId, projectId, startDate, endDate);
			assertNotNull(pnPersonAllocation);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
