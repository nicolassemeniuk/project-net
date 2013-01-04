var uploadScreen = document.createElement('div');
uploadScreen.id = 'uploadScreen';
uploadScreen.style.display = 'none';
var uploadDialogue = document.createElement('div');
uploadDialogue.id = 'uploadDialogue';
uploadDialogue.style.border = 'none';
uploadDialogue.style.display = 'none';
var uploadBody = document.createElement('div');
uploadBody.id = 'uploadBody';
uploadDialogue.style.top = '172px';
uploadDialogue.style.left = '200px';
var validImage, errorAlertTitle;
var scriptTag, scriptTags, responseText, javaScriptCode;

/* open customization popup view to changes the status of columns */
function openCustomizeColumnPopup() {
	window.document.body.style.overflow = 'hidden';	
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		document.getElementsByTagName('html')[0].style.overflowY = 'hidden';
		hideHtmlSelectTags();
	}
	var windowScrollbarY = typeof window.pageYOffset != 'undefined' ? window.pageYOffset : document.documentElement.scrollTop;

	url = JSPRootURL + '/assignments/customize';
	width = 625;
	uploadScreen.style.height = window.screen.height+'px';
	uploadScreen.style.display = 'block';
	uploadDialogue.style.display = 'block';
	uploadDialogue.style.width = width+'px';
	
	document.getElementsByTagName('body')[0].appendChild(uploadScreen);		
	document.getElementsByTagName('body')[0].appendChild(uploadDialogue);		
	document.getElementById('uploadDialogue').appendChild(uploadBody);	
	
	var docTypeTag = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">';
	document.getElementById('uploadBody').innerHTML = loadingMessage +'<img src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	
	Ext.Ajax.request({
	   url: url,
	   params: {module:moduleId},
	   method: 'POST',
	   success: function(result, request){
		    document.getElementById('uploadBody').innerHTML = result.responseText;
	   },
	   failure: function(result, response){
		   //extAlert(errorAlertTitle, errorColumnSettingPopUp, Ext.MessageBox.ERROR);
	   }
	});
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

//To save column settings
function submitColumnsStatus() {
	showLoadingDiv(loadingMessage);
	var form = self.document.columnForm;
	var parameterString = "[";
	for (var elementIndex = 0; elementIndex < form.selected.length; elementIndex++) {
		parameterString += '{"columnName":"'+ form.selected[elementIndex].value +'", "columnValue":"'+ form.selected[elementIndex].checked +'"},';
	}
	//remove last unnecessary  comma.
	parameterString = parameterString.substring(0, parameterString.length-1);
	parameterString += "]";
	if(document.getElementById('uploadDialogue')){
		hideUploadPopup();
	}
	Ext.Ajax.request({
		url: JSPRootURL + '/assignments/customize',
		params: {parameterString : parameterString },
		method: 'POST',
		success: function(result, request){
			if(result.responseText != 'false'){
				updateAssignments(result.responseText, null);
			}else{
				extAlert(errorAlertTitle, requestFailedMsg, Ext.MessageBox.ERROR)
			}
			removeLoadingDiv();
		}
	});
}

//save column order, using ajax request.
function saveColumnSequences(table, draggedColumn, droppedColumn){
	if(draggedColumn == droppedColumn){
		return;
	}
	var header = table.tHead.rows[0].cells;
	if(header.length <= draggedColumn || header.length <= droppedColumn){
		return;
	}
	dragtable.columnSequenceSaved = false;
	showLoadingDiv(persistingColumnMessage);
	Ext.Ajax.request({
		url: JSPRootURL + '/assignments/customize',
		params: {draggedColumn : header[draggedColumn].getAttribute("columnId"), droppedColumn: header[droppedColumn].getAttribute("columnId")},
		method: 'POST',
		success: function(result, request){
			dragtable.columnSequenceSaved = true;
			removeLoadingDiv();
		},
		failure: function(result, response){
			removeLoadingDiv();
		}
	});
}