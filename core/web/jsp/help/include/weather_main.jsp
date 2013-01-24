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
<a name="weather_main"></a>
<table border="0" width="80%" cellpadding="0" cellspacing="0">
<tr class="channelHeader">
	<td width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-left_end.gif" width=8 height=15 alt="" border="0"></td>
	<td nowrap class="channelHeader"><nobr>Weather</nobr></td>
	<td align=right width=1%><img src="<%=SessionManager.getJSPRootURL()%>/images/icons/channelbar-right_end.gif" width=8 height=15 alt="" border="0"></td>
</tr>
</table>
<p>
<a name="weather_description"></a>Weather sites may be displayed on the
project space summary page. You may have as many weather sites as you like,
but the city, state and country combination must be unique for each display.
<br>&nbsp;
<h3>
<a name="quick_forecast"></a>Quick Forecast</h3>
You can obtain weather in the United States by entering a minimal amout
of data with the Quick Forecast option. From the Projects space, simply
click Weather and then key in your ZIP code. The closest weather matching
that ZIP code will be diplayed.
<h3>
<a name="create_new_weather_parameters"></a>Create New Weather</h3>
When you create a new weather display, you must enter the following:
<ul>
<li>
Site name</li>

<li>
City</li>

<li>
State</li>

<li>
Country</li>

<li>
Postal code (ZIP code in the United States)</li>
</ul>
For sites in the United States, the postal code (ZIP code) determines the
city location.
<p>You can create New (add) a new display, modify, and delete existing
weather sites on your project site.
<p>United States weather includes graphics and comes from AcuWeather (www.accuweather.com).
Non-Unites States weather data is provided by UPI.
