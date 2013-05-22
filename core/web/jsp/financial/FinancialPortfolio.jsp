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

<template:getDoctype />

<html>
<head>
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
	<!--  
	<template:getSpaceNavBar />
	-->
	
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

<%-- MOCK --%>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tbody><tr align="left" class="tableHeader">
<td class="tableHeader" width="1%"></td><td class="tableHeader" colspan="2">Business</td><td class="tableHeader">Business Type</td><td class="tableHeader">Projects</td><td class="tableHeader">People</td>
</tr>
<tr class="tableLine">
<td colspan="6" class="tableLine"><img src="../images/spacers/trans.gif" width="1" height="2" border="0"></td>
</tr>
<tr align="left" valign="middle" class="tableContent">
<td class="tableContent"><input type="radio" name="selected" value="6218" id="Vitrax"></td><td colspan="2" align="left"><a href="<%=SessionManager.getJSPRootURL()%>/financial/Main.jsp?page=<%=SessionManager.getJSPRootURL()%>/financial/Dashboard?id=10000">Vitrax</a></td><td class="tableContent" align="left">Amoblamientos</td><td class="tableContent" align="left">0</td><td class="tableContent" align="left">14</td>
</tr>
<tr class="tableLine">
<td colspan="6"><img src="../images/spacers/trans.gif" width="1" height="1" border="0"></td>
</tr>
<tr align="left" valign="middle" class="tableContent">
<td class="tableContent"><input type="radio" name="selected" value="6361" id="Compras"></td><td class="tableContent" align="left" width="2%"><img src="../images/ReplyArrow.gif"></td><td colspan="1" align="left"><a href="../financial/Dashboard?id=10000">Compras</a></td><td class="tableContent" align="left">Actividad auxiliar</td><td class="tableContent" align="left">0</td><td class="tableContent" align="left">2</td>
</tr>
<tr class="tableLine">
<td colspan="6"><img src="../images/spacers/trans.gif" width="1" height="1" border="0"></td>
</tr>
<tr align="left" valign="middle" class="tableContent">
<td class="tableContent"><input type="radio" name="selected" value="6348" id="Desarrollo de tecnologías"></td><td class="tableContent" align="left" width="2%"><img src="../images/ReplyArrow.gif"></td><td colspan="1" align="left"><a href="../financial/Dashboard?id=10000">Desarrollo de tecnologías</a></td><td class="tableContent" align="left">Actividad auxiliar</td><td class="tableContent" align="left">0</td><td class="tableContent" align="left">4</td>
</tr>
<tr class="tableLine">
<td colspan="6"><img src="../images/spacers/trans.gif" width="1" height="1" border="0"></td>
</tr>
<tr align="left" valign="middle" class="tableContent">
<td class="tableContent"><input type="radio" name="selected" value="6321" id="Infraestructura empresarial"></td><td class="tableContent" align="left" width="2%"><img src="../images/ReplyArrow.gif"></td><td colspan="1" align="left"><a href="../financial/Dashboard?id=10000">Infraestructura empresarial</a></td><td class="tableContent" align="left">Actividad auxiliar</td><td class="tableContent" align="left">0</td><td class="tableContent" align="left">1</td>
</tr>
<tr class="tableLine">
<td colspan="6"><img src="../images/spacers/trans.gif" width="1" height="1" border="0"></td>
</tr>
<tr align="left" valign="middle" class="tableContent">
<td class="tableContent"><input type="radio" name="selected" value="6282" id="Logística externa"></td><td class="tableContent" align="left" width="2%"><img src="../images/ReplyArrow.gif"></td><td colspan="1" align="left"><a href="../financial/Dashboard?id=10000">Logística externa</a></td><td class="tableContent" align="left">Actividad primaria</td><td class="tableContent" align="left">0</td><td class="tableContent" align="left">3</td>
</tr>
<tr class="tableLine">
<td colspan="6"><img src="../images/spacers/trans.gif" width="1" height="1" border="0"></td>
</tr>
<tr align="left" valign="middle" class="tableContent">
<td class="tableContent"><input type="radio" name="selected" value="6256" id="Logística interna"></td><td class="tableContent" align="left" width="2%"><img src="../images/ReplyArrow.gif"></td><td colspan="1" align="left"><a href="../financial/Dashboard?id=10000">Logística interna</a></td><td class="tableContent" align="left">Actividad primaria</td><td class="tableContent" align="left">0</td><td class="tableContent" align="left">3</td>
</tr>
<tr class="tableLine">
<td colspan="6"><img src="../images/spacers/trans.gif" width="1" height="1" border="0"></td>
</tr>
<tr align="left" valign="middle" class="tableContent">
<td class="tableContent"><input type="radio" name="selected" value="6295" id="Marketing y ventas"></td><td class="tableContent" align="left" width="2%"><img src="../images/ReplyArrow.gif"></td><td colspan="1" align="left"><a href="../financial/Dashboard?id=10000">Marketing y ventas</a></td><td class="tableContent" align="left">Actividad primaria</td><td class="tableContent" align="left">1</td><td class="tableContent" align="left">5</td>
</tr>
<tr class="tableLine">
<td colspan="6"><img src="../images/spacers/trans.gif" width="1" height="1" border="0"></td>
</tr>
<tr align="left" valign="middle" class="tableContent">
<td class="tableContent"><input type="radio" name="selected" value="6269" id="Producción"></td><td class="tableContent" align="left" width="2%"><img src="../images/ReplyArrow.gif"></td><td colspan="1" align="left"><a href="../financial/Dashboard?id=10000">Producción</a></td><td class="tableContent" align="left">Actividad primaria</td><td class="tableContent" align="left">0</td><td class="tableContent" align="left">2</td>
</tr>
<tr class="tableLine">
<td colspan="6"><img src="../images/spacers/trans.gif" width="1" height="1" border="0"></td>
</tr>
<tr align="left" valign="middle" class="tableContent">
<td class="tableContent"><input type="radio" name="selected" value="6334" id="Recursos humanos"></td><td class="tableContent" align="left" width="2%"><img src="../images/ReplyArrow.gif"></td><td colspan="1" align="left"><a href="../financial/Dashboard?id=10000">Recursos humanos</a></td><td class="tableContent" align="left">Actividad auxiliar</td><td class="tableContent" align="left">0</td><td class="tableContent" align="left">2</td>
</tr>
<tr class="tableLine">
<td colspan="6"><img src="../images/spacers/trans.gif" width="1" height="1" border="0"></td>
</tr>
<tr align="left" valign="middle" class="tableContent">
<td class="tableContent"><input type="radio" name="selected" value="6308" id="Servicio posventa"></td><td class="tableContent" align="left" width="2%"><img src="../images/ReplyArrow.gif"></td><td colspan="1" align="left"><a href="../financial/Dashboard?id=10000">Servicio posventa</a></td><td class="tableContent" align="left">Actividad primaria</td><td class="tableContent" align="left">0</td><td class="tableContent" align="left">1</td>
</tr>
<tr class="tableLine">
<td colspan="6"><img src="../images/spacers/trans.gif" width="1" height="1" border="0"></td>
</tr>
</tbody></table>
<%-- MOCK --%>
					
					<%--
					<td>&nbsp;</td>					
					<td class="tableContent">
						<pnet-xml:transform scope="session" stylesheet="/material/xsl/materials-list.xsl" content="<%=ServiceFactory.getInstance().getMaterialService().getMaterials().getXML()%>" />
					</td>
					<td>&nbsp;</td>
					--%>
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