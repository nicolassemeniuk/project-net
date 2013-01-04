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
import net.project.hibernate.dao.IPnWikiHistoryDAO;
import net.project.hibernate.model.PnWikiHistory;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.service.impl.PnWikiHistoryServiceImpl;

import org.apache.commons.lang.StringUtils;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PnWikiHistoryServiceImplTest extends TestCase{

    private PnWikiHistoryServiceImpl service;
    
    private IPnWikiHistoryDAO mockWikiHistoryDao;
    
	public PnWikiHistoryServiceImplTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		mockWikiHistoryDao = createMock(IPnWikiHistoryDAO.class);
		service = new PnWikiHistoryServiceImpl();
		service.setPnWikiHistoryDAO(mockWikiHistoryDao);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/* Test method for
	 * @see net.project.hibernate.service.IPnWikiHistoryService#findHistoryWithPageId(java.lang.Integer)
	 */
	public void testFindHistoryWithPageId() {
		Integer pageId = 2046771;
		List<PnWikiHistory> wikiHistoryList = new ArrayList<PnWikiHistory>();
		expect(mockWikiHistoryDao.findHistoryWithPageId(pageId)).andReturn(wikiHistoryList);
		replay(mockWikiHistoryDao);
		wikiHistoryList =  service.findHistoryWithPageId(pageId);
		verify(mockWikiHistoryDao);
	}
}