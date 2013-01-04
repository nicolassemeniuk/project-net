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
var uploadScreen = document.createElement('div');
uploadScreen.id = 'uploadScreen';
uploadScreen.style.display = 'none';
var uploadDialogue = document.createElement('div');
uploadDialogue.id = 'uploadDialogue';
uploadDialogue.style.display = 'none';
var uploadBody = document.createElement('div');
uploadBody.id = 'uploadBody';
uploadDialogue.style.top = '100px';
uploadDialogue.style.left = '200px';
var uploadiFrameDiv = document.createElement('div');
uploadiFrameDiv.id = 'uploadiFrameDivId';
uploadiFrameDiv.style.display = 'none';
var uploadiFrame;
var validImage, errorAlertTitle;
var imageTypeNotSupportedMessage, selectImageToUploadMessage, selectedInvalidImageMessage, invalidFilePathMessage;
var scriptTag, scriptTags, responseText, javaScriptCode;
var newId = getRandomNumber('p');

if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0){
	uploadiFrame = document.createElement('<iframe name="uploadPicture'+newId+'" id="uploadPicture'+newId+'" style="display: none">');
}else {
	uploadiFrame = document.createElement('iframe');
	uploadiFrame.name = 'uploadPicture'+newId;
	uploadiFrame.id = 'uploadPicture'+newId;
	uploadiFrame.style.display = 'none';
}
function extAlert(title, msg, icon){
	Ext.MessageBox.show({
		title: title,
		msg: msg,
		buttons: Ext.MessageBox.OK,
		icon: icon
	});
}  

function showUploadPopup(url, width, weblogEntryId ,uploadFor) {
	uploadScreen.style.height = window.screen.height+'px';
	uploadScreen.style.display = 'block';
	uploadDialogue.style.display = 'block';
	uploadDialogue.style.width = width+'px';

	var spaceId = null;
	var userId = null;
	var wikiPageId = null;
	var wikiPageName = null;
	var parentPageName = null;
	
	if(document.getElementById('spaceId') && document.getElementById('userId') 
		&& document.getElementById('wikiPageId') && document.getElementById('wikiPageName')
		&& document.getElementById('parentPageName')){
		spaceId = document.getElementById('spaceId').value;
		userId = document.getElementById('userId').value;
		wikiPageId = document.getElementById('wikiPageId').value;
		wikiPageName = document.getElementById('wikiPageName').value;
		parentPageName = document.getElementById('parentPageName').value;
	}
	
	document.getElementsByTagName('body')[0].appendChild(uploadiFrameDiv);	
	document.getElementById('uploadiFrameDivId').appendChild(uploadiFrame);
	document.getElementsByTagName('body')[0].appendChild(uploadScreen);		
	document.getElementsByTagName('body')[0].appendChild(uploadDialogue);		
	document.getElementById('uploadDialogue').appendChild(uploadBody);	
	
	window.document.body.style.overflow = 'hidden';	
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		document.getElementsByTagName('html')[0].style.overflowY = 'hidden';
		hideHtmlSelectTags();
	}
	
	var docTypeTag = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">';
	document.getElementById('uploadBody').innerHTML = 'Loading popup... <img src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	Ext.Ajax.request({
	   url: url+'?module='+moduleId,
	   params: { uploadFor : uploadFor, module : moduleId, weblogEntryId : weblogEntryId, spaceId : spaceId, userId : userId,
	   			 wikiPageId : wikiPageId, wikiPageName : wikiPageName, parentPageName: parentPageName},
	   method: 'POST',
	   success: function(result, request){
		    document.getElementById('uploadBody').innerHTML = '<div>'+result.responseText + '</div>';
		    responseText = result.responseText.replace(docTypeTag, '');
		    javaScriptCode = responseText;		     
		    scriptTag = '<script space="preserve" type="text/javascript">';
		    javaScriptCode = javaScriptCode.substring(javaScriptCode.lastIndexOf(scriptTag)+scriptTag.length, javaScriptCode.lastIndexOf('<\/script>'));
		    scriptTags = document.getElementsByTagName('script');
		    for(var index = 0; index < scriptTags.length; index++){
		    	if(scriptTags[index].innerHTML.indexOf('selectImageToUploadMessage') >= 0){
			    	javaScriptCode = scriptTags[index].innerHTML;
			    	eval(javaScriptCode.replace(/\n/g, ''));
			    }
			 }
   	        if(document.getElementById('file:icon') != null){
		   		document.getElementById('file:icon').style.display= 'none';
		    }
	   },
	   failure: function(result, response){
		   extAlert('Error', 'Error occurred while loading popup.', Ext.MessageBox.ERROR);
	   }
	});
	setNow = true;
	movePopup(document.getElementById('uploadScreen'), document.getElementById('uploadDialogue'));
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

// Checking if file type is supported or not
function isFileTypeSupported(fileName) {
	var extension = fileName.toLowerCase().substring(fileName.lastIndexOf('.')+1, fileName.length);
	return (extension  == 'gif' || extension == 'png' || extension == 'jpg' || extension == 'jpeg' || extension == 'bmp');		
}

