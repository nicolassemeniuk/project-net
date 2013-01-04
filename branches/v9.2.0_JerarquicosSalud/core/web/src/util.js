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
var endl = "\n";
var isFireFox = navigator.userAgent.match(/gecko/i);
/**
 * Focus on the first non-hidden field in a form.
 */
function focusFirstField(theForm) {
    var fieldName = null;
        
    if (theForm && theForm.elements) {
        // Find the first non-hidden field on the form
        for (var i = 0; i < theForm.elements.length; i++) {
            if (theForm.elements[i].type && theForm.elements[i].type == "hidden") {
                // skip it
            } else {
                fieldName = theForm.elements[i].name;
                break;
            }
        }
    }

    if (fieldName != null) {
        focus(theForm, fieldName);
    }
}

/**
 * Convert Url string by replacing & with it's ascii value. 
 */
function escapeUrl(url) {
   while ( url.indexOf('&') != -1) {
       url = url.replace('&','$0026');
   }
   
   while ( url.indexOf('!') != -1) {
       url = url.replace('!','$0021');
   }
   return url;
}   

/**
 * Focus on the named field in a form.
 */
function focus(theForm, fieldName) {
    
    if (theForm && theForm.elements[fieldName]) {
        theForm.elements[fieldName].focus();
    }
}

function theAction (myAction) {

	   theForm.theAction.value = myAction;
}

function ErrorObj (isOk) {

	if (isOk == true) 
	   this.isOk = true;
	else
	   this.isOk = false;

	this.msg = "";
}

function verifyURL (myURL) {

	var re = new RegExp ("^((http://.*)|(https://.*)|(ftp://.*))");
	var re3 = new RegExp("\\.{2,}");
	var re2 = new RegExp ("\\.{1}");
	var whiteSpace = new RegExp("\\s");

	var err = new ErrorObj();

	err.isOk = false;

	myURL = trim(myURL);

	if (myURL != null && myURL != "") {

		if (myURL.search(re) != -1) 
			err.isOk = true;
		else {
			myURL = "http://" + myURL;		
		
			if (myURL.search(re) != -1)
				err.isOk = true;
			else
				err.isOk = false;
		}
	
		if (err.isOk)
			if (myURL.search(whiteSpace) != -1)
				err.isOk = false;

			//  else if (myURL.search(re2) == -1)
			// 	err.isOk = false;

			else if (myURL.search(re3) != -1)
				err.isOk = false;
	} else
		err.isOk = true;

	err.msg = "* \"" + myURL + "\" is not a valid URL format"; 
	return err;
}


function verifyURLNoErr (myURL) {

	var re = new RegExp ("^((http://.*)|(https://.*)|(ftp://.*))");
	var re3 = new RegExp("\\.{2,}");
	var re2 = new RegExp ("\\.{1}");
	var whiteSpace = new RegExp("\\s");
	var isOK = false;

	myURL = trim(myURL);

	if (myURL != null && myURL != "") {

		if (myURL.search(re) != -1) {
			isOk = true;
		}
		else {
		    isOk = false;
		}
	

		if (isOk) {
		    if (myURL.search(whiteSpace) != -1)
			isOk = false;

		    //  else if (myURL.search(re2) == -1)
		    // 	err.isOk = false;

		    else if (myURL.search(re3) != -1)
			isOk = false;
		}
	} else
		isOk = false;

	return isOk;
}



function verifyDateErr (field) {

	var date = field.value;
	var dateArray = date.split("/");
	var err = new ErrorObj(true);
	var theDate = new Date(date);

	var d1 = new String(theDate.getDate());
	var zeros = new RegExp("0");

	var tmp = new String(dateArray[1])
	tmp = tmp.replace(zeros, "");

	var d2 = new RegExp(tmp);
	
	
	if (date != null && date != "") { 

	d1 = d1.replace(zeros, "");
	
	if (d1.search(d2) == -1) {
		err.msg = date + " is not a valid date";
		err.isOk = false;
	}	

	    else if (dateArray.length != 3) {
	       err.msg = "Date format incorrect.\nPlease re-enter your date as MM/DD/YYYY.";
	       err.isOk = false;
	   }
	   else if ((dateArray[0] < 1) || (dateArray[0] > 12)) {
		   err.msg = "You entered a date that does not exist.\nPlease enter a valid date \(MM/DD/YYYY\).";
		 err.isOk = false;
	    }
	   else if ((dateArray[1] < 1) || (dateArray[1] > 31)) {
		   err.msg = "You entered a date that does not exist.\nPlease enter a valid date \(MM/DD/YYYY\).";
	 	   err.isOk = false;
	   }
	   else if (dateArray[2].length != 4)  {
		   err.msg = "You entered a year that is not Y2K compliant.\nPlease enter a 4-digit year \(MM/DD/YYYY\).";
		   err.isOk = false;
	   }
	}
	
	if (!err.isOk) {
		//eval ("document.forms[0]." + field.name + ".value = \"\";");
      eval ("self.document.forms[0]." + field.name + ".focus();");
      eval ("self.document.forms[0]." + field.name + ".select();");
	}
   
  return err;

}


function isMenuSelected (field) {
   
   // Usage: isSelected(field_object)
   // Example:  if (isSelected(document.forms[0].selectField)) . . .

  if (field.selectedIndex == -1)
     return false;   
  else
     return true;
}

/**
 * Selects a particular radio group entry that has the specified value.
 * @param radioGroup the actual radio input object to select
 * @param defaultValue the value of the radio element that is to be selected
 */
function selectRadio(radioGroup, value) {
   if (radioGroup) {
	  if (radioGroup.length) {
		 for (var i = 0; i < radioGroup.length; i++) {
			if (radioGroup[i].value == value) {
			   radioGroup[i].checked = true;
			}
		 }
	  } else {
		 radioGroup.checked = true;
	  }
   }
}

