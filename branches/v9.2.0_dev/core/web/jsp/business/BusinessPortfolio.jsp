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
|   $Revision: 20912 $
|       $Date: 2010-06-03 15:50:43 -0300 (jue, 03 jun 2010) $
|
| This is a collection of all businesses that the current user
| is a member of.
|--------------------------------------------------------------------%>
<%@ page
    contentType="text/html; charset=UTF-8"
    info="Personal Business Portfolio List"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.portfolio.BusinessPortfolioBean,
            net.project.base.Module,
            net.project.security.User,
            net.project.security.SessionManager,
            net.project.space.Space,
            net.project.space.PersonalSpaceBean,
            net.project.financial.FinancialSpaceBean,
            net.project.base.property.PropertyProvider,
            net.project.document.DocumentManagerBean"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="personalSpace" class="net.project.space.PersonalSpaceBean" scope="session" />
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="businessPortfolio" class="net.project.portfolio.BusinessPortfolioBean" scope="session" />
<jsp:useBean id="businessSpace" class="net.project.business.BusinessSpaceBean" scope="session" />
<jsp:useBean id="financialSpace" class="net.project.financial.FinancialSpaceBean" scope="session" />
<template:getDoctype />
<html>
<head>

<% 
    // No security validation is necessary since the portfolio is loaded via the user bean
    // Switch to personal space if not currently in Personal, Business or Project space
    
    int currentModule = 0;
    Space currentSpace = user.getCurrentSpace();
    if (currentSpace.isTypeOf(Space.PERSONAL_SPACE)) {
        currentModule = Module.PERSONAL_SPACE;
    } else if (currentSpace.isTypeOf(Space.BUSINESS_SPACE)) {
        currentModule = Module.BUSINESS_SPACE;
    } else if (currentSpace.isTypeOf(Space.PROJECT_SPACE)) {
        currentModule = Module.PROJECT_SPACE;
    } else if (currentSpace.isTypeOf(Space.FINANCIAL_SPACE)) {
        currentModule = Module.FINANCIAL_SPACE;        
    } else {
        user.setCurrentSpace(personalSpace);
        docManager.getNavigator().put("TopContainerReturnTo", SessionManager.getJSPRootURL() + "/personal/Main.jsp");
        currentModule = Module.PERSONAL_SPACE;
    }
    businessPortfolio.clear();
    businessPortfolio.setUser(user);
    businessPortfolio.load();
%>

<title><display:get name="prm.global.application.title" /></title>
<%-- Import CSS --%>
<template:getSpaceCSS space="business" />

<%-- Import Javascript --%>
<template:getSpaceJS space="business" />
<script language="javascript">
    var theForm;
    var errorMsg;
    var JSPRootURL = '<%= SessionManager.getJSPRootURL() %>';
    var moduleId = <%=currentModule%>;
    var currentSpaceTypeForBlog = 'person';
    var currentSpaceIdForBlog = '<%= SessionManager.getUser().getID() %>';

function setup() {
    theForm = self.document.forms[0];
    isLoaded = true;
}


function cancel() {
   var theLocation = JSPRootURL + "/personal/Main.jsp";
   self.document.location = theLocation;
}

function reset() {
   var theLocation = JSPRootURL + "/business/BusinessPortfolio.jsp?module=<%=net.project.base.Module.BUSINESS_SPACE%>&portfolio=true";
   self.document.location = theLocation;
}


function create () {
   var theLocation = JSPRootURL + "/business/CreateBusiness1.jsp?module=<%=currentModule%>&portfolio=true";
   self.document.location = theLocation;
}
function modify() {
if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')){ 
	var referer=JSPRootURL + "/business/BusinessPortfolio.jsp?module=<%=net.project.base.Module.BUSINESS_SPACE%>&portfolio=true";	
	var modifyBusinessURL=JSPRootURL + "/business/ModifyBusiness.jsp?module=<%=net.project.base.Module.BUSINESS_SPACE%>&action=<%=net.project.security.Action.MODIFY%>&referer="+referer;
	self.document.location=JSPRootURL+"/business/Main.jsp?id="+getSelectedValueLocal()+"&page="+ escape(modifyBusinessURL);
}
}

