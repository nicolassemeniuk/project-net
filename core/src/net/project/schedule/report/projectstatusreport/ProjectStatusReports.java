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
 |       $Date: 2006-09-26 14:25:36 +0530 (Tue, 26 Sep 2006) $
 |     $Author: avinash $
 |
 +----------------------------------------------------------------------*/
package net.project.schedule.report.projectstatusreport;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

import net.project.base.finder.GroupingIterator;
import net.project.base.property.PropertyProvider;
import net.project.code.ColorCode;
import net.project.code.ImprovementCode;
import net.project.persistence.PersistenceException;
import net.project.report.AbstractReport;
import net.project.report.ReportComponents;
import net.project.report.ReportException;
import net.project.report.ReportScope;
import net.project.report.ReportType;
import net.project.schedule.ScheduleEntry;
import net.project.security.SessionManager;
import net.project.util.NumberFormat;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

import org.apache.log4j.Logger;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;

/**
 * The Project Status Report gives the information about the status of the particular project
 * in either PDF or XML format.
 *
 * @author K B Deepak
 * @since Version 1.0
 */
public class ProjectStatusReports extends AbstractReport {
    /**
     * Label for the "Milestone Name" column.
     */
    private String MILESTONE_NAME = PropertyProvider.get("prm.project.columndefs.projectstatusmilestone");

    /**
     * Label for the "Milestone Start Date" column.
     */
    private String MILESTONE_START = PropertyProvider.get("prm.project.columndefs.projectstatusmilestonestartdate");

    /**
     * Label for the "Milestone Finish Date" column.
     */
    private String MILESTONE_FINISH = PropertyProvider.get("prm.project.columndefs.projectstatusmilestonefinishdate");

    /**
     * Label for the "Milestone Percent Complete" column.
     */
    private String MILESTONE_COMPLETE = PropertyProvider.get("prm.project.columndefs.projectstatusmilestonecomplete");

    /**
     * Label for the "Projct last modified" field.
     */
    private String PROJECT_LAST_UPDATED = PropertyProvider.get("prm.project.columndefs.projectstatuslastmodified");

    /**
     * Label for the "Business Area" field.
     */
    private String BUSINESS_AREA = PropertyProvider.get("prm.project.columndefs.projectstatusbusinessarea");

    /**
     * Label for the "Project Name" field.
     */
    private String PROJECT_NAME = PropertyProvider.get("prm.project.columndefs.projectstatusprojectname");

    /**
     * Label for the "Project Number" field.
     */
    private String PROJECT_NUMBER = PropertyProvider.get("prm.project.columndefs.projectstatusprojectno");

    /**
     * Label for the "Project Description" field.
     */
    private String PROJECT_DESCRIPTION = PropertyProvider.get("prm.project.columndefs.projectstatusprojectdesc");

    /**
     * Label for the "Project Start Date" field.
     */
    private String PROJECT_START_DATE = PropertyProvider.get("prm.project.columndefs.projectstatusprojectstartdate");

    /**
     * Label for the "Project Finish Date" field.
     */
    private String PROJECT_FINISH_DATE = PropertyProvider.get("prm.project.columndefs.projectstatusprojectenddate");

    /**
     * Label for the "Project Overall Complete" field.
     */
    private String PROJECT_OVERALL_COMPLETE = PropertyProvider.get("prm.project.columndefs.projectstatusprojectoverallcomplete");

    /**
     * Label for the "Project Overall Stage" field.
     */
    private String PROJECT_OVERALL_STAGE = PropertyProvider.get("prm.project.columndefs.projectstatusprojectoverallstatus");

    /**
     * Label for the "Project Overall Status" field.
     */
    private String PROJECT_OVERALL_STATUS = PropertyProvider.get("prm.project.columndefs.projectstatusprojectoverallcolor");

    /**
     * Label for the "Project Schedule Status" field.
     */
    private String PROJECT_SCHEDULE_STATUS = PropertyProvider.get("prm.project.columndefs.projectstatusprojectschedulecolor");

    /**
     * Label for the "Project Resource Status" field.
     */
    private String PROJECT_RESOURCE_STATUS = PropertyProvider.get("prm.project.columndefs.projectstatusprojectresourcecolor");

    /**
     * Label for the "Project Financial Status" field.
     */
    private String PROJECT_FINANCIAL_STATUS = PropertyProvider.get("prm.project.columndefs.projectstatusprojectfinancialcolor");

