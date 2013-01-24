package net.project.hibernate.dao;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.io.File;

import javax.sql.DataSource;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.project_space.Teammate;
import net.project.hibernate.model.resource_reports.ReportProjectUsers;
import net.project.test.util.TestProperties;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class PnPersonDAOImplTest extends AbstractDaoIntegrationTestBase{

	private static String TEST_DATA_FILE = TestProperties.getInstance().getProperty("mockobject.properties.location") + "/dbunit-test-data.xml";
	
	protected IPnPersonDAO personDAO;
	
	public PnPersonDAOImplTest() {
		setPopulateProtectedVariables(true);
	}	 
	
	/* (non-Javadoc)
	 * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
	 */
	@Override
	protected void onSetUpInTransaction() throws Exception {	
		super.onSetUpInTransaction();
		DataSource ds = this.jdbcTemplate.getDataSource();
		Connection conn = ds.getConnection();
		try {
            System.out.println("Using data file :" + new File(TEST_DATA_FILE).getAbsolutePath());
            IDatabaseConnection connection = new DatabaseConnection(conn);
            DatabaseOperation.INSERT.execute(connection, new FlatXmlDataSet(new FileInputStream(TEST_DATA_FILE)));
        } finally {
            DataSourceUtils.releaseConnection(conn, ds);
        }
    }		
	
	public void testFindAll() throws Exception {
		// load from DB -- no data loaded!!
		//assertEquals(null, personDAO.findAll() );
	}	
	
	
/*
	public void testFindByPrimaryKey() throws Exception {
		// load from DB
		PnPerson person = personDAO.findByPimaryKey(100001);

		// test if all propertires are equal
		assertEquals("john@test.com", person.getEmail());
		assertEquals("John", person.getFirstName());
		assertEquals("Doe", person.getLastName());
		assertEquals("John Doe", person.getDisplayName());
		assertEquals("Active", person.getUserStatus());
		assertEquals("A", person.getRecordStatus());
		assertEquals("john", person.getPnUser().getUsername()); 
	}	
	
	public void testGetUniqueMembersOfBusinessCollection() throws Exception {
		List<Integer> businessIds = new ArrayList<Integer>();
		businessIds.add(1);
		businessIds.add(2);
		try {
			List<PnPerson> persons = personDAO.getUniqueMembersOfBusinessCollection(businessIds);
			assertEquals(3, persons.size());			
		} catch (Exception e) {
			assertTrue(false);
		}
	}	
	
	public void testGetUniqueMembersOfBusinessCollectionEmptyInput() throws Exception {
		List<Integer> businessIds = new ArrayList<Integer>();
		try {
			List<PnPerson> persons = personDAO.getUniqueMembersOfBusinessCollection(businessIds);
			assertNotNull(persons);
			assertEquals(0, persons.size());			
		} catch (Exception e) {
			assertTrue(false);
		}
	}	
	
	public void testGetAllPersonsIds() throws Exception {
		try {
			Integer personId = 1;
			List<PnPerson> persons = personDAO.getAllPersonsIds(personId);
			boolean found = false;
			for (PnPerson person : persons) {
				found = found || (person.getPersonId().intValue() == personId);
			}
			assertTrue("Excluded person id found in list!!!", !found);

			String sql = "SELECT COUNT(person_id) FROM pn_person WHERE person_id <> :personId";
			int count = jt.queryForInt(sql, personId); 
			assertEquals(count, persons.size());
		} catch (Exception e) {
			assertTrue(false);
		}
	}
	

	public void testGetAllPersonsIdsForNull() throws Exception {
		try {
			List<PnPerson> persons = personDAO.getAllPersonsIds(null);
			assertEquals(0, persons.size());
		} catch (Exception e) {
			assertTrue(false);
		}
	}	

	
	public void testGetPersonsByProjectId() throws Exception {
		try{
			Integer projectId = 1;
			List<PnPerson> persons = personDAO.getPersonsByProjectId(projectId);
			assertNotNull(persons);
			assertEquals(5, persons.size());	
		}catch (Exception e) {
			assertTrue(false);
		}
	}

	public void testGetPersonsByProjectIdWithNonExistID() throws Exception {
		try{
			Integer projectId = -1;
			List<PnPerson> persons = personDAO.getPersonsByProjectId(projectId);
			assertNotNull(persons);
			assertEquals(0, persons.size());	
		}catch (Exception e) {
			assertTrue(false);
		}
	}	
	
	public void testGetPersonsByBusinessId() throws Exception {
		try{
			Integer businessId = 1;
			List<PnPerson> persons = personDAO.getPersonsByBusinessId(businessId);
			assertNotNull(persons);
			assertTrue(persons.size() > 0);
		}catch (Exception e) {
			assertTrue(false);
		}
	}	
	
	public void testGetPersonsByBusinessAndProjectId() throws Exception {
		try{
			Integer businessId = 1;
			Integer projectId = 1;
			List<PnPerson> persons = personDAO.getPersonsByBusinessAndProjectId(businessId, projectId);
			assertNotNull(persons);
			assertTrue(persons.size() > 0);
		}catch (Exception e) {
			assertTrue(false);
		}
	}

	public void testGetPersonsByAllBusinesses() throws Exception {
		try{
			List<PnPerson> persons = personDAO.getPersonsByAllBusinesses();
			assertNotNull(persons);
			assertTrue(persons.size() > 0);
		}catch (Exception e) {
			assertTrue(false);
		}
	}	
	
	public void testGetOnlineMembers() throws Exception {
		try{
			Integer spaceId = 1;
			List<Teammate> teammates = personDAO.getOnlineMembers(spaceId);
			assertEquals(2, teammates.size());
			assertEquals(1, teammates.get(0).getPersonId().intValue());
			assertEquals(2, teammates.get(1).getPersonId().intValue());
		}catch (Exception e) {
			assertTrue(false);
		}
	}
	
	public void testGetAssignedResourcesByProject() throws Exception {
		try{
			Integer projectId = 1;
			Calendar cal = GregorianCalendar.getInstance();
			Date startDate = cal.getTime();
			cal.add(Calendar.DATE, 15);
			Date endDate = cal.getTime();
			List<ReportProjectUsers> persons = personDAO.getAssignedResourcesByProject(projectId, startDate, endDate);
			
			assertEquals(3, persons.size());
			assertEquals(1, persons.get(0).getUserId().intValue());
			assertEquals(2, persons.get(1).getUserId().intValue());
			assertEquals(3, persons.get(2).getUserId().intValue());
			
		}catch (Exception e) {
			assertTrue(false);
		}
	}
*/
	
	
}
