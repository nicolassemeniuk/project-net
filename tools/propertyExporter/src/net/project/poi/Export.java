package net.project.poi;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Reader;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

public class Export {
	
	private HSSFFont getFontStyles(HSSFWorkbook wb){
		HSSFFont font = wb.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setColor(HSSFColor.RED.index);
		
		return font;
	}
	
	public void exportPropertiesTranslators(Properties properties) {
		 try {
	            // redirect output into file
	            FileOutputStream fos = new FileOutputStream(properties
	                    .getProperty("output.filename.location")
	                    + File.separator + "export.log");
	            PrintStream sysOut = new PrintStream(fos);
	            System.setErr(sysOut);
	            System.setOut(sysOut);

	            String driverName = "oracle.jdbc.driver.OracleDriver";
	            Class.forName(driverName);

	            // Create a connection to the database
	            String serverName = properties.getProperty("server.name");
	            String portNumber = properties.getProperty("database.port.number");
	            String sid = properties.getProperty("database.sid");
	            String url = "jdbc:oracle:thin:@" + serverName + ":" + portNumber
	                    + ":" + sid;
	            String username = properties.getProperty("database.user.username");
	            String password = properties.getProperty("database.user.password");
	            Connection connection = DriverManager.getConnection(url, username,
	                    password);
	            PreparedStatement ps = connection
	                    .prepareStatement("SELECT PROPERTY, PROPERTY_TYPE, PROPERTY_VALUE_CLOB, PROPERTY_VALUE, LANGUAGE, IS_TRANSLATABLE_PROPERTY"
	                            + " FROM PN_PROPERTY WHERE PROPERTY_TYPE IN ('text', 'largetext') AND LANGUAGE = ? ORDER BY PROPERTY_TYPE, PROPERTY ");
	            ps.setString(1, "en");

	            //Prepared Statement for retrieving the value of a Property
	            PreparedStatement ps2 = connection
	            .prepareStatement("SELECT PROPERTY_VALUE_CLOB, PROPERTY_VALUE, PROPERTY, LANGUAGE FROM PN_PROPERTY WHERE PROPERTY = ? AND LANGUAGE = ? ");
	            ps2.setString(2, properties.getProperty("language"));

	            ResultSet rs = ps.executeQuery();
	            
	            short rownum = (short) 0;
	            FileOutputStream out = new FileOutputStream(properties
	                    .getProperty("output.filename.location")
	                    + File.separator
	                    + properties.getProperty("output.filename"));
	            // create a new workbook
	            HSSFWorkbook wb = new HSSFWorkbook();
	            // create a new sheet
	            HSSFSheet s = wb.createSheet();
	            s.autoSizeColumn((short)1);
	            // declare a row object reference
	            HSSFRow r = null;
	            // declare a cell object reference
	            HSSFCell c = null;
	            
	            while (rs.next()) {
	            	
	            	Clob english_clob = rs.getClob("PROPERTY_VALUE_CLOB");
	                String english_value = rs.getString("PROPERTY_VALUE");
	                 
	                r = s.createRow(rownum);
	                c = r.createCell((short) 0);
	                String property = rs
	                .getString("PROPERTY");
	                
	                c
	                        .setCellValue(new HSSFRichTextString(property));

	                String propertyType = rs.getString("PROPERTY_TYPE");
	                c = r.createCell((short) 1);
	                c.setCellValue(new HSSFRichTextString(propertyType));

	                c = r.createCell((short) 2);
	                
	                ps2.setString(1, property);
	                ResultSet rs2 = ps2.executeQuery();
	                
	                Clob not_english_clob = null;
	                String not_english_value = null;
	                while (rs2.next()) {
	                	not_english_clob = rs2.getClob("PROPERTY_VALUE_CLOB");
	                	not_english_value = rs2.getString("PROPERTY_VALUE");
					}
	                
	                if ("largetext".equals(propertyType)) {

	                    System.out.println("rs.getString(PROPERTY):"
	                            + rs.getString("PROPERTY"));
	                    
	                    if (not_english_clob != null) {
	                        Reader reader = not_english_clob.getCharacterStream();
	                        CharArrayWriter writer = new CharArrayWriter();
	                        int k = -1;

	                        while ((k = reader.read()) != -1) {
	                            writer.write(k);
	                        }
	                        c.setCellValue(new HSSFRichTextString(new String(writer
	                                .toCharArray())));
	                    } else {
	                    	HSSFCellStyle style = wb.createCellStyle();
	    	                style.setFillBackgroundColor(HSSFColor.RED.index);
	    	                style.setFont(getFontStyles(wb));
	    	                c.setCellStyle(style);
	    	                
	    	                HSSFPatriarch patr = s.createDrawingPatriarch();
	    	                HSSFComment comment = patr.createComment(new HSSFClientAnchor(0,0,0,0,(short)4, 2, (short)6,5));
	    	                comment.setString(new HSSFRichTextString("English Version"));
	    	                c.setCellComment(comment);
	    	                
	    	                if(english_clob != null) {
	    	                	Reader reader = english_clob.getCharacterStream();
		                        CharArrayWriter writer = new CharArrayWriter();
		                        int k = -1;

		                        while ((k = reader.read()) != -1) {
		                            writer.write(k);
		                        }
		                        c.setCellValue(new HSSFRichTextString(new String(writer
		                                .toCharArray())));
	    	                } else {
	    	                	c.setCellValue(new HSSFRichTextString("Not English Value Setted"));
	    	                }
	                    }

	                } else {
	                	
	                	if(not_english_value != null) { 
	                		HSSFRichTextString richValue = new HSSFRichTextString(not_english_value);
		                    c.setCellValue(richValue);
	                	} else {
	                		HSSFCellStyle style = wb.createCellStyle();
			                style.setFillBackgroundColor(HSSFColor.RED.index);
			                style.setFont(getFontStyles(wb));
			                c.setCellStyle(style);
			                
			                HSSFPatriarch patr = s.createDrawingPatriarch();
			                HSSFComment comment = patr.createComment(new HSSFClientAnchor(0,0,0,0,(short)4, 2, (short)6,5));
			                comment.setString(new HSSFRichTextString("English Version"));
			                c.setCellComment(comment);
			                
		                	HSSFRichTextString richValue = new HSSFRichTextString(english_value);
		                    c.setCellValue(richValue);
	                	}
	                }

	                c = r.createCell((short) 3);
	                c
	                        .setCellValue(new HSSFRichTextString(properties.getProperty("language")));

	                c = r.createCell((short) 4);
	                c.setCellValue(rs.getInt("IS_TRANSLATABLE_PROPERTY"));

	                rownum++;
	            }
	            wb.write(out);
	            out.close();
	            System.out.println("Export performed properly. " + rownum
	                    + " records are exported.");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}

    public void exportProperties(Properties properties) {
    	try {
			// redirect output into file
			FileOutputStream fos = new FileOutputStream(properties.getProperty("output.filename.location") + File.separator + "export.log");
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
			Connection connection = DriverManager.getConnection(url, username, password);
			PreparedStatement ps = connection.prepareStatement("SELECT PROPERTY, PROPERTY_TYPE, PROPERTY_VALUE_CLOB, PROPERTY_VALUE, LANGUAGE, IS_TRANSLATABLE_PROPERTY"
					+ " FROM PN_PROPERTY WHERE PROPERTY_TYPE IN ('text', 'largetext') ORDER BY PROPERTY_TYPE, PROPERTY ");
			ResultSet rs = ps.executeQuery();

			short rownum = (short) 0;
			FileOutputStream out = new FileOutputStream(properties.getProperty("output.filename.location") + File.separator + properties.getProperty("output.filename"));
			// create a new workbook
			HSSFWorkbook wb = new HSSFWorkbook();
			// create a new sheet
			HSSFSheet s = wb.createSheet();
			// declare a row object reference
			HSSFRow r = null;
			// declare a cell object reference
			HSSFCell c = null;
			while (rs.next()) {
				r = s.createRow(rownum);
				c = r.createCell((short) 0);
				c.setCellValue(new HSSFRichTextString(rs.getString("PROPERTY")));

				String propertyType = rs.getString("PROPERTY_TYPE");
				c = r.createCell((short) 1);
				c.setCellValue(new HSSFRichTextString(propertyType));

				c = r.createCell((short) 2);
				if ("largetext".equals(propertyType)) {
					Clob clob = rs.getClob("PROPERTY_VALUE_CLOB");
					System.out.println("rs.getString(PROPERTY):" + rs.getString("PROPERTY"));
					if (clob != null) {
						Reader reader = clob.getCharacterStream();
						CharArrayWriter writer = new CharArrayWriter();
						int k = -1;

						while ((k = reader.read()) != -1) {
							writer.write(k);
						}
						c.setCellValue(new HSSFRichTextString(new String(writer.toCharArray())));
					} else {
						c.setCellValue(new HSSFRichTextString(""));
					}

				} else {
					c.setCellValue(new HSSFRichTextString(rs.getString("PROPERTY_VALUE")));
				}

				c = r.createCell((short) 3);
				c.setCellValue(new HSSFRichTextString(rs.getString("LANGUAGE")));

				c = r.createCell((short) 4);
				c.setCellValue(rs.getInt("IS_TRANSLATABLE_PROPERTY"));

				rownum++;
			}
			wb.write(out);
			out.close();
			System.out.println("Export performed properly. " + rownum + " records are exported.");
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}
