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
function checkIsNumber(myObj, errorMes){
	// checks that
	//var re = /[0123456789-]+/
	//if (re.test(myObj.value)){
	var split = myObj.value.indexOf("-");
	var sub1 = myObj.value.substring(0,split);
	var sub2 = myObj.value.substring(split,myObj.value.length);
	if ((isNaN(sub1))||(isNaN(sub2))){
		errorHandler(myObj, errorMes);
	}else{
		return true;
	}
}

function checkIsPositiveNumber(myObj, errorMes, allowBlank){
	// checks that
	//var re = /[0123456789-]+/
	//if (re.test(myObj.value)){
	if (!allowBlank) {
	    allowBlank = false;
	}

	if (myObj.value == "") {
	    return allowBlank;
	} else if ((isNaN(myObj.value)) || (myObj.value < 0)) {
		return errorHandler(myObj, errorMes);
	} else {
    	return true;
	}
}