// Upload the image
function uploadDocument(uploadFor){
	var imageUrl = document.getElementById("file").value;
	
	if(imageUrl != null && imageUrl != ''){
		document.getElementById("form").target = "uploadPicture"+newId;
		if(uploadFor == 'blogPage'){
			document.getElementById("form").submit();
		} else if( uploadFor == 'memberview' && isFileTypeSupported(imageUrl)){
			if(userId == selectedUserId ){
					document.getElementById("form").submit();
					setTimeout("uploadPictureSuccess()", 10000);
					hideUploadPopup();
			}else{
				extAlert('error',errormessage,Ext.MessageBox.ERROR);
			}
		}else if(isFileTypeSupported(imageUrl)) {
			try{
				document.getElementById("form").submit();
				setTimeout("uploadPictureSuccess()", 10000);
				hideUploadPopup();
			}
			catch(e){
				extAlert(errorAlertTitle, invalidFilePathMessage, Ext.MessageBox.ERROR);
				return false;
			}
		} else {
			extAlert(errorAlertTitle, imageTypeNotSupportedMessage, Ext.MessageBox.ERROR);
			return false;
		}
	} else {
		extAlert(errorAlertTitle, selectImageToUploadMessage, Ext.MessageBox.ERROR);
		return false;
	}
}
//checking the picture is submit or not
function uploadPictureSuccess(){
	var getInnerHtmlData = window["uploadPicture"+newId].document.body.innerHTML;
	
	if( getInnerHtmlData.indexOf("Upload Image") > 0 && validImage ){
		if(typeof(uploadFromPage) != 'undefined' && uploadFromPage == 'memberview') {
			hideRemoveImageLink(true);
			document.getElementById('personImage').src = JSPRootURL+"/servlet/photo?id="+ memberId +"&size=medium&module="+moduleId;
			self.location = JSPRootURL + "/roster/MemberEdit.jsp?memberid=" + memberId + "&module="+ moduleId + "&action=" + actionId;
		} else {
			window.location.reload();
		}
	} else {
		setTimeout("uploadPictureSuccess()", 10000);		
	}	
}

function confirmRemoveImage(){
	Ext.MessageBox.confirm('Confirm', confirmRemoveImageMessege, 
  		  function(btn){
  		  	if(btn == 'yes') { 
  		  		if(typeof(uploadFromPage) != 'undefined' && uploadFromPage == 'memberview') {
  		  			removeMemberImage();
  		  		} else {
  		  			removeImage();
  		  		}
  		  	} else { 
  		  		return false; 
  		  	}
  	 });
}
 
function removeMemberImage(){
	if(userId == selectedUserId){
		removeImage();
	} else {
		extAlert('Error',removeImageerrormessage,Ext.MessageBox.ERROR);
	}
	
}

// Removing persons image
function removeImage() {		
	Ext.Ajax.request({
	   url: JSPRootURL+'/personal/UploadDocument/'+userId+'?module='+moduleId,
	   params: {module : moduleId},
	   method: 'POST',
	   success: function(result, request) {
	         if(result.responseText == "true") {
		         document.getElementById('personImage').src = JSPRootURL+"/images/NoPicture.gif";
		         hideRemoveImageLink(false);
		   	 }
	   },
	   failure: function(result, response){
		   extAlert(errorAlertTitle, 'Error occurred while removing the image.', Ext.MessageBox.ERROR);
	   }
	});
}

// To hide remove picture link.
function hideRemoveImageLink(disabled) {
	var memberEditLinks;
	if(document.getElementById('left-navbar') != null || document.getElementById('left') != null) {
		if(document.getElementById('left-navbar') != null){
			memberEditLinks =  document.getElementById('left-navbar').getElementsByTagName('a');
		}else {
			memberEditLinks =  document.getElementById('left').getElementsByTagName('a');
		}
		var functionNameString = "javascript:confirmRemoveImage();";
		for (var iterator =0; iterator < memberEditLinks.length; iterator++) {
			if (functionNameString.indexOf(memberEditLinks[iterator].href) != -1) {
				if(!disabled) {
					memberEditLinks[iterator].style.display = 'none';
				} else {
					memberEditLinks[iterator].style.display = '';
				}
			}
		}
	}
}

//////////////For Wiki///////////////////////

// This function is for triming the spaces in values
String.prototype.trim = function(){
    a = this.replace(/^\s+/, '');
    return a.replace(/\s+$/, '');
}

// Checking the file is selected before submitting the form to upload the file	
function validFile(){
	if(document.getElementById('uploadedFile').value.trim() == '' ) {
		extAlert("Error", 'Please select the file from system', Ext.MessageBox.ERROR);
		return false;
	}
	//return !isInvalidChars(document.getElementById('uploadedFile').value);
	return true;
}