// This function replaces all instances of findStr in oldStr with repStr.
// ReplaceAll courtesy http://pachome1.pacific.net.sg/~firehzrd/jsindex.html
function replaceAll(oldStr,findStr,repStr) {
    var srchNdx = 0;  // srchNdx will keep track of where in the whole line
                      // of oldStr are we searching.
    var newStr = "";  // newStr will hold the altered version of oldStr.
    while (oldStr.indexOf(findStr,srchNdx) != -1) {
        newStr += oldStr.substring(srchNdx,oldStr.indexOf(findStr,srchNdx));
        newStr += repStr;
        srchNdx = (oldStr.indexOf(findStr,srchNdx) + findStr.length);
    }
    newStr += oldStr.substring(srchNdx,oldStr.length);
    // Put whatever's left into newStr.
    return newStr;
}

function changeCheckedState(checkboxField, state) {
    if (checkboxField) {
        if (checkboxField.length) {
            for (var i = 0; i < checkboxField.length; i++) {
                checkboxField[i].checked = state;
            }
        } else {
            checkboxField.checked = state;
        }
    }
}

function toggleCheckedState(checkboxField, field) {
    if (checkboxField) {
		var overallState = true;
        if (checkboxField.length) {
            for (var i = 0; i < checkboxField.length; i++) {
                overallState &= checkboxField[i].checked;
            }
        } else {
            overallState = checkboxField.checked;
        }
		field.checked = overallState;
    }
}

function checkCheckboxes(checkboxField, valueList) {
    //If there isn't a list of values to select, we are done.
    if (!valueList) {
        return;
    }

    //Make a space delimited field of values from valuelist
    valueList = replaceAll(valueList,","," ");
    valueList += " ";

    var currentID;

    if (checkboxField) {
        if (checkboxField.length) {
            for (var i = 0; i < checkboxField.length; i++) {
                currentID = checkboxField[i].value;
                if (valueList.indexOf(currentID) != -1) {
                    checkboxField[i].checked = true;
                }
            }
        }
    }
}

// Selects a particular select field based on its value
function selectSelect(selectField, value) {
   if (selectField) {
	  if (selectField.length) {
		 for (var i = 0; i < selectField.length; i++) {
			if (selectField[i].value == value) {
			   selectField[i].selected = true;
			}
		 }
	  } else {
		 selectField.selected = true;
	  }
   }
}

// Returns the current value of a field called "selected"
// This field may be a text field or a radio group
function getSelection(theForm) {
 var idval;
   if(!theForm) {
   		 // Form is undefined
        // idval remains undefined
	  } else {
	   if (theForm.selected) {
		   if (theForm.selected.length) {
			   for (var i = 0; i < theForm.selected.length; i++) {
				  if (theForm.selected[i].checked == true) {
					idval = theForm.selected[i].value;
					break;
				  }
			   }
		   } else {
			  if (theForm.selected.checked == true) {
				idval = theForm.selected.value;
			  }
		   }
	   }
   }
   return idval;
}

// Returns the selected value of a field called "selected" form end
// This field may be a text field or a radio group
function getSelectionFromEnd(theForm) {
 var idval;
   if(!theForm) {
   		 // Form is undefined
        // idval remains undefined
	  } else {
	   if (theForm.selected) {
		   if (theForm.selected.length) {
			   for (var i = theForm.selected.length-1; i >= 0; i--) {
				  if (theForm.selected[i].checked == true) {
					idval = theForm.selected[i].value;
					break;
				  }
			   }
		   } else {
			  if (theForm.selected.checked == true) {
				idval = theForm.selected.value;
			  }
		   }
	   }
   }
   return idval;
}

// Return the value of a field; field may be multi-select, single-select,
// radio, checkbox, other
function getSelectedValue (theField) {
    var retval;

    if (!theField) {
        // Field is undefined
        // retval remains undefined
    } else {
        if ((!theField.type) || theField.type == 'radio') {
            // Undefined field types are assumed to be radio groups
            // For backwards compatibility
            retval = getSelectedRadioValue(theField);
        } else if (theField.type == 'select-one' || theField.type == 'select-multiple') {
            retval = getSelectedSelectValue(theField);
        } else if (theField.type == 'checkbox') { 
			//logic should be same as for radio!!
            retval = getSelectedRadioValue(theField)
        } else {
            // Not a radio or select list or checkbox
            if (theField.value) {
                retval = theField.value;
            }
        }
    }
    
    return retval;
}

function getSelectedRadioValue(theField) {
    var retval;
    
    if (theField) {
        if (!theField.length) {
            // One item radio group
            retval = theField.value;
        } else {
            // Find the checked value
            for (var i = 0; i < theField.length; i++) {
                if (theField[i].checked) {
        	        retval = theField[i].value;
      	            break;
                }
    	    }
        }
    }
    
    return retval;
}

function getSelectedSelectValue(theField) {
    var retval;
    
    if (theField) {
        if (!theField.length) {
            // One item select
            retval = theField.value;
        } else {
            // Find the selected value
            for (var i = 0; i < theField.length; i++) {
                if (theField[i].selected) {
        	        retval = theField[i].value;
      	            break;
                }
    	    }
        }
    }
    
    return retval;
}

// Sets a field based on the specified value
// For radio groups and select lists, the element with that value is selected
// For checkboxes, the value should be boolean
// For all other fields, the value is simply assigned
function setSelectedValue (theField, theValue) {
    if (theField) {
        if (theField.type == 'radio') {
            selectRadio(theField, theValue);
        } else if (theField.type == 'select-one' || theField.type == 'select-multiple') {
            selectSelect(theField, theValue);
        } else if (theField.type == 'checkbox') {
            if (theValue) {
                theField.checked = true;
            } else {
                theField.checked = false;
            }
        } else {
            // Not a radio or select list or checkbox
            if (theField.value) {
                theField.value = theValue;
            }
        }
    }
}

function verifySelection (theForm, type, noSelectionErrMes, multiSelectionErrMes, multiOnlyErrMes, noAlert) {
   var field = theForm.elements["selected"];
   if (field != null) {
       return verifySelectionForField(field, type, noSelectionErrMes, multiSelectionErrMes, multiOnlyErrMes, noAlert);
   }
}

