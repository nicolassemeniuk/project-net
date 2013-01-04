/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/

 /*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 15475 $
|       $Date: 2006-12-10 14:25:36 +0530 (Sun, 10 Dec 2006) $
|     $Author: sjmittal $
|
+----------------------------------------------------------------------*/
package net.project.business.report.projectstatus;

import java.sql.SQLException;

import net.project.base.finder.GroupingIterator;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.project.NoSuchPropertyException;
import net.project.report.AbstractReport;
import net.project.report.ReportComponents;
import net.project.report.ReportException;
import net.project.report.ReportScope;
import net.project.report.ReportType;
import net.project.report.SummaryDetailReportData;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

import org.apache.log4j.Logger;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;

/**
 * The Project Status Report produces a list of projects with thier status for
 * a business and its sub businesses either PDF or XML format.
 * 
 * @author Sachin Mittal
 * @version 1.0
 *
 */
public class ProjectStatusReport extends AbstractReport {

	private static final String ID_COLUMN = PropertyProvider.get("prm.project.columndefs.id");
	private static final String NAME_COLUMN = PropertyProvider.get("prm.project.columndefs.name");
	private static final String DESCRIPTION_COLUMN = PropertyProvider.get("prm.project.columndefs.description");
	private static final String SPONSER_COLUMN = PropertyProvider.get("prm.project.columndefs.sponsor");
	private static final String DATE_START_COLUMN = PropertyProvider.get("prm.project.columndefs.datestart");
	private static final String DATE_FINISH_COLUMN = PropertyProvider.get("prm.project.columndefs.datefinish");
	private static final String WORKPLAN_DATE_START_COLUMN = PropertyProvider.get("prm.project.columndefs.workplandatestart");
	private static final String WORKPLAN_DATE_FINISH_COLUMN = PropertyProvider.get("prm.project.columndefs.workplandatefinish");
	private static final String STATUS_COLUMN = PropertyProvider.get("prm.project.columndefs.status");
    private static final String WORKPLAN_LAST_MODIFIED_COLUMN = PropertyProvider.get("prm.project.columndefs.workplanlastmodified");
    
    private static final String OVERALL_STATUS_COLUMN = PropertyProvider.get("prm.project.portfolio.column.overallstatus.label");
	private static final String FINANCIAL_STATUS_COLUMN = PropertyProvider.get("prm.project.portfolio.column.financialstatus.label");
    private static final String SCHEDULE_STATUS_COLUMN = PropertyProvider.get("prm.project.portfolio.column.schedulestatus.label");
    private static final String RESOURCE_STATUS_COLUMN = PropertyProvider.get("prm.project.portfolio.column.resourcestatus.label");
    private static final String PERCENT_COMPLETE_COLUMN = PropertyProvider.get("prm.project.portfolio.column.percentcomplete.label.title");

    /** Message shown when there are no detailed records to display. */
    private static final String NO_PROJETCS_FOUND = PropertyProvider.get("business.report.projectstatusreport.noprojectsfound.name"); //"No Projects Found";
    /**
     * Token pointing at to an unexpected error message of: "Unexpected error
     * while creating report."
     */
    private String UNEXPECTED_REPORT_ERROR_TOKEN = "prm.report.errors.unexpectedcreationerror.message";
    /**
     * Token pointing to: "Unexpected error populating chart parameters."
     */
    private String UNEXPECTED_CHARTING_ERROR_TOKEN = "prm.report.errors.populatechartparameterserror.message";

    /**
     * Standard constructor.
     */
    public ProjectStatusReport(ReportScope scope) {
        super(new ProjectStatusReportData(), ReportType.PROJECT_STATUS_REPORT, scope);
    }
    
    ProjectStatusReport(SummaryDetailReportData reportData, ReportType reportType, ReportScope scope) {
        super(reportData, reportType, scope);
    }
    
    /**
     * Get a object that represents an XML document.  This will be useful for
     * constructing the XML body and XML outputs.  This method is where we
     * construct that data.
     *
     * @return an XML object that represents that data being aggregated by this
     * object.
     * @throws XMLDocumentException if an internal error occurs while constructing
     * the document.  (This is generally a programmer error.)
     * @throws PersistenceException if there is a database error which occurs
     * while constructing the XML object.
     * @throws SQLException 
     */
	public XMLDocument getXMLDocument() throws XMLDocumentException, PersistenceException, SQLException {
	
		XMLDocument doc = new XMLDocument();
		getData().load();
		
		//Start the output of the late task report
		doc.startElement("projectstatusreport");
		
		//Meta information
		addReportHeader(doc);
		
		//Add the report parameters
		addReportParametersElements(doc);
		
		//Create the detailed data section
		GroupingIterator gi = getData().getGroupingIterator();
		doc.startElement("DetailedData");
		//Iterate through all of the tasks, adding them individually
		while (gi.hasNext()) {
			doc.startElement("ProjectData");
			ProjectWorkplanData currentProject = (ProjectWorkplanData)gi.next();
			//Add a group change, if one exists
			if ((gi.isGroupChanged()) && (gi.getGroupName() != null)) {
				doc.addElement("Group", gi.getGroupName());
			}
			doc.addXMLString(currentProject.projectSpace.getXMLBody());
			doc.addXMLString(currentProject.schedule.getXMLBody());
			doc.endElement();
		}
		//End the details section
		doc.endElement();
		
		//End the project status Report
		doc.endElement();
		
		return doc;
	}

