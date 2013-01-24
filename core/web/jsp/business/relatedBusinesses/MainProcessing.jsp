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
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Processing page for related business commands."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.Module,
            net.project.base.PnetException,
            net.project.base.property.PropertyProvider, 
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.security.User,
            net.project.space.SpaceRelationship"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<%
    //Figure out what action we are performing
    String action = request.getParameter("action");

    if (action.equals(String.valueOf(Action.CREATE))) {
        //Make sure the correct relationship is stored in the space relationship
        request.getSession().putValue("relatedBusinessRelationship", SpaceRelationship.INFORMATION_PROVIDER);
        //Store the business space bean as the parent in this relationship
        request.getSession().putValue("relatedBusinessParent", user.getCurrentSpace());
        //Redirect to the creation page
        response.sendRedirect(SessionManager.getJSPRootURL()+"/business/CreateBusiness1.jsp?"+
                              "module="+Module.BUSINESS_SPACE+"&action="+Action.CREATE+
                              "&createRelationship=1");
    } else {
        //throw new PnetException("The action " + action + " is undefined for this tool.");
		throw new PnetException(PropertyProvider.get("prm.business.relatedBusinesses.mainProcessing.create.exception.message", new Object[] {action}));
    }
%>
