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

if(dashboardIsenabled){
	personalMenu[index++] =	['', dashboard,  JSPRootURL + '/personal/Main.jsp?module='+personalModule, '', '']
	personalMenu[index++] = _cmSplit;
}

if(calendarIsenabled == '1'){
	personalMenu[index++] = ['', calendar,  JSPRootURL + '/calendar/Main.jsp?module='+calendarModule, '', ''];
	personalMenu[index++] = _cmSplit;
}

if(assignmentsIsenabled == '1'){
	personalMenu[index++] = ['', assignment,  JSPRootURL + '/servlet/AssignmentController/PersonalAssignments?module='+personalModule, '', '',
		['', 'Assignments (Beta)',  JSPRootURL + '/assignments/My?module='+personalModule, '', '']
	];
	personalMenu[index++] = _cmSplit;
}

if(blogIsenabled == '1'){
	personalMenu[index++] = ['', 'My Blog',  JSPRootURL + '/blog/view/'+userCurrentSpaceId+'/'+userId+'/person/'+personalModule+'?module='+personalModule, '', ''];
	personalMenu[index++] = _cmSplit;
}

if(documentIsenabled == '1'){
	personalMenu[index++] = ['', documentTitle,  JSPRootURL + '/document/Main.jsp?module='+documentModule, '', ''];
	personalMenu[index++] = _cmSplit;
}    	    
    	    
if(formIsenabled == '1'){
	personalMenu[index++] = ['', form,  JSPRootURL + '/form/Main.jsp?module='+formModule, '', '',
							['', 'Cool Assignable form', 
								 JSPRootURL + '/form/FormQuickAccess.jsp?module='+formModule+'&id='+userCurrentSpaceId, '', '']
							];
	personalMenu[index++] = _cmSplit;
}      
  
if(methodologyIsenabled == '1'){
	personalMenu[index++] = ['', template,  JSPRootURL + '/methodology/MethodologyList.htm', '', ''];
	personalMenu[index++] = _cmSplit;
}

if(setupIsenabled == '1'){
	personalMenu[index++] = ['', setup,  JSPRootURL + '/personal/Setup.jsp?module='+personalModule, '', ''];
	personalMenu[index++] = _cmSplit;
}

if (masterPropertiesExist && isLicenseRequiredAtLogin) {
	personalMenu[index++] = ['', licensing,  JSPRootURL + '/personal/license/LicenseManager.jsp?module='+personalModule, '', ''];
	personalMenu[index++] = _cmSplit;
}

if(displayApplicationSpace == '1'){
	personalMenu[index++] = ['', applicationspace,  JSPRootURL + '/admin/Main.jsp?module='+applicationSpaceModule, '', ''];
	personalMenu[index++] = _cmSplit;
}

if(displayConfigurationSpace == '1'){
	personalMenu[index++] = ['', configurations,  JSPRootURL + '/portfolio/ConfigurationPortfolio.jsp?module='+configurationSpaceModule, '', ''];
	personalMenu[index++] = _cmSplit;
}