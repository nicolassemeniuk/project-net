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

/*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+----------------------------------------------------------------------*/
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Processing page for form sharing"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.Module,
            net.project.form.FormDesigner,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.security.User,
            net.project.util.JSPUtils"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>
<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session"/>

<%
    if (JSPUtils.isEqual(request.getParameter("shareForm"), "true")) {
        //Store the forms we are going to share with in the FormDesigner.
        formDesigner.setSpacesToShareWithArray(request.getParameterValues("spaceToShareWith"));
    } else {
        formDesigner.getSpacesToShareWith().clear();
    }

    //Store the changes that we've made to form sharing policy
    formDesigner.store();

    //Forward to the next page in the list
    response.sendRedirect(SessionManager.getJSPRootURL() + "/form/designer/ActivateEdit.jsp?module="+Module.FORM+
                          "&action="+Action.MODIFY+"&id="+formDesigner.getID());
%>