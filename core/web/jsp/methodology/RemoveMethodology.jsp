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
    info="Remove Methodology"
    language="java"
    errorPage="/errors.jsp"
    import="org.apache.log4j.Logger,
            net.project.methodology.MethodologySpaceBean,
            net.project.persistence.PersistenceException,
            net.project.security.SessionManager,
            net.project.security.User"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="methodologySpace" class="net.project.methodology.MethodologySpaceBean" scope="session" />
<jsp:useBean id="historyTagHistoryObject" class="net.project.gui.history.History" scope="session" />

<%
    //Make sure that the id parameter has been passed to this page, that will be
    //used to decide what methodology to remove.
    String id = (String)request.getParameter("objectID");

    if ((id == null) || (id.equals(""))) {
    	Logger.getLogger(this.getClass()).debug("The id parameter was not passed to RemoveMethodology.jsp.  " +
                                   "This parameter is required.");
        throw new PersistenceException("Unable to remove methodology space, an unexpected error has occurred");
    }
%>
<%----- Verify security ------------------------------------------------------%>
<security:verifyAccess action="delete" module="<%=net.project.base.Module.METHODOLOGY_SPACE%>"/>

<%----- Do the actual removal ------------------------------------------------%>
<%
    methodologySpace.setID(id);
    methodologySpace.remove();
    methodologySpace.clear();
    historyTagHistoryObject=new net.project.gui.history.History();
    pageContext.setAttribute("historyTagHistoryObject",historyTagHistoryObject,pageContext.SESSION_SCOPE);
%>

<%----- Redirect to the appropriate location ---------------------------------%>
<%
    response.sendRedirect(SessionManager.getJSPRootURL() + "/methodology/MethodologyList.jsp");
%>
