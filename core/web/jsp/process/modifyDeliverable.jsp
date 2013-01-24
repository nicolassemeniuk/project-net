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
    info="Deliverable Modify Page"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.process.PhaseBean,
            net.project.process.DeliverableBean,
            net.project.security.SecurityProvider,
            net.project.security.SessionManager,net.project.security.User"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="historyBean" class="net.project.history.HistoryBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%
	DeliverableBean m_deliverable = new DeliverableBean();
	PhaseBean m_phase = new PhaseBean();

	String titleText = "";
	String nameDefault = "";
	String descDefault = "";
	String commentsDefault = "";
	String cancelURL = "";

	String id = request.getParameter("id");

	int action = securityProvider.getCheckedActionID();
	int module = securityProvider.getCheckedModuleID();
	if (action == net.project.security.Action.CREATE) {
%>
		<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.PROCESS%>" /> 
<%
        m_phase.setID(id);
        m_phase.load();
        titleText = PropertyProvider.get("prm.project.process.modifydeliverable.channel.create.title");
        cancelURL = SessionManager.getJSPRootURL() + "/process/ViewPhase.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.VIEW;
        historyBean.setPageActive(true);
	} else { %>
		<security:verifyAccess action="modify"
					   module="<%=net.project.base.Module.PROCESS%>"
					   objectID="<%=id%>" /> 
<%
        m_deliverable.setID(id);
        m_deliverable.load();
        m_phase.setID(m_deliverable.getPhaseID());
        m_phase.load();
        nameDefault = m_deliverable.getName();
        descDefault = m_deliverable.getDesc();
        commentsDefault = m_deliverable.getComments();
        titleText = PropertyProvider.get("prm.project.process.modifydeliverable.channel.modify.title");
        cancelURL = SessionManager.getJSPRootURL() + "/process/ViewDeliverable.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.VIEW;
	}
// Decide which toolbar buttons to enable
String enableRemoveButton = "false";
String enableSecurityButton = "false";
if (action ==  net.project.security.Action.MODIFY) {
	enableRemoveButton = "true";
	enableSecurityButton = "true";
}
%>

<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />
<script language="javascript">
        var theForm;
        var errorMsg;
        var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');
	theForm = self.document.mainForm;
    isLoaded = true;
}

function cancel()
   {
   var theLocation = "<%=cancelURL%>";
   self.document.location = theLocation;
   }

function reset() {
	theForm.reset();
}
   
function security()
        {
        var m_url = JSPRootURL + "/process/secureCheck.jsp?module=<%=net.project.base.Module.PROCESS%>&action=<%=net.project.security.Action.MODIFY_PERMISSIONS%>&id=<%=id%>";
        var link_win = openwin_security("security");
        link_win.document.location = m_url;
        link_win.focus();
        }


function submitForm ()
   {
   	if(validateForm(document.mainForm)){
	   theAction("submit");
	   theForm.submit();
	 } 
   }
   
function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=process_main&section=deliverable_modify";
	openwin_help(helplocation);
}
   
 function validateForm(myForm)
 {
 	if(!checkTextbox(myForm.deliverablename, '<%=PropertyProvider.get("prm.project.process.modifydeliverable.namerequired.message")%>'))return false;
    if(!checkMaxLength(myForm.deliverabledesc,1000,'<%=PropertyProvider.get("prm.project.process.modifydeliverable.descriptionsize.message")%>'))return false;
    if(!checkMaxLength(myForm.deliverablecomments,1000,'<%=PropertyProvider.get("prm.project.process.modifydeliverable.commentssize.message")%>'))return false;
 	if(!checkDropdown_NoSelect(myForm.deliverablestatus, '<%=PropertyProvider.get("prm.project.process.modifydeliverable.statusrequired.message")%>'))return false;
	if(!checkDropdown_NoSelect(myForm.deliverableoptional, '<%=PropertyProvider.get("prm.project.process.modifydeliverable.optionalstatusrequired.message")%>'))return false;
 	return true;
 }
