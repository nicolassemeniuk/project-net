package net.project.financial.report;

import java.io.IOException;
import java.sql.SQLException;

import net.project.base.finder.GroupingIterator;
import net.project.base.property.PropertyProvider;
import net.project.chart.ChartingException;
import net.project.persistence.PersistenceException;
import net.project.project.ProjectSpace;
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

public class ActualCostTypesOverTotalReport extends AbstractReport {
	
	/** "Project Name" column name. */
	private final static String PROJECT_NAME = PropertyProvider.get("prm.financial.report.projectname.name");
	/** "Project Description" column name. */
	private final static String PROJECT_DESCRIPTION = PropertyProvider.get("prm.financial.report.projectdescription.name");	
	/** "Resources Actual Cost to Date" column name. */
	private final static String RESOURCES_ACTUAL_COST = PropertyProvider.get("prm.financial.report.resourcesactualcosttodate.name");	
	/** "Resources Actual Cost to Date" column name. */
	private final static String MATERIALS_ACTUAL_COST = PropertyProvider.get("prm.financial.report.materialsactualcosttodate.name");	
	/** "Resources Actual Cost to Date" column name. */
	private final static String DISCRETIONAL_ACTUAL_COST = PropertyProvider.get("prm.financial.report.discretionalactualcosttodate.name");
	
	/** Label for the "Total Number of Projects" field. */
	private final static String TOTAL_PROJECTS = PropertyProvider.get("prm.financial.report.totalprojects.name");
	/** Label for the "Total Resources Actual Cost To Date" field. */
	private final static String RESOURCES_TOTAL_ACTUAL_COST = PropertyProvider.get("prm.financial.report.resourcestotalactualcosttodate.name");
	/** Label for the "Total Materials Actual Cost To Date" field. */
	private final static String MATERIALS_TOTAL_ACTUAL_COST = PropertyProvider.get("prm.financial.report.materialstotalactualcosttodate.name");   
	/** Label for the "Total Discretional Actual Cost To Date" field. */
	private final static String DISCRETIONAL_TOTAL_ACTUAL_COST = PropertyProvider.get("prm.financial.report.discretionaltotalactualcosttodate.name");	
	
	/** Message shown when there are no detailed records to display. */
    private final static String NO_PROJECTS_FOUND = PropertyProvider.get("prm.financial.report.noprojectsfound.name"); //"No Projects Found";
    
    /**
     * Token pointing at to an unexpected error message of: "Unexpected error
     * while creating report."
     */
    private final static String UNEXPECTED_REPORT_ERROR_TOKEN = "prm.report.errors.unexpectedcreationerror.message";
    
