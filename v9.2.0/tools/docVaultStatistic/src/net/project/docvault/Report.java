package net.project.docvault;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class Report {

	private static final Logger log = Logger.getLogger(Report.class);
	
	public enum ReportType{
		PROJECTS,
		BUSINESS,
		USERS
	}

	public void getStatisticReport(Properties properties, ReportType rt) {
		File f = null;
		if (rt == ReportType.PROJECTS){
			f = new File(properties.getProperty("output.filename.location") + File.separator + properties.getProperty("output.filename.projects"));
		}else if (rt == ReportType.BUSINESS){
			f = new File(properties.getProperty("output.filename.location") + File.separator + properties.getProperty("output.filename.business"));
		}else if (rt == ReportType.USERS){
			f = new File(properties.getProperty("output.filename.location") + File.separator + properties.getProperty("output.filename.users"));
		}
		

		OutputStream out = null;

		try {
			out = new FileOutputStream(f);
			Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
			PdfWriter writer = PdfWriter.getInstance(doc, out);

			doc.open();
			Font tableFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

			Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 15);
			PdfPTable tableHeader = new PdfPTable(1);
			int headerWidths[] = { 100 };
			tableHeader.setWidths(headerWidths);
			tableHeader.setWidthPercentage(100);
			tableHeader.getDefaultCell().setPadding(2);
			tableHeader.getDefaultCell().setBorderWidth(0);
			tableHeader.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			tableHeader.setSpacingAfter(15);

			if (rt == ReportType.PROJECTS){
				tableHeader.addCell(new Phrase("DocVault size per project", headerFont));
			}else if (rt == ReportType.BUSINESS){
				tableHeader.addCell(new Phrase("DocVault size per business", headerFont));
			}else if (rt == ReportType.USERS){
				tableHeader.addCell(new Phrase("DocVault size per user", headerFont));
			}
			
			doc.add(tableHeader);
			List projects = getResults(properties, rt);
			if (projects != null && projects.size() > 0) {

				PdfPTable table = new PdfPTable(3);
				int userTableWidths[] = { 10, 45, 15 };
				table.setWidths(userTableWidths);
				table.setWidthPercentage(100);
				table.getDefaultCell().setPadding(2);
				table.getDefaultCell().setBorderWidth(1);
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);

				if (rt == ReportType.PROJECTS){
					table.addCell(new Phrase("Project id", tableFont));
					table.addCell(new Phrase("Project name", tableFont));
					table.addCell(new Phrase("Documents size", tableFont));
				}else if (rt == ReportType.BUSINESS){
					table.addCell(new Phrase("Business id", tableFont));
					table.addCell(new Phrase("Business name", tableFont));
					table.addCell(new Phrase("Documents size", tableFont));
				}else if (rt == ReportType.USERS){
					table.addCell(new Phrase("User id", tableFont));
					table.addCell(new Phrase("Username", tableFont));
					table.addCell(new Phrase("Documents size", tableFont));
				}
				
				table.setHeaderRows(1);
				table.setSpacingAfter(10);

				for (Iterator iterator = projects.iterator(); iterator.hasNext();) {
					Project p = (Project) iterator.next();
					table.addCell(new Phrase(p.getId().toString(), tableFont));
					table.addCell(new Phrase(p.getProjectName(), tableFont));
					table.addCell(new Phrase(FileUtils.byteCountToDisplaySize(p.getSize()), tableFont));
				}
				doc.add(table);
			}
			doc.close();
			writer.flush();
			writer.close();
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
			log.debug(e.getMessage());
		}
	}

	public List getResults(Properties properties, ReportType rt) {
		List results = new ArrayList();
		try {
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
			PreparedStatement ps = null;
			if (rt == ReportType.PROJECTS){
				ps = connection.prepareStatement("SELECT p.project_id, p.project_name from pn_project_space p order by p.project_name");
			}else if (rt == ReportType.BUSINESS){
				ps = connection.prepareStatement("select b.business_id, b.business_name from pn_business b order by b.business_name");
			}else if (rt == ReportType.USERS){
				ps = connection.prepareStatement("select u.user_id, u.username from pn_user u order by u.username");
			}
			ResultSet rs = ps.executeQuery();
			int i = 0;
			Long size = 0l;
			while (rs.next()) {
				Project p = new Project(rs.getInt(1), rs.getString(2), 0l);
				results.add(i++, p);
				List docVaults = getDocVaultLocations(properties);
				for (int j = 0; j < docVaults.size(); j++) {
					File f = new File(docVaults.get(j) + File.separator + p.getId());
					if (f.isDirectory()) {
						size = size + getFolderSize(f);
					}
				}
				p.setSize(size);
				System.out.println(i + ". p:" + p.toString() + "\n");
				size = 0l;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	public List getDocVaultLocations(Properties properties) {
		List results = new ArrayList();
		try {
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

			Statement stmt = connection.createStatement();

			ResultSet rs = stmt.executeQuery("select repository_path from pn_doc_repository_base where is_active = 1");
			int i = 0;
			while (rs.next()) {
				results.add(i++, rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	class Project {

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append("id:").append(id.toString()).append("\n");
			sb.append("projectName:").append(projectName.toString()).append("\n");
			sb.append("size:").append(FileUtils.byteCountToDisplaySize(size)).append("\n");
			return sb.toString();
		}

		public Project(Integer id, String name) {
			this.id = id;
			this.projectName = name;
		}

		public Project(Integer id, String name, Long size) {
			this.id = id;
			this.projectName = name;
			this.size = size;
		}

		private Integer id;

		private String projectName;

		private Long size;

		/**
		 * @return Returns the size.
		 */
		public Long getSize() {
			return size;
		}

		/**
		 * @param size
		 *            The size to set.
		 */
		public void setSize(Long size) {
			this.size = size;
		}

		/**
		 * @return Returns the id.
		 */
		public Integer getId() {
			return id;
		}

		/**
		 * @param id
		 *            The id to set.
		 */
		public void setId(Integer id) {
			this.id = id;
		}

		/**
		 * @return Returns the projectName.
		 */
		public String getProjectName() {
			return projectName;
		}

		/**
		 * @param projectName
		 *            The projectName to set.
		 */
		public void setProjectName(String projectName) {
			this.projectName = projectName;
		}

	}

	public long getFolderSize(File dir) {
		long size = 0;
		for (File file : dir.listFiles()) {
			if (file.isFile())
				size += file.length();
			else
				size += getFolderSize(file);
		}
		return size;
	}

}
