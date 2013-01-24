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

<%@ page
    contentType="text/html; charset=UTF-8"
    info="Apply Methodology Processing.  Emits no output."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.AuthorizationFailedException,
            net.project.security.SessionManager,
            net.project.base.Module,
            net.project.security.Action,
            net.project.security.User,
            net.project.methodology.MethodologyProvider,
            net.project.util.Validator"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<%@page import="net.project.base.property.PropertyProvider"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<security:verifyAccess action="modify"
                       module="<%=Module.APPLICATION_SPACE%>"/>

<%
    String theAction = request.getParameter("theAction");
            String methodologyID = request.getParameter("methodologyID");
        String workspaceID = request.getParameter("workspaceID");

    if (theAction != null && theAction.equals("apply")) {

        if (Validator.isBlankOrNull(methodologyID) || Validator.isBlankOrNull(workspaceID)) {
            throw new net.project.base.PnetException(PropertyProvider.get("prm.project.admin.utilities.invalidate.state.message")
                    + methodologyID + " / dest: " + workspaceID);
        }

        MethodologyProvider methodologyProvider = new MethodologyProvider();
        methodologyProvider.setUser (user);
        methodologyProvider.applyMethodology(methodologyID, workspaceID);
        
        response.sendRedirect (SessionManager.getJSPRootURL() + "/admin/utilities/ApplyMethodologyUtility.jsp?module=240");


    } else if (theAction != null && theAction.length() == 0) {
        throw new net.project.base.PnetException(PropertyProvider.get("prm.project.admin.utilities.missing.action.message"));

    } else {
        throw new net.project.base.PnetException("Unknown action '" + theAction + "' in ApplyMethodologyProcessing.jsp");
    }
%>

