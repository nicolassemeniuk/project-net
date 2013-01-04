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
|   $Revision: 20065 $
|       $Date: 2009-10-06 13:28:30 -0300 (mar, 06 oct 2009) $
|     $Author: ritesh $
|
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="NewsEditProcessing.jsp  Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.space.Space,
			net.project.security.User,
			net.project.base.PnetException,
			net.project.news.NewsBean,
			net.project.security.SessionManager,
			net.project.base.Module,
			net.project.security.Action" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="news" class="net.project.news.NewsBean" scope="session" />

<%
    news.setUser(user);
    news.setSpace(user.getCurrentSpace());

	String theAction = request.getParameter("theAction");
    if (theAction.equals("submit")) {
		// Submitting modified News Item
		
%>
		<%-- Set all properteies --%>
		<jsp:setProperty name="news" property="*" />
<%
		if ((request.getParameter("message") == null) || (request.getParameter("message").equals(""))) {
		    news.setMessage("");
		}
			news.clearErrors();
			news.validate();
			if (news.hasErrors()) {
				pageContext.forward("NewsEdit.jsp?action=4&module=110&mode=create");
			} else {
		        news.store();
			}
		//---Avinash:-----setting and checking security for edit and and view-------
			request.setAttribute("action",""+net.project.security.Action.VIEW);
			net.project.security.ServletSecurityProvider.setAndCheckValues(request);
		//--------------------------------------------------------------------------
			response.sendRedirect(SessionManager.getJSPRootURL()+ "/news/Main.jsp?module="+Module.NEWS+"&action="+Action.VIEW);
			
    } else {
		throw new PnetException("Unknown action passed to NewsEditProcessing: " + theAction);
	}
%>
<%-- End of action handling --%>
