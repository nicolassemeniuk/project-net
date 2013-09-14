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

<%@page import="net.project.resource.PnPersonSalaryList"%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="rosterProcessing.  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User, 
    net.project.security.SecurityProvider,
    net.project.security.Action,
	net.project.base.property.PropertyProvider,
    net.project.resource.PersonSalaryList,
	net.project.resource.Person,
    net.project.base.Module,
    net.project.security.SessionManager,
    net.project.base.ObjectType,
    net.project.base.EventException,
    org.apache.log4j.Logger,
    net.project.base.EventFactory,
    net.project.events.EventType,
	net.project.hibernate.service.ServiceFactory,
	java.util.Date" 
%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="ownerUser" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="personSalaryList" class="net.project.resource.PersonSalaryList" scope="session" />

<security:verifyAccess action="view" module="<%= Module.SALARY %>" />

<%
	if (request.getParameter("theAction").equals("search"))
	{
		String searchKeyFrom = request.getParameter("searchFieldFrom").trim();
		String searchKeyTo = request.getParameter("searchFieldTo").trim();
		
		request.setAttribute("searchKeyFrom", searchKeyFrom);
		request.setAttribute("searchKeyTo", searchKeyTo);		
				
		// Roster.search takes nulls for wildcard.
		if ((searchKeyFrom == null) || searchKeyFrom.equals("*") || searchKeyFrom.equals(""))
		{
			searchKeyFrom = null;
			request.setAttribute("searchKeyFrom", "");			
		}
		
		// Roster.search takes nulls for wildcard.
		if ((searchKeyTo == null) || searchKeyTo.equals("*") || searchKeyTo.equals(""))
		{
			searchKeyTo = null;
			request.setAttribute("searchKeyTo", "");			
		}		
	
		Date startDate = null;
		if (searchKeyFrom != null)
			startDate = ownerUser.getDateFormatter().parseDateString(searchKeyFrom);
		
		Date endDate = null;
		if(searchKeyTo != null)
			endDate = ownerUser.getDateFormatter().parseDateString(searchKeyTo);
		
		personSalaryList.clear();	
		personSalaryList.setUser(ownerUser);
		personSalaryList.load(startDate, endDate);			
		
		pageContext.forward("/salary/PersonalSalary.jsp?module=" + Module.SALARY + "&user=" + ownerUser.getID() + "&mode=search");
	}
%>
