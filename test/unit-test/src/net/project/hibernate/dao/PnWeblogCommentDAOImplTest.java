package net.project.hibernate.dao;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.dao.IPnWeblogCommentDAO;
import net.project.hibernate.model.PnWeblogComment;

public class PnWeblogCommentDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	protected IPnWeblogCommentDAO pnWeblogCommentDAO;
	
	public PnWeblogCommentDAOImplTest(){
		setPopulateProtectedVariables(true);
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogCommentDAO.getWeblogCommentsForWeblogEntry(Integer, String)
	 */
	public void testGetWeblogCommentsForWeblogEntry(){
		Integer weblogEntryId = 1202457;
		String status = WeblogConstants.COMMENT_APPROVED_STATUS;
		try {
			Set<PnWeblogComment> set = pnWeblogCommentDAO.getWeblogCommentsForWeblogEntry(weblogEntryId, status);
			assertNotNull(set);
			assertTrue(set.size() > 0);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
	
	/* Test method for 
	 * @see net.project.hibernate.dao.IPnWeblogCommentDAO.getWeblogCommentByCommentId(Integer)
	 */
	public void testGetWeblogCommentByCommentId(){
		Integer commentId = 86;
		try {
			PnWeblogComment pnWeblogComment = pnWeblogCommentDAO.getWeblogCommentByCommentId(commentId);
			assertNotNull(pnWeblogComment);
		} catch (Exception pnetEx) {
			assertTrue(false);
		}
	}
}