<%
if (action ==  net.project.security.Action.MODIFY){
%>

function remove ()
{
   Ext.MessageBox.confirm('Confirm', '<%=PropertyProvider.get("prm.project.process.deletedeliverable.confirmmsg")%>', function(btn) { 
		if(btn == 'yes') {
			theForm.action.value = '<%=net.project.security.Action.DELETE%>';
			theAction("submit");
			theForm.submit();
		}	
	});
}		
	

 <%
 }
 %>
</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />


<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.nav.process">
	<tb:setAttribute name="leftTitle">
		<history:history />
	</tb:setAttribute>
	<tb:band name="standard">
		<%if(enableRemoveButton.equalsIgnoreCase("true") && enableSecurityButton.equalsIgnoreCase("true")){ %>
		 <tb:button type="remove" enable="<%=enableRemoveButton%>" label='<%=PropertyProvider.get("prm.project.process.deletedeliverable.label")%>' />
		<tb:button type="security" enable="<%=enableSecurityButton%>" /> 
		<%} %>
	</tb:band>
</tb:toolbar>

<div id='content'>

<form name="mainForm" method="post" action="<%= SessionManager.getJSPRootURL() %>/process/modifyDeliverableProcessing.jsp" onsubmit="return false;">
<input type="hidden" name="theAction">
<input type="hidden" name="id" value="<%=id%>">
<input type="hidden" name="action" value="<%=action%>">
<input type="hidden" name="module" value="<%=module%>">
<input type="hidden" name="phase_id" value="<%=m_phase.getID()%>">

<table border="0" align="left" cellpadding="0" cellspacing="0" width="600">

	<tr align="left" class="channelHeader">
		<td width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
		<td nowrap colspan="4" class="channelHeader">&nbsp;<%=titleText%>
		</td>
 		<td width=1% align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>

	</tr>
	<tr align="left" valign="top">
	<td>&nbsp;</td>
		<td nowrap  class="fieldRequired"><%=PropertyProvider.get("prm.project.process.modifydeliverable.name.label")%>&nbsp;
		</td>
		<td colspan="3">
		<input type="text" name="deliverablename" size="40" maxlength="80" value="<%=nameDefault%>">
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr align="left" valign="top">
	<td>&nbsp;</td>
		<td nowrap  class="fieldNonRequired"><%=PropertyProvider.get("prm.project.process.modifydeliverable.description.label")%>&nbsp;
		</td>
		<td nowrap colspan="3">
		<textarea wrap="VIRTUAL" cols="80" rows="3" name="deliverabledesc"><%=net.project.util.HTMLUtils.escape(descDefault)%></TEXTAREA>
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr align="left" valign="top">
	<td>&nbsp;</td>
		<td nowrap  class="fieldNonRequired"><%=PropertyProvider.get("prm.project.process.modifydeliverable.comments.label")%>&nbsp;
		</td>
		<td nowrap colspan="3">
		<textarea wrap="VIRTUAL" cols="80" rows="5" name="deliverablecomments"><%=net.project.util.HTMLUtils.escape(commentsDefault)%></TEXTAREA>
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr align="left" valign="middle">
	<td>&nbsp;</td>
		<td nowrap  class="fieldRequired"><%=PropertyProvider.get("prm.project.process.modifydeliverable.status.label")%>
		</td>
		<td nowrap>
			<select name="deliverablestatus"><%=m_deliverable.getStatusOptionList()%>
			</select>
		</td>
		<td nowrap >&nbsp;
		</td>
		<td nowrap>&nbsp;
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr align="left" valign="middle">
	<td>&nbsp;</td>
		<td nowrap  class="fieldRequired"><%=PropertyProvider.get("prm.project.process.modifydeliverable.optional.label")%>&nbsp;
		</td>
		<td nowrap>
		<select name="deliverableoptional"><%=m_deliverable.getOptionalOptionList()%>
		</select>
		</td>
		<td nowrap >&nbsp;
		</td>
		<td nowrap>&nbsp;
		</td>
		<td>&nbsp;</td>
	</tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="submit" function = "javascript: submitForm();"/>
	</tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
