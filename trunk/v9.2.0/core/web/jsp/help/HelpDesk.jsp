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
    info="Help Desk Page" 
    language="java" 
    errorPage="/errors.jsp"
    import="java.util.Enumeration,
			net.project.base.property.PropertyProvider,
            org.apache.log4j.Logger,
            net.project.security.SessionManager,
            net.project.util.HTMLUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<%-- Redirect to external help desk system, if enabled --%>
<%
	if (PropertyProvider.getBoolean("prm.global.help.helpdesk.external.isenabled")) {
		StringBuffer query = new StringBuffer();
		String name = null;
		for (Enumeration parameterNames = request.getParameterNames(); parameterNames.hasMoreElements() ;) {
			name = (String) parameterNames.nextElement();
			if (query.length() > 0) {
				query.append("&");
			}
			query.append(name + "=" + request.getParameter(name));
		}
		request.setCharacterEncoding("UTF-8");
		String mySection = request.getParameter("section");
    	String myPage = request.getParameter("page");

        if (myPage == null) {
            // If no page is specified, we need to load the TOC
            myPage = "Login_Logout";
        } else {
        	myPage = HTMLUtils.escape(myPage).replaceAll("'", "&acute;");
        }

		StringBuffer location = new StringBuffer();
		if (mySection != null ) {
			mySection = HTMLUtils.escape(mySection).replaceAll("'", "&acute;");
			location.append(PropertyProvider.get("prm.global.help.helpdesk.pagesection.external.href", new String[]{myPage, mySection}));
		} else {
			location.append(PropertyProvider.get("prm.global.help.helpdesk.page.external.href", myPage));
        }
		
		response.sendRedirect(location.toString());
		
		return;
	} else {
%>

<html>
<head>
<title>Project.net Help</title>
<META HTTP-EQUIV="expires" CONTENT="0">
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">

<link href="<%=SessionManager.getJSPRootURL()%>/styles/help.css" rel="stylesheet" rev="stylesheet" type="text/css">
<template:getSpaceCSS />

<script language="javascript" src="<%=SessionManager.getJSPRootURL()%>/src/util.js"></script>
<script language="javascript" src="<%=SessionManager.getJSPRootURL()%>/src/standard_prototypes.js"></script>
<script language="javascript" src="<%=SessionManager.getJSPRootURL()%>/src/cookie.js"></script>

</head>
<%
        HttpSession mySess=request.getSession() ;	
        String attr=(String)mySess.getAttribute("insideHelp");
        String state=(String)request.getParameter("state");
        String helplink="HelpDesk.jsp";
        
 if(attr!=null && attr.equalsIgnoreCase("yes"))
 	helplink="Help.jsp";
 %>

<body class="main" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0 id="bodyWithFixedAreasSupport">
 
<div id='topframe'> 
<table width=100% cellpadding=1 cellspacing=0 border=0>
 	<tr>
 		<td>&nbsp;<display:img src="@prm.global.registration.header.logo" alt="" border="0" /></td>
 		<td align="right" class="regBanner">HELP&nbsp;</td>
 	</tr>
</table>
</div>

<div id='content'>

<a name='top'></a>

<a href="<%=helplink%>"><img src="<%=SessionManager.getJSPRootURL()%>/images/help/help-banner_button.gif" width=130 height=44 alt="" border="0"></a>
 
<%
    String helpFile=null;
    String mySection=request.getParameter("section");
    String myPage=request.getParameter("page");

    boolean found_file=false;
    RequestDispatcher dispatcher = null;

    // first look for the individual section
    if(mySection!=null && myPage!=null) {
    	// check for the file in the include directory for outside the login
		helpFile="include_outside/"+myPage+"_"+mySection+".jsp";
        dispatcher = request.getRequestDispatcher(helpFile);
        found_file = (dispatcher != null);
    }

    // if you did not find the file or the file did not contain a section,
    // look for the main file
    if(myPage!=null && !found_file) {
    	// check for the file in the include directory for outside the login
        helpFile="include_outside/"+myPage+".jsp";
        dispatcher = request.getRequestDispatcher(helpFile);
        found_file = (dispatcher != null);
     }

    // if no parameters are passed in, set to the TOC
    if (dispatcher == null) {
	    helpFile="include_outside/help_TOC.jsp";
        dispatcher = request.getRequestDispatcher(helpFile);
    }
%>

<%--
	response.getWriter().write("<a name='top'></a>");
    dispatcher.include(request, response);
    
--%>

<jsp:include page="<%=helpFile %>" flush="true" />

</div>


	<jsp:include page="include_outside/return_toc_link.jsp" flush="true"/>

<%
if (attr == null || !attr.equalsIgnoreCase("yes")) {
	if(state!=null && state.equalsIgnoreCase("popup"))
		out.print("<br>return to <a href=\"javascript:opener.focus();this.close();\">Login</a>");
	else
		out.print("<br>return to <a href=\"../Login.jsp\">Login</a>");
}
%>

<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>

<%
	}
%>
<%-- End of external help redirect --%>
