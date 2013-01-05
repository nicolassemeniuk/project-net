	var offset = 0;
	var range = 30;
	var activityIds = '';
	var memberValue = '';	
	var isBlogEventSelected = false;
    var	commentFlag = false;
    var projectEvents = 'project-new,project-edited,project-member_added_to_space,project-member_deleted_from_space,project-overall_status_changed,';
    var theDate = ''
    
    var uncheckedImage = JSPRootURL + "/images/uncheckedbox.gif";
	var checkedImage = JSPRootURL + "/images/checkedbox.gif";
	var intermediateImage = JSPRootURL + "/images/tristate-checkbox.gif";
		
	// window onload
	window.onload = function(){ 
		clearAll("#");
		document.getElementById("leftSpaceName").style.marginTop = -12 + 'px';
		if(filterCriteriaValue == ''){
			document.getElementById('filterCriteria').value = '0,';
			selectDeselectCheckboxes("all", true);
		} else{
			if(jumpedDate != null && jumpedDate != ''){
				document.getElementById('jumpedDateValue').value = jumpedDate;
				document.getElementById('jumpedDateValue').style.color = 'black';
			}
			document.getElementById('filterCriteria').value = filterCriteriaValue;
			var values = filterCriteriaValue.substring(0,(filterCriteriaValue.length-1)).split(",");
			for(var index = 0; index < values.length; index++){
				selectCheckboxes(formattedObjectString(values[index]));
			}
		}
		Ext.BLANK_IMAGE_URL = JSPRootURL+'/images/default/s.gif';
		applyFilter('apply');
		showMemberDetails();
	}
	
	function help() {
        var helplocation=JSPRootURL + "/help/Help.jsp?page=Activity_Main";
        openwin_help(helplocation);
    }
	
	// to format object string
	function formattedObjectString(value) {
		if(value.indexOf('-') >=0) {
			var valueArray = value.split('-');
			valueArray[0] = valueArray[0].replace('blog_entry','blog').replace('document','documents').replace('form','forms').replace('forms_data_','').replace('task','workplan');
			if(valueArray[0] == 'documents')
				valueArray[1] = valueArray[1].indexOf('folder_deleted') < 0 ? valueArray[1].replace('new','imported').replace('edited','properties_edited').replace('deleted','removed') : valueArray[1];
			else if(valueArray[0] == 'workplan')
				valueArray[1] = valueArray[1].replace('new','new_task').replace('edited','task_properties_edited').replace('deleted','deleted_task');
			if(valueArray[1].indexOf('_') >= 0){
				var finalValue = '';
				var eventArray = valueArray[1].split('_');
				for(var index = 0; index < eventArray.length; index++)
					finalValue += initCap(eventArray[index])+" ";
				return initCap(valueArray[0])+"_"+finalValue.substring(0,finalValue.length-1);
			} else {
				return initCap(valueArray[0])+"_"+initCap(valueArray[1]);
			}
		} else {
			return initCap(value);
		}			
	}
	
	// to make first letter capital
	function initCap(value) {
		return value.substring(0,1).toUpperCase()+value.substring(1);
	}

	// to collapse the div
	function closeDiv(id){
		document.getElementById(id +'_obj').className = 'paddingLeftForFiltersColumn displayNone';
		document.getElementById(id +'_img').src = JSPRootURL+ "/images/activitylog/close.gif";
		document.getElementById(id +'_link').href = "javascript:openDiv('"+id+"');";
		var all_obj_Ypos = getElementAbsolutePos(document.getElementById('all_obj'));
		if(all_obj_Ypos.y >= 149){
			document.getElementById('filter_content_row').height = '100%';
			document.getElementById('filter_content').style.height =  '100%';
			document.getElementById('all_obj').style.height =  '100%';
			var filter_content_Ypos = getElementAbsolutePos(document.getElementById('filter_content'));
			var filter_content_row_Ypos = getElementAbsolutePos(document.getElementById('filter_content_row'));
			var activity_content_row_Ypos = getElementAbsolutePos(document.getElementById('activity_content_row'));
			var heightToSet = activity_content_row_Ypos.y - filter_content_row_Ypos.y;
			if(heightToSet >= 400){
				document.getElementById('filter_content_row').height = '400px';
				document.getElementById('filter_content').style.height = '400px';
				document.getElementById('filter_content').style.overflow = 'hidden';
				document.getElementById('filter_content').style.overflow = 'auto';
			}	
		}
	}
	// to expand the div
	function openDiv(id){
		document.getElementById(id +'_obj').className = 'paddingLeftForFiltersColumn';
		document.getElementById(id +'_img').src = JSPRootURL+ "/images/activitylog/open.gif";
		document.getElementById(id +'_link').href = "javascript:closeDiv('"+id+"');";
		var filter_content_Ypos = getElementAbsolutePos(document.getElementById('filter_content'));
		var filter_content_row_Ypos = getElementAbsolutePos(document.getElementById('filter_content_row'));
		var activity_content_row_Ypos = getElementAbsolutePos(document.getElementById('activity_content_row'));
		var heightToSet = activity_content_row_Ypos.y - filter_content_row_Ypos.y;
		if(heightToSet >= 400){
			document.getElementById('filter_content_row').height = '400px';
			document.getElementById('filter_content').style.height = '400px';
			document.getElementById('filter_content').style.overflow = 'hidden';
			document.getElementById('filter_content').style.overflow = 'auto';
		}
	}
	
	// to select check boxes for persisted criteria
	function selectCheckboxes(objectName) {
		if(document.getElementById(objectName+'_chk')) {
			document.getElementById(objectName+'_chk').checked = true;
		} else {
			// To remove deleted forms from filterCriteria
			filterValue = document.getElementById('filterCriteria').value;
			filterValue = objectName != filterValue.substring(0, filterValue.indexOf(',')) ? filterValue.replace('form_data_'+objectName.replace('_','-').toLowerCase()+',', '')
																: filterValue;
			document.getElementById('filterCriteria').value = filterValue;													
		}		
		isBlogEventSelected = isBlogEventPresent(document.getElementById('filterCriteria').value);
		checkAndSelectAllCheckboxesUnderCurrentObject(objectName.substring(0, objectName.indexOf('_')));
		checkAndSelectAllCheckboxesUnderCurrentObject('Forms');
	}
	
	// to select and deselect the check boxes on change of the parent check box
	function selectDeselectCheckboxes(objectName, checked){
		if(document.getElementById(objectName+'_obj') != null){
			var chkBoxes = document.getElementById(objectName+'_obj').getElementsByTagName('input');
			for(var chkIndex = 0; chkIndex < chkBoxes.length; chkIndex++){
				if(chkBoxes[chkIndex].type == 'checkbox' && chkBoxes[chkIndex].value != 'all'){
					if(((chkBoxes[chkIndex].id == 'Marked_chk' && document.getElementById('filterCriteria').value.indexOf("Marked,") < 0 && checked) 
						|| (chkBoxes[chkIndex].id == 'Blog_My Posts That Are Commented_chk' && document.getElementById('filterCriteria').value.indexOf("blog_entry-my_posts_that_are_commented,") < 0 && checked)
						|| (chkBoxes[chkIndex].id == 'Blog_Important_chk' && document.getElementById('filterCriteria').value.indexOf("blog_entry-important,") < 0 && checked) 
						)) {
						chkBoxes[chkIndex].checked = !checked;
					} else if(chkBoxes[chkIndex].id == 'Marked_chk' && document.getElementById('filterCriteria').value.indexOf("Marked,") >= 0 && !checked) {
						chkBoxes[chkIndex].checked = !checked;
					} else {
						chkBoxes[chkIndex].checked = checked;
					}
					if(chkBoxes[chkIndex].checked){
						if(chkBoxes[chkIndex].value != 'on') {
							if(document.getElementById('filterCriteria').value.indexOf(chkBoxes[chkIndex].value+',') < 0)
								document.getElementById('filterCriteria').value += chkBoxes[chkIndex].value+',';
						}
						if(document.getElementById(objectName+'_ImgId'))
							document.getElementById(objectName+'_ImgId').src = checkedImage;
					} else {
						if(chkBoxes[chkIndex].value != ''){
							document.getElementById('filterCriteria').value = document.getElementById('filterCriteria').value.replace(chkBoxes[chkIndex].value+',', '');
						}	
						if(document.getElementById(objectName+'_ImgId') && chkBoxes[chkIndex].id != 'Blog_My Posts That Are Commented_chk' && chkBoxes[chkIndex].id != 'Blog_Important_chk'){
							document.getElementById(objectName+'_ImgId').src = uncheckedImage;
						}
					}
				}
				var checkBoxValue = chkBoxes[chkIndex].value;
				if(checkBoxValue.indexOf('form_data') >= 0){
					checkBoxValue = checkBoxValue.substring(checkBoxValue.lastIndexOf('_')+1, checkBoxValue.indexOf('-'));
				}
				if(document.getElementById(checkBoxValue+'_ImgId')) {
					document.getElementById('filterCriteria').value = document.getElementById('filterCriteria').value.replace(checkBoxValue+',', '');
					if(!checked){
						document.getElementById(checkBoxValue+'_ImgId').src = uncheckedImage;
					} else {
						document.getElementById(checkBoxValue+'_ImgId').src = checkedImage;
					}
				}
				checkAndSelectAllCheckboxesUnderCurrentObject('all');
				checkAndSelectAllCheckboxesUnderCurrentObject('Forms');
			}
			if(objectName=='Blog'){
				document.getElementById('filterCriteria').value = document.getElementById('filterCriteria').value.replace('blog_entry-my_post_that_are_commented,blog_entry-important,', '');	
			}
		} else {
			checkAndSelectAllCheckboxesUnderCurrentObject(objectName != 'MARKED_ENTITIES' ? objectName.split('_')[0] : 'all');
			checkAndSelectAllCheckboxesUnderCurrentObject('Forms');
			if(checked){
				if(document.getElementById(objectName+'_chk') != null && document.getElementById('filterCriteria').value.indexOf(document.getElementById(objectName+'_chk').value+',') < 0)
					document.getElementById('filterCriteria').value += document.getElementById(objectName+'_chk').value+',';
			} else {
				document.getElementById('filterCriteria').value = document.getElementById('filterCriteria').value.replace(document.getElementById(objectName+'_chk').value+',', '');
			}
		}
		if(objectName == 'all' && checked) {
			if(document.getElementById('filterCriteria').value.indexOf(projectEvents) < 0)
				document.getElementById('filterCriteria').value += projectEvents;
		} else {
			if(document.getElementById('filterCriteria').value.indexOf(projectEvents) >= 0)
				document.getElementById('filterCriteria').value = document.getElementById('filterCriteria').value.replace(projectEvents, '');
		}		
		isBlogEventSelected = isBlogEventPresent(document.getElementById('filterCriteria').value);
	}
	// to select all checkboxes under current object if all checkboxes are selected
	function checkAndSelectAllCheckboxesUnderCurrentObject(objectName){
		var allSelected = true;
		var notAllSelected = true;
		if(document.getElementById(objectName+'_obj') != null){
			var chkBoxes = document.getElementById(objectName+'_obj').getElementsByTagName('input');
			for(var chkIndex = 0; chkIndex < chkBoxes.length; chkIndex++){
				if(chkBoxes[chkIndex].type == 'checkbox' && chkBoxes[chkIndex].value != 'all'){
					if(chkBoxes[chkIndex].id != 'Marked_chk' && chkBoxes[chkIndex].id != 'Blog_Important_chk' && chkBoxes[chkIndex].id != 'Blog_My Posts That Are Commented_chk'){
							allSelected = chkBoxes[chkIndex].checked;
					}
					if(!allSelected) break;
				}
			}
			for(var chkIndex = 0; chkIndex < chkBoxes.length; chkIndex++){
				if(chkBoxes[chkIndex].type == 'checkbox' && chkBoxes[chkIndex].value != 'all'){
					if(chkBoxes[chkIndex].id != 'Marked_chk' && chkBoxes[chkIndex].id != 'Blog_Important_chk' && chkBoxes[chkIndex].id != 'Blog_My Posts That Are Commented_chk'){
							notAllSelected = chkBoxes[chkIndex].checked;
							if(notAllSelected) break;
					}
				}
			}
			if(objectName != 'all') {
				if(objectName.indexOf(objectName + '_') < 0 ) {
					if(document.getElementById(objectName+'_ImgId')) {
						if(!allSelected) {
							document.getElementById(objectName+'_ImgId').src = intermediateImage;
						} else {
							document.getElementById(objectName+'_ImgId').src = checkedImage;
						}
						if(!notAllSelected) {
							document.getElementById(objectName+'_ImgId').src = uncheckedImage;
						}
					}
				}
			}
			if(document.getElementById(objectName+'_chk'))
				document.getElementById(objectName+'_chk').checked = allSelected;
		}	
	}
	
	// to check whether blog event present or not
	function isBlogEventPresent(criteria){
		return( criteria.indexOf("blog_entry-new,") >= 0
		       || criteria.indexOf("blog_entry-edited,") >= 0
		       || criteria.indexOf("blog_entry-deleted,") >= 0
		       || criteria.indexOf("blog_entry-commented,") >= 0);
	}
	
	// to collapse the filter pane
	function collapseFilters(collapse){
		if(collapse){
			document.getElementById('filters').style.display = 'none';
			document.getElementById('filterCollapsed').style.display = '';
			document.getElementById('leftPane').width = "97%";
			document.getElementById('rightPane').style.display = 'none';
			document.getElementById('collapsible').style.display = '';
			document.getElementById('filterContent').style.display = 'none';
			document.getElementById('collapseFilter').src = JSPRootURL+'/images/activitylog/next_inverse.gif';
		} else {
			document.getElementById('rightPane').style.display = '';
			document.getElementById('filterContent').style.display = '';
			document.getElementById('filters').style.display = '';
			document.getElementById('filterCollapsed').style.display = 'none';
			document.getElementById('leftPane').width = "66%";
			document.getElementById('rightPane').width = "33%";
			document.getElementById('expandFilter').src = JSPRootURL+'/images/activitylog/next.gif';
		}
	}
	// to apply the filter
	function submitFilterForm(){
		if(document.getElementById('filterCriteria').value == ''){
			extAlert(errorTitle, selectOneMessage, Ext.MessageBox.ERROR);
		}else {
			document.getElementById('apply').disabled = true;
			document.getElementById('filtervalues').value = document.getElementById('teamMember').value+','+document.getElementById('filterCriteria').value;
			document.getElementById('jumptodate').value = jumpToDate.getValue().dateFormat(getJSUserDatePattern(dateFormat));
			document.getElementById('filterForm').submit();
		}
	}
	
	//fired top select event and refresh the zone
	function submit(){
		document.getElementById('showLoadingImg').style.display = 'block';
		document.getElementById('showLoadingImg').innerHTML = loadingMessage+'<img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="absmiddle" />';
		fireEvent(document.getElementById('topSelect'), 'click');
	}
	
	//fired the change event of hidden text field for filtered data.
	function filter(){
		if(document.getElementById('filterCriteria').value == ''){
			extAlert(errorTitle, selectOneMessage, Ext.MessageBox.ERROR);
		}else {
			document.getElementById('apply').disabled = true;
			document.getElementById('filtervalues').value = document.getElementById('teamMember').value+','+document.getElementById('filterCriteria').value;
			fireEvent(document.getElementById('filtervalues'),'change');
		}
	}
	function showLoading(){
		document.getElementById('showLoadingImg').style.display = 'block';
		document.getElementById('showLoadingImg').innerHTML = loadingMessage+'<img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="absmiddle" />';
	}
	
	//To mark and unmark activity
	function marked(activityId, isMarked){
	    var documentIdText = '';
		if(document.getElementById("commentMarked_"+activityId)!= null){
			document.getElementById("commentMarked_"+activityId).innerHTML = '<img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="absmiddle" />';
			documentIdText = "commentMarked";
		} 
		document.getElementById("marked_"+activityId).innerHTML = '<img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="absmiddle" />';
		
		Ext.Ajax.request({
		   url: JSPRootURL +'/activity/activityLog/marked',
		   params: { activityId : activityId, isMarked : isMarked },
		   method: 'POST',
		   success: function(result, request){
		   	   if(!isMarked){
		   	   		setAsUnmarked("marked", activityId);
		   	   		if(documentIdText != ''){
		   	   			setAsUnmarked(documentIdText, activityId);
		   	   		}
		   	   		activityIds = activityIds.replace(activityId+',','');
		   	   } else {
		   	   		setAsMarked("marked", activityId);
		   	   		if(documentIdText != ''){
		   	   			setAsMarked(documentIdText, activityId);
		   	   		}
		   	   		activityIds +=  activityId+",";
		   	   }
		   	   if(activityIds != ''){
		   	   		clearAll("javascript:clearAllMarked();");
		   	   } else {
	   	   			clearAll("#");
		   	   }	
		   },
		   failure: function(result, response){
		   		 if(!isMarked){
				   	extAlert(errorTitle, unmarkedActivityError, Ext.MessageBox.ERROR);			   
				 } else {
				 	extAlert(errorTitle, markedActivityError, Ext.MessageBox.ERROR);			   
				 }
		   }
		});
	}
	
	//To set as unmarked
	function setAsUnmarked(idText, activityId){
		document.getElementById(idText+"_"+activityId).innerHTML = "<img src='"+JSPRootURL+"/images/activitylog/blank_checkmark.gif' class=\"markedIcon\" />"; 
		document.getElementById(idText+"_"+activityId).href = "javascript:marked("+activityId+", 1)";
		document.getElementById(idText+"_"+activityId).title = unMarkedToken; 
	}
	
	//To set as marked
	function setAsMarked(idText, activityId){
		document.getElementById(idText+"_"+activityId).innerHTML = "<img src='"+JSPRootURL+"/images/activitylog/green_checkmark.gif' class=\"markedIcon\" />"; 
		document.getElementById(idText+"_"+activityId).href = "javascript:marked("+activityId+", 0)";
		document.getElementById(idText+"_"+activityId).title = markedToken; 
	}
	
	//To unmark all activities
	function clearAllMarked(){
		document.getElementById('clearAll').innerHTML = '<img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="absmiddle" />';
		Ext.Ajax.request({
		   url: JSPRootURL +'/activity/activityLog/clear_all',
		   params: { activityIds : activityIds },
		   method: 'POST',
		   success: function(result, request){
			   var Ids = activityIds.substring(0,(activityIds.length-1)).split(",");
			   for (var index = 0; index < Ids.length; index++){ 
        			setAsUnmarked("marked", Ids[index]);
        			if(document.getElementById("commentMarked_"+Ids[index])!= null){
        				 setAsUnmarked("commentMarked", Ids[index]);
        			}
    		   }
    		   activityIds = "";
    		   document.getElementById('clearAll').innerHTML = clearAllToken;
    		   clearAll("#");
		   },
		   failure: function(result, response){
			   extAlert(errorTitle, unmarkedActivityError, Ext.MessageBox.ERROR);			   
		   }
		});
	}
	
	//To disable clearAll link
	function clearAll(value){
		document.getElementById('clearAll').href = value;
		if(value == '#'){		
			document.getElementById('clearAll').style.color = 'silver';
			document.getElementById('clearAll').style.cursor = 'help';
		} else {
			document.getElementById('clearAll').style.color = '#3399FF';
			document.getElementById('clearAll').style.cursor = '';
		}
	}
	
	//To mark activity
	function markedByPerson(jumpToDate){
		activityIds = '';	  
		Ext.Ajax.request({
		   url: JSPRootURL +'/activity/activityLog/marked_by_person',
		   params: { activityIdsPerPage : activityIdsPerPage, jumpToDate : jumpToDate },
		   method: 'POST',
		   success: function(result, request){
		   	  if(result.responseText != '') {
		   	  	activityIds = result.responseText;
		   	  	var Ids = result.responseText.substring(0, (activityIds.length-1)).split(",");
		   	  	for (var index = 0; index < Ids.length; index++){
		   	  		if(document.getElementById("marked_"+Ids[index]) != null){
		   	  			setAsMarked("marked", Ids[index]);
		   	   		}
    		    }
    		  }
    		  if(activityIds != ''){
		   	   		clearAll("javascript:clearAllMarked();");
		   	   } else {
	   	   			clearAll("#");
		   	   }
		   },
		   failure: function(result, response){
			   extAlert(errorTitle, markingErrorMessage, Ext.MessageBox.ERROR);			   
		   }
		});
	}
	
