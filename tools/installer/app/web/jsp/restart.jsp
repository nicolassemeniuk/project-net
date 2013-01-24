<jsp:useBean id="webInstallerHelper" scope="page" class="net.project.util.installer.WebInstallerHelper" > 
<jsp:setProperty name="webInstallerHelper" property="realTomcatPath" value="<%= application.getRealPath("/") %>" /> 
</jsp:useBean>
<%
webInstallerHelper.log( "restart.jsp" );
if(webInstallerHelper.isCompletedInstaller()){
	response.sendRedirect("Login.jsp");
}else if(!webInstallerHelper.isCompletedContext()){
	if(!webInstallerHelper.changeContextXml()){
		request.setAttribute("errorMsg","Error occured while changing context.xml");	
		RequestDispatcher rd = request.getRequestDispatcher("tomcatsetup.jsp");
		rd.forward(request, response);
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
function doSubmit(arg){
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
          <li>Tomcat setup</li>
          <li class="selected">Restart Tomcat</li>
          <li>Test configuration</li>
          <li>Finish</li>
        </ol></td>
      </tr>
      <tr>
        <td class="navlinks"><img src="img/sidebottom.gif" alt=" " width="185" height="20" border="0" /></td>
      </tr>
    </table></td>
    <td width="570" align="left" valign="top" bgcolor="#f5f5f5" style="padding-left:10px; padding-top:25px; padding-right:10px; padding-bottom:10px; "><h1><span style="color: #14809F">5</span> Restart Tomcat </h1>
      <p> </p>
      		  <form name="installForm" action="testconf.jsp" method="post">
		  <table width="100%" border="0" cellspacing="3" cellpadding="3">
  <tr>
    <td width="3%" valign="top"><img src="img/mftico.gif" alt="bullet" width="12" height="10"></td>
    <td width="78%"><h2>Configuration file changes are applied to tomcat, and Tomcat is stopped automatically, You have to start it manually.</h2><br/>(it may be possible that tomcat will not stop automatically on your system)<br/><br/>
                        Please use <pre>startTomcat</pre> script available in installer folder to start tomcat.</td>
    <td width="19%"></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
  	<td width="3%">&nbsp;</td>
    <td align="right" width="78%">Yes, I have started tomcat&nbsp;&nbsp;&nbsp; <button onClick="doSubmit();"> Next >></button></td>
    <td width="19%">&nbsp;</td>
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
<% 
	//refreshing object and status.
	if(!webInstallerHelper.isCompletedRestart() && webInstallerHelper.isCompletedContext()) {
		webInstallerHelper.writePropertyFile("step.restart", "1");
		webInstallerHelper.stopTomcat();
	}
%>
