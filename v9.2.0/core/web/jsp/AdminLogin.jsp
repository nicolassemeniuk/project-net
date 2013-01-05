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
    info="Login page"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
            net.project.resource.Language,
            net.project.security.domain.DomainOption,
            net.project.security.domain.DomainOptionList,
            net.project.security.SessionManager,
            net.project.security.User,
            net.project.util.Validator"

%>

<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="brandManager" class="net.project.brand.BrandManager" scope="session" />

<%
    SessionManager.initializeFromRequest(request);

	Object loginError = null;

	String[] userDomain = request.getParameterValues("userDomain");
	String userName = request.getParameter("login");
    String verified = (String) session.getAttribute("verified");
    session.removeAttribute("verified");
	session.setAttribute("fromAdminLogin", true);
%>

<%---------------------------------------------------------------------------------------------------
  -- Misc Setup for new user session.
  -------------------------------------------------------------------------------------------------%>
<%
    //remove registration information if loaded
    session.removeAttribute("registration");

    // remove the veriable that says that I can get inside Help
    session.removeAttribute("insideHelp");

    // logout the previous user
    user.setAuthenticated(false);
    user.clear();
%>

<%---------------------------------------------------------------------------------------------------
  -- Load Property Provider Configuration
  -------------------------------------------------------------------------------------------------%>
<%
    // Initializes the Properties
    PropertyProvider.setContextFromRequest (request, application);

    // If there was no language, then we look for one in a cookie
    String language = request.getParameter("language");
    if (Validator.isBlankOrNull(language)) {
        language = net.project.util.HttpUtils.getCookieValue(request, "prm.login.userLanguage");
        if (!Validator.isBlankOrNull(language)) {
            PropertyProvider.setLanguage(language, application);
        }
    }
%>

<template:insert template="template/PreAuthentication.jsp">
	<template:put name="title" content='<%=PropertyProvider.get ("prm.global.login.pagetitle")%>' direct="true" />

<%-- Additional HEAD stuff --%>
<template:put name="head">
<template:getSpaceCSS space="personal"/>
<%-- Import Javascript --%>
<template:import type="javascript" src="/src/util.js" />
<template:import type="javascript" src="/src/com_functions.js" />
<template:import type="javascript" src="/src/cookie.js" />
<template:import type="javascript" src="/src/window_functions.js" />
<template:import type="javascript" src="/src/browser.js" />

<script language="javascript">

<%-- Do a cookie check, if javascript is turned off the user will be notifed --%>
SetCookie("testcookie","Cookies On!",null,"/");
if(GetCookie("testcookie")==null)
	top.location.href = "CookieRequired.jsp"

detectBrowser("<%=SessionManager.getJSPRootURL()%>/BadBrowser.jsp");

function newUser() {
<%  if (verified != null) { %>
		extAlert(errorTitle, '<%=verified%>' , Ext.MessageBox.ERROR);
<%  } %>
}

<%-- Make sure this login page does not load in any frame. --%>
if (window != top) {
	top.location.href = location.href;
}

<%--
    Save username and domain to browser cookie.
    Note: It is critical to user "getSelectedValue" to handle case
    when domain may be a dropdown list or a hidden field
--%>
function doAction(action) {
//  replaceVar("pnet_login", document.forms[0].J_USERNAME.value, 365, "/");
//  replaceVar("prm.login.userDomain", getSelectedValue(document.forms[0].userDomain), 365, "/");
    setVar("pnet_login", document.forms[0].J_USERNAME.value, 365);
    setVar("prm.login.userDomain", getSelectedValue(document.forms[0].userDomain), 365);
    setVar("prm.login.userLanguage", getSelectedValue(document.forms[0].language), 365);

    if(self.document.Login && processForm(self.document.Login)) {
        self.document.Login.submit();
        return true;
    }

    return false;
}

<%-- get last username and userDomain from browser cookie. --%>
function load_login() {
<%
	if (userName != null ) {
%>
		var login="<%=userName %>";
<%
	} else {
%>
		var login = getVar("pnet_login");
<%
	}
%>
	if(self.document.Login)
	{
		if(login != null && login != "")
		{
			self.document.Login.J_USERNAME.value = login;
            <%-- Only set from cookie if it is a list of domains :
                 if there is only one domain (in a hidden field)
                 we dont want to overwrite its value --%>

			<%
				if(userDomain == null ) {
			%>
            if (self.document.Login.userDomain.type != "hidden") {
    			setSelectedValue (self.document.Login.userDomain, getVar("prm.login.userDomain"));
            }
			<%
				}
			%>

			self.document.Login.J_PASSWORD.focus();
			self.document.Login.J_PASSWORD.select();
		}
		else
			self.document.Login.J_USERNAME.focus();
	}
}

