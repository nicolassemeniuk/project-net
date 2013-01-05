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
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.User,
            net.project.base.Module,
            net.project.base.property.PropertyProvider,
            net.project.security.SessionManager,
            net.project.security.domain.UserDomainCollection,
            java.util.Iterator,
            net.project.admin.setting.SettingsEditor,
            net.project.util.HtmlGen,
            net.project.admin.setting.SettingHelper"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="settingsEditor" type="net.project.admin.setting.SettingsEditor" scope="request" />

<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>
<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<template:getDoctype />
<html>
<head>
<META http-equiv="expires" content="0"> 
<title><display:get name="prm.global.application.title" /></title>

<%------------------------------------------------------------------------
  -- Import CSS and Javascript Files
  ----------------------------------------------------------------------%>

<%-- Import CSS --%>
<template:getSpaceCSS space="application" />

<%-- Import Javascript --%>
<template:import type="javascript" src="/src/errorHandler.js" />
<template:getSpaceJS space="application" />

<script language="javascript">
        var theForm;
        var errorMsg;
        var isLoaded = false;
        var JSPRootURL = "<%=SessionManager.getJSPRootURL()%>";

    function setup() {
	    theForm = self.document.forms["main"];
        isLoaded = true;
    }

    function reset() {
        theForm.reset();
    }

	function help()	{
		var helplocation=JSPRootURL+"/help/Help.jsp?page=domain_main";
		openwin_help(helplocation);
	}

    function submit() {
        theAction("modify");
        theForm.submit();
    }

    function cancel() {
        self.document.location=JSPRootURL + "/admin/Main.jsp?module=<%=Module.APPLICATION_SPACE%>";
    }
    function toggle(namePrefix, optionElement) {

        var inputElement = theForm.elements[namePrefix + "_customValue"];

        if (optionElement.value == "default") {
            inputElement.disabled = true;
        } else {
            inputElement.disabled = false;
            inputElement.focus();
        }
    }

    function resetDefault() {
		Ext.MessageBox.confirm('<%=PropertyProvider.get("prm.global.extconfirm.title")%>', '<%=PropertyProvider.get("prm.project.admin.setting.main.resetsetting.confirm")%>', function(btn) { 
			if(btn == 'yes'){ 
				theAction("resetDefault");
           		theForm.submit();
			} else {
			 	return false;
			}
		});
    }


</script>

<style type="text/css">
    .requiresRestart {
        color: #FF0000;
    }
</style>

</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />

<tb:toolbar style="tooltitle" showAll="true"  groupTitle="prm.application.nav.systemsettings" 	>
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:module display="Edit Systems Settings"
                            jspPage='<%=SessionManager.getJSPRootURL() + "/admin/settings/Main.jsp"%>'
                            queryString='<%="module=" + Module.APPLICATION_SPACE%>' />
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<br>

<form name="main" action="<%=SessionManager.getJSPRootURL()%>/servlet/AdminController/SettingsUpdate" method="post">
    <input type="hidden" name="module" value="<%=Module.APPLICATION_SPACE%>">
	<input type="hidden" name="theAction">

<table border="0" cellpadding="0" cellspacing="0"  width="100%">
    <tr class="channelHeader">
	    <td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
	    <td valign="middle" align="left" class="channelHeader">
	    	<%=PropertyProvider.get("prm.project.admin.settings.system.label") %>
	    </td>
	    <td width=1% align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
    </tr>
    <tr><td colspan="3">&nbsp;</td></tr>
    <tr>
        <td width="1%">&nbsp;</td>
        <td class="instructions">
            <b><%=PropertyProvider.get("prm.project.admin.settings.note.label")%></b> 
            <%=PropertyProvider.get("prm.project.admin.settings.message.label")%>
        </td>
        <td width="1%">&nbsp;</td>
    </tr>
    <%  if (settingsEditor.isAnySettingDifferent()) { %>
    <tr>
        <td width="1%">&nbsp;</td>
        <td class="tableContent requiresRestart">
            <%=PropertyProvider.get("prm.project.admin.settings.requires.restart.label")%>
        </td>
        <td width="1%">&nbsp;</td>
    </tr>
    <%  } %>
    <tr><td colspan="3">&nbsp;</td></tr>
    <tr>
        <td width="1%">&nbsp;</td>
    	<td>

    <table border="0">
