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
|   $Revision: 20792 $
|       $Date: 2010-05-04 11:45:32 -0300 (mar, 04 may 2010) $
|     $Author: ritesh $
|
+-----------------------------------------------------------------------------*/
package net.project.resource.report.workcompleted;

import java.awt.Color;
import java.sql.SQLException;
import java.util.Date;

import net.project.base.finder.GroupingIterator;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.report.AbstractReport;
import net.project.report.ReportComponents;
import net.project.report.ReportException;
import net.project.report.ReportScope;
import net.project.report.ReportType;
import net.project.resource.AssignmentWorkLogEntry;
import net.project.util.DateFormat;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;

public class WorkCompletedReport extends AbstractReport {
    /**
     * Token pointing at to an unexpected error message of: "Unexpected error
     * while creating report."
     */
    private String UNEXPECTED_REPORT_ERROR_TOKEN = "prm.report.errors.unexpectedcreationerror.message";

    public WorkCompletedReport(ReportScope scope) {
        super(new WorkCompletedData(scope), ReportType.WORK_COMPLETED_REPORT, scope);
    }

    /**
     * Get a object that represents an XML document.  This will be useful for
     * constructing the XML body and XML outputs.  This method is where we
     * construct that data.
     *
     * @return an XML object that represents that data being aggregated by this
     *         object.
     * @throws net.project.xml.document.XMLDocumentException if an internal
     * error occurs while constructing the document.  (This is generally a
     * programmer error.)
     * @throws net.project.persistence.PersistenceException if there is a
     * database error which occurs while constructing the XML object.
     * @throws SQLException 
     */
    public XMLDocument getXMLDocument() throws XMLDocumentException, PersistenceException, SQLException {
        getData().load();

        XMLDocument doc = new XMLDocument();
        doc.startElement("WorkCompleted");

        addReportHeader(doc);
        addReportParametersElements(doc);

        GroupingIterator gi = getData().getGroupingIterator();
        doc.startElement("DetailedData");
        while (gi.hasNext()) {
            AssignmentWorkLogEntry entry = (AssignmentWorkLogEntry)gi.next();

            if ((gi.isGroupChanged()) && (gi.getGroupName() != null)) {
                doc.addElement("Group", gi.getGroupName());
            }

            doc.addXMLString(entry.getXMLBody());
        }
        doc.endElement();

        doc.endElement();
        return doc;
    }

    /**
     * Write the report to a PDF document encapsulated by the document object.
     *
     * @param document a <code>com.lowagie.text.Document</code> object that
     * should construct the PDF.
     * @throws net.project.report.ReportException if any exception occurs while
     * trying to construct the report.  Exceptions other than report exception
     * will be wrapped in a report exception and rethrown.
     */
    public void writeReport(Document document) throws ReportException {
        try {
            getData().load();
            addReportMetaInformation(document);
            document.open();
            addReportHeader(document);
            if (showReportParameters) {
                document.add(createReportParameters());
            }

            //Detailed Section
            document.add(createDetailedSection());

        } catch (Exception e) {
            throw new ReportException(UNEXPECTED_REPORT_ERROR_TOKEN, e);
        }
    }

    private Table createDetailedSection() throws BadElementException, PersistenceException {
        DateFormat df = DateFormat.getInstance();
        Table detailedTable = ReportComponents.createDetailedTable(8);

        detailedTable.setDefaultCellBorder(Rectangle.BOTTOM);
        detailedTable.setDefaultCellBorderWidth(3);

        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(PropertyProvider.get("prm.resource.report.workcompleted.assignment.name")));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(PropertyProvider.get("prm.resource.report.workcompleted.actualstart.name")));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(PropertyProvider.get("prm.resource.report.workcompleted.actualfinish.name")));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(PropertyProvider.get("prm.resource.report.workcompleted.scheduledwork.name")));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(PropertyProvider.get("prm.resource.report.workcompleted.hourslogged.name")));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(PropertyProvider.get("prm.resource.report.workcompleted.remaininghours.name")));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(PropertyProvider.get("prm.resource.report.workcompleted.datelogged.name")));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(PropertyProvider.get("prm.resource.report.workcompleted.assignee.name")));

        GroupingIterator it = getData().getGroupingIterator();

        if (!it.hasNext()) {
            Cell noDataFoundCell = new Cell(new Phrase(PropertyProvider.get("prm.resource.report.workcompleted.noworkfound"), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            noDataFoundCell.setColspan(8);
            detailedTable.addCell(noDataFoundCell);
        } else {
            while (it.hasNext()) {
                AssignmentWorkLogEntry entry = (AssignmentWorkLogEntry)it.next();

                if ((it.isGroupChanged()) && (it.getGroupName() != null)) {
                    Cell groupingCell = new Cell(new Phrase(it.getGroupName(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
                    groupingCell.setBackgroundColor(Color.LIGHT_GRAY);
                    groupingCell.setColspan(8);
                    groupingCell.setBorder(Rectangle.BOX);
                    detailedTable.addCell(groupingCell);
                }

                detailedTable.addCell(ReportComponents.createDetailedCell(entry.getObjectName()));
                detailedTable.addCell(ReportComponents.createDetailedCell(df.formatDateMedium(entry.getDatesWorked() == null ? new Date(0) : entry.getDatesWorked().getRangeStart())));
                detailedTable.addCell(ReportComponents.createDetailedCell(df.formatDateMedium(entry.getDatesWorked() == null ? new Date(0) : entry.getDatesWorked().getRangeEnd())));
                detailedTable.addCell(ReportComponents.createDetailedCell(entry.getTotalAssignmentWork().toShortString(0,2)));
                detailedTable.addCell(ReportComponents.createDetailedCell(entry.getWork().toShortString(0,2)));
                detailedTable.addCell(ReportComponents.createDetailedCell(entry.getTotalRemainingWork().toShortString(0,2)));
                detailedTable.addCell(ReportComponents.createDetailedCell(df.formatDateMedium(entry.getLogDate())));
                detailedTable.addCell(ReportComponents.createDetailedCell(entry.getAssigneeName()));
            }
        }

        return detailedTable;
    }
}