function showContent(){
	removeLoadingDiv();
	if(activitiesCounter == 0){
		if(document.getElementById('topShowDate')){
			document.getElementById('topShowDate').innerHTML= '';
		}
		if(document.getElementById('bottomShowDate')){
			document.getElementById('bottomShowDate').innerHTML='';
		}
	} else {
		if(startDate != '' && endDate != ''){
			if(startDate == endDate){
				document.getElementById('topShowDate').innerHTML= entriesFrom.replace("{0}", "<b>"+startDate+"</b>");
				document.getElementById('bottomShowDate').innerHTML= entriesFrom.replace("{0}", "<b>"+startDate+"</b>");
			} else {
				document.getElementById('topShowDate').innerHTML= entriesFromTo.replace("{0}", "<b>"+startDate+"</b>").replace("{1}", "<b>"+endDate+"</b>");
				document.getElementById('bottomShowDate').innerHTML= entriesFromTo.replace("{0}", "<b>"+startDate+"</b>").replace("{1}", "<b>"+endDate+"</b>");
			}
		}
	}
	if(document.getElementById("rssfeedLink") != null){
		document.getElementById("rssfeedLink").href = JSPRootURL+'/activity/rssFeed/'+encryptedValue;
	}
	if(document.getElementById("toprssFeedLink") != null){	
		document.getElementById("toprssFeedLink").href = JSPRootURL+'/activity/rssFeed/'+encryptedValue;
	}	
	if(document.getElementById("apply") != null){
	 	document.getElementById('apply').disabled = false;
	}
}
					
