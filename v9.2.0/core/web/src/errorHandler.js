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
// this function presents the error message
// then places focus on the offending object
// finally returns false so form does not submit
function errorHandler(myObj,mes){
	extAlert('Error Message', mes, Ext.MessageBox.ERROR);
	if(typeof(myObj.length) != 'undefined'  && myObj.length != null && myObj.length > 1 ){
		if(typeof(myObj[0]) != 'undefined' && myObj[0] != null){
			myObj[0].focus();
		}else{
			myObj.focus();
		}
	} else {
		myObj.focus();
	}
	return false;
}

function noFocusErrorHandler(myObj,mes){
	extAlert('Error Message', mes, Ext.MessageBox.ERROR);
	return false;
}

function populateField(fieldName, fieldValue, throwErrorIfFieldNotFound, throwErrorMsg) {
    var elementArray = document.getElementsByName(fieldName);

    if (elementArray == undefined || elementArray.length == 0) {
        if ((throwErrorIfFieldNotFound != undefined) && (throwErrorIfFieldNotFound == true)) {
            if (throwErrorMsg == undefined) {
                throwErrorMsg = "Error -- field " + fieldName + " not found";
            }
            extAlert(errorTitle, throwErrorMsg , Ext.MessageBox.ERROR);
        }
    } else if (elementArray.length == 1) {
        var element = elementArray[0];

        if (element.type == 'text' || element.type == 'hidden' || element.type == 'password' || element.type == 'submit' || element.type == 'reset') {
            element.value = fieldValue;
        } else if (element.type == 'select-one') {
            for (var i = 0; i < element.options.length; i++) {
                if (element.options[i].text == fieldValue || element.options[i].value == fieldValue) {
                    element.selectedIndex = i;
                }
            }
        } else if (element.type == 'select-multiple') {
            for (var i = 0; i < element.options.length; i++) {
                if (element.options[i].text == fieldValue || element.options[i].value == fieldValue) {
                    element.options[i].selected = true;
                }
            }
        } else if (element.type == 'radio' || element.type == 'checkbox') {
            if (element.value == fieldValue) {
                element.checked = true;
            }
        } else if (element.type == 'file') {
            //Nothing we can do here -- setting file name is not allowed by any browser
        } else if (element.type == 'image') {
            //Ignore this too, nothing we can do.
        } else {
        	var errorMessage = "Unhandled case, cannot handle single type " + element.type;
            extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
        }

    } else {
        if (elementArray[0].type == 'radio' || elementArray[0].type == 'checkbox') {
            for (var i = 0; i < elementArray.length; i++) {
                if (elementArray[i].value == fieldValue) {
                    elementArray[i].checked = true;
                }
            }
        } else {
         	var errorMessage = "Unhandled case, cannot handle multiple type "+elementArray[0].type;
            extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
        }
    }
}