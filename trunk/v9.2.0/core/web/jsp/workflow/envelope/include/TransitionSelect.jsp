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
    info="Transition Select" 
    language="java" 
    errorPage="../../WorkflowErrors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.*,
			net.project.space.Space,
			net.project.workflow.*"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="envelopeManagerBean" class="net.project.workflow.EnvelopeManagerBean" scope="session" />
<%
	Envelope envelope = envelopeManagerBean.getCurrentEnvelope();
%>
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
  		<tr class="channelHeader" align="left">
        	<th class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"/></th>
		    <th class="channelHeader">&nbsp;<%=PropertyProvider.get("prm.workflow.envelope.include.transitionselect.channel.action.title")%></th>
			<th class="channelHeader" align="right"><%-- Insert toolbar here --%></th>
            <th class="channelHeader" align="right" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"/></th>
	    </tr>
        <tr>
			<td>&nbsp;</td>
            <td colspan="2">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td class="fieldNonRequired" align="left"><%=PropertyProvider.get("prm.workflow.envelope.include.transitionselect.comments.label")%></td>
					</tr>
					<tr>
						<td class="tableContent" align="left">
							<%-- Don't get existing comments - they are displayed on the properties page, read-only.  This is for additional comments --%>
							<textarea cols="50" rows="3" name="comments"></textarea>
						</td>
					</tr>
					<tr>
						<td class="fieldNonRequired" align="left"><%=PropertyProvider.get("prm.workflow.envelope.include.transitionselect.action.label")%></td>
					</tr>
					<tr>
						<td>
							<%-- Display Transitions list --%>
							<jsp:setProperty name="envelopeManagerBean" property="stylesheet" value="/workflow/envelope/include/xsl/transition_select.xsl" />
							<jsp:getProperty name="envelopeManagerBean" property="availableTransitionsPresentation" />
			            </td>
					</tr>
				</table>
			</td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td colspan="2">
				<%=envelopeManagerBean.consumeLastTransitionMessage()%>
			</td>
			<td>&nbsp;</td>
		</tr>			
    </table>
