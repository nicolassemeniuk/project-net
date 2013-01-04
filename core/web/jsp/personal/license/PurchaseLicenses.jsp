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
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.SessionManager,
            net.project.base.Module,
            net.project.base.IUniqueTransaction,
            net.project.util.UniqueTransaction"
%>
<%@ include file="/base/taglibInclude.jsp"%>

<%
    //Create a globally unique transaction for this registration.  This prevents
    //any credit card transactions from being submitted twice.
    IUniqueTransaction uniqueTransaction = new UniqueTransaction(request.getRemoteAddr());
    session.setAttribute("uniqueTransaction", uniqueTransaction);
%>

<jsp:forward page="/creditcard/CreditCardController.jsp">
    <jsp:param name="theAction" value="initialize"/>
    <jsp:param name="createLicense" value="true"/>
    <jsp:param name="nextPage" value='<%=SessionManager.getJSPRootURL() + "/personal/license/LicenseManager.jsp?module="+Module.PERSONAL_SPACE%>'/>
    <jsp:param name="previousPage" value='<%=SessionManager.getJSPRootURL() + "/personal/license/LicenseManager.jsp?module="+Module.PERSONAL_SPACE%>'/>
    <jsp:param name="cancelPage" value='<%=SessionManager.getJSPRootURL() + "/personal/license/LicenseManager.jsp?module="+Module.PERSONAL_SPACE%>'/>
    <jsp:param name="useUserFromSession" value="true"/>
</jsp:forward>

