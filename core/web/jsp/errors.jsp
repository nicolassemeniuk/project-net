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

<%@
	page info="Generic Error Processing Page"
    contentType="text/html; charset=UTF-8"
    isErrorPage="true"
    import="net.project.security.SessionManager,
		    net.project.base.PnetException,
			net.project.base.SessionNotInitializedException,
			net.project.base.property.PropertyProvider,
            org.apache.log4j.Logger,
            net.project.security.AuthenticationRequiredException,
            net.project.base.directory.AuthenticationFailedException,
            net.project.security.AuthorizationFailedException"
%>
<%!
	String getStackTrace(Throwable t) {
		java.io.StringWriter writer = new java.io.StringWriter();
		t.printStackTrace(new java.io.PrintWriter(writer));
		return writer.toString();
	}
%>
<%
    // Lookup the translated text on this page
    String title;
    String message;
    String genericErrorMessage;
    String returnButtonLabel;
    String reportErrorLabel;
    String stackTrace;

    try {
        title = PropertyProvider.get("prm.base.errors.pagetitle");
        message = PropertyProvider.get("prm.base.errors.message");
        genericErrorMessage = PropertyProvider.get("prm.base.errors.generic.message");
        returnButtonLabel = PropertyProvider.get("prm.base.errors.button.return.label");
        reportErrorLabel = PropertyProvider.get("prm.base.errors.reporterror.label");
        stackTrace = PropertyProvider.get("prm.base.errors.stacktrace.label");
    } catch (Exception e) {
        // An error in the error page?  We ignore it, fill in the text with
        // hardcoded values; the original error is displayed
        title = "Error";
        message = "An error was encountered and has been logged:";
        genericErrorMessage = "Please click REPORT ERROR to report this error to our support team.<br>Please press the return button to go back to the previous page.";
        returnButtonLabel = "Return";
        reportErrorLabel = "Report Error";
        stackTrace = "Stack Trace";
    }

%>
<html>
<head>
<link href="<%=SessionManager.getJSPRootURL()%>/styles/global.css" rel="stylesheet" rev="stylesheet" type="text/css">
<link href="<%=SessionManager.getJSPRootURL()%>/styles/fonts.css" rel="stylesheet" rev="stylesheet" type="text/css">
<link href="<%=SessionManager.getJSPRootURL()%>/styles/personal.css" rel="stylesheet" rev="stylesheet" type="text/css">
<style type="text/css">
    #hidden { position: static; visibility: hidden; }
    #shown { position: static; visibility: visible; }
    #footer { position: relative; }
</style>
<script language="javascript" src="<%=SessionManager.getJSPRootURL()%>/src/util.js"></script>
<script language="javascript" src="<%=SessionManager.getJSPRootURL()%>/src/window_functions.js"></script>
<script language="javascript" src="<%=SessionManager.getJSPRootURL()%>/src/dhtml/findDOM.js"></script>
<script language="javascript" src="<%=SessionManager.getJSPRootURL()%>/src/dhtml/visibility.js"></script>
<script language="javascript">
	var t_standard;
	var theForm;
	var page = false;
    var isLoaded = false;
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';  

function setup() {
	page=true;
	theForm = self.document.forms[0];
	isLoaded = true;
    setVisibility('normalNavigation', 'visible');
    setVisibility('stackTrace', 'hidden');
    setVisibility('exceptionNavigation', 'hidden');
    document.body.style.cursor = "default";
}

function toggleStackTrace() {
    toggleVisibility('normalNavigation');
    toggleVisibility('stackTrace');
    toggleVisibility('exceptionNavigation');
}

