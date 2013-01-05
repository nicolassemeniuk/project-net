var uploadScreen = document.createElement('div');
uploadScreen.id = 'uploadScreen';
uploadScreen.style.display = 'none';
uploadScreen.style.height = (getWindowWidth())+ 'px';
var uploadDialogue = document.createElement('div');
uploadDialogue.id = 'uploadDialogue';
uploadDialogue.style.border = 'none';
uploadDialogue.style.display = 'none';
var uploadBody = document.createElement('div');
uploadBody.id = 'uploadBody';
var validImage, errorAlertTitle;
var scriptTag, scriptTags, responseText, javaScriptCode;
var applySort = false;


function extAlert(title, msg, icon){
	Ext.MessageBox.show({
		title: title,
		msg: msg,
		buttons: Ext.MessageBox.OK,
		icon: icon
	});
}  

/* open customization popup view to changes the status of columns */
function openCustomizeColumnPopup() {
	window.document.body.style.overflow = 'hidden';	
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		document.getElementsByTagName('html')[0].style.overflowY = 'hidden';
		hideHtmlSelectTags();
	}
	var windowScrollbarY = typeof window.pageYOffset != 'undefined' ? window.pageYOffset : document.documentElement.scrollTop;

	url = JSPRootURL + '/portfolio/customize';
	width = 585;
	uploadScreen.style.height = window.screen.height+'px';
	uploadScreen.style.display = 'block';
	uploadDialogue.style.display = 'block';
	uploadDialogue.style.width = width+'px';
	uploadDialogue.style.top = (getWindowHeight()/10)+ 'px';
	uploadDialogue.style.left = (getWindowWidth()/2 - width/2)+ 'px';
	window.document.body.style.overflowY = 'scroll';
	
	document.getElementsByTagName('body')[0].appendChild(uploadScreen);		
	document.getElementsByTagName('body')[0].appendChild(uploadDialogue);		
	document.getElementById('uploadDialogue').appendChild(uploadBody);	
	
	var docTypeTag = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">';
	document.getElementById('uploadBody').innerHTML = loadingMessageColSetting +'<img src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	
	Ext.Ajax.request({
	   url: url,
	   params: {module:moduleId},
	   method: 'POST',
	   success: function(result, request){
		    document.getElementById('uploadBody').innerHTML = result.responseText;
	   },
	   failure: function(result, response){
		   extAlert(errorAlertTitle, errorColumnSettingPopUp, Ext.MessageBox.ERROR);
	   }
	});
	setNow = true;
}

/* open popup to save current two pane view settings as a portfolio view*/
function saveCurrentSettings() {
	window.document.body.style.overflow = 'hidden';	
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		document.getElementsByTagName('html')[0].style.overflowY = 'hidden';
		hideHtmlSelectTags();
	}
	var windowScrollbarY = typeof window.pageYOffset != 'undefined' ? window.pageYOffset : document.documentElement.scrollTop;

	url = JSPRootURL + '/portfolio/saveportfolioview';
	width = 500;
	uploadScreen.style.height = window.screen.height+'px';
	uploadScreen.style.display = 'block';
	uploadDialogue.style.display = 'block';
	uploadDialogue.style.width = width+'px';
	uploadDialogue.style.top = (getWindowHeight()/3)+ 'px';
	uploadDialogue.style.left = (getWindowWidth()/2 - width/2)+ 'px';
	
	document.getElementsByTagName('body')[0].appendChild(uploadScreen);		
	document.getElementsByTagName('body')[0].appendChild(uploadDialogue);		
	document.getElementById('uploadDialogue').appendChild(uploadBody);	
	
	var docTypeTag = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">';
	document.getElementById('uploadBody').innerHTML = loadingMessageColSetting +'<img src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	
	Ext.Ajax.request({
	   url: url,
	   params: {module:moduleId},
	   method: 'POST',
	   success: function(result, request){
		    document.getElementById('uploadBody').innerHTML = result.responseText;
		    var makeShared = document.getElementById('makeShared');
	   	    if(makeShared && makeShared.checked)
				loadSharedHtmlContent(makeShared);
				
	   },
	   failure: function(result, response){
		   extAlert(errorAlertTitle, errorColumnSettingPopUp, Ext.MessageBox.ERROR);
	   }
	});
	setNow = true;
}

// Method to hide popup widow
function hideUploadPopup(){
	document.getElementById('uploadDialogue').removeChild(uploadBody);
	document.getElementsByTagName('body')[0].removeChild(uploadDialogue);
	document.getElementsByTagName('body')[0].removeChild(uploadScreen);
	window.document.body.style.overflow = 'visible';
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		document.getElementsByTagName('html')[0].style.overflowY = 'scroll';
		showHtmlSelectTags();
	}
}

// To submit select column and sorting columns
function submitColumnsStatus() {
	var form = self.document.columnForm;
	var parameterString = "[";
	for (var i = 0; i < form.selected.length; i++) {
		parameterString += '{"columnName":"'+ form.selected[i].value +'", "columnValue":"'+ form.selected[i].checked +'"},';
	}
	//remove last unnecessary  comma.
	parameterString = parameterString.substring(0, parameterString.length-1);
	parameterString += "]";
	
	var sorterParameterString = "[";
	for (var index = 1; index < 4; index++){
		if(document.getElementById('sorter_' + index + '_selected') && document.getElementById('sorter_' + index + '_selected').checked)
			sorterParameterString += '{"columnName":"'+ getSelectedValue(document.getElementById('sorter_' + index + '_list')) +'", "order":"'+ getSelectedRadioValue(document.getElementsByName('sorter_'+ index +'_order')) +'"},';
	}
	//remove last unnecessary  comma.
	sorterParameterString = sorterParameterString.substring(0, sorterParameterString.length-1);
	sorterParameterString += "]";
	
	if(sorterParameterString != "]"){
		sort(sorterParameterString, true);
	}
	Ext.Ajax.request({
		url: JSPRootURL + '/portfolio/customizeportfolio/saveChanges',
		params: {parameterString : parameterString, module : moduleId},
		method: 'POST',
		success: function(result, request){
			sorterParameterString = "";
		},
		callback: function(result, request) {
			reloadProjectList();
		}
	});
}

// Function to save column position when position is changed using drag and drop 
function saveColumnSequences(table, draggedColumn, droppedColumn){
	if(draggedColumn == droppedColumn){
		return;
	}
	var header = table.tHead.rows[0].cells;
	if(header.length <= draggedColumn || header.length <= droppedColumn){
		return;
	}
	dragtable.columnSequenceSaved = false;
	Ext.Ajax.request({
		url: JSPRootURL + '/portfolio/Project/saveColumnOrder',
		params: {draggedColumn : header[draggedColumn].getAttribute("columnId"), droppedColumn : header[droppedColumn].getAttribute("columnId")
				,	draggedColumnOrder : draggedColumn, droppedColumnOrder : droppedColumn, module : moduleId},
		method: 'POST',
		success: function(result, request){
			dragtable.columnSequenceSaved = true;
			viewChanged = true;			
		},
		failure: function(result, response){
		}
	});
}

// Function to save column width 
function saveColumnWidth(columnName, width){
	Ext.Ajax.request({
		url: JSPRootURL + '/portfolio/Project/saveColumnWidth',
		params: {columnName : columnName, width : width, module : moduleId},
		method: 'POST',
		success: function(result, request){
			dragtable.columnSequenceSaved = true;
			viewChanged = true;			
		},
		failure: function(result, response){
		}
	});
}
