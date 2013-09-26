package net.project.material.report;

import java.io.IOException;
import java.sql.SQLException;

import net.project.base.finder.GroupingIterator;
import net.project.base.property.PropertyProvider;
import net.project.chart.ChartingException;
import net.project.material.MaterialBean;
import net.project.persistence.PersistenceException;
import net.project.report.AbstractReport;
import net.project.report.ReportComponents;
import net.project.report.ReportException;
import net.project.report.ReportScope;
import net.project.report.ReportType;
import net.project.schedule.report.latetaskreport.LateTaskReport;
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

public class ProjectMaterialReport extends AbstractReport {

	/** "Material Name" column name. */
	private String MATERIAL_NAME = PropertyProvider.get("prm.material.report.materialname.name");
	/** "Material Description" column name. */
	private String MATERIAL_DESCRIPTION = PropertyProvider.get("prm.material.report.materialdescription.name");	
	/** "Material Description" column name. */
	private String MATERIAL_COST = PropertyProvider.get("prm.material.report.materialcost.name");	
	/** "Material Description" column name. */
	private String MATERIAL_TYPE = PropertyProvider.get("prm.material.report.materialtype.name");	
	/** "Material Description" column name. */
	private String MATERIAL_CONSUMABLE = PropertyProvider.get("prm.material.report.materialconsumable.name");
	
	/** Label for the "Total Materials" field. */
	private String TOTAL_MATERIALS = PropertyProvider.get("prm.material.report.totalmaterials.name");
	/** Label for the "Total Cost" field. */
	private String TOTAL_COST = PropertyProvider.get("prm.material.report.totalcost.name");
    /** Message shown when there are no detailed records to display. */
    private String NO_MATERIALS_FOUND = PropertyProvider.get("prm.material.report.nomaterialsfound.name"); //"No Materials Found";

	
    /**
     * Token pointing at to an unexpected error message of: "Unexpected error
     * while creating report."
     */
    private String UNEXPECTED_REPORT_ERROR_TOKEN = "prm.report.errors.unexpectedcreationerror.message";

	/**
	 * Standard constructor which creates a FormItemSummaryReport.
	 */
	public ProjectMaterialReport(ReportScope scope) {
		super(new ProjectMaterialReportData(), ReportType.MATERIAL_REPORTS, scope);
	}

	@Override
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
	
    private Table createReportSummary() throws BadElementException, IOException, ChartingException {
        ProjectMaterialReportSummaryData summaryData = ((ProjectMaterialReportData)getData()).getSummaryData();

        //Create the table that will house the top summary section
        Table summaryTable = ReportComponents.createSummaryTable(2, 2);
        summaryTable.setWidths(new float[]{42f, 50f});

        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_MATERIALS));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getTotalMaterials())));

        //Create the remaining rows.
        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_COST));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getTotalCost())));

        return summaryTable;
    }
    
    private Table createDetailedSection() throws PersistenceException, DocumentException {
        Table detailedTable = ReportComponents.createDetailedTable(5);
        //Note that the widths don't have to equal one hundred, they are just relative widths
        detailedTable.setWidths(new int[]{8, 12, 5, 5, 5});

        //Add the header to the detailed section
        detailedTable.setDefaultCellBorder(Rectangle.BOTTOM);
        detailedTable.setDefaultCellBorderWidth(3);
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(MATERIAL_NAME));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(MATERIAL_DESCRIPTION));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(MATERIAL_COST));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(MATERIAL_TYPE));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(MATERIAL_CONSUMABLE));
        detailedTable.endHeaders();
        detailedTable.setDefaultCellBorderWidth(1);

        GroupingIterator materialIterator = getData().getGroupingIterator();

        if (!materialIterator.hasNext()) {
            //There aren't any late tasks.  Let the user know this.
            Cell noDataFoundCell = new Cell(new Phrase(NO_MATERIALS_FOUND, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            noDataFoundCell.setColspan(8);
            detailedTable.addCell(noDataFoundCell);
        } else {
            //Iterate through each matching task, creating a row for each
            while (materialIterator.hasNext()) {
                MaterialBean currentMaterial = (MaterialBean)materialIterator.next();

                if ((materialIterator.isGroupChanged()) && (materialIterator.getGroupName() != null)) {
                    detailedTable.addCell(ReportComponents.createGroupChangeCell(materialIterator.getGroupName(), 5));
                }

                detailedTable.addCell(ReportComponents.createDetailedCell(currentMaterial.getName()));
                if(currentMaterial.getDescription() == null)
                	detailedTable.addCell(ReportComponents.createDetailedCell(""));
                else
                	detailedTable.addCell(ReportComponents.createDetailedCell(currentMaterial.getDescription().trim()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentMaterial.getCost().trim()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentMaterial.getMaterialTypeName().trim()));
                if(currentMaterial.getConsumable()){
                	detailedTable.addCell(ReportComponents.createDetailedCell("   *   "));
                } else {
                	detailedTable.addCell(ReportComponents.createDetailedCell("       "));
                }
            }
        }

        return detailedTable;
    } 
    

	@Override
	public XMLDocument getXMLDocument() throws XMLDocumentException, PersistenceException, SQLException {
		XMLDocument doc = new XMLDocument();
		getData().load();

		// Start the output of the material report
		doc.startElement("materialreport");

		// Meta information
		addReportHeader(doc);

		// Add the report parameters
		addReportParametersElements(doc);

		// Create the summary section
		ProjectMaterialReportSummaryData summaryData = ((ProjectMaterialReportData) getData()).getSummaryData();
		doc.startElement("SummaryData");
		doc.addElement("MaterialCount", new Integer(summaryData.getTotalMaterials()));
		doc.addElement("MaterialTotalCost", new Float(summaryData.getTotalCost()));
		
		doc.endElement();

		// Create the detailed data section
		GroupingIterator gi = getData().getGroupingIterator();
		doc.startElement("DetailedData");
		// Iterate through all of the materials, adding them individually
		while (gi.hasNext()) {
			doc.startElement("MaterialData");
			MaterialBean currentMaterial = (MaterialBean) gi.next();
			// Add a group change, if one exists
			if ((gi.isGroupChanged()) && (gi.getGroupName() != null)) {
				doc.addElement("Group", gi.getGroupName());
			}
			doc.addXMLString(currentMaterial.getXMLBody());
			doc.endElement();
		}
		doc.endElement();

		// End the late Material Report
		doc.endElement();

		return doc;
	}

}
