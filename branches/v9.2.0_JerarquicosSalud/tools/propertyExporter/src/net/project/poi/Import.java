package net.project.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class Import {

	@SuppressWarnings("unchecked")
	public void importProperties(Properties properties) {

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			// redirect output into file
			FileOutputStream fos = new FileOutputStream(properties.getProperty("input.filename.location") + File.separator + "import.log");
			PrintStream sysOut = new PrintStream(fos);
			System.setErr(sysOut);
			System.setOut(sysOut);

			String driverName = "oracle.jdbc.driver.OracleDriver";
			Class.forName(driverName);

			// Create a connection to the database
			String serverName = properties.getProperty("server.name");
			String portNumber = properties.getProperty("database.port.number");
			String sid = properties.getProperty("database.sid");
			String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber + ":" + sid;
			String username = properties.getProperty("database.user.username");
			String password = properties.getProperty("database.user.password");
			connection = DriverManager.getConnection(url, username, password);

			InputStream inputStream = null;

			try {
				inputStream = new FileInputStream(properties.getProperty("input.filename.location") + File.separator + properties.getProperty("input.filename"));
			} catch (FileNotFoundException e) {
				System.out.println("File not found in the specified path.");
				e.printStackTrace();
			}

			POIFSFileSystem fileSystem = null;

			try {
				fileSystem = new POIFSFileSystem(inputStream);
//
				HSSFWorkbook workBook = new HSSFWorkbook(fileSystem);
				HSSFSheet sheet = workBook.getSheetAt(0);
				Iterator<HSSFRow> rows = sheet.rowIterator();
				int rowNumber = 1;
				int displayCount = 100;
				while (rows.hasNext()) {
					try {
						HSSFRow row = rows.next();
						if ("true".equals(properties.getProperty("update.properties"))) {
							ps = connection.prepareStatement("UPDATE PN_PROPERTY SET PROPERTY_VALUE = ?, PROPERTY_VALUE_CLOB = ? WHERE  " +
									" CONTEXT_ID = 2000 AND  LANGUAGE = ? AND "
									+ " PROPERTY = ? ");
							// set property value, property_value_clob
							HSSFCell cell = row.getCell(1);
							HSSFRichTextString cellRich = cell.getRichStringCellValue();
							String type = StringUtils.EMPTY;
							if (cellRich != null) {
								type = cellRich.toString();
							} 
							if ("text".equals(type) || "largetext".equals(type)) {
								cell = row.getCell(2);
								
								if (cell != null && cell.getRichStringCellValue() != null && cell.getRichStringCellValue().length() > 0) {
									StringReader reader = new StringReader(cell.getRichStringCellValue().toString());
									ps.setCharacterStream(2, reader, cell.getRichStringCellValue().toString().length());
									ps.setString(1, cell.getRichStringCellValue().toString());
								} else {
									ps.setString(1, null);
									ps.setCharacterStream(2, null, 0);
								}
							} else {
								cell = row.getCell(2);
								if (cell != null && cell.getRichStringCellValue() != null) {
									ps.setString(1, cell.getRichStringCellValue().toString());
								} else {
									ps.setString(1, null);
								}
								ps.setCharacterStream(2, null, 0);
							}
							
							// set language
							cell = row.getCell(3);
							if ("true".equals(properties.getProperty("is.language.specified.in.property.file"))) {
								ps.setString(3, properties.getProperty("language"));
							} else {
								ps.setString(3, cell.getRichStringCellValue().toString());
							}
							// set property
							cell = row.getCell(0);
							ps.setString(4, cell.getRichStringCellValue().toString());

							ps.execute();
							if (rowNumber >= displayCount) {
								System.out.println(rowNumber + " rows updated.");
								displayCount = displayCount + 100;
							}
							rowNumber++;

						} else {
							ps = connection.prepareStatement("INSERT INTO PN_PROPERTY(CONTEXT_ID, PROPERTY, PROPERTY_TYPE, "
									+ "PROPERTY_VALUE, PROPERTY_VALUE_CLOB, LANGUAGE, IS_TRANSLATABLE_PROPERTY, IS_SYSTEM_PROPERTY, RECORD_STATUS)"
									+ " VALUES (2000, ?, ?, ?, ?, ?, ?, 0, 'A') ");
							// property
							HSSFCell cell = row.getCell(0);
							ps.setString(1, cell.getRichStringCellValue().toString());
							// property_type
							cell = row.getCell(1);
							ps.setString(2, cell.getRichStringCellValue().toString());

							String propertyType = cell.getRichStringCellValue().toString();
							cell = row.getCell(2);
							if ("text".equals(propertyType)) {
								String str = StringUtils.EMPTY;
								try {
									str = cell.getRichStringCellValue().toString();
								} catch (Exception ie) {
									try {
									str = String.valueOf(cell.getNumericCellValue());
									} catch (NullPointerException npe) {
										str = StringUtils.EMPTY;
									}
								}
								ps.setString(3, str);
								ps.setCharacterStream(4, null, 0);
							} else {
								ps.setString(3, null);
								if (cell != null && cell.getRichStringCellValue() != null && cell.getRichStringCellValue().length() > 0) {
									StringReader reader = new StringReader(cell.getRichStringCellValue().toString());
									ps.setCharacterStream(4, reader, cell.getRichStringCellValue().toString().length());
								} else {
									ps.setCharacterStream(4, null, 0);
								}
							}
							// set language
							cell = row.getCell(3);
							if ("true".equals(properties.getProperty("is.language.specified.in.property.file"))) {
								ps.setString(5, properties.getProperty("language"));
							} else {
								ps.setString(5, cell.getRichStringCellValue().toString());
							}
							
							cell = row.getCell(4);
							String str = StringUtils.EMPTY;
							try {
								str = String.valueOf(cell.getNumericCellValue());
							} catch (IllegalStateException ie) {
								str = cell.getRichStringCellValue().toString();
							} 
							int index = str.indexOf(".");
							if (index != -1) {
								str = str.substring(0, index);
							}
							
							int value = Integer.parseInt(str);
							ps.setInt(6, value );
							ps.execute();
							if (rowNumber >= displayCount) {
								System.out.println(rowNumber + " rows inserted.");
								displayCount = displayCount + 100;
							}
							rowNumber++;
						}
					} catch (Exception e) {
						System.err.println("Error at line: " + rowNumber);
						e.printStackTrace();
					} finally {
						if ( ps != null) {
							ps.close();
						}
					}
				}
				if ("true".equals(properties.getProperty("update.properties"))) {
					System.out.println((rowNumber - 1) + " rows updated.");
				} else {
					System.out.println((rowNumber - 1) + " rows inserted.");
				}
			} catch (IOException e) {
				System.out.println("File not found in the specified path.");
				e.printStackTrace();
			}
			System.out.println("Import complete.");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				if (!connection.isClosed()) {
					connection.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}