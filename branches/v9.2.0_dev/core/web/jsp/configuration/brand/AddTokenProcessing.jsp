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
    info="Token Edit Processing"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.brand.BrandManager,
	net.project.base.property.BrandGlossary,
	net.project.base.property.PropertyProvider,
	net.project.base.property.Token,
	net.project.security.SessionManager,
	java.util.Enumeration,
            net.project.base.property.TokenChangeManager,
            net.project.base.RecordStatus"
 %>

<jsp:useBean id="addBrand" class="net.project.brand.BrandManager" scope="session"/>

<%
    BrandGlossary glossary = new BrandGlossary (addBrand.getID(), addBrand.getActiveLanguage());
    glossary.load();

    Token token = new Token(addBrand.getID(), request.getParameter("tokenName"),
            request.getParameter("currentBrandValue"), request.getParameter("propertyType"),
            addBrand.getActiveLanguage(), RecordStatus.ACTIVE, false,
            net.project.util.Conversion.toBoolean(request.getParameter("isTranslatableProperty")));

    token.setIsNewToken();

    glossary.addToken (token, request.getParameter("defaultBrandValue"));

    // Token has been added
    // Flag that context has changed
    // This will cause the tokens for the context to be reloaded the next time
    // someone logs in
    TokenChangeManager.flagTokenChanged(addBrand.getID(), addBrand.getActiveLanguage());

    String goBack = request.getParameter("goBack");

    if (goBack != null && goBack.equals("true"))
        pageContext.forward ("Back.jsp");
    else if (request.getParameter("theAction").equals("addAnotherToken"))
        response.sendRedirect ("AddToken.jsp?contextID="+addBrand.getID()+"&language="+addBrand.getID()+"&refresh=false");
    else
        response.sendRedirect ("BrandTokenEdit.jsp?brandID="+addBrand.getID()+"&language="+addBrand.getRequestedLanguage());

%>


