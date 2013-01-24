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
function checkAlphaNumeric(stringObj, errorMes){
/*    var re = /[^A-Za-z0-9@.]/
    var result = re.test(stringObj.value);
    if (result == true){
    	return errorHandler(stringObj, errorMes);
    }else{
    	return true;	
    }*/
	var str = stringObj.value;
	var i = 0;
	for (i = 0; i<str.length; i++) {
		var code = str.charCodeAt(i);
		if (!(
			((code >=65)&&(code<=90))||
			((code >=97)&&(code<=122))||
			((code >=48)&&(code<=57))||
			((code >=129)&&(code<=254))|| //for spec characters
			(code == 64) ||
			(code == 46) ||
			(code > 256)
			)) {
				return errorHandler(stringObj, errorMes);
		}
	}
	return true;
}

function checkUserName(stringObj, errorMes) {
	var re = /[a-zA-Z][-_.@a-zA-Z0-9]{3,31}/
	var result = re.test(stringObj.value);
	if (result) {
		return true;
	} else{
		return errorHandler(stringObj, errorMes);
	}
}

function checkAlphaNumericLowercase(stringObj, errorMes){
    var re = /[^a-z0-9@.]/
    var result = re.test(stringObj.value);
    if (result == true){
    	return errorHandler(stringObj, errorMes);
    }else{
    	return true;	
    }
}

// To check for valid postal code allowing space with alphanumeric values
function checkPostalCode(stringObj, errorMes){
	var re = /^[\w ]+$/; 
    if (!re.test(stringObj.value)){
    	return errorHandler(stringObj, errorMes);
    }else{
    	return true;	
    }
}

// To check for vlaid phone no. allowing following characters 
// digits (48-57), space (32), + (43), - (45), # (35)
function checkPhoneNo(stringObj, errorMes){
   var str = stringObj.value;
   var i = 0;
   for (i = 0; i<str.length; i++) {
		var code = str.charCodeAt(i);
		if (!(((code >=48)&&(code<=57))||
			(code == 32) || (code == 45) || (code == 35)|| (code == 43) || (code == 40)|| (code == 41))) {
				return errorHandler(stringObj, errorMes);
		}
	}
	return true;
}
