<jsp:useBean id="webInstallerHelper" scope="page" class="net.project.util.installer.WebInstallerHelper" > 
<jsp:setProperty name="webInstallerHelper" property="realTomcatPath" value="<%= application.getRealPath("/") %>" /> 
</jsp:useBean>
<%
webInstallerHelper.log( "tomcatsetup.jsp" );
if(webInstallerHelper.isCompletedInstaller()){
	response.sendRedirect("Login.jsp");
}else if (!webInstallerHelper.isCompletedSmtpSetup()){
	String smtpHost = request.getParameter("smtpHost");
	String smtpPort = request.getParameter("smtpPort");
	String smtpUsername = request.getParameter("smtpUsername");
	String smtpPassword = request.getParameter("smtpPassword");

	if(smtpUsername == null) smtpUsername = "";
	if(smtpPassword == null) smtpPassword = "";
	
	if(smtpHost!=null && smtpPort != null && smtpUsername!=null && smtpPassword!=null){
		webInstallerHelper.writePropertyFile("smtp.smtpHost" , smtpHost);
		webInstallerHelper.writePropertyFile("smtp.smtpPort" , smtpPort);
		webInstallerHelper.writePropertyFile("smtp.smtpUsername" , smtpUsername);
		webInstallerHelper.writePropertyFile("smtp.smtpPassword" , smtpPassword);
		webInstallerHelper.writePropertyFile("step.check.smtp.setup" , "1");
	}else{
		request.setAttribute("errorMsg","Error occured in SMTP setup, SMTP Host or port is null.");	
		request.getRequestDispatcher("smtpsetup.jsp").forward(request, response);;
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
          <li class="selected">Tomcat setup</li>
          <li>Restart Tomcat</li>
          <li>Test configuration</li>
          <li>Finish</li>
        </ol></td>
      </tr>
      <tr>
        <td class="navlinks"><img src="img/sidebottom.gif" alt=" " width="185" height="20" border="0" /></td>
      </tr>
    </table></td>
    <td width="570" align="left" valign="top" bgcolor="#f5f5f5" style="padding-left:10px; padding-top:25px; padding-right:10px; padding-bottom:10px; "><h1><span style="color: #14809F">4</span> Tomcat Setup </h1>
      <p> </p>
      		  <form name="installForm" action="restart.jsp" method="post">
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
    <td width="78%"><h2>Change Context.xml file</h2><br>
	This will overwrite the context.xml file from <%=  getServletContext().getRealPath("/").substring(0, getServletContext().getRealPath("/").indexOf("webapps")) %>conf<br>
				with the respect to Database and SMTP information provided above.</td>
    <td width="19%"></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td colspan="3" align="right"><button onClick="doSubmit();"> Next >></button></td>
  </tr>
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