<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
    
    <table>
      <tr>
        <td>
           User activity
        </td>
        <td>
         <a href="<c:url value="/userActivity?view=attachment"/>">Download</a>
        </td>
        <td>
          <a href="<c:url value="/userActivity?view=inline"/>">View</a>
        </td>        
      </tr>
      <tr>
        <td>
           Project activity
        </td>
        <td>
         <a href="<c:url value="/projectActivity?view=attachment"/>">Download</a>
        </td>
        <td>
          <a href="<c:url value="/projectActivity?view=inline"/>">View</a>
        </td>        
      </tr>      
    </table>
    
</body>
</html>