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
<%
/*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
| Non-authentication initialization page.  Loads Properties / brand etc.
+----------------------------------------------------------------------*/
%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Initialization page. Omits no output."
    language="java"
    errorPage="/errors.jsp"
    import="net.project.security.User,
			net.project.security.SessionManager,
			net.project.base.PnetException,
			net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<%
	// Kill all the old session objects.
	// IMPORTANT:  useBeans must follow this code block
	String requestedPage = (String) session.getValue("requestedPage");
    String scheme = (String) session.getValue("SiteScheme");
    String host = (String) session.getValue("SiteHost");
	String[] values = session.getValueNames();
    if (values != null) {
    	for (int i=0;i<values.length;i++) {
        	session.removeValue(values[i]);
		}
	}
    session.putValue("requestedPage", requestedPage);
    session.putValue("SiteScheme", scheme);
    session.putValue("SiteHost", host);
%>


<%---------------------------------------------------------------------------------------------------
  -- INITIALIZATION
  -------------------------------------------------------------------------------------------------%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%
	SessionManager.initializeFromRequest(request);
	String JSPRootURL = SessionManager.getJSPRootURL();
%> 

<%------------------------------------------------------------------------------------------------------------------------
  -- PROPERTIES LOADING
  -- Load the properties from the request.
  -----------------------------------------------------------------------------------------------------------------------%>
<%
	//PropertyProvider.setContextFromRequest (request);
    PropertyProvider.setContextFromRequest (request, application);
	if (!PropertyProvider.isLoaded()) {
		// Can't load properties, we have to abort initialization
		throw new PnetException(PropertyProvider.get("prm.project.main.initializing.error.application.message"));
	} 
%>

<%---------------------------------------------------------------------------------------------------
  -- FORWARD                                                                                         
  -- Forwards to request page so that request parameters are preserved, even during HTTP POST        
  -- NOTE: This _MUST_ FORWARD - HTTP redirection will _NOT_ work in certain cases
  -- 1) HTTP redirection causes infinite loops to occur if client does not have cookies
  -- Of course it is a requirement that all clients support cookies.  However the ULF scheduler
  -- which acts as a client when executing the NotificationScheduler and PostmanAgent does not preserve
  -- cookies. Those JSP pages require initialization too.
  -- 2) If a page is navigated to via a POST and initialization is required, a forward
  -- must be performed in order to preserve all the parameters.
  -------------------------------------------------------------------------------------------------%>
<%
	requestedPage = (String) session.getValue("requestedPage");
	session.removeValue("requestedPage");
	if (requestedPage != null) {
		pageContext.forward(net.project.util.HttpUtils.getServletNameFromRequestURI(requestedPage));
	} else {
		throw new PnetException(PropertyProvider.get("prm.project.main.initializing.error.application.message2"));
	}
%>