function applyFilter(filterType){
	var url = JSPRootURL +'/activity/ActivityLog/load_activities';
	var filterValues = document.getElementById('filterCriteria').value;
	var jumpToDate = getNextDate(document.getElementById('jumpedDateValue').value.trim());
	memberValue = filterValues.substring(0,filterValues.indexOf(','));
	if((!isNaN(memberValue)) && memberValue != document.getElementById('teamMember').value)
			filterValues = filterValues.replace(memberValue+',', document.getElementById('teamMember').value+',');
	if (filterType == 'apply') {
		document.getElementById('apply').disabled = true;
		offset = 0;
	}else if ((filterType == 'next' || filterType == 'prev')) {
		if (filterType == 'next') {
			offset += range;
		} else if (filterType == 'prev') {
			offset -= range;
		}
	} else if (filterType == 'jumpedDate'){
		offset = 0;
	}
	if(document.getElementById('filterCriteria').value == memberValue+',' || document.getElementById('filterCriteria').value == memberValue+',Marked,'){
			extAlert(errorTitle, selectFilterObjectMessage, Ext.MessageBox.ERROR);
			document.getElementById('apply').disabled = false;
	} else if((document.getElementById('filterCriteria').value.indexOf('blog_entry-my_posts_that_are_commented,') >= 0 
	           || document.getElementById('filterCriteria').value.indexOf('blog_entry-important,') >= 0) 
	           && !isBlogEventSelected){
			extAlert(errorTitle, selectBlogEventMessage, Ext.MessageBox.ERROR);
			document.getElementById('apply').disabled = false;
	} else if(jumpToDate == invalidDateMessage){
			extAlert(errorTitle, invalidDateMessage, Ext.MessageBox.ERROR);
			document.getElementById('apply').disabled = false;
	} else {
		showLoadingDiv(loadingMessage);	
		Ext.Ajax.request({
			   url: url+'', 
			   params: {jumpToDate : jumpToDate, filterValues : filterValues, offset : offset },
			   method: 'POST',
			   success: function(result, request){
			   		if(result.responseText == invalidDateMessage) {
			   			removeLoadingDiv();
			   			extAlert(errorTitle, invalidDateMessage, Ext.MessageBox.ERROR);
			   			document.getElementById('apply').disabled = false;
			   		} else {	
				   		document.getElementById('activityLog').innerHTML = result.responseText;
				   		clearAll("#");
				   		if(result.responseText.indexOf(noActivitiesMsg) >= 0){
				   			activityIds = '';
							document.getElementById('tableContent').style.height = '198px';
						}
				   		var scriptTag = '<script type="text/javascript">';
						var javaScriptCode = result.responseText;
						javaScriptCode = javaScriptCode.substring(javaScriptCode.indexOf(scriptTag)+scriptTag.length, javaScriptCode.indexOf('<\/script>'));
						eval(javaScriptCode.replace(/\n/g, ''));
				   		removeLoadingDiv();
				   		if (filterType == 'apply') {
				   			document.getElementById('apply').disabled = false;
				   		} 
						document.getElementById('continuedDate').innerHTML = continuedDateMessage;		   		
				   		handleLinks(offset, range, activitiesCounter);
				   			
				   		if(document.getElementById("rssfeedLink") != null){
							document.getElementById("rssfeedLink").href = JSPRootURL+'/activity/rssFeed/'+encryptedValue;
						}
						if(document.getElementById("toprssFeedLink") != null){	
							document.getElementById("toprssFeedLink").href = JSPRootURL+'/activity/rssFeed/'+encryptedValue;
						}
						showContent();
						try	{
							markedByPerson(jumpToDate);
							if(blogActivityIds != '')
								showExpandForBlog(jumpToDate);
				   		}catch(e){ }
			   		}
			   },
			   failure: function(result, response){
			   		document.getElementById('apply').disabled = false;
				   extAlert(errorTitle, activitiesLoadingError, Ext.MessageBox.ERROR);			   
			   }
			});
	}
}

