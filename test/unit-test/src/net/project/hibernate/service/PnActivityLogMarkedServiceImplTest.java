package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;
import net.project.hibernate.dao.IPnActivityLogDAO;
import net.project.hibernate.dao.IPnActivityLogMarkedDAO;
import net.project.hibernate.dao.IPnAssignmentDAO;
import net.project.hibernate.dao.IPnPersonDAO;
import net.project.hibernate.model.PnActivityLog;
import net.project.hibernate.model.PnActivityLogMarked;
import net.project.hibernate.model.PnActivityLogMarkedPK;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.service.impl.PnActivityLogMarkedServiceImpl;
import net.project.hibernate.service.impl.PnActivityLogServiceImpl;
import net.project.hibernate.service.impl.PnAssignmentServiceImpl;
import net.project.hibernate.service.impl.PnPersonServiceImpl;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PnActivityLogMarkedServiceImplTest extends TestCase{

    private PnActivityLogMarkedServiceImpl service;
    
    private IPnActivityLogMarkedDAO mockActivityMarkedDao;
    
	public PnActivityLogMarkedServiceImplTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		mockActivityMarkedDao = createMock(IPnActivityLogMarkedDAO.class);
		service = new PnActivityLogMarkedServiceImpl();
		service.setPnActivityLogMarkedDAO(mockActivityMarkedDao);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	/*
	 * Test method for 
	 * 'net.project.hibernate.service.impl.PnActivityLogServiceImpl.getActivityLogBySpaceIdAndDate(Integer, Date, Date, String, Integer, Integer, Integer, Integer)'
	 */
	public void testGetMarkedByPersonId() throws Exception {
			Integer personId = 497434;
			Date startDate = createDate(2009, 10, 27);
			Date endDate = createDate(2009, 10, 25);
			List activityIds = new ArrayList();
			activityIds.add(46932);
			activityIds.add(12228);
		String activityIdsToCompare = "46932,";
		expect(mockActivityMarkedDao.getMarkedByPersonId(personId, startDate, endDate, activityIds)).andReturn(activityIdsToCompare);
		replay(mockActivityMarkedDao);
		String activities =  service.getMarkedByPersonId(personId, startDate, endDate, activityIds);
		verify(mockActivityMarkedDao);
	}
		
	/**
	 * Create date 
	 * 
	 * @param year  - date year
	 * @param month - date month
	 * @param day   - date day
	 * @return      - specified date
	 */
	private Date createDate(int year, int month, int day){
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		return cal.getTime();
	}
}