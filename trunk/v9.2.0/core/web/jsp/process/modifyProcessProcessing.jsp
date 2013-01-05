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
|   processing page for modifyProcess.jsp
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="modify process processing" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.process.Process,
            net.project.security.User,
            net.project.security.SecurityProvider,
            net.project.security.SessionManager;" 
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<%
Process m_process = new Process();

String m_space_id = user.getCurrentSpace().getID();
int module = securityProvider.getCheckedModuleID();
int action = securityProvider.getCheckedActionID();
String id = securityProvider.getCheckedObjectID();
if (module != net.project.base.Module.PROCESS)
	{
	throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.process.security.validationfailed.message"));
	}

if (id.length() > 0)
	{
	if (action == net.project.security.Action.MODIFY)
		{
		m_process.setID(id);
		}
	else
		{
		throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.process.security.validationfailed.message"));
		}
		
	}
else
	{
	if (action != net.project.security.Action.CREATE)
		{
		throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.process.security.validationfailed.message"));
		}
	}
	
m_process.setSpaceID(m_space_id);
m_process.setName(request.getParameter("processname"));
m_process.setDesc(request.getParameter("processdesc"));
m_process.store();     

response.sendRedirect(SessionManager.getJSPRootURL() + "/process/Main.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.VIEW);
%>


