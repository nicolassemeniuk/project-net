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

function isURL(theurl) {
	if((theurl != null) && (theurl != "")) {
	var tomatch= /^(http|https):\/\/[A-Za-z0-9\.-]{3,}\.[A-Za-z]{3}/
	if (!tomatch.test(theurl)){
		 return false;
		}
	}
	 return true; 
}  


function isURL2(urlStr, urlSpaceAreNotAllowedErrMes, urlIncorrectErrMes, urlInvalidCharactersErrMes, urlDomainNameInvalidCharactersErrMes, urlDomainNameInvalidErrMes) {
		if(urlStr==""||urlStr==null){
			return false;
		}
		if (urlStr.indexOf(" ")!=-1) {
			extAlert(errorTitle, urlSpaceAreNotAllowedErrMes, Ext.MessageBox.ERROR);
			return false;
		}
		urlStr=urlStr.toLowerCase();
		var specialChars="\\(\\)><@,;:\\\\\\\"\\.\\[\\]";
		var validChars="\[^\\s" + specialChars + "\]";
		var atom=validChars + '+';
		var urlPat=/^(http|https|file|ftp|cvs|svn):\/\/((\w*)\.)?([\-\+a-zA-Z0-9\/]*)(\.)?(\w*)/;
		var matchArray=urlStr.match(urlPat);
		if (matchArray==null){
			extAlert(errorTitle, urlIncorrectErrMes, Ext.MessageBox.ERROR);		
			return false;
		}
		var user = (typeof(matchArray[2]) == 'undefined' ? '' : matchArray[2]);
		var domain = (typeof(matchArray[3]) == 'undefined' ? '' : matchArray[3]);
		if (user) {
			for (i=0; i<user.length; i++) {
				if (user.charCodeAt(i)>127) {
					extAlert(errorTitle, urlInvalidCharactersErrMes, Ext.MessageBox.ERROR);		
					return false;
				}
			}
		}
		for (i=0; i<domain.length; i++) {
			if (domain.charCodeAt(i)>127) {
				extAlert(errorTitle, urlDomainNameInvalidCharactersErrMes, Ext.MessageBox.ERROR);		
				return false;
			}
		}
		var atomPat=new RegExp("^" + atom + "$");
		var domArr=domain.split(".");
		var len=domArr.length;
		for (i=0;i<len;i++) {
			if (domArr[i].search(atomPat)==-1) {
				extAlert(errorTitle, urlDomainNameInvalidErrMes, Ext.MessageBox.ERROR);					
				return false;
			}
		}
		return true;
	}