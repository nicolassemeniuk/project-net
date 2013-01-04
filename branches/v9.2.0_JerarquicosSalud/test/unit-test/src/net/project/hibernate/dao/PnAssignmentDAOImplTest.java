package net.project.hibernate.dao;

import net.project.base.ObjectType;
import net.project.base.hibernate.TestBase;
import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.model.PnAssignment;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.model.resource_reports.ReportUser;
import net.project.resource.AssignmentStatus;

import java.util.Date;
import java.util.List;

public class PnAssignmentDAOImplTest extends AbstractDaoIntegrationTestBase {
    protected IPnAssignmentDAO assignmentDAO;

    private TestBase testBase;


    public PnAssignmentDAOImplTest() {
        setPopulateProtectedVariables(true);
        testBase = new TestBase();
    }

    /*
      * Test method for 'net.project.hibernate.dao.IPnAssignmentDAO.getAssignmentDetailsWithFilters(Integer[],
      *	String, Integer[], Integer, String[], boolean, boolean, boolean, boolean, Date, Date, Integer, Double, String, String, String,
      *	int, int, boolean)'
      */
    public void testGetAssignmentDetailsWithFilters() {
        Integer[] personIds = {497434};
        String assigneeORAssignor = "assignor";
        Integer[] projectIds = null;
        Integer businessId = null;
        String[] assignmentTypes = {ObjectType.TASK};
        boolean lateAssignment = false;
        boolean comingDueDate = false;
        boolean shouldHaveStart = false;
        boolean inProgress = false;
        Date startDate = null;
        Date endDate = null;
        Integer statusId = Integer.valueOf(AssignmentStatus.ACCEPTED.getID());
        Double percentComplete = 1.00;
        String PercentCompleteComparator = "lessthan";
        String assignmentName = null;
        String assignmentNameComparator = null;
        int offset = 0;
        int range = 0;
        boolean isOrderByPerson = false;
        assertNotNull(assignmentDAO);
        try {
            List<PnAssignment> assignments = assignmentDAO.getAssignmentDetailsWithFilters(personIds, assigneeORAssignor,
                    projectIds, businessId, assignmentTypes, lateAssignment, comingDueDate, shouldHaveStart, inProgress, startDate,
                    endDate, statusId, percentComplete, PercentCompleteComparator, assignmentName, assignmentNameComparator, offset,
                    range, isOrderByPerson);

            assertNotNull(assignments);
            assertTrue(assignments.size() > 0);
        } catch (Exception pnetEx) {
            pnetEx.printStackTrace();
            assertTrue(false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnAssignmentDAO.getAssigmentsList(Integer[], Date, Date)
      */
    public void testGetAssigmentsList() {
        Integer[] personIds = {497434};
        Date startDate = null;
        Date endDate = null;
        try {
            List<PnAssignment> list = assignmentDAO.getAssigmentsList(personIds, startDate, endDate);
            assertNotNull(list);
            assertTrue(list.size() > 0);
        } catch (Exception pnetEx) {
            assertTrue(false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnAssignmentDAO.getCurrentAssigmentsListForProject(Integer[], Date, Date)
      */
    public void testGetCurrentAssigmentsListForProject() {
        Integer[] personIds = {497434};
        Integer projectId = 477997;
        Date date = null;
        try {
            List<PnAssignment> list = assignmentDAO.getCurrentAssigmentsListForProject(projectId, personIds, date);
            assertNotNull(list);
            assertTrue(list.size() > 0);
        } catch (Exception pnetEx) {
            assertTrue(false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnAssignmentDAO.getWorkSumByMonthForUsers(Integer[], Integer[], Date, Date)
      */
    public void testGetWorkSumByMonthForUsers() {
        Integer[] personIds = {497434};
        Integer[] projectIds = {477997};
        Date startDate = null;
        Date endDate = null;
        try {
            List list = assignmentDAO.getWorkSumByMonthForUsers(personIds, projectIds, startDate, endDate);
            assertNotNull(list);
            assertTrue(list.size() > 0);
        } catch (Exception pnetEx) {
            assertTrue(false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnAssignmentDAO.getWorkSumByMonthForBusiness(Integer, Integer, Date, Date)
      */
    public void testGetWorkSumByMonthForBusiness() {
        Integer resourceId = 497434;
        Integer businessId = 434709;
        Date startDate = null;
        Date endDate = null;
        try {
            List list = assignmentDAO.getWorkSumByMonthForBusiness(resourceId, businessId, startDate, endDate);
            assertNotNull(list);
            assertTrue(list.size() > 0);
        } catch (Exception pnetEx) {
            assertTrue(false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnAssignmentDAO.getResourceAssignmentSummary(Integer, Integer, Date, Date)
      */
    public void testGetResourceAssignmentSummary() {
        Integer businessId = 434709;
        Integer resourceId = 497434;
        Date startDate = null;
        Date endDate = null;
        try {
            List list = assignmentDAO.getResourceAssignmentSummaryByBusiness(resourceId, businessId, startDate, endDate);
            assertNotNull(list);
            assertTrue(list.size() > 0);
        } catch (Exception pnetEx) {
            assertTrue(false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnAssignmentDAO.getResourceAssignmentDetails(Integer, Integer[], Date, Date)
      */
    public void testGetResourceAssignmentDetails() {
        Integer resourceId = 497434;
        Integer[] projectIds = {477997};
        Date startDate = null;
        Date endDate = null;
        try {
            List list = assignmentDAO.getResourceAssignmentDetails(resourceId, projectIds, startDate, endDate);
            assertNotNull(list);
            assertTrue(list.size() > 0);
        } catch (Exception pnetEx) {
            assertTrue(false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnAssignmentDAO.getOverAllocatedResources(Integer)
      */
    public void testGetOverAllocatedResources() {
        Integer userId = 497434;
        try {
            List<PnAssignment> list = assignmentDAO.getOverAllocatedResources(userId);
            assertNotNull(list);
        } catch (Exception pnetEx) {
            assertTrue(false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnAssignmentDAO.getOverAssignedResources(Integer, Date, Date)
      */
    public void testGetOverAssignedResources() {
        Integer userId = 497434;
        Date startDate = null;
        Date endDate = null;
        try {
            List<PnAssignment> list = assignmentDAO.getOverAllocatedResources(userId);
            assertNotNull(list);
        } catch (Exception pnetEx) {
            assertTrue(false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnAssignmentDAO.getCurrentOverAssignedResourcesForProject(Integer, Date)
      */
    public void testGetCurrentOverAssignedResourcesForProject() {
        Integer projectId = 477997;
        Date date = testBase.createDate(2008, 1, 29);
        try {
            List<PnAssignment> list = assignmentDAO.getCurrentOverAssignedResourcesForProject(projectId, date);
            assertNotNull(list);
        } catch (Exception pnetEx) {
            assertTrue(false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnAssignmentDAO.getAssignmentsByPersonForProject(Integer, Date, Date)
      */
    public void testGetAssignmentsByPersonForProject() {
        Integer projectId = 477997;
        Date startDate = testBase.createDate(2008, 1, 29);
        Date endDate = testBase.createDate(2008, 3, 29);
        try {
            List<Teammate> list = assignmentDAO.getAssignmentsByPersonForProject(projectId, startDate, endDate);
            assertNotNull(list);
        } catch (Exception pnetEx) {
            assertTrue(false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnAssignmentDAO.getAssignedProjectsByBusiness(String, Integer, Date, Date)
      */
    public void testGetAssignedProjectsByBusiness() {
        String userId = "497434";
        Integer businessId = null;
        Date startDate = testBase.createDate(2008, 1, 29);
        Date endDate = testBase.createDate(2008, 3, 29);
        try {
            List<ReportUser> list = assignmentDAO.getAssignedProjectsByBusiness(userId, businessId, startDate, endDate);
            assertNotNull(list);
            assertTrue(list.size() > 0);
        } catch (Exception pnetEx) {
            assertTrue(false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnAssignmentDAO.getTeammatesWithoutAssignments(Integer, Date)
      */
    public void testGetTeammatesWithoutAssignments() {
        Integer projectId = 477997;
        Date date = testBase.createDate(2008, 3, 29);
        try {
            List<Teammate> list = assignmentDAO.getTeammatesWithoutAssignments(projectId, date);
            assertNotNull(list);
            assertTrue(list.size() > 0);
        } catch (Exception pnetEx) {
            assertTrue(false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnAssignmentDAO.getAssignorAssignmentDetails(Integer)
      */
    public void testGetAssignorAssignmentDetails() {
        Integer assignorId = 497434;
        try {
            List<PnAssignment> list = assignmentDAO.getAssignorAssignmentDetails(assignorId);
            assertNotNull(list);
            assertTrue(list.size() > 0);
        } catch (Exception pnetEx) {
            assertTrue(false);
        }
    }

    /* Test method for
      * @see net.project.hibernate.dao.IPnAssignmentDAO.getTotalAssignmentCountWithFilters(Integer[],
      *	String, Integer[], Integer, String[], boolean, boolean, boolean, boolean, Date, Date, Integer, Double, String, String, String)
      */
    public void testGetTotalAssignmentCountWithFilters() {
        Integer personId = 497434;
        String assigneeORAssignor = "assignor";
        Integer[] projectIds = null;
        Integer businessId = null;
        String[] assignmentTypes = {ObjectType.TASK};
        boolean lateAssignment = false;
        boolean comingDueDate = false;
        boolean shouldHaveStart = false;
        boolean inProgress = false;
        Date startDate = null;
        Date endDate = null;
        Integer statusId = Integer.valueOf(AssignmentStatus.ACCEPTED.getID());
        Double percentComplete = 1.00;
        String PercentCompleteComparator = "lessthan";
        String assignmentName = null;
        String assignmentNameComparator = null;
        try {
            Integer totalAssignments = assignmentDAO.getTotalAssignmentCountWithFilters(personId, assigneeORAssignor, projectIds, businessId, assignmentTypes, lateAssignment, comingDueDate, shouldHaveStart, inProgress, startDate, endDate, statusId, percentComplete, PercentCompleteComparator, assignmentName, assignmentNameComparator);
            assertNotNull(totalAssignments);
            assertTrue(totalAssignments.intValue() > 0);
        } catch (Exception pnetEx) {
            assertTrue(false);
        }
    }
}
