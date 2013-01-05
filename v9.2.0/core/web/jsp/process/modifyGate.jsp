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
    info="Gate Modify Page"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.process.PhaseBean, 
            net.project.process.GateBean, 
            net.project.security.User,
            net.project.security.SecurityProvider,
            net.project.security.SessionManager,
            net.project.xml.XMLFormatter"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="gate" class="net.project.process.GateBean" scope="session"/>
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="session"/>

<%
	PhaseBean m_phase = new PhaseBean();

	String titleText = "";
	String nameDefault = "";
	String descDefault = "";
	String dateDefault = "";
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
        titleText = PropertyProvider.get("prm.project.process.modifygate.channel.create.title");
        //Clear out the gate to make sure we don't overwrite the one in session.
        //gate.clear();
        if (errorReporter != null && errorReporter.errorsFound()) {
            //Reset some of the defaults to the values that the user submitted.
            nameDefault = gate.getName();
            descDefault = gate.getDesc();
            dateDefault = request.getParameter("gatedate");
        }
	} else {
        %>
		<security:verifyAccess action="modify"
					   module="<%=net.project.base.Module.PROCESS%>"
					   objectID="<%=id%>" /> 
        <%
        gate.setID(id);
        gate.load();
        m_phase.setID(gate.getPhaseID());
        m_phase.load();
        nameDefault = gate.getName();
        descDefault = gate.getDesc();
        dateDefault = gate.getDateString();
        titleText = PropertyProvider.get("prm.project.process.modifygate.channel.modify.title");
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
<META http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/checkLength.js" />
<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/checkDate.js" />
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

function cancel() {
   var theLocation = JSPRootURL + "/process/ViewPhase.jsp?module=<%=net.project.base.Module.PROCESS%>&action=<%=net.project.security.Action.VIEW%>";
   self.document.location = theLocation;
}

function reset() {
	<% if (action ==  net.project.security.Action.CREATE){ %>
		var theLocation = JSPRootURL + "/process/modifyGate.jsp?module=<%=net.project.base.Module.PROCESS%>&action=<%=net.project.security.Action.CREATE%>&id=<%=m_phase.getID()%>";
	<% } else { %>
		var theLocation = JSPRootURL + "/process/modifyGate.jsp?module=<%=net.project.base.Module.PROCESS%>&action=<%=net.project.security.Action.MODIFY%>&id=<%=m_phase.getGate().getID()%>";
	<% } %>
	self.document.location = theLocation;
	theForm.reset();
}
   
function security() {
    var m_url = JSPRootURL + "/process/secureCheck.jsp?module=<%=net.project.base.Module.PROCESS%>&action=<%=net.project.security.Action.MODIFY_PERMISSIONS%>&id=<%=id%>";
    var link_win = openwin_security("security");
    link_win.document.location = m_url;
    link_win.focus();
}

function submit () {
    if(validate()) {
       theAction("submit");
       theForm.submit();
    }
}
function validate() {
    if(!checkTextbox(theForm.gatename, '<%=PropertyProvider.get("prm.project.process.modifygate.namerequired.message")%>'))return false;
    if (!checkMaxLength(theForm.gatedesc,1000,'<%=PropertyProvider.get("prm.project.process.modifygate.descriptionsize.message")%>'))return false;
    if(!checkDropdown_NoSelect(theForm.gatestatus, '<%=PropertyProvider.get("prm.project.process.modifygate.statusrequired.message")%>'))return false;
    if(!verifyNonBlankField_withAlert(theForm.gatedate, '<%=PropertyProvider.get("prm.project.process.modifygate.optionalstatusrequired.message")%>'))return false;
    return true;
}
 
function help() {
    var helplocation=JSPRootURL+"/help/Help.jsp?page=process_main&section=gate_modify";
    openwin_help(helplocation);
}
<% if (action ==  net.project.security.Action.MODIFY){ %>
function remove () {
	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.process.deletegate.comfirmmsg")%>', function(btn) { 
		if(btn == 'yes') {
			theForm.action.value = "<%=net.project.security.Action.DELETE%>";
			theAction("submit");
			theForm.submit();
		}	
	});
}
<% } %>

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
		<tb:button type="remove" enable="<%=enableRemoveButton%>" label='<%=PropertyProvider.get("prm.project.process.deletegate.label")%>'/>
		<tb:button type="security" enable="<%=enableSecurityButton%>" /> 
	<%} %>	
	</tb:band>
</tb:toolbar>
<div id='content'>
<form name="mainForm" method="post" action="modifyGateProcessing.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="id" value="<%=id%>">
<input type="hidden" name="action" value="<%=action%>">
<input type="hidden" name="module" value="<%=module%>">
<input type="hidden" name="phase_id" value="<%=m_phase.getID()%>">
<table border="0" align="left" cellpadding="0" cellspacing="0" width="600">
<tr align="left" class="channelHeader">
    <td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
    <td nowrap colspan="6" class="channelHeader">&nbsp;<%=titleText%>
    <td width="1%" align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
</tr>
<tr align="left" valign="top">
        <td>&nbsp;</td>
        <td colspan="4" class="warnText">
        <% 
            if (errorReporter.errorsFound()) {
                XMLFormatter formatter = new XMLFormatter();
                formatter.setXML(errorReporter.getXML());
                formatter.setStylesheet("/base/xsl/error-report.xsl");
                out.println(formatter.getPresentation());
                //Clear out the error reporter now that we are done with it
                errorReporter.clear();
            }
        %>
    </td>
    <td>&nbsp;</td>
</tr>
<tr align="left" valign="top">
    <td>&nbsp;</td>
    <td nowrap  class="fieldRequired"><%=PropertyProvider.get("prm.project.process.modifygate.name.label")%>&nbsp;
    </td>
    <td colspan="3">
    <input type="text" name="gatename" size="40" maxlength="80" value="<%=nameDefault%>">
    </td>
    <td>&nbsp;</td>
</tr>
<tr align="left" valign="top">
<td>&nbsp;</td>
    <td nowrap  class="fieldNonRequired"><%=PropertyProvider.get("prm.project.process.modifygate.description.label")%>&nbsp;
    </td>
    <td nowrap colspan="3">
    <textarea wrap="VIRTUAL" cols="80" rows="3" name="gatedesc" ><%=net.project.util.HTMLUtils.escape(descDefault) %></TEXTAREA>
    </td>
    <td>&nbsp;</td>
</tr>
<tr align="left" valign="middle">
<td>&nbsp;</td>
    <td nowrap class="fieldRequired"><%=PropertyProvider.get("prm.project.process.modifygate.status.label")%>&nbsp;
    </td>
    <td nowrap>
            <select name="gatestatus"><%=gate.getStatusOptionList()%>
            </select>
    </td>
    <td nowrap width="115">&nbsp;
    </td>
    <td nowrap>&nbsp;
    </td>
    <td>&nbsp;</td>
</tr>
<tr align="left" valign="middle">
<td>&nbsp;</td>
    <td nowrap  class="fieldRequired"><%=PropertyProvider.get("prm.project.process.modifygate.reviewdate.label")%>&nbsp;
    </td>
    <td nowrap>
            <input type="text" name="gatedate" size="10" maxlength="10" value="<%=dateDefault%>">
            <util:insertCalendarPopup fieldName="gatedate" />
    </td>
    <td nowrap width="115">&nbsp;
    </td>
    <td nowrap>&nbsp;
    </td>
    <td>&nbsp;</td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
	    <tb:button type="submit" />
	</tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
