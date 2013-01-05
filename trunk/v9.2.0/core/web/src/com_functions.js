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
//common_functions.js

var endl = "\n";

function ErrorObj (isOk) {

	if (isOk == true) 
	   this.isOk = true;
	else
	   this.isOk = false;

	this.msg = "";
}

function verifyURL (myURL) {

	var re = new RegExp ("^http://.*");
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



function verifyDate (field) {

	var date = field.value;
	var dateArray = date.split("/");
	var isOk = true;
	var theDate = new Date(date);

	var d1 = new String(theDate.getDate());
	var zeros = new RegExp("0");

	var tmp = new String(dateArray[1])
	tmp = tmp.replace(zeros, "");

	var d2 = new RegExp(tmp);

	if (date != null && date != "") { 

	d1 = d1.replace(zeros, "");

	if (d1.search(d2) == -1) {
		extAlert(errorTitle, date + " is not a valid date", Ext.MessageBox.ERROR); 
		isOk = false;
	}	

	    else if (dateArray.length != 3) {
	    	extAlert(errorTitle, "Date format incorrect.\nPlease re-enter your date as MM/DD/YYYY.", Ext.MessageBox.ERROR); 
		   isOk = false;
	   }
	   else if ((dateArray[0] < 1) || (dateArray[0] > 12)) {
		   extAlert(errorTitle, "You entered a date that does not exist.\nPlease enter a valid date \(MM/DD/YYYY\).", Ext.MessageBox.ERROR); 
		 isOk = false;
	    }
	   else if ((dateArray[1] < 1) || (dateArray[1] > 31)) {
		   extAlert(errorTitle, "You entered a date that does not exist.\nPlease enter a valid date \(MM/DD/YYYY\).", Ext.MessageBox.ERROR); 
	 	   isOk = false;
	   }
	   else if (dateArray[2].length != 4)  {
	   		extAlert(errorTitle, "You entered a year that is not Y2K compliant.\nPlease enter a 4-digit year \(MM/DD/YYYY\).", Ext.MessageBox.ERROR); 
		   isOk = false;
	   }
	}
	
	if (!isOk) {
		//eval ("document.forms[0]." + field.name + ".value = \"\";");
      eval ("self.document.forms[0]." + field.name + ".focus();");
      eval ("self.document.forms[0]." + field.name + ".select();");
	}
   
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


function getSortDirection(sortBy) {

   // Usage:  var direction = getSortDirection(sortBy);
   // Toggles sort "direction" from asc to desc

	var current;

	if (getVar("lastSort") == sortBy) {

	   current = getVar("sortDir");

           if (!current)
	      setVar("sortDir", "asc");
           else if (current == "asc") 
	      setVar("sortDir", "desc");
           else if (current == "desc")
	      setVar("sortDir", "asc");
	   else 
	      setVar("sortDir", "asc");

	} else 
          setVar("sortDir", "asc");

	setVar("lastSort", sortBy);
	return getVar("sortDir");
}

function sortList(sortBy) {

   // Usage:  sortList ("schema.table.column_name")
   // Example:  sortList ("document_name");

   // this function compiles a string in a hidden field
   // called sort.  You must have this hidden field
   // for this to work!

	var sortString;
	var sortDirection;

	sortDirection = getSortDirection(sortBy);	

	sortString = "order by " + sortBy + " " + sortDirection;

	if (page) {

		theForm.sort.value = sortString;
		theAction("sort");
		theForm.submit();
	}
}

function isMenuSelected (field) {
   
   // Usage: isSelected(field_object)
   // Example:  if (isSelected(document.forms[0].selectField)) . . .

  if (field.selectedIndex == -1)
     return false;   
  else
     return true;
}

function verifySelection(errorMsg) {
   var retval = false;
   var myRadio = false;
   // Usage: if (verifySelected(msg)) . . .
   // Returns boolean

   if (!errorMsg)
      errorMsg = "Please select an item from the list";

   myRadio = document.forms[0].elements["selected"].checked;   

   if (myRadio)
      retval = true
   else {
   	for (var i = 0; i < document.forms[0].selected.length; i++) 
	      if (document.forms[0].selected[i].checked == true) 
        	 retval = true;
   }	
	if (!retval)
		extAlert(errorTitle, errorMsg, Ext.MessageBox.ERROR); 

   return retval;
}

function clearSelect(select) {

	var mySelect;
	eval ("mySelect = theForm." + select);

	for (var i=0; i < mySelect.length; i++)
		mySelect.options[i].selected = false;
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
	//alert ("isNumber: " + isOk);
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