    /**
     * Label for the "Project Status Comments" field.
     */
    private String PROJECT_STATUS_COMMENTS = PropertyProvider.get("prm.project.columndefs.projectstatusprojectcomments");

    /**
     * Message shown when there are no detailed records to display.
     */
    private String NO_MILESTONE_FOUND = PropertyProvider.get("prm.schedule.report.projectstatus.nomilestonefound.name");

    /**
     * Dummy Message shown.
     */
    private String PROJECT_DUMMY = PropertyProvider.get("prm.project.columndefs.dummy");

    /**
     * Token pointing at to an unexpected error message of: "Unexpected error
     * while creating report."
     */
    private String UNEXPECTED_REPORT_ERROR_TOKEN = "prm.report.errors.unexpectedcreationerror.message";

    /**
     * Standard constructor.
     */
    public ProjectStatusReports(ReportScope scope) {
        super(new ProjectStatusReportsData(), ReportType.PROJECT_STATUS_REPORTS, scope);
    }

    /**
     * Get a object that represents an XML document.  This will be useful for
     * constructing the XML body and XML outputs.  This method is where we
     * construct that data.
     *
     * @return an XML object that represents that data being aggregated by this
     *         object.
     * @throws XMLDocumentException if an internal error occurs while constructing
     *                              the document.  (This is generally a programmer error.)
     * @throws PersistenceException if there is a database error which occurs
     *                              while constructing the XML object.
     * @throws SQLException
     */
    public XMLDocument getXMLDocument() throws XMLDocumentException, PersistenceException, SQLException {
        XMLDocument doc = new XMLDocument();
        getData().load();

        //Start the output of the late task report
        doc.startElement("latetaskreport");

        //Meta information
        addReportHeader(doc);

        //Add the report parameters
        addReportParametersElements(doc);

        //Create the summary section
        ProjectStatusReportsSummaryData summaryData = ((ProjectStatusReportsData) getData()).getSummaryData();
        doc.startElement("SummaryData");
        doc.addElement("DateModified", summaryData.getProjectLastUpdated());
        doc.addElement("BusinessName", summaryData.getProjectBusinessArea());
        doc.addElement("Name", summaryData.getProjectName());
        doc.addElement("Number", summaryData.getProjectNumber());
        doc.addElement("Description", summaryData.getProjectDescription());
        doc.addElement("StartDate", summaryData.getProjectStartDate());
        doc.addElement("FinishDate", summaryData.getProjectFinishDate());
        doc.addElement("OverallComplete", formatProjectOverralComplete(summaryData));
        doc.addElement("OverallStage", summaryData.getProjectOverallStage());
        doc.startElement("OverallStatus");
        addStatusXML(doc, summaryData.getImprovementCode(), summaryData.getColorCode());
        doc.endElement();
        doc.startElement("FinancialStatus");
        addStatusXML(doc, summaryData.getFinancialStatusImprovementCode(), summaryData.getFinancialStatusColorCode());
        doc.endElement();
        doc.startElement("ScheduleStatus");
        addStatusXML(doc, summaryData.getScheduleStatusImprovementCode(), summaryData.getScheduleStatusColorCode());
        doc.endElement();
        doc.startElement("ResourceStatus");
        addStatusXML(doc, summaryData.getResourceStatusImprovementCode(), summaryData.getResourceStatusColorCode());
        doc.endElement();
        doc.addElement("Comments", summaryData.getProjectStatusComments());
        doc.endElement();

        //Create the detailed data section
        GroupingIterator gi = getData().getGroupingIterator();
        doc.startElement("DetailedData");
        //Iterate through all of the tasks, adding them individually
        while (gi.hasNext()) {
            doc.startElement("TaskData");
            ScheduleEntry currentTask = (ScheduleEntry) gi.next();
            //Add a group change, if one exists
            if ((gi.isGroupChanged()) && (gi.getGroupName() != null)) {
                doc.addElement("Group", gi.getGroupName());
            }
            doc.addXMLString(currentTask.getXMLBody());
            doc.endElement();
        }
        doc.endElement();

        //End the late Task Report
        doc.endElement();

        return doc;
    }

    //##########################################################################
    // PDF Implementation
    //##########################################################################

