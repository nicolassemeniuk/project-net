var uploadScreen = document.createElement('div');
uploadScreen.id = 'uploadScreen';
uploadScreen.style.display = 'none';
var uploadDialogue = document.createElement('div');
uploadDialogue.id = 'uploadDialogue';
uploadDialogue.style.border = 'none';
uploadDialogue.style.display = 'none';
var uploadBody = document.createElement('div');
var notifyEventsDiv;
var spaceParticipantDiv;
var isPersonalSpace;
uploadBody.id = 'uploadBody';

var nameRequiredErrorMessage;
var editsubscriptionSeperateText;
var externalEmailAddressErrorMessage;
var loadingImage;
var actionToNotifiedrequiredErrorMessage;
var deliverToRequiredErrorMessage;
var errorAlertTitle;
var savingMessage;
var spaceId;
var notificationSavingFailed;
function checkExternalEmailList(){
		var otherEmails = "";
		if(document.getElementById("otherEmail")){
			otherEmails = document.getElementById("otherEmail").value;
		}
		var emailList = trim(otherEmails);
			var regex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@([A-Za-z0-9-]{2,4})+(\\.([A-Za-z0-9-]{2,4})+)+$";
			var arrEmail = emailList.split(",");
			if(arrEmail.length > 0){
				if(emailList.length > 0){
					for(i = 0; i < arrEmail.length; i++){
						arrEmail[i] = trim(arrEmail[i]);
					}

				for(i = 0; i < arrEmail.length; i++){
					var email = arrEmail[i];
					if(email.match(regex) == null){
						extAlert(errorTitle, externalEmailAddressErrorMessage , Ext.MessageBox.ERROR);
						return false;
					} else {
						arrEmail[i] = ""+i;
						arrEmail[i] = arrEmail[i];
						for(j = 0; j < arrEmail.length; j++){
							if(email == arrEmail[j]){
								extAlert(errorTitle, externalEmailAddressErrorMessage , Ext.MessageBox.ERROR);
								return false;
							}
						}
					}
				}
			}	
		}
		
		if(document.getElementById("txt") && document.getElementById("txt")!=null){
			document.getElementById("txt").innerHTML= editsubscriptionSeperateText;		
		}
	return true;
}



// Open Notify popup
function openNotifyPopup(targetObjectID, m_url) {
	var url = "";
	var width = 600;
	uploadDialogue.style.width = width +'px';
	uploadDialogue.style.left = (window.screen.width/2 - width/2)+ 'px';

	uploadDialogue.style.top = getWindowHeight()/10;
	uploadScreen.style.height = window.screen.height + 'px';
	window.document.body.style.overflow = 'hidden';	
	window.document.body.style.overflowY = 'scroll';
	document.getElementsByTagName('html')[0].style.overflowY = 'scroll';
	if(typeof(targetObjectID) != 'undefined' &&  m_url ){
		url = m_url;
	} else if(typeof(targetObjectID) != 'undefined'){ 
		url = JSPRootURL + '/notification/CreateSubscription2.jsp?targetObjectID='+targetObjectID;
	} else {
		url = m_url;
	}
	
	uploadScreen.style.display = 'block';
	uploadDialogue.style.display = 'block';
	
	document.getElementsByTagName('body')[0].appendChild(uploadScreen);		
	document.getElementsByTagName('body')[0].appendChild(uploadDialogue);		
	document.getElementById('uploadDialogue').appendChild(uploadBody);	
	
	var docTypeTag = '<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">';
	document.getElementById('uploadBody').innerHTML = 'Loading	...<img src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	Ext.Ajax.request({
	   url: url,
	   method: 'POST',
	   success: function(result, request){
	    	var responseText = result.responseText.replace(docTypeTag, '');	
			document.getElementById('uploadBody').innerHTML = responseText;	
			var scriptTag = '/*Start Script*/';
			var javaScriptCode = responseText;
			javaScriptCode = javaScriptCode.substring(javaScriptCode.lastIndexOf(scriptTag)+scriptTag.length, javaScriptCode.indexOf('/*End Script*/'));
			eval(javaScriptCode.replace(/\n/g, ''));
					
			if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
				hideHtmlSelectTags();
				document.getElementById('deliveryTypeID').style.visibility = 'visible';
				document.getElementById('deliveryInterval').style.visibility = 'visible';
			}
			document.getElementById("name").focus();
			setCheckBoxListDivHeight(document.getElementById('notifyEventsDiv'), 'input', 4);
			if(!checkIsPersonalSpace(isPersonalSpace)){				
			setCheckBoxListDivHeight(document.getElementById('spaceParticipantDiv'), 'input', 4);
			}
	   },
	   failure: function(result, response){
	   }
	});
}

// Method to hide popup widow
function hideNotifyPopup(){
	document.getElementById('uploadDialogue').removeChild(uploadBody);
	document.getElementsByTagName('body')[0].removeChild(uploadDialogue);
	document.getElementsByTagName('body')[0].removeChild(uploadScreen);
	window.document.body.style.overflow = 'visible';
	document.getElementsByTagName('html')[0].style.overflowY = 'auto';
	window.document.body.style.overflowY = 'auto';
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		document.getElementsByTagName('html')[0].style.width='100%';
		showHtmlSelectTags();
	}
}

