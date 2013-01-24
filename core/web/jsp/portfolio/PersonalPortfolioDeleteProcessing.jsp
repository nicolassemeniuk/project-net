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
|   $Revision: 20660 $
|       $Date: 2010-04-03 10:22:40 -0300 (sáb, 03 abr 2010) $
|
|--------------------------------------------------------------------%>
<%@ page contentType="text/html; charset=UTF-8"
	info="Process Project Remove" language="java" errorPage="/errors.jsp"
	import="net.project.base.Module,
			net.project.base.property.PropertyProvider,
			net.project.resource.Roster,
			net.project.project.ProjectSpace,
			net.project.project.ProjectVisibility,
			net.project.security.group.Group,
			net.project.security.group.GroupProvider"%>
<%@ include file="/base/taglibInclude.jsp"%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />

<% int currentModule = Integer.parseInt(request.getParameter("module")); %>

<security:verifyAccess action="view" module="<%=currentModule%>" />
<%String selected = request.getParameter("selected");
			boolean isGlobal = false;
			boolean isGroupAdmin = false;

			ProjectSpace space = (ProjectSpace) pageContext.getAttribute(
					"space_to_disable", PageContext.SESSION_SCOPE);
			Group groupAdmin;
			GroupProvider groupProvider = new GroupProvider();
			
			Roster roster = new Roster();
			roster.setSpace(space);

			groupAdmin = groupProvider.getSpaceAdminGroup(roster.getSpaceID());
            groupAdmin.load();
            
			if (selected != null && selected.equals("project") && space != null) {

				if (space.isUserSpaceAdministrator(user))
					space.remove();

			} else if (selected != null && selected.equals("self")
					&& space != null) {
				if (space.getVisibility() == ProjectVisibility.GLOBAL) {
					isGlobal = true;
				} else if (groupAdmin.isMember(user.getID()) && groupAdmin.getPersonMembers().size() == 1){
					isGroupAdmin = true;
				} else {
					roster.removePerson(user.getID());
				}
			}
			out.println("<script language=\"javascript\">");
			if (isGlobal) {
				String message = PropertyProvider
						.get("prm.project.portfoliodelete.deletenotallowed");
				out.print("alert(\"");
				out.print(message);
				out.print("\");");
				
			} else if (isGroupAdmin) {
				String message = PropertyProvider
						.get("prm.directory.resource.roster.removeperson.spaceadmingroup.message");
				out.print("alert(\"");
				out.print(message);
				out.print("\");");
			
			} else {
				out.println("opener.location.reload(true);");
			}
			out.println("self.close();");
			out.println("</script>");

		%>


