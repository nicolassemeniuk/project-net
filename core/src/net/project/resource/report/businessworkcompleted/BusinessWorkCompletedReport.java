package net.project.resource.report.businessworkcompleted;

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

public class BusinessWorkCompletedReport extends AbstractReport{

	  /**
     * Token pointing at to an unexpected error message of: "Unexpected error
     * while creating report."
     */
    private String UNEXPECTED_REPORT_ERROR_TOKEN = "prm.report.errors.unexpectedcreationerror.message";

    public BusinessWorkCompletedReport(ReportScope scope) {
        super(new BusinessWorkCompletedData(scope), ReportType.WORK_COMPLETED_REPORT, scope);
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
        Table detailedTable = ReportComponents.createDetailedTable(9);

        detailedTable.setDefaultCellBorder(Rectangle.BOTTOM);
        detailedTable.setDefaultCellBorderWidth(3);
        
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(PropertyProvider.get("prm.resource.report.workcompleted.assignment.spacename")));
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
            noDataFoundCell.setColspan(9);
            detailedTable.addCell(noDataFoundCell);
        } else {
            while (it.hasNext()) {
                AssignmentWorkLogEntry entry = (AssignmentWorkLogEntry)it.next();

                if ((it.isGroupChanged()) && (it.getGroupName() != null)) {
                    Cell groupingCell = new Cell(new Phrase(it.getGroupName(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
                    groupingCell.setBackgroundColor(Color.LIGHT_GRAY);
                    groupingCell.setColspan(9);
                    groupingCell.setBorder(Rectangle.BOX);
                    detailedTable.addCell(groupingCell);
                }
                
                detailedTable.addCell(ReportComponents.createDetailedCell(entry.getSpaceName()));
                detailedTable.addCell(ReportComponents.createDetailedCell(entry.getObjectName()));
                detailedTable.addCell(ReportComponents.createDetailedCell(df.formatDate(entry.getDatesWorked() == null ? new Date(0) : entry.getDatesWorked().getRangeStart())));
                detailedTable.addCell(ReportComponents.createDetailedCell(df.formatDate(entry.getDatesWorked() == null ? new Date(0) : entry.getDatesWorked().getRangeEnd())));
                detailedTable.addCell(ReportComponents.createDetailedCell(entry.getTotalAssignmentWork().toShortString(0,2)));
                detailedTable.addCell(ReportComponents.createDetailedCell(entry.getWork().toShortString(0,2)));
                detailedTable.addCell(ReportComponents.createDetailedCell(entry.getTotalRemainingWork().toShortString(0,2)));
                detailedTable.addCell(ReportComponents.createDetailedCell(df.formatDate(entry.getLogDate())));
                detailedTable.addCell(ReportComponents.createDetailedCell(entry.getAssigneeName()));
            }
        }

        return detailedTable;
    }	
	
}