function verifySelectionForField(field, type, noSelectionErrMes, multiSelectionErrMes, multiOnlyErrMes, noAlert) {
   var retval = false;
   var myCheckBox = false;
   var numElements = 0;
   var isMulti = false;
   
   if (!noSelectionErrMes)
       noSelectionErrMes =  "Please select an item from the list";

   if (!multiSelectionErrMes) 
       multiSelectionErrMes = "This operation only supports single item selection.  Please select only one item from the list";

   if (!multiOnlyErrMes)
       multiOnlyErrMes = "This operation requires that you select at least two items."

  if (!noAlert){
	noAlert = false;  
  }else {
	noAlert = true;  	  
  }
    	   
   if (field) {
       if (field.checked) {
           retval = true
       } else {
           for (var i = 0; i < field.length; i++) {
               if (field[i].checked == true) {
                   numElements++;
               }
           }
       }
   } else {
       numElements = 0;
   }

   if (type == "multiple" || type == "multi") {

      if (numElements < 1)
         errorMsg = noSelectionErrMes; //errorMsg = "Please select an item from the list";
      else
         retval = true;

   } else if (type == "multionly" || type == "multipleonly") {

      if (numElements < 2)
         errorMsg = multiOnlyErrMes;
      else
         retval = true;

   } else if (type == "oneorzero") {
      if (numElements > 1) {
         errorMsg = noSelectionErrMes;
      } else {
         retval = true;
      }

   } else {

      if (numElements < 1)
         errorMsg = noSelectionErrMes;//errorMsg = "Please select an item from the list";
      else if (numElements > 1)
         errorMsg = multiSelectionErrMes;//errorMsg = "This operation only supports single item selection.  Please select only one item from the list";
      else
         retval = true;

   } // end else
   
   if (!retval ){	   
	   if(!noAlert){	   
   		  extAlert(errorTitle, errorMsg , Ext.MessageBox.ERROR);
	   }
   }

   return retval;
}

function verifyAdjoiningSelection(theForm, errorMes) {
    var startSelection = false;
    var endSelection = false;
    var retval = true;

    if (!errorMes)
        errorMes = "You may only select checkboxes which are next to each other."

    for (var i=0; i < theForm.selected.length; i++) {
        if ((!startSelection) && theForm.selected[i].checked == true) {
            startSelection = true;
        } else if (startSelection && (!endSelection) && theForm.selected[i].checked == false) {
            endSelection = true;
        } else if (endSelection && theForm.selected[i].checked == true) {
            retval = false;
            break;
        }
    }

    if (!retval)
	    extAlert(errorTitle, errorMes , Ext.MessageBox.ERROR);

    return retval;
}

function adjoiningSelectionFirstIndex(theForm) {
    for (var i = 0; i < theForm.selected.length; i++) {
        if (theForm.selected[i].checked == true) {
            return i;
        }
    }

    return -1;
}

function clearSelect(select) {

	var mySelect;
	eval ("mySelect = theForm." + select);

	for (var i=0; i < mySelect.length; i++)
		mySelect.options[i].selected = false;
}

function clearSelection(selectField) {
   if (selectField) {
	  if (selectField.length) {
		 for (var i = 0; i < selectField.length; i++) {
           selectField[i].selected = false;
		 }
	  } else {
		 selectField.selected = false;
	  }
   }
}

/**
  * Pop up calendar window.
  * Parameters:
  * 	dateField	- (required) name of field containing date value
  * 	JSPRootURL	- (optional) used for correct location of calendar JSP
  * 				  If not specified, assumes calendar package is at "../calendar" wrt current JSP
  * 	formName	- (optional) name of form on which input element with name dateFieldName is drawn
  */
function autoDate(dateFieldName, JSPRootURL, formName) {
	var str;
	var calendarURL = '/calendar/MiniMonthPopup.jsp';

	// If we have a JSP root url, use it.  Otherwise, we might be on a server
	// that allows us to be located at '/' 	
	if (arguments.length > 1 && JSPRootURL && JSPRootURL != null && JSPRootURL != "") {
		calendarURL = JSPRootURL + calendarURL;
	}

	// Use the form name or the 0th form if none specified
	if (arguments.length > 2) {
		str = "window.dateField = self.document.forms['" + formName + "']." + dateFieldName +"\;";
	} else {
		str = "window.dateField = self.document.forms[0]." + dateFieldName +"\;";
	}
		
	var object = eval (str);
	if(object.disabled)
		return;
	calendar = window.open(calendarURL, 'cal', 'width=180,height=200');
    calendar.focus();
}

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

function isNumber(field) {

   var re = new RegExp("\\D");
   var myField = field;
   var isOk = true;

   if (myField.search(re) != -1) {
	isOk = false;
   }

   return isOk;
}

function verifyNonBlankField (text) {

	var isOk = true;

	if ((trim(text)).length == 0) {
		isOk = false;
	}
	return isOk;
}

function verifyNonBlankField_withAlert (fieldName, message) {

// fieldName = document.FormName.FieldName
// Message = the error message to be displayed.

	var isOk = true;
	text= fieldName.value;

	if ((trim(text)).length == 0) 
	{
	    extAlert(errorTitle, message , Ext.MessageBox.ERROR);
		fieldName.focus();
 		fieldName.select();
		isOk = false;
	}
	return isOk;
}

function resetFocus() {
   //theForm.target = "main";
}

function setStandardToolbarActionWindow(window) {
   top.toolbar_main.actionwin = window;
}

function setTransientToolbarActionWindow(window) {
   top.toolbar_transient.actionwin = window;
}

function getWindowManager() {

   var theWM = top.windowManager;

   return theWM;

}

function setVar(name, value, to_expire, path) {
   var today = new Date()
   var expires = new Date()
 
   if (to_expire == null) {
      // Expires one (1) day from now
      expires.setTime(today.getTime() + 1000*60*60*24);
   } else
      expires.setTime(today.getTime() + 1000*60*60*24*to_expire);

   self.setCookie(name, value, expires, path);
}


