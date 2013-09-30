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
	var FINANCIAL_SPACE_PROJECTS = "FinancialSpace_Projects_";
	var FINANCIAL_SPACE_PROJECT_TOTAL_COSTS_CHART = "FinancialSpace_ProjectTotalCostsChart_";
	var FINANCIAL_SPACE_FINANCIAL_TEAM = "FinancialSpace_FinancialTeam_";
	var FINANCIAL_SPACE_ACTUAL_COSTS_TYPES_OVER_TOTAL_CHART = "FinancialSpace_ActualCostsTypesOverTotalChart_";
	
	// checking Projects state
	if( projectsState ){
		minimize('ProjectsContent','ProjectsImg','ProjectsLink',FINANCIAL_SPACE_PROJECTS);
	}else{
		maximize('ProjectsContent','ProjectsImg','ProjectsLink',FINANCIAL_SPACE_PROJECTS);
	}
	
	// checking Project Total Cost Chart state
	if( projectTotalCostsChartState ){
		minimize('ProjectTotalCostsChartContent','ProjectTotalCostsChartImg','ProjectTotalCostsChartLink',FINANCIAL_SPACE_PROJECT_TOTAL_COSTS_CHART);
	}else{
		maximize('ProjectTotalCostsChartContent','ProjectTotalCostsChartImg','ProjectTotalCostsChartLink',FINANCIAL_SPACE_PROJECT_TOTAL_COSTS_CHART);
	}
	
	// checking Financial Team state
	if( financialTeamState ){
		minimize('FinancialTeamContent','FinancialTeamImg','FinancialTeamLink',FINANCIAL_SPACE_FINANCIAL_TEAM);
	}else{
		maximize('FinancialTeamContent','FinancialTeamImg','FinancialTeamLink',FINANCIAL_SPACE_FINANCIAL_TEAM);
	}
	
	// checking Actual Costs Types Over TotalChart state
	if( actualCostsTypesOverTotalChartState ){
		minimize('ActualCostsTypesOverTotalChartContent','ActualCostsTypesOverTotalChartImg','ActualCostsTypesOverTotalChartLink',FINANCIAL_SPACE_ACTUAL_COSTS_TYPES_OVER_TOTAL_CHART);
	}else{
		maximize('ActualCostsTypesOverTotalChartContent','ActualCostsTypesOverTotalChartImg','ActualCostsTypesOverTotalChartLink',FINANCIAL_SPACE_ACTUAL_COSTS_TYPES_OVER_TOTAL_CHART);
	}	
	
	//Hide Content of Channels
	function hideContent(context,value){
		if( context==FINANCIAL_SPACE_PROJECTS ){
			if( value == 1 ){
				minimize('ProjectsContent','ProjectsImg','ProjectsLink',FINANCIAL_SPACE_PROJECTS);
			}else if( value == 0){
				maximize('ProjectsContent','ProjectsImg','ProjectsLink',FINANCIAL_SPACE_PROJECTS);
			}else if( value == 2){
				closeWidget('ProjectsWidget','ProjectsImg','ProjectsCloseLink',FINANCIAL_SPACE_PROJECTS);
			}
		}
		else if( context==FINANCIAL_SPACE_PROJECT_TOTAL_COSTS_CHART ){
			if( value == 1 ){
				minimize('ProjectTotalCostsChartContent','ProjectTotalCostsChartImg','ProjectTotalCostsChartLink',FINANCIAL_SPACE_PROJECT_TOTAL_COSTS_CHART);
			}else if( value == 0){
				maximize('ProjectTotalCostsChartContent','ProjectTotalCostsChartImg','ProjectTotalCostsChartLink',FINANCIAL_SPACE_PROJECT_TOTAL_COSTS_CHART);
			}else if( value == 2){
				closeWidget('ProjectTotalCostsChartWidget','ProjectTotalCostsChartImg','ProjectTotalCostsChartCloseLink',FINANCIAL_SPACE_PROJECT_TOTAL_COSTS_CHART);
			}
		}
		else if( context==FINANCIAL_SPACE_FINANCIAL_TEAM ){
			if( value == 1 ){
				minimize('FinancialTeamContent','FinancialTeamImg','FinancialTeamLink',FINANCIAL_SPACE_FINANCIAL_TEAM);
			}else if( value == 0){
				maximize('FinancialTeamContent','FinancialTeamImg','FinancialTeamLink',FINANCIAL_SPACE_FINANCIAL_TEAM);
			}else if( value == 2){
				closeWidget('FinancialTeamWidget','FinancialTeamImg','FinancialTeamCloseLink',FINANCIAL_SPACE_FINANCIAL_TEAM);
			}
		}
		else if( context==FINANCIAL_SPACE_ACTUAL_COSTS_TYPES_OVER_TOTAL_CHART ){
			if( value == 1 ){
				minimize('ActualCostsTypesOverTotalChartContent','ActualCostsTypesOverTotalChartImg','ActualCostsTypesOverTotalChartLink',FINANCIAL_SPACE_ACTUAL_COSTS_TYPES_OVER_TOTAL_CHART);
			}else if( value == 0){
				maximize('ActualCostsTypesOverTotalChartContent','ActualCostsTypesOverTotalChartImg','ActualCostsTypesOverTotalChartLink',FINANCIAL_SPACE_ACTUAL_COSTS_TYPES_OVER_TOTAL_CHART);
			}else if( value == 2){
				closeWidget('ActualCostsTypesOverTotalChartWidget','ActualCostsTypesOverTotalChartImg','ActualCostsTypesOverTotalChartCloseLink',FINANCIAL_SPACE_ACTUAL_COSTS_TYPES_OVER_TOTAL_CHART);
			}
		}			
	}
	
	// Minimize div with changing image source, content display and title.
	function minimize(divId,imgId,linkId,context){
		document.getElementById(divId).style.display='none';
	    document.getElementById(imgId).src=JSPRootURL+'/images/financial/dashboard_arrow-down.gif';
	    document.getElementById(linkId).href= "javascript:saveState('"+context+"','0');";
  	    document.getElementById(imgId).title = downTitle;
	}
	
	// Maximize div with changing image source, content display and title.
	function maximize(divId,imgId,linkId,context){
		if(document.getElementById(divId) != null) {
			document.getElementById(divId).style.display='block';
		}
		if( document.getElementById(imgId) != null){
	 	   document.getElementById(imgId).src=JSPRootURL+'/images/financial/dashboard_arrow-up.gif';	    
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
	    document.getElementById(imgId).src=JSPRootURL+'/images/financial/dashboard_close.gif' 	    
	    document.getElementById(linkId).href= "javascript:saveState('"+context+"','2');";
   	    document.getElementById(imgId).title = closeTitle;
	}
	
	// Show div with changing image source, content display and title
	function showWidget(divId,imgId,linkId,context){
		document.getElementById(divId).style.display='block';
	    document.getElementById(imgId).src=JSPRootURL+'/images/financial/dashboard_close.gif' 	    
	    document.getElementById(linkId).href= "javascript:saveState('"+context+"','2');";
	    document.getElementById(imgId).title = closeTitle;
	}
	
	// Ajax request for minimize and miximize functionality
	function saveState(context, value){
		hideContent(context,value);
		
		Ext.Ajax.request({
			   url: JSPRootURL+'/financial/Dashboard/saveContextChange',
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
