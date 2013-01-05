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
|   $RCSfile$
|   $Revision: 20713 $
|   $Date: 2010-04-17 11:07:33 -0300 (sáb, 17 abr 2010) $
|   $Author: umesha $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Initiate Domain Migration -- Step 2"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.User,
            java.util.Iterator,
            net.project.security.SessionManager,
            java.util.ArrayList,
			net.project.configuration.ConfigurationSpace,
			net.project.base.property.PropertyProvider,
			net.project.resource.PersonListBean"
%>                                                                        
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="domainMigration" class="net.project.security.domain.DomainMigration" scope="session"/>
<jsp:useBean id="domainMigrationManager" class="net.project.security.domain.DomainMigrationManager" scope="request"/>
<jsp:useBean id="csManager" class="net.project.configuration.ConfigurationSpaceManager" scope="request"/>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/> 
<jsp:useBean id="userDomain" class="net.project.security.domain.UserDomain" scope="session"/> 

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<html>
<head>
<title><%=PropertyProvider.get("prm.application.domainmigration.wizardpage3.channel.title")%></title>
<%
	userDomain.load();
	
	Iterator itr = csManager.getNotSupportedConfigurationProviderList(userDomain).iterator();
	int size = csManager.getNotSupportedConfigurationProviderList(userDomain).size();
	
	Object[] str = { userDomain.getName() } ;
	
%>
<template:getSpaceCSS />
<template:import type="javascript" src="/src/window_functions.js" />
<template:import type="javascript" src="/src/util.js" />

<template:import type="javascript" src="/src/checkComponentForms.js" />
<template:import type="javascript" src="/src/errorHandler.js" />

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    

function setup() {
	theForm = self.document.forms[0];
	isLoaded = true;
}

// do a  redirect on canceling
function cancel() { self.close(); }

function reset() {
	theForm.reset();
}

function next() {
	if(theForm.elements["selected"]) {
		if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) {
			theAction("next");
			theForm.submit();
		}	
	} else {
		theAction("next");
		theForm.submit();
	} 	
}
function back() {
	theAction("back");
	theForm.submit();
}

function enableConfigurations(theRadio) {
	
	for (var i = 0; i< theForm.elements.length; i++) {
        if ((theForm.elements[i].name.indexOf('configurations') > -1)) {
            if (theRadio.checked && theRadio.value =='1') {
				theForm.elements[i].disabled = false;	                
            } else {
				theForm.elements[i].disabled = true;	                
			}
        }
    }
}

function help()
{
	var helplocation=JSPRootURL+"/help/Help.jsp?page=initiate_domainmigration&section=page2";
	openwin_help(helplocation);
}


</script>
</head>

<body class="main" onLoad="setup();">

<form action="<%=SessionManager.getJSPRootURL()%>/admin/domain/InitiateMigration2Processing.jsp" method="post">
    <input type="hidden" name="theAction">

<table width="100%" border="0" cellspacing="0" cellpadding="0">

	<tr>
        <td>&nbsp;</td>
	</tr>	
	
    <tr class="channelHeader">
        <td class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0" hspace="0" vspace="0"></td>
        <td nowrap class="channelHeader"><%=PropertyProvider.get("prm.application.domainmigration.wizardpage3.channel.title") %></td>
        <td align="right" class="channelHeader">&nbsp;</td>
        <td align="right" class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0" hspace="0" vspace="0"></td>
    </tr>
	
	<tr>
        <td>&nbsp;</td>
	</tr>	
	
	<tr>	
		<td>&nbsp;</td>		
		<td colspan="4" class="instructions" wrap >
       	 &nbsp;&nbsp;<%=PropertyProvider.get("prm.application.domainmigration.wizardpage3.instructions",str) %>
		</td>		
	</tr>
    <tr>
        <td>&nbsp;</td>
        <td colspan="2">

	<table width="100%" border="0">
		
<%

	
	if(size > 0 ) { 
	
	
%>
    	
	<tr>
        <td>&nbsp;</td>
	</tr>	
	<tr>
        <td colspan="4" align="left" class="tableContent"><input type="Radio" name="selected" value="0" onClick="enableConfigurations(this)">&nbsp;&nbsp;
			<%=PropertyProvider.get("prm.project.admin.domain.any.users.configurations.label") %> </input></td>
	</tr>	
	<tr>
        <td colspan="4" align="left" class="tableContent"><input type="Radio" name="selected" value="1" onClick="enableConfigurations(this)">&nbsp;&nbsp;
            <%=PropertyProvider.get("prm.project.admin.domain.any.users.configurations.2.label") %></input></td>
	</tr>
	
	<tr>
        <td>&nbsp;</td>
	</tr>
	
	<tr> 
        <td  colspan="4" class="instructions" wrap><%=PropertyProvider.get("prm.application.domainmigration.wizardpage3.configurationsnotsupported.message",str) %></td>
	</tr>	
	
	<tr>
        <td>&nbsp;</td>
	</tr>

<%
	}
%>	
	
<%
		
	while(itr.hasNext()) {
	
		ConfigurationSpace cspace = (ConfigurationSpace) itr.next();
		
		ArrayList newlyAddedConfigurationCollection = userDomain.getNewlyAddedConfigurationCollection();
			           
      		if(cspace !=null && cspace.getID() != null) {	

  %>	
		<tr>
			<td  colspan="4" align="left" class="tableContent">
  			<input type="Checkbox" disabled name="configurations" <%= newlyAddedConfigurationCollection.contains(cspace.getID()) ? "CHECKED" : "" %>  value='<%=cspace.getID() %>' >&nbsp;&nbsp;<%=cspace.getName() %> </input>
		</td>
	</tr>

<%
			}
	}				
%>	
	<tr>
        <td>&nbsp;</td>
	</tr>	

	<tr>
<%
	if(size > 0 ) { 
%>	
        <td  colspan="4" class="tableContent" wrap><%=PropertyProvider.get("prm.application.domainmigration.wizardpage3.finalinstructions1.message",str) %></td>	
		
<%
	} else {
%>		
		<td  colspan="4" class="tableContent" wrap><%=PropertyProvider.get("prm.application.domainmigration.wizardpage3.finalinstructions2.message") %></td>			
<%
	}
%>	
	</tr>
		
</table>

        </td>
        <td>&nbsp;</td>
    </tr>
	
	<tr>
        <td>&nbsp;</td>
	</tr>
</table>

<tb:toolbar style="action" showLabels="true">
            <tb:band name="action">
                <tb:button type="next"/>
				<tb:button type="back"/>
		        <tb:button type="cancel" />
		        
            </tb:band>
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>
</body>
</html>