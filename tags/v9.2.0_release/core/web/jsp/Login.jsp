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
<%
    SessionManager.initializeFromRequest(request);

	Object loginError = null;

	String[] userDomain = request.getParameterValues("userDomain");
	String userName = request.getParameter("login");
    String verified = (String) session.getAttribute("verified");
    session.removeAttribute("verified");

//-------------------------------------------------------------------------------------------------
//-- Load Property Provider Configuration
//-------------------------------------------------------------------------------------------------

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

	String requestedPage = (String) request.getAttribute("requestedPage");
%>

<%

/*check if custom jsp or html login screen is enabled*/
String custom = PropertyProvider.get("prm.custom.loginscreen.content");

String INCLUDE_PATH_CUSTOM_HTML = "/custom/login.html";
String INCLUDE_PATH_CUSTOM_JSP = "/custom/login.jsp";

if ((custom != null)&&(custom.equalsIgnoreCase("jsp"))) {
	//custom jsp
	pageContext.include(INCLUDE_PATH_CUSTOM_JSP);
	return;
} else if ((custom != null)&&(custom.equalsIgnoreCase("html"))) {
	//custom html
	pageContext.include(INCLUDE_PATH_CUSTOM_HTML);
	return;
} 

	/*
	* verify if extenal SSO authentication is on
	*/
	boolean ssoEnabled = PropertyProvider.getBoolean("prm.global.login.sso.allowSSO");
	if (ssoEnabled) {
			response.sendRedirect(SessionManager.getJSPRootURL()+"/sso/SSOLogin.jsp?requestedPage="+requestedPage);
			return;
	}
%>

<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="brandManager" class="net.project.brand.BrandManager" scope="session" />



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



<template:insert template="template/PreAuthentication.jsp">
	<template:put name="title" content='<%=PropertyProvider.get ("prm.global.login.pagetitle")%>' direct="true" />

<%-- Additional HEAD stuff --%>
<template:put name="head">

<%
// login page must be requested from server everytime, othwise brand will not be set.
response.setHeader("Cache-Control","no-cache"); 	//HTTP 1.1
response.setHeader("Pragma","no-cache"); 			//HTTP 1.0
response.setDateHeader ("Expires", -1);				//Keep IE5 happy?
%>
<template:getSpaceCSS space="personal"/>

<template:import type="javascript" src="/src/com_functions.js" />
<template:import type="javascript" src="/src/cookie.js" />

<template:import type="javascript" src="/src/browser.js" />

<%-- ExtJS Components--%>




<script language="javascript">


<%-- Do a cookie check, if javascript is turned off the user will be notifed --%>
<%--
SetCookie("testcookie","Cookies On!",null,"/");
if(GetCookie("testcookie")==null)
	top.location.href = "CookieRequired.jsp"
--%>

detectBrowser("<%=SessionManager.getJSPRootURL()%>/BadBrowser.jsp");

function newUser() {
<%  if (verified != null) { %>
		extAlert("", '<%=verified%>' , Ext.MessageBox.INFO);
<%  } %>
}

<%--
    Save username and domain to browser cookie.
    Note: It is critical to user "getSelectedValue" to handle case
    when domain may be a dropdown list or a hidden field
