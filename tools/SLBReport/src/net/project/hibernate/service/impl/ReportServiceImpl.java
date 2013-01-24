package net.project.hibernate.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.hibernate.dao.IReportsDAO;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.reports.PnProjectActivity;
import net.project.hibernate.model.reports.PnUserActivity;
import net.project.hibernate.service.IReportService;

import org.apache.log4j.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class ReportServiceImpl implements IReportService {

	/**
	 * The application logger.
	 */
	private static final Logger log = Logger.getLogger(ReportServiceImpl.class);	
	
	private IReportsDAO reportDAO;
		
	public void setReportDAO(IReportsDAO reportDAO) {
		this.reportDAO = reportDAO;
	}


	public PnPerson getPerson(BigDecimal personId) {
		return reportDAO.getPerson(personId);
	}


	public OutputStream createProjectActivityReport() {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			
			List projects = new ArrayList();
			projects = reportDAO.getProjectActivity();
			
			Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
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
			
			tableHeader.addCell(new Phrase("PROJECT ACTIVITY REPORT" , headerFont));
			doc.add(tableHeader);
			
			if (projects != null && projects.size() > 0) {
				
				PdfPTable table = new PdfPTable(6);
				int userTableWidths[] = { 25, 10, 15, 15, 10, 25 };
				table.setWidths(userTableWidths);
				table.setWidthPercentage(100);
				table.getDefaultCell().setPadding(2);
				table.getDefaultCell().setBorderWidth(1);
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
				
				table.addCell(new Phrase("Project name", tableFont));				
				table.addCell(new Phrase("Project ID", tableFont));
				table.addCell(new Phrase("Last access time", tableFont));				
				table.addCell(new Phrase("Space admin name", tableFont));
				table.addCell(new Phrase("Phone number", tableFont));
				table.addCell(new Phrase("Email address", tableFont));
				
				table.setHeaderRows(1);
				table.setSpacingAfter(10);	
				
//				for (PnProjectActivity projectActivity : projects) {
				for (Iterator iterator = projects.iterator(); iterator.hasNext();) {
					PnProjectActivity projectActivity = (PnProjectActivity) iterator.next();
					
					table.addCell(new Phrase(projectActivity.getProjectName(), tableFont));
					table.addCell(new Phrase(String.valueOf(projectActivity.getProjectId()), tableFont));
					table.addCell(new Phrase(dateFormatter.format(projectActivity.getLastAccess()), tableFont));
					table.addCell(new Phrase(projectActivity.getLastName().concat(" ").concat(projectActivity.getFirstName()), tableFont));
					table.addCell(new Phrase(projectActivity.getPhoneNumber(), tableFont));
					table.addCell(new Phrase(projectActivity.getEmail(), tableFont));
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

		
		return out;
	}

	public OutputStream createUserActivityReport() {	
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();		
		
		try {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			
			List users = reportDAO.getUsersActivity();
			List nullUsers = reportDAO.getUsersActivityNulls();
			
			Document doc = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
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
			
			tableHeader.addCell(new Phrase("USER ACTIVITY REPORT" , headerFont));
			doc.add(tableHeader);
			
			if (users != null && users.size() > 0) {
				
				PdfPTable table = new PdfPTable(5);
				int userTableWidths[] = { 30, 25, 20, 15, 10 };
				table.setWidths(userTableWidths);
				table.setWidthPercentage(100);
				table.getDefaultCell().setPadding(2);
				table.getDefaultCell().setBorderWidth(1);
				table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
				
				table.addCell(new Phrase("Name", tableFont));				
				table.addCell(new Phrase("Email address", tableFont));
				table.addCell(new Phrase("Last login", tableFont));
				table.addCell(new Phrase("Home phone number", tableFont));
				table.addCell(new Phrase("User status", tableFont));
				
				table.setHeaderRows(1);
				table.setSpacingAfter(10);	
				
				for (Iterator iterator = users.iterator(); iterator.hasNext();) {
					PnUserActivity userActivity = (PnUserActivity) iterator.next();
					table.addCell(new Phrase(userActivity.getLastName().concat(" ").concat(userActivity.getFirstName()), tableFont));
					table.addCell(new Phrase(userActivity.getEmail(), tableFont));
					table.addCell(new Phrase(userActivity.getLastLogin() == null ? "UNKNOWN" : dateFormatter.format(userActivity.getLastLogin()), tableFont));
					table.addCell(new Phrase(userActivity.getPhoneNumber(), tableFont));
					table.addCell(new Phrase(userActivity.getUserStatus(), tableFont));					
				}
				
				for (Iterator iterator = nullUsers.iterator(); iterator.hasNext();) {
					PnUserActivity userActivity = (PnUserActivity) iterator.next();
					table.addCell(new Phrase(userActivity.getLastName().concat(" ").concat(userActivity.getFirstName()), tableFont));
					table.addCell(new Phrase(userActivity.getEmail(), tableFont));
					table.addCell(new Phrase("UNKNOWN" , tableFont));
					table.addCell(new Phrase(userActivity.getPhoneNumber(), tableFont));
					table.addCell(new Phrase(userActivity.getUserStatus(), tableFont));					
				}				
				
/*				for (PnUserActivity userActivity : users) {
					table.addCell(new Phrase(userActivity.getLastName().concat(" ").concat(userActivity.getFirstName()), tableFont));
					table.addCell(new Phrase(userActivity.getEmail(), tableFont));
					table.addCell(new Phrase(dateFormatter.format(userActivity.getLastLogin()), tableFont));
					table.addCell(new Phrase(userActivity.getPhoneNumber(), tableFont));
					table.addCell(new Phrase(userActivity.getUserStatus(), tableFont));
				}*/
				
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

		
		return out;
	}

}
