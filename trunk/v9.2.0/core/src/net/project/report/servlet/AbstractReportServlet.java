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

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.servlet.BaseSecurityServlet;
import net.project.report.IReport;
import net.project.report.MissingReportDataException;
import net.project.report.ReportException;
import net.project.report.ReportOutputType;
import net.project.report.ReportScope;
import net.project.report.ReportType;
import net.project.report.ReportWriter;

/**
 * Created by IntelliJ IDEA.
 * User: Oleg
 * Date: 14.07.2007
 * Time: 10:34:14
 * To change this template use File | Settings | File Templates.
 */
public class AbstractReportServlet extends BaseSecurityServlet {
    protected void createReport(HttpServletRequest request, OutputStream out,
                                ReportOutputType format, ReportType type) throws IOException,
            ReportException, MissingReportDataException, SQLException {

        //Get the correct type of report for the parameters given to this
        //method
            IReport ltr;
            try {  
                ltr = (IReport) request.getSession().getAttribute("report");
            } catch (Exception e) {
                ltr = type.getInstance(ReportScope.getForID(request.getParameter("reportScope")));                
                ltr.populateParameters(request);
            }

        //We are going to handle XML slightly differently.  Because it is only
        //one format, I'm not going to be too picky about making this code
        //elegant.  If we add more, we probably should start delegating this work
        //out to the report type objects.
        if (!format.equals(ReportOutputType.XML_REPORT_OUTPUT_TYPE)) {
            //Create the report writer object that will be reponsible for writing
            //in the correct output format.
            ReportWriter reportWriter = new ReportWriter(format, out);
            try {
                ltr.writeReport(reportWriter.getDocument());
            } finally {
                reportWriter.close();
            }
        } else {
            out.write(ltr.getXML().getBytes());
        }
    }

    /**
     * Standard response to an HTTP servlet post request.
     *
     * @param request
     * @param response
     * @throws IOException
     * @throws ServletException
     * @see javax.servlet.http.HttpServlet#doPost
     */

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        doGet(request, response);
    }

    /**
     * Token points to: "Unable to create report.  Parameter {0} not passed to
     * the report.  This field is required.";
     */

    public static String PARAMETER_MISSING_TOKEN = "prm.report.servlet.parametermissing.message";
}
