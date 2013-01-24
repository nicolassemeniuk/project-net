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

<%--------------------------------------------------------------------
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|
|--------------------------------------------------------------------%>
<%@page
    contentType="text/html; charset=UTF-8"
   language="java"
   errorPage="/errors.jsp"
   import="net.project.base.Module,
           net.project.business.BusinessSpaceBean,
           net.project.portfolio.BusinessPortfolio,
           net.project.portfolio.PortfolioManager,
           net.project.security.Action,
           net.project.security.SessionManager,
           net.project.security.User,
           net.project.space.ISpaceTypes,
           net.project.space.SpaceList,
           net.project.space.SpaceManager,
           net.project.space.SpaceRelationship,
           net.project.xml.XMLFormatter"
%>
<%@include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>
<%
    String JSPRootURL = SessionManager.getJSPRootURL();

    SpaceList relatedSpaces;
    BusinessPortfolio businessPortfolio;

    //Construct BusinessPortfolio
    relatedSpaces = SpaceManager.getRelatedChildSpaces(user.getCurrentSpace(),
                                                       SpaceRelationship.INFORMATION_PROVIDER,
                                                       ISpaceTypes.BUSINESS_SPACE,
                                                       1);
    
    businessPortfolio = (BusinessPortfolio)PortfolioManager.makePortfolioFromSpaceList(relatedSpaces);
    businessPortfolio.load();
%>
<html>
<head>
<title>Related Businesses</title>

<%-- Import CSS --%>
<template:getSpaceCSS/>
<%-- Import JavaScript --%>
<template:import type="javascript" src="/src/util.js"/>
<template:import type="javascript" src="/src/window_functions.js"/>
<template:import type="javascript" src="/src/checkRadio.js"/>

<script language="javascript">
function reset() {
    var theLocation = "<%=JSPRootURL%>/business/relatedBusinesses/Main.jsp?module=<%=Module.BUSINESS_SPACE%>";
    self.document.location = theLocation;
}

function create() {
    var theLocation = "<%=JSPRootURL%>/business/relatedBusinesses/MainProcessing.jsp?module=<%=Module.BUSINESS_SPACE%>"+
        "&action=<%=Action.CREATE%>";
    self.document.location = theLocation;
}

function modify() {
    if (checkRadio(businessListForm.id, "Please select a business before clicking modify")) {
        var theLocation = "<%=JSPRootURL%>/business/ModifyBusiness.jsp?module=<%=Module.BUSINESS_SPACE%>"+
            "&action=<%=Action.MODIFY%>";
        self.document.locatio = theLocation;
    }
}

function help() {
    //TODO: implement relatedBusinesses help.
    openwin_help("<%=JSPRootURL%>/help/Help.jsp?page=business_relatedBusinesses");
}
</script>

</head>

<body class="main">
<%----- Toolbar Declaration ----------------------------------------------------%>
<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.business.subbusiness.business.label">
    <tb:setAttribute name="leftTitle">
        <history:history>
            <history:module displayToken="@prm.business.relatedBusinesses.label"
                            jspPage='<%=JSPRootURL + "/business/relatedBusinesses/Main.jsp?module="+Module.BUSINESS_SPACE%>'/>
        </history:history>
    </tb:setAttribute>
    <tb:band name="standard">
        <tb:button type="create"/>
        <tb:button type="modify"/>
    </tb:band>
</tb:toolbar>

<form name="businessListForm" method="post" action='<%=JSPRootURL+"/business/relatedBusinesses/MainProcessing.jsp"%>'>
<input type="hidden" name="listAction" value=""/>
<input type="hidden" name="module" value="<%=Module.BUSINESS_SPACE%>"/>

<%-- Output the lists of businesses matching our criteria --%>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
    <td width="1%" class="channelHeader" align="left"><img src="<%=JSPRootURL%>/images/icons/channelbar-left_end.gif" width="8" height="15" alt="" border="0"></td>
    <td valign="middle" align="left" class="channelHeader"><display:get name="prm.business.relatedBusinesses.label"/></td>
    <td width="1%" class="channelHeader" align="right"><img src="<%=JSPRootURL%>/images/icons/channelbar-right_end.gif" width="8" height="15" alt="" border="0"></td>
</tr>
<tr>
    <td colspan="3">
        <%
        XMLFormatter xmlFormatter = new XMLFormatter();
        xmlFormatter.setStylesheet("/business/relatedBusinesses/xsl/business-portfolio.xsl");
        out.print(xmlFormatter.getPresentation(businessPortfolio.getXML()));
        %>
    </td>
</tr>
</table>

<tb:toolbar style="action" showLabels="true">
            <tb:band name="action">
                <tb:button type="add" label="Create Business" function="javascript:create();"/>
            </tb:band>
</tb:toolbar>

</form>
<%@include file="/help/include_outside/footer.jsp"%>
</body>
</html>
