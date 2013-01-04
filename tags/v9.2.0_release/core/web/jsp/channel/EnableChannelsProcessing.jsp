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
       info="Processing page for enabling channels"
       language="java" 
       errorPage="/errors.jsp"
       import="net.project.channel.ChannelManager,
       net.project.channel.Channel,
       net.project.security.SessionManager,
               net.project.channel.ChannelCustomizer"
%>
<jsp:useBean id="PR_user_properties" class="net.project.resource.PersonProperty" scope="session" />
<jsp:useBean id="channelCustomizer" class="net.project.channel.ChannelCustomizer" scope="page" />
<%
    String referer = request.getParameter("referer");
    String decodedReferer = java.net.URLDecoder.decode(referer);

    // Process the submitted values to update and store the properties
    channelCustomizer.init(request, PR_user_properties);
    channelCustomizer.process(request);

    if (referer != null) {
        response.sendRedirect(decodedReferer);
    } else {
       pageContext.forward("/channel/CustomizeChannels.jsp");
    }
%>


