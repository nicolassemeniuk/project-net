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
<a name="profile_personal"></a>

<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Profile</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
At any time, you can update your personal information. Simply go to the
Personal pages and click on Personal profile. Make any changes you want,
and click Apply to propagate the changes. Please note that this information
is private and secure as defined by the policies on this site.
<p>There are four tabs in your Personal Profile:
<ul>
<li>
<a href="#name_tab">Name</a></li>

<li>
<a href="#address_tab">Address</a></li>

<li>
<a href="#login_tab">Login</a></li>

<li>
<a href="#license_tab">License</a></li>

<li>
<a href="#business_tab">Business</a></li>
</ul>
The contents of each tab follows.
<h2>
<a name="name_tab"></a>Name tab</h2>
This tab contains your primary contact information. Required fields include:
First Name, Last Name, Display name, and Email address. As most notifications
including meeting invitations, task assignments, and calendar items go
to the email address, this must be kept current.
<h2>
<a name="address_tab"></a>Address tab</h2>
This tab contains your physical address information. As appointments and
check-out and check-out use GMT times, make sure you select the right time
zone. To ensure that all means of contact are covered, you can add mobile
phone number, pager number, and a pager email to your Personal profile.
<br>&nbsp;
<h2>
<a name="login_tab"></a>Login tab</h2>
This tab contains login, password and Jog question information. You can
change these parameters as you wish, but you must enter current login and
password in order to update this page. Note that the Jog question is used
as an additional identifier in case you forget or misplace your login information.
<br>&nbsp;
<h2>
<a name="license_tab"></a>License tab</h2>
The License tab displays your current license information as well as the history
of licenses you were associated with before. You can switch 
to some other license, but you must have a valid license key.
<br>&nbsp;
<h2>
<a name="business_tab"></a>Business tab</h2>
The Business tab includes your company name and includes some additional
information to help Project.net track its user base. This information is not
resold and is kept private according to the policies of this site.