<%
    int count = 0;
    for (Iterator iterator = settingsEditor.getSettings().iterator(); iterator.hasNext(); count++) {
        SettingHelper nextSettingHelper = (SettingHelper) iterator.next();
        String namePrefix = "setting_" + nextSettingHelper.getName();
        String radioName = namePrefix + "_select";
        pageContext.setAttribute("settingHelper", nextSettingHelper);
%>
<jsp:useBean id="settingHelper" type="net.project.admin.setting.SettingHelper" scope="page" />

    <tr><td class="rowSep" colspan="3"></td></tr>

    <%-- Setting name --%>
    <tr><td colspan="2">
        <table>
            <tr>
                <td class="tableHeader">
                    <c:out value="${settingHelper.name}"/>
                </td>
                <td class="tableContent requiresRestart">
                    <%  if (settingHelper.isDifferentFromRuntime()) { %>
                        <%=PropertyProvider.get("prm.project.admin.settings.current.value.label")%>
                         <code><c:out value="${settingHelper.currentRuntimeValue}"/></code>
                    <%  } else { %>
                        &nbsp;
                    <%  } %>
                </td>
            </tr>
        </table>
    </td></tr>

    <tr class="content">
        <td width="2%">&nbsp;</td>
        <td>
        <table>

        <!-- Use default radio option -->
        <tr class="tableContent">
            <td>
                <input type="radio" name="<%=radioName%>" id="<%=radioName%>_default" value="default" onClick="toggle('<%=namePrefix%>', this)" <c:out value="${settingHelper.defaultRadioChecked}"/>>&nbsp;
            </td>
            <td>
                <label for="<%=radioName%>_default">
                	<%=PropertyProvider.get("prm.project.admin.settings.use.default.value.label") %>
                </label>
            </td>
            <td>
                <code><%=settingHelper.getDefaultValue()%></code>
            </td>
        </tr>

        <!-- Custom value radio option -->
        <tr class="tableContent">
            <td>
                <input type="radio" name="<%=radioName%>" id="<%=radioName%>_custom" value="custom" onClick="toggle('<%=namePrefix%>', this)" <c:out value="${settingHelper.customRadioChecked}"/>>&nbsp;
            </td>
            <td>
                <label for="<%=radioName%>_custom">
                	<%=PropertyProvider.get("prm.project.admin.settings.specify.value.label")%>
                </label>
            </td>
            <td>
                <input type="text" name="<%=namePrefix%>_customValue" maxLength="1000" size="100" <c:out value="${settingHelper.customInputDisabled}"/>
                        value='<c:out value="${settingHelper.customValue}"/>'>
            </td>
        </tr>

        <!-- Display a warning message if necessary -->
        <%  if (settingHelper.isFlagWarning()) { %>
        <tr class="tableContent">
            <td>&nbsp;</td>
            <td colspan="2">
                <img src="<%=SessionManager.getJSPRootURL()%>/images/warning_16x16.gif" border="0" alt="Warning Symbol" title="<%=PropertyProvider.get("prm.project.admin.settings.changing.alt")%>">
                <%=PropertyProvider.get("prm.project.admin.settings.warning.label")%>
            </td>
        </tr>
        <%  } %>

        </table>
        </td>
    </tr>
    <tr class="tableContent">
        <td width="2%">&nbsp;</td>
        <td>
            <%=HtmlGen.formatHtml(settingHelper.getDescription())%>
        </td>
    </tr>
    <tr class="tableContent">
        <td width="2%">&nbsp;</td>
        <td>
        	<%=PropertyProvider.get("prm.project.admin.settings.example.values.label")%>
            <code><%=settingHelper.formatExampleValuesCommaSeparated()%></code>
        </td>
    </tr>
    <tr><td colspan="2">&nbsp;</td></tr>
<%
    }
%>
    </table>

    	</td>
        <td width="1%">&nbsp;</td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action">
        <tb:button type="submit" />
        <tb:button type="reset" label="Reset to default" function="javascript:resetDefault();" />
    </tb:band>
</tb:toolbar>

</form>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceNavBar space="application"/>
</body>
</html>
