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

var testServerRestart, showPopUpOn;
var isOpen = false,isAppend = false;
var startSessionTime, startSessionValue;
var expiredTime = '';

var JSPRootUrl = readCookie("JSPRootURL");
var applicationTitle;
// This is used for creating popup window
var sessionMessageScreen = document.createElement('div');
sessionMessageScreen.id = 'sessionMessageScreen';
sessionMessageScreen.style.display = 'block';
sessionMessageScreen.style.height = window.screen.height+'px';

var sessionMessageDialogue = document.createElement('div');
sessionMessageDialogue.id = 'sessionMessageDialogue';
sessionMessageDialogue.style.display = 'block';
sessionMessageDialogue.style.width = 500+'px';
sessionMessageDialogue.style.height = 300+'px';

var sessionMessageBody = document.createElement('div');
sessionMessageBody.id = 'sessionMessageBody';
sessionMessageBody.style.display = 'block';

setNow = true;
var sessionAlertMessage;
var sessionTimedOutMessage;
var sessionExpiredInMinutes;
var continueCurrentSession;
var logOutCurrentSession;
var sessionExpiredAt;
var newLoginAfterExpired;
var stillThereOptionMessage;
var sessionExpInMinutesMessage;
var sessionExpTitleMessage;
var stayOnThisPageMessage;

// Get session inactive time from server.
function getSessionInactiveTime(){
	showPopUpOn = 'beforeSessionExpire';	
	Ext.Ajax.request({
		url  : JSPRootUrl + '/sessionHook/Extend/get_session_inactive_interval',
		params  : {showPopUpOn: showPopUpOn},
		method  : 'POST',
		success : function(result, request){
			  var responseText = result.responseText;
			  var value = eval("("+responseText+")");
			  startSessionTime = value.sessionInactiveInterval;
			  startSessionValue = startSessionTime;
			  applicationTitle = value.applicationTitle;
			  sessionAlertMessage = value.sessionAlertMessage;
			  sessionTimedOutMessage = value.sessionTimedOutMessage;
			  sessionExpiredInMinutes = value.sessionExpiredInMinutes;
			  continueCurrentSession = value.continueCurrentSession;
			  logOutCurrentSession = value.logOutCurrentSession;
			  sessionExpiredAt = value.sessionExpiredAt;
			  newLoginAfterExpired = value.newLoginAfterExpired;
			  stillThereOptionMessage = value.stillThereOptionMessage;
			  sessionExpInMinutesMessage = value.sessionExpInMinutesMessage;
			  sessionExpTitleMessage = value.sessionExpTitleMessage;
			  stayOnThisPageMessage = value.stayOnThisPageMessage;
			  if(window != top){
			 	startSessionTime = 0;
			  }
			  putScreenResolutionInCookie();
			  if(startSessionTime != 0 &&  value.startTimer){ //value.startTimer is set to true if session available
				checkSessionInactiveTime();
			  }
		},
		failure: function(result, response){}
	});
}

// 
if(typeof(JSPRootUrl) != 'undefined' && JSPRootUrl != null){
	getSessionInactiveTime();
}

// after getting session inactive time checkSessionInactiveTime start upto 2 min of session expiration.
function checkSessionInactiveTime(){
	if(startSessionTime == 2){
		showPopUpOn = 'beforeSessionExpire';
		testServerRestart = 1;
		showPopupForSessionTimeout(showPopUpOn, testServerRestart);
		return;
	}
	startSessionTime = startSessionTime - 1;
	setTimeout("checkSessionInactiveTime()", 60000);
}

// Creating div elements for sessionTimeout popup window
function showPopupForSessionTimeout(showPopUpOn, testServerRestart) {
	if(showPopUpOn == 'sessionExpired' && testServerRestart == 0 && !isAppend) {
		hideSessionTimeoutPopup();
		isAppend = true;
	}	
	
	var topPos = window.screen.height/7;
	var leftPos = window.screen.width/4;
	sessionMessageDialogue.style.top = topPos+"px";
	sessionMessageDialogue.style.left = leftPos+"px"; 
	
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		window.document.body.style.overflow = 'visible';
	}
	window.document.body.style.overflowY = 'hidden';
	window.document.body.style.overflow = 'hidden';
	window.parent.document.getElementsByTagName('body')[0].appendChild(sessionMessageScreen);		
	window.parent.document.getElementsByTagName('body')[0].appendChild(sessionMessageDialogue);	
	window.parent.document.getElementById('sessionMessageDialogue').appendChild(sessionMessageBody);	
	isAppend = false;
	var closeButton = '	<div><table width="100%"><tr><td>';
	closeButton += '		Loading ... <img src="'+JSPRootURL+'/images/default/grid/loading.gif" /></td>';
	closeButton += '		<td align="right"><a href="javascript:hideSessionTimeoutPopup();">';
	closeButton += '			<img border="0" alt="x" src="'+JSPRootURL+'/images/menu/close.gif" align="right"/>';
	closeButton += '		</a></td>';
	closeButton += '	</tr></table></div>';
	document.getElementById('sessionMessageBody').innerHTML = closeButton;
	getPopupForSessionTimeout(showPopUpOn);	
}

