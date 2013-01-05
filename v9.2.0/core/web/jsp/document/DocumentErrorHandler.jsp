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

<%@ page info="Document Error Processing Page"
    contentType="text/html; charset=UTF-8"
    isErrorPage="true"
    import="net.project.document.*,
    net.project.util.*,
    net.project.security.*,
    net.project.base.PnetExceptionTypes"
%>
<jsp:useBean id="docManager" class="net.project.document.DocumentManagerBean" scope="session" /> 

<html>
<body class="main" bgcolor="#FFFFFF">

<pre>
<%    

      request.setAttribute("module", Integer.toString(net.project.base.Module.DOCUMENT));
      request.setAttribute("action", Integer.toString(net.project.security.Action.VIEW));
      request.setAttribute("id", docManager.getCurrentObjectID());
      if (exception instanceof UserIsNullException) {

         request.setAttribute("SaSecurityError", ( (UserIsNullException) exception).getDisplayError());
         pageContext.forward("../Login.jsp"); 

         }

      else if (exception instanceof UndoCheckOutFailedException) {

         switch  ( ((UndoCheckOutFailedException) exception).getReasonCode() ) {
         
            case PnetExceptionTypes.UNDO_CHECK_OUT_FAILED_NOT_CKO_BY_USER: 
               {
                    session.putValue("exceptionDisplayMessage", ((UndoCheckOutFailedException) exception).getDisplayError());
                    pageContext.forward("ErrorUndoCheckOutWrongUser.jsp"); 
                    break;
               }   
            case PnetExceptionTypes.UNDO_CHECK_OUT_FAILED_NOT_CHECKED_OUT: 
               {
                    session.putValue("exceptionDisplayMessage", ((UndoCheckOutFailedException) exception).getDisplayError());
                    pageContext.forward("ErrorCheckInOutGeneral.jsp"); 
                    break;
               }   
            default:
                   pageContext.forward("../Login.jsp");

         } // switch

    } // UndoCheckOutFailedException

      else if (exception instanceof CheckInFailedException) {

         switch  ( ((CheckInFailedException) exception).getReasonCode() ) {
         
            case PnetExceptionTypes.CHECK_IN_FAILED_NOT_CKO_BY_USER: 
               {
                    session.putValue("exceptionDisplayMessage", ((CheckInFailedException) exception).getDisplayError());
                    pageContext.forward("ErrorCheckInByWrongUser.jsp"); 
                    break;
               }   
            case PnetExceptionTypes.CHECK_IN_FAILED_NOT_CHECKED_OUT: 
               {
                    session.putValue("exceptionDisplayMessage", ((CheckInFailedException) exception).getDisplayError());
                    pageContext.forward("ErrorCheckInOutGeneral.jsp"); 
                    break;
               }   
            default:
                   pageContext.forward("../Login.jsp");

         }
   }   // end CheckInFailedException

      else if (exception instanceof CheckOutFailedException) {

         switch  ( ((CheckOutFailedException) exception).getReasonCode() ) {
         
            case PnetExceptionTypes.CHECK_OUT_FAILED_ALREADY_CHECKED_OUT:
               {
                    session.putValue("exceptionDisplayMessage", ((CheckOutFailedException) exception).getDisplayError());
                    pageContext.forward("ErrorCheckOutAlreadyCKO.jsp");
                    break;
               }   
            default:
                   pageContext.forward("Error.html");

         }      
   }  // end CheckInFailedException
      else {
         pageContext.forward("/errors.jsp");
    }
   

%>

</pre>
<br clear=all>
<%@ include file="/help/include_outside/footer.jsp" %>

</body>
</html>                                                                                      
