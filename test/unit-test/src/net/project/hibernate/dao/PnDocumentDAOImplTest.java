package net.project.hibernate.dao;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.dao.IPnDocumentDAO;
import net.project.hibernate.model.PnDocument;

public class PnDocumentDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnDocumentDAO dao;
	
	public PnDocumentDAOImplTest() {
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnDocumentDAO.getDocumentDetailsById(Integer)
	 */
	public void testGetDocumentDetailsById() throws Exception {
		try {
			Integer documentId = 395984;
			PnDocument pnDocument = dao.getDocumentDetailsById(documentId);
			assertNotNull(pnDocument);
			assertTrue(StringUtils.isNotEmpty(pnDocument.getDocName()));
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
