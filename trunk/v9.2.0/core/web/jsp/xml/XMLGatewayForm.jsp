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
    info="Login page"
    language="java"
    errorPage="/errors.jsp"
	import="net.project.security.*"
 %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<%
    if (!user.isApplicationAdministrator()) {
        throw new AuthorizationFailedException("Only application administrators may use this facility.", new net.project.admin.ApplicationSpace(net.project.admin.ApplicationSpace.DEFAULT_APPLICATION_SPACE_ID));
    }
%>
<html>
<head>
<title>Project.net Login</title>
<style>
table {font-family: Arial, Helvetica, sans-serif; font-size: 11pt;}
td { font-family: Arial, Helvetica, sans-serif; font-size: 11pt; font-style: normal; font-weight: normal;}
.loginHeader {font-size: 28pt; color: #888888; font-weight: bold; font-family: Verdana, Arial, Helvetica, sans-serif;}
.smallHeader {font-size: 12pt; font-family: Verdana, Arial, Helvetica, sans-serif; font-style: normal; font-weight: normal;}
.footnote {font-size: 10pt; font-family: Verdana, Arial, Helvetica, sans-serif; font-style: italic; font-weight: normal;}
</style>



</head>
<body class="normal11" bgcolor="#FFFFFF">
<form action="XMLGateway.jsp" method="POST">
	<p></p>
  <table border="0" align="center" bgcolor="#CCCCCC" cellspacing="0" cellpadding="2" width="75%">
    <tr> 
      <td width="3%">&nbsp;</td>
      <td colspan="2" valign="bottom" align="left"><span class="loginHeader">XML Gateway </span></td>
      <td align="right" valign="bottom" width="38%"><a href="http://www.project.net"><img src="/images/pnet_logo100_grey.gif" border="0"></a></td>
    </tr>
    <tr> 
      <td colspan="2" valign="bottom" align="left" nowrap>&nbsp;</td>
      <td width="47%" valign="bottom" align="left" nowrap>
	  	<span class="smallHeader">Welcome to Project.net</span>
	  </td>
      <td width="38%" valign="bottom" align="left" nowrap>&nbsp;</td>
    </tr>
    <tr> 
      <td colspan="4">&nbsp;</td>
    </tr>
    <tr> 
      <td colspan="4">&nbsp;</td>
    </tr>
    <tr> 
      <td width="3%">&nbsp;</td>
      <td align="right" valign="middle" width="12%" nowrap>Object ID:</td>
      <td width="47%"> 
        <input name="ID" type="text" value="" size="20" onFocus="this.select();">
        <input type="submit" value="Get XML" name="xml">
      </td>
      <td align="left" valign="middle" width="38%">&nbsp; </td>
    </tr>
    <tr> 
      <td width="3%">&nbsp;</td>
      <td width="12%">&nbsp;</td>
      <td width="47%">&nbsp; </td>
      <td align="left" valign="middle" width="38%"> &nbsp;&nbsp; </td>
    </tr>
    <tr> 
      <td align="center" width="3%">&nbsp;</td>
      <td colspan="3" align="left"></td>
    </tr>
    <tr> 
      <td align="center" width="3%">&nbsp;</td>
      <td colspan="3" align="left">Peer Applications must authenticate first.</td>
    </tr>
    <tr> 
      <td colspan="3" align="right">&nbsp;</td>
      <td align="right" width="38%">M8, 3/8/2000</td>
    </tr>
  </table>
	
	</form>
</body>
</html>

 
 
