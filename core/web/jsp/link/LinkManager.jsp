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
    info="page for managing links to an object" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.link.LinkManagerBean,
            net.project.link.ILinkableObject,
            net.project.security.SecurityProvider,
            net.project.base.ObjectFactory,
            net.project.space.Space,
            net.project.security.User,
            net.project.xml.XMLFormatter,
			net.project.security.SessionManager" 
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="m_linkMgr" class="net.project.link.LinkManagerBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />

<%
int module = securityProvider.getCheckedModuleID();
int action = securityProvider.getCheckedActionID();
String id = securityProvider.getCheckedObjectID();
ILinkableObject m_parent_obj = ObjectFactory.makeLinkableObject(id);
%>
<security:verifyAccess action="modify"
					   module="<%=net.project.base.ObjectType.getModuleFromType(m_parent_obj.getType())%>" /> 

<%
String m_view = request.getParameter("view");
String m_context = request.getParameter("context");
if (m_view != null)
	m_linkMgr.setView(Integer.parseInt(m_view));
if (m_context != null)
	m_linkMgr.setContext(Integer.parseInt(m_context));
	
m_linkMgr.setRootObject(m_parent_obj);

XMLFormatter m_formatter = new XMLFormatter();
m_formatter.setStylesheet("/link/xsl/displayLinksForm.xsl");
    
String linksOutput = m_formatter.getPresentation(m_linkMgr.getLinksXML());

%>
<template:getDoctype />
<html>
<head>
<meta http-equiv="expires" content="0">
<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS />

<%
HttpSession mySess=request.getSession() ;	
String referer = (String)mySess.getAttribute("refererLink");
%>

<style type="text/css">
    body {overflow: auto}
</style>

<%-- Import Javascript --%>

<script>

function go_cancel() {
	var fromURL = "<%=mySess.getAttribute("from")%>";
	if (fromURL != "null" && fromURL != "") {
		parent.location = fromURL;
	} else {
		self.location = "<%=referer%>";	
	}
}

function submitForm() {
    checkedID = -1;
    m_form = document.selectObject;
    
    if (m_form.elements.length > 3)
        {
        var isPicked = false;
        isPicked = m_form.elements["linked_id"].checked;
        if(isPicked)
            {
    	    document.selectObject.submit();
            }
        else
            {
            for (i=0;i<m_form.linked_id.length;i++)
                {
                if (m_form.linked_id[i].checked)
                    checkedID = i;
                }
            if (checkedID > -1) {
                document.selectObject.submit();
            } else {
            	var errorMessage = '<%=PropertyProvider.get("prm.global.links.remove.select.message")%>';
				extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
                }
            }
        }
    else
        {
       	var errorMessage = '<%=PropertyProvider.get("prm.global.links.remove.none.message")%>';
		extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
        }
}

function help()
{
	var helplocation="<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=link_main";
	openwin_help(helplocation);
}

</script>
</head>

<body class="main" id='bodyWithFixedAreasSupport'>
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<div id='content'>

<table cellpadding=0 cellspacing=0 border=0  width="50%">
<tr>
	<td class="channelHeader" width=1%><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td class="channelHeader" width=98% colspan=2 align=left><%=PropertyProvider.get("prm.global.links.channel.currentobject.title")%></td>
	<td class="channelHeader" width=1% align="right"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
<tr>
	<td>&nbsp;</td>
	<td width="20%" class="tableHeader"><%=PropertyProvider.get("prm.global.links.currentobject.name.label")%></td>
	<td class="tableContent"><%=m_parent_obj.getName()%></td>
	<td>&nbsp;</td>
</tr>

</table>

<%
if (request.getParameter("linked") != null)
    out.println("<B><FONT COLOR=\"RED\">" + PropertyProvider.get("prm.global.links.currentobject.added.message", new Object[] {request.getParameter("linked")}) + "</FONT></B>");
if (request.getParameter("unlinked") != null)
    out.println("<B><FONT COLOR=\"RED\">" + PropertyProvider.get("prm.global.links.currentobject.removed.message", new Object[] {request.getParameter("unlinked")}) + "</FONT></B>");
%>
<form name="selectObject" action="removeLinkConfirm.jsp" method="post">
<%=linksOutput%>
<input type="hidden" name="id" value="<%=id%>">
<input type="hidden" name="action" value="<%=action%>">
<input type="hidden" name="module" value="<%=module%>">
</form>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action">
		<tb:button type="delete" label='<%=PropertyProvider.get("prm.global.links.unlink.button.label")%>' function="javascript:submitForm();" order="1" />
		<tb:button type="add" label='<%=PropertyProvider.get("prm.global.links.addnew.button.label")%>' function='<%="addLinkForm.jsp?module="+module+"&action="+action+"&id="+id+"&objectId="+request.getParameter("objectId")%>' order="2" />
		<tb:button type="cancel" label='<%=PropertyProvider.get("prm.global.links.close.button.label")%>' function="javascript:go_cancel();" order="3" />
	</tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>


<template:getSpaceJS />
</body>
</html>
