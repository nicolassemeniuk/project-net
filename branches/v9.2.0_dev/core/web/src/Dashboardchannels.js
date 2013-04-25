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
	//Context of Channel
	var PROJECT_SPACE_PROJECT_NEWS = "ProjectSpace_News_";
	var PROJECT_SPACE_PROJECT_CHANGES = "ProjectSpace_LastChanges_";
	var PROJECT_SPACE_MEETINGS = "ProjectSpace_Meetings_";
	var PROJECT_SPACE_PHASES = "ProjectSpace_phases_";
	var PROJECT_SPACE_MILESTONES = "ProjectSpace_Milestones_";
	var PROJECT_SPACE_SUBPROJECTS = "ProjectSpace_subprojects_";
	var PROJECT_SPACE_TEAMMATES = "ProjectSpace_TeamMembers_";
	var PROJECT_SPACE_MATERIALS = "ProjectSpace_Materials_";	
	var PROJECT_SPACE_PIE_CHART = "ProjectSpace_PieChart_";
	var PROJECT_SPACE_PROJECT_COMPLETION = "ProjectSpace_ProjectCompletion_";
	
	// checking project news state
	if( projectNewsState ){
		minimize('NewsContent','NewsImg','NewsLink',PROJECT_SPACE_PROJECT_NEWS);	
	}else{
		maximize('NewsContent','NewsImg','NewsLink',PROJECT_SPACE_PROJECT_NEWS);
	}
	
	// checking Project Last Changes state
	if( projectLastChangesState ){
		minimize('ChangesContent','ChangesImg','ChangesLink',PROJECT_SPACE_PROJECT_CHANGES);		
	}else{	
		maximize('ChangesContent','ChangesImg','ChangesLink',PROJECT_SPACE_PROJECT_CHANGES);
	}
	
	// checking Meetingt Changes state
	if( meetingsState ){
		minimize('MeetingContent','MeetingsImg','MeetingsLink',PROJECT_SPACE_MEETINGS);		
	}else{
		maximize('MeetingContent','MeetingsImg','MeetingsLink',PROJECT_SPACE_MEETINGS);
	}
	
	//checking Phases state
	if( phasesState ){
		minimize('PhasesContent','PhasesImg','PhasesLink',PROJECT_SPACE_PHASES);
	}else{
		maximize('PhasesContent','PhasesImg','PhasesLink',PROJECT_SPACE_PHASES);
	}
	
	//checking subproject state
	if( subprojectsState ){
		minimize('SubprojectContent','SubprojectImg','SubprojectLink',PROJECT_SPACE_SUBPROJECTS);
	}else{
		maximize('SubprojectContent','SubprojectImg','SubprojectLink',PROJECT_SPACE_SUBPROJECTS);
	}
	
	//checking Teammate state
		
	if( teammatesState ){
		minimize('TeammateContent','TeammateImg','TeammateLink',PROJECT_SPACE_TEAMMATES);
	}else{
		maximize('TeammateContent','TeammateImg','TeammateLink',PROJECT_SPACE_TEAMMATES);
	}
	
	//checking Materials state
	
	if( materialsState ){
		minimize('MaterialsContent','MaterialsImg','MaterialsLink',PROJECT_SPACE_MATERIALS);
	}else{
		maximize('MaterialsContent','MaterialsImg','MaterialsLink',PROJECT_SPACE_MATERIALS);
	}	
	
	//checking PieChart state
	
	if( projectPieChartState ){
		minimize('PieChartContent','PieChartImg','PieChartLink',PROJECT_SPACE_PIE_CHART);
	}else{
		maximize('PieChartContent','PieChartImg','PieChartLink',PROJECT_SPACE_PIE_CHART);
	}
	
	if( projectCompletionState ){
		minimize('ProjectCompletionContent','ProjectCompletionImg','ProjectCompletionLink',PROJECT_SPACE_PROJECT_COMPLETION);
	}else{
		maximize('ProjectCompletionContent','ProjectCompletionImg','ProjectCompletionLink',PROJECT_SPACE_PROJECT_COMPLETION);
	}
	
	//Hide Content of Channels
	function hideContent(context,value){
		if( context == PROJECT_SPACE_PROJECT_NEWS ){
			if( value == 1 ){
			  minimize('NewsContent','NewsImg','NewsLink',PROJECT_SPACE_PROJECT_NEWS);
			}else if( value == 0 ) {
			  maximize('NewsContent','NewsImg','NewsLink',PROJECT_SPACE_PROJECT_NEWS);
			}else if( value == 2 ){
			  closeWidget('NewsWidget','NewsCloseImg','NewsCloseLink',PROJECT_SPACE_PROJECT_NEWS);
			}
		}else if ( context == PROJECT_SPACE_PROJECT_CHANGES ){
			if( value == 1 ){
			  minimize('ChangesContent','ChangesImg','ChangesLink',PROJECT_SPACE_PROJECT_CHANGES);
			}else if( value == 0){
			  maximize('ChangesContent','ChangesImg','ChangesLink',PROJECT_SPACE_PROJECT_CHANGES);
			}else if( value == 2 ){
			  closeWidget('LastChangesWidget','LastChangesCloseImg','LastChangesCloseLink',PROJECT_SPACE_PROJECT_CHANGES);
			}
		}else if( context==PROJECT_SPACE_MEETINGS ){
			if( value == 1 ){
			  minimize('MeetingContent','MeetingsImg','MeetingsLink',PROJECT_SPACE_MEETINGS);
			}else if( value == 0 ) {
			  maximize('MeetingContent','MeetingsImg','MeetingsLink',PROJECT_SPACE_MEETINGS);
			}else if( value == 2 ){
			  closeWidget('MeetingWidget','MeetingCloseImg','MeetingCloseLink',PROJECT_SPACE_MEETINGS);
			}
		}else if( context==PROJECT_SPACE_PHASES ){
			if( value == 1 ){
 			  minimize('PhasesContent','PhasesImg','PhasesLink',PROJECT_SPACE_PHASES);
			}else if( value == 0 ) {
  			  maximize('PhasesContent','PhasesImg','PhasesLink',PROJECT_SPACE_PHASES);			  
  			}else if( value == 2 ) {
			  closeWidget('PhaseWidget','PhaseCloseImg','PhaseCloseLink',PROJECT_SPACE_PHASES);
  			}
		}else if( context==PROJECT_SPACE_SUBPROJECTS ){
			if( value == 1 ){
 			  minimize('SubprojectContent','SubprojectImg','SubprojectLink',PROJECT_SPACE_SUBPROJECTS);
			}else if( value == 0){
  			  maximize('SubprojectContent','SubprojectImg','SubprojectLink',PROJECT_SPACE_SUBPROJECTS);			  
  			}else if( value == 2){
  			  closeWidget('SubProjectWidget','SubProjectCloseImg','SubProjectCloseLink',PROJECT_SPACE_SUBPROJECTS);
  			}
		}
		else if( context==PROJECT_SPACE_TEAMMATES ){
			balloon.hideTooltip(1);
			if( value == 1 ){
 			  minimize('TeammateContent','TeammateImg','TeammateLink',PROJECT_SPACE_TEAMMATES);
			}else if( value == 0 ){
  			  maximize('TeammateContent','TeammateImg','TeammateLink',PROJECT_SPACE_TEAMMATES);
  			}else if( value == 2 ) {
			  closeWidget('TeammateWidget','TeammateCloseImg','TeammateCloseLink',PROJECT_SPACE_TEAMMATES);
  			}
		}
		else if( context==PROJECT_SPACE_MATERIALS ){
			balloon.hideTooltip(1);
			if( value == 1 ){
 			  minimize('MaterialsContent','MaterialsImg','MaterialsLink',PROJECT_SPACE_MATERIALS);
			}else if( value == 0 ){
  			  maximize('MaterialsContent','MaterialsImg','MaterialsLink',PROJECT_SPACE_MATERIALS);
  			}else if( value == 2 ) {
 			  closeWidget('MaterialsWidget','MaterialsCloseImg','MaterialsCloseLink',PROJECT_SPACE_MATERIALS);
  			}
		}		
		else if( context==PROJECT_SPACE_PIE_CHART ){
			if( value == 1 ){
				minimize('PieChartContent','PieChartImg','PieChartLink',PROJECT_SPACE_PIE_CHART);
			}else if( value == 0){
				maximize('PieChartContent','PieChartImg','PieChartLink',PROJECT_SPACE_PIE_CHART);
			}else if( value == 2){
				closeWidget('PieChartWidget','PieChartCloseImg','PieChartCloseLink',PROJECT_SPACE_PIE_CHART);
			}
		}
		else if( context==PROJECT_SPACE_PROJECT_COMPLETION ){
			if( value == 1 ){
				minimize('ProjectCompletionContent','ProjectCompletionImg','ProjectCompletionLink',PROJECT_SPACE_PROJECT_COMPLETION);
			}else if( value == 0){
				maximize('ProjectCompletionContent','ProjectCompletionImg','ProjectCompletionLink',PROJECT_SPACE_PROJECT_COMPLETION);
			}else if( value == 2){
				closeWidget('ProjectCompletionWidget','ProjectCompletionCloseImg','ProjectCompletionCloseLink',PROJECT_SPACE_PROJECT_COMPLETION);
			}
		}
	}
	
	// Minimize div with changing image source, content display and title.
	function minimize(divId,imgId,linkId,context){
		document.getElementById(divId).style.display='none';
	    document.getElementById(imgId).src=JSPRootURL+'/images/project/dashboard_arrow-down.gif';
	    document.getElementById(linkId).href= "javascript:saveState('"+context+"','0');";
  	    document.getElementById(imgId).title = downTitle;
	}
	
	// Maximize div with changing image source, content display and title.
	function maximize(divId,imgId,linkId,context){
		if(document.getElementById(divId) != null) {
			document.getElementById(divId).style.display='block';
		}
		if( document.getElementById(imgId) != null){
	 	   document.getElementById(imgId).src=JSPRootURL+'/images/project/dashboard_arrow-up.gif';	    
	 	}
	 	if(document.getElementById(linkId) != null){
		    document.getElementById(linkId).href= "javascript:saveState('"+context+"','1');";	    
		}
		if( document.getElementById(imgId) != null){
   	    	document.getElementById(imgId).title = upTitle;
		}   	   
	}
	
	// Closing div with changing image source, content display and title
	function closeWidget(divId,imgId,linkId,context){		
		document.getElementById(divId).style.display='none';
	    document.getElementById(imgId).src=JSPRootURL+'/images/project/dashboard_close.gif' 	    
	    document.getElementById(linkId).href= "javascript:saveState('"+context+"','2');";
   	    document.getElementById(imgId).title = closeTitle;
	}
	
	// Show div with changing image source, content display and title
	function showWidget(divId,imgId,linkId,context){
		document.getElementById(divId).style.display='block';
	    document.getElementById(imgId).src=JSPRootURL+'/images/project/dashboard_close.gif' 	    
	    document.getElementById(linkId).href= "javascript:saveState('"+context+"','2');";
	    document.getElementById(imgId).title = closeTitle;
	}
	
	// Ajax request for minimize and miximize functionality
	function saveState(context, value){
		
		hideContent(context,value);
		
		Ext.Ajax.request({
			   url: JSPRootURL+'/project/Dashboard/saveContextChange',
			   params: {module: 150, context: context, value: value},
			   method: 'POST',
			   scope: this,
			   success: function(result, text){
			   	var	resText =result.responseText;
			   },
			   failure: function(result, response){
				   extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
			   }
		});
	}
