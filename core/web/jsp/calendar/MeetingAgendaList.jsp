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
|   $Revision: 19262 $
|       $Date: 2009-05-21 13:34:09 -0300 (jue, 21 may 2009) $
|
|   Displays an editable Meeting Agenda List (add, remove, modify)   
|   
|   Author: Adam Klatzkin    
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Meeting Agenda Listing" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.calendar.MeetingBean, 
		    net.project.security.SecurityProvider,
		    net.project.security.User,
		    net.project.security.SessionManager,
		    net.project.space.Space" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>                                                                                                     
<%	net.project.security.ServletSecurityProvider.setAndCheckValues(request);	%>
<security:verifyAccess action="view"
					   module="<%=net.project.base.Module.CALENDAR%>" 
					   objectID="<%=meeting.getID()%>" /> 
<%
String id = meeting.getID();
String myPath =  SessionManager.getJSPRootURL()+ "/calendar/MeetingAgendaList.jsp?module="+net.project.base.Module.CALENDAR
                + "&action="+net.project.security.Action.VIEW
                + "&id="+id;
java.util.Hashtable nav = (java.util.Hashtable)request.getSession().getValue("PageNavigator");
String myReturnTo;
nav.put("MeetingItem_returnto", myPath);
boolean isWizard = (nav.get("wizard") != null) ? true : false;
if (isWizard)
    myReturnTo = (String)nav.get("AgendaList_returnto");
else
    myReturnTo = (String)nav.get("MeetingList_returnto");

// Does the user have permission to edit this meeting?
boolean editable = securityProvider.isActionAllowed(id,
                                                    Integer.toString(net.project.base.Module.CALENDAR),
                                                    net.project.security.Action.MODIFY);
// if the user does have edit permission, cache it in the navigation object
if (editable)
    nav.put("editable", id);
else
    nav.remove("editable");

// Decide what toolbar buttons to show
String showBackButton = "false";
String showFinishButton = "false";
String showSubmitButton = "false";
if (myReturnTo != null)
	showBackButton="true";
if (isWizard) {
	showFinishButton = "true";
} else {
	showSubmitButton = "true";
}

	String refLink= (String)nav.get("Meeting_cancel_to");	
	
	String showCancelButton=(refLink != null ? "true" : "false");
	
%>
<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/standard_prototypes.js" />
<script language="javascript">
	var theForm;
	var isLoaded = false;
	
	// Variable declarations  for blog-it 
	var blogItFor ='meetingFor'+'<%=user.getCurrentSpace().getSpaceType().getName()%>';
	var spaceId = <%= user.getCurrentSpace().getID()%>;
	var moduleId = <%= net.project.base.Module.CALENDAR%>;
	var objectTypeNameFor = 'Meeting';
	var objectId = <%=(id != null ? id : 0)%>;
    var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";
    
	window.history.forward(-1);
	
	function setup() {
    	load_menu('<%=user.getCurrentSpace().getID()%>');
     	isLoaded = true;
     	theForm = self.document.forms[0];
    }


    function back() {
        var theLocation= "<%= myReturnTo %>&id=<%= id %>";
        self.location = theLocation;
    }
    
	
    function cancel() {
    	self.location = "<%= refLink %>";
    }

function reset() 
    {
    theForm.reset();
    }

function modify() {
    if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
        var num = getSelection(theForm);
	    self.location = "<%= SessionManager.getJSPRootURL() %>/calendar/MeetingAgendaItem.jsp?agendaid=" + num
                    + "&module=<%= net.project.base.Module.CALENDAR %>"
                    + "&action=<%= net.project.security.Action.VIEW %>"
                    + "&id=<%= id %>";
    } else {
    	extAlert("Error","<display:get name='prm.global.javascript.verifyselection.noselection.error.message' />",Ext.MessageBox.ERROR);
    }	  
}

function create() 
    {
	var theLocation="<%= SessionManager.getJSPRootURL() %>/calendar/MeetingAgendaItem.jsp"
                    + "?module=<%= net.project.base.Module.CALENDAR %>"
                    + "&action=<%= net.project.security.Action.MODIFY %>"
                    + "&id=<%= id %>";
                    
	self.location = theLocation;	
    }

