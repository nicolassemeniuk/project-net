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
    info="Modify Status"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
			net.project.base.Module,
			net.project.base.property.PropertyProvider,
			net.project.license.StatusReason,
			net.project.license.LicenseStatusCode,
			net.project.security.User,
            net.project.security.SessionManager,
            net.project.security.Action"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="license" class="net.project.license.License" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>
<html>
<head>
<title>Modify License Status  </title>

<%-- Additional head stuff --%>
<%-- Import JavaScript --%>
<template:import type="javascript" src="/src/util.js" />
<template:import type="javascript" src="/src/checkLength.js" />

<%-- Import CSS --%>
<template:getSpaceCSS space="application" />
<template:getSpaceJS space="application" />

<script language="javascript">
	var theForm;
	var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
    isLoaded = true;
	theForm = self.document.forms["main"];
}

function modifyStatus() {
		
		str = getSelectedValue(theForm.licenseStatusCode);
		
		if (str != '<%= LicenseStatusCode.ENABLED.getCodeID() %>') {
			if( str != '<%= LicenseStatusCode.CANCELED.getCodeID() %>') {
				if (str != '<%= LicenseStatusCode.DISABLED.getCodeID() %>' ) {
					var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.admin.license.modifystatus.error.message")%>';
					extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
					return;
				}
			}
		}
		
		reasonCodeStr = getSelectedValue(theForm.reasonCodeOption);
		
		if (str != '<%= LicenseStatusCode.ENABLED.getCodeID() %>') {
			if ( reasonCodeStr != "existingCode" ) {
				if (reasonCodeStr != "newReasonCode") {
					var errorMessage = '<%=PropertyProvider.get("prm.global.javascript.admin.license.reasoncode.error.message")%>';
					extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
					return;
				}
			}
		}
		
		if ( reasonCodeStr == "newReasonCode" ) {
			if(	!verifyNonBlankField_withAlert (theForm.shortName, '<%=PropertyProvider.get("prm.project.admin.license.title.reason.code.js")%>')) {
				return;	
			}
			if(	!verifyNonBlankField_withAlert (theForm.message, '<%=PropertyProvider.get("prm.project.admin.license.enter.message.reason.code")%>')) {
				return;
			}
            if (!checkMaxLength(theForm.message, 1000, '<%=PropertyProvider.get("prm.project.admin.license.message.must.be.1000.characters.js")%>')) return;
		}
		
		disable = '<%= LicenseStatusCode.DISABLED.getCodeID()%>';
		cancelCode = '<%= LicenseStatusCode.CANCELED.getCodeID()%>';
		enable = '<%= LicenseStatusCode.ENABLED.getCodeID()%>';

		if(str == disable){
			Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.admin.license.disable.license.js")%>', function(btn) { 
				if(btn == 'yes'){ 
					theForm.submit();
				}
			 });
		} else if (str == cancelCode) {
			Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.admin.licese.cancel.license.js")%>', function(btn) { 
				if(btn == 'yes'){ 
					theForm.submit();
				}
			 });
		} else if (str == enable) {
			Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.admin.license.enable.license.js")%>', function(btn) { 
				if(btn == 'yes'){ 
					theForm.submit();
				}
			 });
		}
}
    
function cancel() {
	self.document.location = '<%=SessionManager.getJSPRootURL() + "/admin/license/LicenseDetailView.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW%>';

}

function reset() { 
	self.document.location = '<%=SessionManager.getJSPRootURL() + "/admin/license/ModifyStatus.jsp?module=" + Module.APPLICATION_SPACE + "&action=" + Action.DELETE%>'; 
}

function help() {
   	openwin_help(JSPRootURL + "/help/Help.jsp?page=admin_license&section=modify_status");
}

</script>
</head>

<body class="main" onload="setup();">

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.application.nav.licensemanager"> 
    <tb:setAttribute name="leftTitle">
        <history:history>
			<history:business display="<%= applicationSpace.getName()%>"
                              jspPage='<%=SessionManager.getJSPRootURL() + "/admin/license/Main.jsp"%>'
                              queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
			<history:module display="Licensing"
                              jspPage='<%=SessionManager.getJSPRootURL() + "/admin/license/Main.jsp"%>'
                              queryString='<%="module=" + Module.APPLICATION_SPACE%>' />				  
            <history:page display="Modify Status"
                          jspPage='<%=SessionManager.getJSPRootURL() + "/admin/license/ModifyStatus.jsp"%>'
                          queryString='<%="module=" + Module.APPLICATION_SPACE + "&action=" + Action.VIEW%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

 <form name="main" method="post" action='<%=SessionManager.getJSPRootURL() + "/admin/license/ModifyStatusProcessing.jsp"%>'>
	<input type="hidden" name="module" value='<%= Module.APPLICATION_SPACE %>'>
	<input type="hidden" name="action" value='<%= Action.DELETE %>'>
	