    /**
     * Construct the late task report in PDF, writing it to the Document object
     * provided to this method.
     *
     * @param document a <code>Document</code> object that we are going to write
     *                 this object to.
     * @throws net.project.report.ReportException
     *          when any exception occurs inside of this method.
     *          The message text should give additional detail.
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

            //Summary Section
            document.add(createReportSummary());

            //Detailed Section
            document.add(createDetailedSection());

        } catch (Exception e) {
            Logger.getLogger(ProjectStatusReports.class).error("An unexpected exception occurred: " + e);
            throw new ReportException(PropertyProvider.get(UNEXPECTED_REPORT_ERROR_TOKEN), e);
        }
    }

    /**
     * Create the "Summary" section of the report that shows roll up numbers
     * that summarize the data shown in the detailed section.
     *
     * @return A <code>Table</code> that contains the summary information.
     * @throws BadElementException if the iText libraries are used incorrectly.
     *                             (Such as specifying a colspan greater than the size of a table.
     * @throws IOException
     */
    private Table createReportSummary() throws BadElementException, IOException {
        ProjectStatusReportsSummaryData summaryData = ((ProjectStatusReportsData) getData()).getSummaryData();

        //Create the table that will house the top summary section
        Table summaryTable = ReportComponents.createSummaryTable(5, 13);
        summaryTable.setWidths(new float[] { 20f, 10f, 10f, 20f, 10f });

        //Add the first row of the cell.  This row differs from the others
        //because it also contains the chart cell.  Seems how the chart cell will
        //span all of the rows, the rest of the rows will only appear to have a
        //single cell, even though they actually have two.
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_LAST_UPDATED));
        summaryTable.addCell(ReportComponents.createSummaryCell(SessionManager.getUser().getDateFormatter().formatDate(summaryData.getProjectLastUpdated())));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(BUSINESS_AREA));
        summaryTable.addCell(ReportComponents.createSummaryCell(summaryData.getProjectBusinessArea() == null ? PROJECT_DUMMY : summaryData.getProjectBusinessArea()));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_NAME));
        summaryTable.addCell(ReportComponents.createSummaryCell(summaryData.getProjectName()));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_NUMBER));
        summaryTable.addCell(ReportComponents.createSummaryCell(summaryData.getProjectNumber()));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DESCRIPTION));
        summaryTable.addCell(ReportComponents.createSummaryCell(summaryData.getProjectDescription() == null ? PROJECT_DUMMY : summaryData.getProjectDescription()));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_START_DATE));
        summaryTable.addCell(ReportComponents.createSummaryCell(SessionManager.getUser().getDateFormatter().formatDate(summaryData.getProjectStartDate())));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_FINISH_DATE));
        summaryTable.addCell(ReportComponents.createSummaryCell(SessionManager.getUser().getDateFormatter().formatDate(summaryData.getProjectFinishDate())));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_OVERALL_COMPLETE));
        BigDecimal overallComp = new BigDecimal(formatProjectOverralComplete(summaryData));
        overallComp = overallComp.movePointLeft(2);
        summaryTable.addCell(ReportComponents.createSummaryCell(NumberFormat.getInstance().formatPercent(overallComp.floatValue())));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_OVERALL_STAGE));
        summaryTable.addCell(ReportComponents.createSummaryCell(summaryData.getProjectOverallStage() == null ? PROJECT_DUMMY : summaryData.getProjectOverallStage()));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_OVERALL_STATUS));
        summaryTable.addCell(ReportComponents.createSummaryCell(summaryData.getColorCode() == null ? PROJECT_DUMMY : summaryData.getColorCode().getHtmlOptionDisplay()));
        summaryTable.addCell(ReportComponents.createSummaryCell(summaryData.getImprovementCode() == null ? PROJECT_DUMMY : summaryData.getImprovementCode().getHtmlOptionDisplay()));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_SCHEDULE_STATUS));
        summaryTable.addCell(ReportComponents.createSummaryCell(summaryData.getScheduleStatusColorCode() == null ? PROJECT_DUMMY : summaryData.getScheduleStatusColorCode().getHtmlOptionDisplay()));
        summaryTable.addCell(ReportComponents.createSummaryCell(summaryData.getScheduleStatusImprovementCode() == null ? PROJECT_DUMMY : summaryData.getScheduleStatusImprovementCode().getHtmlOptionDisplay()));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_RESOURCE_STATUS));
        summaryTable.addCell(ReportComponents.createSummaryCell(summaryData.getResourceStatusColorCode() == null ? PROJECT_DUMMY : summaryData.getResourceStatusColorCode().getHtmlOptionDisplay()));
        summaryTable.addCell(ReportComponents.createSummaryCell(summaryData.getResourceStatusImprovementCode() == null ? PROJECT_DUMMY : summaryData.getResourceStatusImprovementCode().getHtmlOptionDisplay()));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_FINANCIAL_STATUS));
        summaryTable.addCell(ReportComponents.createSummaryCell(summaryData.getFinancialStatusColorCode() == null ? PROJECT_DUMMY : summaryData.getFinancialStatusColorCode().getHtmlOptionDisplay()));
        summaryTable.addCell(ReportComponents.createSummaryCell(summaryData.getFinancialStatusImprovementCode() == null ? PROJECT_DUMMY : summaryData.getFinancialStatusImprovementCode().getHtmlOptionDisplay()));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_DUMMY));
        summaryTable.addCell(ReportComponents.createSummaryCell(PROJECT_STATUS_COMMENTS));
        summaryTable.addCell(ReportComponents.createSummaryCell(summaryData.getProjectStatusComments() == null ? PROJECT_DUMMY : summaryData.getProjectStatusComments()));
        return summaryTable;
    }

    /**
     * Create the detailed section of the report, which consists of a number of
     * rows of task data.
     *
     * @return A <code>Table</code> containing the detailed information.
     * @throws PersistenceException if an error occurs while iterating through
     *                              the data to produce the rows.
     * @throws DocumentException    if the iText libraries are used incorrectly.
     *                              (Such as specifying a colspan greater than the size of a table.
     */
    private Table createDetailedSection() throws PersistenceException, DocumentException {
        Table detailedTable = ReportComponents.createDetailedTable(4);
        //Note that the widths don't have to equal one hundred, they are just relative widths
        detailedTable.setWidths(new int[] { 20, 12, 12, 10 });

        //Add the header to the detailed section
        detailedTable.setDefaultCellBorder(Rectangle.BOTTOM);
        detailedTable.setDefaultCellBorderWidth(3);
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(MILESTONE_NAME));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(MILESTONE_START));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(MILESTONE_FINISH));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(MILESTONE_COMPLETE));
        detailedTable.endHeaders();
        detailedTable.setDefaultCellBorderWidth(1);

        GroupingIterator taskIterator = getData().getGroupingIterator();

        if (!taskIterator.hasNext()) {
            //There aren't any late tasks.  Let the user know this.
            Cell noDataFoundCell = new Cell(new Phrase(NO_MILESTONE_FOUND, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            noDataFoundCell.setColspan(4);
            detailedTable.addCell(noDataFoundCell);
        } else {
            //Iterate through each matching task, creating a row for each
            while (taskIterator.hasNext()) {
                ScheduleEntry currentTask = (ScheduleEntry) taskIterator.next();

                if ((taskIterator.isGroupChanged()) && (taskIterator.getGroupName() != null)) {
                    detailedTable.addCell(ReportComponents.createGroupChangeCell(taskIterator.getGroupName(), 4));
                }

                detailedTable.addCell(ReportComponents.createDetailedCell(currentTask.getName()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentTask.getStartTimeString()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentTask.getEndTimeString()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentTask.getWorkPercentComplete()));
            }
        }

        return detailedTable;
    }

    /**
     * Gets the chartCell attribute of the projectstatusreport object.
     *
     * @return a <code>void</code>.
     * @throws XMLDoucmentException.
     */
    public void addStatusXML(XMLDocument doc, ImprovementCode improvementCode, ColorCode colorCode) throws XMLDocumentException {
        boolean needAppendImprovement = (improvementCode != null);
        boolean needAppendColor = (colorCode != null);
        if (needAppendImprovement) {
            doc.addElement(improvementCode.getXMLDocument());
            if ((needAppendColor)) {
                doc.addElement(colorCode.getXMLDocument());
                doc.startElement("ImageURL");
                doc.addValue(improvementCode.getImageURL(colorCode));
                doc.endElement();
            }
        }
    }

    private String formatProjectOverralComplete(ProjectStatusReportsSummaryData value) {
        return value.getProjectOverallComplete() == null ? "0" : value.getProjectOverallComplete();
    }
}