<%-- Check what browser is being used, this info is needed in checkChar() --%>
	var isNav, isIE;

	if (parseInt(navigator.appVersion) >= 4) {
          if(navigator.appName == "Netscape") {
            isNav = true;
          } else {
            isIE = true;
          }
        }

	if(isNav) document.captureEvents(Event.KEYUP);
        document.onkeyup = checkChar

	function checkChar(evt)
        {
	  var theBtnOrKey;
	  if (isNav) {
	    if(evt.target.type == "password"){
	      theBtnOrKey = evt.which;
            }
	  }
	  else {
	    if (window.event.srcElement.type == "password") {
	      theBtnOrKey = window.event.keyCode;
            }
	  }

	  if(theBtnOrKey == 13){
	      doAction('submit');
          }
        }

 function processForm(myForm)
 {
 	if(!verifyNonBlankField(myForm.J_USERNAME.value)) {
	 	extAlert(errorTitle, '<display:get name="prm.global.login.usernamerequired.message" />' , Ext.MessageBox.ERROR);
        focus(myForm, 'J_USERNAME');
	    return false;
	}

 	if(myForm.J_USERNAME.value != 'appadmin' ) {
 		extAlert(errorTitle, '<display:get name="prm.global.login.sso.onlyAdminCanLogin" />' , Ext.MessageBox.ERROR);
        focus(myForm, 'J_USERNAME');
	    return false;
	}
	
	if(!verifyNonBlankField(myForm.J_PASSWORD.value)) {
        extAlert(errorTitle, '<display:get name="prm.global.login.passwordrequired.message" />' , Ext.MessageBox.ERROR);
        focus(myForm, 'J_PASSWORD');
		return false;
	}
	return true;
 }


 function verifyNonBlankField (text) {

 	var isOk = true;

 	if ((trim(text)).length == 0) {
 		isOk = false;
 	}
 	return isOk;
}


 function helpMe()
 {
 	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/HelpDesk.jsp?state=popup";
 	openwin_help(helplocation);
}


function openPopUp(URL)
{
   my_new_window = window.open(URL, "tool_popup", "height=500,width=600, resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=1,status=0,toolbar=0");
   my_new_window.focus();
 }

function changeLanguage(dropDown) {
   self.document.location="<%=SessionManager.getJSPRootURL()%>/Login.jsp?language=" + dropDown.value + "&brand=<%=request.getParameter("brand")%>";
}
</script>

</template:put>
<%-- End of HEAD --%>

<%-- Begin Content --%>
<template:put name="bodyOnLoad" content="newUser();load_login();" direct="true" />
<template:put name="content">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<tr>

	<td align="left" valign="bottom">
		<display:img src="@prm.global.brand.logo.login" href="@prm.global.brand.logo.login.href" />
	</td>
	<td>
		&nbsp;&nbsp;
	</td>
	<td align="left" valign="bottom">

         <display:if name="@prm.global.poweredby.isenabled">
		<display:img src="@prm.global.poweredby.logo.login" href="http://www.project.net" if="@prm.global.poweredby.logo.login.display" />
         </display:if>
	</td>
	<td>
		&nbsp;&nbsp;
	</td>
<%-------------------------------------------------------------------------------------------------------------------------------------------
	-- Login Form
    ---------------------------------------------------------------------------------------------------------------------------------------%>
    <form name="Login" action="<%= SessionManager.getJSPRootURL() %>/LoginProcessing.jsp" onSubmit="return processForm(this);" method="POST">

    <%-- User Language Selection --%>
    <td align="right" valign="bottom">
        <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
        <td width="100%" class="tableContent">&nbsp;</td>
        <td align="right" valign="top" class="tableContent" nowrap>
	        <display:get name="prm.global.login.language.label" />
    	</td>
    <% if (brandManager.getSupportedLanguages().size() == 1) { %>
        <td align="left" valign="top" class="tableContent" align="left" valign="center" nowrap height="22">
            <input type="hidden" name="language" value="<%=((String)brandManager.getSupportedLanguages().get(0))%>">
        &nbsp;&nbsp;<%=Language.getLanguages().getLanguageForCode((String)brandManager.getSupportedLanguages().get(0)).getLanguageName()%>
        </td>
    <% } else { %>
	    <td align="left" valign="top" class="tableContent" nowrap>
	&nbsp;&nbsp;<select name="language" onchange="changeLanguage(this);">
    <%=brandManager.getSupportedLanguageOptionList()%>
                </select>
    	</td>
    <% } %>
        <td>&nbsp;&nbsp;</td>
        </tr>
        </table>
    </td>
