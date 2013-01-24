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

<%
/*----------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 20781 $
|       $Date: 2010-04-30 11:19:21 -0300 (vie, 30 abr 2010) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Step Indicator" 
    language="java" 
    errorPage="../../WorkflowErrors.jsp"
    import="net.project.workflow.*, 
			net.project.security.*,
			net.project.space.Space"
%>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="envelopeManagerBean" class="net.project.workflow.EnvelopeManagerBean" scope="session" />

<div class="envelopePropertiesStepDiv">
	<jsp:setProperty name="envelopeManagerBean" property="stylesheet" value="/workflow/envelope/include/xsl/step_indicator.xsl" />
	<jsp:getProperty name="envelopeManagerBean" property="stepIndicatorPresentation" />
</div>
