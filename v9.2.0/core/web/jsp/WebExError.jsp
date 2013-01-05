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
    info="Webex Error Page" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.*, net.project.base.property.PropertyProvider, net.project.resource.*, java.util.Date,
            net.project.util.ErrorLogger" 
%>

<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 

<%
// validate the security check 
int module = securityProvider.getCheckedModuleID();
String id = securityProvider.getCheckedObjectID();
int action = securityProvider.getCheckedActionID();

//What was the Webex Command that Failed
String CM = request.getParameter("CM");
String attempted_command = "";
String reason = request.getParameter("RS");

if (CM.equals("HM"))
        attempted_command = "Host";
if (CM.equals("SM"))
	attempted_command = "Schedule";
if (CM.equals("JM"))
        attempted_command = "Join";  
%>

<html>
<head>
<title>Webex Error Page!</title>
<link href="<%=SessionManager.getJSPRootURL()%>/styles/global.css" rel="stylesheet" rev="stylesheet" type="text/css">
<script language="javascript" src="<%=SessionManager.getJSPRootURL()%>/src/util.js" type="text/javascript"></script>
<script language="javascript" src="<%=SessionManager.getJSPRootURL()%>/src/window_functions.js" type="text/javascript"></script>

<script language="javascript">
	var t_standard;
	var theForm;
	var page = false;
    var isLoaded = false;
	var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';  

function setup() 
   {
	page=true;
	theForm = self.document.forms[0];
	isLoaded = true;

   }
</script>

</head>
<body class="main" onLoad="setup();">
<form method="post">
<input type="hidden" name="theAction">
</form>



<div align="center">
<table border=0 cellpadding=0 cellspacing=0 width=80%>
	<tr>
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-left.gif" width=41 height=41 alt="" border="0"></td>
		<td class="errorBanner" width=98%>Error</td>
		<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-top-right.gif" width=41 height=41 alt="" border="0"></td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td class="channelContent">
        
          <B> You have encountered a WebEx meeting error!</B><p>

            Your attempt to<B> "<% out.println(attempted_command); %>" </B> a WebEx meeting failed. 
            Please return to the Calendar page and attempt to <B> "<% out.println(attempted_command); %>" </B> your meeting again.
            Should problems persist,  please contact support. <p><p>
        
           <%
            if (reason.equals("MeetingNotInProgress"))
               {
               out.println("<B> It appears your meeting is \"Not In  Progress\".<P>"+
                   "<Font color=#CC0033>Please contact the host to see if the meeting has been started<br>" +
                   "and that the meeting time has not elapsed!</Font></B><p><p>");
               }

           %>
                
		<p>
		<%=PropertyProvider.get("prm.project.main.error.recorder.for.analysis.label") %>
		<br>
		<%=PropertyProvider.get("prm.project.main.return.page.label") %>
		
		</td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-left.gif" width=41 height=41 alt="" border="0"></td>
        
          <%
            String fromStartPage= request.getParameter("sp");
           if (fromStartPage != null){
          %>
        <td class="errorBanner" width=98% align=right><a href="javascript:window.close();" class="errorLink"><%=PropertyProvider.get("prm.project.main.return.label") %></a></td>
		<td><a href="javascript:window.close();" class="errorLink"><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-right.gif" width=41 height=41 alt="" border="0"></a></td> 
         <%
       } else 
       {
         %>
		<td class="errorBanner" width=98% align=right><a href="javascript:history.back();" class="errorLink"><%=PropertyProvider.get("prm.project.main.return.label") %></a></td>
		<td><a href="javascript:history.back();" class="errorLink"><img src="<%=SessionManager.getJSPRootURL()%>/images/error/error-bottom-right.gif" width=41 height=41 alt="" border="0"></a></td>
	   <%
       }
       %>
    </tr>
</table>
</body>
</html>
