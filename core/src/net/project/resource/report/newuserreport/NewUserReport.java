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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.resource.report.newuserreport;

import java.awt.Color;
import java.sql.SQLException;

import net.project.base.finder.GroupingIterator;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.report.AbstractReport;
import net.project.report.ReportComponents;
import net.project.report.ReportException;
import net.project.report.ReportScope;
import net.project.report.ReportType;
import net.project.resource.Invitee;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;

/**
 * Class to create the XML and PDF for the new user report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class NewUserReport extends AbstractReport {
    /** Human-readable text to display for the "Total Number of Invited Users" field. */
    private String TOTAL_NUMBER_OF_INVITED_USERS = PropertyProvider.get("prm.resource.report.newuserreport.totalnumberofinvitedusers.name");
    /** Human-readable text to display for the "Invited in the Last 7 Days" field. */
    private String INVITED_IN_THE_LAST_7_DAYS = PropertyProvider.get("prm.resource.report.newuserreport.invitedinthelast7days.name");
    /** Human-readable text to display for the "Invited in the Last Month" field. */
    private String INVITED_IN_THE_LAST_MONTH = PropertyProvider.get("prm.resource.report.newuserreport.invitedinthelastmonth.name");
    /** Human-readable text to display for the "Responded in the Last 7 Days" field. */
    private String RESPONDED_IN_THE_LAST_7_DAYS = PropertyProvider.get("prm.resource.report.newuserreport.respondedinthelast7days.name");
    /** Human-readable text to display for the "Responded in the Last Month" field. */
    private String RESPONDED_IN_THE_LAST_MONTH = PropertyProvider.get("prm.resource.report.newuserreport.respondedinthelastmonth.name");
    /** Human-readable text to display for the "Invitee" column. */
    private String INVITEE = PropertyProvider.get("prm.resource.report.newuserreport.invitee.name");
    /** Human-readable text to display for the "Email Address" column. */
    private String EMAIL_ADDRESS = PropertyProvider.get("prm.resource.report.newuserreport.emailaddress.name");
    /** Human-readable text to display for the "Invitor" column. */
    private String INVITOR = PropertyProvider.get("prm.resource.report.newuserreport.invitor.name");
    /** Human-readable text to display for the "Date Invited" column. */
    private String DATE_INVITED = PropertyProvider.get("prm.resource.report.newuserreport.dateinvited.name");
    /** Human-readable text to display for the "Date Responded" column. */
    private String DATE_RESPONDED = PropertyProvider.get("prm.resource.report.newuserreport.dateresponded.name");
    /** String to display when no detailed data is found. */
    private String NO_INVITEES_FOUND = PropertyProvider.get("prm.resource.report.newuserreport.noinviteesfound.name");
    /**
     * Token pointing at to an unexpected error message of: "Unexpected error
     * while creating report."
     */
    private String UNEXPECTED_REPORT_ERROR_TOKEN = "prm.report.errors.unexpectedcreationerror.message";


    /**
     * Creates a new <code>NewUserReport</code> instance.
     */
    public NewUserReport(ReportScope scope) {
        super(new NewUserReportData(), ReportType.NEW_USER_REPORT, scope);
    }

    /**
     * Write the report to a PDF document encapsulated by the document object.
     *
     * @param document a <code>com.lowagie.text.Document</code> object that should
     * construct the PDF.
     * @throws ReportException if any exception occurs while trying to construct
     * the report.  Exceptions other than report exception will be wrapped in a
     * report exception and rethrown.
     */
    public void writeReport(Document document) throws ReportException {
        try {
            //Load all of the necessary data needed to construct the report
            getData().load();

            //Add the meta information stored in the properties of a PDF
            addReportMetaInformation(document);
            document.open();

            //Title
            addReportHeader(document);

            //ReportParemeters
            if (showReportParameters)
                document.add(createReportParameters());

            //Summary section
            document.add(createReportSummary());

            //Detailed Section
            document.add(createDetailedSection());
        } catch (Exception e) {
            throw new ReportException(
                PropertyProvider.get(UNEXPECTED_REPORT_ERROR_TOKEN),e);
        }
    }

    /**
     * Create the summary section of the PDF version of the report.
     *
     * @return a <code>Table</code> populated with the summary data.
     * @throws BadElementException if the report writer has constructed the
     * summary section in a way that iText doesn't accept.
     */
    private Table createReportSummary() throws BadElementException {
        //Get the data needed to construct the summary section
        NewUserReportSummaryData summaryData = ((NewUserReportData)getData()).getSummaryData();

        //Create the table that will house the top summary section
        Table summaryTable = ReportComponents.createSummaryTable(3);
        summaryTable.setWidths(new float[]{42f, 8f, 50f});

        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_NUMBER_OF_INVITED_USERS));
        summaryTable.addCell(ReportComponents.createSummaryCell(
            String.valueOf(summaryData.getTotalNumberOfInvitedUsers())));

        Cell chartCell = new Cell();
        chartCell.setBorder(Rectangle.NO_BORDER);
        chartCell.setRowspan(5);
        summaryTable.addCell(chartCell);

        summaryTable.addCell(ReportComponents.createSummaryCell(INVITED_IN_THE_LAST_7_DAYS));
        summaryTable.addCell(ReportComponents.createSummaryCell(
            String.valueOf(summaryData.getInvitedInTheLast7Days())));
        summaryTable.addCell(ReportComponents.createSummaryCell(INVITED_IN_THE_LAST_MONTH));
        summaryTable.addCell(ReportComponents.createSummaryCell(
            String.valueOf(summaryData.getInvitedInTheLastMonth())));
        summaryTable.addCell(ReportComponents.createSummaryCell(RESPONDED_IN_THE_LAST_7_DAYS));
        summaryTable.addCell(ReportComponents.createSummaryCell(
            String.valueOf(summaryData.getRespondedInTheLast7Days())));
        summaryTable.addCell(ReportComponents.createSummaryCell(RESPONDED_IN_THE_LAST_MONTH));
        summaryTable.addCell(ReportComponents.createSummaryCell(
            String.valueOf(summaryData.getRespondedInTheLastMonth())));


        return summaryTable;
    }

    /**
     * Create the "Detailed" section of the new user report.
     *
     * @return a <code>Table</code> object populated with information.
     * @throws DocumentException if the document is assembled in a way not
     * allowed by the iText libraries.
     * @throws PersistenceException if an error occurs loading the data.
     */
    private Table createDetailedSection() throws DocumentException, PersistenceException {
        Table detailedTable = ReportComponents.createDetailedTable(5);

        //Note that the widths don't have to equal one hundred, they are just relative widths
        detailedTable.setWidths(new int[] {15, 20, 15, 10, 10});

        detailedTable.setDefaultCellBorder(Rectangle.BOTTOM);
        detailedTable.setDefaultCellBorderWidth(3);
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(INVITEE));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(EMAIL_ADDRESS));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(INVITOR));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(DATE_INVITED));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(DATE_RESPONDED));
        detailedTable.endHeaders();
        detailedTable.setDefaultCellBorderWidth(1);

        GroupingIterator it = getData().getGroupingIterator();
        if (!it.hasNext()) {
            Cell noDataFoundCell = new Cell(new Phrase(NO_INVITEES_FOUND,
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            noDataFoundCell.setColspan(5);
            detailedTable.addCell(noDataFoundCell);
        } else {
            while (it.hasNext()) {
                Invitee invitee = (Invitee)it.next();

                if ((it.isGroupChanged()) && (it.getGroupName() != null)) {
                    Cell groupingCell = new Cell(new Phrase(it.getGroupName(),
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
                    groupingCell.setBackgroundColor(Color.LIGHT_GRAY);
                    groupingCell.setColspan(8);
                    groupingCell.setBorder(Rectangle.BOX);
                    detailedTable.addCell(groupingCell);
                }

                DateFormat df = SessionManager.getUser().getDateFormatter();

                detailedTable.addCell(ReportComponents.createDetailedCell(invitee.getDisplayName()));
                detailedTable.addCell(ReportComponents.createDetailedCell(invitee.getEmail()));
                detailedTable.addCell(ReportComponents.createDetailedCell(invitee.getInvitorDisplayName()));
                detailedTable.addCell(ReportComponents.createDetailedCell(
                    df.formatDate((invitee.getInvitedDate()))));
                detailedTable.addCell(ReportComponents.createDetailedCell(
                    df.formatDate(invitee.getResponseDate())));
            }
        }
        return detailedTable;
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
        getData().load();

        XMLDocument doc = new XMLDocument();
        doc.startElement("NewUserReport");

        //Add the meta information
        addReportHeader(doc);

        //Add the report parameters
        addReportParametersElements(doc);

        //Add the summary section
        NewUserReportSummaryData summaryData = ((NewUserReportData)getData()).getSummaryData();
        doc.startElement("SummaryData");
        doc.addElement("InvitedUsers", new Integer(summaryData.getTotalNumberOfInvitedUsers()));
        doc.addElement("InvitedInLast7Days", new Integer(summaryData.getInvitedInTheLast7Days()));
        doc.addElement("InvitedInLastMonth", new Integer(summaryData.getInvitedInTheLastMonth()));
        doc.addElement("RespondedInLast7Days", new Integer(summaryData.getRespondedInTheLast7Days()));
        doc.addElement("RespondedInLastMonth", new Integer(summaryData.getRespondedInTheLastMonth()));
        doc.endElement();  //SummaryData

        //Add the detailed section
        GroupingIterator it = getData().getGroupingIterator();
        doc.startElement("DetailedData");
        while (it.hasNext()) {
            doc.startElement("InviteeData");
            Invitee invitee = (Invitee)it.next();

            //Add a group change, if one exists
            if ((it.isGroupChanged()) && (it.getGroupName() != null)) {
                doc.addElement("Group", it.getGroupName());
            }

            //Add the invitee data
            doc.addElement(invitee.getXMLDocument());

            doc.endElement();  //InviteeData
        }
        doc.endElement();


        return doc;
    }
}
