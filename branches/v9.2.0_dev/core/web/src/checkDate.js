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
// Primary Date Checking Object
// Usage:
// var dateFormat = new DateFormat ("M/d/yyyy");
// boolean valid - dateFormat.checkValidDate (theDate);
function DateFormat (formatString) {

   this.checkValidDate = checkValidDate;
   this.checkDate = checkDate;
   this.compareDates = compareDates;
   this.checkDatesEqual = checkDatesEqual;
      
   this.formatString = formatString;
}

function checkDate (field, msg) {
   return verifyDate (this.formatString, field, false, msg);
}

function checkValidDate (field) {
   return verifyDate (this.formatString, field, true);
}

function compareDates(startField, endField, msg) {
	var startDate = startField.value;
	var endDate = endField.value;

    var err = new ErrorObj(true);
	err.msg = msg;
	
	if (startDate == null || startDate == "" || 
		endDate == null || endDate == "") {
		return true;
	}
	var startTime = getDateFromFormat(startDate,this.formatString);
	var endTime = getDateFromFormat(endDate,this.formatString);
	if(startTime == 0 || endTime == 0) {
		err.isOk = false;
	} else if (endTime > startTime) {
		err.isOk = true;
	} else {
		err.isOk = false;
	} 

	if (!err.isOk) {	
		return errorHandler(startField, msg);	
	} else {
		return true;
	}
}

function checkDatesEqual(dateField1, dateField2, msg) {
	var startDate = dateField1.value;
	var endDate = dateField2.value;

	var err = new ErrorObj(true);
	err.msg = msg;
	
	if ((startDate == null || startDate == "") && (endDate == null || endDate == "")) {
		return true;
	}
	
	var startTime = getDateFromFormat(startDate,this.formatString);
	var endTime = getDateFromFormat(endDate,this.formatString);
	if(startTime == 0 || endTime == 0) {
		err.isOk = false;
	} else if (endTime == startTime) {
		err.isOk = true;
	} else {
		err.isOk = false;
	} 

	if (!err.isOk) {	
		return errorHandler(dateField2, msg);	
	} else {
		return true;
	}
	
}

function verifyDate (format, field, nullsAllowed, errMsg) {

	var val = field.value;
    var err = new ErrorObj(true);

	if (nullsAllowed) {
      if (field.value == "")
         return true;
	}

	if (format == null || format == '') {
      err.msg = format + " is not a supported format.";
      err.isOk = false;
      return errorHandler(field,err.msg);
	}

	var date=getDateFromFormat(val,format);
	if (date==0) {
		err.isOk = false;
		err.msg=errMsg;
	} else {
		err.isOk = true;
	}

	if (!err.isOk) 	{
		if(errMsg)
			return errorHandler(field,err.msg);
		else
			return false;
	}
	else return true;
}

function _isInteger(val) {
	var digits="1234567890";
	for (var i=0; i < val.length; i++) {
		if (digits.indexOf(val.charAt(i))==-1) { return false; }
	}
	return true;
}

function _getInt(str,i,minlength,maxlength) {
	for (var x=maxlength; x>=minlength; x--) {
		var token=str.substring(i,i+x);
		if (token.length < minlength) { return null; }
		if (_isInteger(token)) { return token; }
	}
	return null;
}
	
