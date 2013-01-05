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
function checkLength(myObj, min, errorMes){
	// check if was filled in and of at least min length
	if (myObj.value.length < min){
		errorHandler(myObj,errorMes);
	}else{
		return true;
	}
}

function checkMaxLength(myObject, maxLength, errorMessage) {
	// check that the length of the value of myObj is less than or equal
	// to specified maximum length
	if (myObject.value.length > maxLength) {
		if(errorMessage!=''){
			errorHandler(myObject, errorMessage);
		}else{
			myObject.value = myObject.value.substring(0,maxLength);
		}
	} else {
		return true;
	}
}
function checkMaxMinLength(myObject, maxLength,min ,errorMessage) {
	// check that the length of the value of myObj is less than or equal
	// to specified maximum length & the minimum length
	
	if(checkMaxLength(myObject,maxLength,errorMessage) && checkLength(myObject,min,errorMessage)){
		return true;
	} else {
		return false;
	}
	
}