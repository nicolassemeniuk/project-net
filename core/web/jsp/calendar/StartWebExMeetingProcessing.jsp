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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|
|   Processing page for starting (hosting or joining) a webex meeting
|   
|   
|   Author: Adam Klatzkin    
|--------------------------------------------------------------------%>

<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Process a start webex meeting request" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider, 
	net.project.calendar.MeetingBean, 
    net.project.security.SecurityProvider,
    net.project.security.User,
    net.project.resource.WebExFacility,
    net.project.security.SessionManager, 
    java.util.Date,
    org.apache.log4j.Logger" 
    
%>

<jsp:useBean id="meeting" class="net.project.calendar.MeetingBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" /> 

<%!
   private static final String SECURE_SITE = "project.webex.com";
%>

<%
// validate the security check 
int module = securityProvider.getCheckedModuleID();
String id = securityProvider.getCheckedObjectID();
int action = securityProvider.getCheckedActionID();

if ((!id.equals(meeting.getID())) || 
    (action != net.project.security.Action.VIEW) ||
    (module != net.project.base.Module.CALENDAR))
    throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.calendar.security.validationfailed.message")
);
%>
           
<%
// get the meetings webex facility object
WebExFacility webex = (WebExFacility)meeting.getFacility();

String webex_site = SessionManager.getWebexSite();

// The CM parameter will tell us whether we are starting or joining the meeting
String requestCommand = request.getParameter("CM");

// Handle the errors that come back from the webex site (hosting or joining a meeting).

String webex_status = request.getParameter("ST");

// URL for webex errors
String webex_error_url=SessionManager.getAppURL()+"/WebExError.jsp?module=" +module+"&id="+id+"&sp=t";

if (webex_status != null){
    if (!webex_status.equals("SUCCESS")){
	 String webex_reason = request.getParameter("RS");
	 if (webex_reason.equals("AlreadyLogon")){
	 }
	 else{
	 // we need to log this error
	 Logger.getLogger(this.getClass()).error("Webex Action Failed: \""+requestCommand+"\":" +webex_reason);
	 
	 // send to the WebExError page
	 response.sendRedirect(webex_error_url +"&"+request.getQueryString());
	 } // end else *
    }
}

String secure = request.getParameter("secure");

// protocol to use: either "http" or https". set by the host.
String protocol = "https";

 // is this a secure meeting or not ?

                if (secure != null){
                  if (secure.equals("true") )
                    {
                        webex_site = SECURE_SITE;                                            
                    }
                }


// Communication with the webex server is performed by a state machine.
// Get the current state from the request parameter
String state = request.getParameter("state");
int iState = 0;
if (state != null) 
    {
    iState = Integer.parseInt(state);
    }


String href=null;
switch (iState) 
    {
    case 0:
        // redirect the client to login
        href = protocol+"://" + webex_site + "/webex/s.asp?AT=PL&PID=00"
                        + "&FN=" + java.net.URLEncoder.encode(user.getFirstName())
                        + "&LN=" + java.net.URLEncoder.encode(user.getLastName())
                        + "&WID=" + java.net.URLEncoder.encode(user.getLogin())
	                + "&PW=" + java.net.URLEncoder.encode("webex_" +user.getVerificationCode())
	                + "&EM=" + java.net.URLEncoder.encode("webex." +user.getID()+"@project.net")
                        + "&MU=GoBack"
                        + "&BU="
                        + java.net.URLEncoder.encode(SessionManager.getAppURL() + "/calendar/StartWebExMeetingProcessing.jsp?state=" + (iState+1) + "&" +request.getQueryString());
        Logger.getLogger(this.getClass()).debug("Webex Login Command: \n" + href);
        response.sendRedirect(href);  
        break;
    case 1:
        if (requestCommand != null) 
            {
            if (requestCommand.equals("HM")) 
            {
            
               

                
                // Host the webex meeting
                href = protocol+"://" + webex_site + "/webex/m.asp?AT=HM"
                      +"&MK="+webex.getRoomNumber()
                      +"&PW="+webex.getPassword()
                      +"&BU="
                      +java.net.URLEncoder.encode(SessionManager.getAppURL() 
                      +"/calendar/StartWebExMeetingProcessing.jsp?state="+ (iState+1)
                      + "&module=" +module +"&id=" +id + "&CM=HM");
                Logger.getLogger(this.getClass()).debug("Host Meeting Command: \n" + href);
                response.sendRedirect(href);
                }
            else if (requestCommand.equals("JM")) 
                {
                // check to see if the meeting is secure or not
                webex.load();
                //System.out.println("Is the meeting secure: " + (webex.isSecure() ? "yes":"no"));
                if (webex.isSecure())
                {
                    protocol="https";
                }
                // Joing the webex meeting
                href = protocol+"://" + webex_site + "/webex/m.asp?AT=JM"
                     +"&MK="+webex.getRoomNumber()
                     +"&PW="+webex.getPassword()
                     +"&BU="
                     +java.net.URLEncoder.encode(SessionManager.getAppURL()
                     +"/calendar/StartWebExMeetingProcessing.jsp?state="+ (iState+1) 
                     + "&module=" +module +"&id=" +id + "&CM=JM");
                Logger.getLogger(this.getClass()).debug("Join Meeting Command: \n" + href);
                response.sendRedirect(href);
                }
            }
        break;
    case 2:
        // the meeting has ended, close the window

%>
<SCRIPT LANGUAGE="JAVASCRIPT">
window.close();
</SCRIPT>
<%
        break;
    }
%>
