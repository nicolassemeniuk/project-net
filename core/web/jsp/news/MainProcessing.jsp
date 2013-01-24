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
    info="NewsProcessing. Omits no output." 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.space.Space,
			net.project.security.User,
			net.project.base.PnetException,
			net.project.news.NewsBean,
			net.project.news.NewsManagerBean" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="newsManager" class="net.project.news.NewsManagerBean" scope="session" />
<jsp:useBean id="news" class="net.project.news.NewsBean" scope="session" />

<% 
	newsManager.setSpace(user.getCurrentSpace());
	newsManager.setUser(user);

	String verifyAction = null;
	String theAction = request.getParameter("theAction");
	String newsID = request.getParameter("id");

	if (theAction.equals("remove")) {
		// REMOVE NEWS ITEM
		
		news.clear();
		news.setID(newsID);
		news.load();
		news.setUser(user);
%>
		<security:verifyAccess objectID="<%=news.getID()%>"
							   action="delete"
							   module="<%=net.project.base.Module.NEWS%>" />
<%
		news.clearErrors();
		news.prepareRemove();
		if (news.isRemovePermitted()) {
			news.remove();

			if (news.isRemoveSuccessful()) {
				
			// ---Avinash Bhamare:-----setting and checking security violation----------
				request.setAttribute("action",""+net.project.security.Action.VIEW); 
				net.project.security.ServletSecurityProvider.setAndCheckValues(request);
			// -------------------------------------------------------------------------
			
				pageContext.forward("Main.jsp?action=" + net.project.security.Action.VIEW);
			} else {
		        pageContext.forward("NewsRemoveResult.jsp?mode=removeError");
			}

		} else {
			pageContext.forward("NewsRemoveResult.jsp?mode=prepareError");
		}

	} else {
		// UNKNOWN ACTION
		throw new PnetException("Unhandled action " + theAction + " in news/MainProcessing.jsp");
	}
%>

