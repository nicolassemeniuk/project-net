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
function checkRange(obj, low, high, errorMes, decimalSeparator){
	var num = trim(obj.value);
	if(decimalSeparator)
		num = num.replace(decimalSeparator, '.');
	if(isNaN(num))
		errorHandler(obj, errorMes);
	else {
		num = parseFloat(num)
		if(low <= num && num <= high)
			return true;
    }
	return errorHandler(obj, errorMes);
}
//The following function should not be used, instead use the similar tokenized version of this function below
function checkRangeInt(obj, low, high, errorMes, decimalSeparator){
    checkRangeInt(obj, low, high, errorMes, "Percentage must be an integer", decimalSeparator);
}	
//Use the token "prm.schedule.taskview.resources.percentagerange.integer.message" for integerErrMes
function checkRangeInt(obj, low, high, errorMes, integerErrMes, decimalSeparator){
	var num = trim(obj.value);
	if(num.substring(num.length-1, num.length) == "%") {
		num = num.substring(0, num.length-1);
	}
	if(decimalSeparator)
		num = num.replace(decimalSeparator, '.');
	if(isNaN(num))
		return errorHandler(obj, errorMes);
	else {
		num = parseFloat(num)
		if(low <= num && num <= high)
			return true;
    }
	return errorHandler(obj, errorMes);
}

function checkRangeNotBlank(obj, low, high, errorMes, decimalSeparator){
	var num = trim(obj.value);
	if(num != "") {
		if(decimalSeparator)
			num = num.replace(decimalSeparator, '.');
	    if(isNaN(num))
			errorHandler(obj, errorMes);
	    else {
			num = parseFloat(num)
			if(low <= num && num <= high)
				return true;
        }
    }
	return errorHandler(obj, errorMes);
}	
