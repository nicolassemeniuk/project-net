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
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.xml.document.XMLDocument,
            java.util.Date,
            java.net.URLEncoder,
            net.project.security.SessionManager,
            net.project.notification.Email,
            net.project.notification.email.IEmailAttachment,
            javax.activation.DataSource,
            net.project.notification.email.StringAttachment,
            net.project.base.property.PropertyProvider"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%
    XMLDocument doc = new XMLDocument();
    doc.startElement("ErrorReport");
    doc.addElement("ErrorDescription", request.getParameter("errorDescription"));
    doc.addElement("ReproductionSteps", request.getParameter("reproductionSteps"));
    doc.addElement("UserName", request.getParameter("userName"));
    doc.addElement("CurrentDate", request.getParameter("currentSpace"));
    doc.addElement("UserDisplayName", request.getParameter("userDisplayname"));
    doc.addElement("UserID", request.getParameter("userID"));
    doc.addElement("BrowserInfo", request.getParameter("browserInfo"));
    doc.addElement("ServerName", request.getParameter("serverName"));
    doc.addElement("CurrentSpaceID", request.getParameter("currentSpaceID"));
    doc.addElement("CurrentSpaceName", request.getParameter("currentSpaceName"));
    doc.addElement("Email", request.getParameter("email"));
    doc.addElement("StackTrace", request.getParameter("stackTrace"));
    doc.addElement("History", request.getParameter("history"));
    doc.endElement();
    doc.setPrettyFormat(true);


    StringAttachment attachment = new StringAttachment(doc.getXMLString(), "error.xml", "text/xml");
    String errorLocation = PropertyProvider.get("prm.project.main.location.label") + request.getParameter("serverName") + "\n";
    String errorDescription = PropertyProvider.get("prm.project.main.description.label") + (request.getParameter("errorDescription") != null ? request.getParameter("errorDescription") : "No description supplied") + "\n";
    String errorUser = PropertyProvider.get("prm.project.main.user.label") + request.getParameter("userName") + " (" + request.getParameter("email") + ")\n";
    String reproSteps = PropertyProvider.get("prm.project.main.reproduction.label") + request.getParameter("reproductionSteps") + "\n\n";
    String stackTrace = "\n" + request.getParameter("stackTrace");

    String message = errorLocation + errorDescription + errorUser + reproSteps + stackTrace;

    String destinationEmail;
    try {
        destinationEmail = PropertyProvider.get("prm.base.errors.reportErrors.email");
    } catch (Exception e) {
        destinationEmail = "support@project.net";
    }

    Email email = new Email();
    email.setTo(destinationEmail);
    email.setFrom(user.getEmail());
    email.setSubject("Automated Error Report");
    email.setMessageBody(message);
    email.attach(attachment);
    email.send();
%>
<script language="javascript" type="text/javascript">
    history.go(-3);
</script>