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
function checkEmail(emailObj, errorMes){
	// check if was filled in
	//	if (emailObj.value.length == 0){
	//	return errorMess(errorMes, emailObj);
	//}	
	// this reg exp check that there is an @ sign followed by a . followed by something		
	//	var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-zA-Z]{2,6}(?:\.[a-zA-Z]{2})?)$/i
	var re = /^[A-Za-z0-9](([!#\$%&'\*\+\-\/=\?\^_`\{|\}~_\.]?[a-zA-Z0-9]+)+)@(([A-Za-z0-9]+)(([\.\-]?[a-zA-Z0-9]+)*)){2,}\.([A-Za-z]){2,4}$/i
	var result = re.test(emailObj.value);
	if (result == false){
		return errorHandler(emailObj, errorMes);
	}else{
		return true;
	}
}

function checkEmailAllowNull(emailObj, errorMes){
	// check if was filled in
	//	if (emailObj.value.length == 0){
	//	return errorMess(errorMes, emailObj);
	//}	
	// this reg exp check that there is an @ sign followed by a . followed by something		

    if (emailObj == null || emailObj.value == null || emailObj.value == "")
	return true;
    else {
	var re = /.+@.+\..+/
	var result = re.test(emailObj.value);
	if (result == false){
		return errorHandler(emailObj, errorMes);
	}else{
		return true;
	}
    }
}
