package net.project.hibernate.dao;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import net.project.hibernate.AbstractDaoIntegrationTestBase;
import net.project.hibernate.model.PnResourceList;
import net.project.test.util.TestProperties;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.jdbc.datasource.DataSourceUtils;

public class PnResourceListDAOImplTest extends AbstractDaoIntegrationTestBase{
	
	private static String TEST_DATA_FILE = TestProperties.getInstance().getProperty("mockobject.properties.location") + "/dbunit-test-data.xml";
	
	//protected IPnResourceListDAO dao;
	
	public PnResourceListDAOImplTest() {
		super();
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
            IDatabaseConnection connection = new DatabaseConnection(conn);
            DatabaseOperation.INSERT.execute(connection, new FlatXmlDataSet(new FileInputStream(TEST_DATA_FILE)));
        } finally {
            DataSourceUtils.releaseConnection(conn, ds);
        }		
	}	

	/**
	 * 
	 * @throws Exception
	 */
	public void testGetResourceList() throws Exception {
	    /*try{
			List<PnResourceList> resourceList = dao.getResourceList();
			assertNotNull(resourceList);
			assertTrue(resourceList.size() == 0);
		}catch (Exception e) {
			assertTrue(false);
		}*/
	}		
	
	
	
}