function getVar(name) {
  var cookieValue = self.getCookie(name);

  //If cookie doesn't exist, explicitly return an empty string instead of
  //undefined.
  if (!cookieValue)
      cookieValue = "";

  return cookieValue;
}

// Like setVar, but erases any existing cookie at current page
// This is useful when the path is set to be less specific
// prevents multiple cookies from being available at same path
// Used on Login page to ensure that there is only ever 1 cookie
// with a certain name
function replaceVar(name, value, to_expire, path) {
   // Set date in past to erase cookie
   var pastDate = new Date("July 9, 1975");
   self.setCookie(name, "", pastDate);

   // Now set real value at new path
   setVar(name, value, to_expire, path);
}

function getCookie(Name) {
   var search = Name + "="
   if (document.cookie.length > 0) { // if there are any cookies
      offset = document.cookie.indexOf(search) 
      if (offset != -1) { // if cookie exists 
         offset += search.length 
         // set index of beginning of value
         end = document.cookie.indexOf(";", offset) 
         // set index of end of cookie value
         if (end == -1) 
            end = document.cookie.length
         return unescape(document.cookie.substring(offset, end))
      } 
   }
}

// Sets cookie
// name - String name of cookie (required)
// value - value (required)
// expires - Date of expiry (optional)
// path - String path (optional)
// domain - String domain (optional)
function setCookie(name, value, expires, path, domain) {
    var addCookie = name + "=" + escape(value) +
		((expires) ? "; expires=" + expires.toGMTString() : "") +
		((path) ? "; path=" + path : "") +
		((domain) ? "; domain=" + domain : "");
    document.cookie = addCookie;
}

function refresh_banner() {
	// this forces the refresh of the top banner
	 top.toolbar_main.location.replace(getVar("JSPRootURL") + "/toolbar/Main.jsp");

}

function refresh_banner(myspace) {
	// this forces the refresh of the top banner
	 top.toolbar_main.location.replace(getVar("JSPRootURL") + "/toolbar/Main.jsp?space="+myspace);

}

// Sets a named field value to 'true' or 'false' based on selection of a checkbox
function setBoolean(checkboxField, namedField) {
    if (checkboxField && namedField) {
    	if (checkboxField.checked) {	
    		namedField.value = 'true';
    	} else {
    		namedField.value = 'false';
    	}
    }    
}

function hiLite() {
    var row;

    for (var i = 0; i < arguments.length; i++) {
        row = document.getElementById(arguments[i]);

        if (row.className.search(/\browHighlight\b/) == -1) {
            row.className += ' rowHighlight';
        }
    }
}

function unLite() {
    var row;

    for (var i = 0; i < arguments.length; i++) {
        row = document.getElementById(arguments[i]);
        row.className = row.className.replace(/\b rowHighlight\b/, "");
        row.className = row.className.replace(/\browHighlight\b/, "");
    }
}

// Formats the specified checkboxElement's values as multiple parameter values
// in the form parameterName=value&parameterName=value&...
// This is useful for constructing a query string similar to what is submitted during a form submit
// The checkboxElement's name is used as the parameter name, unless parameterName is specified
// No leading "&" is included
// Handles case where only one value is checked
function formatQueryParameters(checkboxElement, parameterName) {
    var result;

    if (arguments.length == 1) {
        // We need to use the element's name
        if (!checkboxElement.length) {
            parameterName = checkboxElement.name;
        } else {
            parameterName = checkboxElement[0].name;
        }
    }

    if (!checkboxElement.length) {
        // One item select
        result = parameterName + "=" + checkboxElement.value;
    } else {
        result = "";
        // Find the selected value
        for (var i = 0; i < checkboxElement.length; i++) {
            if (checkboxElement[i].checked) {
                if (result.length > 0) {
                    result += "&";
                }
                result += parameterName + "=" + checkboxElement[i].value;
            }
        }
    }

    return result;
}

/* This function should check an array for duplicate entries */
function checkArrayDuplicates(array, errorMes){
/*
 	var object = {};
	
	for (var i in array) {
		if (object[array[i].value]) {
			errorHandler(array[i], errorMes);
			return true;
		}
		object[array[i].value] = true;
	}
	return false;
	
 */
	// bug-2188 fix Start
	for(var i = 0; i < array.length - 1; i++) {
		for(var j = i+1; j < array.length; j++) {
			if(array[i].value == array[j].value) {
				errorHandler(array[i], errorMes);
				return true;				
			}
		}
	}
	return false;
	// bug-2188 fix End

}

/* returns xml dom from the xml string */
function getXMLObject(text) {
	var xmlDoc;
	try {
	  //Firefox, Mozilla, Opera, etc.
	  var parser=new DOMParser();
	  xmlDoc=parser.parseFromString(text,"text/xml");
	} catch(e) {
	  try {
		  //Internet Explorer
		  xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
		  xmlDoc.async="false";
		  xmlDoc.loadXML(text);
	  } catch(e) {
	  }
	}
	return xmlDoc;
}

function applyColorToEvenRows(){
	document.all ? applyColorToEvenRows_IE(true) : applyColorToEvenRows_FF(true);	
}

function applyColorToEvenRows_only(){
	document.all ? applyColorToEvenRows_IE(false) : applyColorToEvenRows_FF(false);
		
}

