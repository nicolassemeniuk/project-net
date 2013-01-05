package net.project.hibernate.service;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.dao.IPnNewsDAO;
import net.project.hibernate.model.PnNews;
import net.project.hibernate.service.impl.PnNewsServiceImpl;
import junit.framework.TestCase;

public class PnNewsServiceImplTest extends TestCase{
	
	private PnNewsServiceImpl newsService;
	
	private IPnNewsDAO mockNewsDAO;
	
	public PnNewsServiceImplTest(){
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		mockNewsDAO = createMock(IPnNewsDAO.class);
		newsService = new PnNewsServiceImpl();
		newsService.setPnNewsDAO(mockNewsDAO);
	}
	
	protected void tearDown() throws Exception{
		super.tearDown();
	}
	
	/* 
	 * Test method for
     * @see net.project.hibernate.service.PnNewsServiceImpl#getNewsWithRecordStatus(BigDecimal)
     */
    public final void testGetNewsWithRecordStatus(){
    	BigDecimal newsId = BigDecimal.valueOf(1);
    	
    	PnNews pnNews = new PnNews();
    	pnNews.setCreatedById(1);
    	pnNews.setRecordStatus("A");
    	pnNews.setNewsId(1);
    	pnNews.setTopic("Writing Test Cases");
    	
    	expect(mockNewsDAO.getNewsWithRecordStatus(newsId)).andReturn(pnNews);
    	replay(mockNewsDAO);
    	PnNews news = newsService.getNewsWithRecordStatus(newsId);
    	assertEquals(1, news.getNewsId().intValue());
    	assertEquals("A", news.getRecordStatus());
    	verify(mockNewsDAO);
    }
    
    /* 
	 * Test method for
     * @see net.project.hibernate.service.PnNewsServiceImpl#getNewsWithRecordStatus(BigDecimal)
     */
    public final void testGetNewsWithRecordStatusWithNoNews(){
    	BigDecimal newsId = BigDecimal.valueOf(1);
    	
    	PnNews pnNews = new PnNews();
    	expect(mockNewsDAO.getNewsWithRecordStatus(newsId)).andReturn(pnNews);
    	replay(mockNewsDAO);
    	PnNews news = newsService.getNewsWithRecordStatus(newsId);
    	assertEquals(null, news.getNewsId());
    	assertTrue(StringUtils.isEmpty(news.getRecordStatus()));
    	verify(mockNewsDAO);
    }
}
