<jsp:useBean id="webInstallerHelper" scope="page" class="net.project.util.installer.WebInstallerHelper" > 
<jsp:setProperty name="webInstallerHelper" property="realTomcatPath" value="<%= application.getRealPath("/") %>" /> 
</jsp:useBean>
<%
webInstallerHelper.log( "testconf.jsp" );
//Error message 
String errorType = request.getParameter("errorType");
if(errorType == null) errorType = "";

if(webInstallerHelper.isCompletedInstaller()){
	response.sendRedirect("Login.jsp");
}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Project.net Installer</title>
<LINK REL='SHORTCUT ICON' HREF='/images/favicon.ico'>
<link href="styles/installer.css" rel="stylesheet" type="text/css" />
<SCRIPT TYPE="text/javascript">
      if (top.frames.length!=0)
            top.location=self.document.location;
</SCRIPT>
<script>
function doSubmit(arg){
	form = document.installForm;
	if(arg == 'mail'){
		if(form.userEmail.value == ''){
			alert("Please enter the test email");
			form.userEmail.focus();
			return false;
		}
		form.action = "testing.jsp?p=m";
	}
	if(arg == 'db'){
		form.action = "testing.jsp?p=d";
	}
	if(arg == 'updb'){
		form.action = "testing.jsp?p=ud";
	}
	if(arg == 'upsmtp'){
		form.action = "testing.jsp?p=um";
	}
	if(arg == ''){
		form.action = "finish.jsp?p=d";
	}
	form.submit();
}
</script>
</head>

