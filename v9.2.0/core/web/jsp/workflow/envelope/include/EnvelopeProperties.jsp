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
| Displays properties of an envelope
| Input Parameters
|	mode	- (optional) simple : simple envelope properties (default)
|						 detailed : detailed envelope properties
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Envelope Properties" 
    language="java" 
    errorPage="../../WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.*,
			net.project.space.Space,
			net.project.workflow.*"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="envelopeManagerBean" class="net.project.workflow.EnvelopeManagerBean" scope="session" />
<jsp:useBean id="envelopeBean" class="net.project.workflow.EnvelopeBean" scope="request" />
<%
	String 	mode = "simple";
	if (request.getParameter("mode") != null && !request.getParameter("mode").equals("")) {
		mode = request.getParameter("mode");
	}
	
	String stylesheet = null;
	if (mode.equals("detailed")) {
		stylesheet = "/workflow/envelope/include/xsl/envelope_properties_detail.xsl";
	} else {
		stylesheet = "/workflow/envelope/include/xsl/envelope_properties.xsl";
	}
%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr align="left"> 
    <td class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></td>
    <td class="channelHeader">&nbsp;<%=PropertyProvider.get("prm.workflow.envelope.include.properties.channel.properties.title")%></td>
	<td class="channelHeader" align="right">
		<% if (mode.equals("simple")) { %>
		<tb:toolbar style="channel" showLabels="true">
			<tb:band name="channel">
				<tb:button type="properties" labelToken="@prm.workflow.envelope.include.properties.details.button.label"/>
			</tb:band>
		</tb:toolbar>
		<% } else { %>
			&nbsp;
		<% } %>
	</td>
    <td class="channelHeader" width="1%" align="right"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></td>
  </tr>
  <tr> 
  	<td>&nbsp;</td>
    <td colspan="2"> 
		<%-- Properties table --%>
		<jsp:setProperty name="envelopeBean" property="stylesheet" value="<%=stylesheet%>" />
		<jsp:getProperty name="envelopeBean" property="propertiesPresentation" />
    </td>
  	<td>&nbsp;</td>
  </tr>
  <tr><td colspan="4">&nbsp;</td></tr>
  <tr> 
  	<td>&nbsp;</td>
    <td colspan="2"> 
		<%-- Step Indicator --%>
		<jsp:include page="StepIndicator.jsp" flush="true" />
    </td>
  	<td>&nbsp;</td>
  </tr>
</table>