// applying the row color to even rows for tables on dashboard pages
function applyColorToEvenRows_FF(isRemoveTdClass){
	var tables = document.getElementsByName('tableWithEvenRows');
	for(var tindex = 0; tindex < tables.length; tindex++){
		tables[tindex].className = isRemoveTdClass ? 'row-content' : tables[tindex].className + ' row-content';
		var rows = tables[tindex].getElementsByTagName('tr');		
		for(var index = 0; index < rows.length; index++){			
		    if(index % 2 == 0 && index != 0) {
		        rows[index].className = isRemoveTdClass ? 'dashboard-even' : rows[index].className + ' dashboard-even';
		    } else {
			    if(index) rows[index].className = isRemoveTdClass ? '' :  rows[index].className;
		    }
		    if(index > 0 && isRemoveTdClass){
			    var cols =  rows[index].getElementsByTagName('td');
			    for(var cindex = 0; cindex < cols.length; cindex++){
			    	cols[cindex].className = '';
			    }
		    } else if(isRemoveTdClass) {
			    rows[index].className = 'table-header';
		    	var cols =  rows[index].getElementsByTagName('td');
			    for(var cindex = 0; cindex < cols.length; cindex++){
			    	cols[cindex].className = '';
			    }
		    }
		}
	}
	
	var tables = document.getElementsByName('tableWithEvenRowsWithTwoHRows');
	for(var tindex = 0; tindex < tables.length; tindex++){
		tables[tindex].className = 'row-content';
		var rows = tables[tindex].getElementsByTagName('tr');		
		for(var index = 2; index < rows.length; index++){			
		    if(index % 2 != 0) {
		        rows[index].className = 'dashboard-even';
		    } else {
			    rows[index].className = '';
		    }
		    if(index){
			    var cols =  rows[index].getElementsByTagName('td');
			    for(var cindex = 0; cindex < cols.length; cindex++){
			    	cols[cindex].className = '';
			    }
		    }
		}
	}
	
	var tables = document.getElementsByName('tableWithEvenRowsWOH');
	for(var tindex = 0; tindex < tables.length; tindex++){
		tables[tindex].className = 'row-content';
		var rows = tables[tindex].getElementsByTagName('tr');		
		for(var index = 1; index < rows.length; index++){			
		    if(index % 2 != 0) {
		        rows[index].className = isRemoveTdClass ? 'dashboard-even' : rows[index].className + ' dashboard-even ' ;
		    } else {
			    rows[index].className = isRemoveTdClass ? '' : rows[index].className;
		    }
		    if(index){
			    var cols =  rows[index].getElementsByTagName('td');
			    for(var cindex = 0; cindex < cols.length; cindex++){
			    	cols[cindex].className = '';
			    }
		    }
		}
	}	
}

// applying the row color to even rows for tables on dashboard pages
function applyColorToEvenRows_IE(isRemoveTdClass){
	var tables = document.getElementsByTagName('table');
	for(var tindex = 0; tindex < tables.length; tindex++){
		if(tables[tindex].name == 'tableWithEvenRows'){
			tables[tindex].className = isRemoveTdClass ? 'row-content' : tables[tindex].className + ' row-content';
			var rows = tables[tindex].getElementsByTagName('tr');		
			for(var index = 0; index < rows.length; index++){			
			    if(index % 2 == 0 && index != 0) {
			        rows[index].className = isRemoveTdClass ? 'dashboard-even' : rows[index].className + ' dashboard-even';
			    } else {
				    if(index) rows[index].className = '';
			    }
			    if(index > 0 && isRemoveTdClass){
				    var cols =  rows[index].getElementsByTagName('td');
				    for(var cindex = 0; cindex < cols.length; cindex++){
				    	cols[cindex].className = '';
				    }
			    } else if(isRemoveTdClass){
				    rows[index].className = 'table-header';
			    	var cols =  rows[index].getElementsByTagName('td');
				    for(var cindex = 0; cindex < cols.length; cindex++){
				    	cols[cindex].className = '';
				    }
			    }
			}
		} else if(tables[tindex].name == 'tableWithEvenRowsWithTwoHRows'){
			var rows = tables[tindex].getElementsByTagName('tr');
			tables[tindex].className = 'row-content';	
			for(var index = 2; index < rows.length; index++){			
			    if(index % 2 != 0) {
			        rows[index].className = 'dashboard-even';
			    } else {
				    rows[index].className = '';
			    }
			    if(index){
				    var cols =  rows[index].getElementsByTagName('td');
				    for(var cindex = 0; cindex < cols.length; cindex++){
				    	cols[cindex].className = '';
				    }
			    }
			}
		} else if(tables[tindex].name == 'tableWithEvenRowsWOH'){
			var rows = tables[tindex].getElementsByTagName('tr');
			tables[tindex].className = 'row-content';	
			for(var index = 1; index < rows.length; index++){			
			    if(index % 2 != 0) {
			        rows[index].className = isRemoveTdClass ? 'dashboard-even' : rows[index].className + ' dashboard-even ';
			    } else {
				    rows[index].className = isRemoveTdClass ? '' : rows[index].className;
			    }
			    if(index){
				    var cols =  rows[index].getElementsByTagName('td');
				    for(var cindex = 0; cindex < cols.length; cindex++){
				    	cols[cindex].className = '';
				    }
			    }
			}
		}
	}
}

// To find y position of any dom object 
function findPosY(obj) {
    var curtop = 0;
    try{
	    if(obj.offsetParent)
	        while(1) {
	          curtop += obj.offsetTop;
	          if(!obj.offsetParent)
	            break;
	          obj = obj.offsetParent;
	        }
	    else if(obj.y){
	        curtop += obj.y;
		}
	}catch(err){curtop = 30;}
	return curtop;
}


//returns the absolute position of DOM element within document
function getElementAbsolutePos(element) {
    var res = new Object();
    res.x = 0; res.y = 0;
    if (element !== null) {
        res.x = element.offsetLeft;
        res.y = element.offsetTop;
        
        var offsetParent = element.offsetParent;
        var parentNode = element.parentNode;

        while (offsetParent !== null) {
            res.x += offsetParent.offsetLeft;
            res.y += offsetParent.offsetTop;

            if (offsetParent != document.body && offsetParent != document.documentElement) {
                res.x -= offsetParent.scrollLeft;
                res.y -= offsetParent.scrollTop;
            }
            //next lines are necessary to support FireFox problem with offsetParent
            if (isFireFox) {
                while (offsetParent != parentNode && parentNode !== null) {
                    res.x -= parentNode.scrollLeft;
                    res.y -= parentNode.scrollTop;
                    
                    parentNode = parentNode.parentNode;
                }    
            }
            parentNode = offsetParent.parentNode;
            offsetParent = offsetParent.offsetParent;
        }
    }
    return res;
}

