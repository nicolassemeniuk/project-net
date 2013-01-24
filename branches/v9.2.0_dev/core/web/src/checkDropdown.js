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
// Check an option is selected in a dropdown
// Returns an error if no item is selected or the first item is
// selected and its value is the empty string
function checkDropdown(myObj, errorMes){
	if (myObj.selectedIndex == -1 ||
	    (myObj.selectedIndex == 0 && myObj.options[0].value == '')){
		return errorHandler(myObj,errorMes);
	}else{
		return true;
	}
}


// this function checks to see that at leat one item is selected
// Deprecated.  Use checkDropdown instead
function checkDropdown_NoSelect(myObj, errorMes){
	if (myObj.selectedIndex == -1){
		return errorHandler(myObj,errorMes);
	}else{
		return true;
	}
}	
