package net.project.hibernate.service;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import junit.framework.TestCase;

import net.project.hibernate.dao.IPnWikiPageDAO;
import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.impl.PnWikiPageServiceImpl;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PnWikiPageServiceImplTest extends TestCase{

    private PnWikiPageServiceImpl service;
    
    private IPnWikiPageDAO mockDao;
    
	public PnWikiPageServiceImplTest() {
		super();
	}

	@BeforeMethod
	protected void setUp() throws Exception {
		mockDao = createStrictMock(IPnWikiPageDAO.class);
		service = new PnWikiPageServiceImpl();
		service.setPnWikiPageDAO(mockDao);
	}

	@AfterMethod
	protected void tearDown() throws Exception {

	}
	
	/* Test method for
	 * @see net.project.hibernate.service.PnWikiPageServiceImpl#get(Integer)
	 */
	public void testGet() {
		Integer wikiPageId = 2046771;
		PnWikiPage pnWikiPage = new PnWikiPage(wikiPageId); 
		expect(mockDao.findByPimaryKey(wikiPageId)).andReturn(pnWikiPage);
		replay(mockDao);
		
		PnWikiPage pnWikiPage2 = service.get(wikiPageId);
		assertEquals(2046771, pnWikiPage2.getWikiPageId().intValue());
		verify(mockDao);
	}
	
	/* Test method for
	 * @see net.project.hibernate.service.PnWikiPageServiceImpl#getWikiPage(Integer)
	 */
	public void testGetWikiPage() {
		Integer wikiPageId = 2046771;
		PnWikiPage pnWikiPage = new PnWikiPage(wikiPageId); 
		expect(mockDao.getWikiPageByPageId(wikiPageId)).andReturn(pnWikiPage);
		replay(mockDao);
		
		PnWikiPage pnWikiPage2 = service.getWikiPage(wikiPageId);
		assertEquals(2046771, pnWikiPage2.getWikiPageId().intValue());
		verify(mockDao);
	}
}