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
    info="FormList List" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.security.User,
			net.project.security.SessionManager,
			net.project.persistence.PersistenceException,
			net.project.form.*"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="formMenu" class="net.project.form.FormMenu" scope="page" />
<% 
	boolean hasForms = true;
	formMenu.setSpace(user.getCurrentSpace());
	formMenu.setUser(user);
	formMenu.setDisplayPending(false);
	
	try {
		formMenu.load();
		formMenu.loadLists();
	} catch (FormException fe) {
		hasForms = false;
	}
%>
<form name="container" method="post" action=""> 
<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr class="channelHeader">
	   	<td class="channelHeader" width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
    	<td class="channelHeader" align=left><%=PropertyProvider.get("prm.global.links.addlinkselection.channel.browseformlists.title")%></td>
	    <td class="channelHeader" width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>
    <tr> 
      <td colspan="3"> 

<% if (hasForms && (formMenu.size() > 0)) { %>

	<%-- Apply stylesheet to format Form Menu table --%>
	<jsp:setProperty name="formMenu" property="stylesheet" value="/link/xsl/form_list_browse.xsl" />
	<jsp:getProperty name="formMenu" property="presentation" />
	
<% } else { %>
	<table align="center"><tr class="tableContent"><td><%=PropertyProvider.get("prm.global.links.addlinkselection.browseformlists.noform.message")%></td></tr></table>

<% } %>

		</td>
	</tr>
</table>
</form>
