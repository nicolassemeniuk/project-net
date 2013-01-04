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
    info="Help Page" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
			net.project.space.Space,
			java.util.Enumeration,
			net.project.base.property.PropertyProvider,
            net.project.base.compatibility.Compatibility,
            net.project.security.SessionManager,
            net.project.util.HTMLUtils"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<%-- Redirect to external help system, if enabled --%>
<%
	if (PropertyProvider.getBoolean("prm.global.help.external.isenabled")) {
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
		String myPage = request.getParameter("page");
		String mySection = request.getParameter("section");		
		
        if (myPage == null) {
            // If no page is specified, we need to load the TOC
            myPage = "help_TOC";
        } else {
        	myPage = HTMLUtils.escape(myPage).replaceAll("'", "&acute;");
        }

		StringBuffer location = new StringBuffer();
		if (mySection != null ) {
			mySection = HTMLUtils.escape(mySection).replaceAll("'", "&acute;");
			location.append(PropertyProvider.get("prm.global.help.pagesection.external.href", new String[]{myPage, mySection}));
		} else {
			location.append(PropertyProvider.get("prm.global.help.page.external.href", myPage));
		}
		
		response.sendRedirect(location.toString());
		
		return;
		
	} else {
%>

<html>
<head>
<title>Project.net Help</title>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<link href="<%=SessionManager.getJSPRootURL()%>/styles/global.css" rel="stylesheet" rev="stylesheet" type="text/css">
<link href="<%=SessionManager.getJSPRootURL()%>/styles/fonts.css" rel="stylesheet" rev="stylesheet" type="text/css">
<link href="<%=SessionManager.getJSPRootURL()%>/styles/help.css" rel="stylesheet" rev="stylesheet" type="text/css">

<script language="javascript" src="<%=SessionManager.getJSPRootURL()%>/src/util.js"></script>
<script language="javascript" src="<%=SessionManager.getJSPRootURL()%>/src/standard_prototypes.js"></script>
<script language="javascript" src="<%=SessionManager.getJSPRootURL()%>/src/cookie.js"></script>
</head>

<body class="main" leftmargin=0 topmargin=0 marginwidth=0 marginheight=0>
<table width="100%" cellpadding=1 cellspacing=0 border=0>

 	<tr>
 		<td>&nbsp;</td>
 		<td align="right" class="regBanner">Help&nbsp;</td>
 	</tr>
	<tr>
	<td colspan="2">
	<table width="100%" cellpadding=0 cellspacing=0 border=0>
		<tr>
			<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/spacers/trans.gif" width=10 height=1 alt="" border="0"></td>
			<td width="2%"><a href="Help.jsp"><img src="<%=SessionManager.getJSPRootURL()%>/images/help/help-banner_button.gif" width=130 height=44 alt="" border="0"></a></td>
			<td width="97%" class="navBg">&nbsp;</td>
		</tr>
	</table>
	</td></tr>
	</table> 
<a name="top"></a>

<%
    String helpFile = null;
    String mySection = request.getParameter("section");
    String myPage = request.getParameter("page");
    String className = null;
    String helpname = null;
    boolean found_file = false;

    // first look for the individual section
    if(mySection != null && myPage != null) {
	
    	// check for the file in the include directory for outside the login
    	helpname = myPage + "_"+ mySection;
		helpFile = "include/"+ helpname +".jsp";
		
		if ( application.getResource("/help/include/"+ helpname +".jsp") != null ) {
			found_file = true;
		} else {
			// check for the file in the include directory for outside the login
			helpFile = "include_outside/"+ helpname +".jsp";
			if ( application.getResource("/help/include_outside/"+ helpname +".jsp") == null ) {
				found_file = false;
			} else {
				found_file = true;
			}
		}
    }
    // if  you did not find the file or the file did not contain a section, look for the main file
    if(myPage != null && !found_file) {
    	// check for the file in the include directory for outside the login
		helpFile = "include/" + myPage +".jsp";
	
		if ( application.getResource("/help/include/"+ myPage + ".jsp") == null ) {
			// check for the file in the include directory for outside the login
			helpFile = "include_outside/"+ myPage + ".jsp";
			if ( application.getResource("/help/include_outside/"+ myPage +".jsp") == null ) {
				// if that does not exist, set it to the TOC
				helpFile = "include/help_TOC.jsp";
			}
		}
     }
	 
	// if no parameters are passed in, set to the TOC
	
	if(myPage == null && mySection == null)
		helpFile = "include/help_TOC.jsp";
	
        HttpSession mySess = request.getSession() ;	
        mySess.setAttribute("insideHelp","yes");
%>
    

	<blockquote>

	<jsp:include page='<%= helpFile %>' flush="true"/>
		<br>
    	<jsp:include page="include_outside/return_toc_link.jsp" flush="true"/>
    	<br>
      </blockquote>
	  
<%@ include file="/help/include_outside/footer.jsp" %>

  </body>
</html>

<%
	}
%>
<%-- End of external help redirect --%>
