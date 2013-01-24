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
    info="Gateway to forms"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.base.Module,
            net.project.security.Action,
            net.project.security.SessionManager"
%>
<%@ include file="/base/taglibInclude.jsp"%>

<%--------------------------------------------------------------------
  This page is automatically forwarded to the formlist that was
  indicated by the calling page.  The reason for this page is to set
  up the breadcrumbs correctly.  Normally, when you click on a form
  in the sidebar, we skip the forms main page, which would set the
  module.
  --------------------------------------------------------------------%>

<history:history>
    <history:module display='<%=PropertyProvider.get("prm.form.main.module.history")%>' 
                    jspPage='<%=SessionManager.getJSPRootURL() + "/form/Main.jsp"%>'
                    queryString='<%="module=" + Module.FORM + "&action=" + Action.VIEW%>'
    />
</history:history>

<%
  //Redirect to the page the user requested
  //Avinash:--------------------------------------------------------------------------
  response.sendRedirect(SessionManager.getJSPRootURL() + "/form/FormList.jsp?module=" + Module.FORM + "&id=" + request.getParameter("id"));
  //Avinash:---------------------------------------------------------------------------
%>