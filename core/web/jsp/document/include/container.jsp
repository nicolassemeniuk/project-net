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
    info="Document List" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.document.*, 
            net.project.base.property.PropertyProvider,
            net.project.security.*,
            net.project.project.*,
            net.project.util.*,
            net.project.channel.*"
%>

<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="container" class="net.project.document.ContainerBean" scope="request" />
<jsp:useBean id="path" class="net.project.document.PathBean" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 

<%
	int module = securityProvider.getCheckedModuleID();
	int action = securityProvider.getCheckedActionID();
    String id = securityProvider.getCheckedObjectID();

    docManager.setSpace ( user.getCurrentSpace() );
    docManager.setUser (user);

	container.setID( docManager.getCurrentContainerID() );
	container.setUser(user);
	container.unSetListDeleted();

	container.setSortBy( docManager.getContainerSortBy() );
	container.setSortOrder ( docManager.getContainerSortOrder() );
	container.load();

    if (!securityProvider.isActionAllowed(container.getID(), Integer.toString(docManager.getModuleFromContainerID(container.getID())), net.project.security.Action.VIEW))
        	throw new net.project.security.AuthorizationFailedException(PropertyProvider.get ("prm.document.main.authorizationfailed.message"));

	docManager.setCurrentContainer (container);

    // set the path object of the container
    path.setRootContainerID(docManager.getRootContainerID());
    path.setObject(container);
    path.load();
%>

<form name="container" method="post" action="<%=SessionManager.getJSPRootURL()%>/servlet/DocumentActionBroker"> 
    <input type="hidden" name="module" value="<%= module %>">
	<input type="hidden" name="theAction">
	<input type="hidden" name="containerID" value="<%= container.getID() %>">
    <input type="hidden" name="isRoot" value="<%=container.isRoot()%>">
	<input type="hidden" name="checkedAction" value="<%= action %>">
	<input type="hidden" name="historyType" value="<%= request.getParameter("historyType") %>">

<errors:show clearAfterDisplay="true" scope="session" stylesheet="/base/xsl/error-report.xsl"/>
<div id="errorLocationID" class="errorMessage"></div>
<table border="0" width="100%" cellspacing="0" cellpadding="0">
<tr>
	<td align="left" colspan="3">&nbsp;</td>
</tr>
<tr>
	<td align="left" class="tableContent">
		<jsp:setProperty name="path" property="stylesheet" value="/document/xsl/container-path.xsl" />
		<display:get name="prm.document.main.contentsof.label" />&nbsp; <jsp:getProperty name="path" property="presentation" /> 
	</td>
	<td align="left">&nbsp;</td>

	<td align="right" class="tableContent"> 
		<% if (PropertyProvider.getBoolean ("prm.global.document.powermode.isenabled")) {%> 
		<A HREF="javascript:launchApplet()"><display:get name="prm.document.main.powermode.link" /></A>
        <% } %>
		&nbsp;
	</td>
</tr>
</table>

<%
// Initialize the channel manager
int numChannels = 1;
ChannelManager manager = new ChannelManager(pageContext);

Channel documentList = new Channel("DocumentView_List_Files");
documentList.setTitle(PropertyProvider.get("prm.document.main.channel.container.title"));
documentList.setWidth("100%");
documentList.setMinimizable(false);
documentList.setCloseable(false);
documentList.setInclude("/document/include/documentFileList.jsp");
manager.setCustomizable(false);
manager.addChannel(documentList,0, 0);

manager.display();
%>

</form>

