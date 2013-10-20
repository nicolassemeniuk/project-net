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
|   $Revision: 18995 $
|       $Date: 2009-03-05 08:36:26 -0200 (jue, 05 mar 2009) $
|     $Author: avinash $
|
+----------------------------------------------------------------------*/
package net.project.report;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.project.base.finder.FinderFilter;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.FinderGrouping;
import net.project.base.finder.FinderGroupingList;
import net.project.base.finder.FinderSorter;
import net.project.base.finder.FinderSorterList;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.HttpUtils;
import net.project.util.StringUtils;
import net.project.util.Validator;
import net.project.xml.XMLConstructionException;
import net.project.xml.XMLUtils;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

import org.apache.log4j.Logger;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Table;

/**
 * A reporting base class that provides a place to push up methods.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public abstract class AbstractReport implements IReport {
    /** A token pointing to the human readable name of the parameter section. */
    private String PARAMETER_SECTION_TITLE = PropertyProvider.get("prm.schedule.report.common.reportparameterstitle.name"); //"Report Parameters";
    /** A token pointing to the human-readable "Workspace Name:" label. */
    private String WORKSPACE_NAME = PropertyProvider.get("prm.schedule.report.common.workspace.name");
    /** A token pointing to the human-readable "Prepared By:" label. */
    private String PREPARED_BY = PropertyProvider.get("prm.schedule.report.common.preparedby.name"); //"Prepared by:";
    /** A token pointing to the human-readable "Date Prepared:" label. */
    private String DATE_PREPARED = PropertyProvider.get("prm.schedule.report.common.dateprepared.name"); //"Date Prepared:";
    /** A token used to construct individual report parameters for finder filters. */
    private String FILTERED_ON = "prm.schedule.report.common.filteredon.name";  //"Filtered On: {0}";
    /** A token used to construct individual report parameters for finder groupings. */
    private String GROUPED_BY = "prm.schedule.report.common.groupedby.name"; //"Grouped By: {0}";
    /** A token used to construct individual report parameters for finder sorters. */
    private String SORTED_BY = "prm.schedule.report.common.sortedby.name"; //"Sorted By: {0}";
    /** The id of the space we are reporting on. */
    private String spaceID;
    /** The URL of the chart.  (This is important if we aren't going to inline the image.) */
    private String chartURL;
    /** The height of the chart we are going to construct. */
    private String chartHeight;
    /** The width of the chart we are going to construct. */
    private String chartWidth;
    /** The scope we are going to be reporting on. */
    private ReportScope scope;

    /**
     * An object which is the mechanism to look up data for a report.  This is
     * really more of a "ReportDataController" object that the actual provider
     * of the data.
     */
    private ReportData data;
    /**
     * Which type of report we are producing, for example "Late Task Report" or
     * "Form Item Time Series".
     */
    private ReportType reportType;
    /**
     * Whether or not we should show the parameters that the user specified on
     * the report.  This is particularly helpful when reports are printed out
     * because is makes it easier to determine what the report is actually
     * telling you.
     */
    protected boolean showReportParameters;
    /** Whether or not the image should be part of the binary output stream. */
    protected boolean inlineImage;
    /**
     * Token points to: "Unable to create XML due to an xml construction error."
     */
    private String CONSTRUCTION_ERROR_TOKEN = "prm.report.abstractreport.constructionerror.message";
    /**
     * Token points to: "Unable to create XML due to a database error."
     */
    private String DATABASE_ERROR_TOKEN = "prm.report.abstractreport.databaseerror.message";
    /**
     * "{0} Parameter is required but was not provided";
     */
    private String MISSING_PARAMETER_TOKEN = "prm.report.abstractreport.missingparameter.message";
    /**
     * "{0} Parameter is present, but is not a valid number under {1} characters";
     */
    private String PARAMETER_UNDER_20_CHAR_EXCEPTION = "prm.report.abstractreport.numberparametertooshort.message";

    /**
     * Standard constructor which sets some required variables.
     *
     * @param data the data object that is going to be used to construct the
     * report.
     * @param reportType a <code>ReportType</code> variable which will be used
     * to return the name of the report and to create parameters to pass to the
     * charting servlet when getChartURL() is called.
     */
    public AbstractReport(ReportData data, ReportType reportType, ReportScope scope) {
        this.data = data;
        this.reportType = reportType;
        this.scope = scope;
    }

    /**
     * Get the parameters that were passed to this report in order to create it.
     *
     * @return a <code>List</code> contain strings which describe the parameters
     * passed to this report.
     */
    protected java.util.List getReportParameters() {
        ArrayList reportParameters = new ArrayList();

        //Show all filters
        Iterator selectedFilters = getFilterList().getSelectedFilters().iterator();
        while (selectedFilters.hasNext()) {
            FinderFilter currentFilter = (FinderFilter)selectedFilters.next();
            String filterDescription = currentFilter.getFilterDescription();
            if (!Validator.isBlankOrNull(filterDescription)) {
                reportParameters.add(PropertyProvider.get(FILTERED_ON, filterDescription));
            }
        }

        //Show all groupings
        Iterator selectedGroupings = getGroupingList().getSelectedGroupings().iterator();
        while (selectedGroupings.hasNext()) {
            FinderGrouping finderGrouping = (FinderGrouping) selectedGroupings.next();
            String groupingDescription = finderGrouping.getGroupDescription();
            if (!Validator.isBlankOrNull(groupingDescription)) {
                reportParameters.add(PropertyProvider.get(GROUPED_BY, groupingDescription));
            }
        }

       //Show all sorting
        Iterator selectedSortings = getSorterList().getSelectedSorters().iterator();
        while (selectedSortings.hasNext()) {
            FinderSorter finderSorter = (FinderSorter) selectedSortings.next();
            String sorterDescription = finderSorter.getSorterDescription();
            if (!Validator.isBlankOrNull(sorterDescription)) {
                reportParameters.add(PropertyProvider.get(SORTED_BY, sorterDescription));
            }
        }

        return reportParameters;
    }

    /**
     * Create an HTML table that shows all of the parameters that have been applied
     * to this report.
     *
     * @return a <code>Table</code> object that shows all of the parameters that
     * have been applied to this report.
     * @throws BadElementException if the reporting components are added in the
     * wrong order.  This should only happen if the report writer makes a mistake.
     */
    protected Table createReportParameters() throws BadElementException {
        Table reportParameterTable = ReportComponents.createParameterTable(1);

        //Section Header
        reportParameterTable.addCell(ReportComponents.createParameterHeaderCell(PARAMETER_SECTION_TITLE));

        //Iterate through all of the report parameters and add them
        Iterator reportParameters = getReportParameters().iterator();
        while (reportParameters.hasNext()) {
            String reportParameter = (String)reportParameters.next();
            if (!Validator.isBlankOrNull(reportParameter)) {
                reportParameterTable.addCell(ReportComponents.createParameterCell(reportParameter));
            }
        }

        return reportParameterTable;
    }

    /**
     * Get the URL where this report can find a chart that corresponds to the
     * information in the summary section of this report.
     *
     * @return a <code>String</code> value that contains the URL of a chart.
     */
    public String getChartURL() {
        return chartURL;
    }

    /**
     * Set the URL where this report can find a chart that corresponds to the
     * information in the report.
     *
     * @param chartURL a <code>String</code> value that contains the URL of a
     * chart.
     */
    public void setChartURL(String chartURL) {
        this.chartURL = chartURL;
    }

    /**
     * Returns this object's XML representation, including the XML version tag.
     * @return XML representation of this object
     * @throws SQLException 
     * @see net.project.persistence.IXMLPersistence#getXMLBody
     * @see net.project.persistence.IXMLPersistence#XML_VERSION
     */
    public String getXML() throws SQLException {
        try {
            return getXMLDocument().getXMLString();
        } catch (XMLDocumentException e) {
        	Logger.getLogger(AbstractReport.class).error("Unable to create XML due to an unexpected internal error");
            throw new XMLConstructionException(PropertyProvider.get(CONSTRUCTION_ERROR_TOKEN), e);
        } catch (PersistenceException e) {
        	Logger.getLogger(AbstractReport.class).error("Unable to create XML due to an unexpected database error");
            throw new XMLConstructionException(PropertyProvider.get(DATABASE_ERROR_TOKEN), e);
        }
    }

    /**
     * Returns this object's XML representation, without the XML version tag.
     * @return XML representation of this object
     * @throws SQLException 
     * @see net.project.persistence.IXMLPersistence#getXML
     */
    public String getXMLBody() throws SQLException {
        try{
            return getXMLDocument().getXMLBodyString();
        } catch (XMLDocumentException e) {
        	Logger.getLogger(AbstractReport.class).error("Unable to create XML due to an unexpected internal error");
            throw new XMLConstructionException(PropertyProvider.get(CONSTRUCTION_ERROR_TOKEN, "objectID"), e);
        } catch (PersistenceException e) {
        	Logger.getLogger(AbstractReport.class).error("Unable to create XML due to an unexpected database error");
            throw new XMLConstructionException(PropertyProvider.get(DATABASE_ERROR_TOKEN, "objectID"), e);
        }
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
    public abstract XMLDocument getXMLDocument() throws XMLDocumentException, PersistenceException, SQLException;

    /**
     * Get the data object that is to be used to construct this report.
     *
     * @return a <code>ReportData</code> subclass used to construct the report.
     */
    public ReportData getData() {
        return data;
    }

    /**
     * Set the report data subclass that is to be used to construct this report.
     *
     * @param data a <code>ReportData</code> subclass used to construct the
     * report.
     */
    public void setData(ReportData data) {
        this.data = data;
    }

    /**
     * Get the list of filters that can be applied to this report.
     *
     * @return a <code>FinderFilterList</code> object which contains all of the
     * possible filters that can be applied to this report.
     */
    public FinderFilterList getFilterList() {
        return data.getFilterList();
    }

    /**
     * Get the list of all {@link FinderSorter} objects
     * that can be applied to this report.
     *
     * @return a <code>FinderSorterList</code> object which contains all of the
     * possible <code>FinderSorter</code> objects that can be applied to this
     * Report.
     */
    public FinderSorterList getSorterList() {
        return data.getSorterList();
    }

    /**
     * Get the list of all {@link FinderGrouping}
     * objects that can be applied to this report.
     *
     * @return a <code>FinderGroupingList</code> object which contains all of the
     * possible <code>FinderGrouping</code> objects that can be applied to this
     * report.
     */
    public FinderGroupingList getGroupingList() {
        return data.getGroupingList();
    }

    /**
     * Get the Space ID that we are reporting on.
     *
     * @return a <code>String</code> value identifying the Space ID we are
     * reporting on.
     */
    public String getSpaceID() {
        return getData().getSpaceID();
    }

    /**
     * Set the space id that we are reporting on.
     *
     * @param spaceID a <code>String</code> value identifying the space that we
     * are reporting on.
     */
    public void setSpaceID(String spaceID) {
        getData().setSpaceID(spaceID);
    }

    /**
     * Get the name of the person who is preparing the report.
     *
     * @return a <code>String</code> value indicating the name of the person
     * who is repairing the report.
     */
    private String getPreparer() {
        return SessionManager.getUser().getDisplayName();
    }

    /**
     * Get the name of this report as it would appear in a list.
     *
     * @return a <code>String</code> value controlled by the token
     * prm.schedule.reports.latetaskreport.name that identifies this report
     * to a human. (As opposed to an instance that would identify it to Java.)
     */
    public String getReportName() {
        return reportType.getName();
    }

    /**
     * Get the enumerated type that represents this report.
     *
     * @return a <code>ReportType</code> variable that represents this report.
     */
    public ReportType getReportType() {
        return reportType;
    }

    /**
     * Get the scope from which data should be loaded.
     *
     * @return a <code>ReportScope</code> object which indicates how wide the
     * search should be.
     */
    public ReportScope getScope() {
        return scope;
    }

    /**
     * Set the scope of the data that should be loaded for the report.
     *
     * @param scope a <code>ReportScope</code> indicating how much data should
     * be loaded.
     */
    public void setScope(ReportScope scope) {
        this.scope = scope;
        getData().setScope(scope);
    }

    /**
     * Populate internal data structures based on the results of a form
     * submission.
     *
     * @param request the source of the data for this object.
     */
    public void populateParameters(HttpServletRequest request) {
        String spaceID = request.getParameter("objectID");
        setSpaceID(spaceID);

        String outputType = (request.getParameter("output") != null ? request.getParameter("output") : "html");
        showReportParameters = Boolean.valueOf((
            request.getParameter("showReportParameters") != null ?
            request.getParameter("showReportParameters") : "false"))
            .booleanValue();

        //Set the parameters for the input data
        if (ReportOutputType.getForID(outputType).equals(ReportOutputType.HTML_REPORT_OUTPUT_TYPE)) {
            chartHeight = "100";
            chartWidth = "400";
        } else {
            chartWidth = "-1";
            chartHeight = "-1";
        }

        inlineImage = ReportOutputType.getForID(outputType).isInlineImages();
        setChartURL(SessionManager.getJSPRootURL() + "/servlet/ChartingServlet?"
            + HttpUtils.getRedirectParameterString(request) + "&height=" +
            chartHeight + "&width=" + chartWidth + "&chartType=" +
            getReportType().getID());

        scope = ReportScope.getForID(request.getParameter("reportScope"));
        getData().setScope(scope);
        String assignmentTypeCheck = StringUtils.isNotEmpty(request.getParameter("assignmentTypeCheck")) ? request.getParameter("assignmentTypeCheck") : "";
        //Set any filtering or grouping parameters that the user has set up
        if(StringUtils.isNotBlank(assignmentTypeCheck)){
        	getData().clear();
        }
     
        data.updateParametersFromRequest(request);
        //Set assignment type user choose 
        String assignmentType = request.getParameter("assignmentType");
        data.setReportAssignmentType(ReportAssignmentType.getForID(assignmentType));
    }

    /**
     * Add the report parameters to the supplied XML document.  The Parameters
     * will be added in the format:
     *
     * <pre>
     * &lt;ReportParameters>
     *     &lt;ReportParameter>Filter on Start Date > 01/01/2002&lt;/ReportParameter>
     *     &lt;ReportParameter>Group by Resource Name&lt;/ReportParameter>
     * &lt;/ReportParameters>
     * </pre>
     *
     * @param doc the <code>XMLDocument</code> to which we are going to add
     * report parameters.
     * @throws net.project.xml.document.XMLDocumentException if an error occurs while adding the elements.
     * This shouldn't occur under normal circumstances.  It is designed to catch
     * design time errors that occur when someone closes an element without
     * opening one and those sort of programmatic errors.
     */
    protected void addReportParametersElements(XMLDocument doc) throws XMLDocumentException {
        //Report Parameters
        if (showReportParameters) {
            doc.startElement("ReportParameters");

            //Iterate through all of the report parameters and add them
            Iterator reportParameters = getReportParameters().iterator();
            while (reportParameters.hasNext()) {
                String reportParameter = (String)reportParameters.next();
                if (!Validator.isBlankOrNull(reportParameter)) {
                    doc.addElement("ReportParameter", reportParameter);
                }
            }

            doc.endElement();
        }
    }

    /**
     * Calling this method ensures that all of the prerequisite data has been
     * collected and is of a valid state.  It is the responsibility of the
     * <code>getXML()</code> and <code>writeReport()</code> methods to call this
     *
     *
     * @throws IllegalArgumentException (an unchecked exception) if any
     */
    public void validate() throws IllegalArgumentException {
        if ((spaceID == null) || (spaceID.trim().length() == 0)) {
            throw new IllegalArgumentException(PropertyProvider.get(MISSING_PARAMETER_TOKEN, "objectID"));
        }

        if (!Validator.isValidDatabaseIdentifier(spaceID)) {
            throw new IllegalArgumentException(PropertyProvider.get(PARAMETER_UNDER_20_CHAR_EXCEPTION, "objectID", "20"));
        }
    }

    /**
     * Add workspace, creator, and title information to the body of a PDF
     * document.  This code eliminates the numerous places where this was being
     * done before to introduce a common title look and feel.
     *
     * @param document a <code>com.lowagie.text.Document</code> to which we are
     * going to add the title and header information.
     * @throws DocumentException if this method attempts to incorrectly
     * structure the information.  This exception is mostly an internal one.
     */
    protected void addReportHeader(Document document) throws DocumentException {
        document.add(ReportComponents.createReportTitle(getReportName()));
        document.add(ReportComponents.createWorkspaceHeader(WORKSPACE_NAME +
            " " + SessionManager.getUser().getCurrentSpace().getName()));
        document.add(ReportComponents.createReportHeader(PREPARED_BY + " " +
            getPreparer()));
        document.add(ReportComponents.createReportHeader(DATE_PREPARED + " " +
            SessionManager.getUser().getDateFormatter().formatDate(new Date())));
    }

    /**
     * Add information that will be used to construct the title of the report to
     * the XML.
     *
     * @param doc a <code>net.project.xml.document.XMLDocument</code> to which
     * we are going to add the title information.
     * @throws XMLDocumentException
     */
    protected void addReportHeader(XMLDocument doc) throws XMLDocumentException {
        doc.startElement("ReportInformation");
        doc.addElement("Title", getReportType().getName());
        doc.addElement("WorkspaceName", SessionManager.getUser().getCurrentSpace().getName());
        doc.addElement("Creator", getPreparer());
        doc.addElement("CreationDate", XMLUtils.formatISODateTime(new Date()));
        doc.endElement();
    }

    /**
     * Add information to a PDF which is to be displayed in the document
     * properties.  This is information such as the report title, the
     * creator, and the creation date.
     *
     * @param document a <code>com.lowagie.text.Document</code> object to
     * which we are going to add the meta information.
     */
    protected void addReportMetaInformation(Document document) {
        document.addTitle(getReportType().getName());
        document.addCreator(getPreparer());
        document.addCreationDate();
    }

    /**
     * Get a list of fields that the "populateRequest" method expects to see in
     * a request, but were prepopulated prior to showing the user the parameters
     * page.  This is more than a little bit of a hack.
     *
     * @return a <code>Map</code> of name value pairs.
     */
    public Map getPrepopulatedParameters() {
        return Collections.EMPTY_MAP;
    }
}

