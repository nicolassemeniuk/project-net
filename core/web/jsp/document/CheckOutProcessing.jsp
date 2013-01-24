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
    info="Process Document Check Out"
    language="java"
    errorPage="/errors.jsp"
    import="net.project.document.*,
			net.project.base.property.PropertyProvider,
		    net.project.security.*,
		    net.project.util.*,
		    java.util.*,
		    org.apache.commons.lang.time.DateUtils,
		    org.apache.commons.lang.StringUtils,
		    net.project.base.Module,
            java.net.URLEncoder"
 %>

<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" />
<jsp:useBean id="securityProvider" class="net.project.security.SecurityProvider" scope="session" />
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="errorReporter" class="net.project.util.ErrorReporter" scope="session"/>

<%
    // security check done in docmanager
    Document documentBean = new Document();

    documentBean.setID( docManager.getCurrentObjectID() );
	documentBean.setUser ( docManager.getUser() );
    documentBean.load();
    pageContext.setAttribute("document", documentBean, PageContext.PAGE_SCOPE);
%>
<jsp:useBean id="document" type="net.project.document.Document" scope="page" />

<%-- Get the form fields --%>
<jsp:setProperty name="document" property="notes" param="notes" />

<%-- Store to the database --%>
<%
    String ckoReturn = request.getParameter("ckoReturn");
    String todayString = user.getDateFormatter().formatDate(new Date(), user.getDateFormat());
    if ( StringUtils.isEmpty(ckoReturn) ) {
        errorReporter.addError(new ErrorDescription(
            "ckoReturn",
            PropertyProvider.get("prm.checkout.returndate.error.blank.message", new Object[] { ckoReturn } )
        ));    	
    } else if (!StringUtils.isEmpty(ckoReturn) && !user.getDateFormatter().isLegalDate(ckoReturn)) {
        errorReporter.addError(new ErrorDescription(
            "ckoReturn",
            PropertyProvider.get("prm.checkout.returndate.error.cannotparse.message", new Object[] { ckoReturn } )
        ));
    } else if (user.getDateFormatter().parseDateString(ckoReturn).compareTo( user.getDateFormatter().parseDateString(todayString) ) < 0) {
    		   errorReporter.addError(new ErrorDescription( "ckoReturn",
	            PropertyProvider.get("prm.checkout.returndate.error.estreturndateinpast.message", new Object[] { ckoReturn } )
 			       ));
		   } else {
		        document.setCkoReturn ( request.getParameter("ckoReturn") );
		   }

    //If any errors have been found, terminate the processing of the page here.
    if (errorReporter.errorsFound()) {
        %>
        <jsp:forward page="CheckOut.jsp">
            <jsp:param name="notes" value='<%=request.getParameter("notes")%>'/>
        </jsp:forward>
        <%
    }

    docManager.checkOutDocument (document);

    String forwardingPage = SessionManager.getJSPRootURL() + (String)docManager.getNavigator().get("TopContainer") + "&module=" + Module.DOCUMENT;

    if ((forwardingPage == null))
       forwardingPage = SessionManager.getJSPRootURL() + "/document/Main.jsp?module="+Module.DOCUMENT;
%>


<html>
<head>
<title>Close Window</title>
<script language="javascript">

function finish () {

   var targetWindow;

   if (parent.opener.name == "main")
      targetWindow = parent.opener;
   else
      targetWindow = parent.opener.parent;


    targetWindow.location = "<%=forwardingPage%>";
	targetWindow.focus();
	parent.close();
}
</script>
</head>
<body onLoad="finish()">


</body>
</html>


