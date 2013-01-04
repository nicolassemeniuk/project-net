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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.base.Module;
import net.project.report.IReport;
import net.project.report.MissingReportDataException;
import net.project.report.ReportScope;
import net.project.report.ReportType;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.ISpaceTypes;

public class ShowReportServlet extends AbstractReportServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		User user = (User)request.getSession().getAttribute("user");
		
		ReportType reportType = ReportType.getForID(request.getParameter("reportType"));
	    IReport report = null;
	    try{
	    	report = reportType.getInstance(user.getCurrentSpace().getType().equals(ISpaceTypes.ENTERPRISE_SPACE) ? ReportScope.GLOBAL : ReportScope.SPACE);
	    	//From dashboard work complete report contains only 100% complete task. 
	    	if("wcr".equals(request.getParameter("reportType"))){
	    		report.getData().setHundredPercentComplete(true);
	    	}
	    	report.populateParameters(request);
	    } catch(Exception e) {
	    }
	    request.getSession().removeAttribute("report");
	    request.getSession().setAttribute("report", report);
	    
	    String processParameters = request.getParameter("processAdditionalParameters");
	    if ((processParameters != null) && (processParameters.equals("true"))) {
	        try {
				report.populateParameters(request);
			} catch (MissingReportDataException e) {
				e.printStackTrace();
			}
	    }
	    
	    String rt = request.getParameter("reportType");
	    
	    String url = SessionManager.getJSPRootURL() + "/report/ShowHTMLReport.jsp" + getParameters(rt);
	    
	    response.sendRedirect(url);
	}
	
	private String getParameters(String rt) {
		String parameter = "?reportType=" + rt;
		parameter += "&module=" + Module.REPORT;
		parameter += "&objectID=5018&reportScope=10&output=html&assignmentType=task&processAdditionalParameters=null";
		return parameter;
	}

}
