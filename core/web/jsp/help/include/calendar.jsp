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
<a name="calendar"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Calendar</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
The Calendar displays events, tasks, meetings, and other occurrences. Items
that appear on your calendar such as meetings also appears on the calendars
of all other project members that have been invited to a meeting.
<p>There are three kinds of meetings that can be created in the Project
Space. The types are designated in the Facility list box and are. Physical
(taking place in a conference room), Teleconference (taking place on the
phone in a conference call), and WebEx. WebEx is a type of on-line meeting
where participants see the Host's screen and listen in and participate
via phone.
<p>Please note that if you use the toolbar to modify a calendar item, all
Projects and project members automatically see the updated information.
<h4>
Notes</h4>
When scheduling a meeting, attendees must members of a Project or a Business
before they can be invited. Note that any of the meeting types can have
an Agenda.
<p>In a WebEx meeting, the Host must start the meeting.
<p>Topics covered include:
<ul>
<li>
<a href="#create_physical_meeting">Compose New Physical Meeting</a></li>

<li>
<a href="#create_meeting_agenda">Create Meeting Agenda</a></li>

<li>
<a href="#add_more_agenda_items">Add More Agenda Items</a></li>

<li>
<a href="#create_webex_meeting">Create WebEx Meeting</a></li>

<li>
<a href="#compose_calendar_event">Compose a New Event</a></li>
</ul>

<h3>
<a name="create_physical_meeting"></a>To Compose a new meeting</h3>

<ol>
<li>
In the Project Space, Scheduling, click Calendar.</li>

<li>
In the upper right corner, click Compose New and the type is Meeting.</li>

<li>
The Meeting Wizard then opens.</li>

<li>
In this GUI, simply fill in the blanks. Any required item is listed in
bold. Be sure to select Physical Meeting for the Facility. The items include
Meeting name, Meeting location, Meeting Host (because you created the meeting,
this is you by default), Date, the Start time and the End Time, Facility
Type (physical, teleconference, WebEx) facility description, Purpose, and
Description.</li>

<li>
If you are scheduling a date in the future, simply click on the Popup Calendar
to select a date.</li>

<li>
When done filling in the Wizard, clicking Next goes to meeting attendees
wizard.</li>

<li>
Add members by clicking Add Attendee in the tool bar. This action opens
a GUI for selecting attendees from the project roster. Items in the GUI
include Status (Invited, Accepted invitation, Declined Invitation, Declined,
Absent, Tardy).</li>

<li>
If you were invited to a meeting, it will appear in your personal space
as an assignment. When you Accept, this meeting will be added to your Calendar.</li>

<li>
When you Submit your acceptance, it appears on the Host's calendar and
that of other project members.</li>
</ol>

<h3>
<a name="create_meeting_agenda"></a>Create Meeting Agenda</h3>
Once the meeting is created, you can make an Agenda for that meeting. This
GUI appears when you click Next after Composing the meeting.
<ol>
<li>
Start to populate the Agenda by clicking Add Agenda.</li>

<li>
Fill in the blanks with the Required and optional information.</li>

<li>
Click Submit when done entering data for that item.</li>

<li>
Clicking Add Agenda allows you to add more items. Note that when you add
an item, you also enter a topic and a time. This way you can estimate the
duration of the meeting and by scanning the agenda items, better prepare
for that meeting.</li>

<li>
When you click Next, the calendar of all participants updates. Only the
attendees see the meeting on the calendar.</li>
</ol>
As with all applications on the Project.net site, the Space Administrator is
able to view all calendar and meeting items for all member in projects.
<p>When creating a meeting, Standard tool bar functions apply.
<h3>
<a name="add_more_agenda_items"></a>Add More Agenda Items</h3>
Any invitee who has accepted, can add agenda items to the Meeting. Just
follow the process detailed below.
<ol>
<li>
To add more Agenda items once a meeting has been made, click on the meeting.
This takes you to the Project Space and shows the current meeting information.</li>

<li>
In the Agenda tab, click the Modify (delta) symbol.</li>

<li>
On the next page, click Add Agenda.</li>

<li>
Fill in the Required and optional information.</li>

<li>
Click Submit when done with this item.</li>

<li>
The item you just keyed in is displayed and you are returned to the GUI
to add more items.</li>

<li>
If you wish to add another item, click Add Agenda.</li>

<li>
Click Submit to stop adding items.</li>
</ol>

<h3>
<a name="modify_agenda_item"></a>To Modify an Agenda Item</h3>
As agendas often change, updating and correcting them is easy. To fix an
agenda item, just use the following procedure.
<ol>
<li>
As the Owner, just click on the item you wish to change.</li>

<li>
The Add Agenda GUI appears with the data as initially entered.</li>

<li>
Make the desired changes.</li>

<li>
Click Submit when done.</li>
</ol>

<h3>
<a name="create_webex_meeting"></a>To Create a WebEx meeting</h3>

<ol>
<li>
In the Personal or Project Space, Click on Compose new meeting.</li>

<li>
Set the facility type to WebEx. As the creator of the meeting, you are
Meeting Host by default.</li>

<li>
Click Next. Behind the scenes, the meeting is created on the WebEx site.</li>

<li>
Times are shown local (GMT relative). Note that as an attendee you can
only join a meeting</li>

<li>
When it is time for the meeting to start, the Host clicks on start meeting.</li>

<li>
If you are a first time user, the required WebEx components will be automatically
installed on your computer.</li>

<li>
Meeting room menu appears, with your name (host name). See the WebEx instructions.</li>

<li>
Choose your meeting format with the WebEx options which include:</li>

<li>
End meeting.</li>
</ol>

<h3>
<a name="compose_calendar_event"></a>To Compose a new Event</h3>

<ol>
<li>
In the Personal Space calendar, click Compose New.</li>

<li>
Key in the data, being sure to fill in the required fields.</li>

<li>
The Event now appears on your personal Calendar.</li>
</ol>
Events can also be created in <a href="Help.jsp?page=calendar&section=personal">Personal
Space</a>.
