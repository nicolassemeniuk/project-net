package net.project.hibernate.dao;

import static org.easymock.EasyMock.createStrictMock;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.io.File;

import javax.sql.DataSource;

import junit.framework.TestCase;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnActivityLogDAO;
import net.project.hibernate.model.PnActivityLog;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.model.resource_reports.ReportProjectUsers;
import net.project.test.util.TestProperties;

import org.apache.commons.lang.StringUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.testng.annotations.BeforeMethod;

public class PnActivityLogDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnActivityLogDAO pnActivityLogDAO;
	
	public PnActivityLogDAOImplTest() {
		setPopulateProtectedVariables(true);
	}	 
	
	/* Test method for 
	 * @see net.project.hibernate.service.impl.PnActivityLogServiceImpl.getActivityLogBySpaceIdAndDate(Integer, Date, Date, String, Integer, Integer, Integer, Integer)
	 */
	public void testGetActivityLogBySpaceIdAndDate() throws Exception {
		Integer spaceId = 972052;
		Integer personId = null;
		Date startDate = createDate(2009, 10, 27);
		Date endDate = createDate(2009, 10, 25);
		Integer offSet = 0;
		Integer range = 10;
		Integer currentUserId = 497434;
		String criteria = "";
		try {
			List<PnActivityLog> activitys = pnActivityLogDAO.getActivityLogBySpaceIdAndDate(spaceId, 
										startDate, endDate, criteria, personId, offSet, range, currentUserId);
			assertNotNull(activitys);
			assertTrue(activitys.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
		
	}
	
	/* Test method for 
	 * @see net.project.hibernate.service.impl.PnActivityLogServiceImpl.getActivityIdsOfBlogHavingComment(List)
	 */
	public void testGetActivityIdsOfBlogHavingComment() {
		List<Integer> activityIds = new ArrayList<Integer>();
		activityIds.add(46926);
		try {
			String result = pnActivityLogDAO.getActivityIdsOfBlogHavingComment(activityIds);
			assertNotNull(result);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/**
	 * Create date 
	 * 
	 * @param year  - date year
	 * @param month - date month
	 * @param day   - date day
	 * @return      - specified darte
	 */
	private Date createDate(int year, int month, int day){
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		return cal.getTime();
	}
}
