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
    info="User List"
    language="java"
    import="net.project.security.User,
            net.project.security.SessionManager,
            net.project.security.Action,
            net.project.base.property.PropertyProvider,
            net.project.base.Module,
            net.project.admin.ApplicationSpace,
            net.project.xml.XMLFormatter,
            net.project.resource.PersonListBean,
            java.io.FileWriter,
            net.project.util.Validator"
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="applicationSpace" class="net.project.admin.ApplicationSpace" scope="session" />
<jsp:useBean id="userList" class="net.project.resource.PersonListBean" scope="session" />

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>"/>

<%
    String errorMessage = request.getParameter("errorMessage");
    if (!Validator.isBlankOrNull(errorMessage)) {
%>
    <p><div id="errorLocationID" class="errorMessage"><%=errorMessage%></div>
<%
    }
%>

<table border="0" cellspacing="0" cellpadding="0" width="100%">
<tr>
  <td>&nbsp;</td></tr>
<tr>
  <td colspan="3">&nbsp;&nbsp;
  <span class=tableContent><search:letter /></span>
	&nbsp;&nbsp;
  </td>
  <td></td>
  <td class="tableHeader">&nbsp;&nbsp;<b><%=PropertyProvider.get("prm.project.admin.domain.keyword.label") %></b>
    <input type="text" onkeyPress="keyhandler(event)" name="key" size="15" maxlength="40" value='<c:out value="${getFilter}"/>'>
	<a href="javascript:search(self.document.forms['mainForm'].key.value);"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/toolbar-gen-search_on.gif" alt="Go" border=0 align="absmiddle"></a>
	&nbsp;&nbsp;
  </td>
</tr>
<tr>
  <td colspan=5 class="tableContent"><i><%=PropertyProvider.get("prm.project.admin.domain.main.message.label")%></i></td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr>
  <td class="tableHeader">&nbsp;&nbsp;<%=PropertyProvider.get("prm.project.admin.domain.user.status.label")%> </br>
    <select name="userStatusFilter" multiple height="4">
      <%=net.project.resource.ProfileCodes.getUserStatusOptionList(userList.getUserStatusFilter())%>
    </select>
  </td>
  <td></td>
  <td class="tableHeader"> &nbsp;&nbsp;<%=PropertyProvider.get("prm.project.admin.forms.include.license.label")%> </br>
    <select name="userLicenseFilter" multiple height="4">
      <%=net.project.resource.ProfileCodes.getLicenseList(userList.getLicenseFilter())%>
    </select>
  </td>
  <td></td>
  <td class="tableHeader">&nbsp;&nbsp;<%=PropertyProvider.get("prm.project.admin.forms.include.domains.label")%> </br>
    <select name="userDomainFilter" multiple height="4">
    <%
      net.project.security.domain.DomainOptionList optionList = new net.project.security.domain.DomainOptionList();
      optionList.loadAll();
    %>
    <%=optionList.getDomainOptionList (userList.getUserDomainFilter())%>
    </select>
  </td>
</tr>	
<tr><td>&nbsp;</td></tr>
<tr><td>&nbsp;</td></tr>
</table>
<table border="0" cellspacing="0" cellpadding="0" width="100%">
  <tr>
    <td>
<%
     // load and display the userList if the display mode is set to "search"
     if (userList.getDisplayMode().equals("search")) {
	 
         // see if a filter has been passed in the request
	 String searchFilter = request.getParameter("searchFilter");
	 if (searchFilter != null && !searchFilter.equals("")) {
	     userList.setFilter (searchFilter);
         }
      
         // then load the filtered list
	 userList.loadFiltered();	
     }

     else if (userList.getDisplayMode().equals("initial")) {
         // clear the userList and all current filters
	 userList.clear();
     }
%>

	<table border="0" cellspacing="0" cellpadding="0">
	<tr class="channelHeader">
		<td class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
		<td nowrap class="channelHeader" align="left" width="85%">Filter Results (number of matches): <%=userList.size()%></td>
		<td align=right class="channelHeader" width="1%"><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
	</tr>

        <tr class="channelHeader">
              <td class="channelContent">&nbsp;</td>
              <td colspan="2" align="center">

			 <jsp:setProperty name="userList" property="stylesheet" value="/admin/include/xsl/user-list.xsl" /> 
			 <jsp:getProperty name="userList" property="presentation" />               
                  </td>
         </tr>
      </table>

    </td>
  </tr>
</table>




