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
package net.project.form.report.formitemtimeseries;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

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
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.util.ImageUtils;
import net.project.util.NumberFormat;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;

/**
 * This report is remarkably similar to the
 * {@link net.project.form.report.formitemsummaryreport.FormItemSummaryReport}
 * except that it shows the same data over the history of the report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class FormItemTimeSeriesReport extends AbstractReport {
    /** Human-readable column name for the Date column. */
    private String DATE = PropertyProvider.get("prm.form.report.formitemtimeseries.field.date.name"); // "Date";
    /** Human-readable column name for the Field Value column. */
    private String FIELD_VALUE = PropertyProvider.get("prm.form.report.formitemtimeseries.field.fieldname.name"); // "Field Value";
    /** Human-readable column name for the Frequency column. */
    private String FREQUENCY = PropertyProvider.get("prm.form.report.formitemtimeseries.field.frequency.name"); // "Frequency";
    /** Token to look up the "Form Name: {0}" token used to construct the FormName report parameter. */
    private String FORM_FILTER_PARAMETER = "prm.form.report.common.formfilterparameter.name";
    /** Token to look up the "Field Name: {0}" token used to construct the FieldName report parameter. */
    private String FIELD_PARAMETER = "prm.form.report.common.fieldfilterparameter.name";
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
     * Get the parameters that were passed to this report in order to create it.
     *
     * @return a <code>List</code> contain strings which describe the parameters
     * passed to this report.
     */
    protected List getReportParameters() {
        List reportParameters = new ArrayList();

        reportParameters.add(PropertyProvider.get(FORM_FILTER_PARAMETER,
            ((FormItemTimeSeriesReportData)getData()).getForm().getName()));
        reportParameters.add(PropertyProvider.get(FIELD_PARAMETER,
            ((FormItemTimeSeriesReportData)getData()).getFormField().getFieldLabel()));
        reportParameters.addAll(super.getReportParameters());

        return reportParameters;
    }

    /**
     * Standard constructor which creates an instance of a
     * <code>FormItemTimeSeriesReport</code>.
     */
    public FormItemTimeSeriesReport(ReportScope scope) {
        super(new FormItemTimeSeriesReportData(), ReportType.FORM_ITEM_TIME_SERIES, scope);
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

        toReturn.put("formID", ((FormItemTimeSeriesReportData)getData()).getFormID());
        toReturn.put("formFieldID", ((FormItemTimeSeriesReportData)getData()).getFieldID());

        return toReturn;
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
        //Reload the report data to make sure it is there for building the
        //sections below.
        getData().load();

        //Build the document
        XMLDocument doc = new XMLDocument();
        doc.startElement("FormItemTimeSeriesReport");

        //Add the meta information
        addReportHeader(doc);

        //Add the report parameters to the report
        addReportParametersElements(doc);

        //Add the chart
        doc.startElement("Chart");
        doc.addElement("ChartURL", getChartURL());
        doc.endElement();  //Chart

        //Add the summarized field data
        doc.startElement("SummaryData");

        doc.startElement("ColumnDefinition");
        doc.addAttribute("Count", "3");
        doc.addElement("Column", DATE);
        doc.addElement("Column", FIELD_VALUE);
        doc.addElement("Column", FREQUENCY);
        doc.endElement(); //ColumnDefinition

        FormItemTimeSeriesData data = ((FormItemTimeSeriesReportData)getData()).getData();
        doc.startElement("DataRows");
        SortedSet dates = data.getAllDatesInSeries();
        SortedSet fieldValues = data.getAllFieldValues();
        DateFormat df = SessionManager.getUser().getDateFormatter();
        NumberFormat nf = NumberFormat.getInstance();
        FormField field = ((FormItemTimeSeriesReportData)getData()).getFormField();

        for (Iterator dateIT = dates.iterator(); dateIT.hasNext(); ) {
            Date currentDate = (Date)dateIT.next();
            for (Iterator fieldValueIT = fieldValues.iterator(); fieldValueIT.hasNext(); ) {
                doc.startElement("DataRow");
                String currentFieldValue = (String)fieldValueIT.next();
                String formattedFV = FormDataFormatter.formatSimpleData(field, currentFieldValue);

                doc.addElement("Column", df.formatDate(currentDate));
                doc.addElement("Column", formattedFV);

                Integer countOnDate = data.getCountOnDate(currentDate, currentFieldValue);
                int count = (countOnDate != null ? countOnDate.intValue() : 0);
                doc.addElement("Column", nf.formatNumber(count));
                doc.endElement();  //DataRow
            }
        }
        doc.endElement(); //DataRows

        doc.endElement(); //SummaryData

        //Return the constructed document
        return doc;
    }

    /**
     * Write the report to a PDF document encapsulated by the document object.
     *
     * @param document a <code>com.lowagie.text.Document</code> object that should
     * construct the PDF.
     * @throws ReportException when any exception occurs while creating the
     * report.  If an exception other than a ReportException occurs, it will be
     * wrapped in a report exception and rethrown.
     */
    public void writeReport(Document document) throws ReportException {
        try {
            //Reload the report data to make sure it is there for building the
            //sections below.
            getData().load();

            //Add information that the user can see if they look at the properties
            //of their PDF.
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

            //Table of results
            document.add(getResultsTable());
        } catch (Exception e) {
            throw new ReportException(
                PropertyProvider.get(UNEXPECTED_REPORT_ERROR_TOKEN),e);
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
    private Table getChart() throws BadElementException, ChartingException, IOException {
        Table chartTable = ReportComponents.createSummaryTable(1);

        FormItemTimeSeriesStackedBarChart chartCreator = new FormItemTimeSeriesStackedBarChart();
        try {
            chartCreator.populateParameters(getData());
        } catch (Exception e) {
            throw new ChartingException(
                PropertyProvider.get(UNEXPECTED_CHARTING_ERROR_TOKEN),e);
        }
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
    private Table getResultsTable() throws BadElementException {
        Table table = ReportComponents.createDetailedTable(3);
        table.setWidth(50f);
        table.addCell(ReportComponents.createDetailedHeaderCell(DATE));
        table.addCell(ReportComponents.createDetailedHeaderCell(FIELD_VALUE));
        table.addCell(ReportComponents.createDetailedHeaderCell(FREQUENCY));

        //Now draw lines underneath each row for the data values.
        table.setDefaultCellBorder(Rectangle.BOTTOM);
        table.setDefaultCellBorderWidth(3);

        FormItemTimeSeriesData data = ((FormItemTimeSeriesReportData)getData()).getData();
        Set dates = data.getAllDatesInSeries();
        Set fieldValues = data.getAllFieldValues();
        DateFormat df = SessionManager.getUser().getDateFormatter();
        NumberFormat nf = NumberFormat.getInstance();
        FormField field = ((FormItemTimeSeriesReportData)getData()).getFormField();

        for (Iterator dateIT = dates.iterator(); dateIT.hasNext(); ) {
            Date currentDate = (Date)dateIT.next();

            for (Iterator fieldValueIT = fieldValues.iterator(); fieldValueIT.hasNext(); ) {
                String currentFieldValue = (String)fieldValueIT.next();
                String formattedFV = FormDataFormatter.formatSimpleData(field, currentFieldValue);

                table.addCell(ReportComponents.createDetailedCell(df.formatDate(currentDate)));
                table.addCell(ReportComponents.createDetailedCell(formattedFV));

                Integer countOnDate = data.getCountOnDate(currentDate, currentFieldValue);
                int count = (countOnDate != null ? countOnDate.intValue() : 0);
                table.addCell(ReportComponents.createDetailedCell(nf.formatNumber(count)));
            }
        }

        return table;
    }
}
