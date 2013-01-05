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
    info="Phase Modify Page"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.security.SessionManager,
            net.project.xml.XMLFormatter,
            net.project.process.ProgressReportingMethod,
            net.project.util.DateFormat,
            net.project.util.Validator"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="session"/>
<jsp:useBean id="m_phase" class="net.project.process.PhaseBean" scope="session" />
<jsp:useBean id="m_process" class="net.project.process.ProcessBean" scope="page" />

<%
    String titleText = "";
    String nameDefault = "";
    String descDefault = "";
    String startDefault = "";
    String endDefault = "";
    String sequenceDefault = "";
    String percentDefault = "";
    String cancelURL = "";

	String id = request.getParameter("id");

	int action = securityProvider.getCheckedActionID();
	int module = securityProvider.getCheckedModuleID();
	if (action == net.project.security.Action.CREATE) {
        %>
		<security:verifyAccess action="create"
					   module="<%=net.project.base.Module.PROCESS%>" /> 
        <%
        titleText = PropertyProvider.get("prm.project.process.modifyphase.channel.create.title");
        cancelURL =  SessionManager.getJSPRootURL() + "/process/Main.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.VIEW;

        if (errorReporter.errorsFound()) {
            nameDefault = m_phase.getName();
            descDefault = m_phase.getDesc();
            startDefault = request.getParameter("startdate");
            endDefault = request.getParameter("enddate");
            sequenceDefault = m_phase.getSequence();
            percentDefault = m_phase.getPercentComplete();
        } else {
            m_phase.clear();
            m_process.setID(id);
            m_process.load();
        }
	} else {
        %>
		<security:verifyAccess action="modify"
					   module="<%=net.project.base.Module.PROCESS%>"
					   objectID="<%=id%>" /> 
        <%
        m_phase.clear();
        m_phase.setID(id);
        m_phase.load();
        m_process.setID(m_phase.getProcessID());
        m_process.load();
        nameDefault = m_phase.getName();
        descDefault = m_phase.getDesc();
        startDefault = (request.getParameter("startdate") == null) ? m_phase.getStartDateString() : request.getParameter("startdate");
        endDefault = (request.getParameter("enddate") == null) ? m_phase.getEndDateString() : request.getParameter("enddate");
        sequenceDefault = m_phase.getSequence();
        percentDefault = m_phase.getPercentComplete();
        titleText = PropertyProvider.get("prm.project.process.modifyphase.channel.modify.title");
        cancelURL =  SessionManager.getJSPRootURL() + "/process/ViewPhase.jsp?module=" + net.project.base.Module.PROCESS + "&action=" + net.project.security.Action.VIEW;
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
<template:import type="javascript" src="/src/checkRadio.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
    var theForm;
    var errorMsg;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';

function setup() {
    load_menu('<%=user.getCurrentSpace().getID()%>');;
    theForm = self.document.mainForm;

    isLoaded = true;

<%
    String progressReportingMethod = request.getParameter("progressReportingMethod");
	if (action == net.project.security.Action.CREATE) {
	    if (!Validator.isBlankOrNull(request.getParameter("progressReportingMethod"))) {
	    	progressReportingMethod = request.getParameter("progressReportingMethod");
	    }else {
	    	progressReportingMethod = ProgressReportingMethod.SCHEDULE.getID();
	    }
	}else {
        if (!Validator.isBlankOrNull(request.getParameter("progressReportingMethod"))) {
            progressReportingMethod = request.getParameter("progressReportingMethod");
        }else {
            progressReportingMethod = m_phase.getProgressReportingMethod().getID();
        }
   	}
%>
    selectRadio(theForm.progressReportingMethod, '<%=progressReportingMethod%>');
    changeProgressMethod("<%=progressReportingMethod%>");
}

function cancel() {
    var theLocation = "<%=cancelURL%>";
    self.document.location = theLocation;
}

function reset() {
	theForm.reset();
	
	// 10/10/2007 Alex - the following are required to reset the form with the proper values
	selectRadio(theForm.progressReportingMethod, '<%=progressReportingMethod%>');
	changeProgressMethod("<%=progressReportingMethod%>");
}

function security() {
    var m_url = JSPRootURL + "/process/secureCheck.jsp?module=<%=net.project.base.Module.PROCESS%>&action=<%=net.project.security.Action.MODIFY_PERMISSIONS%>&id=<%=id%>";
    var link_win = openwin_security("security");
    link_win.document.location = m_url;
    link_win.focus();
}

function help() {
   	var helplocation=JSPRootURL+"/help/Help.jsp?page=process_main&section=phase_modify";
   	openwin_help(helplocation);
}

function submit () {
  	if (validate(theForm)) {
	   theAction("submit");
	   theForm.submit();
	}
}

function validate(theForm) {
    if(!checkTextbox(theForm.phasename,'<%=PropertyProvider.get("prm.project.process.modifyphase.namerequired.message")%>'))return false;
    if (!checkMaxLength(theForm.phasedesc,1000,'<%=PropertyProvider.get("prm.project.process.modifyphase.descriptionsize.message")%>'))return false;
    if(!checkDropdown_NoSelect(theForm.phasestatus,'<%=PropertyProvider.get("prm.project.process.modifyphase.statusrequired.message")%>'))return false;
    if(!checkTextbox(theForm.sequence,'<%=PropertyProvider.get("prm.project.process.modifyphase.sequence.required.message")%>'))return false;
    if(isNaN(theForm.sequence.value))
        return errorHandler(theForm.sequence,'<%=PropertyProvider.get("prm.project.process.modifyphase.sequencenumber.message")%>');

    <%-- only check these required fields if the manual progress option is selected --%>
    if (getValueRadio(theForm.progressReportingMethod) == "<%=ProgressReportingMethod.MANUAL.getID()%>") {
        if(!verifyNonBlankField_withAlert (theForm.startdate,'<%=PropertyProvider.get("prm.project.process.modifyphase.startdaterequired.message")%>'))return false;
        if(!verifyNonBlankField_withAlert (theForm.enddate,'<%=PropertyProvider.get("prm.project.process.modifyphase.enddaterequired.message")%>'))return false;
        if(!verifyNonBlankField_withAlert (theForm.percentcomplete,'<%=PropertyProvider.get("prm.project.process.modifyphase.percentrequired.message")%>')){
        	return false;
        } else{
	        if(isNaN(theForm.percentcomplete.value)){
	            return errorHandler(theForm.percentcomplete,'<%=PropertyProvider.get("prm.project.process.modifyphase.percentrange.message")%>');
	         }
	    }
    }
    return true;
}

function changeProgressMethod (value) {
    if (value == "manual") {
        setProgressRequiredFields(true);
    } else {
        setProgressRequiredFields(false);
    }
    theForm.progressReportingMethod.value = value;
}

function setProgressRequiredFields (required) {
    var cssClass = (required) ? 'fieldRequired' : 'fieldNonRequired';
    var isFieldDisabled = !required;

        self.document.getElementById("phase.startDate").className = cssClass;
        self.document.getElementById("startdate").disabled = isFieldDisabled;
        self.document.getElementById("phase.finishDate").className = cssClass;
        self.document.getElementById("enddate").disabled = isFieldDisabled;
        self.document.getElementById("phase.percentComplete").className = cssClass;
        self.document.getElementById("percentcomplete").disabled = isFieldDisabled;
        <%-- bfd-3281: Calendar image is enabled when the Manually report&.this phase option is not selected.--%>
        if(isFieldDisabled){
        	self.document.getElementById("star").style.visibility ='hidden';
        	self.document.getElementById("endd").style.visibility ='hidden';
        	self.document.getElementById("startdate").value = "";
        	self.document.getElementById("enddate").value = "";
	        self.document.getElementById("percentcomplete").value = "";
        	
        }
        else{
        	<%DateFormat sf = DateFormat.getInstance();%>
        	self.document.getElementById("star").style.visibility ='visible';
        	self.document.getElementById("endd").style.visibility ='visible';
	        self.document.getElementById("startdate").value = "<%try{ out.print(sf.formatDate(m_phase.getStart()));}catch(Exception ee){} %>";
    	    self.document.getElementById("enddate").value = "<% try{ out.print(sf.formatDate(m_phase.getEnd()));}catch(Exception ee){} %>";
        	self.document.getElementById("percentcomplete").value = "<%try{ out.print(Math.round(Float.parseFloat(m_phase.getPercentComplete())));}catch(Exception ee){} %>";
 	 }
}

<% if (action ==  net.project.security.Action.MODIFY) { %>
function remove () {
	Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.process.phase.delete.message")%>', 
		function(btn){
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

<body class="main" bgcolor="#FFFFFF" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.project.nav.process">
	<tb:setAttribute name="leftTitle">
		<history:history />
	</tb:setAttribute>
	<tb:band name="standard">
		<tb:button type="remove" enable="<%=enableRemoveButton%>" label='<%=PropertyProvider.get("prm.project.process.deletephase.label")%>'/>
		<tb:button type="security" enable="<%=enableSecurityButton%>" />
	</tb:band>
</tb:toolbar>

<div id='content'>

<form name="mainForm" method="post" action="modifyPhaseProcessing.jsp">
<input type="hidden" name="theAction">
<input type="hidden" name="id" value="<%=id%>">
<input type="hidden" name="action" value="<%=action%>">
<input type="hidden" name="module" value="<%=module%>">
<input type="hidden" name="process_id" value="<%=m_process.getID()%>">

<table border="0" align="left" cellpadding="0" cellspacing="0" width="600">
<tr align="left" class="channelHeader">
        <td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
        <td nowrap colspan="4" class="channelHeader">&nbsp;<%=titleText%>
        </td>

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
        <td nowrap width="20%" class="fieldRequired"><%=PropertyProvider.get("prm.project.process.modifyphase.name.label")%>
        </td>
        <td colspan="3">
        <input type="text" name="phasename" size="40" maxlength="80" value="<%=nameDefault%>">
        </td>
        <td>&nbsp;</td>
</tr>
<tr align="left" valign="top">
        <td>&nbsp;</td>
        <td nowrap width="20%" class="fieldNonRequired"><%=PropertyProvider.get("prm.project.process.modifyphase.description.label")%>
        </td>
        <td nowrap colspan="3">
        <textarea cols="80" rows="3" name="phasedesc"><%=net.project.util.HTMLUtils.escape(descDefault)%></TEXTAREA>
        </td>
        <td>&nbsp;</td>
</tr>
<tr align="left" valign="middle">
        <td>&nbsp;</td>
        <td nowrap width="20%" class="fieldRequired"><%=PropertyProvider.get("prm.project.process.modifyphase.status.label")%>
        </td>
        <td nowrap>
                <select name="phasestatus"><%=m_phase.getStatusOptionList()%>
                </select>
        </td>
        <td nowrap width="20%" class="fieldRequired"><%=PropertyProvider.get("prm.project.process.modifyphase.sequence.label")%>
        </td>
        <td>
        <input type="text" name="sequence" size="3" maxlength="3" value="<%=sequenceDefault%>">
        </td>
        <td>&nbsp;</td>
</tr>



<%-- PERCENT COMPLETE & START/FINISH DATE STUFF --%>

<tr><td colspan="6">&nbsp;</td></tr>

<tr class="channelHeader">
<td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
<td nowrap class="channelHeader" colspan="4"><nobr><display:get name="prm.project.process.modifyphase.progress.section"/></nobr></td>
<td align=right width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>

<tr><td colspan="6">&nbsp;</td></tr>

<tr align="left">
<td>&nbsp;</td>
<td class="tableContent" colspan="4">
    <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
            <td class="tableContent">
                <input type="radio" name="progressReportingMethod"  onclick="changeProgressMethod(this.value);" value="<%=ProgressReportingMethod.SCHEDULE.getID()%>">
                <display:get name="prm.project.process.modifyphase.progress.schedule.label"/>
            </td>
        </tr>
        <tr>
            <td class="tableContent">
                <br><display:get name="prm.project.process.modifyphase.progress.schedule.description"/>
            </td>
        </tr>

        <tr><td>&nbsp;</td></tr>

        <tr>
            <td class="tableContent">
                <input type="radio" name="progressReportingMethod" onclick="changeProgressMethod(this.value);" value="<%=ProgressReportingMethod.MANUAL.getID()%>">
                <display:get name="prm.project.process.modifyphase.progress.manual.label"/>
            </td>
        </tr>
        <tr>

        <td class="tableContent">
            <br>
            <table width="100%" border="0" cellpadding="0" cellspacing="0">

            <tr align="left" valign="middle">
                    <td>&nbsp;</td>
                    <td nowrap width="20%"><span id="phase.startDate"><%=PropertyProvider.get("prm.project.process.modifyphase.startdate.label")%></span>
                    </td>
                    <td nowrap>
                            <input type="text" id="startdate" name="startdate" size="10" maxlength="10" value="<%=startDefault%>">
        <%-- bfd-3281: Calendar image is enabled when the Manually report&.this phase option is not selected.--%>
                          <span id="star" style="visibility:hidden"> <util:insertCalendarPopup fieldName="startdate" /></span>
                    </td>
                    <td nowrap width="20%"><span id="phase.finishDate"><%=PropertyProvider.get("prm.project.process.modifyphase.enddate.label")%></span>
                    </td>
                    <td nowrap>
                            <input type="text" id="enddate" name="enddate" size="10" maxlength="10" value="<%=endDefault%>">
        <%-- bfd-3281: Calendar image is enabled when the Manually report&.this phase option is not selected.--%>
                         <span id="endd" style="visibility:hidden">  <util:insertCalendarPopup fieldName="enddate" /></span>
                    </td>
                    <td>&nbsp;</td>
            </tr>

            <tr align="left" valign="top">
            <td>&nbsp;</td>
                    <td nowrap width="20%"><span id="phase.percentComplete"><%=PropertyProvider.get("prm.project.process.modifyphase.percent.label")%></span>
                    </td>
                    <td colspan="3">
                    <input type="text" id="percentcomplete" name="percentcomplete" size="7" maxlength="7" value="<%=percentDefault%>">%
                    </td>
                    <td>&nbsp;</td>
            </tr>

            </table>

        </td>
    </tr>
    </table>
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
