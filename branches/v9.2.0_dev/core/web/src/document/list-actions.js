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
	theForm.target = "_self";
	theForm.submit();
}

function listdeleted() {

	resetFocus();

	theAction("list_Deleted");
	theForm.target = "_self";
	theForm.submit();
}

function undodelete() {
	    
    var cannotUndoRootErrMes = "You cannot un-delete the top folder.";
    if (theForm.isRoot.value == "true" && getSelection(theForm) == theForm.containerID.value) {
       	extAlert(errorTitle, cannotUndoRootErrMes , Ext.MessageBox.ERROR);
        return;
    }

    if (!noSelectionErrMes)
        noSelectionErrMes = "Please select an item from the list";

    var  confirmUndoDeletionMes = "Are you sure you want to un-delete this object?";

	resetFocus();
	if (verifySelection(theForm, "multiple", noSelectionErrMes)){
		Ext.MessageBox.confirm("Confim", confirmUndoDeletionMes, function(btn) { 
				if(btn == 'yes'){ 
					theAction("undo_delete");
					theForm.target = "_self";
					theForm.submit();
				}else{
				 	return false;
				}
			});
	}	
}

function remove() {
    if (!cannotMoveRootErrMes)
        cannotMoveRootErrMes = "You cannot remove the top folder.";
	//bfd 3266: Pop Message is wrong while deleting the Current Folder
    if (getSelection(theForm) == theForm.containerID.value) {
	    extAlert(errorTitle, cannotRemoveRootErrMes , Ext.MessageBox.ERROR);
        return;
    }

    if (!noSelectionErrMes)
        noSelectionErrMes = "Please select an item from the list";
	if (!confirmDeletionMes)
        confirmDeletionMes = "Are you sure you want to delete this object?";

	resetFocus();
	if (verifySelection(theForm, "multiple", noSelectionErrMes))
        Ext.MessageBox.confirm("Confirm", confirmDeletionMes,
                function(btn){
                    if(btn == "yes") {
                    	if(theForm.checkedAction.value == "256") {
							theAction("remove_Deleted");
							theForm.target = "_self";
							theForm.submit();
						}else {
							theAction("remove");
							theForm.target = "_self";
							theForm.submit();
						}
                    } 
          });
}

function search() { 
    self.document.location = JSPRootURL + "/project/SearchProject.jsp?otype=<%=net.project.base.ObjectType.DOCUMENT%>&module=<%=net.project.base.Module.PROJECT_SPACE%>&action=<%=net.project.security.Action.VIEW%>"; 
}



function modify() {

    if (!noSelectionErrMes)
        noSelectionErrMes = "Please select an item from the list";
        
	resetFocus();
	
	if (theForm.isRoot.value == "true" && getSelection(theForm) == theForm.containerID.value) {
   		extAlert(errorTitle, 'You cannot modify the top folder' , Ext.MessageBox.ERROR);
        return;
    }

   if (verifySelection(theForm, 'multiple', noSelectionErrMes)) {
         theAction("modify");
		 theForm.target = "_self";
         theForm.submit();
   } // if verifyselection

}

function isObjectTypeOf (name, type) {

   var retval = false;
   var strExecute;
   if(typeof(name) != 'undefined'){
	   strExecute = "if (theForm.item" + name + ".value == '" + type + "') \nretval = true";
	   eval (strExecute);
   }
   return retval;

} // isTypeOf



function reset() {

	resetFocus();

	theAction("reset");
	theForm.target = "_self";
	theForm.submit();
}

function cancel() {

	resetFocus();

	theAction("cancel");
	theForm.target = "_self";
	theForm.submit();
}


function move() {
    if (!cannotMoveRootErrMes)
        cannotMoveRootErrMes = "You cannot move the top folder.";
    if (theForm.isRoot.value == "true" && getSelection(theForm) == theForm.containerID.value) {
		extAlert(errorTitle, cannotMoveRootErrMes , Ext.MessageBox.ERROR);
        return;
    }

    if (!noSelectionErrMes)
        noSelectionErrMes = "Please select an item from the list";
         
    if (verifySelection(theForm, 'multiple', noSelectionErrMes)) {
        var doc_win = openwin_small("doc_win");

      	if (doc_win) {
            theAction("move");
            theForm.target = "doc_win";
            theForm.submit();
            doc_win.focus();
        }
    }
}


function launchApplet() {

   var applet_win = openwin_applet ("LaunchDocumentAppletGetCookies.jsp");
   applet_win.focus();

}


function workflow() {

   if (!noSelectionErrMes)
        noSelectionErrMes = "Please select an item from the list";
   if (!addDocumentToWorkflowErrMes)
        addDocumentToWorkflowErrMes = "You can only route documents in a workflow at this time.";   
         
   if (verifySelection(theForm, 'multiple', noSelectionErrMes)) {	

       if ( !isObjectTypeOf (getSelection(theForm, 'multiple', noSelectionErrMes), 'document') )
	   	extAlert(errorTitle, addDocumentToWorkflowErrMes, Ext.MessageBox.ERROR);

       else {

	   if (!workflow) {
	       var workflow = openwin_wizard("workflow");

	       if (workflow) {
		   theAction("workflow");
		   theForm.target = "workflow";
		   theForm.submit();
		   workflow.focus();
	       }
	   }

       } // end else

   } // end verify
}


