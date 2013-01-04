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
import net.project.hibernate.dao.IPnActivityLogMarkedDAO;
import net.project.hibernate.dao.impl.PnActivityLogMarkedDAOImpl;
import net.project.hibernate.model.PnActivityLog;
import net.project.hibernate.model.PnActivityLogMarked;
import net.project.hibernate.model.PnActivityLogMarkedPK;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.model.resource_reports.ReportProjectUsers;
import net.project.security.SessionManager;
import net.project.test.util.TestProperties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Query;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.testng.annotations.BeforeMethod;

public class PnActivityLogMarkedDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnActivityLogMarkedDAO pnActivityLogMarkedDAO;
	
	public PnActivityLogMarkedDAOImplTest() {
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnActivityLogMarkedDAO#getMarkedByPersonId(java.lang.Integer, java.util.Date, java.util.Date, java.util.List)
	 */
	public void testGetMarkedByPersonId() throws Exception {
		Integer personId = 497434;
		Date startDate = createDate(2009, 10, 27);
		Date endDate = createDate(2009, 10, 25);
		List activityIds = new ArrayList();
		activityIds.add(46932);
		activityIds.add(12228);
		
		try {
			String activities = pnActivityLogMarkedDAO.getMarkedByPersonId(personId, startDate, endDate, activityIds);
			assertNotNull(activities);
			assertEquals(activities, "46932,");
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnActivityLogMarkedDAO#deleteByActivityIds(java.util.List)
	 */
	public void testDeleteByActivityIds(List activityIds) {
		try {
			PnActivityLogMarked pnActivityLogMarked = new PnActivityLogMarked(new PnActivityLogMarkedPK(new Integer(46931), new Integer(497434)), 1);
			pnActivityLogMarkedDAO.create(pnActivityLogMarked);
			
			pnActivityLogMarkedDAO.deleteByActivityIds(new ArrayList(46931));
			assertNotNull(pnActivityLogMarkedDAO.findByPimaryKey(new PnActivityLogMarkedPK(new Integer(46931), new Integer(497434))));
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
