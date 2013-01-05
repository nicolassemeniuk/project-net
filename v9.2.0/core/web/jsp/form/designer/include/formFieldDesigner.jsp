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
    info="Form Designer - Field Information"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.form.FormElementList,
            net.project.form.FormFieldDesigner"
%>
<jsp:useBean id="formFieldDesigner" type="net.project.form.FormField" scope="session" />
<jsp:useBean id="fieldPropertySheet" type="net.project.form.property.IPropertySheet" scope="session"/>
<jsp:useBean id="formElementList" class="net.project.form.FormElementList" scope="request" />

<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr>
    <td align="left" class="tableHeader">
      <%=PropertyProvider.get("prm.form.designer.fieldedit.type.label")%>
      <% if (formFieldDesigner.dataColumnExists())
           out.print(formFieldDesigner.getElementLabel());
         else { %>
            <select name="ElementID" onChange="changeSheet();">
            <%=formElementList.getHtmlOptionList(fieldPropertySheet.getID())%>
            </select>
      <% } %>
    </td>
</tr>
<tr><td>&nbsp;</td></tr>
<tr><td><% fieldPropertySheet.writeHtml(new java.io.PrintWriter(out)); %></td></tr>
</table>


    