// method to move popup to screens scroll postion
function movePopup(screenDivObj, dialogueDivObj) {
	if(screenDivObj != null){
		if(typeof(screenDivObj) != 'undefined' && typeof(screenDivObj.style) != 'undefined' && screenDivObj.style.display == 'block'){
			var yMenuFrom, yMenuTo, yOffset, windowHeight;
			var scrOfX = 0, scrOfY = 0;
			if(typeof(window.pageYOffset) == 'number') {
				//Netscape compliant
				windowHeight = window.innerHeight;
				scrOfY = window.pageYOffset;
				yMenuFrom = findPosY(dialogueDivObj);
			} else if(document.body && document.body.scrollTop) {
				//DOM compliant
				scrOfY = document.body.scrollTop;
				yMenuFrom = findPosY(dialogueDivObj);
			} else if(document.documentElement && document.documentElement.scrollTop) {
				//IE6 standards compliant mode
				scrOfY = document.documentElement.scrollTop;
				yMenuFrom = findPosY(dialogueDivObj);
			}
			yMenuTo = scrOfY + screenTop;		
			if (yMenuFrom != yMenuTo) {
				yOffset = Math.ceil(Math.abs(yMenuTo - yMenuFrom));
				
				if (yMenuTo < yMenuFrom) {
					yOffset = -yOffset;
				}
				if(scrOfY != null)
					screenDivObj.style.top = scrOfY + 'px';
				if(typeof(windowHeight) == 'undefined'){
					windowHeight = window.screen.height - 173;// for IE
				}
				screenDivObj.style.height = windowHeight + "px";
				
				if(setNow && typeof(yMenuFrom) != 'undefined' && !isNaN(yOffset)){
					dialogueDivObj.style.top = yMenuFrom + yOffset+"px";				
				}
			}
		}
	}
}

function verifyNoHtmlContent(name, message){
	if(name.indexOf('<') > -1 || name.indexOf('>') > -1){
		extAlert(errorTitle, message , Ext.MessageBox.ERROR);
		return false;
	}else{
		return true;
	}
}

// format a string by replacing the placeholders with given arguments.
if (typeof(msgFormat) == 'undefined') {
	msgFormat = new Object();
  	msgFormat.format=function(str){
	    var i = 1;
	    while(i < arguments.length) str = str.replace("{" +(i-1)+ "}", arguments[i++]);
	    return str;
  	}
}

//This is to fire any event of any elements
function fireEvent(element, event){
    if (document.createEventObject){
        // dispatch for IE
        var evt = document.createEventObject();
        return element.fireEvent('on'+event,evt)
    } else{
        // dispatch for firefox + others
        var evt = document.createEvent("HTMLEvents");
        evt.initEvent(event, true, true ); // event type,bubbling,cancelable
        return !element.dispatchEvent(evt);
    }
}
//to get multiple selection with desired join.
function getSelections(theForm, toJoin){
var idval = '';
   if(!theForm) {
   		 // Form is undefined
        // idval remains undefined
	} else {
	   if (theForm.selected) {
		   if (theForm.selected.length) {
			   for (var i = 0; i < theForm.selected.length; i++) {
				  if (theForm.selected[i].checked == true) {
					idval += theForm.selected[i].value + toJoin;
				  }
			   }
		   } else {
			  if (theForm.selected.checked == true) {
				idval = theForm.selected.value;
			  }
		   }
	   }
   }
   return idval;
}

//This is method to convert java date pattern as js compatible date pattern.
function getJSUserDatePattern(pattern){
	//For the Deutsch locale the pattern is 'tt.MM.jj' instead of 'tt.MM.uu' 
	// so in this case. 
	if(pattern == 'tt.MM.jj'){
		//consider this pattern as 'tt.MM.uu' and return corresponding JS date pattern;
		return 'd.m.y';
	}
	//Otherwise following code is capable to convert all locale date pattern to supported JS date pattern..
	//Year pattern
	pattern = replaceAll(pattern, "yyyy", "Y");
	pattern = replaceAll(pattern, "yy", "y");
	pattern = replaceAll(pattern, "uuuu", "Y");
	pattern = replaceAll(pattern, "uu", "y");
	pattern = replaceAll(pattern, "aaaa", "Y");
	pattern = replaceAll(pattern, "aa", "y");
	pattern = replaceAll(pattern, "bb", "y");
	//month Pattern		             
	pattern = replaceAll(pattern, "MM", "m");
	pattern = replaceAll(pattern, "M", "n");
	pattern = replaceAll(pattern, "mm", "m");
	pattern = replaceAll(pattern, "nn", "m");
	//date Pattern		             		             
	pattern = replaceAll(pattern, "t", "j");
	pattern = replaceAll(pattern, "LL", "d");
	if(pattern.indexOf("dd") != -1 || pattern.indexOf("jj") != -1 || pattern.indexOf("tt") != -1){
		pattern = replaceAll(pattern, "jj", "d");
		pattern = replaceAll(pattern, "tt", "d");
		pattern = replaceAll(pattern, "dd", "d");
	}else{
		pattern = replaceAll(pattern, "d", "j");
	}
	return pattern;
}

function setSelections(theForm, ids) {
	if(!theForm && !ids) {
   		// Form is undefined
        // ids is undefined
	} else {
		var idArray = ids.split(",");
		if (theForm.selected) {
			for (var i = 0; i < idArray.length; i++) {
				if (theForm.selected.length) {
					for (var j = 0; j < theForm.selected.length; j++){
						if (theForm.selected[j].value == idArray[i]) {
							theForm.selected[j].checked = true;
						}
					}
				} else {
					if (theForm.selected.value == idArray[i]) {
						theForm.selected.checked = true;
					}
			   }
		   }
	   }
   }
}

