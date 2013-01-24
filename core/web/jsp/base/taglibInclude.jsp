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

<%-- Commonly used taglibs --%>
<%@ taglib uri="/WEB-INF/taglibs/templateTags.tld" prefix="template" %>
<%@ taglib uri="/WEB-INF/taglibs/sessionTags.tld" prefix="session" %>
<%@ taglib uri="/WEB-INF/taglibs/securityTags.tld" prefix="security" %>
<%@ taglib uri="/WEB-INF/taglibs/toolbarTags.tld" prefix="tb" %>
<%@ taglib uri="/WEB-INF/taglibs/historyTags.tld" prefix="history" %>
<%@ taglib uri="/WEB-INF/taglibs/tabTags.tld" prefix="tab" %>
<%@ taglib uri="/WEB-INF/taglibs/utilTags.tld" prefix="util" %>
<%@ taglib uri="/WEB-INF/taglibs/displayTags.tld" prefix="display"%>
<%@ taglib uri="/WEB-INF/taglibs/channelTags.tld" prefix="channel"%>
<%@ taglib uri="/WEB-INF/taglibs/linksTags.tld" prefix="links"%>
<%@ taglib uri="/WEB-INF/taglibs/xmlTags.tld" prefix="pnet-xml"%>
<%@ taglib uri="/WEB-INF/taglibs/navbarTags.tld" prefix="navbar"%>
<%@ taglib uri="/WEB-INF/taglibs/refererTags.tld" prefix="referer"%>
<%@ taglib uri="/WEB-INF/taglibs/inputTags.tld" prefix="input"%>
<%@ taglib uri="/WEB-INF/taglibs/pagerTags.tld" prefix="pg"%>
<%@ taglib uri="/WEB-INF/taglibs/searchLetterTags.tld" prefix="search"%>
<%@ taglib uri="/WEB-INF/taglibs/outputTags.tld" prefix="output"%>
<%@ taglib uri="/WEB-INF/taglibs/errorTags.tld" prefix="errors"%>
<%@ taglib uri="/WEB-INF/taglibs/dropDownTags.tld" prefix="dropDown"%>
<%@ taglib uri="/WEB-INF/taglibs/propertyProviderTags.tld" prefix="propertyProvider"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<jsp:useBean id="pageTimingBean" class="net.project.util.PageTimingBean" scope="request" />
