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

function cancel () {
   var newLocation = getVar("JSPRootURL") + "/document/CancelProcessing.jsp?module=" + theForm.module.value;
   self.document.location = newLocation;
}

function submit() {
    if (verifyForm()) {
         theAction("submit");
         theForm.submit();
    } 
}

function reset() {
	theForm.reset();
}

function verifyForm () {
	var documentType;

	if (isModifyPage == true)
	    documentType = theForm.documentType.value;
	else
	    documentType = getSelectedValue (theForm.documentType);

	if (trim(theForm.name.value).length == 0) {
		extAlert(errorTitle, nameRequiredErrMes , Ext.MessageBox.ERROR);
		theForm.name.focus()
		return false;
	}

	if (isModifyPage == false) {
	    if (verifyTypeSelection (theForm.documentType) == false) {
	   	 	extAlert(errorTitle, objectSelectionRequiredErrMes, Ext.MessageBox.ERROR);
		    return false;
	    }

	    if ((theForm.file.value == null || theForm.file.value == "" || trim(theForm.file.value).length==0) && documentType == "document") {
		    extAlert(errorTitle, fileSelectionRequiredErrMes, Ext.MessageBox.ERROR);
		    theForm.file.focus();
		    return false;
	    }

    }
    
    if ( documentType == "bookmark" && (theForm.url.value == null || theForm.url.value == "" || trim(theForm.url.value).length==0) ) {
        extAlert(errorTitle, urlSelectionRequiredErrMes, Ext.MessageBox.ERROR);
        theForm.url.focus();
        return false;
    }
	    	
	if (theForm.notes.value.length > 500) {
		extAlert(errorTitle, commentErrMes , Ext.MessageBox.ERROR);
	    theForm.notes.focus();
		return false;
	}
	
	if (theForm.description.value.length > 500) {
		extAlert(errorTitle, descriptionErrMes , Ext.MessageBox.ERROR);
	    theForm.description.focus();
		return false;
	}

	return true;
}


function verifyTypeSelection (theField, type) {
   var retval = false;
   var myCheckBox = false;
   var numElements = 0;
   var isMulti = false;

   if (theField == null)
      return false;

   myCheckBox = theField.checked;   

   if (myCheckBox)
      retval = true

   else {
   	for (var i = 0; i < theField.length; i++) 

         if (theField[i].checked == true) {

	     numElements++;
         }
   }

      if (numElements < 1)
	  retval = false;
      else
         retval = true;

   return retval;
}
