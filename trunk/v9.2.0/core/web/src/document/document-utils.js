var record;

// Create document
function create(){
	record = documentGrid.getSelectionModel().getSelected();
	if(typeof(record) != 'undefined'){
		if(record.get("objectType") == "doc_container"){
			self.location = JSPRootURL+"/servlet/DocumentActionBroker?theAction=create&containerID="+objectId+"&module="+ moduleId;
		} else {
			extAlert(errorTitle,"Selected record is not a container",Ext.MessageBox.INFO);
		}
	} else {
		extAlert(errorTitle, 'Please select a container before importing a document.' , Ext.MessageBox.ERROR);
	}
}

// Edit document
function modify(){
	record = documentGrid.getSelectionModel().getSelected();
	if(typeof(record) != 'undefined'){
		if(typeof(record) != 'undefied'){	
			self.location = JSPRootURL+"/servlet/DocumentActionBroker?theAction=modify&containerID=" + objectId + "&module="+ moduleId;
		} else {
			extAlert(errorTitle,"Please select an item from the list",Ext.MessageBox.INFO);
		}
	} else {
		extAlert(errorTitle, 'Please select a document before modify it.' , Ext.MessageBox.ERROR);
	}
}

// Remove document
function remove() {
	if (documentName != 'Top Folder')
        cannotMoveRootErrMes = "You cannot remove the top folder.";
	
    if(typeof(record) != 'undefied'){
    	Ext.MessageBox.confirm("Confirm","Are you sure you want to delete this object?",
		          function(btn){
                    if(btn == "yes") {
                   		//self.location = JSPRootURL+"/servlet/DocumentActionBroker?theAction=remove_Deleted&containerID=" + objectId + "&module="+ moduleId;
                   		removeDocuments();
                   		self.document.location = self.document.location;
                    } 
          });
     } else {
    	extAlert(errorTitle,"Please select an document before remove it.",Ext.MessageBox.INFO);
     }	
}

// View Properties
function properties(){
	if(typeof(record) != 'undefined'){
		record = documentGrid.getSelectionModel().getSelected();
		if(typeof(record) != 'undefied'){
			self.location = JSPRootURL+"/servlet/DocumentActionBroker?theAction=properties&containerID="+ objectId +"&module="+ moduleId;
		} else {
			extAlert(errorTitle,"Selected record is not a container",Ext.MessageBox.INFO);
		}
	} else {
		extAlert(errorTitle,"Please select an document.",Ext.MessageBox.INFO);
	}
}

function check_out() {
	record = documentGrid.getSelectionModel().getSelected();
	if(typeof(record) != 'undefined'){
		if(record.get("is_checked_out") != true){
	        if ( objectType != 'document' )
	           extAlert("Error", "Currently you can not check out the selected document", Ext.MessageBox.ERROR);
	        else {
	           var doc_win = openwin_small("doc_win");
	           if (doc_win) {
	              doc_win.location.href = JSPRootURL+"/servlet/DocumentActionBroker?theAction=check_out&containerID="+ objectId +"&module="+ moduleId;
	              doc_win.focus();
	           }
	        }
	      } else {
			extAlert(errorTitle, 'You may not check out a document which is already checked out.' , Ext.MessageBox.ERROR);
	  	  }
	    } else {
	    	extAlert(errorTitle, 'Please select a document before check out.' , Ext.MessageBox.ERROR);
	    }
}

function check_in() {
	record = documentGrid.getSelectionModel().getSelected();
	if(typeof(record) != 'undefined'){
		if(record.get("is_checked_out") != false){
			if ( objectType != 'document')
	        	extAlert("Error", "Currently you can not check in selected document", Ext.MessageBox.ERROR);
	        else {
	 	        var doc_win = openwin_small("doc_win");
	    	    if (doc_win) {
	          	  doc_win.location.href = JSPRootURL+"/servlet/DocumentActionBroker?theAction=check_in&containerID="+ objectId +"&module="+ moduleId;
	              doc_win.focus();
	          	}
			}
		} else {
			extAlert(errorTitle, "You may not check in a document which has not been checked out.", Ext.MessageBox.ERROR);
		}
	} else {
		extAlert(errorTitle, 'Please select a document before check in.' , Ext.MessageBox.ERROR);
	}
}

function undo_check_out() {
	record = documentGrid.getSelectionModel().getSelected();
	if(typeof(record) != 'undefined'){
	    if ( objectType != 'document' )
	       extAlert("Error", "Currently you can not undo check out.", Ext.MessageBox.ERROR);
	    else {
	       var undo_cko = openwin_small("undo_cko");  
	       if (undo_cko) {
		      undo_cko.location.href = JSPRootURL+"/servlet/DocumentActionBroker?theAction=undo_check_out&containerID="+ objectId +"&module="+ moduleId;
	          undo_cko.focus();
	       }
	    }
	} else {
		extAlert(errorTitle, 'Please select a document before undo check out.' , Ext.MessageBox.ERROR);
	}
}

function new_folder() {
	record = documentGrid.getSelectionModel().getSelected();
	if(typeof(record) != 'undefined'){
		if(record.get("objectType") == "doc_container"){
		   	var new_folder = openwin_dialog_resizable("new_folder","","","","",0);  
			if (new_folder) {
				new_folder.location.href = JSPRootURL+"/servlet/DocumentActionBroker?theAction=create_folder&containerID="+ objectId +"&module="+ moduleId;
				new_folder.focus();
			}
		} else {
			extAlert(errorTitle,"Selected record is not a container",Ext.MessageBox.INFO);
		}
	} else {
		extAlert(errorTitle, 'Please select a container before creating a new folder.' , Ext.MessageBox.ERROR);
	}
}

function link() {
	if(objectId != null && typeof(objectId) != 'undefined') {
	   	if ( objectType != 'document') {
	   		extAlert("Information", "You can only link to documents at this time" , Ext.MessageBox.ERROR);
	   	} else {
			if (objectId != null && typeof(objectId) != 'undefined'){
				self.document.location = JSPRootURL + "/link/LinkManager.jsp?context="+ contextId +"&view="+ viewId +"&id="+ objectId +"&action="+ linkActionId +"&module="+ linkModule;
			} else {
				extAlert(errorTitle, "You have to select an object to link" , Ext.MessageBox.ERROR);
			}
	   	}
	}
}

function move() {
	record = documentGrid.getSelectionModel().getSelected();
	if(typeof(record) != 'undefined'){
	    if (typeof(objectId) != 'undefined')
	        var doc_win = openwin_small("doc_win");
	      	if (doc_win) {
	      		doc_win.location.href = JSPRootURL+"/servlet/DocumentActionBroker?theAction=move&containerID="+ objectId +"&module="+ moduleId;
	            doc_win.focus();
	    }
    } else {
		extAlert(errorTitle, 'Please select an item from a list.' , Ext.MessageBox.ERROR);
	}
}

function notify() {
	record = documentGrid.getSelectionModel().getSelected();
    if ( objectId != null && typeof(objectId) != 'undefined' ){
       var notifyWin = openwin_wizard("notifyWin");
       if (notifyWin) {
	       notifyWin.location.href = JSPRootURL+"/servlet/DocumentActionBroker?theAction=notify&containerID="+ containerId +"&module="+ moduleId;
           notifyWin.focus();
       }
	}
}