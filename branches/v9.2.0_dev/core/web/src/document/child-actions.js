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
// THIS IS TO BE INCLUDED ON ALL PAGES IN THE DOC MODULE *EXCEPT* THE
// TOP LEVEL LIST PAGE (main.jsp)

function create() {

	resetFocus();

	theAction("create");
	theForm.target = "_self";
	theForm.submit();
}

function undodelete() {
    if (!confirmUndoDeletionMes)
        confirmUndoDeletionMes = "Are you sure you want to un-delete this object?";
	resetFocus();
		Ext.MessageBox.confirm("Confirm", confirmUndoDeletionMes, function(btn) { 
				if(btn == 'yes'){ 
					theAction("undo_delete");
					theForm.target = "_self";
					theForm.submit();
				}else{
				 	return false;
				}
		});	
}

function remove() {
    if (!confirmDocumentDeletionMes)
        confirmDocumentDeletionMes = "Are you sure you want to delete this document?";
	resetFocus();
		Ext.MessageBox.confirm("Confirm", confirmDocumentDeletionMes , function(btn) { 
				if(btn == 'yes'){ 
					if(theForm.module.value == "330") {
						theAction("remove_Deleted");
						theForm.target = "_self";
						theForm.submit();
					} else {
						theAction("remove");
						theForm.target = "_self";
						theForm.submit();
					}
				}else{
				 	return false;
				}
			 });
}

function modify() {

	resetFocus();

	theAction("modify");
	theForm.target = "_self";
	theForm.submit();

}

function move() {

         var doc_win = openwin_small("doc_win");

      	if (doc_win) {
            theAction("move");
            theForm.target = "doc_win";
            theForm.submit();
            doc_win.focus();
         }
}

function reset() {

	resetFocus();

	theAction("properties");
	theForm.target = "_self";
	theForm.submit();
}

function cancel() {

	resetFocus();

	theAction("cancel");
	theForm.target = "_self";
	theForm.submit();
}


function check_in() {

	if (!doc_win)
		var doc_win = openwin_small("doc_win");
  	
	if (doc_win) {
		theAction("check_in");
		theForm.target = "doc_win";
	  	theForm.submit();
		doc_win.focus();
  	}
}

function check_out() {

	if (!doc_win)
		var doc_win = openwin_small("doc_win");

      	if (doc_win) {
		theAction("check_out");
		theForm.target = "doc_win";
		theForm.submit();
		doc_win.focus();
	}
}

function undo_check_out() {
	
	resetFocus();

   var undo_cko = openwin_dialog("undo_cko");  

      if (undo_cko) {
         theAction("undo_check_out");
         theForm.target = "undo_cko";
         theForm.submit();
         undo_cko.focus();
      }

} // undo_check_out


function cko_view () {
		setVar("isCheckOut", "false");


		theAction("cko_view");
		theForm.target = "_self";
		theForm.submit();
}

function view() {

      //theAction("view");
//      setSecurityAction("view");

     var objectID = theForm.objectID.value;

      var theURL = getVar("JSPRootURL") + "/servlet/DownloadDocument?id=" + objectID;
      window.open(theURL, "view_window", "directory=0,resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=1,status=0,toolbar=0");

}

function search() { 
    self.document.location = getVar("JSPRootURL") + "/project/SearchProject.jsp?otype=<%=net.project.base.ObjectType.DOCUMENT%>&module=<%=net.project.base.Module.PROJECT_SPACE%>&action=<%=net.project.security.Action.VIEW%>"; 
}

function workflow() {

	   if (!workflow) {
	       var workflow = openwin_wizard("workflow");

	       if (workflow) {
		   theAction("workflow");
		   theForm.target = "workflow";
		   theForm.submit();
		   workflow.focus();
	       }
	   }
}


function notify() {

           var notifyWin = openwin_wizard("notifyWin");   // takes the name of a window and retruns a window which we assign to a variable of that name.
           if (notifyWin) {        
              theAction("notify"); // this is passed by the form to the subsequent page
              theForm.target = "notifyWin";  //  a form has a target to which the results of the server activity will be posted.
              theForm.submit();     // the form is submitted 
              notifyWin.focus();   // and  focus is passed to the window we created
           }
}




function security() 
{
    if (!security)
	var security = openwin_security("security");

    if (security) {
	theAction("security");
	theForm.target = "security";
	theForm.submit();
	security.focus();
    }
}

function view_version (version_id, object_id) {


      var theURL = getVar("JSPRootURL") + "/servlet/DownloadVersion?id=" + object_id + "&versionid=" + version_id;

      window.open(theURL, "view_window", "directory=0,resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=1,status=0,toolbar=0");


//      var view_window = window.open("/blank.html", "view_window", "directory=0,resizable=1, statusbar=0, hotkeys=0,menubar=0,scrollbars=1,status=0,toolbar=0");

//         theAction("view_version");
         //setSecurityAction("view");

//         theForm.versionID.value = version_id;
//         theForm.target = "view_window";
//         theForm.submit();

}

function properties () {

   resetFocus();
      theAction("properties");
	  theForm.target = "_self";
		theForm.submit();
}

function propertySheet () {

      theForm.target = "property_main";
      theAction("property_sheet");
		theForm.submit();
}

function version () {

      theForm.target = "property_main";
		theAction("version");
		theForm.submit();
}

function history () {

      theForm.target = "property_main";
      theAction("history");
		theForm.submit();
}

function theAction (myAction) {
	theForm.theAction.value = myAction;
}

function discuss () {
      theForm.target = "property_main";
      theAction("discuss");
      theForm.submit();
}
