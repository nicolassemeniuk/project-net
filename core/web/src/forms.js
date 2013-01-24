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
//Form.js
//
//Provides javascript functions and utilities specific to the forms module.


//Check that all the forms fields that are required are present.
//
//Parameters
//  fieldNames  - An associative array of fieldNames to field labels, for
//                example fieldNames['fieldName'] = "Field Name";
//  errorPrefix - Text that comes before the name of the field when the
//                error text is constructed.
//  errorSuffix - Text that comes after the name of the field when the
//                error text is constructed.
//Returns
//  true if no errors were found; false if errors were found.
function checkRequiredFields(fieldNames, errorPrefix, errorSuffix) {
    var okToSubmit = true;

    //Make sure that the error prefix and suffix are there so we don't get
    //"undefined" in our error string.
    if (!errorPrefix)
        errorPrefix = "";
    if (!errorSuffix)
        errorSuffix = "";

    //Find all the different tag types to check
    var childrenToCheck = new Array(3);
    childrenToCheck[0] = document.getElementsByTagName("input");
    childrenToCheck[1] = document.getElementsByTagName("select");
    childrenToCheck[2] = document.getElementsByTagName("textarea");

    //Iterate through everything in the form.  If there is a "required"
    //attibute, then do some validation.
    for (var j = 0; j < childrenToCheck.length && okToSubmit; j++) {
        var formChildren = childrenToCheck[j];

        for (var i = 0; i < formChildren.length && okToSubmit; i++) {
            var child = formChildren[i];
            //Necessary because firefox includes things like plain text as a
            //node.  We need to skip this stuff, because it doesn't have a
            //getAttribute method and it doesn't behave.
            if (!child.getAttribute) {
                continue;
            }

            //See if this field is required
            if (!child.getAttribute("required") || child.getAttribute("required") == "false") {
                continue;
            }

            //Now we have to figure out which validation to run.
            var type = child.type;
            if (type == "text" || type =="textarea") {
                //This is a bit strange for the Gecko rendering engine.
                //Normally, it can't use attributes as just .something
                //However, in the case of value, it doesn't even recognize
                //child.getAttribute("value");
                var value = child.value;
                var notEmpty = true;
                if (type == "text"){
                	notEmpty = Ext.util.Format.trim(value).length > 0;
                }
                if (!value || !notEmpty) {
                	var errorMessage = errorPrefix + fieldNames[child.getAttribute("name")] + errorSuffix;
                	extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
                    okToSubmit = false;
                } 
            } else if (type == "select-one" || type == "select-multiple") {
                var noneSelectedIndex = child.getAttribute("noneSelectedIndex");

                if (child.selectedIndex == -1 || (noneSelectedIndex && (child.selectedIndex == noneSelectedIndex))) {
                  	var errorMessage = errorPrefix + fieldNames[child.getAttribute("name")] + errorSuffix;
                	extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
                    okToSubmit = false;
                }
            } else {
            	var errorMessage = "Required field " + fieldNames[child.getAttribute("name")] + " of unknown type ("+type+") found, cannot validate";
                extAlert(errorTitle, errorMessage , Ext.MessageBox.ERROR);
                okToSubmit = false;
            }
        }
    }

    return okToSubmit;
}
