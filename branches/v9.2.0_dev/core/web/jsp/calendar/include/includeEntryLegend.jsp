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
       info="Entry color legend"
       language="java" 
       errorPage="/errors.jsp"
       import="net.project.base.property.PropertyProvider,
	   		   net.project.calendar.*,
	   		   net.project.security.SessionManager,
	   		   net.project.space.SpaceTypes" 
%>
<%!                
public static final String              ICON_meeting    = "red-ball-small.gif";
public static final String              ICON_event      = "green-ball-small.gif";
public static final String              ICON_milestone  = "blue-ball-small.gif";
public static final String              ICON_task       = "yellow-ball-small.gif";

public static final String              COLOR_meeting    = "darkred";
public static final String              COLOR_event      = "darkgreen";
public static final String              COLOR_milestone  = "darkblue";
public static final String              COLOR_task       = "#888800";
%>                                            
<table border=0 cellpadding=0 cellspacing=0 width=190>
<tr>
	<td class="channelHeader" width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td class="channelHeader" width=98% colspan=2 align=center><%=PropertyProvider.get("prm.calendar.main.entrytypelegend.channel.legend.title")%></td>
	<td class="channelHeader" width=1% align="right"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>


    <tr>
        <td align=left width="50%" colspan="2" class="tableContent">
            <IMG SRC="<%= SessionManager.getJSPRootURL() %>/images/<%= ICON_meeting %>">
            <FONT class="legend" COLOR="<%= COLOR_meeting %>">
            <%=PropertyProvider.get("prm.calendar.main.entrytypelegend.meeting.label")%>
            </FONT>
        </td>
        <td align=left width="50%" colspan="2" class="tableContent">
            <IMG SRC="<%= SessionManager.getJSPRootURL() %>/images/<%= ICON_event %>">    
            <FONT class="legend" COLOR="<%= COLOR_event %>">
            <%=PropertyProvider.get("prm.calendar.main.entrytypelegend.event.label")%>
            </FONT>
        </td>
    </tr>
    <%if(!SessionManager.getUser().getCurrentSpace().isTypeOf(SpaceTypes.BUSINESS_SPACE)){ %>
    <tr>
        <td align=left width="50%" colspan="2" class="tableContent">
            <IMG SRC="<%= SessionManager.getJSPRootURL() %>/images/<%= ICON_milestone %>">    
            <FONT class="legend" COLOR="<%= COLOR_milestone %>">
            <%=PropertyProvider.get("prm.calendar.main.entrytypelegend.milestone.label")%>
            </FONT>
        </td>
        <td align=left width="50%" colspan="2" class="tableContent">            
            <IMG SRC="<%= SessionManager.getJSPRootURL() %>/images/<%= ICON_task %>">    
            <FONT class="legend" COLOR="<%= COLOR_task %>">
            <%=PropertyProvider.get("prm.calendar.main.entrytypelegend.task.label")%>
            </FONT>    
        </td>
    </tr>
    <%}%>
</table>
