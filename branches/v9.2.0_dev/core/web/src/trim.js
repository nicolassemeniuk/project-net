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
function trimL(str) {
	var tmp_str = "";
	var blank = true;
	var i = 0; j = 0; 

	while (blank && (i< str.length)) {
		if (str.charAt(i) == " ")
			i++;
		else blank = false;
	}
	
	if (!blank) {
			tmp_str = str.substring(i, str.length);
	}

	return tmp_str; 
}

function trimR(str) {
	var tmp_str = "";
	var blank = true;
	var i = str.length - 1;
	
	while (blank && i >= 0) {
 		if (str.charAt(i) == " ")
			i--;
		else blank = false;
	}
	
	if (!blank) {
			tmp_str = str.substring(0, i+1);
	}
		
	return tmp_str;
}

function trim(str) {
	return(trimR(trimL(str)));
}
