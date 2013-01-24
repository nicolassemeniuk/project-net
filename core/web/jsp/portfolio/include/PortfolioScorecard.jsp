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
    info=""
    language="java"
    errorPage="/errors.jsp"
    import="net.project.portfolio.view.ResultType,
            net.project.base.property.PropertyProvider"
%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="view" type="net.project.portfolio.view.PersonalPortfolioView" scope="session"/>
<jsp:useBean id="viewResults" type="net.project.portfolio.view.PersonalPortfolioViewResults" scope="session"/>
<template:import type="css" src="/styles/project.css" />
<%
    // Decide which XSL file we need; the tree view has custom XSL to
    // assist with presentation
    String xslFile = null;
    if (viewResults.getResultType().equals(ResultType.TREE)) {
        xslFile = "/portfolio/xsl/personal-portfolio-tree.xsl";
    } else {
        xslFile = "/portfolio/xsl/personal-portfolio-view.xsl";
    }
%>

 <%-- Portfolio Table --%>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
    <td>&nbsp;</td>
    <td class="tableContent">
<output:text><%=view.getDescription() != null ? view.getDescription() : PropertyProvider.get("prm.portfolio.personal.main.portfolioscore.defaultname")%></output:text>
    </td>
    <td>&nbsp;</td>
</tr>
<tr>
    <td>&nbsp;</td>
    <td class="tableContent">
		<%
			if (viewResults.getResultType().equals(ResultType.TREE)) {
			%>
			<pnet-xml:transform name="viewResults" scope="session" stylesheet="<%=xslFile%>" />
			<%
			} else {
				out.println(net.project.portfolio.view.MetaColumnView.getHtml(viewResults, view));
			}
		%>
	</td>
    <td>&nbsp;</td>
</tr>
<tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
</tr>
</table>