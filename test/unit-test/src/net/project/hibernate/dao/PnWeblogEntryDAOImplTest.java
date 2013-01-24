package net.project.hibernate.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.dao.IPnWeblogEntryDAO;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.base.hibernate.TestBase;

public class PnWeblogEntryDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnWeblogEntryDAO pnWeblogEntryDAO;
	
//	private TestBase testBase;
	
	public PnWeblogEntryDAOImplTest(){
		setPopulateProtectedVariables(true);
//		testBase = new TestBase();
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO.getWeblogEntry(Integer)
	 */
	public void testGetWeblogEntry(){
		Integer weblogEntryId = 1201628;
		try {
			PnWeblogEntry weblogEntry = pnWeblogEntryDAO.getWeblogEntry(weblogEntryId);
			assertNotNull(weblogEntry);
			assertTrue(StringUtils.isNotEmpty(weblogEntry.getAnchor()));
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO.getWeblogEntryDetail(Integer)
	 */
	public void testGetWeblogEntryDetail(){
		Integer weblogEntryId = 1201628;
		try {
			PnWeblogEntry weblogEntry = pnWeblogEntryDAO.getWeblogEntryDetail(weblogEntryId);
			assertNotNull(weblogEntry);
			assertTrue(StringUtils.isNotEmpty(weblogEntry.getAnchor()));
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO.getWeblogEntryWithSpaceId(Integer)
	 */
	public void testGetWeblogEntryWithSpaceId(){
		Integer weblogEntryId = 1201628;
		try {
			PnWeblogEntry weblogEntry = pnWeblogEntryDAO.getWeblogEntryWithSpaceId(weblogEntryId);
			assertNotNull(weblogEntry);
			assertTrue(StringUtils.isNotEmpty(weblogEntry.getPnWeblog().getSpaceId().toString()));
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO.getWeblogEntries(Integer, Integer, Date, Date, String, int, int)
	 */
	public void testGetWeblogEntries(){
		Integer weblogId = null;
		Integer userId = 497434;
		Date startDate = null;
		Date endDate = null;
		String status = WeblogConstants.STATUS_PUBLISHED;
		int offset = 0;
		int range = 20;
		try {
			List<PnWeblogEntry> weblogEntries = pnWeblogEntryDAO.getWeblogEntries(weblogId, userId, startDate, endDate, status, offset, range);
			assertNotNull(weblogEntries);
			assertTrue(weblogEntries.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO.getWeblogEntriesByTaskId(Integer)
	 */
	public void testGetWeblogEntriesByTaskId(){
		Integer taskId = 380513;
		try {
			List<PnWeblogEntry> weblogEntries = pnWeblogEntryDAO.getWeblogEntriesByTaskId(taskId);
			assertNotNull(weblogEntries);
			assertTrue(weblogEntries.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO.getWeblogEntriesByObjectId(String, String, Date, Date)
	 */
	public void testGetWeblogEntriesByObjectId(){
		String objectId = "380513";
		String status = WeblogConstants.STATUS_PUBLISHED;
		Date startDate = null; 
		Date endDate = null;
		try {
			List<PnWeblogEntry> weblogEntries = pnWeblogEntryDAO.getWeblogEntriesByObjectId(objectId, status, startDate, endDate);
			assertNotNull(weblogEntries);
			assertTrue(weblogEntries.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO.getFilteredWeblogEntries(Integer, Integer, Integer, Integer, Date, Date, String, int, int, String[], boolean, String, boolean)
	 */
	public void testGetFilteredWeblogEntries(){
		Integer weblogId = null; 
		Integer memberId = null; 
		Integer userId = 497434;
		Integer objectId = 477997;
		Date startDate = null; 
		Date endDate = null;
		String status = WeblogConstants.STATUS_PUBLISHED;
		int offset = 0;
		int range = 10; 
		String[] childNodes = null;
		boolean showTimeReportedEntries = false; 
		String itemType = StringUtils.EMPTY;
		boolean showImportantBlogEntries = false;
		Integer currentUserId = 94239;
		try {
			List<PnWeblogEntry> weblogEntries = pnWeblogEntryDAO.getFilteredWeblogEntries(weblogId, memberId, userId, objectId, startDate, endDate, status, offset, range, childNodes, showTimeReportedEntries, itemType, showImportantBlogEntries, currentUserId);
			assertNotNull(weblogEntries);
			assertTrue(weblogEntries.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(pnetEx.getMessage(),false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO.getLastBlogEntryOfUser(Integer)
	 */
	public void testGetLastBlogEntryOfUser(){
		Integer userId = 497434;
		Integer currentUserId = 94239;
		try {
			PnWeblogEntry weblogEntry = pnWeblogEntryDAO.getLastBlogEntryOfUser(userId, currentUserId);
			assertNotNull(weblogEntry);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO.getLastBlogEntryOfUserByProject(Integer, Integer)
	 */
	public void testGetLastBlogEntryOfUserByProject(){
		Integer userId = 497434;
		Integer projectId = 477997;
		try {
			PnWeblogEntry weblogEntry = pnWeblogEntryDAO.getLastBlogEntryOfUserByProject(userId, projectId);
			assertNotNull(weblogEntry);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO.getLastBlogEntiesOfAllUsersBySpace(Integer)
	 */
	public void testGetLastBlogEntiesOfAllUsersBySpace(){
		Integer spaceId = 477997;
		try {
			List<PnWeblogEntry> weblogEntries = pnWeblogEntryDAO.getLastBlogEntiesOfAllUsersBySpace(spaceId);
			assertNotNull(weblogEntries);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO.getCountOfBlogEntries(Integer, Integer, String, Date, Date)
	 */
	public void testGetCountOfBlogEntries(){
		Integer weblogId = null; 
		Integer userId = 497434; 
		String status = WeblogConstants.STATUS_PUBLISHED;
		Date startDate = null;
		Date endDate = null;
		try {
			int count = pnWeblogEntryDAO.getCountOfBlogEntries(weblogId, userId, status, startDate, endDate);
			assertTrue(count > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO.getCountOfBlogEntries(Integer, Integer, Integer, Integer, Date, Date, String, int, int, String[],boolean, String, boolean)
	 */
	public void testGetCountOfBlogEntriesWithImportantEntries(){
		Integer weblogId = null; 
		Integer memberId = null;
		Integer userId = 497434;
		Integer objectId = 477997; 
		Date startDate = null;
		Date endDate = null;
		String status = WeblogConstants.STATUS_PUBLISHED;
		boolean showTimeReportedEntries = false;
		String itemType = StringUtils.EMPTY;
		boolean showImportantBlogEntries = false;
		String[] childNodes = null;
		Integer currentUserId = 94239;
		try {
			int count = pnWeblogEntryDAO.getCountOfBlogEntries(weblogId, memberId, userId, objectId, startDate, endDate, status, showTimeReportedEntries, itemType, showImportantBlogEntries, childNodes, currentUserId);
			assertTrue(count > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogEntryDAO.isWeblogEntryDeleted(Integer)
	 */
	public void testIsWeblogEntryDeleted(){
		Integer weblogEntryId = 1201628; 
		try {
			boolean isEntryDeleted = pnWeblogEntryDAO.isWeblogEntryDeleted(weblogEntryId);
			assertTrue(isEntryDeleted || !isEntryDeleted);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
