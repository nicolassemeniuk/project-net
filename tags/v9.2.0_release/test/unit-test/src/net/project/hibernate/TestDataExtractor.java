package net.project.hibernate;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.xml.FlatXmlDataSet;



public class TestDataExtractor {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, DataSetException, FileNotFoundException, IOException {  

		Class.forName("oracle.jdbc.OracleDriver");  
			
		Connection jdbcConnection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:v84dev", "pnet", "pnet");

		IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
		//Enable the qualified table names feature - this must be done when working with Oracle
		DatabaseConfig config = connection.getConfig();
		String id = "http://www.dbunit.org/features/qualifiedTableNames"; 
		config.setFeature(id, true);
		QueryDataSet partialDataSet = new QueryDataSet(connection);
				  
		// Mention all the tables here for which you want data to be extracted  
        //take note of the order to prevent FK constraint violation when re-inserting		
		partialDataSet.addTable("PNET.PN_ASSIGNMENT"); 
		//partialDataSet.addTable("PNET.PN_ASSIGNMENT", " SELECT * FROM PNET.PN_ASSIGNMENT WHERE PERSON_ID=1 ");
		
		// XML file into which data needs to be extracted  
		FlatXmlDataSet.write(partialDataSet, new FileOutputStream("C:/dbunit-test-data.xml")); 
	}
	
}
