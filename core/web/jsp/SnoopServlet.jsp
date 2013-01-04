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

<html>
<head>
<title>Project.net HTTP Request Status</title>
</head>

<%@ page
    contentType="text/html; charset=UTF-8"
    info="Login page"
    language="java"
    errorPage="/errors.jsp"
    import="java.util.*"
 %>

<body bgcolor=#FFFFFF>
<font face="Helvetica">

<h2>
<font color=#DB1260>
Snoop Servlet
</font>
</h2>

<p>
This servlet returns information about the HTTP request from your browser.
<h3>
Requested URL
</h3>

<pre>
<%= HttpUtils.getRequestURL(request) %>
</pre>

<h3>
Init parameters
</h3>

<pre>
<%
Enumeration e = getServletConfig().getInitParameterNames();
while (e.hasMoreElements()) {
  String name = (String)e.nextElement();
  out.println(name + ": " + getServletConfig().getInitParameter(name));
}
%>
</pre>

<h3>
Request information
</h3>

<pre>
Request Method: <%= request.getMethod() %>
Request URI: <%= request.getRequestURI() %>
Request Protocol: <%= request.getProtocol() %>
Servlet Path: <%= request.getServletPath() %>
Path Info: <%= request.getPathInfo() %>
Path Translated: <%= request.getPathTranslated() %>
Query String: <%= request.getQueryString() %>
Content Length: <%= request.getContentLength() %>
Content Type: <%= request.getContentType() %>
Server Name: <%= request.getServerName() %>
Server Port: <%= request.getServerPort() %>
Remote User: <%= request.getRemoteUser() %>
Remote Address: <%= request.getRemoteAddr() %>
Remote Host: <%= request.getRemoteHost() %>
Authorization Scheme: <%= request.getAuthType() %>
</pre>

<h3>
Request headers
</h3>

<pre>
<%
e = request.getHeaderNames();
while (e.hasMoreElements()) {
  String name = (String)e.nextElement();
  out.println(name + ": " + request.getHeader(name));
}
%>
</pre>

</body>
</html>

