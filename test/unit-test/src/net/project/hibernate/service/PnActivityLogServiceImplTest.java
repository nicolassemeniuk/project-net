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
import net.project.hibernate.dao.IPnAssignmentDAO;
import net.project.hibernate.dao.IPnPersonDAO;
import net.project.hibernate.model.PnActivityLog;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.service.impl.PnActivityLogServiceImpl;
import net.project.hibernate.service.impl.PnAssignmentServiceImpl;
import net.project.hibernate.service.impl.PnPersonServiceImpl;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PnActivityLogServiceImplTest extends TestCase{

    private PnActivityLogServiceImpl service;
    
    private IPnActivityLogDAO mockActivityDao;
    
	public PnActivityLogServiceImplTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		mockActivityDao = createMock(IPnActivityLogDAO.class);
		service = new PnActivityLogServiceImpl();
		service.setPnActivityLogDAO(mockActivityDao);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	/*
	 * Test method for 
	 * 'net.project.hibernate.service.impl.PnActivityLogServiceImpl.getActivityLogBySpaceIdAndDate(Integer, Date, Date, String, Integer, Integer, Integer, Integer)'
	 */
	public final void testGetActivityLogBySpaceIdAndDate() {
		Integer spaceId = 972052;
		Integer personId = null;
		Date startDate = createDate(2009, 10, 27);
		Date endDate = createDate(2009, 10, 25);
		Integer offSet = 0;
		Integer range = 10;
		Integer currentUserId = 497434;
		String criteria = "edited";
		List<PnActivityLog> activities = new ArrayList<PnActivityLog>();
		expect(mockActivityDao.getActivityLogBySpaceIdAndDate(spaceId, startDate, endDate, criteria, personId, offSet, range, currentUserId)).andReturn(activities);
		replay(mockActivityDao);
		activities =  service.getActivityLogBySpaceIdAndDate(spaceId, startDate, endDate, criteria, personId, offSet, range, currentUserId);
		verify(mockActivityDao);
	}
	
	/*
	 * Test method for 
	 * 'net.project.hibernate.service.impl.PnActivityLogServiceImpl.getActivityIdsOfBlogHavingComment(List)'
	 */
	public final void testGetActivityIdsOfBlogHavingComment() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(46926);
		String result = StringUtils.EMPTY;
		expect(mockActivityDao.getActivityIdsOfBlogHavingComment(list)).andReturn(result);
		replay(mockActivityDao);
		result = service.getActivityIdsOfBlogHavingComment(list);
		verify(mockActivityDao);
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