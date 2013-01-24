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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+-----------------------------------------------------------------------------*/
package net.project.form.report.formitemsummaryreport;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.base.property.PropertyProvider;
import net.project.chart.ChartingException;
import net.project.form.FormField;
import net.project.form.report.FormDataFormatter;
import net.project.persistence.PersistenceException;
import net.project.report.AbstractReport;
import net.project.report.ReportComponents;
import net.project.report.ReportException;
import net.project.report.ReportScope;
import net.project.report.ReportType;
import net.project.util.ImageUtils;
import net.project.util.Validator;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Table;

/**
 * Report to display data about form items.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class FormItemSummaryReport extends AbstractReport {
    /** "Field Value" column name. */
    private String FIELD_VALUE = PropertyProvider.get("prm.form.report.formitemsummaryreport.columns.fieldvalue.name");
    /** "Field Frequency" column name. */
    private String FIELD_FREQUENCY = PropertyProvider.get("prm.form.report.formitemsummaryreport.columns.fieldfrequency.name");
    /** Token to look up the "Form Name: {0}" token used to construct the FormName report parameter. */
    private String FORM_FILTER_PARAMETER = "prm.form.report.common.formfilterparameter.name";
    /** Token to look up the "Field Name: {0}" token used to construct the FieldName report parameter. */
    private String FIELD_PARAMETER = "prm.form.report.common.fieldfilterparameter.name";
    /** Token to use when a field value is blank.  This value will be used in the legend and field counts. */
    private String EMPTY_FIELD_LABEL = "prm.form.report.common.emptyfieldvalue.message";

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
     * Standard constructor which creates a FormItemSummaryReport.
     */
    public FormItemSummaryReport(ReportScope scope) {
        super(new FormItemSummaryReportData(), ReportType.FORM_ITEM_SUMMARY_REPORT, scope);
    }

    /**
     * Get the parameters that were passed to this report in order to create it.
     *
     * @return a <code>List</code> contain strings which describe the parameters
     * passed to this report.
     */
    protected List getReportParameters() {
        List reportParameters = new ArrayList();
        reportParameters.add(PropertyProvider.get(FORM_FILTER_PARAMETER,
            ((FormItemSummaryReportData)getData()).getForm().getName()));
        reportParameters.add(PropertyProvider.get(FIELD_PARAMETER,
            ((FormItemSummaryReportData)getData()).getFormField().getFieldLabel()));
        reportParameters.addAll(super.getReportParameters());
        return reportParameters;
    }

    /**
     * Get a list of fields that the "populateRequest" method expects to see in
     * a request, but were prepopulated prior to showing the user the parameters
     * page.  This is more than a little bit of a hack.
     *
     * @return a <code>Map</code> of name value pairs.
     */
    public Map getPrepopulatedParameters() {
        Map toReturn = new HashMap();

        toReturn.put("formID", ((FormItemSummaryReportData)getData()).getFormID());
        toReturn.put("formFieldID", ((FormItemSummaryReportData)getData()).getFieldID());

        return toReturn;
    }

    /**
     * Get a object that represents an XML document.  This will be useful for
     * constructing the XML body and XML outputs.  This method is where we
     * construct that data.
     *
     * @return an XML object that represents that data being aggregated by this
     * object.
     * @throws net.project.xml.document.XMLDocumentException if an internal error occurs while constructing
     * the document.  (This is generally a programmer error.)
     * @throws net.project.persistence.PersistenceException if there is a database error which occurs
     * while constructing the XML object.
     * @throws SQLException 
     */
    public XMLDocument getXMLDocument() throws XMLDocumentException, PersistenceException, SQLException {
        //Reload the report data to make sure it is there for building the
        //sections below.
        getData().load();

        XMLDocument doc = new XMLDocument();
        doc.startElement("FormItemSummaryReport");

        //Add the meta information
        addReportHeader(doc);

        //Add the report parameters to the report
        addReportParametersElements(doc);

        //Add the chart
        doc.startElement("Chart");
        doc.addElement("ChartURL", getChartURL());
        doc.endElement();  //Chart

        //Add the summarized field data
        SummarizedFieldData data = ((FormItemSummaryReportData)getData()).getSummaryData();
        doc.startElement("SummaryData");

        //Add definitions for the columns so the report knows what to render
        doc.startElement("ColumnDefinition");
        doc.addAttribute("Count", "2");
        doc.addElement("Column", FIELD_VALUE);
        doc.addElement("Column", FIELD_FREQUENCY);
        doc.endElement();  //ColumnDefinition

        //Add the data from the columns
        doc.startElement("DataRows");
        FormField field = ((FormItemSummaryReportData)getData()).getFormField();
        for (Iterator it = data.getFieldValues().keySet().iterator(); it.hasNext(); ) {
            String key = (String)it.next();

            doc.startElement("DataRow");
            String displayFieldName = FormDataFormatter.formatSimpleData(field, key);
            if (Validator.isBlankOrNull(displayFieldName)) {
                displayFieldName = PropertyProvider.get(EMPTY_FIELD_LABEL);
            }

            doc.addElement("Column", displayFieldName);
            doc.addElement("Column", data.getFieldValues().get(key));
            doc.endElement();  //DataRow
        }
        doc.endElement(); //DataRows

        doc.endElement();  //SummaryData
        doc.endElement();  //FormItemSummaryData
        return doc;
    }

    /**
     * Write the report to a PDF document encapsulated by the document object.
     *
     * @param document a <code>com.lowagie.text.Document</code> object that should
     * construct the PDF.
     * @throws ReportException if any exception occurs during the creation of
     * the report.  (All other exceptions are wrapped into a ReportException and
     * rethrown.)
     */
    public void writeReport(Document document) throws ReportException {
        try {
            //Reload the report data to make sure it is there for building the
            //sections below.
            getData().load();

            //Add information that the user can see if they look at the PDF properties.
            addReportMetaInformation(document);
            document.open();

            //Title section
            addReportHeader(document);

            //Report Parameters
            if (showReportParameters) {
                document.add(createReportParameters());
            }

            //Chart
            document.add(getChart());

            //Write a table of the results, separate from the chart
            document.add(addResultsTable());
        } catch (Exception e) {
            throw new ReportException(PropertyProvider.get(UNEXPECTED_REPORT_ERROR_TOKEN), e);
        }
    }

    /**
     * Get a table element populated with a chart.
     *
     * @return a <code>Element</code> populated with a chart.
     * @throws BadElementException if the table is assembled in a way that the
     * pdf library doesn't support.  (For example, if you have a table which is
     * two columns wide and you add a column with a colspan of 3.)
     * @throws ChartingException if the FormItemSummaryReportChart class
     * encounters any errors while creating the chart.
     * @throws IOException if any error occurs transforming the byte stream from
     * the <code>FormItemSummaryReportChart</code> into a PNG image.
     */
    private Element getChart() throws BadElementException, ChartingException, IOException {
        Table chartTable = ReportComponents.createSummaryTable(1);

        FormItemSummaryReportChart chartCreator = new FormItemSummaryReportChart();
        try {
            chartCreator.populateParameters(getData());
        } catch (Exception e) {
            throw new ChartingException(PropertyProvider.get(UNEXPECTED_CHARTING_ERROR_TOKEN),e);
        }
        chartCreator.setHeight(384);
        chartCreator.setWidth(1040);
        chartCreator.setChartFont(new Font("Dialog", Font.PLAIN, 24));

        BufferedImage chartImage = chartCreator.getChart();
        byte[] chartByteArray = ImageUtils.bufferedImageToByteArray(chartImage, "png");
        Cell chartCell = new Cell(Image.getInstance(chartByteArray));

        chartTable.addCell(chartCell);
        return chartTable;
    }

    /**
     * Create a table which will show the same data shown in the Pie chart in
     * tabular form.
     *
     * @return a <code>Table</code> which contains the textual results of the
     * query.
     * @throws BadElementException if the table is put together in a way that
     * the PDF engine doesn't like.  For example, if the table is only two
     * columns in width and you add a column with a 3 column colspan.
     */
    private Table addResultsTable() throws BadElementException {
        Table table = ReportComponents.createDetailedTable(2);
        table.setWidth(50f);
        table.addCell(ReportComponents.createDetailedHeaderCell(FIELD_VALUE));
        table.addCell(ReportComponents.createDetailedHeaderCell(FIELD_FREQUENCY));

        Map data = ((FormItemSummaryReportData)getData()).getSummaryData().getFieldValues();
        FormField formField = ((FormItemSummaryReportData)getData()).getFormField();

        for (Iterator it = data.keySet().iterator(); it.hasNext(); ) {
            String keyName = (String)it.next();
            Integer keyValue = (Integer)data.get(keyName);

            table.addCell(ReportComponents.createDetailedCell(FormDataFormatter.formatSimpleData(formField, keyName)));
            table.addCell(ReportComponents.createDetailedCell(keyValue.toString()));
        }

        return table;
    }
}
