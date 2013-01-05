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
    import="net.project.base.property.BrandGlossary,
            net.project.base.property.PropertiesFilter,
            net.project.security.SessionManager,
            java.util.Enumeration,
            net.project.util.HTMLUtils,
            net.project.base.property.TokenChangeManager,
            net.project.base.property.PropertyProvider"
 %>

<jsp:useBean id="brandTokenEditFilter" class="net.project.base.property.PropertiesFilter" scope="session" />
<jsp:useBean id="versionCheck" scope="application" class="net.project.versioncheck.VersionCheck"/>

<%     String brandID = request.getParameter("brandID");
    String currentLanguage = request.getParameter("currentLanguage");
    String numberOfTokens = request.getParameter("numberOfTokens");
%>

<%
    brandTokenEditFilter.setName ( request.getParameter ("filterTokenName") );
    brandTokenEditFilter.setValue ( request.getParameter ("filterTokenValue") );
    brandTokenEditFilter.setType ( request.getParameter ("filterPropertyType") );
    brandTokenEditFilter.setFilterCategories ( request.getParameterValues ("filterCategories") );
%>

<%

    if (request.getParameter("theAction").equals("editToken")) {

        BrandGlossary glossary = new BrandGlossary ( brandID, currentLanguage );
        glossary.load();

        String TOKEN = "token::";
        String TYPE = "::type";
        String SYSTEM_PROPERTY = "::systemProperty";
        String TRANSLATABLE_PROPERTY = "::translatableProperty";

        Enumeration keys = null;

        for (keys = request.getParameterNames(); keys.hasMoreElements() ;) {

            String key = (String) keys.nextElement();

            if (key.startsWith(TOKEN)) {

                String tokenName = key.substring ( TOKEN.length() );
                String tokenType = request.getParameter (tokenName + TYPE);
                boolean isSystemProperty = net.project.util.Conversion.toBoolean ( request.getParameter (tokenName + SYSTEM_PROPERTY) );
                boolean isTranslatableProperty = net.project.util.Conversion.toBoolean ( request.getParameter (tokenName + TRANSLATABLE_PROPERTY) );
//              Avinash: bfd - 3148 checking for empty values
				if(tokenName == null ||tokenName.trim().length()<=0 || tokenType == null || tokenType.trim().length()<=0)
					response.sendRedirect (response.encodeRedirectURL("BrandTokenEdit.jsp?brandID="+brandID+"&language="+currentLanguage+ "&numberOfTokens=" + numberOfTokens+ "&message=prm.admin.configuration.editTokens.emptyTokenProperties.error.message"));
                glossary.set (tokenName, request.getParameter (key), tokenType, brandID, currentLanguage, isSystemProperty, isTranslatableProperty);
            }

        }

        glossary.store();

        // Token has been added
        // Flag that context has changed
        // This will cause the tokens for the context to be reloaded the next time
        // someone logs in
        TokenChangeManager.flagTokenChanged(brandID, currentLanguage);
		if("true".equals(request.getParameter("versionCheck"))){
			PropertyProvider.forceReloadTokenCache(application);
			versionCheck.setVersionCheckEnabled("1".equals(request.getParameter("token::prm.versioncheck.isenabled")));
			response.sendRedirect ("../../admin/utilities/BuildInfo.jsp?module=240");
		}else{
        	response.sendRedirect ("BrandTokenEdit.jsp?brandID="+brandID+"&language="+currentLanguage+"&numberOfTokens=" + numberOfTokens);
		}
    }

    else if (request.getParameter("theAction").equals("addToken"))
        response.sendRedirect ("AddToken.jsp?contextID="+brandID+"&language="+currentLanguage+"&refresh=true"+"&numberOfTokens=" + numberOfTokens);

    else if (request.getParameter("theAction").equals("reloadApplicationCache")) {

        PropertyProvider.forceReloadTokenCache(application);
        response.sendRedirect ("BrandTokenEdit.jsp?brandID="+brandID+"&language="+currentLanguage+"&numberOfTokens=" + numberOfTokens);
    }

    else if (request.getParameter("theAction").equals("filter")) {
        response.sendRedirect ("BrandTokenEdit.jsp?brandID="+brandID+"&language="+currentLanguage+"&numberOfTokens=" + numberOfTokens);
    }

%>


