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
| Lists active envelopes
| This page should only be included into others.  Typically it will be
| included by a page which sets the filtering details.
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Envelope List" 
    language="java" 
    errorPage="../../WorkflowErrors.jsp"
    import="net.project.workflow.*, net.project.security.*,
			net.project.space.Space,net.project.base.property.PropertyProvider,net.project.security.SessionManager"
%><script>
function abortEnvelope(invelopeId, abortFrom){
Ext.MessageBox.confirm('Confirm','<%=PropertyProvider.get("prm.global.envelope.abort.confirmationmessage")%>', 
	function(btn){
		if(btn=='yes'){
			document.envelopesForm.action = "<%= SessionManager.getJSPRootURL() %>/workflow/envelope/AbortEnvelopeProcessing.jsp";
			document.envelopesForm.elements["theAction"].value  = "abortEnvelope";
			document.envelopesForm.elements["id"].value = invelopeId;
			if(abortFrom == 'envelope')
				document.envelopesForm.elements["referingURL"].value = 
				"<%= SessionManager.getJSPRootURL() %>/workflow/PropertiesProcessing.jsp?theAction=submit&module=200&action=1&id="+ 
				workflowId + "&nextPage=WorkflowEnvelopeList.jsp";
			else
				document.envelopesForm.elements["referingURL"].value = "<%= SessionManager.getJSPRootURL() %>/personal/Main.jsp";
			document.envelopesForm.submit();				
		}else{
			return false;
		}
	});
}
</script><jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="envelopeManagerBean" class="net.project.workflow.EnvelopeManagerBean" scope="session" />
<%@ include file="/base/taglibInclude.jsp" %>
<form name="envelopesForm" method="post" action="<session:getJSPRootURL />/workflow/envelope/include/EnvelopeListProcessing.jsp">
<%
	String queryString = request.getQueryString(); 	
    String referingURL;
    if(queryString != null) {
        referingURL = request.getRequestURI() + "?" + queryString;
	} else {
        referingURL = request.getRequestURI();
	}
%>	
<%
    int hiddenModule = -1;
    String mySpace = user.getCurrentSpace().getType();
    if (mySpace.equals(Space.PERSONAL_SPACE)) {
        hiddenModule = net.project.base.Module.PERSONAL_SPACE;
	} else if (mySpace.equals(Space.PROJECT_SPACE)) {
        hiddenModule = net.project.base.Module.PROJECT_SPACE;
	}
%>
	<input type="hidden" name="id" />
	<input type="hidden" name="theAction" />
	<input type="hidden" name="module" value="<%=hiddenModule%>">
    <input type="hidden" name="referingURL" value=<%=referingURL%> />	
	<jsp:getProperty name="envelopeManagerBean" property="envelopeListPresentation" />

</form>
