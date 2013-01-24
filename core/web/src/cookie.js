/* 
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
*/
	// Function to create or update a cookie. 
	// [expires] - Date object containing the expiration data of the cookie.  If 
	// omitted or null, expires the cookie at the end of the current session.
	function SetCookie (name,value,expires,path,domain,secure) 
	{
	 document.cookie = name + "=" + escape (value) +
		((expires) ? "; expires=" + expires : "") +
		((path) ? "; path=" + path : "") +
		((domain) ? "; domain=" + domain : "") +
		((secure) ? "; secure" : "");
	}

	//"Internal" function to return the decoded value of a cookie 
	function getCookieVal (offset)
	{
	  var endstr = document.cookie.indexOf (";", offset);
	  if (endstr == -1)
		endstr =document.cookie.length;
	  return unescape(document.cookie.substring(offset, endstr));
	}

	// Function to return the value of the cookie specified by "name".
	// name - String object containing the cookie name.
	// returns - String object containing the cookie value, or null if
	// the cookie does not exist.
	function GetCookie (name) 
	{
	  var arg = name + "=";
	  var alen = arg.length;
	  var clen = document.cookie.length;
	  var i = 0;
	  while (i < clen) {
		var j = i + alen;
		if (document.cookie.substring(i, j) == arg)
		  return getCookieVal (j);
		i = document.cookie.indexOf(" ", i) + 1;
		if (i == 0) break; 
	  }
	  return null;
	}