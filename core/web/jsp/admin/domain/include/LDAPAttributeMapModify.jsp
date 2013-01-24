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
    info="LDAP Attribute Map Modify" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.admin.ApplicationSpace,
            net.project.security.SessionManager,
            net.project.base.directory.ldap.LDAPAttributeMapEditor"
%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="attributeMapEditor" class="net.project.base.directory.ldap.LDAPAttributeMapEditor" scope="request" /> 

<%------------------------------------------------------------------------
  -- Security Verification
  ----------------------------------------------------------------------%>

<security:checkSpaceAccess userID="<%=user.getID()%>" spaceID="<%=ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID%>" action="modify"/>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td class="tableHeader"><display:get name="prm.application.domain.directory.ldap.attributemapping.profileproperty" /></td>
        <td class="tableHeader"><display:get name="prm.application.domain.directory.ldap.attributemapping.ldapattribute" /></td>
        <td class="tableHeader"><display:get name="prm.application.domain.directory.ldap.attributemapping.ldapattributevaluenumber" /></td>
    </tr>
    <tr class="tableLine">
	    <td colspan="3" class="tableLine"><img src="<%=SessionManager.getJSPRootURL()%>/images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
    </tr>

<%
    int count = 0;
    for (java.util.Iterator it = attributeMapEditor.getMapElements().iterator(); it.hasNext(); count++) {
        LDAPAttributeMapEditor.MapElement nextElement = (LDAPAttributeMapEditor.MapElement) it.next();
%>
    <tr>
        <td class="tableContent">
            <input type="hidden" name="<%="profileAttributeID_" + count%>" size="20" value="<%=nextElement.getProfileAttributeID()%>"/>
<%      if (nextElement.isRequired()) { %>
                <span id="<%=nextElement.getProfileAttributeID()%>" class="fieldRequired">
<%      } else { %>
                <span id="<%=nextElement.getProfileAttributeID()%>" class="fieldNonRequired">
<%      } %>
                    <%=nextElement.getProfileAttributeDisplayName()%>:
                </span>
        </td>
<%      if (attributeMapEditor.isLDAPAttributeIDListAvailable()) { %>
        <%-- Provide a dropdown listbox containing available attributes 
             (including the current value, if its not actually part of the list) --%>
        <td class="tableContent">
            <select name="<%="ldapAttributeName_" + count%>">
                <option value=""></option>
                <%=attributeMapEditor.getLDAPAttributeIDOptionList(nextElement.getLDAPAttributeName())%>
            </select>
        </td>
<%      } else { %>
        <%-- Provide a textbox containing the current value --%>
        <td class="tableContent">
            <input type="text" size="20" name="<%="ldapAttributeName_" + count%>" value="<%=nextElement.getLDAPAttributeName()%>">
        </td>
<%      } %>
        <td class="tableContent">
            <input type="text" name="<%="valueIndex_" + count%>" size="5" maxlength="3" value="<%=nextElement.getValueIndex()%>" >
        </td>
    </tr>
<%  } // end for %>

</table>

