package net.project.hibernate.dao;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.model.PnWikiPage;

import java.util.List;

public class PnWikiPageDAOImplTest extends AbstractDaoIntegrationTestBase {

    protected IPnWikiPageDAO pnWikiPageDAO;

    public PnWikiPageDAOImplTest() {
        setPopulateProtectedVariables(true);
    }


    /* Test method for
      * @see net.project.hibernate.dao.IPnWikiPageDAO#getWikiPageWithName(String, Integer)
      */
    public void testGetWikiPageWithName() {
        String pageName = "testpage";
        Integer ownerObjectId = 972052;

        try {
            PnWikiPage pnWikiPage = pnWikiPageDAO.getWikiPageWithName(pageName, ownerObjectId);
                assertNotNull(pnWikiPage);
                assertEquals("testpage", pnWikiPage.getPageName());
        } catch (Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnWikiPageDAO#getRecordWithWikiPageNameAndProjectSpaceIdWithStatusAorD(String, Integer)
      */
    public void testGetRecordWithWikiPageNameAndProjectSpaceIdWithStatusAorD() {
        String pageName = "testpage";
        Integer ownerObjectId = 972052;

        try {
            PnWikiPage pnWikiPage = pnWikiPageDAO.getRecordWithWikiPageNameAndProjectSpaceIdWithStatusAorD(pageName, ownerObjectId);
            assertNotNull(pnWikiPage);
            assertEquals("testpage", pnWikiPage.getPageName());
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnWikiPageDAO#getWikiPagesByOwnerAndRecordStatus(Integer, String)
      */
    public void testGetWikiPagesByOwnerAndRecordStatus() {
        Integer ownerObjectId = 972052;
        String status = "A";

        try {
            List<PnWikiPage> pnWikiPageList = pnWikiPageDAO.getWikiPagesByOwnerAndRecordStatus(ownerObjectId, status);
            assertNotNull(pnWikiPageList);
            assertTrue("Wiki pages to owner: " + ownerObjectId+" is ", pnWikiPageList.size() > 0);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnWikiPageDAO#doesWikiPageWithGivenNameExist(String, Integer)
      */
    public void testDoesWikiPageWithGivenNameExist() {
        String wikiPageName = "testpage";
        Integer projectSpaceId = 972052;
        boolean isWikiPageExist = false;
        try {
            isWikiPageExist = pnWikiPageDAO.doesWikiPageWithGivenNameExist(wikiPageName, projectSpaceId);
            assertTrue(isWikiPageExist);
        } catch (Exception e) {
            assertTrue(false);
        }
    }
}
