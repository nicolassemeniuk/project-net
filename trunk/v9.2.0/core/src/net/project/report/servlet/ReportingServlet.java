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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+----------------------------------------------------------------------*/
package net.project.report.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.property.PropertyProvider;
import net.project.report.IReport;
import net.project.report.MissingReportDataException;
import net.project.report.ReportOutputType;
import net.project.security.SessionManager;
import net.project.util.Validator;

/**
 * Servlet that streams a report back to the client.  A report can be in the form
 * of html, pdf, or other binary formats, hence the need to have a servlet.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class ReportingServlet extends AbstractReportServlet {
    /**
     * Create a report output stream from the parameters provided.
     *
     * @param request the parameters that were originally submitted to the servlet.
     * These will be used to populate the parameters in the report.
     * @param out the <code>OutputStream</code> that will be written to.
     * @param format an output type that the report will be written as, for example
     * xml or pdf.
     * @param type a <code>ReportType</code> object representing which report the
     * user has requested.  (For example, late task report.)
     * @throws IOException if there is a problem writing data produced by the
     * report to the output stream.
     * @throws ReportException if any exception occurs during the creation of the
     * report by the reporting object.
     * @throws MissingReportDataException if the call to
     * {@link net.project.report.IReport#populateParameters} indicates that a
     * data value required to create the report was not supplied.
     * @throws SQLException
     */


    /**
     * Standard response to an HTTP servlet get request.
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     * @see javax.servlet.http.HttpServlet#doGet
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
                   //Determine which type of report we are creating
                   String reportTypeParam = request.getParameter("reportType");
                   if ((reportTypeParam == null) || (reportTypeParam.trim().length() == 0)) {
                       throw new MissingReportDataException(PropertyProvider.get(PARAMETER_MISSING_TOKEN, "reportType"));
                   }

                   //Determine what format we are going to display the data to the
                   //user (pdf, xml, etc)
                   String outputTypeParam = (request.getParameter("output") != null ? request.getParameter("output") : "pdf");
                                                         
                   if (outputTypeParam.equals(ReportOutputType.HTML_REPORT_OUTPUT_TYPE.getID())) {
                       handleHTMLReport(request, response);
                   } else if (outputTypeParam.equals(ReportOutputType.XLS_REPORT_OUTPUT_TYPE.getID())) {
                	   handleXLSReport(request, response);
                   } else {
                       redirectToNonHtmlServlet(request, response);
                   }
               } catch (Exception e) {
                   handleException(request, response, e);
               }

    }

    private void redirectToNonHtmlServlet(HttpServletRequest request, HttpServletResponse response) throws MissingReportDataException, IOException {
        //Populate the report before forwarding.  Otherwise, we might
        //lose parameters if the get string is too long.
        IReport report = (IReport) request.getSession().getAttribute("report");
        report.populateParameters(request);
        String url = "../servlet/NonHtmlReportingServlet" + getParametersUrl(request);
        response.sendRedirect(url);
    }

    private String getParametersUrl(HttpServletRequest request) {
        String url = "";
        url += "?reportType=" + request.getParameter("reportType");
        url += "&module=" + request.getParameter("module");
        url += "&objectID=" + request.getParameter("objectID");
        url += "&reportScope=" + request.getParameter("reportScope");
        url += "&output=" + request.getParameter("output");
        url += "&assignmentType=" + request.getParameter("assignmentType");

        if (Validator.isBlankOrNull(request.getParameter("processAdditionalParameters"))) {
            url += "&processAdditionalParameters=" + request.getParameter("processAdditionalParameters");
        }
        return url;
    }

    private void handleHTMLReport(HttpServletRequest request, HttpServletResponse response) throws MissingReportDataException, IOException {
        //Populate the report before forwarding.  Otherwise, we might
        //lose parameters if the get string is too long.
        IReport report = (IReport) request.getSession().getAttribute("report");
        report.populateParameters(request);
        String url = SessionManager.getJSPRootURL() + "/report/ShowHTMLReport.jsp" + getParametersUrl(request);

        response.sendRedirect(url);
    }

    private void handleXLSReport(HttpServletRequest request, HttpServletResponse response) throws MissingReportDataException, IOException {
        //Populate the report before forwarding.  Otherwise, we might
        //lose parameters if the get string is too long.
        IReport report = (IReport) request.getSession().getAttribute("report");
        report.populateParameters(request);
        String url = SessionManager.getJSPRootURL() + "/report/ShowXLSReport.jsp" + getParametersUrl(request);

        response.sendRedirect(url);
    }
}