	/**
	 * Standard constructor which creates a FormItemSummaryReport.
	 */
	public ActualCostTypesOverTotalReport(ReportScope scope) {
		super(new ActualCostTypesOverTotalReportData(), ReportType.ACTUAL_COST_TYPES_OVER_TOTAL_REPORTS, scope);
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
    	ActualCostTypesOverTotalReportSummaryData summaryData = ((ActualCostTypesOverTotalReportData)getData()).getSummaryData();

        //Create the table that will house the top summary section
        Table summaryTable = ReportComponents.createSummaryTable(2, 2);
        summaryTable.setWidths(new float[]{42f, 50f});

        summaryTable.addCell(ReportComponents.createSummaryCell(TOTAL_PROJECTS));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getTotalProjects())));

        //Create the remaining rows.
        summaryTable.addCell(ReportComponents.createSummaryCell(RESOURCES_TOTAL_ACTUAL_COST));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getResourcesTotalActualCostToDate())));
        
        summaryTable.addCell(ReportComponents.createSummaryCell(MATERIALS_TOTAL_ACTUAL_COST));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getMaterialsTotalActualCostToDate())));
        
        summaryTable.addCell(ReportComponents.createSummaryCell(DISCRETIONAL_TOTAL_ACTUAL_COST));
        summaryTable.addCell(ReportComponents.createSummaryCell(String.valueOf(summaryData.getDiscretionalTotalActualCostToDate())));

        return summaryTable;
    }
    
    private Table createDetailedSection() throws PersistenceException, DocumentException {
        Table detailedTable = ReportComponents.createDetailedTable(5);
        //Note that the widths don't have to equal one hundred, they are just relative widths
        detailedTable.setWidths(new int[]{8, 12, 5, 5, 5});

        //Add the header to the detailed section
        detailedTable.setDefaultCellBorder(Rectangle.BOTTOM);
        detailedTable.setDefaultCellBorderWidth(3);
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(PROJECT_NAME));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(PROJECT_DESCRIPTION));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(RESOURCES_ACTUAL_COST));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(MATERIALS_ACTUAL_COST));
        detailedTable.addCell(ReportComponents.createDetailedHeaderCell(DISCRETIONAL_ACTUAL_COST));
        detailedTable.endHeaders();
        detailedTable.setDefaultCellBorderWidth(1);

        GroupingIterator projectIterator = getData().getGroupingIterator();

        if (!projectIterator.hasNext()) {
            //There aren't any late tasks.  Let the user know this.
            Cell noDataFoundCell = new Cell(new Phrase(NO_PROJECTS_FOUND, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            noDataFoundCell.setColspan(8);
            detailedTable.addCell(noDataFoundCell);
        } else {
            //Iterate through each matching task, creating a row for each
            while (projectIterator.hasNext()) {
                ProjectSpace currentProject = (ProjectSpace)projectIterator.next();

                if ((projectIterator.isGroupChanged()) && (projectIterator.getGroupName() != null)) {
                    detailedTable.addCell(ReportComponents.createGroupChangeCell(projectIterator.getGroupName(), 5));
                }

                detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.getName()));
                if(currentProject.getDescription() == null)
                	detailedTable.addCell(ReportComponents.createDetailedCell(""));
                else
                	detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.getDescription().trim()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.getActualCostToDate().getValue().toString()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.getCurrentEstimatedTotalCost().getValue().toString()));
                detailedTable.addCell(ReportComponents.createDetailedCell(currentProject.getBudgetedTotalCost().getValue().toString()));
            }
        }

        return detailedTable;
    } 
    

	@Override
	public XMLDocument getXMLDocument() throws XMLDocumentException, PersistenceException, SQLException {
		XMLDocument doc = new XMLDocument();
		getData().load();

		// Start the output of the material report
		doc.startElement("actualcosttypesovertotalreport");

		// Meta information
		addReportHeader(doc);

		// Add the report parameters
		addReportParametersElements(doc);

		// Create the summary section
		ActualCostTypesOverTotalReportSummaryData summaryData = ((ActualCostTypesOverTotalReportData) getData()).getSummaryData();
		doc.startElement("SummaryData");
		doc.addElement("ProjectCount", new Integer(summaryData.getTotalProjects()));
		doc.addElement("ResourcesTotalActualCostToDate", new Float(summaryData.getResourcesTotalActualCostToDate()));
		doc.addElement("MaterialsTotalActualCostToDate", new Float(summaryData.getMaterialsTotalActualCostToDate()));
		doc.addElement("DiscretionalTotalActualCostToDate", new Float(summaryData.getDiscretionalTotalActualCostToDate()));
		
		doc.endElement();

		// Create the detailed data section
		GroupingIterator gi = getData().getGroupingIterator();
		doc.startElement("DetailedData");
		// Iterate through all of the materials, adding them individually
		while (gi.hasNext()) {
			doc.startElement("ProjectData");
			ProjectSpace currentProject = (ProjectSpace) gi.next();
			// Add a group change, if one exists
			if ((gi.isGroupChanged()) && (gi.getGroupName() != null)) {
				doc.addElement("Group", gi.getGroupName());
			}
			doc.addXMLString(currentProject.getXMLBody());
			doc.endElement();
		}
		doc.endElement();

		// End the late Material Report
		doc.endElement();

		return doc;
	}
}
