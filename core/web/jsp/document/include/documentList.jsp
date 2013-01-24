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
|   $RCSfile$
|   $Revision: 19652 $
|   $Date: 2009-08-08 12:26:34 -0300 (sáb, 08 ago 2009) $
|   $Author: dpatil $
|
|--------------------------------------------------------------------%>
<%@ page 
    contentType="text/html; charset=UTF-8"
    info="Document List" 
    language="java" 
    errorPage="../DocumentErrorHandler.jsp"
    import="net.project.document.*, 
    net.project.security.*,
    net.project.project.*,
    net.project.base.property.PropertyProvider,
    java.util.Date"

%>
<%@ include file="/base/taglibInclude.jsp" %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" /> 
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />


<%
// no security check necessary since this only displays docs checked out by current user
// and therefore the user has access to those documents
        
docManager.setUser (user);
Date currentDate = new Date();
String timeZoneHeader = user.getDateFormatter().formatDate(currentDate, "z");
%>


        <table border="0" width="100%" cellpadding="0" cellspacing="0" name="tableWithEvenRows">

	 <tr align="left"> 

	     <td colspan="2" align="left" class="table-header"><display:get name="prm.document.documentlist.name.label" />
            </td>

	     <td align="left" class="table-header"><display:get name="prm.document.documentlist.space.label" />
            </td>
            
        <!-- <td align="left" class="table-header"><display:get name="prm.document.documentlist.format.label" />
            </td>

	    <td align="left" class="table-header"><display:get name="prm.document.documentlist.status.label" />
            </td> -->

	    <td align="left" class="table-header"><display:get name="prm.document.documentlist.checkoutdate.label" />&nbsp;&nbsp;(<%=timeZoneHeader %>)</td>

	    <!-- <td align="left" class="table-header"><display:get name="prm.document.documentlist.returndate.label" />
            </td> -->

	 </tr>
	<!-- tr class="tableLine">
		<td  colspan="7" class="tableLine"><img src="<%=SessionManager.getJSPRootURL()%>/images/spacers/trans.gif" width="1" height="2" border="0" alt=""/></td>
	</tr -->
	
	    <%-- Apply stylesheet to format document list--%>
		<jsp:setProperty name="docManager" property="stylesheet" value="/document/xsl/document-lists.xsl" /> 
		<jsp:getProperty name="docManager" property="docsCheckedOutByUserPresentation" /> 

</table>


