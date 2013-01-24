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
// THIS IS TO BE INCLUDED ONLY ON THE DOC LIST PAGE
// TOP LEVEL LIST PAGE (main.jsp)

function create() {

	resetFocus();

	theAction("create");
	theForm.submit();
}

function remove() {
	
	resetFocus();

	if (verifySelection(theForm, "multiple")){
		Ext.MessageBox.confirm("Confirm", "Are you sure you want to delete this object?", function(btn) { 
			if(btn == 'yes'){ 
				theAction("remove");
				theForm.submit();
			}else{
			 	return false;
			}
		});
	}	
}


function modify() {

	resetFocus();

   if (verifySelection(theForm)) {

      if ( !isObjectTypeOf (getSelection(theForm), 'document') )
     	 extAlert(errorTitle, "You can only modify documents at this time.", Ext.MessageBox.ERROR);
      else {
         theAction("modify");
         theForm.submit();
      }
   } // if verifyselection

}

function isObjectTypeOf (name, type) {

   var retval = false;

   var strExecute = "if (theForm.item" + name + ".value == '" + type + "') \nretval = true";
   eval (strExecute);

   return retval;

} // isTypeOf



function reset() {

	resetFocus();

	theAction("reset");
	theForm.submit();
}

function cancel() {

	resetFocus();

	theAction("cancel");
	theForm.submit();
}


function launchApplet() {

   var applet_win = openwin_applet ("LaunchDocumentAppletGetCookies.jsp");
   applet_win.focus();

}

function check_in() {

     if (verifySelection(theForm)) {

        if ( !isObjectTypeOf (getSelection(theForm), 'document') )
        	extAlert(errorTitle, "You can only check in documents at this time.", Ext.MessageBox.ERROR);
        else {
           var doc_win = openwin_small("doc_win");
  	
           if (doc_win) {
              theAction("check_in");
              theForm.target = "doc_win";
              theForm.submit();
              doc_win.focus();
           } // if(doc_win)
        } // else
     } // if verify
}

function check_out() {

     if (verifySelection(theForm)) {	
        if ( !isObjectTypeOf (getSelection(theForm), 'document') )
         	extAlert(errorTitle, "You can only check out documents at this time.", Ext.MessageBox.ERROR);
        else {

           var doc_win = openwin_small("doc_win");

           if (doc_win) {
              theAction("check_out");
              theForm.target = "doc_win";
              theForm.submit();
              doc_win.focus();
           }
        }
     }
}

function undo_check_out() {
	
	resetFocus();

	if (verifySelection(theForm)) {
        if ( !isObjectTypeOf (getSelection(theForm), 'document') )
           extAlert(errorTitle, "You can only undo check out documents at this time.", Ext.MessageBox.ERROR);  
        else {
           var undo_cko = openwin_dialog("undo_cko");  

           if (undo_cko) {
              theAction("undo_check_out");
              theForm.target = "undo_cko";
              theForm.submit();
              undo_cko.focus();
           }
        }
   }  // verifySelection()

} // undo_check_out


function properties () {

	resetFocus();	

	if (verifySelection(theForm)) {
		theAction("properties");
		theForm.submit();
	}
}


function cko_view () {
		setVar("isCheckOut", "false");
		theAction("cko_view");
		theForm.submit();
}

function view(doc_id) {

   if (doc_id) {
      select_radio(theForm.selected, doc_id);
   }

   if ( !isObjectTypeOf (getSelection(theForm), 'document') )
   		extAlert(errorTitle, "You can only view documents at this time.", Ext.MessageBox.ERROR);
   else {

      var view_window = window.open("blank.html", "view_window", "directory=0,resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=0,status=0,toolbar=0");

      if (verifySelection(theForm)) {

         theAction("view");
         setSecurityAction("view");
         theForm.target = "view_window";
         theForm.submit();
      }
   } // else 
}

function docSort(column) {

	resetFocus();
	sortList(column);
}

function theAction (myAction) {
	theForm.theAction.value = myAction;
}

function setSecurityAction (securityAction) {
   theForm.action.value= securityAction;
}

1;