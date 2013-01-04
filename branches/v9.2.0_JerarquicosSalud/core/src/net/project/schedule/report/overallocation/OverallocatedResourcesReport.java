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
package net.project.schedule.report.overallocation;

import java.math.BigDecimal;
import java.sql.SQLException;

import net.project.base.finder.GroupingIterator;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.report.AbstractReport;
import net.project.report.ReportComponents;
import net.project.report.ReportException;
import net.project.report.ReportScope;
import net.project.report.ReportType;
import net.project.resource.ResourceAssignment;
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
 * Class to construct a PDF version of the Overallocated resources report or the
 * XML to produce the HTML in combination with an XSL stylesheet.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class OverallocatedResourcesReport extends AbstractReport {
    /** Human-readable string for the "Total Number of Overallocated Days" Field. */
    private String TOTAL_NUMBER_OF_OVERALLOCATED_DAYS = PropertyProvider.get("prm.schedule.report.resourceallocation.number.name");
    /** Human-readable string for the "Total Number of Overallocated Resources" Field. */
    private String TOTAL_NUMBER_OF_OVERALLOCATED_RESOURCES = PropertyProvider.get("prm.schedule.report.resourceallocation.totalnumberofoverallocatedresources.name");
    /** Human-readable string for the "Highest Percentage of Allocation" Field. */
    private String HIGHEST_PERCENTAGE_OF_ALLOCATION = PropertyProvider.get("prm.schedule.report.resourceallocation.highestpercentageofallocation.name");
    /** Human-readable string for the "Most Frequently Overallocated Resource" Field. */
    private String MOST_FREQUENTLY_OVERALLOCATED_RESOURCE = PropertyProvider.get("prm.schedule.report.resourceallocation.mostfrequentlyoverallocatedresource.name");
    /** Human-readable string for the "Date" column. */
    private String DATE = PropertyProvider.get("prm.schedule.report.resourceallocation.date.name");
    /** Human-readable string for the "Resource Name" column. */
    private String RESOURCE_NAME = PropertyProvider.get("prm.schedule.report.resourceallocation.resourcename.name");
    /** Human-readable string for the "Percent Allocated" column. */
    private String PERCENT_ALLOCATED = PropertyProvider.get("prm.schedule.report.resourceallocation.percentallocated.name");
    /** Human-readable string for the "Task Names" column. */
    private String TASK_NAMES = PropertyProvider.get("prm.schedule.report.resourceallocation.tasknames.name");
    /**
     * Token pointing at to an unexpected error message of: "Unexpected error
     * while creating report."
     */
    private String UNEXPECTED_REPORT_ERROR_TOKEN = "prm.report.errors.unexpectedcreationerror.message";

    /**
     * Standard constructor to create a new OverallocatedResourceReport instance.
     */
    public OverallocatedResourcesReport(ReportScope scope) {
        super(new OverallocatedResourcesData(), ReportType.OVERALLOCATED_RESOURCES_REPORT, scope);
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
        XMLDocument xml = new XMLDocument();
        getData().load();

        xml.startElement("OverallocatedResourcesReport");

        //Meta Information
        addReportHeader(xml);

        //ReportParameters
        this.addReportParametersElements(xml);

        //Create the summary section
        OverallocatedResourcesSummaryData summaryData =
            ((OverallocatedResourcesData)getData()).getSummaryData();
        xml.startElement("SummaryData");
        xml.addElement("OverallocatedDayCount", String.valueOf(summaryData.getOverallocatedDays()));
        xml.addElement("OverallocatedResourceCount", String.valueOf(summaryData.getOverallocatedResources()));
        xml.addElement("HighestPercentageOfAllocation", String.valueOf(summaryData.getHighestPercentOfAllocation()));
        xml.addElement("MostOverallocatedResource", summaryData.getMostOverallocatedResource());
        xml.endElement();

        //Create the detailed data section
        GroupingIterator it = getData().getGroupingIterator();
        xml.startElement("DetailedData");
        //Iterate through all of the resource allocations, adding them to the xml.
        while (it.hasNext()) {
            ResourceAssignment ra = (ResourceAssignment)it.next();
            xml.addElement(ra.getXMLDocument());
        }
        xml.endElement();
        xml.endElement();

        return xml;
    }

    /**
     * Write the report to a PDF document encapsulated by the document object.
     *
     * @param document a <code>com.lowagie.text.Document</code> object that should
     * construct the PDF.
     * @throws ReportException if any error occurs while creating the report.
     */
    public void writeReport(Document document) throws ReportException {
        try {
            //Load the data to populate the report
            getData().load();

            //Add meta information
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
        	Logger.getLogger(OverallocatedResourcesReport.class).debug("An unexpected exception occurred: " + e);
            throw new ReportException(
                PropertyProvider.get(UNEXPECTED_REPORT_ERROR_TOKEN), e);
        }
    }

    /**
     * Create the <code>table</code> object which holds the summary section of
     * the overallocated resources report.
     *
     * @return a {@link com.lowagie.text.Table} object which has been properly
     * populated with the information to display the summary section of the
     * overallocated resources report.
     * @throws BadElementException if the <code>Table</code> is assembled
     * incorrectly.
     */
    private Table createReportSummary() throws BadElementException, DocumentException {
        //Get the data that we will need to populate this table
        OverallocatedResourcesSummaryData data = ((OverallocatedResourcesData)getData()).getSummaryData();

        //Create the table
        Table summaryTable = ReportComponents.createSummaryTable(3);
        //Set up the default widths for all of the cells
        summaryTable.setWidths(new int[]{50, 20, 30});


        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_NUMBER_OF_OVERALLOCATED_DAYS));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(data.getOverallocatedDays())));

        //Add a placeholder just in case we add a graph later
        Cell chartCell = new Cell();
        chartCell.setRowspan(5);
        summaryTable.addCell(chartCell);

        //Create the rest of the summary section
        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_NUMBER_OF_OVERALLOCATED_RESOURCES));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(data.getOverallocatedResources())));
        summaryTable.addCell(ReportComponents.createSummaryCell(HIGHEST_PERCENTAGE_OF_ALLOCATION));

        BigDecimal highestAllocation = new BigDecimal(data.getHighestPercentOfAllocation());
        highestAllocation = highestAllocation.movePointLeft(2);
        summaryTable.addCell(ReportComponents.createSummaryCell(NumberFormat.getInstance().formatPercent(highestAllocation.floatValue())));
        summaryTable.addCell(ReportComponents.createSummaryCell(MOST_FREQUENTLY_OVERALLOCATED_RESOURCE));
        summaryTable.addCell(ReportComponents.createSummaryCell(data.getMostOverallocatedResource()));

        return summaryTable;
    }

    /**
     * This method creates a table which will become the detailed section of the
     * overallocated resources report.
     *
     * @return a <code>Table</code> which displays summary information about
     * overallocated resources.
     * @throws PersistenceException if an error occurs while iterating through
     * the data to produce the rows.
     * @throws DocumentException if the iText libraries are used incorrectly.
     * (Such as specifying a colspan greater than the size of a table.
     */
    private Table createDetailedSection() throws PersistenceException, DocumentException {
        Table detailedTable = ReportComponents.createDetailedTable(4);
        detailedTable.setWidths(new int[]{15, 30, 15, 50});

        //Create the table header row
        detailedTable.setDefaultCellBorder(Rectangle.BOTTOM);
        detailedTable.setDefaultCellBorderWidth(3);

        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(DATE));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(RESOURCE_NAME));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(PERCENT_ALLOCATED));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(TASK_NAMES));
        detailedTable.endHeaders();
        detailedTable.setDefaultCellBorderWidth(1);

        GroupingIterator iterator = getData().getGroupingIterator();

        if (!iterator.hasNext()) {
            Cell noDataFoundCell = new Cell(new Phrase(PropertyProvider.get("prm.project.reports.overallocation.nodata.message"), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            noDataFoundCell.setColspan(3);
            detailedTable.addCell(noDataFoundCell);
        } else {
            //Iterate through each matching resource, creating a row for each
            while (iterator.hasNext()) {
                ResourceAssignment allocation = (ResourceAssignment)iterator.next();

                //If the group has changed, show a cell for the new group
                if ((iterator.isGroupChanged()) && (iterator.getGroupName() != null)) {
                    detailedTable.addCell(ReportComponents.createGroupChangeCell(iterator.getGroupName(), 4));
                }

                //Date work was overallocated
                detailedTable.addCell(ReportComponents.createDetailedCell(
                    SessionManager.getUser().getDateFormatter().formatDate(
                        allocation.getAssignmentDate())));

                //Name of the resource being allocated
                detailedTable.addCell(ReportComponents.createDetailedCell(allocation.getResourceName()));

                //Put together the properly formatted value for a percent
                String percentString = NumberFormat.getInstance().formatPercent(((float)allocation.getPercentAssigned()) / 100);
                detailedTable.addCell(ReportComponents.createDetailedCell(percentString));

                //Add the name of the tasks that this resource is assigned to
                detailedTable.addCell(ReportComponents.createDetailedCell(allocation.getTaskNames()));
            }
        }


        return detailedTable;
    }
}
