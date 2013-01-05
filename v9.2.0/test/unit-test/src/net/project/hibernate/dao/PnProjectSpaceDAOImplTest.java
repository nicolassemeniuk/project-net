package net.project.hibernate.dao;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.sql.DataSource;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import org.springframework.jdbc.datasource.DataSourceUtils;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.project_space.ProjectChanges;
import net.project.hibernate.model.project_space.ProjectPhase;
import net.project.hibernate.model.project_space.ProjectSchedule;
import net.project.test.util.TestProperties;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

public class PnProjectSpaceDAOImplTest extends AbstractDaoIntegrationTestBase {

	private static String TEST_DATA_FILE = TestProperties.getInstance().getProperty("mockobject.properties.location") + "/dbunit-test-data.xml";

	//protected IPnProjectSpaceDAO dao;

	public PnProjectSpaceDAOImplTest() {
		super();
		setPopulateProtectedVariables(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
	 */
	@Override
	protected void onSetUpInTransaction() throws Exception {
		super.onSetUpInTransaction();
		DataSource ds = this.jdbcTemplate.getDataSource();
		Connection conn = ds.getConnection();
		try {
			IDatabaseConnection connection = new DatabaseConnection(conn);
			DatabaseOperation.INSERT.execute(connection, new FlatXmlDataSet(new FileInputStream(TEST_DATA_FILE)));
		} finally {
			DataSourceUtils.releaseConnection(conn, ds);
		}
	}

	/**
	 * test success
	 * 
	 * @throws Exception
	 */
	public void testGetProjectsByUserId() throws Exception {
		/*try {
			Integer userId = 1;
			List<PnProjectSpace> projectList = dao.getProjectsByUserId(userId);
			assertNotNull(projectList);
			assertTrue(projectList.size() == 0);
		} catch (Exception e) {
			assertTrue(false);
		}*/
	}

/*
	public void testGetProjectsByUserIdNull() throws Exception {
		try {
			List<PnProjectSpace> projectList = dao.getProjectsByUserId(null);
			assertNotNull(projectList);
			assertTrue(projectList.size() == 0);
		} catch (Exception e) {
			assertTrue(false);
		}
	}


	public void testGetProjectsByMemberId() throws Exception {
		try {
			Integer userId = 1;
			List<PnProjectSpace> projectList = dao.getProjectsByMemberId(userId);
			assertNotNull(projectList);
			assertTrue(projectList.size() > 0);
		} catch (Exception e) {
			assertTrue(false);
		}
	}


	public void testGetProjectsByMemberIdNull() throws Exception {
		try {
			List<PnProjectSpace> projectList = dao.getProjectsByMemberId(null);
			assertNotNull(projectList);
			assertTrue(projectList.size() == 0);
		} catch (Exception e) {
			assertTrue(false);
		}
	}


	public void testGetProjectsByBusinessId() throws Exception {
		try {
			Integer businessId = 1;
			List<PnProjectSpace> projectList = dao.getProjectsByBusinessId(businessId);
			assertNotNull(projectList);
			assertTrue(projectList.size() > 0);
		} catch (Exception e) {
			assertTrue(false);
		}
	}


	public void testGetProjectsByBusinessIdNull() throws Exception {
		try {
			List<PnProjectSpace> projectList = dao.getProjectsByBusinessId(null);
			assertNotNull(projectList);
			assertTrue(projectList.size() == 0);
		} catch (Exception e) {
			assertTrue(false);
		}
	}


	public void testGetProjectsVisbleByUser() throws Exception {
		try {
			Integer userId = 1;
			List<PnProjectSpace> projectList = dao.getProjectsVisbleByUser(userId);
			assertNotNull(projectList);
			assertTrue(projectList.size() > 0);
		} catch (Exception e) {
			assertTrue(false);
		}
	}


	public void testGetProjectsVisbleByUserNull() throws Exception {
		try {
			List<PnProjectSpace> projectList = dao.getProjectsByBusinessId(null);
			assertNotNull(projectList);
			assertTrue(projectList.size() == 0);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	public void testGetProjectPhasesAndMilestones() {
		try {
			Integer projectId = 1;
			List<ProjectPhase> phases = dao.getProjectPhasesAndMilestones(projectId);

			assertEquals(2, phases.size());
			assertEquals(2, phases.get(0).getMilestones().size());
			assertEquals(1, phases.get(1).getMilestones().size());
		} catch (Exception e) {
			assertTrue(false);
		}
	}


	private Date createDate(int year, int month, int day) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, day);
		return cal.getTime();
	}

	public void testGetProjectSchedule() {
		try {
			Integer projectId = 1;
			ProjectSchedule projectSchedule = dao.getProjectSchedule(projectId);

			assertEquals(createDate(2008, 2, 1), projectSchedule.getPlannedStart());
			assertEquals(createDate(2008, 2, 28), projectSchedule.getPlannedFinish());
			assertEquals(createDate(2008, 2, 5), projectSchedule.getActualStart());
			assertEquals(createDate(2008, 3, 1), projectSchedule.getActualFinish());

			assertEquals(3, projectSchedule.getNumberOfCompletedTasks().intValue());
			assertEquals(0, projectSchedule.getNumberOfLateTasks().intValue());
			assertEquals(0, projectSchedule.getNumberOfTaskComingDue().intValue());
			assertEquals(0, projectSchedule.getNumberOfUnassignedTasks().intValue());

		} catch (Exception e) {
			assertTrue(false);
		}
	}

	public void testGetProjectChanges() {
		try {
			Integer projectId = 1;
			Integer numberOfDays = 7;
			ProjectChanges projectChanges = dao.getProjectChanges(projectId, numberOfDays);

			assertEquals(0, projectChanges.getDiscussions().size());
			assertEquals(3, projectChanges.getDocuments().size());
			assertEquals(5, projectChanges.getForms().size());

		} catch (Exception e) {
			assertTrue(false);
		}
	}

	public void testGetAssignedProjectsByResource() {
		try {
			Integer businessId = 1;
			Integer resourceId = 1;
			Date startDate = createDate(2008, 3, 1);
			Date endDate = createDate(2008, 3, 10);
			List<PnProjectSpace> projects = dao.getAssignedProjectsByResource(businessId, resourceId, startDate,
					endDate);

			assertEquals(3, projects.size());

		} catch (Exception e) {
			assertTrue(false);
		}
	}

	public void testGetAssignedProjectsByResourceBusinessIdNull() {
		try {
			Integer resourceId = 1;
			Date startDate = createDate(2008, 3, 1);
			Date endDate = createDate(2008, 3, 10);
			List<PnProjectSpace> projects = dao.getAssignedProjectsByResource(null, resourceId, startDate, endDate);

			assertEquals(4, projects.size());

		} catch (Exception e) {
			assertTrue(false);
		}
	}
*/
}
