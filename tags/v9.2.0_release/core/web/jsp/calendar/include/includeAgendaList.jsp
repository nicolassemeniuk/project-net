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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|
|   use this to include a listing of agenda items for a meeting
|   set the request attribute
|       AgendaSelectEnabled="true"
|   to obtain a list that includes selectable radio controls
|   
|   
|   Author: Adam Klatzkin    
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
       info="Agenda list"
       language="java" 
       errorPage="/errors.jsp"
       import="net.project.base.property.PropertyProvider,
       		   net.project.calendar.AgendaBean,
       		   net.project.security.SessionManager"  
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session" />

<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.CALENDAR%>" 
					   objectID="<%=meeting.getID()%>" /> 

<%
    boolean enableSelect = false;
    if (request.getAttribute("AgendaSelectEnabled") != null)
        enableSelect = true;
    
    boolean editable = false;
    if (request.getAttribute("editable") != null) 
        editable = true;
%>

<input type="hidden" name="IncludeJSP" value="MeetingAgendaListProcessing.jsp">
<table cellpadding="0" cellspacing="0" border="0" width="100%">
    <tbody>
        <tr>
<%
    if (enableSelect){
%>			<td>
            	<input type="hidden" name="AgendaSelectEnabled" value="true">                    
            </td>
<%
     }
%>
            <td class="tableHeader"><%=PropertyProvider.get("prm.calendar.meeting.agendalist.item.column")%></td>
            <td class="tableHeader"><%=PropertyProvider.get("prm.calendar.meeting.agendalist.time.column")%></td>                                
            <td class="tableHeader"><%=PropertyProvider.get("prm.calendar.meeting.agendalist.owner.column")%></td>
            <td class="tableHeader"><%=PropertyProvider.get("prm.calendar.meeting.agendalist.status.column")%></td>
        </tr>
        
		<tr class="tableLine">
			<td  colspan="5" class="tableLine"><img src="<%= SessionManager.getJSPRootURL() %>/images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
		</tr>
<%  
	java.util.ArrayList items = meeting.getAgendaItems();
    if (items != null){
        net.project.code.TableCodeDomain domain = new net.project.code.TableCodeDomain();
        domain.setTableName("pn_agenda_item");
        domain.setColumnName("status_id");
        domain.load();
        int maxItems = items.size();
        for (int i=0;i<maxItems;i++){
            AgendaBean theItem = (AgendaBean)items.get(i);
%>                  
		<tr align="left" valign="top">
<%
			if (enableSelect){
%>
            <td align="center" class="tableContent">
                <input type="radio" name="selected" value="<%= theItem.getId() %>"/>
            </td>
<%
			}
%>                    
            <td align="left" class="tableContent"><%= net.project.util.HTMLUtils.escape(theItem.getItemSequence()) %>. <A href="../calendar/MeetingAgendaItem.jsp?agendaid=<%= theItem.getId() %>&module=<%= net.project.base.Module.CALENDAR %>&action=<%= net.project.security.Action.VIEW %>&id=<%= meeting.getID() %>"><%= net.project.util.HTMLUtils.escape(theItem.getName()) %></A></td>
            <td class="tableContent"><%= net.project.util.HTMLUtils.escape(theItem.getAllotedTime()) %></td>
            <td align="left" class="tableContent"><%= net.project.util.HTMLUtils.escape(theItem.getOwner()) %></td>
            <td colspan="2" class="tableContent">     
<% 
			if (editable){
%>
                <select name="agenda_status_<%= theItem.getId() %>">
                	<%= domain.getOptionList(theItem.getStatusId()) %>
                </select>

<%
    		} else{
%>
            	<%= theItem.getStatus() %>
<%
    		}
%>                               
            </td>
        </tr>
<%
		}
	}
%>                        
    </tbody>
</table>
                                        
