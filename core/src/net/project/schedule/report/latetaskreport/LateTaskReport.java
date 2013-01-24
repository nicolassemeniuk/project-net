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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.schedule.report.latetaskreport;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import net.project.base.finder.GroupingIterator;
import net.project.base.property.PropertyProvider;
import net.project.chart.ChartingException;
import net.project.persistence.PersistenceException;
import net.project.report.AbstractReport;
import net.project.report.ReportComponents;
import net.project.report.ReportException;
import net.project.report.ReportScope;
import net.project.report.ReportType;
import net.project.schedule.ScheduleEntry;
import net.project.util.ImageUtils;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

import org.apache.log4j.Logger;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;

/**
 * The Late Task Report produces a list of tasks that are due before the current
 * date and time in either PDF or XML format.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class LateTaskReport extends AbstractReport {
    /** Label for the "Task Name" column. */
    private String TASK_NAME = PropertyProvider.get("prm.schedule.report.latetaskreport.taskname.name");
    /** Label for the "Type" column. */
    private String TASK_TYPE = PropertyProvider.get("prm.schedule.report.latetaskreport.type.name");
    /** Label for the "Start" column. */
    private String START = PropertyProvider.get("prm.schedule.report.latetaskreport.start.name");
    /** Label for the "Finish" column. */
    private String FINISH = PropertyProvider.get("prm.schedule.report.latetaskreport.finish.name");
    /** Label for the "Work Complete" column. */
    private String WORK_COMPLETE = PropertyProvider.get("prm.schedule.report.latetaskreport.workcomplete.name");
    /** Label for the "Total Work" column. */
    private String TOTAL_WORK = PropertyProvider.get("prm.schedule.report.latetaskreport.totalwork.name");
    /** Label for the "Percent Complete" column. */
    private String PERCENT_COMPLETE = PropertyProvider.get("prm.schedule.report.latetaskreport.complete.name");
    /** Label for the "Resources" column. */
    private String RESOURCES = PropertyProvider.get("prm.schedule.report.latetaskreport.resources.name");
    /** Label for the "Total Number of Tasks" field. */
    private String TOTAL_NUMBER_OF_TASKS = PropertyProvider.get("prm.schedule.report.latetaskreport.totalnumberoftasks.name");
    /** Label for the "Total Number of Completed Tasks" field. */
    private String TOTAL_NUMBER_OF_COMPLETED_TASKS = PropertyProvider.get("prm.schedule.report.latetaskreport.totalnumberofcompletedtasks.name");
    /** Label for the "Total Number of Overdue Tasks" field. */
    private String TOTAL_NUMBER_OF_OVERDUE_TASKS = PropertyProvider.get("prm.schedule.report.latetaskreport.totalnumberofoverduetasks.name");
    /** Label for the "Total Number of Overdue Milestones" field. */
    private String TOTAL_NUMBER_OF_OVERDUE_MILESTONES = PropertyProvider.get("prm.schedule.report.latetaskreport.totalnumberofoverduemilestones.name");
    /** Label for the "Tasks Completed in the Last 7 days" field. */
    private String TASKS_COMPLETED_IN_LAST_7_DAYS = PropertyProvider.get("prm.schedule.report.latetaskreport.taskscompletedinlast7days.name");
    /** Label for the "Tasks Due in Next 7 Days" field. */
    private String TASKS_DUE_IN_NEXT_7_DAYS = PropertyProvider.get("prm.schedule.report.latetaskreport.tasksdueinnext7days.name");
    /** Message shown when there are no detailed records to display. */
    private String NO_LATE_TASKS_FOUND = PropertyProvider.get("prm.schedule.report.latetaskreport.nolatetasksfound.name"); //"No Late Tasks Found";
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
    public LateTaskReport(ReportScope scope) {
        super(new LateTaskReportData(), ReportType.LATE_TASK_REPORT, scope);
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
        doc.startElement("latetaskreport");

        //Meta information
        addReportHeader(doc);

        //Add the report parameters
        addReportParametersElements(doc);

        //Create the summary section
        LateTaskReportSummaryData summaryData = ((LateTaskReportData)getData()).getSummaryData();
        doc.startElement("SummaryData");
        doc.addElement("TaskCount", new Integer(summaryData.getTaskCount()));
        doc.addElement("ChartURL", getChartURL());
        doc.addElement("CompletedTasks", new Integer(summaryData.getCompletedTaskCount()));
        doc.addElement("OverdueTasks", new Integer(summaryData.getOverdueTaskCount()));
        doc.addElement("OverdueMilestones", new Integer(summaryData.getOverdueMilestoneCount()));
        doc.addElement("TasksCompletedInLast7Days", new Integer(summaryData.getTaskCompletedInLast7DaysCount()));
        doc.addElement("TasksDueInNext7Days", new Integer(summaryData.getTaskDueInNext7DaysCount()));
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

    //##########################################################################
    // PDF Implementation
    //##########################################################################

    /**
     * Construct the late task report in PDF, writing it to the Document object
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

            //Summary Section
            document.add(createReportSummary());

            //Detailed Section
            document.add(createDetailedSection());

        } catch (Exception e) {
        	Logger.getLogger(LateTaskReport.class).error("An unexpected exception occurred: " + e);
            throw new ReportException(
                PropertyProvider.get(UNEXPECTED_REPORT_ERROR_TOKEN), e);
        }
    }


    /**
     * Create the "Summary" section of the report that shows roll up numbers
     * that summarize the data shown in the detailed section.
     *
     * @return A <code>Table</code> that contains the summary information.
     * @throws BadElementException if the iText libraries are used incorrectly.
     * (Such as specifying a colspan greater than the size of a table.
     * @throws IOException if the chart could not be loaded from the URL
     * specified by getChartURL().
     * @throws ChartingException if an error occurs while trying to create the
     * chart.
     */
    private Table createReportSummary() throws BadElementException, IOException, ChartingException {
        LateTaskReportSummaryData summaryData = ((LateTaskReportData)getData()).getSummaryData();

        //Create the table that will house the top summary section
        Table summaryTable = ReportComponents.createSummaryTable(3, 6);
        summaryTable.setWidths(new float[]{42f, 8f, 50f});

        //Add the first row of the cell.  This row differs from the others
        //because it also contains the chart cell.  Seems how the chart cell will
        //span all of the rows, the rest of the rows will only appear to have a
        //single cell, even though they actually have two.
        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_NUMBER_OF_TASKS));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getTaskCount())));

        Cell chartCell = getChartCell();

        chartCell.setBorder(Rectangle.NO_BORDER);
        chartCell.setRowspan(6);
        summaryTable.addCell(chartCell);

        //Create the remaining rows.
        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_NUMBER_OF_COMPLETED_TASKS));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getCompletedTaskCount())));
        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_NUMBER_OF_OVERDUE_TASKS));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getOverdueTaskCount())));
        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_NUMBER_OF_OVERDUE_MILESTONES));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getOverdueMilestoneCount())));
        summaryTable.addCell(ReportComponents.createSummaryCell(TASKS_COMPLETED_IN_LAST_7_DAYS));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getTaskCompletedInLast7DaysCount())));
        summaryTable.addCell(ReportComponents.createSummaryCell(TASKS_DUE_IN_NEXT_7_DAYS));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getTaskDueInNext7DaysCount())));

        return summaryTable;
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
            Cell noDataFoundCell = new Cell(new Phrase(NO_LATE_TASKS_FOUND, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            noDataFoundCell.setColspan(8);
            detailedTable.addCell(noDataFoundCell);
        } else {
            //Iterate through each matching task, creating a row for each
            while (taskIterator.hasNext()) {
                ScheduleEntry currentTask = (ScheduleEntry)taskIterator.next();

                if ((taskIterator.isGroupChanged()) && (taskIterator.getGroupName() != null)) {
                    detailedTable.addCell(ReportComponents.createGroupChangeCell(taskIterator.getGroupName(), 8));
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

    /**
     * Gets the chartCell attribute of the latetaskreport object.
     *
     * @return a <code>Cell</code> containing the latetaskreport chart.
     * @throws ChartingException if any errors occurs while creating the chart.
     * @throws IOException if the URL for the chart is not valid.
     * @throws BadElementException if the information being offered to create a
     * chart is invalid.  That is, if the URL or the byte array produced by the
     * charting object is incorrect.
     */
    private Cell getChartCell() throws ChartingException, IOException, BadElementException {
        Cell chartCell;

        //Determine whether the image should be included as a binary part of the
        //report or if we should be creating it as a URL.  This is important for
        //the HTML version of reports, which cannot inline image data.
        if (inlineImage) {
            //Create the chart based on a binary stream
            LateTaskChart ltc = new LateTaskChart();
            try {
                ltc.populateParameters(getData());
            } catch (Exception e) {
                throw new ChartingException(
                    PropertyProvider.get(UNEXPECTED_CHARTING_ERROR_TOKEN), e);
            }
            BufferedImage chartImage = ltc.getChart();
            byte[] chartByteArray = ImageUtils.bufferedImageToByteArray(chartImage, "png");
            chartCell = new Cell(Image.getInstance(chartByteArray));
        } else {
            //Create the chart cell based on a URL.
            chartCell = new Cell(Image.getInstance(new URL(getChartURL())));
        }

        return chartCell;
    }
}
