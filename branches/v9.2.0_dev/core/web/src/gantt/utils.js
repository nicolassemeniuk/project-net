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
// DynScript loads a script an puts it into the browser.
// Whatever is the last script loaded with dynscript.
function dynScript(file){
  var scriptTag = document.getElementById('loadScript');
  var head = document.getElementsByTagName('head').item(0)
  if(scriptTag) head.removeChild(scriptTag);
  script = document.createElement('script');
  script.src = file;
	script.type = 'text/javascript';
	script.id = 'loadScript';
	head.appendChild(script)
}
