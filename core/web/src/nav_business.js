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
// Loads the menu when displaying a space
function load_menu(currentSpaceID) {
}

// Loads the menu when displaying the portfolio
function load_menu_portfolio() {
}

function reload_menu() {
}


 function load_header() {
}

function writeButtonName(name) {
	if (document.all) {
		displayname.innerHTML=name;
	} if (document.layers && document.displayname) {	
		document.displayname.document.write(name);
		document.displayname.document.close();
	}
}