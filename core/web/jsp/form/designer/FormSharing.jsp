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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Form Designer -- Sharing"
    language="java"
    errorPage="/errors.jsp"
    import="java.util.ArrayList,
            java.util.Iterator,
            net.project.base.property.PropertyProvider,
			net.project.base.Module,
            net.project.form.FormDesigner,
            net.project.portfolio.Portfolio,
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
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session"/>
<jsp:useBean id="formDesigner" class="net.project.form.FormDesigner" scope="session"/>
<template:getDoctype />
<html>
<head>
<title><display:get name="prm.global.application.title" /></title>
<template:getSpaceCSS/>
<security:verifyAccess objectID='<%=formDesigner.getID()%>'
                       action="modify"
                       module="<%=net.project.base.Module.FORM%>"
/>
<template:import type="javascript" src="/src/standard_prototypes.js"/>

<%
    //Load the potential list of spaces that this space could share this form with.
    SpaceList relatedSpaces;
    Portfolio informationProviders;

    //Get the list of spaces identified as INFORMATION_PROVIDERS for the current space
    relatedSpaces = SpaceManager.getRelatedChildSpaces(user.getCurrentSpace(),
                                                       SpaceRelationship.INFORMATION_PROVIDER,
                                                       ISpaceTypes.BUSINESS_SPACE,
                                                       1);

    //Try to load some basic information about these spaces, such as name and description
    informationProviders = (Portfolio)PortfolioManager.makePortfolioFromSpaceList(relatedSpaces);
    informationProviders.load();
    //Set thsi variable in the page scope
    pageContext.removeAttribute("infoProviders");
    pageContext.setAttribute("infoProviders", informationProviders, PageContext.PAGE_SCOPE);


    //Determine if we have already been sharing forms previously
    boolean alreadySharing = (formDesigner.getSpacesToShareWith().size() > 0);

    //Construct an idcsv of the spaces that we are sharing with
    StringBuffer sharingIDCSV = new StringBuffer();
    ArrayList sharingIDs = formDesigner.getSpacesToShareWith();
    Iterator it = sharingIDs.iterator();
    while(it.hasNext()) {
        sharingIDCSV.append((String)it.next());
        if (it.hasNext())
            sharingIDCSV.append(",");
    }

    System.out.println("sharingIDCSV: " + sharingIDCSV);
%>

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';    
    
function setup() {
    theForm = self.document.forms[0];
    isLoaded = true;
    //Always reload the navbar menu here to account for possible changes
    //due to the featured items.
    top.menu.menuloaded = false;
    load_menu('<%=user.getCurrentSpace().getID()%>');
}

function cancel() {
    self.document.location= JSPRootURL + "/form/designer/Main.jsp?module=<%=Module.FORM%>&action=<%=Action.MODIFY%>";
}

function reset() {
    self.document.location= JSPRootURL + "/form/designer/FormSharing.jsp?module=<%=Module.FORM%>&action=<%=Action.MODIFY%>&id=<%=formDesigner.getID()%>";
}

function submit() {
    theForm.submit();
}

function help() {
    var helplocation = "<%=SessionManager.getJSPRootURL()%>/help/Help.jsp?page=form_sharing";
    openwin_help(helplocation);
}

</script>
</head>

<body class="main" onLoad="setup();" id="bodyWithFixedAreasSupport">
<template:getSpaceMainMenu />
<template:getSpaceNavBar />

<tb:toolbar style="tooltitle" showAll="true" groupTitle="prm.global.tool.form.name">
    <tb:setAttribute name="leftTitle">
        <history:display/>
    </tb:setAttribute>
    <tb:band name="standard">
    </tb:band>
</tb:toolbar>

<div id='content'>

<form name="formSharingForm" method="post" action="FormSharingProcessing.jsp"/>
<input type="hidden" name="theAction"/>
<input type="hidden" name="module" value="<%=Module.FORM%>"/>
<input type="hidden" name="action" value="<%=Action.MODIFY%>"/>
<input type="hidden" name="id" value="<%=formDesigner.getID()%>"/>

<%-- Header and Tab Bar --%>    
<table width="100%" border="0" cellspacing="0" cellpadding="0" vspace="0">
<tr align="left">
    <td align="left">
        <span class="pageTitle"><%=PropertyProvider.get("prm.form.designer.main.pagetitle")%></span>
    </td>
    <td align="right" nowrap>
        <span class="pageTitle"><jsp:getProperty name="formDesigner" property="name" /></span>
    </td>
</tr>
<tr>
    <td>&nbsp;</td>
</tr>
</table>

<tab:tabStrip>
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.definitionedit.definition.tab")%>' href='<%="DefinitionEdit.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.fieldsmanager.fields.tab")%>' href='<%="FieldsManager.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.preview.preview.tab")%>' href='<%="Preview.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.listsmanager.lists.tab")%>' href='<%="ListsManager.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
<%-- Disabled for now since they are not implemented yet
    <tab:tab label="Search" href='<%="SearchEdit.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <tab:tab label="Links" href='<%="LinksManager.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <tab:tab label="Documentation" href='<%="DocumentationManager.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
--%>
	<% if(!user.getCurrentSpace().getType().equals(net.project.space.Space.PERSONAL_SPACE)){%>
    	<tab:tab label='<%=PropertyProvider.get("prm.form.designer.workflowselect.workflows.tab")%>' href='<%="WorkflowSelect.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
    <% } %>
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.formsharing.sharing.tab")%>' href='<%="FormSharing.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' selected="true" />    
    <tab:tab label='<%=PropertyProvider.get("prm.form.designer.activateedit.activate.tab")%>' href='<%="ActivateEdit.jsp?module=" + Module.FORM + "&action=" + Action.MODIFY + "&id=" + formDesigner.getID()%>' />
</tab:tabStrip>
<%
    // reload the form to get all the latest changes.
    // THIS IS TO FIX A REFRESH BUG.  SHOULD AVOID DB HIT HERE IN FUTURE -roger
    formDesigner.load();
%>

<table cellspacing="2" cellpadding="2" border="0">
    <tr>
        <td width="1%"><input type="radio" name="shareForm" value="false"<% if (!alreadySharing) out.print(" CHECKED");%>/></td>
        <td><%=PropertyProvider.get("prm.form.designer.formsharing.donotshare.label")%></td>
    </tr>
    <tr>
        <td><input type="radio" name="shareForm" value="true"<% if (alreadySharing) out.print(" CHECKED");%>/></td>
        <td><%=PropertyProvider.get("prm.form.designer.formsharing.sharewith.label")%></td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td>
            <pnet-xml:transform name="infoProviders" scope="page" stylesheet="/form/designer/xsl/information-providers.xsl">
                <pnet-xml:property name="idcsv" value="<%=sharingIDCSV.toString()%>"/>
            </pnet-xml:transform>
            <%--
            XMLFormatter xmlFormatter = new XMLFormatter();
            xmlFormatter.setStylesheet("/form/designer/xsl/information-providers.xsl");
            out.print(xmlFormatter.getPresentation(informationProviders.getXML()));
            --%>
        </td>
    </tr>
</table>
<p />

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
    <tb:band name="action">
        <tb:button type="submit" />
        <tb:button type="cancel" />
    </tb:band>
</tb:toolbar>

<%@ include file="/help/include_outside/footer.jsp" %>

<template:getSpaceJS />
</body>
</html>
