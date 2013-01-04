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

function setVar(name, value, to_expire) {
   var today = new Date()
   var expires = new Date()
 
   if (to_expire == null) {
      // Expires one (1) day from now
      expires.setTime(today.getTime() + 1000*60*60*24);
   } else
      expires.setTime(today.getTime() + 1000*60*60*24*to_expire);

   self.setCookie(name, value, expires);
}


function getVar(name) {
  return self.getCookie(name);
}


function getCookie(Name) {
   var search = Name + "="
   if (document.cookie.length > 0) { // if there are any cookies
      offset = document.cookie.indexOf(search) 
      if (offset != -1) { // if cookie exists 
         offset += search.length 
         // set index of beginning of value
         end = document.cookie.indexOf(";", offset) 
         // set index of end of cookie value
         if (end == -1) 
            end = document.cookie.length
         return unescape(document.cookie.substring(offset, end))
      } 
   }
}

function setCookie(name, value, expire) {
   document.cookie = name + "=" + escape(value)
   + ((expire == null) ? "" : ("; expires=" + expire.toGMTString()))
}

client_state_loaded = true;