function check_in() {
	if (!isObjectTypeOf (getSelection(theForm), 'document')) {
		document.getElementById("errorLocationID").innerHTML = ("You can only check in documents" + "<br/>");
	}
	if(typeof documentNotCheckedOut == 'undefined'){
		documentNotCheckedOut = "You may not check in a document which has not been checked out."; 
	}
     if (!noSelectionErrMes)
        noSelectionErrMes = "Please select an item from the list";
     if (!documentCheckInErrMes)
        documentCheckInErrMes = "You can only check in documents at this time.";
           
     if (verifySelection(theForm, 'multiple', noSelectionErrMes)) {

		if(document.getElementById("documentCheckIn_"+getSelection(theForm)) != null){ // Condition for checking if the file is checked out or not.
	        if ( !isObjectTypeOf (getSelection(theForm), 'document') )
	           extAlert(errorTitle, documentCheckInErrMes, Ext.MessageBox.ERROR);
	        else {
	           var doc_win = openwin_small("doc_win");
	  			
	           if (doc_win) {
	           	  if(document.getElementById("errorLocationID") != null){
	           	  	document.getElementById("errorLocationID").innerHTML = "";
	           	  }
	              theAction("check_in");
	              theForm.target = "doc_win";
	              theForm.submit();
	              doc_win.focus();
	           } // if(doc_win)
	        } // else
	      } else {
	      	//Display the error message if the document is not checked out but the user tries to check in.
	     	 document.getElementById("errorLocationID").innerHTML = (documentNotCheckedOut+"<br/>");
	      }
     } // if verify
}

function check_out() {

     if (!noSelectionErrMes)
        noSelectionErrMes = "Please select an item from the list";
     if (!documentCheckOutErrMes)
        documentCheckOutErrMes = "You can only check out documents at this time.";
        
     if (verifySelection(theForm, 'multiple', noSelectionErrMes)) {	
        if ( !isObjectTypeOf (getSelection(theForm), 'document') )
           extAlert(errorTitle, documentCheckOutErrMes, Ext.MessageBox.ERROR);
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
	
    if (!noSelectionErrMes)
        noSelectionErrMes = "Please select an item from the list";
	if (!documentUndoCheckOutErrMes)
        documentUndoCheckOutErrMes = "You can only undo check out documents at this time.";
        
    resetFocus();

	if (verifySelection(theForm, 'multiple', noSelectionErrMes)) {
        if ( !isObjectTypeOf (getSelection(theForm), 'document') )
           extAlert(errorTitle, documentUndoCheckOutErrMes, Ext.MessageBox.ERROR);
        else {
           var undo_cko = openwin_small("undo_cko");  

           if (undo_cko) {
              theAction("undo_check_out");
              theForm.target = "undo_cko";
              theForm.submit();
              undo_cko.focus();
           }
        }
   }  // verifySelection()

} // undo_check_out


function notify() {

	 var targetObjectID;	
	 var m_url;
	 if (!noSelectionErrMes)
        noSelectionErrMes = "Please select an item from the list";
     if (!notificationErrMes)
        notificationErrMes = "This object type does not currently support notification";   
        
     if (verifySelection(theForm, 'multiple', noSelectionErrMes)) {	
    
        if ( !(isObjectTypeOf (getSelection(theForm), 'document') || isObjectTypeOf (getSelection(theForm), 'doc_container')) )
			extAlert(errorTitle, notificationErrMes, Ext.MessageBox.ERROR);           
        else {	
        	var selectedItem = theForm.elements["selected"];	
        
        	for(var index = 0; index<selectedItem.length; index++){
        		if(selectedItem[index].checked){ 
        			targetObjectID = selectedItem[index].value;
        			break;
        		}
        	}	
        	
        	openNotifyPopup(getSelection(theForm),m_url);
        }

     }
}




function new_folder() {

   var new_folder = openwin_dialog_resizable("new_folder","","","","",0);  

	if (new_folder) {
		theAction("create_folder");
		theForm.target = "new_folder";
		theForm.submit();
		new_folder.focus();
	}
}

function properties () {
    
    if (!noSelectionErrMes)
        noSelectionErrMes = "Please select an item from the list";

    if (theForm.isRoot.value == "true" && getSelection(theForm) == theForm.containerID.value) {
   		extAlert(errorTitle, cannotViewPropertiesRootErrMes , Ext.MessageBox.ERROR);
        return;
    }

	resetFocus();	

	if (verifySelection(theForm, 'multiple', noSelectionErrMes)) {
		theAction("properties");
		theForm.target = "_self";
		theForm.submit();
	}
}

function discuss () {
    resetFocus();

    if (verifySelection(theForm)) {
		if ( !isObjectTypeOf (getSelection(theForm), 'document') ) {
			extAlert(errorTitle, 'The top folder has no discussions.', Ext.MessageBox.ERROR);
	    } else {
	        theAction("discussWithHistory2");
			theForm.target = "_self";
	        theForm.submit();
        }
    }
}


function cko_view () {
		setVar("isCheckOut", "false");
		theAction("cko_view");
		theForm.target = "_self";
		theForm.submit();
}

function view(doc_id) {
   
   if (!noSelectionErrMes)
        noSelectionErrMes = "Please select an item from the list";
   if (!viewObjectErrMes)
        viewObjectErrMes = "You can not view this type of object."

   if (doc_id) {
      select_radio (theForm.selected, doc_id);
   }

   if (verifySelection(theForm, 'multiple', noSelectionErrMes)) {
       if ( !isObjectTypeOf (getSelection(theForm), 'document'))
			extAlert(errorTitle, viewObjectErrMes, Ext.MessageBox.ERROR); 
       else {

		theAction("view");
         setSecurityAction("view");

		var theURL = getVar("JSPRootURL") + "/servlet/DownloadDocument?id=" + getSelection(theForm);
		var view_window = window.open(theURL, "view_window", "directory=0,resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=1,status=0,toolbar=0");
      }
   }
}

function version () {

	resetFocus();

	if (verifySelection(theForm)) {
		theAction("version");
		theForm.target = "_self";
		theForm.submit();
	}
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