// To show expand icon for blog entries which having comments
function showExpandForBlog(jumpToDate) {
	Ext.Ajax.request({
	   url: JSPRootURL +'/activity/activityLog/blog_with_comment',
	   params: { blogActivityIds : blogActivityIds, jumpToDate : jumpToDate},
	   method: 'POST',
	   success: function(result, request){
	   	  if(result.responseText != '') {
	   	  	var Ids = result.responseText.substring(0, (result.responseText.length-1)).split(",");
	   	  	for (var index = 0; index < Ids.length; index++){
	   	  		if(document.getElementById('expand_'+Ids[index]) != null)
	   	  			document.getElementById('expand_'+Ids[index]).className = '';
   		    }
   		  }
	   },
	   failure: function(result, response){
	   }
	});
}	
	
function handleLinks(offset, range, activitiesCounter){
	if(offset >= range){
		document.getElementById('topPreviousLink').innerHTML = '<a id=\'topPrevious\' href=\'javascript:applyFilter("prev");\' title=\''+prev+'\'><img src=\''+JSPRootURL+'/images/activitylog/left.gif\'/></a>';
		document.getElementById('bottomPreviousLink').innerHTML = '<a id=\'bottomPrevious\' href=\'javascript:applyFilter("prev");\' title=\''+prev+'\'><img src=\''+JSPRootURL+'/images/activitylog/left.gif\'/></a>';
	}
	if(activitiesCounter < range){
		document.getElementById('topNextLink').innerHTML = '';
		document.getElementById('bottomNextLink').innerHTML = '';
	} else {
		document.getElementById('topNextLink').innerHTML = '<a id=\'topNext\' href=\'javascript:applyFilter("next");\' title=\''+next+'\'><img src=\''+JSPRootURL+'/images/activitylog/right.gif\'/></a>';
		document.getElementById('bottomNextLink').innerHTML = '<a id=\'bottomNext\' href=\'javascript:applyFilter("next");\' title=\''+next+'\'><img src=\''+JSPRootURL+'/images/activitylog/right.gif\'/></a>';
	}
	if(offset == 0){
		document.getElementById('topPreviousLink').innerHTML = '';
		document.getElementById('bottomPreviousLink').innerHTML = '';
	}
}
		   			