// Method to save notification
function saveNotification(){
	var status = getSelectedValue(document.getElementsByName("status")); 
	var name = trim(document.getElementById("name").value);
	var notificationType = document.getElementsByName('notificationType');
	var deliveryTypeID = document.getElementById('deliveryTypeID').value;
	var deliveryInterval = document.getElementById("deliveryInterval").value; 
	var notificationTypeValue = "";
	var teamMembersValue = "";
	var notifyCount = 0;
	var deliverCount = 0;
	var otherEmail = "";
	var customMessage = trim(document.getElementById('customMessage').value);
	var description = trim(document.getElementById("description").value);
	
	// to replace \n with \\n new line character for customMessage and description
	customMessage = customMessage.replace(new RegExp("\\n", "g" ),'\\n');
	description = description.replace(new RegExp("\\n", "g" ),'\\n');
	
	if(document.getElementById("otherEmail")){
		otherEmail = document.getElementById("otherEmail").value;
	}
	
	 if(name == "" || name == null || name == 'undefiend' || trim(name).length == 0){
		extAlert(errorAlertTitle, nameRequiredErrorMessage, Ext.MessageBox.ERROR);
		document.getElementById("name").focus();
		return;
	}
	 
	for(var index=0; index<notificationType.length; index++){
		if(notificationType[index].checked){	
			notificationTypeValue += notificationType[index].value+',';
			++notifyCount;
		}	
	}
	
	if(notifyCount == 0){
		extAlert(errorAlertTitle, actionToNotifiedrequiredErrorMessage , Ext.MessageBox.ERROR);
		return;
	}

	if(document.getElementById("teamMember")){
		teamMembersValue = document.getElementById("teamMember").value;
	}else {
		var teamMembers = document.getElementsByName("teamMembers");
		for(var index = 0; index < teamMembers.length; index++){
			if(teamMembers[index].checked){
				teamMembersValue += teamMembers[index].value+',';
				deliverCount++;
			}
		}
		if(deliverCount == 0){
			extAlert(errorAlertTitle, deliverToRequiredErrorMessage , Ext.MessageBox.ERROR);
			return;
		}
			teamMembersValue = teamMembersValue.substring(0, teamMembersValue.length-1);
	}	
	if(!checkExternalEmailList())
		return;
	
	notificationTypeValue = notificationTypeValue.substring(0, notificationTypeValue.length-1);
	
	var newNotifyJsonObject = '{"status" : "'+ status
 						+'","name" : "'+ name
 						+'","description" : "'+ description
 						+'","deliveryTypeID" : "'+ deliveryTypeID
 						+'","deliveryInterval" : "'+ deliveryInterval   
 						+'","otherEmail" : "'+ otherEmail
 						+'","notificationType" : "'+notificationTypeValue  
 						+'","customMessage" : "'+ customMessage  
 						+'","teamMembers" : "'+teamMembersValue + '"}'; 


	url = JSPRootURL + '/notification/CreateSubscription2Processing.jsp';
	hideNotifyPopup();
	showLoadingDiv(savingMessage);
	
	Ext.Ajax.request({
	    url: url,
	    params: {newNotifyJsonObject : newNotifyJsonObject,theAction : "popup", spaceID : spaceId},
	    method: 'POST',
	    timeout: 120000,
	    success: function(result, request){
			 removeLoadingDiv();
	    },
	    failure: function(result, response){
	    	extAlert(errorTitle, notificationSavingFailed, Ext.MessageBox.ERROR);
	    	removeLoadingDiv();
	    }
	});
}

//Method to select more less options
function toggleOptions(option){
	if(option == 'more'){
		document.getElementById('moreoptions').style.display = 'none';
		document.getElementById('lessoptions').style.display = '';
		document.getElementById('extraItems').style.display = '';
	} else if(option == 'less'){
		document.getElementById('moreoptions').style.display = '';
		document.getElementById('lessoptions').style.display = 'none';
		document.getElementById('extraItems').style.display = 'none';
	}
}

// Method to set size of div containing single or multiple check boxes  
function setCheckBoxListDivHeight(DivObject ,type , noOfCheckBox){
	if(DivObject && DivObject.getElementsByTagName(type).length > noOfCheckBox){
		DivObject.style.height = (noOfCheckBox * 22) + 'px';
	}
}

function checkIsPersonalSpace(isPersonalSpace){
	if(isPersonalSpace && document.getElementById('spaceParticipantDiv') && (isPersonalSpace == 'true') && (document.getElementById('spaceParticipantDiv').getElementsByTagName('input').length == 1) ){
		document.getElementById('spaceParticipantDiv').getElementsByTagName('input')[0].checked = 'checked';
		document.getElementById('delivertoTR').style.display = 'none';
		return true;
	}
	return false;
}