</tr>

</table>


<input type="hidden" name="theAction" value="submit">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
<%-- login/more info --%>
	<tr valign="top">
		<td>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr class="channelHeader">
					<%-- Each <td> must be on one line to avoid presentation irregularities --%>
					<td class="channelHeader" width="1%" style="background-color: #336699;"><img src="/images/channelbar-left_end.gif" width=8 height=15 border=0 alt=""></td>
					<td class="channelHeader" colspan=2 nowrap width="98%" style="background-color: #336699;"><display:get name="prm.global.login.channel.login.title" /></td>
					<td align=right class="channelHeader" width="1%" style="background-color: #336699;"><img src="/images/channelbar-right_end.gif" width=8 height=15 border=0 alt=""></td>
				</tr>
				<tr><td colspan=4><img src="/images/trans.gif" width=1 height=8 alt="" border="0"></td></tr>
				<tr>
					<td class="tableContent">&nbsp;</td>
					<td class="tableContent" align="left" nowrap width="10%">
						<display:get name="prm.global.login.loginname.label" />
					</td>
					<td class="tableContent" align="left" nowrap>
						&nbsp;&nbsp;<input type="Text" name="J_USERNAME" size="20" maxlength="32">
					</td>
				</tr>
				<tr><td colspan=4><img src="/images/trans.gif" width=1 height=4 alt="" border="0"></td></tr>
				<tr>
					<td class="tableContent">&nbsp;</td>
					<td class="tableContent" align="left" nowrap>
						<display:get name="prm.global.login.password.label" />
					</td>
					<td class="tableContent" align="left" nowrap>
					 	&nbsp;&nbsp;<input type="Password" name="J_PASSWORD" size="20" maxlength="20"  onKeyUp="checkChar(event)" MAXLENGTH="60">
					</td>
				</tr>

                <tr><td colspan=4><img src="/images/trans.gif" width=1 height=4 alt="" border="0"></td></tr>
				<tr>
					<td class="tableContent">&nbsp;</td>
					<td class="tableContent" align="left" nowrap>
						<display:get name="prm.global.login.domain.label" />
					</td>
                    <%
                        DomainOptionList dol = new DomainOptionList();
                        dol.setConfigurationID(PropertyProvider.getActiveConfigurationID());
                        dol.load();

                        if (dol.size() == 1) {
                    %>
                    <td class="tableContent" align="left" valign="center" nowrap height="22">
                        <input type="hidden" name="userDomain" value="<%=((DomainOption)dol.get(0)).getID()%>">
                        &nbsp;&nbsp;<%=((DomainOption)dol.get(0)).getName()%>
                    </td>
                    <%  } else {

						%>
							<td class="tableContent" align="left" nowrap>
					    	&nbsp;&nbsp;<select name="userDomain">
								<%= dol.getDomainOptionList(userDomain) %>
							</select>
					 </td>
					<%
					} %>
				</tr>
                <tr><td colspan=4><img src="/images/trans.gif" width=1 height=4 alt="" border="0"></td></tr>
                <display:if name="@prm.global.login.allowSecureModeSwitching">
                <tr>
                    <td class="tableContent">&nbsp;</td>
                    <td class="tableContent"><display:get name="prm.global.login.mode.label" /></td>
                    <%
                        //Construct the links to secure and standard login modes.
                        String standardLink;
                        String secureLink;

                        if (request.isSecure()) {
                            secureLink = "Secure (SSL)";
                            standardLink = "<a href=\"http://" + HttpUtils.getRequestURL(request).toString().substring(8) + "\">"+ PropertyProvider.get("prm.project.main.standard.label") +"</a>";
                        } else {
                            secureLink = "<a href=\"https://" + HttpUtils.getRequestURL(request).toString().substring(7) + "\">"+ PropertyProvider.get("prm.project.main.secure.ssl.label") +"</a>";
                            standardLink = "Standard";
                        }
                    %>
                    <td class="tableContent">&nbsp;&nbsp;<%=standardLink%> | <%=secureLink%></td>
                </tr>
				<tr><td colspan=4><img src="/images/trans.gif" width="1" height="8" alt="" border="0"></td></tr>
                </display:if>
				<tr>
					<td class="tableContent" colspan=2>&nbsp;</td>
					<td class="tableContent" colspan=2 align="left" nowrap>
						&nbsp;&nbsp;
                        <input type="submit" value='<display:get name="prm.global.login.submit.button.label" />'  onclick="return doAction('login');">
                        <% if (request.isSecure()) { %>
                        <img src="/images/icons/lock.gif" width="15" height="17" border="0"/>
                        <% } %>
					</td>
				</tr>
