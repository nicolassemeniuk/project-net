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
       info="Business Materials Channel"
       language="java" 
       errorPage="/errors.jsp"
           import="net.project.security.*,
                   net.project.business.BusinessSpaceBean,
                   net.project.material.MaterialBeanList,
                   net.project.base.Module" 
 %>
 
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="businessSpace" class="net.project.business.BusinessSpaceBean" scope="session" />
<jsp:useBean id="materialsList" class="net.project.material.MaterialBeanList" scope="page" />

<%
	materialsList.clear();
	materialsList.setSpaceID(businessSpace.getID());
	materialsList.load();
%>

<%-- Apply stylesheet to format Business Materials channel --%>
<pnet-xml:transform name="materialsList" scope="page" stylesheet="/material/xsl/business-materials.xsl">
	<pnet-xml:param name="materialModuleID" value="<%=String.valueOf(Module.MATERIAL)%>" />
	<pnet-xml:param name="JSPRootURL" value="<%=SessionManager.getJSPRootURL()%>" />
	<pnet-xml:param name="createAction" value="<%=String.valueOf(Action.CREATE)%>" />
</pnet-xml:transform>