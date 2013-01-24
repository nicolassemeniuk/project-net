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
package net.project.report.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.property.PropertyProvider;
import net.project.report.MissingReportDataException;
import net.project.report.ReportException;
import net.project.report.ReportOutputType;
import net.project.report.ReportType;

public class NonHtmlReportingServlet extends AbstractReportServlet {
    private void handleNonHTMLReport(String outputTypeParam, String reportTypeParam, HttpServletRequest request, HttpServletResponse response) throws IOException, ReportException, MissingReportDataException, SQLException {
        //Create supporting objects for this method.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ReportOutputType outputType = ReportOutputType.getForID(outputTypeParam);
        ReportType reportType = ReportType.getForID(reportTypeParam);

        //Create the report
        createReport(request, baos, outputType, reportType);

        //Set the filename that should appear in the dialog box if the user
        //doesn't have acrobat installed and attempts to save the PDF to a
        //file.
        String outputFilename = reportType.getName();
        outputFilename = outputFilename.replaceAll("[ ]", "");
        outputFilename = outputFilename + "." + outputTypeParam;
        response.setHeader("Content-disposition", "inline; filename=" + outputFilename);

        //Prepare the response object to return data
        response.setContentType(outputType.getMIMEType());
        response.setContentLength(baos.size());

        //Stream the response to the servlet
        ServletOutputStream out = response.getOutputStream();
        baos.writeTo(out);
        out.flush();
        out.close();
    }

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
            //
            if (!outputTypeParam.equals(ReportOutputType.HTML_REPORT_OUTPUT_TYPE.getID())) {
                handleNonHTMLReport(outputTypeParam, reportTypeParam, request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

}
