package net.project.hibernate.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnWikiAttachmentDAO;
import net.project.hibernate.model.PnWikiAttachment;
import net.project.hibernate.model.PnWikiPage;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.springframework.test.AssertThrows;

import net.project.form.CreateDateField;

public class PnWikiAttachmentDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnWikiAttachmentDAO pnWikiAttachmentDAO;
	
	public PnWikiAttachmentDAOImplTest() {
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for
	 * @see net.project.hibernate.dao.PnWikiAttachmentDAOImpl#getFileIdWithWikiPageAndFileName(Integer, String)
	 */
	public void testGetFileIdWithWikiPageAndFileName() {
		Integer wikiPageId = 1045617;
		String fileName = "Sunset.jpg";
		try {
			PnWikiAttachment wikiAttachment = pnWikiAttachmentDAO.getFileIdWithWikiPageAndFileName(wikiPageId, fileName);
			assertNotNull(wikiAttachment);
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	
	/* Test method for
	 * @see net.project.hibernate.dao.PnWikiAttachmentDAOImpl#getRecordWithWikiPageIdAndFileNameWithStatusAorD(Integer, String)
	 */
	public void testGetRecordWithWikiPageIdAndFileNameWithStatusAorD() {
		Integer wikiPageId = 1045617;
		String fileName = "Sunset.jpg";
		try {
			PnWikiAttachment wikiAttachment = pnWikiAttachmentDAO.getRecordWithWikiPageIdAndFileNameWithStatusAorD(wikiPageId, fileName);
			assertNotNull(wikiAttachment);
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	
	/* Test method for
	 * @see net.project.hibernate.dao.PnWikiAttachmentDAOImpl#getAllAttachmentsFromWikiPage(Integer)
	 */
	public void testGetAllAttachmentsFromWikiPage() {
		Integer wikiPageId = 1045617;
		List<PnWikiAttachment> pnWikiAttachmentList = new ArrayList<PnWikiAttachment>();
		
		try {
			pnWikiAttachmentList = pnWikiAttachmentDAO.getAllAttachmentsFromWikiPage(wikiPageId);
			assertNotNull(pnWikiAttachmentList);
			assertTrue(pnWikiAttachmentList.size() > 0);
		} catch (Exception e) {
			assertTrue(false);
		}		
	}
	
	/* Test method for
	 * @see net.project.hibernate.dao.PnWikiAttachmentDAOImpl#doesAttachmentWithNameExistOnWikiPage(Integer, String, char)
	 */
	public void testDoesAttachmentWithNameExistOnWikiPage() {
		Integer wikiPageId = 1045617;
		String attachmentName = "Sunset.jpg";
		char withStatus = 'A';
		boolean result = false;
		try{
			result = pnWikiAttachmentDAO.doesAttachmentWithNameExistOnWikiPage(wikiPageId, attachmentName, withStatus);
			assertTrue(result);
		} catch (Exception e) {
			assertTrue(false);
		}
	}
}
