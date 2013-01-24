package net.project.hibernate.dao;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.model.PnBusinessSpaceView;
import net.project.security.User;

import java.util.List;

public class BusinessSpaceDAOImplTest extends AbstractDaoIntegrationTestBase {

    protected IBusinessSpaceDAO businessSpaceDAO;

    public BusinessSpaceDAOImplTest() {
        setPopulateProtectedVariables(true);
    }

    /* Test method for
      * @see net.project.hibernate.dao.IBusinessSpaceDAO.findByUser(User, String)
      */
    public void testFindByUser() {
        User user = new User();
        user.setID("497434");
        String recordStatus = "A";
        try {
            List<PnBusinessSpaceView> list = businessSpaceDAO.findByUser(user, recordStatus);
            if (list != null && list.size() > 0)
                assertTrue(true);
            else assertTrue("Cannot find user with id: "+user.getID(),false);
        } catch (Exception pnetEx) {
            assertTrue(false);
        }
    }
}
