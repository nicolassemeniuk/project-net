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
    info="Material Directory Processing" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User, 
    net.project.security.SecurityProvider,
    net.project.security.Action,
	net.project.base.property.PropertyProvider,
    net.project.base.Module,
    net.project.security.SessionManager,
    net.project.base.ObjectType,
    net.project.base.EventException,
    org.apache.log4j.Logger,
    net.project.base.EventFactory,
    net.project.events.EventType,
    net.project.hibernate.service.ServiceFactory" 
%>

<jsp:useBean id="materialBeanList" class="net.project.material.MaterialBeanList" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%
	// Make sure a security check has been passed to view a discussion group
	int module = securityProvider.getCheckedModuleID();
	int action = securityProvider.getCheckedActionID();
    String id = securityProvider.getCheckedObjectID();

	if ((id.length() > 0) || 
        (module != net.project.base.Module.MATERIAL))
	    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.material.main.authorizationfailed.message"));
	
	if (request.getParameter("theAction").equals("search"))
	{
		// Name search
		String key = request.getParameter("key");
		
		// Roster search takes nulls for wildcard.
		if (key == null || key.equals("*") || key.equals(""))
		{
			key = null;
			request.setAttribute("searchKey", "");			
		}
		else
			request.setAttribute("searchKey", key);
				
		// Material Type search always sends a value, at least 0 for any
		String materialTypeId = request.getParameter("materialTypeId");
		request.setAttribute("materialTypeId", materialTypeId);
		
		// Consumable search
		String consumable = request.getParameter("consumable");
		
		// Consumable checkbox sends a null value when is not selected
		if (consumable == null)
		{
			consumable = null;
			request.setAttribute("consumable", "");			
		}
		// consumable == "on"
		else
			request.setAttribute("consumable", consumable);
		
		// Minimum cost search
		String minCost = request.getParameter("minCost");
		
		// Roster search takes nulls for wildcard.
		if (minCost == null || minCost.equals(""))
		{
			minCost = null;
			request.setAttribute("minCost", "");			
		}
		else
			request.setAttribute("minCost", minCost);
		
		// Maximum cost search
		String maxCost = request.getParameter("maxCost");
				
		// Roster search takes nulls for wildcard.
		if (maxCost == null || maxCost.equals(""))
		{
			maxCost = null;
			request.setAttribute("maxCost", "");			
		}
		else
			request.setAttribute("maxCost", maxCost);		

		materialBeanList.clear();
		materialBeanList.load(key, materialTypeId, consumable, minCost, maxCost);
		
		pageContext.forward("/material/MaterialDirectory.jsp?module="+Module.MATERIAL + "&mode=search&currentTab=" + session.getAttribute("currentTab"));
	}
%>
