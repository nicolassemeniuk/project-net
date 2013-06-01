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

<%@ page contentType="text/html; charset=UTF-8" info="Personal Financial Portfolio List" language="java" errorPage="/errors.jsp"
	import="net.project.security.*,
			net.project.base.property.PropertyProvider,
            net.project.space.Space,
            net.project.space.SpaceRelationship,
            net.project.util.JSPUtils"%>
<%@ include file="/base/taglibInclude.jsp"%>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="financialPortfolio" class="net.project.portfolio.FinancialPortfolioBean" scope="session" />
<jsp:useBean id="financialSpace" class="net.project.financial.FinancialSpaceBean" scope="session" />

<template:getDoctype />

<html>
<head>

<% 
    // No security validation is necessary since the portfolio is loaded via the user bean
    // Switch to personal space if not currently in Personal, Business or Project space
    

    financialPortfolio.clear();
	financialPortfolio.setUser(user);
	financialPortfolio.load();
%>


<title><display:get name="prm.global.application.title" /></title>

<%-- Import CSS --%>
<template:getSpaceCSS space="financial" />

<%-- Import Javascript --%>
<template:getSpaceJS space="financial" />

<script language="javascript">
    var theForm;
    var isLoaded = false;
    var JSPRootURL = '<%=SessionManager.getJSPRootURL()%>';    
	var currentSpaceTypeForBlog = 'financial';
    var currentSpaceIdForBlog = '<%=SessionManager.getUser().getID()%>';
    
	function setup() {
		theForm = self.document.forms[0];
		isLoaded = true;
	}
	
	function help() {
		var helplocation = JSPRootURL + "/help/Help.jsp?page=financial_portfolio";
		openwin_help(helplocation);
	}
</script>
</head>

<body class="main" id='bodyWithFixedAreasSupport' onLoad="setup();">
	<template:getSpaceMainMenu />
	
	<tb:toolbar style="tooltitle" groupTitle="prm.financial.financialportfolio.title" showAll="true" showSpaceDetails="false" space="financial">
		<tb:band name="standard">
		</tb:band>
	</tb:toolbar>

	<div id='content'>
		<form action="" method="post">
			<input type="hidden" name="theAction" />

			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr align=left class="channelHeader">
					<td width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border=0></td>
					<td class="channelHeader"><display:get name="prm.financial.financialportfolio.channel.memberof.title" /></td>
					<td width="1%" align=right><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border=0></td>
				</tr>
				<tr>					
					<td>&nbsp;</td>					
					<td class="tableContent">
						<pnet-xml:transform scope="session" stylesheet="/financial/xsl/financial-portfolio-tree.xsl" content="<%=financialPortfolio.getTreeView().getXMLBody()%>" />
					</td>
					<td>&nbsp;</td>

				</tr>
			</table>
			
			<tb:toolbar style="action" showLabels="true" bottomFixed="true">
				<tb:band name="action" />
			</tb:toolbar>			
		</form>
	</div>

	<%@ include file="/help/include_outside/footer.jsp"%>
</body>
</html>