    //##########################################################################
    // PDF Implementation
    //##########################################################################

    /**
     * Construct the project status report in PDF, writing it to the Document object
     * provided to this method.
     *
     * @param document a <code>Document</code> object that we are going to write
     * this object to.
     * @throws net.project.report.ReportException when any exception occurs inside of this method.
     * The message text should give additional detail.
     */
	public void writeReport(Document document) throws ReportException {
        try {
            //Load the data that is going to populate this report
            getData().load();

            //Add meta information stored internally in the PDF.
            addReportMetaInformation(document);
            document.open();

            //Title
            addReportHeader(document);

            //Report Parameters
            if (showReportParameters) {
                document.add(createReportParameters());
            }

            //Detailed Section
            document.add(createDetailedSection());

        } catch (Exception e) {
        	Logger.getLogger(ProjectStatusReport.class).error("An unexpected exception occurred: " + e);
            throw new ReportException(
                PropertyProvider.get(UNEXPECTED_REPORT_ERROR_TOKEN), e);
        }

	}
	
	
    /**
     * Create the detailed section of the report, which consists of a number of
     * rows of task data.
     *
     * @return A <code>Table</code> containing the detailed information.
     * @throws PersistenceException if an error occurs while iterating through
     * the data to produce the rows.
     * @throws DocumentException if the iText libraries are used incorrectly.
     * (Such as specifying a colspan greater than the size of a table.
     */
    private Table createDetailedSection() throws PersistenceException, DocumentException {
    	
    	DateFormat dateFormat = SessionManager.getUser().getDateFormatter();
		
        Table detailedTable = ReportComponents.createDetailedTable(12);
        //Note that the widths don't have to equal one hundred, they are just relative widths
        detailedTable.setWidths(new int[]{10, 15, 8, 8, 8, 8, 8, 6, 4, 6, 8, 20});

        //Add the header to the detailed section
        detailedTable.setDefaultCellBorder(Rectangle.BOTTOM);
        detailedTable.setDefaultCellBorderWidth(3);
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(SPONSER_COLUMN));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(NAME_COLUMN));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(ID_COLUMN));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(DATE_START_COLUMN));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(DATE_FINISH_COLUMN));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(WORKPLAN_DATE_START_COLUMN));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(WORKPLAN_DATE_FINISH_COLUMN));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(STATUS_COLUMN));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(OVERALL_STATUS_COLUMN));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(FINANCIAL_STATUS_COLUMN));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(SCHEDULE_STATUS_COLUMN));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(RESOURCE_STATUS_COLUMN));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(PERCENT_COMPLETE_COLUMN));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(WORKPLAN_LAST_MODIFIED_COLUMN));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(DESCRIPTION_COLUMN));
        detailedTable.endHeaders();
        detailedTable.setDefaultCellBorderWidth(1);

        GroupingIterator gi = getData().getGroupingIterator();

        if (!gi.hasNext()) {
            //There aren't any late tasks.  Let the user know this.
            Cell noDataFoundCell = new Cell(new Phrase(NO_PROJETCS_FOUND, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            noDataFoundCell.setColspan(8);
            detailedTable.addCell(noDataFoundCell);
        } else {
            //Iterate through each matching task, creating a row for each
            while (gi.hasNext()) {
            	ProjectWorkplanData currentProject = (ProjectWorkplanData)gi.next();

                if ((gi.isGroupChanged()) && (gi.getGroupName() != null)) {
                    detailedTable.addCell(ReportComponents.createGroupChangeCell(gi.getGroupName(), 12));
                }

                detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.projectSpace.getSponsor()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.projectSpace.getName()));
                try {
                    detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.projectSpace.getMetaData().getProperty("ExternalProjectID")));
                } catch (NoSuchPropertyException e) {
                    detailedTable.addCell(ReportComponents.createDetailedCell(""));
                }                
                detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.projectSpace.getStartDateString()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.projectSpace.getEndDateString()));
                detailedTable.addCell(ReportComponents.createDetailedCell(dateFormat.formatDate(currentProject.schedule.getScheduleStartDate())));
                detailedTable.addCell(ReportComponents.createDetailedCell(dateFormat.formatDate(currentProject.schedule.getScheduleEndDate())));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.projectSpace.getStatus()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.projectSpace.getColorCode().getHtmlOptionDisplay()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.projectSpace.getFinancialStatusColorCode().getHtmlOptionDisplay()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.projectSpace.getScheduleStatusColorCode().getHtmlOptionDisplay()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.projectSpace.getResourceStatusColorCode().getHtmlOptionDisplay()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.projectSpace.getPercentComplete()));
                detailedTable.addCell(ReportComponents.createDetailedCell(dateFormat.formatDate(currentProject.schedule.getLastModified())));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.projectSpace.getDescription()));

            }
        }

        return detailedTable;
    }


}
