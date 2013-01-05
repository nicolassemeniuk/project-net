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

<%--------------------------------------------------------------------
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|  Lists all Licenses in the system
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="License List View"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.security.Action,
            net.project.security.SessionManager,
			net.project.xml.XMLFormatter,
            net.project.license.LicenseException"
%>

<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="licenseCollection" class="net.project.license.LicenseCollection" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="request"/>


<security:verifyAccess module="<%=net.project.base.Module.APPLICATION_SPACE%>" 
                       action="view" /> 
   	
<%
    try {
        String userIdentity = (String)session.getAttribute("userIdentity");

        if (userIdentity.equals("applicationAdministrator")) {
            licenseCollection.load();
        } else if (userIdentity.equals("responsibleUser")) {
            licenseCollection.loadResponsibleForUser(user);
        }

    } catch (LicenseException e) {
        // A problem occurred loading one or more licenses in the colleciton
        // This is not the same as a general PersistenceException
        // The collection is still valid
        // Unfortunately we have no information that could help a user uncover
        // which license was in error
        // As a result no error is displayed; simply list the licenses that
        // were loaded
    }
%>
<tr align="left" valign="top">
	<td>&nbsp;</td>
    <td colspan="4" class="warnText">
     <%
     //Show any error or warnning found during license search
     errorReporter = licenseCollection.getErrorReporter();
        if (errorReporter.errorsFound()||errorReporter.warningsFound()) {
            XMLFormatter formatter = new XMLFormatter();
            formatter.setXML(errorReporter.getXML());
            formatter.setStylesheet("/base/xsl/error-report.xsl");
            out.println(formatter.getPresentation());
            //Clear out the error reporter now that we are done with it
            errorReporter.clear();
        }
    %>
    </td>
    <td>&nbsp;</td>
</tr>
<pnet-xml:transform name="licenseCollection" scope="session" stylesheet='/admin/license/include/xsl/licenses-list-view.xsl' />