<%-- error message --%>
<% if ((loginError = request.getAttribute("SaSecurityError")) != null)  { %>
				<tr>
					<td colspan=4 align="left">
						<div class="loginError"><%= loginError %></div>
					</td>
				</tr>
<% } %>
<%-- spacer --%>
				<tr>
					<td colspan=4><img src="/images/trans.gif" width=1 height=8 alt="" border="0"></td>
				</tr>
				<tr>
					<td><img src="/images/trans.gif" width=1 height=1 alt="" border="0"></td>
					<td class="channelHeader" colspan=3 style="background-color: #336699;"><img src="/images/trans.gif" width=1 height=2 alt="" border="0"></td>
					<td><img src="/images/trans.gif" width=1 height=1 alt="" border="0"></td>
				</tr>
			</table>
		</td>

<%-------------------------------------------------------------------------------------------------------------------------------------------
	-- Support Links
					<td nowrap class="channelHeader" width="98%"><%=brandManager.lookup("prm.login.header.customer-support")%></td>
	---------------------------------------------------------------------------------------------------------------------------------------%>
		<td><img src="/images/trans.gif" width=15 height=1 alt="" border="0"></td>
		<td>
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr class="channelHeader">
					<td class="channelHeader" width="1%"><img src="/images/channelbar-left_end.gif" width=8 height=15 border=0 hspace=0 vspace=0 alt=""></td>

					<td nowrap class="channelHeader" width="98%"><display:get name="prm.login.header.customer-support" /></td>
					<td align=right class="channelHeader" width="1%"><img src="/images/channelbar-right_end.gif" width=8 height=15 border=0 hspace=0 vspace=0 alt=""></td>
				</tr>

				<%-- spacer --%>
				<tr><td colspan=3><img src="/images/trans.gif" width=1 height=8 alt="" border="0"></td></tr>

				<tr>
					<td class="tableContent">&nbsp;</td>
					<td class="tableContent"><img src="/images/trans.gif" width=1 height=120 alt="" border=0 align="right">
                            <display:if name="prm.global.login.requestinformation.isenabled">
                                <b>&#0149;</b><display:get name="prm.global.login.requestinformation.text"/>
                            <br><br>
                            </display:if>
							<display:if name="prm.global.login.register.isenabled">
								<b>&#0149;</b><display:get name="prm.global.login.registration.1.text" /><a href="<%=SessionManager.getJSPRootURL()%>/registration/Register.jsp"><display:get name="prm.global.login.registration.2.link" /></a><display:get name="prm.global.login.registration.3.text" />
							<br><br>
							</display:if>
							<b>&#0149;</b><display:get name="prm.global.login.verification.1.text" /><a href="<%=SessionManager.getJSPRootURL()%>/registration/VerifyRegistration.jsp"><display:get name="prm.global.login.verification.2.link" /></a><display:get name="prm.global.login.verification.3.text" />
							<br><br>
							<b>&#0149;</b><display:get name="prm.global.login.forgotten.1.text" /><a href="<%=SessionManager.getJSPRootURL()%>/registration/ForgottenInfoWizard.jsp"><display:get name="prm.global.login.forgotten.2.link" /></a><display:get name="prm.global.login.forgotten.3.text" />
							<br><br>
							<b>&#0149;</b><display:get name="prm.global.login.helpdesk.1.text" /><a href="<%=SessionManager.getJSPRootURL()%>/help/HelpDesk.jsp"><display:get name="prm.global.login.helpdesk.2.link" /></a><display:get name="prm.global.login.helpdesk.3.text" />
					</td>
					<td class="tableContent">&nbsp;</td>
				</tr>

				<%-- spacer --%>
				<tr><td colspan=3><img src="/images/trans.gif" width=1 height=8 alt="" border="0"></td></tr>

				<tr>
					<td><img src="/images/trans.gif" width=1 height=1 alt="" border="0"></td>
					<td class="channelHeader"><img src="/images/trans.gif" width=1 height=2 alt="" border="0"></td>
					<td><img src="/images/trans.gif" width=1 height=1 alt="" border="0"></td>
				</tr>
			</table>
			</td>
	</tr>
</table>
</form>

<%@ include file="/help/include_outside/app_server.jsp" %>
</template:put>
<%-- End Content --%>
</template:insert>
<template:getSpaceJS space="personal"/>
<template:getSpaceJS space="personal"/>