function getDateFromFormat(val,format) {
	val=val+"";
	format=format+"";
	var i_val=0;
	var i_format=0;
	var c="";
	var token="";
	var token2="";
	var x,y;
	var now=new Date();
	var year=now.getYear();
	var month=now.getMonth()+1;
	var date=1;
	var hh=now.getHours();
	var mm=now.getMinutes();
	var ss=now.getSeconds();
	var ampm="";
	
	while (i_format < format.length) {
		// Get next token from format string
		c=format.charAt(i_format);
		token="";
		while ((format.charAt(i_format)==c) && (i_format < format.length)) {
			token += format.charAt(i_format++);
		}
		// Extract contents of value based on format token
		if (token=="yyyy" || token=="yy" || token=="y") {
			if (token=="yyyy") { x=4;y=4; }
			if (token=="yy")   { x=2;y=2; }
			if (token=="y")    { x=2;y=4; }
			year=_getInt(val,i_val,x,y);
			if (year==null) { return 0; }
			i_val += year.length;
			if (year.length==2) {
				if (year > 70) { year=1900+(year-0); }
				else { year=2000+(year-0); }
			}
		}
		else if (token=="MMM"||token=="NNN"){
			month=0;
			for (var i=0; i<MONTH_NAMES.length; i++) {
				var month_name=MONTH_NAMES[i];
				if (val.substring(i_val,i_val+month_name.length).toLowerCase()==month_name.toLowerCase()) {
					if (token=="MMM"||(token=="NNN"&&i>11)) {
						month=i+1;
						if (month>12) { month -= 12; }
						i_val += month_name.length;
						break;
					}
				}
			}
			if ((month < 1)||(month>12)){return 0;}
		}
		else if (token=="EE"||token=="E"){
			for (var i=0; i<DAY_NAMES.length; i++) {
				var day_name=DAY_NAMES[i];
				if (val.substring(i_val,i_val+day_name.length).toLowerCase()==day_name.toLowerCase()) {
					i_val += day_name.length;
					break;
				}
			}
		}
		else if (token=="MM"||token=="M") {
			month=_getInt(val,i_val,token.length,2);
			if(month==null||(month<1)||(month>12)){return 0;}
			i_val+=month.length;
		}
		else if (token=="dd"||token=="d") {
			date=_getInt(val,i_val,token.length,2);
			if(date==null||(date<1)||(date>31)){return 0;}
			i_val+=date.length;
		}
		else if (token=="hh"||token=="h") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<1)||(hh>12)){return 0;}
			i_val+=hh.length;
		}
		else if (token=="HH"||token=="H") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<0)||(hh>23)){return 0;}
			i_val+=hh.length;
		}
		else if (token=="KK"||token=="K") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<0)||(hh>11)){return 0;}
			i_val+=hh.length;
		}
		else if (token=="kk"||token=="k") {
			hh=_getInt(val,i_val,token.length,2);
			if(hh==null||(hh<1)||(hh>24)){return 0;}
			i_val+=hh.length;hh--;
		}
		else if (token=="mm"||token=="m") {
			mm=_getInt(val,i_val,token.length,2);
			if(mm==null||(mm<0)||(mm>59)){return 0;}
			i_val+=mm.length;
		}
		else if (token=="ss"||token=="s") {
			ss=_getInt(val,i_val,token.length,2);
			if(ss==null||(ss<0)||(ss>59)){return 0;}
			i_val+=ss.length;
		}
		else if (token=="a") {
			if (val.substring(i_val,i_val+2).toLowerCase()=="am") {ampm="AM";}
			else if (val.substring(i_val,i_val+2).toLowerCase()=="pm") {ampm="PM";}
			else {return 0;}
			i_val+=2;
		}
		else {
			if (val.substring(i_val,i_val+token.length)!=token) {return 0;}
			else {i_val+=token.length;}
		}
	}
	// If there are any trailing characters left in the value, it doesn not match
	if (i_val != val.length) { return 0; }
	// Is date valid for month
	if (month==2) {
		// Check for leap year
		if ( ( (year%4==0)&&(year%100 != 0) ) || (year%400==0) ) { // leap year
			if (date > 29){ return 0; }
			}
		else { if (date > 28) { return 0; } }
	}
	if ((month==4)||(month==6)||(month==9)||(month==11)) {
		if (date > 30) { return 0; }
	}
	// Correct hours value
	if (hh<12 && ampm=="PM") { hh=hh-0+12; }
	else if (hh>11 && ampm=="AM") { hh-=12; }
	var newdate=new Date(year,month-1,date,hh,mm,ss);
	return newdate.getTime();
}

