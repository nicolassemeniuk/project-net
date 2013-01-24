<jsp:useBean id="webInstallerHelper" scope="page" class="net.project.util.installer.WebInstallerHelper" > 
<jsp:setProperty name="webInstallerHelper" property="realTomcatPath" value="<%= application.getRealPath("/") %>" /> 
</jsp:useBean>
<%
webInstallerHelper.log( "index.jsp" );
	if(webInstallerHelper.isCompletedInstaller()) { %>
	<SCRIPT TYPE="text/javascript">
      if (top.frames.length!=0)
            top.location="Login.jsp";
	</SCRIPT>
	<%} else if (webInstallerHelper.getIndexForward() != null ) {
	    request.getRequestDispatcher(webInstallerHelper.getIndexForward()).forward(request, response);
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
<script language="javascript">
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
            <li class="selected">Basic setup</li>
          <li>Database setup</li>
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
    <td width="570" align="left" valign="top" bgcolor="#f5f5f5" style="padding-left:10px; padding-top:25px; padding-right:10px; padding-bottom:10px; "><h1><span style="color: #14809F">1</span> Basic Setup </h1>
      <p> Project.net requires following basic configuration </p>
		  <form name="installForm" action="dbsetup.jsp">
		  <table width="100%" border="0" cellspacing="3" cellpadding="3">
		  <%if( request.getAttribute("errorMsg") != null) {%>
			<tr>		
				<td colspan="3" class="installErrorStyle">
					<%= request.getAttribute("errorMsg")%>
				</td>
			</tr>
		  <%}%>
  <tr>
    <td width="4%" valign="top"><img src="img/mftico.gif" alt="bullet" width="12" height="10"></td>
    <td width="81%" valign="top"><h2>Configure Oracle Database JDBC Driver</h2> 
	<!--(This will copy ojdbc.jar file to <%=webInstallerHelper.getRealTomcatPath()%>common\lib)--></td>
    <td width="15%"><% if(webInstallerHelper.isCompletedJdbcJarCopy()){%>
				<font color="green"><b> Successful..</b></font>
				<%}%></td>
  </tr>
  <tr>
    <td valign="top"><img src="img/mftico.gif" alt="bullet" width="12" height="10"></td>
    <td valign="top"><h2>Configure Endorsed jar files</h2> 
	<!--(This will copy ojdbc.jar file to <%=webInstallerHelper.getRealTomcatPath()%>common\endorsed directory<br>
	"endorsed" directory in common if not exist, will be created)--></td>
    <td><% if(webInstallerHelper.isCompletedEndorsedJarCopy()){%>
				<font color="green"><b> Successful..</b></font>
		<%}%></td>
  </tr>
  <tr>
    <td><img src="img/mftico.gif" alt="bullet" width="12" height="10"></td>
    <td><h2>Configure Mail jar files</h2>
	<!--Copy activation.jar and mail.jar to TOMCAT_HOME/common/lib<br>
	(Copies activation.jar and mail.jar files to 
			<%=webInstallerHelper.getRealTomcatPath()%>common\lib directory.)--></td>
    <td><% if(webInstallerHelper.isCompletedMailJarCopy()){%>
				<font color="green"><b> Successful..</b></font>
				<%}%></td>
  </tr>
  <tr>
    <td valign="top"><img src="img/mftico.gif" alt="bullet" width="12" height="10"></td>
    <td valign="top"><h2>Configure JDK for higher encryption</h2>
	<!--(JDK on this system will be configured by copying Project.net security policy files<br>
	to <%=System.getenv("JAVA_HOME").replace("jdk","jre")%>/lib/security directory)--></td>
    <td><% if(webInstallerHelper.isCompletedJceSecurityCopy()){%>
				<font color="green"><b> Successful..</b></font>
		<%}%></td>
  </tr>
  <tr>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
    <td>&nbsp;</td>
  </tr>
  <tr>
    <td colspan="3" align="right"><button onClick="doSubmit();" id ="btnNext"> Next >></button></td>
  </tr>
  <tr height="5" >
    <td colspan="3"></td>
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
<div align="center" id="register" class="license">
	<table>
	<tr>
	<td valign="top" align="center" class="vardanablackbold" colspan="3"><br/>
              <table width="89%" cellspacing="0" cellpadding="0" border="0">
              <tbody>
              <tr>
                <td valign="top" align="left" colspan="2"><strong>  <strong>Information Request Form:<span class="bodytexboldco"><font color="darkred"><strong><font color="darkred"><strong>*</strong></font></strong></font></span></strong> = Required Field</strong></span><br/>
                  <br/>
                 
<script language="JavaScript"><!--
function validator(f){
    //alert(f.elements[11].name);
    if(f.first_name.value==""){
        alert("You must enter your first name.");
        f.first_name.focus();
        return;
    }
    if(f.last_name.value==""){
        alert("You must enter your last name.");
        f.last_name.focus();
        return;
    }
    if(f.email.value.search("@") == -1 || f.email.value.search("[.*]") == -1){
        alert("You must enter a valid email address.");
        f.email.focus();
        return;
    }
    if(f.phone.value==""){
        alert("You must enter your phone number.");
        f.phone.focus();
        return;
    }
    if(f.company.value==""){
        alert("You must enter your organization name.");
        f.company.focus();
        return;
    }
    if(f.noofemp.selectedIndex == 0){
        alert("You must select the number of employees in your company.");
        f.elements[11].focus();
        return;
    }
	f.action="path to server to submit form data";
	f.submit();
	displayBasicSetup();
}

var optionTest = true;
var store = new Array();

store[0] = new Array(
	'',
	'');

store[1] = new Array(
	'',
	'');

store[2] = new Array(
	'----- Select Best Answer -----',
	''
	, 'Email'
	, 'Email'
	, 'Paper Mail'
	, 'Paper Mail'
	);

store[3] = new Array(
	'----- Select Best Answer -----',
	''
	, 'Google'
	, 'Google'
	, 'Project-management-software.org'
	, 'Project-management-software.org'
	);

store[4] = new Array(
	'----- Select Best Answer -----',
	''
	, 'CIO'
	, 'CIO'
	, 'PC Magazine'
	, 'PC Magazine'
	, 'PC World'
	, 'PC World'
	);

store[5] = new Array(
	'',
	'');

store[6] = new Array(
	'----- Select Best Answer -----',
	''
	, 'Altavista'
	, 'Altavista'
	, 'Ask.com'
	, 'Ask.com'
	, 'Google'
	, 'Google'
	, 'MSN'
	, 'MSN'
	, 'search.com'
	, 'search.com'
	, 'Yahoo'
	, 'Yahoo'
	);

store[7] = new Array(
	'',
	'');

store[8] = new Array(
	'----- Select Best Answer -----',
	''
	, 'co-worker'
	, 'co-worker'
	, 'friend'
	, 'friend'
	, 'manager'
	, 'manager'
	, 'other'
	, 'other'
	);


	
function optionTestIt()
{
	optionTest = true;
	lgth = document.forms[0].elements[18].options.length - 1;
	document.forms[0].elements[18].options[lgth] = null;
	if (document.forms[0].elements[18].options[lgth]) optionTest = false;
}


function populate(f)
{
	if (!optionTest) return;
	var box = f.elements[17];
	var number = box.selectedIndex;
	//if (!number) return;
	var list = store[number];
	var box2 = f.elements[18];
	box2.options.length = 0;
	for(i=0;i<list.length;i+=2)
	{
		box2.options[i/2] = new Option(list[i],list[i+1]);
	}
}
//--></script>				
						

<form method="get" target="registeriframe"/>
<input type="hidden" name="os" value='<jsp:getProperty name="webInstallerHelper" property="operatingSystem"/>' />
<tbody><tr> 
  <td align="right" class="verdanablackl"><span class="vardanablackl"><span class="bodytexboldco"><font color="darkred"><strong><font color="darkred"><strong>*</strong></font></strong></font></span><strong>First Name :</strong></span></td>
  <td width="62%" valign="top" align="left" class="verdanablackl"> 
    <input type="text" maxlength="40" size="30" name="first_name"/>  </td>
</tr>
<tr> 
  <td align="right" class="verdanablackl"><span class="vardanablackl"><span class="bodytexboldco"><font color="darkred"><strong><font color="darkred"><strong>*</strong></font></strong></font></span><strong>Last Name :</strong></span></td>
  <td> 
    <input type="text" maxlength="40" size="30" name="last_name"/>  </td>
</tr>
<tr> 
  <td align="right" class="verdanablackl"><span class="style1"><font color="darkred"><font color="darkred">*</font></font></span><b class="bodytextbold">Email :</b> </td>
  <td> 
    <input type="text" maxlength="80" size="30" name="email"/>  </td>
</tr>
<tr> 
  <td align="right" class="verdanablackl"><span class="style1"><font color="darkred"><font color="darkred"><strong>*</strong></font></font></span><b class="bodytextbold">Phone Number :</b> </td>
  <td> 
    <input type="text" maxlength="40" size="20" name="phone"/>  </td>
</tr>

<tr> 
  <td align="right" class="verdanablackl"><span class="style1"><font color="darkred"><font color="darkred"><strong>*</strong></font></font></span><b class="bodytextbold">Organization :</b> </td>
  <td> 
    <input type="text" maxlength="80" size="30" name="company"/>  </td>
</tr>
<tr> 
	<td align="right" class="verdanablackl"><span class="style1"><font color="darkred"><font color="darkred"><strong>*</strong></font></font></span><b class="bodytextbold">Number of Employees : </b></td>
	<td> 
		<select name="noofemp">
		<option>----- Select Best Answer -----</option>
		<option value="1-99">1-99</option>
		<option value="100-499">100-499</option>
		<option value="500-2499">500-2499</option>
		<option value="2500+">2500+</option>
		</select>	</td>
</tr>

<tr> 
  <td align="right" class="verdanablackl"> <b class="bodytextbold">Country :</b> </td>
  <td> 
	<input type="text" maxlength="40" name="country"/>  </td>
</tr>
<tr> 
  <td align="right" class="verdanablackl"> <b class="bodytextbold">State/Province :</b> </td>
  <td> 
    <input type="text" maxlength="20" name="state"/> </td>
</tr>
<tr> 
  <td align="right" class="verdanablackl"><span class="bodytextbold"><b>Industry :</b></span></td>
  <td class="verdanablackl"> 
   <select name="industry"><option value=""/>
   <option value="Agriculture">Agriculture</option>
   <option value="Apparel">Apparel</option>
   <option value="Banking">Banking</option>
   <option value="Biotechnology">Biotechnology</option>
   <option value="Chemicals">Chemicals</option>
   <option value="Collaboration">Collaboration</option>
   <option value="Communications">Communications</option>
   <option value="Construction">Construction</option>
   <option value="Consulting">Consulting</option>
   <option value="Consulting IT">Consulting IT</option>
   <option value="Consulting PM">Consulting PM</option>
   <option value="Education">Education</option>
   <option value="Electronics">Electronics</option>
   <option value="Energy">Energy</option>
   <option value="Engineering">Engineering</option>
   <option value="Entertainment">Entertainment</option>
   <option value="Environmental">Environmental</option>
   <option value="Finance">Finance</option>
   <option value="Food & Beverage">Food & Beverage</option>
   <option value="Government">Government</option>
   <option value="Healthcare">Healthcare</option>
   <option value="Hospitality">Hospitality</option>
   <option value="Insurance">Insurance</option>
   <option value="Internet">Internet</option>
   <option value="Internet Product">Internet Product</option>
   <option value="Internet Service">Internet Service</option>
   <option value="Machinery">Machinery</option>
   <option value="Manufacturing">Manufacturing</option>
   <option value="Media">Media</option>
   <option value="Not For Profit">Not For Profit</option>
   <option value="Professional Service">Professional Service</option>
   <option value="Recreation">Recreation</option>
   <option value="Retail">Retail</option>
   <option value="Shipping">Shipping</option>
   <option value="Software">Software</option>
   <option value="Technology">Technology</option>
   <option value="Telecommunications">Telecommunications</option>
   <option value="Transportation">Transportation</option>
   <option value="Utilities">Utilities</option>
   <option value="Other">Other</option></select><br/>  </td>
</tr>
<tr> 
  <td align="right" class="verdanablackl" nowrap> <b class="bodytextbold">How did you hear about us? </b> </td>
  <td> 
	<select name="00N00000001420E">
		<option value="No Answer">----- Select Best Answer -----</option>
		<option value="ProjectWorld (Los Angeles)">ProjectWorld (Los Angeles)</option>		
		<option value="Direct Mail">Direct Mail</option>		
		<option value="Internet Ad">Internet Ad</option>		
		<option value="Magazine">Magazine</option>		
		<option value="Other">Other</option>		
		<option value="Search Engine">Search Engine</option>		
		<option value="Trade Show">Trade Show</option>		
		<option value="Word of Mouth">Word of Mouth</option>	</select><br/>  </td>
</tr>

<tr align="left"> 
  <td valign="top" align="right" class="verdanablackl"> <b class="bodytextbold">Comments :</b> </td>
  <td align="left" class="verdanablackl">
    <textarea wrap="VIRTUAL" rows="6" cols="30" name="description"></textarea>  </td>
</tr>
<tr> 
  <td valign="top" align="right" class="verdanablackl"/>
  <td class="verdanablackl"> 
    <input type="button" onclick="validator(this.form)" name="btn_submit" value="Register"/>  </td>
</tr></form>

</tbody></table>
</td>
          </tr>
        </tbody></table>
            
</td></tr>
			  </table>
</div>
<div align="center" id="welcome" class="welcome">
	<span><img src="img/test_08.gif" alt="Basic Setup"></span>
		  <p> Welcome to Installation of Project.net </p>
		  <p>This Software is developed by :</p>
		  <p align="center"> Project.net  &lt;<a href="mailto:support@project.net">support@project.net</a>&gt; </p>
		  <p align="center">The Home Page is at <a href="http://www.project.net" target="_blank">http://www.project.net</a> </p>
</div>

<div align="center" id="license" class="license">
	<span><h1>License Agreement </h1></span>
		  <p> Please read the following License Agreement carefully </p>
		   <div align="center" style="overflow:auto; height:200px; text-align:left"> 
<p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
Copyright 2000-2008 Project.net Inc.<br>
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
Licensed under the Project.net Public License, Version 1.0 (the "License");<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
you may not use this file except in compliance with the License.<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
You may obtain a copy of the License at<br>
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<a href="http://dev.project.net/licenses/PPL1.0">http://dev.project.net/licenses/PPL1.0</a><br>
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
Unless required by applicable law or agreed to in writing, software<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
distributed under the License is distributed on an "AS IS" BASIS,<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
See the License for the specific language governing permissions and<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
limitations under the License.<br> </p></div> </p></center>
		  <p><button id="next" onClick="displayRegistration();">I Accept</button></p>
</div>
<script>
function displayWelcome(){
	document.getElementById("welcome").style.visibility="visible";
	document.getElementById("license").style.visibility="hidden";
	//document.getElementById("btnNext").style.visibility="hidden";
}
function displayLicense(){
	document.getElementById("welcome").style.visibility="hidden";
	document.getElementById("license").style.visibility="visible";
	document.getElementById("register").style.visibility="hidden";
	//document.getElementById("btnNext").style.visibility="hidden";
}
function displayBasicSetup(){
	document.getElementById("welcome").style.visibility="hidden";
	document.getElementById("license").style.visibility="hidden";
	document.getElementById("register").style.visibility="hidden";
	//document.getElementById("btnNext").style.visibility="visible";
}
function displayRegistration(){
	document.getElementById("welcome").style.visibility="hidden";
	document.getElementById("license").style.visibility="hidden";
	document.getElementById("register").style.visibility="visible";
}
//displayWelcome();
//setTimeout(displayLicense, 7000);
displayLicense();
</script>
<iframe name="registeriframe" id="registeriframe" height="0" width="0"></iframe>
</body>
</html>