<body>
<table width="755" height="100%" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td height="140" colspan="2" align="left" valign="top" background="img/topbanner.gif"><img src="img/toplogo.gif" width="474" height="140" /></td>
  </tr>
  <tr>
    <td width="185" align="left" valign="top" bgcolor="#EDEDED"><table width="185" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td width="185" background="img/sidetop.gif"></td>
      </tr>
      <tr>
        <td width="185" height="23" background="img/sidetop.gif" style="background-repeat:no-repeat; background-position:bottom">&nbsp;</td>
      </tr>
      <tr>
        <td class="navlinks"><ol class="setsteps">
            <li>Basic setup</li>
          <li>Database setup</li>
          <li>SMTP setup</li>
          <li>Tomcat setup</li>
          <li>Restart Tomcat</li>
          <li class="selected">Test configuration</li>
          <li>Finish</li>
        </ol></td>
      </tr>
      <tr>
        <td class="navlinks"><img src="img/sidebottom.gif" alt=" " width="185" height="20" border="0" /></td>
      </tr>
    </table></td>
    <td width="570" align="left" valign="top" bgcolor="#f5f5f5" style="padding-left:10px; padding-top:25px; padding-right:10px; padding-bottom:10px; "><h1><span style="color: #14809F">6</span> Test configuration </h1>
      <p>  </p>
      		  <form name="installForm" method="post">
		  <table width="100%" border="0" cellspacing="3" cellpadding="3">
		  <%if((String) request.getAttribute("errorMsg") != null) {%>
			<tr>
				<td colspan="3" class="installErrorStyle">
					<%=(String) request.getAttribute("errorMsg")%>
				</td>
			</tr>
		  <tr>
			<td colspan="3" >Make sure following information is correct, which is provided by you.</td>
		  <tr>
		  <%} %>
		  
		  <%if( errorType.equals("1") ){%>
			<tr>
				<td colspan="3">
				<table align= "center" width="95%">
				<tr>
					<td align="left">Database Host:</td>
					<td align="left" class="installErrorStyle"><%=webInstallerHelper.getDatabaseHost() %></td>
				</tr>
				<tr>
					<td align="left">Database Name:</td>
					<td align="left" class="installErrorStyle"><%=webInstallerHelper.getDatabaseName() %></td>
				</tr>
				<tr>
				    <td align="left">Oracle's oradata path:</td>
				    <td align="left" class="installErrorStyle"><%=webInstallerHelper.getOraclePath()%></td>
				</tr>
				<tr>
					<td align="left">Database System Username:</td>
					<td align="left" class="installErrorStyle"><%=webInstallerHelper.getSystemUser() %></td>
				</tr>
				<tr>
					<td align="left">Databse System Password:</td>
					<td align="left" class="installErrorStyle"><%= webInstallerHelper.getSystemPass() %></td>
				</tr>
				
				</table>
				</td>
			</tr>	
			<tr>
				<td align="right" colspan="3">
					<button onClick="doSubmit('updb'); return false;">Update Info</button>
				</td>
			</tr>
			<%} else if(errorType.equals("2")){%>
			<tr>
				<td colspan="3">
					<table align="center" width="95%" >
					<tr>
					 <tr>
						<td align="left">SMTP Host:</td>
						<td align="left" colspan="2" class="installErrorStyle"><%=webInstallerHelper.getSmtpHost()%></td>
					  </tr>
					  <tr>
						<td align="left">SMTP Port:</td>
						<td align="left" colspan="2" class="installErrorStyle"><%= webInstallerHelper.getSmtpPort()%></td>
					  </tr>
					  <tr>
						<td align="left">SMTP Username:</td>
						<td align="left" colspan="2" class="installErrorStyle"><%= webInstallerHelper.getSmtpUsername()%></td>
					  </tr>
					  <tr>
						<td align="left">SMTP Password:</td>
						<td align="left" colspan="2" class="installErrorStyle"><%= webInstallerHelper.getSmtpPassword()%></td>
					  </tr>
					</table>
					</td>
				<tr>
				<tr>
					<td align="right"  colspan="3">
						<button onClick="doSubmit('upsmtp'); return false;">Update Info</button>
					</td>
				</tr>

	<%}%>

		 
  <tr>
    <td width="3%" valign="top"><img src="img/mftico.gif" alt="bullet" width="11" height="11"></td>
    <td width="78%"><h2>Test JDBC connection</h2><br>
	This will check for JDBC connection if database is configured and created correctly</td>
    <td width="19%"><% if(webInstallerHelper.isCompletedDatabaseConnectionTest()){%>
				<font color="green"><b> Test successful</b></font>
				<%} else {%><button onClick="doSubmit('db');">Test DB</button>
				<%}%></td>
  </tr>
  <tr>
    <td valign="top"><img src="img/mftico.gif" alt="bullet" width="11" height="11"></td>
    <td><h2>Test Email</h2><br>
	This will send email to the user's email address given below for testing SMTP server<br>
	configured correctly or not, using SMTP detail information provided by user in previous step :
	<br><i style="color: #660099;">Configure SMTP Server</i><br>Enter email address to test: <input type="text" name="userEmail" /></td>
    <td><!--% if(webInstallerHelper.isCompletedMailConnectionTest()){% >
				<font color="green"><b> Test successful</b></font>
				< % } else {%--><button onClick="doSubmit('mail'); return false;">Test Email</button>
				<!--%}%--></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <% if(webInstallerHelper.isCompletedConfigurationTest()) { %>
	<tr>
		<td colspan="3" align="right"><button onClick="doSubmit('');">Finish >></button></td>
	</tr>
  <% } %>
</table>
		</form>
		  <a href="http://forum.project.net/"><div id="helplink">Forum</div></a> <a href="http://forum.project.net/"><div id="helplink">Manual</div></a>
			  </td>
  </tr>
  <tr>
    <td class="basestyle" height="85" colspan="2" background="img/basebott.gif"><div align="center">&copy; Copyright 2000-2008 <a href="http://www.project.net">Project.net Inc.</a> All Rights Reserved.<br />
    <a href="http://dev.project.net/licenses/PPL1.0">Licence agreement</a>.</div></td>
  </tr>
</table>
</body>
</html>