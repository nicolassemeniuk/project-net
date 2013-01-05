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
package net.project.schedule.report.scheduletasks;

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
import net.project.schedule.ScheduleEntry;
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
 * This class creates the PDF version of the ScheduleTaskReport or XML containing
 * all of the data of that report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ScheduleTasksReport extends AbstractReport {
    /** Human-readable fieldname for "Total Number Of Tasks". */
    private String TOTAL_NUMBER_OF_TASKS = PropertyProvider.get("prm.schedule.report.scheduletasks.totalnumberoftasks.name");
    /** Human-readable fieldname for "Total number of completed tasks". */
    private String TOTAL_NUMBER_OF_COMPLETED_TASKS = PropertyProvider.get("prm.schedule.report.scheduletasks.totalnumberofcompletedtasks.name");
    /** Human-readable fieldname for "Total Number of Overdue Tasks". */
    private String TOTAL_NUMBER_OF_OVERDUE_TASKS = PropertyProvider.get("prm.schedule.report.scheduletasks.totalnumberofoverduetasks.name");
    /** Human-readable fieldname for "Completed in Last 7 Days". */
    private String TOTAL_NUMBER_COMPLETED_IN_LAST_7_DAYS = PropertyProvider.get("prm.schedule.report.scheduletasks.totalcompletedinlast7days.name");
    /** Human-readable fieldname for "Tasks Due in Next 7 Days". */
    private String TOTAL_NUMBER_DUE_IN_NEXT_7_DAYS = PropertyProvider.get("prm.schedule.report.scheduletasks.totaldueinnext7days.name");
    /** Human-readable column name for the "Task Name" column. */
    private String TASK_NAME = PropertyProvider.get("prm.schedule.report.scheduletasks.taskname.name");
    /** Human-readable column name for the "Type" column. */
    private String TASK_TYPE = PropertyProvider.get("prm.schedule.report.scheduletasks.type.name");
    /** Human-readable column name for the "Start" column. */
    private String START = PropertyProvider.get("prm.schedule.report.scheduletasks.start.name");
    /** Human-readable column name for the "Finish" column. */
    private String FINISH = PropertyProvider.get("prm.schedule.report.scheduletasks.finish.name");
    /** Human-readable column name for the "Work Complete" column. */
    private String WORK_COMPLETE = PropertyProvider.get("prm.schedule.report.scheduletasks.workcomplete.name");
    /** Human-readable column name for the "Total Work" column. */
    private String TOTAL_WORK = PropertyProvider.get("prm.schedule.report.scheduletasks.totalwork.name");
    /** Human-readable column name for the "% Complete" column. */
    private String PERCENT_COMPLETE = PropertyProvider.get("prm.schedule.report.scheduletasks.complete.name");
    /** Human-readable column name for the "Resources" column. */
    private String RESOURCES = PropertyProvider.get("prm.schedule.report.scheduletasks.resources.name");
    /** String displayed to user when there aren't any detailed records to be shown. */
    private String NO_TASKS_FOUND = PropertyProvider.get("prm.schedule.report.scheduletasks.notasksfound.name");
    /**
     * Token pointing at to an unexpected error message of: "Unexpected error
     * while creating report."
     */
    private String UNEXPECTED_REPORT_ERROR_TOKEN = "prm.report.errors.unexpectedcreationerror.message";

    /**
     * Common constructor which initializes objects required to create the report.
     */
    public ScheduleTasksReport(ReportScope scope) {
        super(new ScheduleTasksReportData(), ReportType.SCHEDULE_TASKS_REPORT, scope);
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
        doc.startElement("ScheduleTasks");

        //Add the meta information
        addReportHeader(doc);

        //Add the report parameters
        addReportParametersElements(doc);

        //Add the summary section
        ScheduleTasksSummaryData summaryData = ((ScheduleTasksReportData)getData()).getSummaryData();
        doc.startElement("SummaryData");
        doc.addElement("TaskCount", new Integer(summaryData.getTotalNumberOfTasks()));
        //doc.addElement("ChartURL", getChartURL());
        doc.addElement("CompletedTasks", new Integer(summaryData.getTotalCompletedInLast7Days()));
        doc.addElement("OverdueTasks", new Integer(summaryData.getTotalNumberOfOverdueTasks()));
        doc.addElement("TasksCompletedInLast7Days", new Integer(summaryData.getTotalCompletedInLast7Days()));
        doc.addElement("TasksDueInNext7Days", new Integer(summaryData.getTotalDueInNext7Days()));
        doc.endElement();

        //Create the detailed data section
        GroupingIterator gi = getData().getGroupingIterator();
        doc.startElement("DetailedData");
        //Iterate through all of the tasks, adding them individually
        while (gi.hasNext()) {
            doc.startElement("TaskData");
            ScheduleEntry currentTask = (ScheduleEntry)gi.next();
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

    /**
     * Write the report to a PDF document encapsulated by the document object.
     *
     * @param document a <code>com.lowagie.text.Document</code> object that should
     * construct the PDF.
     * @throws ReportException if any error occurs while creating the report.
     */
    public void writeReport(Document document) throws ReportException {
        try {
            getData().load();

            //Meta information
            addReportMetaInformation(document);
            document.open();

            //Title section
            addReportHeader(document);

            //Parameter section
            if (showReportParameters) {
                document.add(createReportParameters());
            }

            //Summary Section
            document.add(createSummarySection());

            //Detailed Section
            document.add(createDetailedSection());
        } catch (Exception e) {
            throw new ReportException(
                PropertyProvider.get(UNEXPECTED_REPORT_ERROR_TOKEN), e);
        }
    }

    /**
     * Create a table from the report that summarizes all of the data shown in
     * the detailed section.
     *
     * @return a <code>Table</code> which contains data summarizing the detailed
     * section of this report.
     * @throws BadElementException if the iText libraries are used incorrectly.
     * (Such as specifying a colspan greater than the size of a table.
     */
    private Table createSummarySection() throws BadElementException {
        Table summaryTable = ReportComponents.createSummaryTable(3);
        summaryTable.setWidths(new float[]{42f, 8f, 50f});

        //Get the data for the summary table
        ScheduleTasksSummaryData summaryData = ((ScheduleTasksReportData)getData()).getSummaryData();

        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_NUMBER_OF_TASKS));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getTotalNumberOfTasks())));

        Cell chartCell = new Cell();
        chartCell.setRowspan(4);
        summaryTable.addCell(chartCell);

        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_NUMBER_OF_COMPLETED_TASKS));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getTotalNumberOfCompletedTasks())));
        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_NUMBER_OF_OVERDUE_TASKS));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getTotalNumberOfOverdueTasks())));
        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_NUMBER_COMPLETED_IN_LAST_7_DAYS));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getTotalCompletedInLast7Days())));
        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_NUMBER_DUE_IN_NEXT_7_DAYS));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getTotalDueInNext7Days())));

        return summaryTable;
    }

    /**
     * Create the detailed section of the report, which consists of a number of
     * rows of task data.
     *
     * @return A <code>Table</code> containing the detailed information.
     * @throws DocumentException if the user has set widths for the wrong number
     * of columns internally.  This error should really only be thrown at design
     * time.
     * @throws PersistenceException if an error occurs while iterating through
     * the data to produce the rows.
     */
    private Table createDetailedSection() throws PersistenceException, DocumentException {
        Table detailedTable = ReportComponents.createDetailedTable(8);
        //Note that the widths don't have to equal one hundred, they are just relative widths
        detailedTable.setWidths(new int[]{20, 10, 12, 12, 10, 10, 10, 20});

        //Add the header to the detailed section
        detailedTable.setDefaultCellBorder(Rectangle.BOTTOM);
        detailedTable.setDefaultCellBorderWidth(3);
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(TASK_NAME));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(TASK_TYPE));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(START));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(FINISH));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(WORK_COMPLETE));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(TOTAL_WORK));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(PERCENT_COMPLETE));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(RESOURCES));
        detailedTable.endHeaders();
        detailedTable.setDefaultCellBorderWidth(1);

        GroupingIterator taskIterator = getData().getGroupingIterator();

        if (!taskIterator.hasNext()) {
            //There aren't any late tasks.  Let the user know this.
            Cell noDataFoundCell = new Cell(new Phrase(NO_TASKS_FOUND, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            noDataFoundCell.setColspan(8);
            detailedTable.addCell(noDataFoundCell);
        } else {
            //Iterate through each matching task, creating a row for each
            while (taskIterator.hasNext()) {
                ScheduleEntry currentTask = (ScheduleEntry)taskIterator.next();

                if ((taskIterator.isGroupChanged()) && (taskIterator.getGroupName() != null)) {
                    Cell groupingCell = new Cell(new Phrase(taskIterator.getGroupName(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
                    groupingCell.setBackgroundColor(Color.LIGHT_GRAY);
                    groupingCell.setColspan(8);
                    groupingCell.setBorder(Rectangle.BOX);
                    detailedTable.addCell(groupingCell);
                }

                detailedTable.addCell(ReportComponents.createDetailedCell(currentTask.getName()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentTask.getTaskType().getName()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentTask.getStartTimeString()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentTask.getEndTimeString()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentTask.getWorkCompleteTQ().toString()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentTask.getWorkTQ().toString()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentTask.getWorkPercentComplete()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentTask.getAssigneeString()));
            }
        }

        return detailedTable;
    }
}