// check for valid date format
	function checkDate(date, pattern) {
		if (date != null && pattern != null) {
			var pattern = getJSUserDatePattern(pattern);
			dateString = date.replace(new RegExp("[0-9]","g"), "");
			patternString = (pattern.toLowerCase()).replace(new RegExp("[a-z]","g"), "");
			if(dateString != patternString) {
				return false;
			} else {
				var seperator = patternString.charAt(0);
				var dateArray = date.split(seperator);
				if(pattern == 'd'+seperator+'m'+seperator+'y' || pattern == 'd'+seperator+'m'+seperator+'y'+seperator || pattern == 'j'+seperator+'n'+seperator+'y' || pattern == 'j'+seperator+'m'+seperator+'y' || pattern == 'd'+seperator+'m'+seperator+'Y') {
					var arrayVal = dateArray[1];
					dateArray[1] = dateArray[0];
					dateArray[0] =  arrayVal;
				} else if (pattern == 'y'+seperator+'n'+seperator+'j' || pattern == 'y'+seperator+'m'+seperator+'d' || pattern == 'y'+seperator+'m'+seperator+'j' || pattern == 'Y'+seperator+'m'+seperator+'d' || pattern == 'Y'+seperator+'m'+seperator+'d'+seperator) {
					var tmpVal1 = dateArray[2];
					var tmpVal2 = dateArray[0];
					dateArray[0] = dateArray[1];
					dateArray[1] = tmpVal1;
					dateArray[2] = tmpVal2;
				} else if (pattern == 'y'+seperator+'j'+seperator+'n' || pattern == 'y'+seperator+'d'+seperator+'m' || pattern == 'y'+seperator+'j'+seperator+'m' || pattern == 'Y'+seperator+'d'+seperator+'m') {
					var tmpVal1 = dateArray[2];
					var tmpVal2 = dateArray[0];
					dateArray[0] = tmpVal1; 
					dateArray[2] = tmpVal2;
				}
				if(isNaN(dateArray[0]) || isNaN(dateArray[1]) || isNaN(dateArray[2])) {
					return false;
				} else if ((pattern.indexOf("Y") >= 0) && dateArray[2].length != 4) {
					return false;
				} else if ((pattern.indexOf("y") >= 0) && dateArray[2].length != 2) {
					return false;
				} else if (dateArray[0].length > 2 || dateArray[0] < 1 || dateArray[0] > 12) {
					return false;
				} else if (dateArray[1].length > 2 || dateArray[1] < 1 || dateArray[1] > 31) {
			 		return false;
				}
			}
			var theDate = Date.parseDate(date, pattern);
			var d1 = new String(theDate.getDate());
			var zeros = new RegExp("0");
			var tmp = new String(dateArray[1])
			tmp = tmp.replace(zeros, "");
			var d2 = new RegExp(tmp);
			date = date.replace(" ", "");
			d1 = d1.replace(zeros, "");
			return (d1.search(d2) != -1);
		} else {
			return false;
		}
}
 
// To check date range.
function isdateStartBeforeEnd(startDate,endDate){
	if(startDate != '' && endDate != ''){
		startDate = Date.parseDate(startDate, getJSUserDatePattern(userDateFormatString));
		endDate = Date.parseDate(endDate, getJSUserDatePattern(userDateFormatString));
		if(startDate.getFullYear() > endDate.getFullYear()){
			return true;
		}
		if( startDate.getFullYear() == endDate.getFullYear() && startDate.getMonth() > endDate.getMonth()){
			return true;
		}
		if(startDate.getFullYear() == endDate.getFullYear() && startDate.getMonth() == endDate.getMonth() && startDate.getDate() > endDate.getDate()){
			return true;
		}
		return false;
	}else{
		return false;
	}
}