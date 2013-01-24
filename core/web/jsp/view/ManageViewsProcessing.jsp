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
    info="Manage Views Processing"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.portfolio.view.IViewList,
            net.project.security.SessionManager,
            net.project.base.Module,
            net.project.base.PnetException,
            net.project.portfolio.view.ViewBuilder"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="viewBuilder" type="net.project.portfolio.view.ViewBuilder"  scope="session" />

<%
    String module = request.getParameter("module");
%>
<security:verifyAccess module="<%=Integer.valueOf(module).intValue()%>" action="view" />

<%
    String viewID = request.getParameter("viewID");
    String theAction = request.getParameter("theAction");

    if (theAction != null && theAction.equals("create")) {
        // Create
        viewBuilder.createView();
        pageContext.forward("/view/EditView.jsp");

    } else if (theAction != null && theAction.equals("modify")) {
        // Modify
        viewBuilder.editView(viewID);
        viewBuilder.load();
        pageContext.forward("/view/EditView.jsp");

    } else if (theAction != null && theAction.equals("remove")) {
        // Remove
        viewBuilder.removeView(viewID);
        viewBuilder.remove();
        pageContext.forward("/view/ManageViews.jsp");

    } else {
        throw new PnetException("Missing or unknown action in ManageViewsProcessing.jsp");
    }
%>