//returns the absolute position of some element within document
function getElementAbsolutePos(element) {
    var res = new Object();
    res.x = 0; res.y = 0;
    if (element !== null) {
        res.x = element.offsetLeft; 
        res.y = element.offsetTop; 
        
        var offsetParent = element.offsetParent;
        var parentNode = element.parentNode;

        while (offsetParent !== null) {
            res.x += offsetParent.offsetLeft;
            res.y += offsetParent.offsetTop;

            if (offsetParent != document.body && offsetParent != document.documentElement) {
                res.x -= offsetParent.scrollLeft;
                res.y -= offsetParent.scrollTop;
            }
            //next lines are necessary to support FireFox problem with offsetParent
            if (isFireFox) {
                while (offsetParent != parentNode && parentNode !== null) {
                    res.x -= parentNode.scrollLeft;
                    res.y -= parentNode.scrollTop;
                    
                    parentNode = parentNode.parentNode;
                }    
            }
            parentNode = offsetParent.parentNode;
            offsetParent = offsetParent.offsetParent;
        }
    }
    return res;
}
// to collapse the blog pane
function collapseDiv(collapse, leftDiv, rightDiv, divCollapsed, divExpanded, leftWidth, rightWidth,color){
	if(collapse){
		document.getElementById(divExpanded).style.display = 'none';
		document.getElementById(divCollapsed).style.display = '';
		document.getElementById(leftDiv).width = leftWidth;
		document.getElementById(rightDiv).width = rightWidth;
		document.getElementById(divCollapsed).src = JSPRootURL+'/images/personal/dashboard_arrow-left.gif';
		if(color){
			document.getElementById(rightDiv).style.border = '1px solid '+color;
			document.getElementById(rightDiv).style.background = color;
		}
	} else {
		document.getElementById(divExpanded).style.display = '';
		document.getElementById(divCollapsed).style.display = 'none';
		document.getElementById(leftDiv).width = leftWidth;
		document.getElementById(rightDiv).width = rightWidth;
		document.getElementById(divExpanded).src = JSPRootURL+'/images/personal/dashboard_arrow-right.gif';
		if(color){
			document.getElementById(rightDiv).style.background = 'white';
		}
	}
}


// To show loading div and action div on top of the page
var loadingDiv = document.createElement('div');
loadingDiv.id = 'loadingDiv';

var actionDiv = document.createElement('div');
actionDiv.id = 'actionDiv';

// prepare loading div with given loading text and bgcolor
function showLoadingDivWithColor(loadingText, bgcolor) {
	loadingDiv.style.background = bgcolor;	
	showLoadingDiv(loadingText);
}

// prepare loading div with given loading text and yellowish background
function showLoadingDiv(loadingText) {
	showDivOfType('loading', loadingText);
}

// prepare action message div with given loading text and bgcolor
function showActionMessageDivWithColor(loadingText, bgcolor) {
	actionDiv.style.background = bgcolor;	
	showActionMessageDiv(loadingText);
}

// prepare action message div with given loading text and yellowinsh background
function showActionMessageDiv(loadingText) {
	showDivOfType('action', loadingText);
}

// append div to body of given type (action or loading) with specified text and background color
function showDivOfType(type, loadingText) {
	var loadingDivWidth = 60;
	if(typeof(loadingText) != 'undefined' && loadingText != '') {
		loadingDivWidth = (loadingText.length * 7) + 5;
	} else {
		loadingText = 'Loading';
	}
	if (type == 'action') {
		actionDiv.style.width = loadingDivWidth + 'px';
		actionDiv.innerHTML = loadingText;
		document.getElementsByTagName('body')[0].appendChild(actionDiv);
	} else {
		loadingDiv.style.width = loadingDivWidth + 'px';
		loadingDiv.innerHTML = loadingText;
		document.getElementsByTagName('body')[0].appendChild(loadingDiv);
		moveLoadingDiv(document.getElementById('loadingDiv'));
	}
}

// remove loading div on operation completion
function removeActionMessageDiv() {
	try {
		document.getElementsByTagName('body')[0].removeChild(actionDiv);
	} catch(err) {}
}

// remove loading div on operation completion
function removeLoadingDiv() {
	try {
		document.getElementsByTagName('body')[0].removeChild(loadingDiv);
	} catch(err) {}
}

// move a div object on page scroll 
// pass the div object to scroll, will work for IE7,FF3,chrome
function moveLoadingDiv(object) {
	var scrOfY = 0;
	try{
		if(typeof(window.pageYOffset) == 'number') {
			//Netscape compliant
			scrOfY = window.pageYOffset;
		} else if(document.body && document.body.scrollTop) {
			//DOM compliant
			scrOfY = document.body.scrollTop;
		} else if(document.documentElement && document.documentElement.scrollTop) {
			//IE6 standards compliant mode
			scrOfY = document.documentElement.scrollTop;
		}
		object.style.top = scrOfY + "px";
	}catch(err){}
}

// scroll the loading div (if loaded) on page scroll
window.onscroll = function() { if(document.getElementById('loadingDiv') != null) moveLoadingDiv(document.getElementById('loadingDiv'))};

// for loading external js / css file dynamically at runtime
function loadjscssfile(filename, filetype, functionCall){
	if (filetype=="js"){ //if filename is a external JavaScript file
		var fileref = document.createElement('script');
		fileref.setAttribute("type","text/javascript");
		fileref.setAttribute("src", filename);
		if(typeof functionCall != 'undefined') {
			// IE
			fileref.onreadystatechange = function() {
				if (fileref.readyState == 'complete' || fileref.readyState == 'loaded') {
					eval(functionCall);
				}
			};
			// FF
			fileref.onload = function() {
				eval(functionCall);
			};
		}
	} else if (filetype=="css"){ //if filename is an external CSS file
		var fileref = document.createElement("link");
		fileref.setAttribute("rel", "stylesheet");
		fileref.setAttribute("type", "text/css");
		fileref.setAttribute("href", filename);
	}
	
	if (typeof fileref != "undefined")
		document.getElementsByTagName("head")[0].appendChild(fileref);
}

