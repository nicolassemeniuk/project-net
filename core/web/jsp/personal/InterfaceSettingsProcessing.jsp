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
    info="Interface Settings" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.resource.PersonProperty,
    		net.project.resource.PersonPropertyGlobalScope,    		
			net.project.space.ISpaceTypes,
			net.project.util.StringUtils "
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%
		String type = StringUtils.isNotEmpty(request.getParameter("spaceType"))?request.getParameter("spaceType"):"";		
		if (request.getParameter("theAction").equals("submit")) {
			PersonProperty property = new PersonProperty();
			property.setScope(new PersonPropertyGlobalScope(user));
			String[] properties = property.get("prm.global.login","startPage");
			if (properties.length == 0) {
				// The user hasn't settled yet the starting page property
				property.put("prm.global.login", "startPage",type);
			} else {
				if (!properties[0].equals(type)) {
					// The user wants to change the starting page.
					property.replace("prm.global.login", "startPage",type);
				}
			}
			session.setAttribute("spaceType",type);
		}
	pageContext.forward("/personal/Setup.jsp");
%>