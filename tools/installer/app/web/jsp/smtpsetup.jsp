<jsp:useBean id="webInstallerHelper" scope="page" class="net.project.util.installer.WebInstallerHelper" > 
<jsp:setProperty name="webInstallerHelper" property="realTomcatPath" value="<%= application.getRealPath("/") %>" /> 
</jsp:useBean>
<%
webInstallerHelper.log( "smtpsetup.jsp" );
String databaseHost = ""; 
String databaseName = ""; 
String dataFilePath = ""; 
String systemUser = ""; 
String systemPass = "";

if(webInstallerHelper.isCompletedInstaller())
	response.sendRedirect("Login.jsp");
else if (webInstallerHelper.getSMTPsetupForward() != null ) {
    request.getRequestDispatcher(webInstallerHelper.getSMTPsetupForward()).forward(request, response);
}else{
	databaseHost = request.getParameter("databasehost"); 
	databaseName = request.getParameter("databaseName"); 
	dataFilePath = request.getParameter("pnetDataFilePath"); 
	systemUser = request.getParameter("sysUsername"); 
	systemPass = request.getParameter("sysPassword");
	
    if(databaseHost != null && databaseName!= null && dataFilePath != null && systemUser != null && systemPass != null) {
    	webInstallerHelper.writePropertyFile("db.databasehost", databaseHost);
    	webInstallerHelper.writePropertyFile("db.databaseName", databaseName);
    	webInstallerHelper.writePropertyFile("db.sysUsername", systemUser);
    	webInstallerHelper.writePropertyFile("db.sysPassword", systemPass);
    	webInstallerHelper.writePropertyFile("db.oraclePath", dataFilePath);
    	webInstallerHelper.writePropertyFile("step.check.db.setup", "1");
    }
	
	boolean isNext = true;

	//To execute Database script using java.
	if(isNext && !webInstallerHelper.getDbScriptExeStatus()){
		if(!webInstallerHelper.initDbProperty()){
			System.out.println("Error_dbinitProp");
			request.setAttribute("errorMsg","Error occured while initializing dbinit property");	
			request.getRequestDispatcher("dbsetup.jsp").forward(request, response);
		}
		else if(!webInstallerHelper.initDatabase()){
			System.out.println("Error_dbinitExe");
			String errorMsg = webInstallerHelper.getErrorMessage();
			if(errorMsg != null || errorMsg != "")
				request.setAttribute("errorMsg",errorMsg);
			else
				request.setAttribute("errorMsg","Error occured while executing database script");	
			request.getRequestDispatcher("dbsetup.jsp").forward(request, response);
		}
	}
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
function doSubmit(){
	form = document.installForm;
	if(form.smtpHost.value == ''){
		alert('Please enter SMTP Host');
		form.smtpHost.focus();
		return false;
	}
	if(form.smtpPort.value == ''){
		alert('Please enter SMTP Port');
		form.smtpPort.focus();
		return false;
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
          <li class="selected">SMTP setup</li>
          <li>Tomcat setup</li>
          <li>Restart Tomcat</li>
          <li>Test configuration</li>
          <li>Finish</li>
        </ol></td>
      </tr>
      <tr>
        <td class="navlinks"><img src="img/sidebottom.gif" alt=" " width="185" height="20" border="0" /></td>
      </tr>
    </table></td>
    <td width="570" align="left" valign="top" bgcolor="#f5f5f5" style="padding-left:10px; padding-top:25px; padding-right:10px; padding-bottom:10px; "><h1><span style="color: #14809F">3</span> SMTP Setup </h1>
      <p>  </p>
      		  <form name="installForm" action="tomcatsetup.jsp" method="post">

		  <table width="100%" border="0" cellspacing="3" cellpadding="3">
		  <%if((String) request.getAttribute("errorMsg") != null) {%>
			<tr>		
				<td colspan="3" class="installErrorStyle">
					<%=(String) request.getAttribute("errorMsg")%>
				</td>
			</tr>
		  <%}%>
  <tr>
    <td width="3%" valign="top"><img src="img/mftico.gif" alt="bullet" width="12" height="10"></td>
    <td colspan="2"><h2>Configure SMTP Server</h2><br>
	This will configure tomcat to use your SMTP host to send emails.
				For this you have to provide following details  - </td>
  </tr>
  <tr>
    <td></td>
    <td width="37%" align="right">SMTP Host:</td>
    <td width="59%"><input type="text" name="smtpHost" value="<%=webInstallerHelper.getSmtpHost()%>"/></td>
  </tr>
  <tr>
    <td></td>
    <td width="37%" align="right">SMTP Port:</td>
    <td width="59%"><input type="text" name="smtpPort" value="25" value ="<%= webInstallerHelper.getSmtpPort()%>"/></td>
  </tr>
  <tr>
    <td></td>
    <td width="37%" align="right">SMTP Username:</td>
    <td width="59%"><input type="text" name="smtpUsername" value ="<%= webInstallerHelper.getSmtpUsername()%>"/> (optional)</td>
  </tr>
  <tr>
    <td></td>
    <td width="37%" align="right">SMTP Password:</td>
    <td width="59%"><input type="text" name="smtpPassword" value ="<%= webInstallerHelper.getSmtpPassword()%>"/> (optional)</td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td colspan="3" align="right"><button onClick="doSubmit(); return false;"> Next >></button></td>
  </tr>
  <tr valign="bottom"><td colspan="3"><!--See <a href="install/db/pnet_<%= databaseName == "" ? webInstallerHelper.getDatabaseName() : databaseName %>_db_build.log" target="_blank"> log</a> for detail of database script execution.--></td> </tr>
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