<channel:channel name='<%="ApplicationSpaceMain_" + applicationSpace.getName()%>' customizable="false">
    <channel:insert name='<%="ModifyLicenseStatus_" + applicationSpace.getName()%>'
                    title="Modify Status" minimizable="false" closeable="false">                    
	</channel:insert>						
</channel:channel>

<table width="100%" cellpadding="0" cellspacing="0" border="0">
<tr>
 	<td colspan="6" align="left" class="tableContent">
 		<%=PropertyProvider.get("prm.project.admin.license.disable.or.cancel.message")%>
	</td>
</tr>

 <tr><td colspan="6">&nbsp;</td></tr>

 
 <tr><td colspan="6"  class="fieldRequired"><%=PropertyProvider.get("prm.project.admin.license.new.status.label")%> </td></tr>
 <tr><td colspan="6">&nbsp;</td></tr>	    
	<tr align="left">
	
	
	<% if(!license.getStatus().getCode().equals(LicenseStatusCode.ENABLED)) { %>				
	<tr>
		<td align="right">
			<input type="radio" name="licenseStatusCode" value='<%= LicenseStatusCode.ENABLED.getCodeID()%>'/>
		</td>
		<td class="fieldNonRequired"  align="left">
                 <%=PropertyProvider.get("prm.project.admin.license.enabled.label")%>
        </td>
		<td align="left" class="tableContent">
				  <%=PropertyProvider.get("prm.project.admin.license.allow.associated.login.message")%>
		</td>
		<td colspan="3">&nbsp;</td>
	</tr>
	<tr><td colspan="6">&nbsp;</td></tr>
	<% } %>
			
	<% if(!license.getStatus().getCode().equals(LicenseStatusCode.DISABLED)) { %>			
	<tr>
		<td align="right">
			<input type="radio" name="licenseStatusCode" value='<%= LicenseStatusCode.DISABLED.getCodeID()%>'/>
		</td>
		<td class="fieldNonRequired"  align="left">
                 <%=PropertyProvider.get("prm.project.admin.license.disabled.label")%>
       	</td>
		<td align="left" class="tableContent">
			 <%=PropertyProvider.get("prm.project.admin.license.prevents.users.associated.message")%>
		</td>
	</tr>
	<tr><td colspan="3">&nbsp;</td></tr>
	<% } %>		
	<tr>
		<td align="right">
			<input type="radio" name="licenseStatusCode" value='<%= LicenseStatusCode.CANCELED.getCodeID()%>'/>
		</td>
		<td class="fieldNonRequired" align="left">
            <%=PropertyProvider.get("prm.project.admin.license.cancelled.label")%>
    	</td>
		<td colspan="4" class="tableContent">
		 	<%=PropertyProvider.get("prm.project.admin.license.users.associated.message")%>
		 </td>
	</tr>
	<tr><td colspan="6">&nbsp;</td></tr>
	<tr><td colspan="6">&nbsp;</td></tr>
	<tr><td colspan="6" align="left" class="fieldRequired"><%=PropertyProvider.get("prm.project.admin.reason.code.label")%></td></tr>
	<tr><td colspan="6">&nbsp;</td></tr>
	<tr>
		<td align="left">
			<input type="radio" name="reasonCodeOption" value="existingCode">
		</td>
		<td class="fieldNonRequired" align="left">
			<%=PropertyProvider.get("prm.project.admin.license.select.reason.code.label")%>
         </td>
	 	 <td align="left">
			<select name="reasonCode">
				<%= StatusReason.getReasonOptionList()%>
			</select>
		</td>
		<td colspan="3">&nbsp;</td>		
	</tr>
	<tr><td colspan="6">&nbsp;</td><tr>	
	
	<tr align="left">
		<td align="left">
			<input type="radio" name="reasonCodeOption" value="newReasonCode">
		</td>
		<td align="left" class="fieldNonRequired"><%=PropertyProvider.get("prm.project.admin.license.create.new.reason.code.label")%> </td>
		<td colspan="6">&nbsp;</td>
	</tr>
	<tr><td colspan="6">&nbsp;</td><tr>
	<tr>
		<td class="fieldNonRequired" colspan="2">
          <%=PropertyProvider.get("prm.project.admin.license.title.label")%>
    	</td>
		<td>
			<input type="Text" name="shortName" size="25" maxlength="100">
		</td>
		<td colspan="3">&nbsp;</td>
     </tr>
     <tr>
        <td class="fieldNonRequired" colspan="2">
        	<%=PropertyProvider.get("prm.project.admin.license.message.label")%>
        </td>
        <td class="fieldContent" colspan="2">
             <code>
                <textarea name="message" rows="5" cols="50"></textarea>
             </code>
        </td>
		<td colspan="6">&nbsp;</td>
     </tr>
</table>

<tb:toolbar style="action" showLabels="true">
				<tb:band name="action">
					<tb:button type="submit" label="Submit" function="javascript:modifyStatus();"/>
					<tb:button type="cancel" />
				</tb:band>
</tb:toolbar>

</form>	
<br>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>
