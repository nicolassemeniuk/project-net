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
function extAlert(title, msg, icon){
	Ext.MessageBox.show({
		title: title,
		msg: msg,
		buttons: Ext.MessageBox.OK,
		icon: icon
	});
}  

/* open save charge code popup to create new or edit existing */
function openSaveChargeCodePopup(theAction) {
	action = theAction;
	window.document.body.style.overflow = 'hidden';	
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		document.getElementsByTagName('html')[0].style.overflowY = 'hidden';
		hideHtmlSelectTags();
	}
	var windowScrollbarY = typeof window.pageYOffset != 'undefined' ? window.pageYOffset : document.documentElement.scrollTop;

	url = JSPRootURL + '/chargecode/Save';
	width = 450;
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
	document.getElementById('uploadBody').innerHTML = loadingMessageSaveChargeCode +'<img src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	
	Ext.Ajax.request({
	   url: url,
	   params: {module: moduleId, action : action, codeId: chargeCodeId, codeName: chargeCodeName,
	   			 codeNo: chargeCodeNo, codeDesc: chargeCodeDesc},
	   method: 'POST',
	   success: function(result, request){
		    document.getElementById('uploadBody').innerHTML = result.responseText;
		    responseText = result.responseText.replace(docTypeTag, '');  
		    javaScriptCode = responseText;
		    scriptTag = '<script type="text/javascript">';
		   	javaScriptCode = javaScriptCode.substring(javaScriptCode.lastIndexOf(scriptTag)+scriptTag.length, javaScriptCode.lastIndexOf('<\/script>'));
		    scriptTags = document.getElementsByTagName('script');
		    javaScriptCode = scriptTags[scriptTags.length-2].innerHTML;
   	        eval(javaScriptCode.replace(/\n/g, ''));
   	        if(action == 'save_charge_code'){
   	        	document.getElementById('save-charge-code-page-heading').innerHTML = createTitle;
   	        }
   	        else {
   	        	document.getElementById('save-charge-code-page-heading').innerHTML = editTitle;	
   	        }
	   },
	   failure: function(result, response){
		   extAlert(errorAlertTitle, errorColumnSettingPopUp, Ext.MessageBox.ERROR);
	   }
	});
	setNow = true;
}

// Method to hide popup widow
function hideUploadPopup(){
	if(!document.getElementById('uploadDialogue')){
		return;
	}
	document.getElementById('uploadDialogue').removeChild(uploadBody);
	document.getElementsByTagName('body')[0].removeChild(uploadDialogue);
	document.getElementsByTagName('body')[0].removeChild(uploadScreen);
	window.document.body.style.overflow = 'visible';
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		document.getElementsByTagName('html')[0].style.overflowY = 'scroll';
		showHtmlSelectTags();
	}
}

// Method to create, edit or delete charge code by ajax request.
function manageChargeCodeAjaxAction(){
	var form;

	if(action == 'delete_charge_code' && chargeCodeId == '' || action == 'edit_charge_code' && chargeCodeId == ''){
		return;
	}
		
	if(action == 'save_charge_code' || action == 'edit_charge_code' ){
		form = self.document.form;
		chargeCodeId = form.codeId.value;
		chargeCodeNo = form.codenumber.value;
		chargeCodeName = form.codename.value;
		chargeCodeDesc = form.codedescription.value;
	}
	showLoadingDiv(savingMsg);
	Ext.Ajax.request({
		url: JSPRootURL + '/chargecode/Manage/'+action,
		params: {moduleId: moduleId, codeId: chargeCodeId, codeNo : chargeCodeNo, codeName: chargeCodeName
				, description: chargeCodeDesc, bussinessId: businessId},
		method: 'POST',
		timeout: 180000,
		success: function(result, request){	
			if(result.responseText == 'updated'){	
				updateRowData(chargeCodeId, chargeCodeNo, chargeCodeName, chargeCodeDesc);
				removeLoadingDiv();		
			} else if (result.responseText == 'deleted'){
				removeDeletedChargeCodeRow(chargeCodeId);
			}
			else {
				if(totalchargeCode > 0){
					addChargeCodeRow(result.responseText, chargeCodeNo, chargeCodeName, chargeCodeDesc);
					removeLoadingDiv();		
				} else {
						self.document.location = JSPRootURL + "/chargecode/Manage?referer=business/Setup.jsp?module=" +moduleId;
				}
			}	
			resetChargeCodeDataVariable()
		}		
	});
}

// Update Row Data when charge code is updated
function updateRowData(chargeCodeId, chargeCodeNo, chargeCodeName, chargeCodeDesc){
	var cell = document.getElementById(chargeCodeId).cells;
	cell[0].innerHTML = chargeCodeNo;
	cell[1].innerHTML = chargeCodeName;
	cell[2].innerHTML = chargeCodeDesc;
	resetChargeCodeDataVariable();
}

// Add a row to charge code table with data containing newly created charge code
function addChargeCodeRow(codeId, codeNo, codeName, description){
	
	var tbl = document.getElementById('charge-code-table');
	var lastRow = tbl.rows[tbl.rows.length - 1];
	var newRow = lastRow.cloneNode(true);
	lastRow.parentNode.insertBefore(newRow, lastRow.nextSibling);
	
	var newRowTd = newRow.getElementsByTagName('td');
	
	newRow.id = codeId; 
	newRowTd[0].innerHTML = codeNo;
	newRowTd[1].innerHTML = codeName;
	newRowTd[2].innerHTML = description;
	
	totalchargeCode = totalchargeCode + 1;
}

// To reset variable containing charge code values provided by user while creating or editing charge code.
function resetChargeCodeDataVariable(){
	chargeCodeId = '';
	chargeCodeName = '';
	chargeCodeNo = '';
	chargeCodeDesc = '';
	hideUploadPopup();			
}

// To remove deleted view row from table
function removeDeletedChargeCodeRow(codeId){
	var rowObj = document.getElementById(codeId);
	var tableObj = document.getElementById('charge-code-table');
	tableObj.deleteRow(rowObj.rowIndex); 
}
