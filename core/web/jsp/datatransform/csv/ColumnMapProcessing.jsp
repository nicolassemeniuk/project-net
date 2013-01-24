<%--
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
--%>

<%
/*----------------------------------------------------------------------+
|
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
+----------------------------------------------------------------------*/
%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="CSV Import"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.PnetException,
			net.project.security.User,
			net.project.security.SessionManager,
			net.project.datatransform.csv.map.ColumnMap,
			net.project.datatransform.csv.transformer.IDataTransformer,
			net.project.base.attribute.*,
			java.util.Iterator,
            net.project.base.Module,
            net.project.security.Action"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="csv" class="net.project.datatransform.csv.CSV" scope="session" />
<jsp:useBean id="attributes" class="net.project.base.attribute.AttributeCollection" scope="session" />
<jsp:useBean id="columnMaps" class="net.project.datatransform.csv.map.ColumnMaps" scope="session" />
<jsp:useBean id="columnMapProcessing" class="net.project.datatransform.csv.ColumnMapPageProcessor" scope="request" />
<jsp:useBean id="csvWizard" class="net.project.datatransform.csv.CSVWizard" scope="session" />

<%
	String theAction = request.getParameter("theAction");
	if (theAction != null && theAction.equals("next")) {
		String urlString = columnMapProcessing.extractColumnMappings(request ,csv , attributes , columnMaps);

		if(columnMaps.size() == 0) {
			csvWizard.validateColumnMaps(columnMaps);
			response.sendRedirect(SessionManager.getJSPRootURL() +
                "/datatransform/csv/ColumnMap.jsp?module="+Module.FORM+
                "&action="+Action.MODIFY);
		} else {
			boolean checkForAttribute = true;
			Iterator itr = columnMaps.iterator();

            while ( itr.hasNext()) {
                ColumnMap columnMap = (ColumnMap)itr.next();
                Iterator itrDataTransformer = columnMap.getDataTransformerList().iterator();

                while (itrDataTransformer.hasNext()) {
                    IDataTransformer idt = (IDataTransformer) itrDataTransformer.next();
                    IAttribute iAttribute = idt.getAttribute();

                    if(iAttribute instanceof DateAttribute ||
                       iAttribute instanceof DomainListAttribute ||
                       iAttribute instanceof PersonListAttribute ||
                       iAttribute instanceof BooleanAttribute){
                        checkForAttribute=false;
                    }
                }
			}
			if(checkForAttribute) {
				response.sendRedirect(SessionManager.getJSPRootURL()+
                    "/datatransform/csv/DataResolutionProcessing.jsp?module="+
                    Module.FORM+"&action="+Action.MODIFY);
			} else {
				response.sendRedirect(SessionManager.getJSPRootURL()+urlString);
			}
		}
	} else if (theAction != null && theAction.equals("back")) {
		response.sendRedirect(SessionManager.getJSPRootURL() + "/datatransform/csv/FileUpload.jsp");

	} else {
		throw new PnetException("Unknown or missing action '" + theAction + "' in ColumnMapProcessing.jsp");
	}
%>
