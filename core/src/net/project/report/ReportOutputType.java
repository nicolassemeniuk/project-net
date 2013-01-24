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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$NAME.java,v $
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+--------------------------------------------------------------------------------------*/
package net.project.report;

import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;

/**
 * Typesafe enumeration of report output types.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ReportOutputType {
    /** The collection of all report output types available. */
    private static ArrayList reports = new ArrayList();

    /** PDF output type. */
    public static final ReportOutputType PDF_REPORT_OUTPUT_TYPE = new ReportOutputType("pdf", "application/pdf", true, "prm.report.outputtype.pdf.name");
    /** RTF output type.  Not fully implemented. */
    public static final ReportOutputType RTF_REPORT_OUTPUT_TYPE = new ReportOutputType("rtf", "application/rtf", true, "prm.report.outputtype.rtf.name");
    /**
     * HTML output type.  Not supported.  Please use
     * {@link #XML_REPORT_OUTPUT_TYPE} and XSLT to transform instead.
     */
    public static final ReportOutputType HTML_REPORT_OUTPUT_TYPE = new ReportOutputType("html", "text/html", false, "prm.report.outputtype.html.name");
    /**
     * XML output type.
     */
    public static final ReportOutputType XML_REPORT_OUTPUT_TYPE = new ReportOutputType("xml", "text/xml", false, "prm.report.outputtype.xml.name");

    /**
     * XLS (MS Excel) output type.
     */
    public static final ReportOutputType XLS_REPORT_OUTPUT_TYPE = new ReportOutputType("xls", "application/octet-stream", false, "prm.report.outputtype.xls.name");

    
    /**
     * Default output type.  Currently PDF.
     */
    public static final ReportOutputType DEFAULT_REPORT_OUTPUT_TYPE = PDF_REPORT_OUTPUT_TYPE;

    /**
     * Get the report output type that corresponds to the string provided to this
     * method.
     *
     * @param reportOutputTypeID a <code>String</code> value that identifies one
     * type of report.
     * @return a <code>ReportOutputType</code> that corresponds to the report
     * output type id identified by the parameter to this method.
     */
    public static ReportOutputType getForID(String reportOutputTypeID) {
        Iterator it = reports.iterator();
        ReportOutputType toReturn = DEFAULT_REPORT_OUTPUT_TYPE;

        while (it.hasNext()) {
            ReportOutputType currentReport = (ReportOutputType)it.next();

            if (currentReport.getID().equals(reportOutputTypeID)) {
                toReturn = currentReport;
            }
        }

        return toReturn;
    }

    /**
     * Get a <code>String</code> containing HTML radio buttons for each report
     * output type.
     *
     * @param htmlName a <code>String</code> containing the name that each radio
     * button should have in the html input tags that are returned form this method.
     * @param selected a <code>ReportOutputType</code> which should be selected
     * initially.
     * @return a <code>String</code> value containing HTML option tags.
     */
    public static String getHTMLRadioList(String htmlName, ReportOutputType selected) {
        return getHTMLRadioList(htmlName, (ReportOutputType[])reports.toArray(), selected);
    }

    /**
     * Get a <code>String</code> containing HTML radio buttons for each report
     * output type.
     *
     * @param htmlName a <code>String</code> containing the name that each radio
     * button should have in the html input tags that are returned form this method.
     * @param validOptions a <code>ReportOutputType[]</code> value which indicates
     * all of the report output types that should be displayed.
     * @param selected a <code>ReportOutputType</code> which should be selected
     * initially.
     * @return a <code>String</code> value containing HTML option tags for each
     * report output type specified in the parameter.
     */
    public static String getHTMLRadioList(String htmlName, ReportOutputType[] validOptions, ReportOutputType selected) {
        StringBuffer html = new StringBuffer();

        for (int i = 0; i < validOptions.length; i++) {
            html.append("<input type=\"radio\" name=\"").append(htmlName)
                .append("\" value=\"").append(validOptions[i].getID())
                .append("\"").append((validOptions[i].equals(selected) ? " checked" : ""))
                .append(">").append(validOptions[i].getName()).append("<br>");
        }

        return html.toString();
    }

    //--------------------------------------------------------------------------
    //Instance implementation
    //--------------------------------------------------------------------------

    /**
     * A string that uniquely identifies this report type to non-java entities,
     * such as JSP.
     */
    private String uniqueID;
    /**
     * The MIME type of the report.  This will be used by servlets so it knows
     * what type of data is being streamed.
     */
    private String mimeType;
    /**
     * Indicates whether images should be included inline (as binary data).  If
     * false, the image will be included as a URL.
     */
    private boolean inlineImages;
    /**
     * Points to a token value where a human-readable name for this report output
     * type can be found.
     */
    private String nameToken;

    /**
     * Standard private constructor designed to be used internally.
     *
     * @param reportTypeID a <code>String</code> value that uniquely identifies
     * this report.
     * @param mimeType a <code>String</code> value containing the mime type
     * identifier in which this report will be displayed to the user.
     * @param inlineImages a <code>boolean</code> value that indicates whether or
     * not the specified format can include images as part of their output stream.
     * If false, the report will need to include a URL that points to the image.
     * @param nameToken a <code>String</code> value pointing to the human-readable
     * name for this <code>ReportOutputType</code>.
     */
    private ReportOutputType(String reportTypeID, String mimeType, boolean inlineImages, String nameToken) {
        reports.add(this);
        this.uniqueID = reportTypeID;
        this.mimeType = mimeType;
        this.inlineImages = inlineImages;
        this.nameToken = nameToken;
    }

    /**
     * Get a <code>String</code> that uniquely identifies this report type id
     * outside of java classes.
     *
     * @return a <code>String</code> value that uniquely identifies this
     * ReportOutputType.
     */
    public String getID() {
        return uniqueID;
    }

    /**
     * Get the MIME type for this report output type.  This might be used by a
     * servlet for the content type.
     * @return a <code>String</code> value identifying the mime type.
     */
    public String getMIMEType() {
        return mimeType;
    }

    /**
     * Indicates whether images should be included inline (as binary data).  If
     * false, the image will be included as a URL.
     *
     * @return a <code>boolean</code> value indicating whether the image should
     * be included inline.
     */
    public boolean isInlineImages() {
        return inlineImages;
    }

    /**
     * Get a <code>String</code> representation of this report type id.
     *
     * @return a <code>String</code> value containing the unique id for this
     * object.
     * @see #getID
     */
    public String toString() {
        return uniqueID;
    }

    /**
     * Get a human-readable name for this output type.
     *
     * @return a <code>String</code> value that contains a human-readable name
     * for this output type.
     */
    public Object getName() {
        return PropertyProvider.get(nameToken);
    }
}
