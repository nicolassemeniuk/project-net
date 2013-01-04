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

projectMenu[index++] =	['', 'Dashboard',  JSPRootURL + '/project/Main.jsp?module='+projectModule, '', '',
				    	['', 'Dashboard (Beta)',  JSPRootURL + '/project/Main2.jsp?module='+projectModule, '', '']
				    ];
projectMenu[index++] = _cmSplit;

if(contactIsenabled != '1' && directoryIsenabled == '1'){
	projectMenu[index++] = ['', directory,  JSPRootURL + '/project/DirectorySetup.jsp?module='+directoryModule, '', ''];
	projectMenu[index++] = _cmSplit;
}

projectMenu[index++] = ['', 'Blog',  JSPRootURL + '/blog/view/'+userCurrentSpaceId+'/'+userId+'/project/'+projectModule+'?module='+projectModule, '', ''];
projectMenu[index++] = _cmSplit;

if(wikiIsenabled == '1'){
	projectMenu[index++] = ['', wiki,  JSPRootURL + '/wiki/Welcome/'+userCurrentSpaceId+'/'+userId+'?module='+projectModule, '', ''];
    projectMenu[index++] = _cmSplit;
}    

if(documentIsenabled == '1'){
	projectMenu[index++] = ['', documentTitle,  JSPRootURL + '/document/Main.jsp?module='+documentModule, '', ''];
	projectMenu[index++] = _cmSplit;
}    	    

if(discussionIsenabled == '1'){
	projectMenu[index++] = ['', discussion,  JSPRootURL + '/discussion/Main.jsp?module='+discussionModule, '', ''];
	projectMenu[index++] = _cmSplit;
}	    	    

if(discussionIsenabled == '1'){
	projectMenu[index++] = ['', form,  JSPRootURL + '/form/Main.jsp?module='+formModule, '', '',
							['', 'Cool Assignable form', 
								 JSPRootURL + '/form/FormQuickAccess.jsp?module='+formModule+'&amp;id='+userCurrentSpaceId, '', '']
							];
	projectMenu[index++] = _cmSplit;
}        

if(processIsenabled == '1'){
	projectMenu[index++] = ['', process,  JSPRootURL + '/process/Main.jsp?module='+processModule, '', ''];
	projectMenu[index++] = _cmSplit;
}

if(schedulingIsenabled == '1'){
	projectMenu[index++] = ['', scheduling, '', '', '',
								['', calendar,  JSPRootURL + '/calendar/Main.jsp?module='+calendarModule, '', ''],
								['', schedule,  JSPRootURL + '/schedule/Main.jsp?module='+scheduleModule, '', '']
							];
	projectMenu[index++] = _cmSplit;
}    

if(workflowIsenabled == '1'){
	projectMenu[index++] = ['', workflow,  JSPRootURL + '/workflow/Main.jsp?module='+workflowModule, '', ''];
	projectMenu[index++] = _cmSplit;
}

if(newsIsenabled == '1'){
	projectMenu[index++] = ['', news,  JSPRootURL + '/news/Main.jsp?module='+newsModule, '', ''];
	projectMenu[index++] = _cmSplit;
}

if(subprojectIsenabled == '1'){
	projectMenu[index++] = ['', subproject,  JSPRootURL + '/project/subproject/Main.jsp?module='+projectModule, '', ''];
	projectMenu[index++] = _cmSplit;
}

if(reportsIsenabled == '1'){
	projectMenu[index++] = ['', reports,  JSPRootURL + '/report/Main.jsp?module='+reportModule, '', ''];
	projectMenu[index++] = _cmSplit;
}

if(setupIsenabled == '1'){
	projectMenu[index++] = ['', setup,  JSPRootURL + '/project/Setup.jsp?module='+projectModule, '', ''];
	projectMenu[index++] = _cmSplit;
}