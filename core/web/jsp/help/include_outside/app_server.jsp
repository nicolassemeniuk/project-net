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

<%--
    Prints Page Timing Statistics in a JSP page as an html comment.
    Assumes a request-scoped PageTimingBean was instantiated.
    This is typically accomplished by including taglibInclude.jsp
--%>
<%
    // Grab the bean from the request
    net.project.util.PageTimingBean statBean = (net.project.util.PageTimingBean) request.getAttribute("pageTimingBean");
%>
<!--
		PAGE TIMING STATISTICS : <%=statBean%>
        Application Server Host:  <%=request.getServerName()%>
        Application Server Port:  <%=request.getAttribute("weblogic.servlet.network_channel.port")%>
-->