function remove() {
    if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) 
        {
        var m_url = JSPRootURL + "/business/BusinessPortfolioDelete.jsp?selected="+ getSelection(theForm) +"&module=<%= Module.BUSINESS_SPACE %>"+"&action=<%= net.project.security.Action.VIEW %>&portfolio=true";
        var redirect_url = JSPRootURL +"/business/Main.jsp?id="+ getSelectedValueLocal()+"&page="+ escape(m_url);
        var link_win = openwin_linker(redirect_url);
        link_win.focus();
        }
}

function getSelectedValueLocal() {
	var field = theForm.elements["selected"];
	if(!field) {
		field = document.getElementById('channelIFrame').contentWindow.document.getElementById('iFrameForm').elements['selected'];
		var idval = field.value;
		for (var i = 0; i < field.length; i++) {
			if (field[i].checked == true) {
				idval = field[i].value;
				break;
			}
		}
  			return idval;
	} else {
		return getSelection(theForm);
	}
}
	
function help()
{
    var helplocation=JSPRootURL+"/help/Help.jsp?page=business_portfolio";
    openwin_help(helplocation);
}

function exportMSP() {
	  if (verifySelection(theForm, 'multiple', '<%=PropertyProvider.get("prm.global.javascript.verifyselection.noselection.error.message")%>')) 
      {
	      var m_url = JSPRootURL + "/business/ExportAllProjectsXMLMain.jsp?selected="+ getSelection(theForm) +"&module=<%= Module.BUSINESS_SPACE %>"+"&action=<%= net.project.security.Action.VIEW %>&portfolio=true";
	      var redirect_url = JSPRootURL +"/business/Main.jsp?id="+ getSelectedValueLocal()+"&page="+ escape(m_url);
	      var link_win = openwin_linker(redirect_url);
	      link_win.focus();
      }
}

</script>
</head>
<body class="main" id='bodyWithFixedAreasSupport' onLoad="setup();">
<template:getSpaceMainMenu />

<tb:toolbar style="tooltitle" showAll="true" space="business" groupTitle='<%= PropertyProvider.get("prm.business.businessportfolio.title") %>' showSpaceDetails="false">
    <tb:band name="standard">
        <tb:button type="create" label='<%= PropertyProvider.get("prm.business.businessportfolio.create.button.tooltip")%>' />
        <tb:button type="remove" label='<%= PropertyProvider.get("prm.business.businessportfolio.remove.button.tooltip")%>' />
        <tb:button type="modify" label='<%= PropertyProvider.get("prm.business.main.modify.link")%>' />
		<tb:button type="custom" imageEnabled='<%=PropertyProvider.get("all.global.toolbar.standard.export.image.on")%>' imageOver='<%=PropertyProvider.get("all.global.toolbar.standard.export.image.over")%>' label='Export MS Project Files' function="javascript:exportMSP();"/>
		
    </tb:band>
</tb:toolbar>

<div id='content'>

<form action="" method="post">
    <input type="hidden" name="theAction">

  <table  border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr align=left class="channelHeader">
        <td width="1%"><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
        <td class="channelHeader"><display:get name="prm.business.businessportfolio.channel.memberof.title" /></td>
        <td width="1%" align=right><img src="<%= SessionManager.getJSPRootURL() %>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
    </tr>
    <tr>
        <td>&nbsp;</td>
        <td class="tableContent">
            <pnet-xml:transform scope="session" stylesheet="/business/xsl/business-portfolio-tree.xsl" content="<%=businessPortfolio.getTreeView().getXMLBody()%>" />
        </td>
        <td>&nbsp;</td>
    </tr>
</table>

<tb:toolbar style="action" showLabels="true" bottomFixed="true">
	<tb:band name="action" />
</tb:toolbar>

</form>
<%@ include file="/help/include_outside/footer.jsp" %>

</body>
</html>