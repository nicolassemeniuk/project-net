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
<a name="forgottenpwd"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Forgotten Password or Login</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>If you have forgotten your password or login, use the following procedures.

<h3>
To get your forgotten password</h3>

<ol>
<li>
Click on Forgotten Password on the Login page.</li>

<li>
In the Forgotten LoginName/Password Wizard, enter your email address and
select the Password radio button.</li>

<li>
You will then see your Jog question. Answer this question so that it matches
the string keyed in when you registered on the site. The Jog question serves
as an additional identifier for the project.net application to verify that you are indeed the
person who is trying to access your account.</li>

<li>
Wait for the email that was sent to the address you used when you registered
for a Project.net account. Use the verification code in this email in the following steps.</li>

<li>
Click on the link in the email. (Alternatively, you could paste the response
from the email in the last window).</li>

<li>
This brings up a page for you to enter your email address, the Verification
Code, and allows you to reset the password.</li>
</ol>


<h3>
To retrieve your forgotten login</h3>

<ol>
<li>
Click on Forgotten Password on the Project.net Login page.</li>

<li>
In the Forgotten LoginName/Password Wizard, key in your Email address and
select the Login radio button.</li>

<li>
You will then see a combo box requesting your First Name, Last Name, and
your jog question response. (This information was obtained in the initial
registration process). Key in your data in the appropriate fields.</li>

<li>
When you have successfully filled in the fields, an email is sent to your
email address with your login ID.</li>

<li>
Use this login name and your password to access the project.net application.</li>
</ol>


