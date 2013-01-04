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
|   processing page for modifyDeliverable.jsp
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="modify deliverable processing" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.process.Deliverable,
            net.project.security.SessionManager,
            net.project.security.SecurityProvider;" 
%>
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<%
Deliverable m_deliverable = new Deliverable();

int module = securityProvider.getCheckedModuleID();
int action = securityProvider.getCheckedActionID();
String id = securityProvider.getCheckedObjectID();
if ((module != net.project.base.Module.PROCESS) || (id.length() == 0))
	{
	throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.process.security.validationfailed.message"));
	}

if ((action == net.project.security.Action.MODIFY)  || (action == net.project.security.Action.DELETE))
	{
	m_deliverable.setID(id);
	m_deliverable.setPhaseID(request.getParameter("phase_id"));
	}
else if (action == net.project.security.Action.CREATE)
	{
	m_deliverable.setPhaseID(id);
	}
else 
	{
	throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.process.security.validationfailed.message"));
	}

m_deliverable.setName(request.getParameter("deliverablename"));
m_deliverable.setDesc(request.getParameter("deliverabledesc"));
m_deliverable.setComments(request.getParameter("deliverablecomments"));
m_deliverable.setStatus(request.getParameter("deliverablestatus"));
m_deliverable.setOptional(request.getParameter("deliverableoptional"));

if (action == net.project.security.Action.DELETE)
	{
	m_deliverable.remove();
	}
else
	{
	m_deliverable.store();
	}
	
if (action == net.project.security.Action.MODIFY)
    response.sendRedirect(SessionManager.getJSPRootURL() + "/process/ViewDeliverable.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.VIEW + "&id=" + m_deliverable.getID() + "&rl=1");
else
	response.sendRedirect(SessionManager.getJSPRootURL() + "/process/ViewPhase.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.VIEW + "&id=" + m_deliverable.getPhaseID());
%>