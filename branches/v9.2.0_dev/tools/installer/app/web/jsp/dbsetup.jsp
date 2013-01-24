<jsp:useBean id="webInstallerHelper" scope="page" class="net.project.util.installer.WebInstallerHelper" > 
<jsp:setProperty name="webInstallerHelper" property="realTomcatPath" value="<%= application.getRealPath("/") %>" /> 
</jsp:useBean>
<%
webInstallerHelper.log( "dbsetup.jsp" );
if(webInstallerHelper.isCompletedInstaller())
	response.sendRedirect("Login.jsp");
else if (webInstallerHelper.getDbsetupForward() != null ) {
    request.getRequestDispatcher(webInstallerHelper.getDbsetupForward()).forward(request, response);
}else{
	boolean isNext = true;
	if(isNext && !webInstallerHelper.isCompletedJdbcJarCopy()){
		if(!webInstallerHelper.copyJDBCjars()){
			isNext = false;
			request.setAttribute("errorMsg","Error occured while copying ojdbc14.jar file.");
			RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
			rd.forward(request, response);	
		}
	}
	if(isNext && !webInstallerHelper.isCompletedEndorsedJarCopy()){
		if(!webInstallerHelper.copyEndorsedjars()){
			isNext = false;
			request.setAttribute("errorMsg","Error occured while copying endoresed dir contents.");
			RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
			rd.forward(request, response);
		}
	}
	if(isNext && !webInstallerHelper.isCompletedMailJarCopy()){
		if(!webInstallerHelper.copyMailjars()) {
			isNext = false;
			request.setAttribute("errorMsg","Error occured while copying activation or mail jar  file.");
			RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
			rd.forward(request, response);
		}
	}
	if(isNext && !webInstallerHelper.isCompletedJceSecurityCopy()){
		if(!webInstallerHelper.copyJDKjars()) {
			isNext = false;
			request.setAttribute("errorMsg","Error occured while configuring JDK or JRE.");						
			RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
			rd.forward(request, response);
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
<script type="text/javascript" src="/src/extjs/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="/src/extjs/ext-all.js"></script>
<SCRIPT TYPE="text/javascript">
      if (top.frames.length!=0)
            top.location=self.document.location;
</SCRIPT>
<script>


function setProgressBarValue(){	
	document.getElementById("databaseinput").innerHTML = "";
	document.getElementById("progressTd").style.visibility="visible";
	document.getElementById("progressBar").style.visibility="visible";

	Ext.Ajax.request({
		   url: 'progress.jsp',
		   method: 'POST',
		   success: function(result, request){ 
			   resText = result.responseText;
			   var message = resText.split("|");
			   if(message[0] < 11){
					percentcounter = message[0] * 20;
					document.getElementById("bar").width = percentcounter;
					document.getElementById("bartext").innerHTML = message[1];
					setTimeout(setProgressBarValue,5000);
				}else{
					// max value for percentcounter = 200
					document.getElementById("bar").width = 200;
					document.getElementById("bartext").innerHTML = "Done...";
				}
		   },
		   failure: function(result, response){
			   //document.getElementById("progressImage").style.visibility="visible";
		   }
	});	
}

function doSubmit(){
	form = document.installForm;
	if(form.dbstatus.checked)
		alert(form.dbstatus.value);
	if(form.databasehost.value==''){
		alert('Please enter database host');
		form.databasehost.focus();
		return false;
	}
	if(form.databaseName.value==''){
		alert('Please enter database name');
		form.databaseName.focus();
		return false;
	}
	if(form.pnetDataFilePath.value==''){
		alert('Please enter Oracle Home path');
		form.pnetDataFilePath.focus();
		return false;
	}
	if(form.sysUsername.value==''){
		alert('Please enter System Username');
		form.sysUsername.focus();
		return false;
	}
	if(form.sysPassword.value==''){
		alert('Please enter System Password');
		form.sysPassword.focus();
		return false;
	}
	setTimeout(setProgressBarValue,5000);
	form.submit();
}
function init(){
	if(<%=webInstallerHelper.getDatabaseSetupStatus()%> == "true" || <%=webInstallerHelper.getDbScriptExeStatus()%> == "true"){
		document.installForm.dbstatus[0].checked='checked';
		showyes();
	}
}
function showyes(){
	document.getElementById("dbyes").style.display="none";
	document.getElementById("dbno").style.display="block";
}
function showno(){
	document.getElementById("dbyes").style.display="block";
	document.getElementById("dbno").style.display="none";
}
</script>
</head>

<body onload="init();">
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
          <li class="selected">Database setup</li>
          <li>SMTP setup</li>
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
    <td width="570" align="left" valign="top" bgcolor="#f5f5f5" style="padding-left:10px; padding-top:25px; padding-right:10px; padding-bottom:10px; ">
	<TABLE>
	<TR>
		<TD id="databaseinput" colspan="2">

		<!-- start db input-->

	<h1><span style="color: #14809F">2</span> Database Setup </h1>
      <p>  </p>
		<form name="installForm" action="smtpsetup.jsp" method="post">
		<table width="100%" border="0" cellspacing="3" cellpadding="3">
		 <%if((String) request.getAttribute("errorMsg") != null) {%>
			<tr valign="top">		
				<td colspan="3" class="installErrorStyle">
					<%=(String) request.getAttribute("errorMsg")%>
				</td>
			</tr>
		  <%}%>
		  <tr valign="top"><td>
		   Project.net requires Oracle Database to be installed which should be accessible from Project.net installer 
		  <p> Do you already have a Oracle Database installed ?
		  <input type="radio" name="dbstatus" value="Y" onClick="showyes();">Yes <input type="radio" name="dbstatus" value="N" onClick="showno();" checked="checked">No </p>
		  
		  <div id="dbyes">
			  <table width="100%" border="0" cellspacing="3" cellpadding="3">
			  <tr>
			    <td width="4%" valign="top"><img src="img/mftico.gif" alt="bullet" width="12" height="10"></td>
			    <td colspan="2"><h2>If No, Create new database</h2><br>
				<a href="http://download-uk.oracle.com/docs/cd/B10501_01/server.920/a96521/create.htm" target="_blank"> 
						How to create a database in Oracle?</a><br>Instruction : Create a new database as per the information provided</td>
			  </tr></table>
		 </div>
		<div id="dbno" style="height:280px;">
			<table width="100%" border="0" cellspacing="3" cellpadding="3" style="table-layout:fixed;">
			<tr>
		    <td valign="top" width="4%"><img src="img/mftico.gif" alt="bullet" width="12" height="10"></td>
		    <td colspan="2" width="96%" ><h2>Configure Database for Project.net</h2><br>
			Following information is required according to your Oracle Database Installation.<br>
			</td>
		    </tr>
		  <tr>
		    <td></td>
		    <td width="37%" align="right">Database Host:</td>
		    <td width="59%"><input type="text" name="databasehost" value="<%=webInstallerHelper.getDatabaseHost()  %>"/></td>
		  </tr>
		  <tr>
		    <td></td>
		    <td width="37%" align="right">Database Name:</td>
		    <td width="59%"><input type="text" name="databaseName" value="<%=webInstallerHelper.getDatabaseName()  %>"/></td>
		  </tr>
		  <tr>
		    <td></td>
		    <td align="right">Oracle's oradata path:</td>
		    <td>
		      <input type="text" name="pnetDataFilePath" value = "<%= webInstallerHelper.getOraclePath()%>"/><br>
		    (e.g. e:/oracle/oradata) </td>
		  </tr>
		  <tr>
		    <td></td>
		    <td align="right">Database System Username:</td>
		    <td><input type="text" name="sysUsername" value = "<%=webInstallerHelper.getSystemUser()  %>"/></td>
		  </tr>
		    <tr>
		    <td></td>
		    <td align="right">Database System Password:</td>
		    <td><input type="text" name="sysPassword" value="<%= webInstallerHelper.getSystemPass() %>"/></td>
		  </tr>
		  <tr>
		    <td>&nbsp;</td>
		    <td>&nbsp;</td>
		    <td>&nbsp;</td>
		  </tr>
		  <tr>
		    <td colspan="3" align="right"><button id="submit_Btn" onClick="doSubmit(); return false;">Next >></button></td>
		  </tr>
		  </table>
	  </div></td></tr></table></form>
		<!-- end  db input-->
		</TD>
	</TR>
	<TR id="progressTd" style="visibility:hidden;" >
		<td width="90">&nbsp
		</td>
		<TD align="left" style="margin-left: 40px;">
		<!-- start progress bar -->
			<div id="progressBar" class="loadingPage">
				 <b><div id="bartext">Database Execution Progress  </div></b> <br/><br clear="all"/>
				<div >
					<TABLE height="42" width="219">
					<TR>
						<TD height="41" width="219" background="img/bg_bar.gif" >
						<img id="bar"  style="margin-left: 8px;" src="img/graph.gif" border="0" height="24" width="0">
						</TD>
					</TR>
					</TABLE>
				</div>
				<br/><br/>
			</div>
		<!-- end progress bar -->
		</TD>
	</TR>
	</TABLE>

			  </td>
  </tr>
  <tr>
    <td class="basestyle" height="85" colspan="2" background="img/basebott.gif"><div align="center">&copy; Copyright 2000-2008 <a href="http://www.project.net">Project.net Inc.</a> All Rights Reserved.<br />
    <a href="http://dev.project.net/licenses/PPL1.0">Licence agreement</a>.</div></td>
  </tr>
</table>

<script>
	document.getElementById("dbyes").style.display="block";
	document.getElementById("dbno").style.display="none";
</script>

</body>
</html>
