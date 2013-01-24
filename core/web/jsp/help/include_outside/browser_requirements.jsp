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


<%@page import="net.project.security.SessionManager"%>
<a name="browser_requirements"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Web Browser Requirements</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
The project.net application is designed to work with most modern browsers.  
You must enable javascript and cookies in the web browser to use this application.<p>
<p>
Browser Check:
<div align=left>
<script language="javascript">
<!--
document.write("<b>JavaScript on!</b>")
//-->
</script>
<noscript><b>JavaScript off!  Please enable JavaScript support in your web broswer.</b></noscript>

<br>
<script language="javascript">
<!--
today=new Date();

function getexpirydate( nodays){
var UTCstring;
Today = new Date();
nomilli=Date.parse(Today);
Today.setTime(nomilli+nodays*24*60*60*1000);
UTCstring = Today.toUTCString();
return UTCstring;
}

function GetCookie(cookiename) {
 var cookiestring=""+document.cookie;
 var index1=cookiestring.indexOf(cookiename);
 if (index1==-1 || cookiename=="") return ""; 
 var index2=cookiestring.indexOf(';',index1);
 if (index2==-1) index2=cookiestring.length; 
 return unescape(cookiestring.substring(index1+cookiename.length+1,index2));
}

function SetCookie(name,value,duration){
cookiestring=name+"="+escape(value)+";EXPIRES="+getexpirydate(duration);
document.cookie=cookiestring;
if(!GetCookie(name)){
return false;
}
else{
return true;
}
}


SetCookie("testcookie","Cookies On!",null);
if(GetCookie("testcookie")==null)
	document.write("<B> Cookies Off!</b>");
else
	document.write("<B>" + GetCookie ("testcookie") + "</b>");
//document.cookie = "Cookies on!";
//document.write("<B>" + document.cookie + "</b>")
//-->
</script>
<noscript><b>Cookies off!  Please enable Cookie support in your web broswer.</b></noscript>
</div>



