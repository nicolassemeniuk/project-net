package net.project.hibernate.dao;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.model.PnWikiHistory;

import java.util.List;

public class PnWikiHistoryDAOImplTest extends AbstractDaoIntegrationTestBase {

    protected IPnWikiHistoryDAO pnWikiHistoryDAO;

    public PnWikiHistoryDAOImplTest() {
        setPopulateProtectedVariables(true);
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnWikiHistoryDAO#findHistoryWithPageId(java.lang.Integer)
      */
    public void testFindHistoryWithPageId() {
        Integer pageId = 2046771;
        List<PnWikiHistory> wikiHistoryList;
        try {
            wikiHistoryList = pnWikiHistoryDAO.findHistoryWithPageId(pageId);
            assertNotNull("History for page: " + pageId + " is", wikiHistoryList);
            assertTrue("History result for page id : " + pageId + "is " + wikiHistoryList.size(), wikiHistoryList.size() > 0);
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }
}
