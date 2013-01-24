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
    info="Kills off the session"
    language="java"
%>
<%
{
    // Kill all the old session objects.
    String[] values = session.getValueNames();
    if (values != null)
         for (int i=0;i<values.length;i++)
             session.removeValue(values[i]);
			 
    session.invalidate();

    // SUCCESS
    out.clear (); // Clear the page so this will be the first line
    out.print ("<?xml version = \"1.0\" ?><response>" + "true" + "</response>");
}


%>



