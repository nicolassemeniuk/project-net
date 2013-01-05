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
|  Displays an invoice list
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="View Invoice List"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.security.Action,
            net.project.security.SessionManager,
			net.project.xml.XMLFormatter"
%>

<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="invoiceCollection" class="net.project.billing.invoice.InvoiceCollection" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>
   	
<%  String styleSheet = "/admin/invoice/include/xsl/invoice-list.xsl";
    //System.out.println("ViewInvoiceList displayInvoice is : \n" + request.getParameter("displayInvoice"));
    //System.out.println("InvoiceCollection xml is : \n"  + invoiceCollection.getXML());
    if (session.getAttribute("displayInvoice") != null && session.getAttribute("displayInvoice").equals("true")) {
%>

<pnet-xml:transform name="invoiceCollection" scope="session" stylesheet='<%= styleSheet %>' />
<% } else { %>
    Enter search criteria to list invoices. 
<% } %>
