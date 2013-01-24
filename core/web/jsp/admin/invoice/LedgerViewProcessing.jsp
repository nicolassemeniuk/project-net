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
|  Processes ledger view with user selected filters
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Ledger View Processing"
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
<jsp:useBean id="ledgerEntryCollection" class="net.project.billing.ledger.LedgerEntryCollection" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>
   	
	<%  if(request.getParameter("theAction").equals("submit")) {
			String status = request.getParameter("status");
			String category = request.getParameter("category");
			String group = request.getParameter("group");
		
			session.setAttribute("status", status);
			session.setAttribute("category", category);
			session.setAttribute("group", group);
		
			String payInfo = request.getParameter("payInfo");
			String folName = request.getParameter("folName");
			String partNumber = request.getParameter("partNumber");
		
			session.setAttribute("payInfo", payInfo);
			session.setAttribute("folName", folName);
			session.setAttribute("partNumber", partNumber);
		
	        session.setAttribute("displayLedger", "true" );
		
			if(request.getParameter("searchOption").equals("ledgerFilter")) {
				ledgerEntryCollection.setLedgerFilters(status, category, group);
				
			} else if(request.getParameter("searchOption").equals("userFilter")) {
				ledgerEntryCollection.setSearchCriteria(payInfo, folName, partNumber);
				
			} else {
				ledgerEntryCollection.setLedgerFilters(status, category, group);
				ledgerEntryCollection.setSearchCriteria(payInfo, folName, partNumber);
			}
			
		} else if(request.getParameter("theAction").equals("sort")) {	
			
			ledgerEntryCollection.setSortField(request.getParameter("sortField"));
			ledgerEntryCollection.setSortOrder(request.getParameter("sortOrder"));
		}
	%>

        <jsp:forward page="/admin/invoice/LedgerView.jsp">
			<jsp:param name="module" value='<%=Module.APPLICATION_SPACE%>'/>
			<jsp:param name="action" value='<%=Action.VIEW%>'/>
		</jsp:forward> 