function remove() {
   if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
      Ext.MessageBox.confirm('<display:get name="prm.global.extconfirm.title"/>', '<display:get name="prm.calendar.meetingagendalist.deleteagenda.confirm"/>', function(btn) { 
		if(btn == 'yes'){ 
			theAction("remove");
			theForm.submit();
		}else{
			return false;
		}
	});
	} else {
    	extAlert("Error","<display:get name='prm.global.javascript.verifyselection.noselection.error.message' />",Ext.MessageBox.ERROR);
    }
}

function submit () 
    {
	theAction("submit");
	theForm.submit();
    }
    
function finish() 
    {
	theAction("submit");
    theForm.wizard.value = "next";
	theForm.submit();
    }    
function help()
{
	var helplocation="<%= SessionManager.getJSPRootURL() %>/help/Help.jsp?page=calendar&section=agenda_view";
	openwin_help(helplocation);
}

</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.calendar.mainpage.title">
	<tb:setAttribute name="leftTitle">
		<history:history>
			<history:page display="<%=meeting.getName()%>" 
					jspPage='<%=SessionManager.getJSPRootURL() + "/calendar/MeetingManager.jsp"%>'
					queryString='<%="module="+net.project.base.Module.CALENDAR+"&id="+meeting.getID()%>' />
		</history:history>
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="create" />
		<tb:button type="modify" />
		<tb:button type="remove" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form method="post" action="MeetingAgendaListProcessing.jsp">
    <input type="hidden" name="theAction">
    <input type="hidden" name="module" value="<%= net.project.base.Module.CALENDAR %>" >
    <input type="hidden" name="action" value="<%= net.project.security.Action.MODIFY %>" >
    <input type="hidden" name="id" value="<%= id %>">
    <input type="hidden" name="wizard" value="" >    
	
<table border="0" cellpadding="0" cellspacing="0" width="100%">

<tr class="channelHeader">
	<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader" colspan="4"><nobr><%=PropertyProvider.get("prm.calendar.meeting.agendalist.channel.agenda.title")%></nobr></td>
	<td align=right width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr>
	<td colspan="6">
	<jsp:include page="include/includeMeetingHeader.jsp" flush="true" />
	</td>
</tr>
<tr>
    <td colspan="6">&nbsp;</td>
</tr>
<tr class="channelHeader">
	<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader" colspan="4"><nobr><%=PropertyProvider.get("prm.calendar.meeting.agendalist.channel.items.list")%></nobr></td>
	<td align=right width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>

<tr>
	<td colspan="6">                          
	<%
	if (editable) 
	    request.setAttribute("editable", "1");
	request.setAttribute("AgendaSelectEnabled", new Boolean(true)); 
	%>            
	<jsp:include page="include/includeAgendaList.jsp" flush="true" />            
	</td>
</tr>
  <tr class="actionBar">
	<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-left_end.gif" width=8 height=27 alt="" border=0></td>
	<td class="actionBar" colspan="3">&nbsp;</td>
	<td class="actionBar" align=right><nobr><a href="javascript:create();" class="channelNoUnderline"><%=PropertyProvider.get("prm.calendar.meeting.agendalist.add.button.label")%>&nbsp;<img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-add_off.gif" width=27 height=27 alt="" border=0 align="absmiddle"></a></nobr></td>
	<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/actionbar-right_end.gif" width=8 height=27 alt="" border=0></td>
  </tr>
     <tr>
        	<td colspan="6">&nbsp;</td>  
     </tr>
<% if (isWizard) { %>
<tr>
    <td colspan="6">&nbsp;</td>
</tr>
<% } %>    
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="cancel" show="<%=showCancelButton%>" />
		<tb:button type="back" show="<%=showBackButton%>" />
		<tb:button type="submit" show="<%=showSubmitButton%>" />
		<tb:button type="finish" show="<%=showFinishButton%>" />
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
