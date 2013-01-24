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

<%
/*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
| Displays envelope version info
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Envelope Version List" 
    language="java" 
    errorPage="../../WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.*,
			net.project.space.Space,
			net.project.workflow.*"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="envelopeManagerBean" class="net.project.workflow.EnvelopeManagerBean" scope="session" />
<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr class="channelHeader" align="left">
       	<th class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
	    <th class="channelHeader">&nbsp;<%=PropertyProvider.get("prm.workflow.envelope.include.versionlist.channel.comments.title")%></th>
		<th class="channelHeader" align="right"><%-- Insert toolbar here --%></th>
        <th class="channelHeader" align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	</tr>
    <tr>
		<td>&nbsp;</td>
        <td colspan="2">
			<jsp:setProperty name="envelopeManagerBean" property="stylesheet" value="/workflow/envelope/include/xsl/envelope_version_list.xsl" />
			<jsp:getProperty name="envelopeManagerBean" property="envelopeVersionsPresentation" />
        </td>
		<td>&nbsp;</td>
    </tr>
</table>

