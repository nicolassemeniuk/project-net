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

<%@page import="net.project.security.SessionManager"%>
<table width="80%" border="0" cellpadding="0" cellspacing="0">
<tr><td align="left">
<%
        HttpSession mySess=request.getSession() ;	
        String attr=(String)mySess.getAttribute("insideHelp");
        
 if(attr!=null && attr.equalsIgnoreCase("yes"))
	out.print("<a href=\"Help.jsp\" class=\"pageTitle\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/actionbar-back_off.gif\" width=\"27\" height=\"27\" alt=\"\" border=\"0\" align=\"absmiddle\"/>Return to Table Of Contents</a>");
else
	out.print("<a href=\"HelpDesk.jsp\" class=\"pageTitle\"><img src=\""+ SessionManager.getJSPRootURL() +"/images/icons/actionbar-back_off.gif\" width=\"27\" height=\"27\" alt=\"\" border=\"0\" align=\"absmiddle\"/>Return to Table Of Contents</a>");
%>

</td>
<td align="right">
<a href="#top" class="pageTitle"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/actionbar-jump_off.gif" width="27" height="27" alt="" border="0" align="absmiddle" />Top</a>
</td>
</tr>
</table>
