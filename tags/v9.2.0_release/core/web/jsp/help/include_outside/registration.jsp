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

<a name="registration"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Registration</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
The login page is the first page you see, either as a new or existing user.
Where new users must complete the registration process, existing customers
just log in. If you have been invited to a project, the last page, Verification
has already been filled in.
<p>The registration process has four different pages. As a general rule,
all fields in bold must be filled in. The first Registration page, Identity,
is used for entering your individual contact data and the contact data
for your firm. This step is also part of the security process. Passwords
and verification codes are tied to both the organization hosting a project
and the members assigned to it. As such, each email address must have a
unique password. Multiple passwords cannot be assigned to a single email
address.
<p>You will also enter information for a “jog” question to help Project.net
identify you. The Jog question could be considered as a second&nbsp; password.
As passwords are one-way encrypted, if you have lost your password and
the jog question does not help, you will have to obtain a new password,
by requesting one from Project.net support.
<p>On the next Registration page, Address, you enter the required physical
address data. You will also enter your time zone. As time zone is stored
as GMT, you can schedule meetings, view schedule dates, and check-in or
check-out according to local time.
<p>Phone numbers can be entered free format, with no required brackets,
dashes or dots.
<p>On the next Registration page, Business, you enter data to provide industry
and user experience classification.
<p>On the last Registration page, Verification, you enter the required
Project.net data. If you were invited to a project, your email address and the
necessary authorization strings were automatically filled in. You will
see a confirmation notice when the data has been accepted.