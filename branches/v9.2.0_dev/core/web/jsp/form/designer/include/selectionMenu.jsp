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
    import="java.util.ArrayList,
            java.util.Iterator,
            net.project.form.FieldDomain,
            net.project.form.FieldDomainValue,
            net.project.form.FormFieldDesigner"
%>
<jsp:useBean id="formFieldDesigner" class="net.project.form.FormFieldDesigner" scope="session" />

<%@ include file="/base/taglibInclude.jsp"%>

<%
    FieldDomain domain = formFieldDesigner.getDomain();

    //If there isn't a domain yet, create a dummy one to satisfy the xsl transformer
    if (domain == null) {
        domain = new FieldDomain();
    }

    pageContext.removeAttribute("domain");
    pageContext.setAttribute("domain", domain, PageContext.PAGE_SCOPE);
%>

<pnet-xml:transform name="domain" scope="page" stylesheet="/form/designer/include/xsl/SelectionMenu.xsl">
    <pnet-xml:property name="showAddElement" value="true"/>
</pnet-xml:transform>
