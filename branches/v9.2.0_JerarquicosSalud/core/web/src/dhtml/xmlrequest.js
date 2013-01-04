/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Netscape code.
 *
 * The Initial Developer of the Original Code is
 * Netscape Corporation.
 * Portions created by the Initial Developer are Copyright (C) 2003
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s): Marcio Galli <mgalli@mgalli.com>
 * Contributors(s): Elder Reami <reami@yahoo.com>
 *
 * ***** END LICENSE BLOCK ***** */

function XMLRemoteRequest() {
	this.XMLHttpComponent = this.getXMLHttpComponentInstance();
}

XMLRemoteRequest.prototype.getXMLHttpComponentInstance = function () {
	var xComp = null;

	try {
		xComp = new XMLHttpRequest();
	} catch (e) {
		try {
			xComp = new ActiveXObject("Microsoft.XMLHTTP");
		} catch (e) {
			window.alert("Unable to instantiate XMLHttpRequest.  We require Netscape 6 or higher o IE 5.5 or higher.");
		}
	}

	return xComp;
}

XMLRemoteRequest.prototype.getRemoteDocument = function (urlString) {
	urlString = addTimestamp(urlString);
	this.XMLHttpComponent.open("GET", urlString, false);
	this.XMLHttpComponent.send(null);
	return this.XMLHttpComponent.responseXML;
}

XMLRemoteRequest.prototype.getRemoteDocumentString = function (urlString) {
	urlString = addTimestamp(urlString);
	this.XMLHttpComponent.open("GET", urlString, false);
	this.XMLHttpComponent.send(null);
	return this.XMLHttpComponent.responseText;
}

XMLRemoteRequest.prototype.asyncRequest = function(urlString) {
	urlString = addTimestamp(urlString);
	this.XMLHttpComponent.open("GET", urlString, true);
	this.XMLHttpComponent.send(null);
}

function addTimestamp(url) {
	var d = new Date();
	var t = d.getTime();
	if(url.indexOf("=") == -1) {
		url += "?xmlRequestTimestamp=" + t;
	} else {
		url += "&xmlRequestTimestamp=" + t;
	}
	return url;
}
