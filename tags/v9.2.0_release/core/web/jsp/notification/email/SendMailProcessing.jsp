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
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Send Mail Processing" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.security.User,
            net.project.security.Action,
            net.project.security.SessionManager,
            net.project.base.Module,
            net.project.notification.Email,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="recipients" class="net.project.notification.email.RecipientProvider" scope="session" />

<security:verifyAccess action="view"
					   module="<%=Module.PERSONAL_SPACE%>" /> 

<%
	String refLink, refLinkEncoded = null;	
	refLinkEncoded = ( (refLink = request.getParameter("refLink")) != null ? java.net.URLEncoder.encode(refLink) : "");
%>

<%-- Set the selected recipients; these may be roles or persons --%>
<jsp:setProperty name="recipients" property="selectedRecipientsArray" param="selectedRecipients" />

<%
    String subject = request.getParameter("subject");
    String body = request.getParameter("body");

    if (recipients.getSelectedRecipients().getEmailAddresses().isEmpty()) {
        // No recipients or all recipients have no email addresses
        // (might happen if recipient is empty role); display appropriate error
        request.setAttribute("errorMessage", PropertyProvider.get("prm.notification.email.send.norecipients.message"));
        pageContext.forward("/notification/email/SendMailErrors.jsp");

    } else {
        // We have some recipients; try to send it
        Email email = new Email();

        email.setTo(new java.util.ArrayList(recipients.getSelectedRecipients().getEmailAddresses()));
        email.setFrom(user.getDisplayName() + "<" + user.getEmail() + ">");
        email.setSubject(subject);
        email.setMessageBody(body);

        try {
            email.send();
            
            //Avinash:-----setting the previous module which was stored previously------
            Integer prevModule = (Integer)session.getAttribute("prevModule");
            if(prevModule!=null){
            	request.setAttribute("module",""+prevModule.intValue());
            	session.removeAttribute("prevModule");
            	net.project.security.ServletSecurityProvider.setAndCheckValues(request);
            }
            //Avinash:-----setting the previous module which was stored previously-------
            
            response.sendRedirect(SessionManager.getJSPRootURL() + refLink);

        } catch (net.project.notification.EmailException ee) {
            request.setAttribute("emailException", ee);
            pageContext.forward("/notification/email/SendMailErrors.jsp");
        }

    }
%>
    