function help() {
	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp";
	openwin_help(helplocation);
}
</script>
</head>
<body class="main" onLoad="setup();">
<form method="post">
<input type="hidden" name="theAction">
</form>
<%
    // Return URL is set to refLink if available in request 
    // else it will return to back page in history
	String returnURL = null;
    if(request.getAttribute("refLink") != null) {
    	returnURL = (String) request.getAttribute("refLink");
    } else {
    	returnURL = "javascript:history.back();";
    }
	if (exception instanceof SessionNotInitializedException) {
		// In this case we need to forward to the initialization page after
		// saving the requested page.
		// Note: We don't care about the request parameters because the Initialize
		// page will perform a "forward" to the requested page, thus preserving
		// the request.  See Intialize.jsp for the reasons for requiring a "forward".
		String requestPage = request.getRequestURI();
		session.putValue("requestedPage", requestPage);
		pageContext.forward("Initialize.jsp");
		return;
	}

	// AuthenticationRequiredException.  User not logged in, or session timeout
	else if (exception instanceof AuthenticationRequiredException) {
		String requestPage = null;
		String queryString = null;
		// save the page the user wanted so we can take them there after login / initialization
		if (request.getAttribute("javax.servlet.forward.request_uri") != null)
			requestPage = (String) request.getAttribute("javax.servlet.forward.request_uri");
		if (request.getAttribute("javax.servlet.forward.query_string") != null)
			queryString = (String) request.getAttribute("javax.servlet.forward.query_string");
		if (queryString != null && requestPage != null) {
			requestPage += "?" + queryString;
		}
		// Save requested page for redirect after login
		if (requestPage != null)
			request.setAttribute("requestedPage", java.net.URLEncoder.encode(requestPage, SessionManager.getCharacterEncoding()));
		request.setAttribute("SaSecurityError", "Please Login");
		// AuthenticationRequiredException forwarding to Login.jsp
        pageContext.forward("Login.jsp");
		return;
    }

	// AuthenticationFailedException.  login credentials not valid
    else if (exception instanceof AuthenticationFailedException) {
		request.setAttribute("SaSecurityError", exception.getMessage());
        pageContext.forward("Login.jsp");
		return;
    }

	// AuthorizationFailedException. User was denied access to an object/action.
    else if (exception instanceof AuthorizationFailedException) {
        pageContext.forward("AccessDenied.jsp");
		return;
    }

	//  Log the Exception
	String errorMessage = exception.getMessage();
    if (exception instanceof PnetException) {
        PnetException pnetException = (PnetException) exception;
		if (pnetException.getUserMessage() != null) {
			Logger.getLogger(this.getClass()).debug(exception.getClass().getName() + ": " + pnetException.getUserMessage());
		}
    }
	
	// Default error message for the user.
    if (errorMessage==null) {
		errorMessage="Unable to continue with current process";
	}

    //Put the exception object in the session in case the user decides to report it
    session.setAttribute("reportedException", exception);
%>

<center>
<table border=0 cellpadding=0 cellspacing=0 width=80%>
	<tr>
		<td width=1%><a href="javascript:toggleStackTrace();"><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-left.gif" width=41 height=41 alt="" border="0"></a></td>
		<td class="errorBanner" width=98%><%=title%></td>
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-right.gif" width=41 height=41 alt="" border="0"></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td class="channelContent">
			<%=message%>
			<p>
				<b><%= errorMessage %></b>
		    </p>
			
		<%	if (exception instanceof PnetException && ((PnetException)exception).getUserMessage() == null) { %>		    
			<p>
			<%=genericErrorMessage%>
			</p>	
		<%	} %>
		</td>
		<td>&nbsp;</td>
	</tr>
</table>
<table id="normalNavigation" class="shown" border=0 cellpadding=0 cellspacing=0 width=80%>
	<tr>
        <td><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-left.gif" width=41 height=41 alt="" border="0"></td>
        <td class="errorBanner" width="98%" align=right><a href="<%=SessionManager.getJSPRootURL()%>/ReportError.jsp?module=150" class="errorLink"><%=reportErrorLabel%></a></td>
        <td class="errorBanner" align=right><a href="<%=SessionManager.getJSPRootURL()%>/ReportError.jsp?module=150" class="errorLink"><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-right-2.gif" width="41" height="41" alt="" border="0"></a></td>
        <td class="errorBanner" width="40">&nbsp;</td>
        <td class="errorBanner" align=right><a href="javascript:history.back();" class="errorLink"><%=returnButtonLabel%></a></td>
        <td><a href="<%=returnURL%>" class="errorLink"><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-right.gif" width=41 height=41 alt="" border="0"></a></td>
	</tr>
</table>
<table id="stackTrace" class="hidden" border=0 cellpadding="4" cellspacing="0">
    <tr><td>
        <div class="channelContentLarge"><%=stackTrace%><BR></div>
        <pre><%=getStackTrace(exception)%></pre>
    </td></tr>
</table>
<table id="exceptionNavigation" class="hidden" border=0 cellpadding=0 cellspacing=0 width="80%">
	<tr>
        <td><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-left.gif" width=41 height=41 alt="" border="0"></td>
        <td class="errorBanner" width="98%" align=right><a href="<%=SessionManager.getJSPRootURL()%>/ReportError.jsp?module=150" class="errorLink"><%=reportErrorLabel%></a></td>
        <td class="errorBanner" align=right><a href="<%=SessionManager.getJSPRootURL()%>/ReportError.jsp?module=150" class="errorLink"><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-right-2.gif" width="41" height="41" alt="" border="0"></a></td>
        <td class="errorBanner" width="40">&nbsp;</td>
        <td class="errorBanner" align=right><a href="javascript:history.back();" class="errorLink"><%=returnButtonLabel%></a></td>
        <td><a href="<%=returnURL%>" class="errorLink"><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-right.gif" width=41 height=41 alt="" border="0"></a></td>
	</tr>
</table>
</center>
<!--
The stack trace is:
<%=getStackTrace(exception)%>
-->   
</body>
</html>                                                                                    
