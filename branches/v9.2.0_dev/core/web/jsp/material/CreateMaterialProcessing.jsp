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

<%@ page contentType="text/html; charset=UTF-8" info="New Business Processing 2" language="java" errorPage="/errors.jsp"
	import="net.project.material.MaterialCreateWizard,
    		net.project.security.User,
    		net.project.base.Module,
    		net.project.security.SessionManager,
    		net.project.hibernate.service.ServiceFactory"%>
<jsp:useBean id="materialBean" class="net.project.material.MaterialCreateWizard" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:setProperty name="materialWizard" property="*" />

<%
	materialBean.setUser(user);

	if ((request.getParameter("type") == null) || (request.getParameter("type").equals(""))) {
		materialBean.setFlavor("");
	}

	if ((request.getParameter("description") == null) || (request.getParameter("description").equals(""))) {
		materialBean.setDescription("");
	}

	ServiceFactory.getInstance().getMaterialService().saveMaterial(materialBean);

	// TODO Ramiro Enviar la pantalla del material recien creado
	response.sendRedirect(SessionManager.getJSPRootURL() + "/material/Main.jsp?module=" + Module.MATERIAL + "&portfolio=true");
%>