// functions called from showActivityTextForBlogEntry
	//display full view of blog entry
	function fullView(activityLogId, objectId, activityBy, actionFor){
	  var isMarked = false;
	  var isImportantBlog = false;
	  if(document.getElementById('expand_'+activityLogId) != null){	
		document.getElementById('expand_'+activityLogId).innerHTML = '<img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="absmiddle" />';
	  }
	  if(document.getElementById('marked_'+activityLogId) != null){
	  	isMarked = document.getElementById('marked_'+activityLogId).title == markedToken;
	  }
	  if(document.getElementById('objectLink_'+activityLogId)){
	  	isImportantBlog = (document.getElementById('objectLink_'+activityLogId).innerHTML.indexOf("<font color=\"red\"><b>!</b></font>") >= 0);
	  }	
	  Ext.Ajax.request({	
		  url: JSPRootURL +'/activity/BlogCommentView/showFullBlogEntry',
			   params: { activityLogId : activityLogId,  objectId : objectId, activityBy : activityBy, actionFor : actionFor, isMarked : isMarked, isImportantBlog : isImportantBlog},
			   method: 'POST',
			   success: function(result, request){
			   	  if(result.responseText != '') {
			   	  	removeLoadingDiv();
			   	  	if(document.getElementById('expand_'+activityLogId) != null){	
			   	  		document.getElementById('expand_'+activityLogId).innerHTML = '<img src="'+JSPRootURL+'/images/activitylog/expand.gif" align="absmiddle" />';
			   	  	}	
			   	  	var fullBlogEntryHtml = result.responseText;
			   	  	document.getElementById("blogEntryTextShortView_"+activityLogId).style.display = 'none';
			   	  	document.getElementById("blogEntryTextFullView_"+activityLogId).style.display = 'block';
			   	  	document.getElementById("blogEntryTextFullView_"+activityLogId).innerHTML = fullBlogEntryHtml;
			   	  	if(actionFor == 'blogComment'){
			   	  		document.getElementById("commentTitle_"+activityLogId).style.display = 'none';
			   	  		document.getElementById("personImage_"+activityLogId).style.display = 'none';
			   	  		if(document.getElementById("commentLabel_"+activityLogId) != null){
			   	  			document.getElementById("commentLabel_"+activityLogId).style.display = 'block';
			   	  		}
			   	  		document.getElementById("blogEntryTextShortView_"+activityLogId).style.paddingLeft = '';	
			   	  	}else if(actionFor == 'blogEntry'){
			   	  		if(document.getElementById("commentLabel_"+activityLogId) != null){
			   	  			document.getElementById("commentLabel_"+activityLogId).style.display = 'block';
			   	  		}
			   	  	}
			   	  }	
			   },
			   failure: function(result, response){
				   extAlert(errorTitle, blogEntryLoadingError, Ext.MessageBox.ERROR);			   
			   }
	 });	   	  
	}
	
	//display short view of blog entry
	function shortView(activityLogId){
		hideActivityCommentDialogue(activityLogId);
		document.getElementById("blogEntryTextShortView_"+activityLogId).style.paddingLeft = '5px';
		document.getElementById("blogEntryTextShortView_"+activityLogId).style.display = '';
		document.getElementById("blogEntryTextFullView_"+activityLogId).style.display = 'none';
		if(document.getElementById("commentTitle_"+activityLogId) != null && document.getElementById("commentTitle_"+activityLogId).style.display == 'none'){
			document.getElementById("commentTitle_"+activityLogId).style.display = '';
		}
		if(document.getElementById("personImage_"+activityLogId) != null && document.getElementById("personImage_"+activityLogId).style.display == 'none'){
			document.getElementById("personImage_"+activityLogId).style.display = '';
		}
		if(document.getElementById("commentLabel_"+activityLogId) != null){
			document.getElementById("commentLabel_"+activityLogId).style.display = 'none';
		}
		document.getElementById('expand_'+activityLogId).className = '';	
	}
	
	//open dialog for add new comment
	function addActivityComment(activityId, weblogEntryId){
		var url = JSPRootURL +'/activity/ActivityLog/check_blog';
		Ext.Ajax.request({
			   url : url+'?module='+blogModuleId,
			   params: {weblogEntryId : weblogEntryId},   
			   method: 'POST',
			   success: function(result, request){
			   		if(result.responseText == 'true') {
			   			  extAlert(errorTitle, blogEntryDeleted, Ext.MessageBox.ERROR);
			   		} else {
				   		var imgPath =  JSPRootURL+"/images/activitylog/coment.gif";
						if(document.getElementById('newComentDialogue_For_'+activityId).style.display = 'none'){
							document.getElementById('newComentDialogue_For_'+activityId).style.display = 'block';
							document.getElementById('textAreaContent_'+activityId).style.color = 'Gray';
							document.getElementById('textAreaContent_'+activityId).value = commentHere;
							commentFlag = false;
						}
						if(document.getElementById('commentImage_'+activityId))
							document.getElementById('commentImage_'+activityId).innerHTML = '<img src="'+imgPath+'" />';
						if(document.getElementById('addComment_'+activityId) != null){
							document.getElementById('addComment_'+activityId).innerHTML = '<span style="color: #3399FF;font-size: 11px;font-weight: bold;">'+comment+'</span>';
						}
					}
			   },
			   failure: function(result, response){
				   extAlert(errorTitle, blogEntryCheck, Ext.MessageBox.ERROR);			   
			   }
		});
	}
	
	//save activity comment
	function saveBlogActivityComment(weblogEntryId, commentId, activityLogId, activityBy, actionFor){
		var isActivityComment = true;
		var  url = JSPRootURL +'/blog/AddWeblogEntryComment/Save';
		var content = document.getElementById('textAreaContent_'+activityLogId).value;
		if(content.trim() == '' || !commentFlag){
			extAlert(errorTitle, contentNotBlankMessage, Ext.MessageBox.ERROR);
		}else{
			hideActivityCommentDialogue(activityLogId);
			if(document.getElementById('addComment_'+activityLogId) == null)
				document.getElementById('showLoading_'+activityLogId).innerHTML = '<img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="top" />';
			if(document.getElementById('expand_'+activityLogId) != null)	
				document.getElementById('expand_'+activityLogId).innerHTML = '<img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="absmiddle" />';
			if(document.getElementById('collapse_'+activityLogId) != null)	
				document.getElementById('collapse_'+activityLogId).innerHTML = '<img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="absmiddle" />';
			Ext.Ajax.request({
			   url : url+'?module='+blogModuleId,
			   params: {moduleId : blogModuleId, weblogEntryId : weblogEntryId, content : content, isActivityComment : isActivityComment },
			   method: 'POST',
			   success: function(result, request){
			   	   if(document.getElementById('addComment_'+activityLogId) == null)
			   	   		document.getElementById('showLoading_'+activityLogId).innerHTML = '';
			   	   if(result.responseText == "true") {
			   	   		var objectId = (actionFor == 'blogComment') ? commentId : weblogEntryId;
			   	   		fullView(activityLogId, objectId, activityBy, actionFor);
			   	   } else  if(result.responseText == "deleted") {
			   	   		document.getElementById('expand_'+activityLogId).innerHTML = '<img src="'+JSPRootURL+'/images/default/grid/expand.gif" align="absmiddle" />';
			   	   		extAlert(errorTitle, commentDeletedMessage, Ext.MessageBox.ERROR);
			   	   } else if(result.responseText == "false"){
			   	   		extAlert(errorTitle, commentNotSavedMessage, Ext.MessageBox.ERROR);
			   	   }
			   },
			   failure: function(result, response){
				   extAlert(errorTitle, commentNotSavedMessage, Ext.MessageBox.ERROR);			   
			   }
			});
		}	
	}
	
	//hide add comment dialogue
	function hideActivityCommentDialogue(activityId){
		var imagePath = JSPRootURL+"/images/activitylog/coment_blank.gif";
		document.getElementById('commentImage_'+activityId).innerHTML = '';
		document.getElementById('commentImage_'+activityId).innerHTML = '<img src="'+imagePath+'" />';
		document.getElementById('newComentDialogue_For_'+activityId).style.display = 'none';
		if(document.getElementById('addComment_'+activityId) != null){
			document.getElementById('addComment_'+activityId).innerHTML = '<a style="color: #3399FF;font-size: 11px;font-weight: bold;" href="javascript:addActivityComment('+activityId+');">'+addCommentLabel+'</a>';
		}
	}
	
	//Hide the comments
	function hideActivityComments(entryId, commentToken){
		var commentsBlockId = 'Comments_For_'+entryId;
		var addCommentForId = 'addCommentFor_'+entryId;
		if(document.getElementById(commentsBlockId).style.display == 'block') {
			document.getElementById(commentsBlockId).style.display = 'none';
			document.getElementById('hideShowCommentsLinkFor_'+entryId).innerHTML = '<span style="color: #3399FF;font-size: 11px;font-weight: bold;">'+viewComment+'</span>';
		} else {
			document.getElementById(commentsBlockId).style.display = 'block';
			document.getElementById('hideShowCommentsLinkFor_'+entryId).innerHTML = '<span style="color: #3399FF;font-size: 11px;font-weight: bold;">'+hideComment+'</span>';
		}
	}		
	
	// Hide the 'Comment Here' text from text area
	function showCommentHere(activityId){
		if(!commentFlag){
			commentFlag = true;
			document.getElementById('textAreaContent_'+activityId).value = '';
			document.getElementById('textAreaContent_'+activityId).style.color = 'Black';
		}
	}
	
	// reset empty values for comment dialog box
	function changeCommentMessage(activityId){
		var content = document.getElementById('textAreaContent_'+activityId).value.trim();
		if(commentFlag && content == ''){
			commentFlag = false;
			document.getElementById('textAreaContent_'+activityId).style.color = 'Gray';
			document.getElementById('textAreaContent_'+activityId).value = commentHere;
		}
	}
	
	//This function set tooltip for Team Members
	function showMemberDetails() {	
		var teamMemberSel = document.getElementById("teamMember");	
		if(typeof teamMemberSel != 'undefined' && teamMemberSel != null){
			for(var index=0; index<teamMemberSel.length; index++){
				teamMemberSel.options[index].title = teamMemberSel.options[index].text;			
			}
		}
	}	
	
	// Expand and collapse of filter window
	function filterExpandCollapse(){
		if(document.getElementById('filter_content_row').className == 'displayNone'){
			openSlidingPanel();
		}else{
			closeSlidingPanel();
		}
	} 
	
	// Open filter window
	function openSlidingPanel(){
		document.getElementById('filter_content_row').className = '';
		document.getElementById('slidingImage').className = 'filterCollapseImage';
	}

	// Open close filter window
	function closeSlidingPanel(){
		document.getElementById('filter_content_row').className = 'displayNone';
		document.getElementById('slidingImage').className = 'filterExpandImage';
	}

	//Clear all check boxes
	function clearFilterCheckBobex(){
		selectDeselectCheckboxes('all',false);
	}
	
	// Handling keydown event for the Jump to date field
	document.onkeydown = function(e){
		var type = e ? e.target : window.event.srcElement;
		e =(window.event)? event : e;
		if(e.keyCode == 13 && type.id == 'jumpedDateValue') {
			if(type.value.trim() == ''){
				extAlert(errorTitle, dateValueNotBlankMessage, Ext.MessageBox.ERROR);
			} else {
			   	applyFilter('jumpedDate');
			}
		} 
	}
	
	// changes state of checkbox
	function changeState(id){
		document.getElementById(id).checked = !document.getElementById(id).checked;
	}
	
	// changes in date by adding next date
	function getNextDate(dateString) {
		if(dateString != ''){
			if(dateString == userDateformat)
				return '';
			if(checkDate(dateString, userDateformat)) {
				theDate.setDate(theDate.getDate()+1);
				return theDate.getDate() + '/' + (theDate.getMonth()+1) + '/' + theDate.getFullYear();
			} else {
				return invalidDateMessage;
			}
		} else {
			return dateString;
		}
	}
	
	// check for valid date format
	function checkDate(date, pattern) {
		if (date != null && pattern != null) {
			var isTwoDigitDate = (pattern.indexOf("dd") >= 0 || pattern.indexOf("tt") >= 0 || pattern.indexOf("jj") >= 0);
			var isTwoDigitMonth = (pattern.indexOf("MM") >= 0 || pattern.indexOf("nn") >= 0);
			var pattern = getJSUserDatePattern(pattern).toLowerCase();
			dateString = date.replace(new RegExp("[0-9]","g"), "");
			patternString = pattern.replace(new RegExp("[a-z]","g"), "");
			if(dateString != patternString) {
				return false;
			} else {
				var seperator = patternString.charAt(0);
				var patternArray = pattern.split(seperator);
				var dArray = date.split(seperator);
				var dateArray=new Array(3);
				var finalDate = '';
				for(var index = 0; index < patternArray.length; index++) {
					switch(patternArray[index]){
						case 'd' : case 'j' :
						                     dateArray [1] = dArray[index];
						                     if(isTwoDigitDate && dateArray[1].length == 1)
												dateArray[1] = '0' + dateArray[1];
						                     finalDate += dateArray[1] + seperator;
						                     break;
						case 'm' : case 'n' :
											 dateArray [0] = dArray[index];
											 if(isTwoDigitMonth && dateArray[0].length == 1)
												dateArray[0] = '0' + dateArray[0];
											 finalDate += dateArray[0] + seperator;
											 break;
						case 'y' : case 'Y' :
											 dateArray [2] = dArray[index];
											 finalDate += dateArray[2] + seperator;
											 break;
					}
				}
				finalDate = finalDate.substring(0, finalDate.length-1);
				if(isNaN(dateArray[0]) || isNaN(dateArray[1]) || isNaN(dateArray[2])) {
					return false;
				} else if ((getJSUserDatePattern(userDateformat).indexOf("Y") >= 0) && dateArray[2].length != 4) {
					return false;
				} else if ((getJSUserDatePattern(userDateformat).indexOf("y") >= 0) && dateArray[2].length != 2) {
					return false;
				} else if (dateArray[0].length > 2 || dateArray[0] < 1 || dateArray[0] > 12) {
					return false;
				} else if (dateArray[1].length > 2 || dateArray[1] < 1 || dateArray[1] > 31) {
			 		return false;
				}
			}
			theDate = Date.parseDate(finalDate, getJSUserDatePattern(userDateformat));
			var d1 = new String(theDate.getDate());
			var zeros = new RegExp("0");
			var tmp = new String(dateArray[1])
			tmp = tmp.replace(zeros, "");
			var d2 = new RegExp(tmp);
			d1 = d1.replace(zeros, "");
			return (d1.search(d2) != -1);
		} else {
			return false;
		}
	}
	
	// To set date format in jumped date text box
	function changeJumpedDateText() {
		if(document.getElementById('jumpedDateValue').value == ''){
			document.getElementById('jumpedDateValue').style.color = 'Gray';
			document.getElementById('jumpedDateValue').value = userDateformat;
		}
	}
	
	// Set jump date string in black color 
	function setJumpedDateFrom() {
		if (document.getElementById('jumpedDateValue').value.trim() == userDateformat) {
			document.getElementById('jumpedDateValue').value = '';
		}
		document.getElementById('jumpedDateValue').style.color = 'Black';
	}
	
	// To check object whether it is deleted or not exluding wiki object
	function checkAndRedirect(objectId, objectType, activityId) {
		checkAndRedirectForObject(objectId, objectType, activityId, null, null);
	}
	
	// To check object whether it is deleted or not and redirect to object url if not deleted 
	function checkAndRedirectForObject(objectId, objectType, activityId, objectName, parentObjectId) {
		document.getElementById('showLoading_'+activityId).innerHTML = '<img src="'+JSPRootURL+'/images/default/grid/loading.gif" align="top" />';
		document.getElementById('objectLink_'+activityId).href = '#';
		Ext.Ajax.request({
		   url: JSPRootURL +'/activity/activityLog/check_and_redirect',
		   params: { objectId : objectId, objectType : objectType, objectName : objectName, parentObjectId : parentObjectId},
		   method: 'POST',
		   success: function(result, request){
		   	  document.getElementById('showLoading_'+activityId).innerHTML = '';
		   	  if(objectType == 'wiki')
		   	  	document.getElementById('objectLink_'+activityId).href = "javascript:checkAndRedirectForObject('" + objectId + "','" + objectType + "','" + activityId + "','" + objectName + "','" + parentObjectId + "');";
		   	  else
		   	  	document.getElementById('objectLink_'+activityId).href = "javascript:checkAndRedirect('" + objectId + "','" + objectType + "','" + activityId + "');";
		   	  objectType = objectType.indexOf('_') >=0 ? objectType.replace("_", " ") : objectType;
		   	  if(result.responseText != '' && result.responseText != 'hidden' && result.responseText != 'deleted' && result.responseText != 'accessDenied')
		   	  	self.location = result.responseText;
	   		  else if (result.responseText == 'hidden')
	   		    extAlert(errorTitle, msgFormat.format(hiddenMessage, initCap(objectType), "<i>"+document.getElementById('objectLink_'+activityId).innerHTML+"</i>"), Ext.MessageBox.ERROR);
	   		  else if(result.responseText == 'accessDenied')
	   		  	extAlert(errorTitle, msgFormat.format(accessDeniedMessage, initCap(objectType), "<i>"+document.getElementById('objectLink_'+activityId).innerHTML+"</i>"), Ext.MessageBox.ERROR);
	   		  else	
	   		 	extAlert(errorTitle, msgFormat.format(objectDeletedMessage, initCap(objectType == 'doc container'? 'folder' : objectType), "<i>"+document.getElementById('objectLink_'+activityId).innerHTML+"</i>"), Ext.MessageBox.ERROR);  	  
		   },
		   failure: function(result, response){
		   }
		});
	}		
	