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

 package net.project.schedule.report.resourceallocation;

import java.math.BigDecimal;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import net.project.base.PnetRuntimeException;
import net.project.base.finder.GroupingIterator;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.report.AbstractReport;
import net.project.report.ReportComponents;
import net.project.report.ReportException;
import net.project.report.ReportScope;
import net.project.report.ReportType;
import net.project.schedule.Schedule;
import net.project.schedule.ScheduleFinder;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.util.DateFormat;
import net.project.util.NumberFormat;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

import org.apache.log4j.Logger;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;

/**
 * Class to implement the PDF and XML generation for the Resource Allocation
 * Report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ResourceAllocationReport extends AbstractReport {
    /** Human readable name for the "Total Number of Tasks" field. */
    private String TOTAL_NUMBER_OF_TASKS = PropertyProvider.get("prm.schedule.report.resourceallocation.totalnumberoftasks.name");  //"Total Number of Tasks";
    /** Human readable name for the "Total Number of Resources" field. */
    private String TOTAL_NUMBER_OF_RESOURCES = PropertyProvider.get("prm.schedule.report.resourceallocation.totalnumberofresources.name");  //"Total Number of Resources";
    /** Human readable name for the "Total Number of Working Hours" field. */
    private String TOTAL_NUMBER_OF_WORKING_HOURS = PropertyProvider.get("prm.schedule.report.resourceallocation.totalnumberofassignedhours.name");  //"Total Number of Assigned Hours";

    /** Human readable name for the "Resource" field. */
    private String RESOURCE = PropertyProvider.get("prm.schedule.report.resourceallocation.resource.name");  //"Resource";
    /** Human readable name for the "Task Name" field. */
    private String TASK_NAME = PropertyProvider.get("prm.schedule.report.resourceallocation.taskname.name");  //"Task Name";
    /** Human readable name for the "Duration" field. */
    private String DURATION = PropertyProvider.get("prm.schedule.report.resourceallocation.duration.name");  //"Duration";
    /** Human readable name for the "Start Date" field. */
    private String START_DATE = PropertyProvider.get("prm.schedule.report.resourceallocation.startdate.name");  //"Start Date";
    /** Human readable name for the "Finish Date" field. */
    private String FINISH_DATE = PropertyProvider.get("prm.schedule.report.resourceallocation.finishdate.name");  //"Finish Date";
    /** Human readable name for the "Percent Allocated" field. */
    private String PERCENT_ALLOCATED = PropertyProvider.get("prm.schedule.report.resourceallocation.percentallocated.name");  //"Percent Allocated";
    /** Human readable field for the column "Allocated Hours". */
    private String ALLOCATED_HOURS = PropertyProvider.get("prm.schedule.report.resourceallocation.allocatedhours.name");

    /** This is the schedule for the current space we are analyzing. */
    private Schedule currentSchedule = null;

    /**
     * Token pointing at to an unexpected error message of: "Unexpected error
     * while creating report."
     */
    private String UNEXPECTED_REPORT_ERROR_TOKEN = "prm.report.errors.unexpectedcreationerror.message";

    /**
     * Standard constructor which initializes the data and the report type for
     * the report.
     */
    public ResourceAllocationReport(ReportScope scope) {
        super(new ResourceAllocationData(), ReportType.RESOURCE_ALLOCATION_REPORT, scope);
    }

    /**
     * Populate internal data structures based on the results of a form
     * submission.
     *
     * @param request the source of the data for this object.
     */
    public void populateParameters(HttpServletRequest request) {
        super.populateParameters(request);

        //Check to see if the schedule has already been loaded.  We are going to
        //need to have a loaded schedule, so it makes sense to load it
        currentSchedule = (Schedule)request.getSession().getAttribute("session");

        if (currentSchedule == null || currentSchedule.getSpace() != SessionManager.getUser().getCurrentSpace()) {
            try {
                //We don't have the correct schedule available, we'll have to load it
                ScheduleFinder sf = new ScheduleFinder();
                Space currentSpace = SessionManager.getUser().getCurrentSpace();
                currentSchedule = (Schedule)sf.findBySpace(currentSpace).get(0);

                request.getSession().setAttribute("schedule", currentSchedule);
            } catch (PersistenceException e) {
            	Logger.getLogger(ResourceAllocationReport.class).debug("Unable to load schedule for current space.");
                throw new PnetRuntimeException("Unable to load schedule for current space.");
            }
        }

        ((ResourceAllocationData)getData()).setSchedule(currentSchedule);
    }

    /**
     * Write the report to a PDF document encapsulated by the document object.
     *
     * @param document a <code>com.lowagie.text.Document</code> object that should
     * construct the PDF.
     * @throws ReportException if any error occurs while assembling the report.
     */
    public void writeReport(Document document) throws ReportException {
        try {
            //Load the data that is going to populate this report
            getData().load();

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
        	Logger.getLogger(ResourceAllocationReport.class).error("An unexpected exception occurred: " + e);
            throw new ReportException(PropertyProvider.get(UNEXPECTED_REPORT_ERROR_TOKEN), e);
        }
    }

    /**
     * Create the detailed section of the resource allocation report in PDF
     * format.
     *
     * @return a <code>Table</code> object which has been populated with a newly
     * constructed summary section.
     * @throws BadElementException if an internal exception occurs while
     * assembling the cells of the table or the table itself.  This should only
     * occur if the report was poorly assembled.
     * @throws PersistenceException if an error occurs loading the data for this
     * report.
     */
    private Table createDetailedSection() throws BadElementException, PersistenceException {
        Table detailedTable = ReportComponents.createDetailedTable(7);
        detailedTable.setDefaultCellBorder(Rectangle.BOTTOM);
        detailedTable.setDefaultCellBorderWidth(3);
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(RESOURCE));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(TASK_NAME));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(DURATION));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(START_DATE));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(FINISH_DATE));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(PERCENT_ALLOCATED));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(ALLOCATED_HOURS));
        detailedTable.endHeaders();
        detailedTable.setDefaultCellBorderWidth(1);

        DateFormat df = SessionManager.getUser().getDateFormatter();
        NumberFormat nf = NumberFormat.getInstance();
        GroupingIterator iter = getData().getGroupingIterator();
        if (!iter.hasNext()) {
            //There isn't any data, return a cell which says that
            Cell noDataFoundCell = new Cell(new Phrase(PropertyProvider.get("prm.schedule.report.resourceallocation.nodata.message"), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            noDataFoundCell.setColspan(6);
            detailedTable.addCell(noDataFoundCell);
        } else {
            while (iter.hasNext()) {
                TaskResourceAllocation tra = (TaskResourceAllocation)iter.next();

                if ((iter.isGroupChanged()) && (iter.getGroupName() != null)) {
                    detailedTable.addCell(ReportComponents.createGroupChangeCell(iter.getGroupName(), 7));
                }

                BigDecimal percentAllocated = new BigDecimal(tra.getPercentAllocated());
                percentAllocated = percentAllocated.movePointLeft(2);

                detailedTable.addCell(ReportComponents.createDetailedCell(tra.getResourceName()));
                detailedTable.addCell(ReportComponents.createDetailedCell(tra.getTaskName()));
                detailedTable.addCell(ReportComponents.createDetailedCell(tra.getTaskDuration().toString()));
                detailedTable.addCell(ReportComponents.createDetailedCell(df.formatDate(tra.getStartDate())));
                detailedTable.addCell(ReportComponents.createDetailedCell(df.formatDate(tra.getFinishDate())));
                detailedTable.addCell(ReportComponents.createDetailedCell(nf.formatPercent(percentAllocated.floatValue())));
                detailedTable.addCell(ReportComponents.createDetailedCell(nf.formatNumber(tra.getAllocatedHours().floatValue(), 0, 3)));
            }
        }

        return detailedTable;
    }

    /**
     * Create the table which will become the summary section of the report
     * allocation report.
     *
     * @return a <code>Table</code> which will be the summary section of the
     * report.
     * @throws BadElementException if we have made a programmatic error while
     * coding the summary section.  An example of this would be if we were to
     * try to add a cell with a colspan of 5 columns to a table that had 4
     * columns.
     */
    private Table createReportSummary() throws BadElementException {
        //Get the data required to produce the report
        ResourceAllocationSummaryData summaryData = ((ResourceAllocationData)getData()).getSummaryData();

        //Create a table in which we will present the data
        Table summaryTable = ReportComponents.createSummaryTable(3);
        summaryTable.setWidths(new float[]{42f, 8f, 50f});

        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_NUMBER_OF_TASKS));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getTaskCount())));

        //Create a placeholder for a graph, in case we get time to add one
        Cell chartCell = new Cell();
        chartCell.setRowspan(3);
        summaryTable.addCell(chartCell);

        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_NUMBER_OF_RESOURCES));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getResourceCount())));
        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_NUMBER_OF_WORKING_HOURS));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getAssignedHourCount())));
        //summaryTable.addCell(TOTAL_NUMBER_OF_UNASSIGNED_HOURS);
        //summaryTable.addCell(String.valueOf(summaryData.getUnassignedHourCount()));

        return summaryTable;
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

        //Start the output of the resource allocation report
        doc.startElement("ResourceAllocationReport");

        //Meta information about the report
        addReportHeader(doc);

        //Add the report parameters if the user has requested them
        addReportParametersElements(doc);

        //Create the summary section
        doc.startElement("SummaryData");
        ResourceAllocationSummaryData summaryData = ((ResourceAllocationData)getData()).getSummaryData();
        doc.addElement("TaskCount", String.valueOf(summaryData.getTaskCount()));
        doc.addElement("ResourceCount", NumberFormat.getInstance().formatNumber(summaryData.getResourceCount()));
        doc.addElement("AssignedHours", NumberFormat.getInstance().formatNumber(summaryData.getAssignedHourCount(), 0, 3));
        doc.addElement("UnassignedHours", NumberFormat.getInstance().formatNumber(summaryData.getUnassignedHourCount(), 0, 3));
        doc.endElement();

        //Create the detailed data section
        doc.startElement("DetailedData");
        GroupingIterator gi = getData().getGroupingIterator();
        while (gi.hasNext()) {
            doc.startElement("ResourceAllocation");
            TaskResourceAllocation current = (TaskResourceAllocation)gi.next();
            //Add a group change, if one exists
            if ((gi.isGroupChanged()) && (gi.getGroupName() != null)) {
                doc.addElement("Group", gi.getGroupName());
            }
            doc.addXMLString(current.getXMLDocument().getXMLBodyString());
            doc.endElement();
        }
        doc.endElement();
        doc.endElement();

        return doc;
    }
}
