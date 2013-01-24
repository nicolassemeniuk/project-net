package net.project.hibernate.dao;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnCalendarDAO;
import net.project.hibernate.model.PnCalendar;

public class PnCalendarDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnCalendarDAO pnCalendarDAO;
	
	public PnCalendarDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/*
	 * Test method for 'net.project.hibernate.dao.IPnCalendarDAO.findByPrimary(Integer)'
	 */	
	public void testFindByPrimaryKey(){
		try {
			Integer calendarId = 333044;
			PnCalendar pnCalendar = pnCalendarDAO.findByPimaryKey(calendarId);
			assertNotNull(pnCalendar);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