--%>
function doAction(action) {
    setVar("pnet_login", document.forms[0].J_USERNAME.value, 365);
    setVar("prm.login.userDomain", getSelectedValue(document.forms[0].userDomain), 365);
    setVar("prm.login.userLanguage", getSelectedValue(document.forms[0].language), 365);

    if(self.document.Login && processForm(self.document.Login)) {
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
  	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/HelpDesk.jsp";
 	openwin_help(helplocation);
}


function openPopUp(URL)
{
   my_new_window = window.open(URL, "tool_popup", "height=500,width=600, resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=1,status=0,toolbar=0");
   my_new_window.focus();
 }

function changeLanguage(dropDown) {
	setVar("prm.login.userBrand", dropDown.value, 365);
	self.document.location = "<%=SessionManager.getJSPRootURL()%>/Login.jsp?language=" + dropDown.value + "&brand="+ dropDown.value;
}
</script>

<style type="text/css">
<!--
body {
	margin-left: 0px;
	margin-top: 0px;
}


.style1 {
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 12px;
	border-bottom-color: #FFFFFF;
	margin-bottom: 0px;
	color: #3C5F84;
	height: 0px;
	border-top-width: 0px;
	border-right-width: 0px;
	border-bottom-width: 1px;
	border-left-width: 0px;
	border-top-style: none;
	border-right-style: none;
	border-bottom-style: solid;
	border-left-style: none;
	}
.style2 {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 12px;
	font-weight: bold;
	color: #3c5f84;
}
.style3 {
	color: #FFFFFF;
	font-weight: bold;
	font-family: Arial, Helvetica, sans-serif;
	font-size: 12px;
}
.style4 {font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 12px; color: #000000; }


.style5 {
	font-size: 11px;
	font-family: Verdana, Arial, Helvetica, sans-serif;
}

.style6 {
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 12px;
	color: #3C5F84;
	}
.style7 {
	font-family: Arial, Helvetica, sans-serif;
	font-size: 22px;
	border-bottom-color: #FFFFFF;
	margin-bottom: 0px;
	color: #FF0033;
	height: 40px;
	width:220px;
	border-top-width: 0px;
	border-right-width: 0px;
	border-bottom-width: 1px;
   }	
 .style8 {  
 	font-family: Arial, Helvetica, sans-serif;
	font-size: 26px;
	color: #CC0000;
 }
-->
</style>
</template:put>
<%-- End of HEAD --%>

<%-- Begin Content --%>
<template:put name="bodyOnLoad" content="newUser();load_login();" direct="true" />
<template:put name="content">
<form name="Login" action="<%= SessionManager.getJSPRootURL() %>/LoginProcessing.jsp<%= requestedPage == null ? "" : ("?requestedPage=" + requestedPage) %>" onSubmit="return doAction('login');" method="POST">
<table width="99%" height="400" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td width="66%" height="90" align="left" valign="top"><a href="http://www.project.net"><display:img src="@prm.project.login.pnet_logo2" width="165" height="90"/></a>
    <a href="http://www.project.net"><display:img src="@prm.project.login.pnet_home_button" width="113" height="17" /></a></td>
    <td width="34%" height="90" align="right" valign="bottom"><span class="style1"><span class="style4"><display:get name="prm.global.login.language.label" />
	<% if (brandManager.getSupportedLanguages().size() == 1) { %>
        <input type="hidden" name="language" value="<%=((String)brandManager.getSupportedLanguages().get(0))%>">
<%=Language.getLanguages().getLanguageForCode((String)brandManager.getSupportedLanguages().get(0)).getLanguageName()%>
    <% } else { %>
		<select name="language" onchange="changeLanguage(this);">
    		<%=brandManager.getSupportedLanguageOptionList()%>
    	</select>
    <% } %>
	</span>
    </span><display:img src="@prm.project.login.top_img_product" width="326" height="51" border="0" /></td>
  </tr>
  <tr>
    <td height="20" colspan="2" align="left" valign="top" bgcolor="666666"></td>
  </tr>
  <tr>
    <td colspan="2" valign="top">
      <table width="900" height="390" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="173" rowspan="4" valign="top"><display:img src="@prm.project.login.bluBG" width="171" height="390"/></td>
          <td height="49" colspan="2" valign="top"><span class="style7"><span class="style8"><display:get name="prm.project.login.login_header_text-1" /></td>
          <td width="17"></td>
          <td width="281" valign="top" align="right" ><span class="style1"><span class="style4"><display:get name="prm.global.project.version.label" />
          &nbsp; <%=net.project.util.Version.getInstance().getProductVersionCodename()%></td>
        </tr>
        
        <tr>
          <td height="283"></td>
          <td valign="top" align="right">
            <table width="393"  valign="top" height="2" border="0" cellpadding="0" cellspacing="0">
            <tr>
              <td width="71"></td>
              <td width="77"></td>
              <td width="245"></td>
            </tr>
            <tr>
              <td></td>
              <td></td>
              <td></td>
            </tr>


<%-- LOGIN CHANNEL --%>

<input type="hidden" name="theAction" value="submit">			
            <tr>
              <td height="20" colspan="3" align="center" bgcolor="#3c5f84"><span class="style3"><display:get name="prm.global.login.channel.login.title" /></span></td>
            </tr>
            
<%-- Login Name --%>           
            <tr>
              <td height="25" bgcolor="D2E2F2"></td>
              <td bgcolor="D2E2F2"></td>
              <td bgcolor="D2E2F2"></td>
            </tr>
            <tr>
              <td height="20" bgcolor="D2E2F2"></td>
              <td valign="middle" bgcolor="D2E2F2"><span class="style2"><display:get name="prm.global.login.loginname.label" /></span></td>
              <td valign="middle" bgcolor="D2E2F2"><span class="style2">
                <input type="Text" name="J_USERNAME" size="20" maxlength="32">
              </span></td>
            </tr>
            
<%-- Password --%>
            <tr>
              <td height="10" bgcolor="D2E2F2"></td>
              <td bgcolor="D2E2F2"></td>
              <td bgcolor="D2E2F2"></td>
            </tr>
            <tr>
              <td height="20" bgcolor="D2E2F2"></td>
              <td bgcolor="D2E2F2"><span class="style2"><display:get name="prm.global.login.password.label" /></span></td>
              <td bgcolor="D2E2F2"><span class="style2">
                <input type="Password" name="J_PASSWORD" size="20" onKeyUp="checkChar(event)">
              </span></td>
            </tr>

<%-- Domain --%>
            <tr>
              <td height="10" bgcolor="D2E2F2"></td>
              <td bgcolor="D2E2F2"></td>
              <td bgcolor="D2E2F2"></td>
            </tr>
            <tr>
              <td height="20" bgcolor="D2E2F2"></td>
              <td bgcolor="D2E2F2"><span class="style2"><display:get name="prm.global.login.domain.label" /></span></td>
              <td bgcolor="D2E2F2"><span class="style2">
              
			  <%
                 DomainOptionList dol = new DomainOptionList();
                 dol.setConfigurationID(PropertyProvider.getActiveConfigurationID());
                 dol.load();
                 
                 if (dol.size() == 1) {
              %>
              	<input type="hidden" name="userDomain" value="<%=((DomainOption)dol.get(0)).getID()%>">
              	<%=((DomainOption)dol.get(0)).getName()%>
              <%  
                 } else {
			  %>
				<select name="userDomain">
			  		<%= dol.getDomainOptionList(userDomain) %>
				</select>
			  <% } %>
              </span></td>
            </tr>
			
<%-- Mode --%>
			<display:if name="@prm.global.login.allowSecureModeSwitching">
			<tr>
              <td height="10" bgcolor="D2E2F2"></td>
              <td bgcolor="D2E2F2"></td>
              <td bgcolor="D2E2F2"></td>
            </tr>
            <tr>
              <td height="25" bgcolor="D2E2F2"></td>
              <td bgcolor="D2E2F2"><span class="style2"><display:get name="prm.global.login.mode.label" /></span></td>
			  <%
          		    //Construct the links to secure and standard login modes.
                    String standardLink;
                    String secureLink;

                    if (request.isSecure()) {
                        secureLink = "Secure (SSL)";
                        standardLink = "<a href=\"http://" + HttpUtils.getRequestURL(request).toString().substring(8) + "\">Standard</a>";
                    } else {
                        secureLink = "<a href=\"https://" + HttpUtils.getRequestURL(request).toString().substring(7) + "\">Secure (SSL)</a>";
                        standardLink = "Standard";
                    }
              %>
              <td bgcolor="D2E2F2"><span class="style2"><%=standardLink%> | <%=secureLink%></span></td>
            </tr>
			</display:if>
			
<%-- Login Button --%>
			<tr>
              <td height="10" bgcolor="D2E2F2"></td>
              <td bgcolor="D2E2F2"></td>
              <td bgcolor="D2E2F2"></td>
            </tr>
            
            <tr>
              <td height="10" bgcolor="D2E2F2"></td>
              <td bgcolor="D2E2F2"></td>
              <td valign="top" bgcolor="D2E2F2">
                <input type="submit" value='<display:get name="prm.global.login.submit.button.label" />'  >
			    <% if (request.isSecure()) { %>
                      <img src="/images/icons/lock.gif" width="15" height="17" border="0"/>
                <% } %>
			  </td>
            </tr>

			<tr>
              <td height="10" bgcolor="D2E2F2"></td>
              <td bgcolor="D2E2F2"></td>
              <td bgcolor="D2E2F2"></td>
            </tr>
            
<%-- Display Login-related error message --%>
<% if ((loginError = request.getAttribute("SaSecurityError")) != null)  { %>			
			<tr>
              <td height="25" bgcolor="D2E2F2" colspan="3">
			  <div class="loginError"><%= loginError %></div>
			  </td>
            </tr>
<% } %>			
          </table>
          </td>
          
          <td height="283"></td>
          <td valign="top">
            <table width="257"  valign="top" height="210" border="0" cellpadding="0" cellspacing="0">
            
          
<%-- SUPPORT CHANNEL --%>

            
            <tr>
              <td height="20" align="center" bgcolor="87B1DD"><span class="style3"><display:get name="prm.login.header.customer-support" /></span></td>
              </tr>
            	<display:if name="prm.global.login.register.isenabled">
            		<tr>
              			<td width="257" height="25" align="left" bgcolor="C8D7E3" class="style1"><ul>
               			 <li><display:get name="prm.global.login.registration.1.text" /><a href="<%=SessionManager.getJSPRootURL()%>/registration/Register.jsp"><display:get name="prm.global.login.registration.2.link" /></a><display:get name="prm.global.login.registration.3.text" />
							<display:if name="prm.global.login.requestinformation.isenabled">
                        		<display:get name="prm.global.login.requestinformation.text"/>
    	            		</display:if>
                		</li></ul>
                		</td>
            		</tr>
       			</display:if>
            <tr>
              <td height="40" align="left" bgcolor="C8D7E3" class="style1"><ul>
               <li><display:get name="prm.global.login.verification.1.text" /><a href="<%=SessionManager.getJSPRootURL()%>/registration/VerifyRegistration.jsp"><display:get name="prm.global.login.verification.2.link" /></a><display:get name="prm.global.login.verification.3.text" /></li>
              </ul></td>
            </tr>
            <tr>
              <td height="40" align="left" bgcolor="C8D7E3" class="style1"><ul>
                <li><display:get name="prm.global.login.forgotten.1.text" /><a href="<%=SessionManager.getJSPRootURL()%>/registration/ForgottenInfoWizard.jsp"><display:get name="prm.global.login.forgotten.2.link" /></a><display:get name="prm.global.login.forgotten.3.text" /></li>
              </ul></td>
            </tr>
            <tr>
              <td height="40" align="left" bgcolor="C8D7E3" class="style1"><ul>
                <li class="style6"><display:get name="prm.global.login.helpdesk.1.text" /><a href="javascript:helpMe();"><display:get name="prm.global.login.helpdesk.2.link" /></a><display:get name="prm.global.login.helpdesk.3.text" /></li>
              </ul></td>
            </tr>
          </table></td>
        </tr>
      </table></td>
  </tr>
</table>
</form>

</template:put>

<%-- End Content --%>
</template:insert>

<div class="footer">
	<%@ include file="/help/include_outside/copy_right.jsp" %>
</div>


<%
final String timeoutParameter = request.getParameter("t"); 
	if (timeoutParameter != null && timeoutParameter.equals("1")) {
		SessionManager.setSessionShortInactiveInterval(request);
	}
%>
<template:getSpaceJS space="personal"/>
