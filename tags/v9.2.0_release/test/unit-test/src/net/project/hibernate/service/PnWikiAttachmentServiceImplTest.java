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
import net.project.hibernate.dao.IPnWikiAttachmentDAO;
import net.project.hibernate.model.PnWikiAttachment;
import net.project.hibernate.service.impl.PnWikiAttachmentServiceImpl;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PnWikiAttachmentServiceImplTest extends TestCase{

    /**
     * The logger
     */
   // private final Logger log = Logger.getLogger(PnPersonServiceImplTest.class);	
	     
    private PnWikiAttachmentServiceImpl service;
    
    private IPnWikiAttachmentDAO mockDao;
    
	public PnWikiAttachmentServiceImplTest() {
		super();
	}

	@BeforeMethod
	protected void setUp() throws Exception {
		//log.debug("setUpDao for PnPersonServiceImplTest");
		System.out.println("setUpDao for PnWikiAttachmentServiceImplTest");
		mockDao = createStrictMock(IPnWikiAttachmentDAO.class);
		service = new PnWikiAttachmentServiceImpl();
		service.setPnWikiAttachmentDAO(mockDao);
	}

	@AfterMethod
	protected void tearDown() throws Exception {

	}
	
	/* Test method for
	 * @see net.project.hibernate.service.PnWikiAttachmentServiceImpl#getFileIdWithWikiPageAndFileName(Integer, String)
	 */
	public void testGetFileIdWithWikiPageAndFileName() {
		Integer wikiPageId = 1045617;
		String fileName = "test.gif";
		Integer wikiAttachmentId = 1;
		PnWikiAttachment pnWikiAttachment = new PnWikiAttachment(wikiAttachmentId);
		pnWikiAttachment.setAttachmentName(fileName);
		
		expect(mockDao.getFileIdWithWikiPageAndFileName(wikiPageId, fileName)).andReturn(pnWikiAttachment);
		replay(mockDao);
		
		PnWikiAttachment pnWikiAttachment2 = service.getFileIdWithWikiPageAndFileName(wikiPageId, fileName);
		assertEquals(1, pnWikiAttachment2.getWikiAttachmentId().intValue());
		assertEquals("test.gif", pnWikiAttachment2.getAttachmentName());
		verify(mockDao);
	}
	
	/* Test method for
	 * @see net.project.hibernate.service.PnWikiAttachmentServiceImpl#getRecordWithWikiPageIdAndFileNameWithStatusAorD(Integer, String)
	 */
	public void testGetRecordWithWikiPageIdAndFileNameWithStatusAorD() {
		Integer wikiPageId = 1045617;
		String fileName = "test.gif";
		Integer wikiAttachmentId = 1;
		PnWikiAttachment pnWikiAttachment = new PnWikiAttachment(wikiAttachmentId);
		pnWikiAttachment.setAttachmentName(fileName);
		
		expect(mockDao.getRecordWithWikiPageIdAndFileNameWithStatusAorD(wikiPageId, fileName)).andReturn(pnWikiAttachment);
		replay(mockDao);
		
		PnWikiAttachment pnWikiAttachment2 = service.getRecordWithWikiPageIdAndFileNameWithStatusAorD(wikiPageId, fileName);
		assertEquals(1, pnWikiAttachment2.getWikiAttachmentId().intValue());
		assertEquals("test.gif", pnWikiAttachment2.getAttachmentName());
		verify(mockDao);
	}
	
	/* Test method for
	 * @see net.project.hibernate.service.PnWikiAttachmentServiceImpl#getAllAttachmentsFromWikiPage(Integer)
	 */
	public void testGetAllAttachmentsFromWikiPage() {
		Integer wikiPageId = 1045617;
		List<PnWikiAttachment> pnWikiAttachmentList = new ArrayList<PnWikiAttachment>();
		pnWikiAttachmentList.add(new PnWikiAttachment(1));
		pnWikiAttachmentList.add(new PnWikiAttachment(2));
		
		expect(mockDao.getAllAttachmentsFromWikiPage(wikiPageId)).andReturn(pnWikiAttachmentList);
		replay(mockDao);
		
		List<PnWikiAttachment> pnWikiAttachmentList2 = service.getAllAttachmentsFromWikiPage(wikiPageId);
		assertEquals(1, pnWikiAttachmentList2.get(0).getWikiAttachmentId().intValue());
		assertEquals(2, pnWikiAttachmentList2.get(1).getWikiAttachmentId().intValue());
		verify(mockDao);
	}
}