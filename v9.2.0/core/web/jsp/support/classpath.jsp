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
    info="Show the classpath"
    language="java"
    import="java.util.StringTokenizer,
            java.io.File"
%>
<%
    String classPathString = System.getProperty("java.class.path");
    StringTokenizer tok = new StringTokenizer(classPathString, ";");
%>
<html>
<head>
    <title>Classpath</title>
</head>
<body>
    <table border="1">
        <tr bgcolor="#CCCCCC">
            <td><b>Classpath Element</b></td>
            <td><b>Element Exists?</b></td>
        </tr>
<%     
    while (tok.hasMoreTokens()) {
        String fileOrDirectory = tok.nextToken();
        out.print("<tr><td>" + fileOrDirectory + "</td>");
        if ((new File(fileOrDirectory)).exists())
            out.println("<td>Yes</td></tr>");
        else
            out.println("<td><font color=\"red\"><b>No</b></font>");
    }
%>
    </table>
</body>
</html>        