// Request for getting proper popup message by time.
function getPopupForSessionTimeout(showPopUpOn) {
	Ext.Ajax.request({
		url: JSPRootUrl +'/sessionHook/SessionTimeout/get_session_timeout_popup',
		params: {showPopUpOn: showPopUpOn, startSessionTime : startSessionTime, applicationTitle : applicationTitle,
				  sessionInactiveTime : startSessionValue, sessionAlertMessage : sessionAlertMessage,
				  sessionTimedOutMessage : sessionTimedOutMessage, sessionExpiredInMinutes : sessionExpiredInMinutes,
				  continueCurrentSession : continueCurrentSession, logOutCurrentSession : logOutCurrentSession,
				  sessionExpiredAt : sessionExpiredAt, newLoginAfterExpired : newLoginAfterExpired,
				  stillThereOptionMessage : stillThereOptionMessage, sessionExpInMinutesMessage : sessionExpInMinutesMessage,
				  sessionExpTitleMessage : sessionExpTitleMessage, stayOnThisPageMessage : stayOnThisPageMessage},
		method: 'POST',
		success: function(result,request) {
			document.getElementById('sessionMessageBody').innerHTML = result.responseText; 
			if(document.getElementById('expireTime') != null){
				if (expiredTime == '')
					expiredTime = getHHMMTime();
				document.getElementById('expireTime').innerHTML = expiredTime;
			}
			// to scroll the popup, this function is in window_function.js
			movePopup(sessionMessageScreen, sessionMessageDialogue);
			if(startSessionTime == 2) {
				startSessionTimer(); // start timer for next expriration popup
			}
		},
		failure: function(result, response){}
	});
}

// Gives warning message before two minutes.
function startSessionTimer() {
	startSessionTime--;
	setTimeout('setSessionWarningTimer()', 60000); //reduce time by 1 min first and set another timer for 1 min 
}

// Gives warnning message before one minute.
function setSessionWarningTimer() { // this function fist update the time in popup as 1 min remains to expire session
	if(document.getElementById('remainingTimeId') != null) {
		document.getElementById('remainingTimeId').innerHTML = startSessionTime;
	}	
	startSessionTime--;
	setTimeout('sessionExpirationPopup()', 60000);
}

// Gives popup expired message.
function sessionExpirationPopup() { // if startSessionTime is 0, session consider to be expired.
	if(startSessionTime == 0){
		showPopUpOn = 'sessionExpired';
		testServerRestart = 0;
		showPopupForSessionTimeout(showPopUpOn, testServerRestart);
	}
}

// display a expiration popup after server restart
function sessionExpirationPopupAfterRestart(testServerRestart) {
	showPopUpOn = 'SessionExpired';
	showPopupForSessionTimeout(showPopUpOn, testServerRestart);
}

// Request for checking session already expire or not.
function checkSession() {
	isOpen = false;
	showPopUpOn = 0;	
	Ext.Ajax.request({
		url: JSPRootUrl + '/sessionHook/Extend/session_expired',
		params: {showPopUpOn: showPopUpOn},
		method: 'POST',
		success: function(result, request) {
	    	if(result.responseText == "true") {
				resetSessionTimeoutTime();
				hideSessionTimeoutPopup();
			}
		}, 
		failure: function(result, response) {
			 hideSessionTimeoutPopup();
		}
	});
}

// hide popup
function hideSessionTimeoutPopup() {
	isOpen = false;
	if(document.getElementById("sessionMessageScreen") != null) {
		document.getElementsByTagName('body')[0].removeChild(sessionMessageScreen);		
		document.getElementsByTagName('body')[0].removeChild(sessionMessageDialogue);
		window.document.body.style.overflow = 'visible';
		if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
				window.document.body.style.overflow = 'visible';
		}
	}
}

// Resetting session in active time.
function resetSessionTimeoutTime() {
	startSessionTime = startSessionValue;
	if(showPopUpOn != 'comefromRequestComplete'){
		checkSessionInactiveTime();
	}
}

// call for each ajax request to reset session timer
Ext.Ajax.on({"requestcomplete" : function(conn, response, options){
		if(options.url.indexOf('get_session_timeout_popup') == -1){
			showPopUpOn = 'comefromRequestComplete';
			resetSessionTimeoutTime();
		}
	}
});

// call for each ajax exception, if session invalidate dislpay a session expiration popup.
Ext.Ajax.on({"requestexception": function(conn, response, options){
		if(options.url.indexOf('get_session_inactive_interval') == -1){
			if( response.responseText && response.responseText.indexOf('Please Login') != -1 && !isOpen){
				startSessionTime = 0;
				testServerRestart = 1;
				isOpen = true;
				sessionExpirationPopupAfterRestart(testServerRestart);
			}
		}
	}
});

// to get current time 
function get12Hours(dt) {
  return dt.getHours() == 0 ? 12 :
  (dt.getHours() > 12 ? dt.getHours() - 12 : dt.getHours());
}

// get time in hours:minutes AM/PM format
function getHHMMTime() {
	var dt = new Date();
	var minutes = dt.getMinutes() < 10 ? '0' + dt.getMinutes() : dt.getMinutes();
	return hhmmtime = get12Hours(dt) + ':' + minutes +' '+ (dt.getHours() > 11 ? "PM" : "AM" );
}


function _enableShortcuts(){
// BlogIt actions
shortcut.add("F2", function() { try{ if(document.getElementById('blog-ItEnabled') != null)blogit();}catch(e){};}); // BlogIt
}

Ext.onReady(_enableShortcuts);

/**
* Ajax request to set screen resolution in session.
* which will use to render page with perfect screen resolution.
*/
function putScreenResolutionInCookie(){
	Ext.Ajax.request({
		url: JSPRootUrl +'/sessionHook/Extend/put_screen_resolution_in_cookie',
		params: {windowWidth: getWindowWidth(), windowHeight: getWindowHeight()},
		method: 'POST',
		success: function(result,request){},
		failure: function(result, response){}
	});
}
