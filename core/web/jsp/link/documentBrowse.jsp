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
    import="net.project.base.property.PropertyProvider,
			net.project.document.DocumentManagerBean, 
            net.project.document.PathBean,
            net.project.document.ContainerBean,
            net.project.security.SecurityProvider,
            net.project.security.User,
            net.project.security.SessionManager,
            net.project.xml.XMLFormatter"
%>

<jsp:useBean id="container" class="net.project.document.ContainerBean" />
<jsp:useBean id="path" class="net.project.document.PathBean" />
<jsp:useBean id="m_docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<%
	int module = securityProvider.getCheckedModuleID();
	int action = securityProvider.getCheckedActionID();
	String id = securityProvider.getCheckedObjectID();
        

        // will throw if session has expired
        // SessionManager.verifySessionActive ((User)user);

        m_docManager.setSpace ( user.getCurrentSpace() );
        m_docManager.setUser (user);

	    container.setID( m_docManager.getCurrentContainerID() );
        container.setUser(user);

        container.setSortBy( m_docManager.getContainerSortBy() );
        container.setSortOrder ( m_docManager.getContainerSortOrder() );
        container.load();

        if (!securityProvider.isActionAllowed(container.getID(),
            Integer.toString(m_docManager.getModuleFromContainerID(container.getID())),
            net.project.security.Action.VIEW))
            throw new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.links.security.validationfailed.message"));


	    m_docManager.setCurrentContainer (container);
	    
	    // set the path object of the container
	    path.setRootContainerID(m_docManager.getRootContainerID());
	    path.setObject(container);
	    path.load();

%>

<%

    String type_pick = request.getParameter("TYPE_PICK_DD");
    String search_level = request.getParameter("SEARCH_LEVEL");

%>

<form name="container" method="post" action="ActionProcessing.jsp"> 
  <table border="0" width="100%" cellspacing="0" cellpadding="0">	
  	<tr><td class="tableContent"> &nbsp; </td></tr>
	<tr>
		<td align="left" class="tableContent">
		<%
          XMLFormatter m_formatter = new XMLFormatter();
		  m_formatter.setStylesheet("/link/xsl/container-path.xsl");
          String m_xml = "<path_result>" + path.getXMLBody() + "<module>" + module + "</module><action>" + action + "</action><return_id>" + id + "</return_id><type_pick>" + type_pick + "</type_pick><search_level>" + search_level + "</search_level></path_result>";
          out.println(PropertyProvider.get("prm.document.main.contentsof.label") + "&nbsp;" + m_formatter.getPresentation(m_xml));
        %>
		</td>
	</tr>
  </table>
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr class="channelHeader"> <!-- PATH STUFF GOES HERE --> 
    <tr>
    	<td class="channelHeader" width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
    	<td class="channelHeader" width=49% align=left><%=PropertyProvider.get("prm.global.links.addlinkselection.channel.browsedocuments.title")%></td>
	    <td class="channelHeader" width=49% align=left> &nbsp;</td>
	    <td class="channelHeader" width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>
    <tr> 
      <td colspan="4"> 
        <table border="0" width="100%" cellpadding="0" cellspacing="0">

          <%-- Apply stylesheet to format document list--%> 
          <%
          m_formatter.setStylesheet("/link/xsl/documentBrowse.xsl");
          m_xml = "<container_result><container>" + container.getXMLProperties() + container.getXMLContents() + "</container><module>" + module + "</module><action>" + action + "</action><return_id>" + id + "</return_id><type_pick>" + type_pick + "</type_pick><search_level>" + search_level + "</search_level></container_result>";
          out.println(m_formatter.getPresentation(m_xml));
          %>
        </table>
      </td>
    </tr>
  </table>
</form>