// Submitting the form to upload image from wik ipage
function submitForm() {
	if (!validFile()) {
		return false;
	} else {
		document.getElementById('indicator').style.display = '';
		document.forms['form'].submit();
  	}
}

// Ext ajax request to check file existence in wiki space
function doesFileExist() {
	document.getElementById('indicator').style.display = '';
	var fileName = Ext.get('uploadedFile').getValue(false);
	var optionsEl = document.getElementById('options');
	var typeNotSupportedEl = document.getElementById('typeNotSupported');
	var invalidFileName = document.getElementById('invalidFileName');
	Ext.Ajax.request({
	   url: JSPRootURL+'/pwiki/Upload/checkFileExistence?module='+moduleId,
	   params: {module: 150, imageName: fileName},
	   method: 'POST',	   
	   success: function(result, request){
	   	   if(result.responseText == 'FileTypeNotSupported'){
	   	       typeNotSupportedEl.style.display = '';
	   	       document.getElementById('addImageButton').disabled = true;
	   	   } else if(result.responseText == 'InvalidFileName' ){
	   	   	   invalidFileName.style.display = '';
	   	   	   optionsEl.style.display = 'none';
	   	   	   document.getElementById('addImageButton').disabled = true;
	   	   } else if(result.responseText == 'true'){
				optionsEl.style.display = '';
				invalidFileName.style.display = 'none';
				document.getElementById('addImageButton').disabled = false;
			} else {
				optionsEl.style.display = 'none';
				typeNotSupportedEl.style.display = 'none';
 			    invalidFileName.style.display = 'none';
				document.getElementById('addImageButton').disabled = false;
			}				
			document.getElementById('indicator').style.display = 'none';
   		},
	   failure: function(result, response){
		    extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
	   }
	});
}
	
//////////////For User Manangement Import Functionality///////////////////////

function validateType(){
	document.getElementById('uploadId').disabled = false;
	var fileName = document.getElementById("file").value
	var extension = fileName.toLowerCase().substring(fileName.lastIndexOf('.')+1, fileName.length);
	var selectedExtension = document.getElementById('typeId').value;
	if(fileName!= "" && typeof(selectedExtension) != 'undefined' && selectedExtension != null ) {
		if ( extension  != selectedExtension ) {
			document.getElementById('typeNotSupported').style.display = '';
			document.getElementById('uploadId').disabled = true;
		} else {
			document.getElementById('typeNotSupported').style.display = 'none';
			document.getElementById('uploadId').disabled = false;
		}
	} else {
		document.getElementById('uploadId').disabled = true;
	}
}
// sends the ajax request to upload a file.
AIM = {
 
	frame : function(c) {
 
		var n = 'f' + Math.floor(Math.random() * 99999);
		var d = document.createElement('DIV');
		d.innerHTML = '<iframe style="display:none" src="about:blank" id="'+n+'" name="'+n+'" onload="AIM.loaded(\''+n+'\')"></iframe>';
		document.body.appendChild(d);
 
		var i = document.getElementById(n);
		if (c && typeof(c.onComplete) == 'function') {
			i.onComplete = c.onComplete;
		}
 
		return n;
	},
 
	form : function(f, name) {
		f.setAttribute('target', name);
	},
 
	submit : function(f, c) {
		if(f.file.value != null && f.file.value != ''){
			if(isSupportedFiles(f.file.value)){
				AIM.form(f, AIM.frame(c));
				if (c && typeof(c.onStart) == 'function') {
					return c.onStart();
				} else {
					return true;
				}
			} else {
				extAlert('Error', 'This file type is not supported. Please select the image file of type .csv, .xml, .vcf', Ext.MessageBox.ERROR);
				return c.onComplete();
			}
		} else {
			extAlert('Error', 'Please select a file to upload', Ext.MessageBox.ERROR);
			return c.onComplete();
		}
	},
 
	loaded : function(id) {
		var i = document.getElementById(id);
		if (i.contentDocument) {
			var d = i.contentDocument;
		} else if (i.contentWindow) {
			var d = i.contentWindow.document;
		} else {
			var d = window.frames[id].document;
		}
		if (d.location.href == "about:blank") {
			return;
		}
 
		if (typeof(i.onComplete) == 'function') {
			i.onComplete(d.body.innerHTML);
		}
	}
 
}

// call on start of uploading file
function startCallback() {
	document.getElementById('uploadDialogue').style.display = 'none';
	document.getElementById('uploadScreen').style.display = 'none';
	return true;
}

// call on file upload completion
function completeCallback(response) {
	hideUploadPopup();
	// call for refreshing invitee list zone.
	forRefreshInviteeList();
}

function fileType(){
	document.getElementById('typeId').value = document.getElementById('filetypeId').value;
	fireEvent(document.getElementById('typeId'),'change');
}

function isSupportedFiles(fileName){
	var extension = fileName.toLowerCase().substring(fileName.lastIndexOf('.')+1, fileName.length);
	return (extension  == 'csv' || extension == 'vcf' || extension == 'xml');
}
