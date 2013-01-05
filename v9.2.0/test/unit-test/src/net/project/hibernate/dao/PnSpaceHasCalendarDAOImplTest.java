package net.project.hibernate.dao;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnSpaceHasCalendarDAO;
import net.project.hibernate.model.PnSpaceHasCalendar;
import net.project.hibernate.model.PnSpaceHasCalendarPK;

public class PnSpaceHasCalendarDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnSpaceHasCalendarDAO pnSpaceHasCalendarDAO;
	
	public PnSpaceHasCalendarDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/*
	 * Test method for 'net.project.hibernate.dao.IPnSpaceHasCalendarDAO.findByPrimary(PnSpaceHasCalendarPK))'
	 */	
	public void testFindByPrimary(){
		try {
			PnSpaceHasCalendarPK pnSpaceHasCalendarPK = new PnSpaceHasCalendarPK();
			pnSpaceHasCalendarPK.setSpaceId(477997);
			pnSpaceHasCalendarPK.setCalendarId(478009);
			PnSpaceHasCalendar pnSpaceHasCalendar = pnSpaceHasCalendarDAO.findByPimaryKey(pnSpaceHasCalendarPK);
			assertNotNull(pnSpaceHasCalendar);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