//To get window width and height array
function getWindowsWidthHeight(){
	var size = new Array();
	if((navigator.userAgent.toLowerCase()).indexOf( "msie" ) != -1 ) {
		//IF IE
		if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
		//IE 6+ in 'standards compliant mode'
			size[0] = document.documentElement.clientWidth;
			size[1] = document.documentElement.clientHeight;
		} else {
		//IE 4 compatible
			size[0] = document.body.clientWidth;
			size[1] = document.body.clientHeight;
		}
	}else{
		//Non-IE
		size[0] = window.innerWidth;
		size[1] = window.innerHeight;
	}
	return  size;
}

//To get window height 
function getWindowHeight(){
	if((navigator.userAgent.toLowerCase()).indexOf( "msie" ) != -1 ) {
		//IF IE
		if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
		//IE 6+ in 'standards compliant mode'
			return document.documentElement.clientHeight;
		} else {
		//IE 4 compatible
			return document.body.clientHeight;
		}
	}else{
		//Non-IE
		return window.innerHeight;
	}
}

//To get window width 
function getWindowWidth(){
	if((navigator.userAgent.toLowerCase()).indexOf( "msie" ) != -1 ) {
		//IF IE
		if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
		//IE 6+ in 'standards compliant mode'
			return document.documentElement.clientWidth;
		} else {
		//IE 4 compatible
			return document.body.clientWidth;
		}
	}else{
		//Non-IE
		return window.innerWidth;
	}
}

// return the value in decimal format for calculation.
function defaultDecSeparator(checkValue, dec_separator) {
	if(dec_separator != null & typeof(dec_separator) != 'undefined' && typeof(checkValue) != 'undefined'){
		checkValue = (checkValue+'').replace('hrs','').replace('days','').trim();
		var isDecimalValue = checkValue.indexOf(dec_separator) >= 0;
		if(isDecimalValue && dec_separator != '.') {
			checkValue = checkValue.replace(dec_separator, '.');
		}
	}
	return checkValue;
}

// return the value as per locale's decimal separator
function changeDecSeparator(checkValue, dec_separator) {
	return (checkValue+'').replace('.', dec_separator);
}

// return the random number as string with txt as parameter
function getRandomNumber(txt){
	return txt + Math.floor(Math.random() * 99999);
}

/*
* function for selecting / deselecting the checkbox for the selected cell or text
* only need to call this method onclick event of the text
*/
function selectDeselectCheckbox(headerID) {
	if(document.getElementById(headerID)){
		document.getElementById(headerID).checked = !(document.getElementById(headerID).checked);
	}
}

/*
* function for checking whether the object has scroll or not
*/
function hasVerticalScrollbar(elem){
	return (elem.clientHeight < elem.scrollHeight)
}

// Manage table left scroll with header
function scrollHeader(divToScroll, sourceDiv){
	if(document.getElementById(divToScroll) != null && document.getElementById(sourceDiv) != null){
		document.getElementById(divToScroll).scrollLeft = document.getElementById(sourceDiv).scrollLeft;
	}
}
  
// Create message formatter 
if(typeof(msgFormat) == 'undefined'){
  	msgFormat=new Object();
  	msgFormat.format=function(s){
    var i=1;
    while(i<arguments.length) s=s.replace("{"+(i-1)+"}",arguments[i++]);
    return s;
  }
}

// Set flag false for not submitting form
function onSubmitFromForm(event){
    return false;
}

// Submit forms
function setFunctionForOnSubmitEventOfForm(){
	var formsOnPage = document.getElementsByTagName('form');
	for(var formsIndex = 0; formsIndex < formsOnPage.length; formsIndex++){
		formsOnPage[formsIndex].onsubmit = onSubmitFromForm;
	}	
}

// Function to validate numbers
function numberCheck(event, element) {
   var value = element.value;
   var temp = value.indexOf(dec_separator);
   var unicode= event.charCode ? event.charCode : event.keyCode;
   // Always allow back space
   if(unicode != 8){

      // Always allow Delete[46], Left arrow[37], Right arrow[39], Home[36], End[35], Tab[9] keys
      if(unicode== 46 || unicode== 37 || unicode== 39 || unicode== 36 || unicode== 35 || unicode== 9){
          return true;
      }

      // Allow all kind of decimal support for locale support 
      if(unicode == dec_separator.charCodeAt(0)){
      	return true;
      } else if(unicode< 48 || unicode > 57){
            return false;
      }
   }
}

// Function to stop event propagation for passed event in all browsers
// e event to propagate 
function stopEventPropagationFor(e){
	var event = e || window.event;
	if (event.stopPropagation) {
		event.stopPropagation();
	} else {
		event.cancelBubble = true;
	}
}

// To remove special character for specified str parameter 
function removeSpecialCharacter(str){
    while ( str.indexOf('\\') != -1) {
    	str = str.replace('\\',' ');
    }
    str = str.replace(new RegExp("\\\"", "g" ), '\\"');
    str = str.replace(new RegExp("\\n", "g" ), '\\n');
	return str;
}

// function to sort projects under my project channel on personal dashboard when column header is clicked
function doProjectSort(column, valueType, jspRootURL, viewID, oldSortColumn, oldSortOrder) {
	var suffix = '';
	var viewIDparam = 'PersonalSpace_projects_viewID=' + viewID;
	var url = '';
	if (viewIDparam.length != 0)
		viewIDparam += '&';
	if (valueType == "number")
		suffix = "&sorttype=number";
	
	if (oldSortColumn == column) {
		if (oldSortOrder == "asc") {
			url = "/personal/Main.jsp?" + viewIDparam + "sortcolumn=" + column + "&sortorder=dsc" + suffix;
		}
	} else {
		url = "/personal/Main.jsp?" + viewIDparam + "sortcolumn=" + column + "&sortorder=asc" + suffix;
	}
	self.document.location = jspRootURL + url;
}
