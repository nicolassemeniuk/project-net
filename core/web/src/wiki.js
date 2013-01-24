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
/**
 *	JavaScript file for wiki related functions.
 */

// This function is for triming the spaces in values
String.prototype.trim = function(){
    a = this.replace(/^\s+/, '');
    return a.replace(/\s+$/, '');
}

/* Global variables */
var win;							//window for displaying wiki page content
var wikiSpaceId;					//'owning' Space's(project...) spaceId which index we are selecting images form 
var wikiObjectId;					//id of object for who we are creating wiki (task/form...)
var selectedWikiSpacePageName;		//name of wiki page from 'owning' object space which we will assign to current object (wikiObjectId)

/* Wokrplan related variables */
var wikiWin;						// window for holding wiki tabs
var wikiTabs;						// wiki tabs
var wikiContentPanel;				// Initial wiki panel (index = 0)
var wikiEditPagePanel;				// Edit Page wiki panel
var selectedRecord;					// selected record from the grid
var wikiObjectName;					// Name of selected object (item)
var standardWikiMenu;				// Standard Wiki Menu: Edit/Assign/Cancel
var wikiObjectType					// Object type for the task or assignment

var SHOW_PAGE_CONTENT_TAB = 'showPageContent';				// the name/ID of the tab used for showing page content - selected from pg. index
var WIKI_PAGE_CONTENT_TAB = 'wikiPageContent';				// the ID of the tab used for holding the content of selected items w. page (or displaying Page Index)

var previewingProjectsPage = false;			// if clicking on Page Index page for some project - set this property to 'true', to hide Edit/Assign menu buttons

standardWikiMenu = new Ext.menu.Menu({
				        id: 'wikiStandardMenu',
				        items: [
						  {  
				               text: 'Back To Project Page Index',				               
				               id: 'backToPageIndexWButton',
							   cls: 'x-btn-text-icon',
							   disabled: true,	
				               handler: function(){	handleBackToProjectPageIndex();	}
				          }, '-', 
						  {  
				               text:'Edit',
				               id: 'editWButton',
							   cls: 'x-btn-text-icon',
							   disabled: true,	
				               handler: function(){	handleEditWikiPage();	}
				          }, '-', 
				          {  
				               text:'Cancel',
				               id: 'cancelButton',
							   cls: 'x-btn-text-icon',
							   disabled: true,
				               handler: function(){	handleCancelWikiPagePreview();		}
				          },  
				          {  
				               text:'Assign Page To Selected Object',
				               id: 'assignButton',
							   cls: 'x-btn-text-icon',
							   disabled: true,
				               handler: function(){	handleAssignWikiPageToAssignment();		}
				          },  
				          {  
				               text:'Unassign Page From Selected Object',
				               id: 'unassignButton',
							   cls: 'x-btn-text-icon',
							   disabled: true,
				               handler: function(){	handleUnAssignWikiPageFromAssignment();		}
				          }
						]
					});

//Function for displaying conent of selected wiki page
function showPageContent(spaceId, pageName, objectId, wikiPgId) {
	//persist values for new wiki assignment record
	wikiSpaceId = spaceId;
	wikiObjectId = objectId;
	selectedWikiSpacePageName = pageName;
	var isPreview = 'preview';
	if( spaceId == objectId) {											// If previewing 'project' - display Page Index 
		previewingProjectsPage = true;									// hide Edit/Assign buttons, enable 'Back to Page Index' button
		isPreview = 'projectPagePreview';
	} else {
		previewingProjectsPage = false;
	}
	if(!wikiTabs) {
		showPageContentRightPane(spaceId, pageName, objectId, isPreview, wikiPgId);
	} else {
		if(typeof(wikiItFor) != 'undefined' && wikiItFor == 'workPlanProject') {			/* WORKPLAN INTEGRATION */
			var tab;
			tab = wikiTabs.getItem(SHOW_PAGE_CONTENT_TAB);			// get a reference to tab with id="SHOW_PAGE_CONTENT_TAB_NAME"
			disableWikiPanelButtons();									// when getting content set wiki buttons to disabled
	
			if( !tab ) {									// if "showPageContent" tab isn't created
				addWikiTab(SHOW_PAGE_CONTENT_TAB, pageName);			// create new tab for displaying content of selected w. page from Pg.Index
				tab = wikiTabs.getItem(SHOW_PAGE_CONTENT_TAB);			// get a reference to newly created tab
				tab.getUpdater().update({								// update the tab with content
	    			url: JSPRootURL+'/pwiki/WikiAjaxHandler/getPageContent?module='+moduleId,
				    params: {spaceId: spaceId, objectType: 'project', objectName: pageName, objectId: spaceId, isPreview: isPreview},	//objectId is set to spaceId in order to set important values for previewing project's space wiki pages
				    callback: function(){
						if( previewingProjectsPage == true ) {	// Projects Page Index previewing
							disableWikiButtons();
							enableShowProjectPageIndexButton();
						} else {
					    	updateMenu();
							toggleEditing();								// upon retreiving the content - check should "Edit" button be displayed
							enableWikiPanelButtons();
						}
						tableOfContentClickHandler();				// TOC click handler
						changeWikiTabHeaderMessage('wikiDivTop', wikiPagesFoundMessage, wikiPagesNotFoundMessage, objectName);
				   	},
				    failure: function(result, response){
					    extAlert(errorAlertTitle, serverRequestFailed, Ext.MessageBox.ERROR);
						wikiTabs.hideTabStripItem(tab);					// server failed to load tab with content - hide tab
				    }
	    		});
			} else {										// else if "showPageContent" tab exists
				wikiTabs.unhideTabStripItem(tab);						// unhide Tab - which is hidden in "Cancel" button handler
				tab.setTitle(pageName);
	    		wikiTabs.activate(tab);
	    		tab.getUpdater().update({								// refresh tab
	    			url: JSPRootURL+'/pwiki/WikiAjaxHandler/getPageContent?module='+moduleId,
				    params: {spaceId: spaceId, objectType: 'project', objectName: pageName, objectId: spaceId, isPreview: isPreview},	//objectId is set to spaceId in order to set important values for previewing project's space wiki pages
				    callback: function(){
						if( previewingProjectsPage == true ) {	// Projects Page Index previewing
							disableWikiButtons();
							enableShowProjectPageIndexButton();
						} else {
					    	updateMenu();
							toggleEditing();								// upon retreiving the content - check should "Edit" button be displayed
							enableWikiPanelButtons();
						}
						tableOfContentClickHandler();				// TOC click handler
						changeWikiTabHeaderMessage('wikiDivTop', wikiPagesFoundMessage, wikiPagesNotFoundMessage, objectName);
				   	},
				    failure: function(result, response){
					    extAlert(errorAlertTitle, serverRequestFailed, Ext.MessageBox.ERROR);
					    wikiTabs.hideTabStripItem(tab);					// server failed to load tab with content - hide tab
				    }
	    		});
	    	}
	
		} else {																		/* MYASSIGNMENTS INTEGRATION */
			//relaoad wikiPanel window with conent of new wiki page
			if(typeof(wikiPanel) != 'undefined'){
			wikiPanel.load({
				    url: JSPRootURL+'/pwiki/WikiAjaxHandler/getPageContent?module='+moduleId,
				    params: {spaceId: spaceId, objectType: 'project', objectName: pageName, objectId: spaceId, isPreview: isPreview},	//objectId is set to spaceId in order to set important values for previewing project's space wiki pages
				    method: 'POST',
				    discardUrl: false,
				    nocache: false,
				    text: loadingImage,
				    timeout: 30,
				    scripts: false,
				    callback: function(){
						if( previewingProjectsPage == true ) {	// Projects Page Index previewing
							disableWikiButtons();
							enableShowProjectPageIndexButton();
						} else {
					    	toggleEditing();								// upon retreiving the content - check should "Edit" button be displayed
							toggleWikiPanelButtons();
						}
						tableOfContentClickHandler();				// TOC click handler
						changeWikiTabHeaderMessage('wikiDivTop', wikiPagesFoundMessage, wikiPagesNotFoundMessage, objectName);
				   	},
				    failure: function(result, response){
						selectedTask = objectName;						// objectName - variable from Personal Assignment page grid
					    extAlert(errorAlertTitle, serverRequestFailed, Ext.MessageBox.ERROR);
					    loadWikiForAssignment(selectedTask, taskSpaceId);
				    }
			});
		  } else {
		  	Ext.Ajax.request({
			   url: JSPRootURL+'/pwiki/WikiAjaxHandler/getPageContent?module='+moduleId,
			   params: {spaceId: spaceId, objectType: 'project', objectName: pageName, objectId: spaceId, isPreview: isPreview},	//objectId is set to spaceId in order to set important values for previewing project's space wiki pages
			   method: 'POST',
			   success: function(result, request){
					document.getElementById('wikiDivBody').innerHTML =  '';					
					document.getElementById('wikiDivBody').innerHTML = result.responseText;
					changeWikiTabHeaderMessage('wikiDivTop', wikiPagesFoundMessage, wikiPagesNotFoundMessage, objectName);
			   },
			   failure: function(result, response){
		 			selectedTask = objectName;						// objectName - variable from Personal Assignment page grid
				    extAlert(errorAlertTitle, serverRequestFailed, Ext.MessageBox.ERROR);
				    loadWikiForAssignment(selectedTask, taskSpaceId);
			   },
			   callback : function(result, response){
	   				if( previewingProjectsPage == true ) {	// Projects Page Index previewing
						disableHtmlWikiButtons();;
						enableShowProjectPageIndexButton();
					} else {
				    	toggleHtmlEditing();							// upon retreiving the content - check should "Edit" button be displayed
						toggleWikiPanelButtons();
					}
					tableOfContentClickHandler();				// TOC click handler
			   }
		   });
		  }
		}
	}	
}

/**
 * Function executed after clicking on 'Assing page to selected object' button:
 * 		Assigns selected wiki page(selectedWikiSpacePageName) from 'owner' objects(wikiSpaceId) 
 *		wiki to selected assignment's object type wiki (wikiObjectId)
 */	
function assignWikiPageToAssignmentWiki(wikiSpaceId, wikiObjectId, selectedWikiSpacePageName) {
	//Call java method to assing selected wiki page to selected assignment object
	Ext.Ajax.request({
		url: JSPRootURL+'/pwiki/WikiAjaxHandler/assignWikiPageToObject?module='+moduleId,
		params: {module: moduleId , spaceId: wikiSpaceId, objectId:wikiObjectId, wikiPgName:selectedWikiSpacePageName},
		method: 'POST',
		success: function(result, request) {
			//refresh Wiki Content Panel with newly assigned wiki page
			if( typeof(wikiItFor) != 'undefined' && wikiItFor == 'workPlanProject' ) {			// WORKPLAN INTEGRATION
				var tab = wikiTabs.getItem(SHOW_PAGE_CONTENT_TAB);
				wikiTabs.hideTabStripItem(tab);			//wikiTabs.unhideTabStripItem(tab);
				tab = wikiTabs.getItem( WIKI_PAGE_CONTENT_TAB );
				var wikiObjectRootPageName = replaceAll(wikiObjectName, ' ', '_');
				// reload content of this tab - start
				tab.getUpdater().update({								// refresh tab
	    			url: JSPRootURL+'/pwiki/WikiAjaxHandler/getPageContent?module='+moduleId, 
		        	params: 'spaceId='+wikiSpaceId+'&objectType=task&objectName='+wikiObjectRootPageName+'&objectId='+wikiObjectId,
				    callback: function(){
				    	updateMenu();
				   	},
				    failure: function(result, response){
					    extAlert(errorAlertTitle, serverRequestFailed, Ext.MessageBox.ERROR);
				    }
	    		});
				// reload content of this tab - end
				wikiTabs.activate(tab);											// activating wiki tab that should hold selected item wiki page content
				
			} else {																			// MYASSIGNMENT INTEGRATION
				selectedTask = objectName;										// objectName: Personal Assignment page grid variable
				loadWikiForAssignment(selectedTask, taskSpaceId);
			}
		},
		failure: function(result, response) {
			extAlert(errorAlertTitle, serverRequestFailed, Ext.MessageBox.ERROR);
		}
	});
	
}

function toggleWikiPanelButtons() {
	var status = Ext.getCmp("assignButton").disabled;	
    status ? 	enableWikiPanelButtons() : disableWikiPanelButtons();
}

/** 
 * Function to determine should "Edit" button for wiki tab be enabled or not. 
 */
function toggleEditing() {
	var editWButton = Ext.getCmp("editWButton");
	var wContent = document.getElementById("wEContent");

	if( wContent ) {
	    editWButton.enable()
	} else {
	    editWButton.disable();
	}
}
/**
 * Function to disable all wiki buttons. Used when checking wiki existence for some assignment. 
 */
function disableWikiButtons() {
	var status = Ext.getCmp("assignButton").disabled;
	var editWButton = Ext.getCmp("editWButton");
	
	if( !status ) {
		disableWikiPanelButtons();
	}
	if( !editWButton.disabled ) {
		editWButton.disable();
	}
}

function enableWikiPanelButtons(){
	Ext.getCmp("assignButton").enable();
	Ext.getCmp("cancelButton").enable(); 	
}

function disableWikiPanelButtons(){
	Ext.getCmp("assignButton").disable();
	Ext.getCmp("cancelButton").disable(); 
}

function enableShowProjectPageIndexButton(){
	Ext.getCmp("backToPageIndexWButton").enable(); 	
}

function disableShowProjectPageIndexButton(){
	Ext.getCmp("backToPageIndexWButton").disable(); 
}

/**
 *	Wiki Workplan integration function.
 */
function wiki() {
	if (verifySelection(theForm, 'single', noSelectionErrorMessage)) {
		createWorkplanWidgets(document.getElementById("tn_"+getSelection(theForm)).innerHTML, getSelection(theForm));
	    wikiTabs.activate(wikiContentPanel);
	    wikiWin.show();
	    wikiTabs.on('tabchange', tabChangedHandler);							// registering 'tabchange' handler upon showing wikiWin
	}    
}

/* Helper function for creating Workplan related widgets */
function createWorkplanWidgets(taskName, workPlanId) {
	
	// Wiki Content Panel creation
	wikiContentPanel = new Ext.Panel({
		id: WIKI_PAGE_CONTENT_TAB,
		title: taskName,
		autoScroll:true,
		autoLoad: {	
        	url: JSPRootURL+'/pwiki/wikiAjaxHandler/getPageContent?module='+moduleId, 
        	params: 'spaceId='+spaceId+'&objectType=task&objectName='+taskName+'&objectId='+workPlanId,
		    callback: function(){
		    	updateMenu();
		    	tableOfContentClickHandler();										// TOC click handler
		   	}
       	},
        text: loadingImage
	});	
	
	//createWikiEditPanel();
	
	// Wiki TabPanel creation
    wikiTabs = new Ext.TabPanel({
        region: 'center',
		margins:'3 3 3 3', 
        defaults: {autoScroll:true},
        items:[
				wikiContentPanel
				//wikiEditPagePanel
        ]
    });
    
    // Wiki Window creation
    wikiWin = new Ext.Window({
       id: 'dialogWin',
       title: 'Wiki in a Window',          
       width:780,  
       height:435,
	   plain: true,
	   modal: true,
	   resizable: true,
	   layout: 'border', 
       items: [  wikiTabs  ],
       bbar: [
       		{  
               text:			'Close',
               id:				'closeBtn',
               handler:			function(){	wikiWin.close();	}//wikiWin.destroy();		}
           	}, 
           	'-',
       		{
       			text:			'Menu',
       			id:				'wikiMenuBtn',
       			menu:			standardWikiMenu
       		}
       ],
       listeners: {
		       render:	renderWikiWinHandler
	   }
    });
}

/**
 *	Function to create new tab with:
 *	@id set to idValue
 *	@title set to tabName
 */
function addWikiTab(idValue, tabName){
    wikiTabs.add({
    	id: idValue,
        title: tabName,
        enableTabScroll:true,
        closable:true
    });

    var tab = wikiTabs.getItem(idValue);
	wikiTabs.activate(tab);
}

/**
 * Helper method for opening each Wiki Page in different tab.
 * Function to create(if it doesn't exist) or show(if it exists) tab with specified id
 * @id the name of wiki page to show 
 */
function showAppropriateWikiPageTab(id) {
  var tab = wikiTabs.getItem(id);
  
  if (!tab) {											// tab doesn't exist - create one
    alert('tab does not exist - creating...');
    addWikiTab(id, id);
    wikiTabs.getItem(id).getUpdater().update({						// refresh tab
		url: JSPRootURL+'/pwiki/wikiAjaxHandler/getPageContent?module='+moduleId,
	    params: {spaceId: spaceId, objectType: 'project', objectName: pageName, objectId: spaceId, isPreview: 'preview'}	//objectId is set to spaceId in order to set important values for previewing project's space wiki pages
	    ,callback: function(){
			toggleEditing();			// upon retreiving the content - check should "Edit" button be displayed
			toggleWikiPanelButtons();
	   	},
	    failure: function(result, response){
		    extAlert(errorAlertTitle, serverRequestFailed, Ext.MessageBox.ERROR);
		    loadWikiForAssignment(id, spaceId);
	    }
	});
  } else {												// tab exist - activate it
    alert('tab exists - activating...');
    wikiTabs.activate(tab);
  }
}

/*******************************	Wiki Menu Handlers - Start	*******************************/

// Handler function for displaying project Page Index
function handleBackToProjectPageIndex() {
	// helper variables
	var wikiPageName;					// variable to hold the name of selected Wiki Page to edit
	var wikiSpaceId;					// id of current space
	
	wikiPageName = replaceAll(objectName, ' ', '_');								//replace all " " occurences with "_" in Wiki Page Names
	wikiSpaceId = taskSpaceId;
	loadWikiForAssignment(wikiPageName, wikiSpaceId, true);
}

// Handler function which is trigerd upon clicking on "Edit" button //
function handleEditWikiPage() {
	// helper variables
	var wikiSpaceId;				// id of current space
	var wikiObjectId;				// id of selected object which wiki page we are editing
	var currentWikiItFor;
	
	if( typeof(wikiItFor) != 'undefined' && wikiItFor == 'workPlanProject' ) {		// for workplan integration
		currentWikiItFor = wikiItFor;
		wikiSpaceId = spaceId;
		//wikiObjectId = scheduleView.getSelectedRowIds();
		wikiObjectId = getSelection(theForm);
	} else if( typeof(wikiItFor) != 'undefined' && wikiItFor == 'projectPortfolio' ) {		// for projectPortfolio integration
		currentWikiItFor = wikiItFor;
		wikiSpaceId = selectedProjectId;
		wikiObjectId = selectedProjectId;
	} else {																		// for myAssignment integration
		currentWikiItFor = wikiItFor;
		wikiSpaceId = taskSpaceId;
		wikiObjectId = assignmentTreeNodeId;
	}
	
	Ext.Ajax.request({
		url: JSPRootURL+'/pwiki/WikiAjaxHandler/getEditPageUrl?module='+moduleId,  
		params:{spaceId: wikiSpaceId, wikiPageName: selectedWikiSpacePageName, objectId: wikiObjectId, wikiItFor: currentWikiItFor},
		method :'POST',
		success: function(result, request){
			window.location.href = JSPRootURL+'/project/Main.jsp?id='+wikiSpaceId+'&page='+result.responseText+'&module='+moduleId;
		}
	});
}

// Handler function which is trigerd upon clicking on "Cancel" button //
function handleCancelWikiPagePreview() {
	// helper variables
	var wikiPageName;				// variable to hold the name of selected Wiki Page to edit
	var wikiSpaceId;					// id of current space
	
	if( typeof(wikiItFor) != 'undefined' && wikiItFor == 'workPlanProject' ) {		// for workplan integration
		var tab = wikiTabs.getItem(wikiContentPanel);
		wikiTabs.activate(tab);
		tab = wikiTabs.getItem(SHOW_PAGE_CONTENT_TAB);
		wikiTabs.hideTabStripItem(tab);
		disableWikiPanelButtons();
	} else {																		// for myAssignment integration
		wikiPageName = replaceAll(objectName, ' ', '_');								//replace all " " occurences with "_" in Wiki Page Names
		wikiSpaceId = taskSpaceId;
		loadWikiForAssignment(wikiPageName, wikiSpaceId);
	}
}

// Handler function which assigns selected wiki page to selected item //
// Same for workplan and myAssignment integration //
function handleAssignWikiPageToAssignment() {
	// helper variables
	var wPageName;				// variable to hold the name of selected Wiki Page to edit
	var wSpaceId;				// id of current space
	var wObjectId;				// id of selected object item
	
	// setting values from appropriate variables (set in "showPageContent" function)
	wPageName = replaceAll(selectedWikiSpacePageName, ' ', '_');						//replace all " " occurences with "_" in Wiki Page Names
	wSpaceId = wikiSpaceId;
	wObjectId = wikiObjectId;

	// call method for assigning wiki page with appropriate arguments
	//extAlert('Info', 'assignWikiPageToAssignmentWiki('+wSpaceId+', '+wObjectId+', '+wPageName+')', Ext.MessageBox.ERROR);	
	assignWikiPageToAssignmentWiki(wikiSpaceId, wikiObjectId, wPageName);
	
}

/**
 * Handler function for wikiTabs 'tabchange' event.
 * Made to hide menu bar on <b>tabchange</<b> event. The helper function <b>updateMenu</b>
 * should be as <i>callback</i> function, to set up menu bar upon retreiving conent. 
 */
function tabChangedHandler() {
	updateMenu();
}

function renderWikiWinHandler() {
	hideWikiMenu();																			// hide wiki menu when showing wikiWin
}

/*******************************	Wiki Menu Handlers - End *******************************/

/**
 *	Function to update wiki menu - display/hide it, and appropriately set its values
 */
function updateMenu() {
	if( typeof(wikiItFor) != 'undefined' && wikiItFor == 'workPlanProject' ) {			/* WORKPLAN INTEGRATION */
		if( wikiWin ) {
			if( wikiTabs.getActiveTab().getId() == WIKI_PAGE_CONTENT_TAB) {
				var wEContent = document.getElementById("wEContent");							// get "wEContent" div if exist
				if( !wEContent ) {
					hideWikiMenu();
				} else {
					disableWikiPanelButtons();
					toggleEditing();
					showWikiMenu();
				}
			} else if( wikiTabs.getActiveTab().getId() == SHOW_PAGE_CONTENT_TAB ) {
				showWikiMenu();
			}
			
			wikiWin.doLayout();
		}
	} else {																			/* MYASSIGNMENTS INTEGRATION */
		if( wikiPanel ) {
			var tb = wikiPanel.getBottomToolbar();
			tb.items.get('wikiMenuBtn').hide();
			wikiPanel.doLayout();
		}
	}
}

function hideWikiMenu() {
	// hide 'wikiMenuBtn'
	var tb = wikiWin.getBottomToolbar();
	tb.items.get('wikiMenuBtn').hide();
}

function showWikiMenu() {
	// show 'wikiMenuBtn'
	var tb = wikiWin.getBottomToolbar();
	tb.items.get('wikiMenuBtn').show();
}

// TODO:
function createWikiEditPanel() {
	// tab for display wiki content for editing
	// Create these explicitly so we can manipulate them later
    var wordCount = new Ext.Toolbar.TextItem('Words: 0');
    var charCount = new Ext.Toolbar.TextItem('Chars: 0');
    var clock = new Ext.Toolbar.TextItem('');
	
	wikiEditPagePanel = new Ext.Panel({
		id: 'wikiEditPagePanel',
		title: 'Edit Page',
	    width: 500,
        autoHeight: true,
        bodyStyle: 'padding:5px;',
        layout: 'fit',
        bbar: new Ext.Toolbar({
            id: 'word-status',
            items: [wordCount, ' ', charCount, ' ', clock, ' ']
        }),
        items: {
            xtype: 'textarea',
            id: 'word-textarea',
            enableKeyEvents: true,
            grow: true,
            growMin: 100,
            growMax: 200,
            listeners: {
                // After each keypress update the word and character count text items
                'keypress': {
                    fn: function(t){
                        var v = t.getValue(),
                            wc = 0, cc = v.length ? v.length : 0;
                            
                        if(cc > 0){
                            wc = v.match(/\b/g);
                            wc = wc ? wc.length / 2 : 0;
                        }
	                    Ext.fly(wordCount.getEl()).update('Words: '+wc);
                        Ext.fly(charCount.getEl()).update('Chars: '+cc);
	                },
                    buffer: 1 // buffer to allow the value to update first
                }
            }
        },
        listeners: {
            'render': {
                fn: function(){
                    // Add a class to the parent TD of each text item so we can give them some custom inset box 
                    // styling. Also, since we are using a greedy spacer, we have to add a block level element
                    // into each text TD in order to give them a fixed width (TextItems are spans).  Insert a
                    // spacer div into each TD using createChild() so that we can give it a width in CSS.
                    Ext.fly(wordCount.getEl().parentNode).addClass('x-status-text-panel').createChild({cls:'spacer'});
                    Ext.fly(charCount.getEl().parentNode).addClass('x-status-text-panel').createChild({cls:'spacer'});
                    Ext.fly(clock.getEl().parentNode).addClass('x-status-text-panel').createChild({cls:'spacer'});
                    
                    // Kick off the clock timer that updates the clock el every second:
				    Ext.TaskMgr.start({
				        run: function(){
				            Ext.fly(clock.getEl()).update(new Date().format('g:i:s A'));
				        },
				        interval: 1000
				    });
                }
            },
            'activate': handleEditActivate		// populate the text area with page content
        }
	});
}

// TODO: implement this function in order to make Editing UI as a part of wiki integration
function handleEditActivate(tab){
	// check does selected item has wiki page associated with it
	// populate textarea with 'wiki markup' content
    //tab.getUpdater().update({});
    // populate this tab with edit box
}

function tableOfContentClickHandler() {
	var wrapper = Ext.fly('toc');
	if( wrapper ) {
		wrapper.on("click", function(e){
		    var target = e.getTarget();
			var elId = replaceAll(target.innerHTML.trim(), ' ', '_');
			//var target = Ext.get(elId);
			//var wrapper = Ext.get('wEContent');
			//target.scrollIntoView(wrapper);
	        e.stopEvent();
		});
	}
	
	removeDetachImageLinks();
}

// Function for removing 'detachImage' links
function removeDetachImageLinks() {
	var wrapper = Ext.fly('attachedImages');
	if( wrapper ) {
	    var repper = true;
	    while (repper) {
	        var el = wrapper.child('.detachImage');
	        if( el ) {
	            el.remove();
	        } else {
	            repper = false;
	        }
	    }
	}
}

// Helper function to replace all occurences of strA with strB in given text
function replaceAll(text, strA, strB) {
    while ( text.indexOf(strA) != -1) {
        text = text.replace(strA,strB);
    }
    return text;
}

function createWikiPageFormScratch(){
	var wikiObjectId = 0;
	var currentWikiItFor;
	if( typeof wikiItFor!= 'undefined' && wikiItFor == 'workPlanProject' ) {		// for workplan integration
		currentWikiItFor = wikiItFor;
		wikiPageName = replaceAll(wikiObjectName, ' ', '_');							//replace all " " occurences with "_" in Wiki Page Names
		wikiSpaceId = spaceId;
		//wikiObjectId = scheduleView.getSelectedRowIds();
		wikiObjectId = getSelection(theForm);
	} else if( typeof wikiItFor!= 'undefined' && wikiItFor == 'projectportfolio' ) {		// for projectPortfolio integration
		currentWikiItFor = wikiItFor;
		wikiPageName = replaceAll(wikiObjectName, ' ', '_');							//replace all " " occurences with "_" in Wiki Page Names
		wikiSpaceId = selectedProjectId;
		wikiObjectId = selectedProjectId;
	} else {
		currentWikiItFor = wikiItFor;												// for myAssignment integration
		wikiPageName = replaceAll(objectName, ' ', '_');								//replace all " " occurences with "_" in Wiki Page Names
		wikiSpaceId = taskSpaceId;
		wikiObjectId = assignmentTreeNodeId;
	}
	if(wikiObjectId != 0){
		Ext.Ajax.request({
			url: JSPRootURL+'/pwiki/WikiAjaxHandler/getCreateObjectWikiPageURL?module='+moduleId, 
			params:{objectId: wikiObjectId, spaceId: wikiSpaceId, wikiPageName: wikiPageName, objectId: wikiObjectId, wikiItFor: currentWikiItFor},
			method :'POST',
			success: function(result, request){
				window.location.href = JSPRootURL+'/project/Main.jsp?id='+wikiSpaceId+'&page='+result.responseText+'&module='+moduleId;
			}
		});
	}
}

//  to show/hide the access permission block
function showAccessPermissionOptions(isShow) {
	if (isShow == true) {
		document.getElementById('accessPermissions').style.display = 'block';
		document.getElementById('changePermission').style.display = 'none';
		document.getElementById('hidePermission').style.display = '';
	} else {
		document.getElementById('accessPermissions').style.display = 'none';
		document.getElementById('changePermission').style.display = '';
		document.getElementById('hidePermission').style.display = 'none';
	}
}

function setPermission(setForId) {
	document.getElementById('permission').innerHTML = document.getElementById(setForId).innerHTML;
}

function validateWikiContent(){
	if(document.getElementById('wikiPageBasicContent') == null || document.getElementById('wikiPageBasicContent').value.trim() == '') {
		extAlert(errorAlertTitle, 'Wiki page content should not be blank.', Ext.MessageBox.ERROR);
		return false;
	}
	
	if( !isContentNotEmpty() ) {
		return false;
	}
	return true;
}

// Method written for deleting the image attachment
function deleteAttachment(wikiPageId, ownerObjectId, imageName){
	// find references to this image in other wiki pages, if any
	whatReferencesHereDialogBox(wikiPageId, ownerObjectId, imageName)
}
// function performing delition - displaying confirmation box first
function performDeleteAttachment(wikiPageId, ownerObjectId, imageName) {
	if(winWLH) {	winWLH.destroy();	}
	Ext.MessageBox.confirm(confirmMessage, sureToDeleteImageMessage, function(btn) {
		if( btn == 'yes' ) {
			Ext.Ajax.request({
				url: JSPRootURL +'/pwiki/WikiAjaxHandler/getPageReferences?module='+ moduleId,
				params: {module: moduleId, wikiPageId: wikiPageId, imageName: imageName, ownerObjectId: ownerObjectId, spaceId: spaceId},
		   		method: 'POST',
				success: function(result, request){
					// image references determined, perform actual image deletion
					Ext.Ajax.request({
						url: JSPRootURL +'/pwiki/WikiAjaxHandler/deleteAttachment?module='+ moduleId,
						params: {module: moduleId, wikiPageId: wikiPageId, imageName: imageName, ownerObjectId: ownerObjectId, spaceId: spaceId},
				   		method: 'POST',
						success: function(result, request){
							if(result.responseText == 'deleted successfully'){
								self.document.location = self.document.location;
							} else {
								extAlert(errorAlertTitle, deletingAttachmentFailedMessage, Ext.MessageBox.ERROR);
							}
						},
						failure: function(result, response){
							extAlert(errorAlertTitle, serverRequestFailed, Ext.MessageBox.ERROR);
						}
					});
				},
				failure: function(result, response){
					extAlert(errorAlertTitle, serverRequestFailed, Ext.MessageBox.ERROR);
				}
			});
			// end
		} else {
			return false;
		}
	});	
}

// show what references here in extJs dialog
var winWLH;
function whatReferencesHereDialogBox(wikiPageId, ownerObjectId, imageName) {
	var spaceId = ownerObjectId;
	var pageName = "Image:"+imageName;
	var myView;
	var myStore;
	
	myStore = new Ext.data.Store({
		autoLoad: true,
		proxy: new Ext.data.HttpProxy({
			url: JSPRootURL+'/pwiki/WikiAjaxHandler/whatLinksHere',
			method: 'POST'
		}),
		reader: new Ext.data.JsonReader({
			totalProperty: 'total',
			root: 'results',
			fields:['id', 'pageName', 'url']
		}),
        baseParams: { spaceId : spaceId, wikiPageName: pageName, module: moduleId }
	});
	
	myView = new Ext.DataView({
	    tpl: '<div id="whatLinkHereList" class="model_sec_body"><br/><br/>' +
		     	'<h1 class="model_sec_header">'+imgReferencesTitle+'</h1>' +
	          	'<tpl for=".">' + 										// Loop through the Array which DataView creates from the Store.
		          '<div class="model_sec_row">' +
			      	' <a id="{id}" wikiLink="{pageName}" style="padding-left:5px" href="{url}">{pageName}</a> <div style="float:right; padding-right:5px;">({#})</div> <br/>' +
		          '</div> <br/>' +
		          '<div id="imgNotDeletable">' 
		          	+ imgNotDeleteableMsg + 
		          '</div> <br/>' +
		      '</tpl>' +
	          '</div>',
	    itemSelector: '.model_sec_row',
	    store: myStore,
	    emptyText: '<div id="imgDeleteable"><h1>'+imgReferencesTitle+'</h1> <br/><br/>'+norefereces+'</div>',
	    loadingText: loadingImage
	});
	
	winWLH = new Ext.Window({
						width:400,
  						id:'wlhWindow',
						height:300,
						autoScroll:true,
						items: myView,
						title: imgReferencesTitle,
						text:	loadingImage,
			            buttons: [{
			                text: deleteButton,
			            	id:	'deleteBtn',
			            	disabled: true,
			            	handler: function(){
			            		performDeleteAttachment(wikiPageId, ownerObjectId, imageName);
			            	}
			            }, {
			                text: closeButton,
			                handler: function(){
			                	winWLH.destroy();
			                }			            	
			            }]
	});
	
	winWLH.show();
	
	// register store load handler
	myStore.on('load', function(store, records){
		if(store.reader.jsonData.total == 0) {
			Ext.getCmp('deleteBtn').enable();
		} else {
			Ext.getCmp('deleteBtn').disable();
			Ext.get('imgNotDeletable').show();
		}
	});
}

/**
 * Check does content has any data (other than special symbols) -  
 * if only special symbols are entered within content - content is considered as empty content.
 * @return {Boolean}
 */
function isContentNotEmpty() {
	var chars = 'abcdefghijklmnopqrstuvwxyz';
	var content = document.getElementById('wikiPageBasicContent').value;
	var found = false;
	for (i=0; i<chars.length; i++) {
		if( content.indexOf(chars[i]) > 0 ) {
			found = true;
		}
	}
	if( !found ) {
		extAlert(errorAlertTitle, 'Wiki page content can not contain only special symbols.', Ext.MessageBox.ERROR);
	}
	return found;
}

// Function for displaying submenu
function showWikiSubMenu(menuOpen) {
	if(menuOpen) {
		var menuPos = getElementAbsolutePos(document.getElementById('wikiMenu'));
		var leftDivWidth = (document.getElementById('slidingpanel-toolbar').style.width).replace("px", "");
		var rightDivWidth = (document.getElementById('rightTabSet').style.width).replace("px", "");
		document.getElementById('wikiSubMenu').style.left =	parseInt(menuPos.x + 5) + 'px';
		document.getElementById('wikiSubMenu').style.zIndex = '15000';
		document.getElementById('wikiSubMenu').style.top = ((wikiItFor == 'projectPortfolio') ? parseInt(menuPos.y - 83) : parseInt(menuPos.y - 108)) + 'px';  
		document.getElementById('wikiSubMenu').style.display = '';
		document.getElementById('wikiMenuLink').onclick = new Function('showWikiSubMenu(false)');
		document.getElementById('wikiMenuImg').onclick = new Function('showWikiSubMenu(false)');
		
	} else {
		document.getElementById('wikiSubMenu').style.zIndex = '14999';
		document.getElementById('wikiSubMenu').style.left = '-10000px';
		document.getElementById('wikiSubMenu').style.top = '-10000px';
		document.getElementById('wikiSubMenu').style.display = 'none';
		document.getElementById('wikiMenuLink').onclick = new Function('showWikiSubMenu(true)');
		document.getElementById('wikiMenuImg').onclick = new Function('showWikiSubMenu(true)');
	}
	resizeWikiMenu();
}

// Load wiki page content for the selected object i.e. task in the right "Wiki" tab
function getWikiPageForSelectedTask(taskName, workPlanId, spaceId, forType, listItemSelected, wikiTabContentDivId, wikiDivBodyId, wikiDivTopId) {
	document.getElementById('wikiDivTop').innerHTML = loadingMsg +' <img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	wikiObjectName = taskName;
	wikiSpaceId = spaceId;
	wikiObjectType = forType;
	wikiObjectId = workPlanId;
	Ext.Ajax.request({
		url: JSPRootURL+'/pwiki/wikiAjaxHandler/getPageContent?module='+moduleId, 
		params: {module: moduleId, spaceId: spaceId, objectId: workPlanId, objectName: taskName, objectType: forType},
		method: 'POST',
		success: function(result, request) {
			if(result.responseText.indexOf('AccessDenied.jsp') > 0 ) {
				loadAccessDenied(result.responseText);
				document.getElementById(wikiDivTopId).innerHTML =  wikiPagesNotFoundMessage + spaceName;					
			} else if(result.responseText != '') {
				document.getElementById(wikiDivBodyId).innerHTML =  '';					
				document.getElementById(wikiDivBodyId).innerHTML = result.responseText;
				updateHtmlMenu();
//				toggleHtmlEditing();
				toggleHtmlEditing(listItemSelected);
				changeWikiTabHeaderMessage('wikiDivTop', wikiPagesFoundMessage, wikiPagesNotFoundMessage, spaceName)
			}
		},
		failure: function(result, request) {
			document.getElementById(wikiDivBodyId).innerHTML = serverRequestFailed;
		}
	});		
	document.getElementById(wikiDivBodyId).style.height = (document.getElementById(wikiTabContentDivId).offsetHeight - document.getElementById(wikiDivTopId).offsetHeight) + 'px';
}

//Method for Access Denied page
function loadAccessDenied(url) {
	Ext.Ajax.request({
		   url: JSPRootURL + url,
		   params: {disableBackLink: true },
		   method: 'POST',
		   success: function(result, request){
		   		document.getElementById('wikiDivBody').innerHTML = result.responseText;
		   },
		   failure: function(result, request) {
				document.getElementById('wikiDivBody').innerHTML = serverRequestFailed;
		   }
	});
}

// Load the page content for the internal wiki page links
function showPageContentRightPane(spaceId, pageName, objectId, isPreview, wikiPgId) {
	var wikiDivBody = 'wikiDivBody';
	var wikiDivTop = 'wikiDivTop';
	if( spaceId == objectId) {											// If previewing 'project' - display Page Index 
		previewingProjectsPage = true;									// hide Edit/Assign buttons, enable 'Back to Page Index' button
		isPreview = 'projectPagePreview';
	} else {
		previewingProjectsPage = false;
	}
	
	if(typeof(wikiItFor) != 'undefined' && wikiItFor == 'projectPortfolio') {											// If previewing 'project' - display Page Index 
		wikiContentDivId = 'projectWikiDivRight';
		previewingProjectsPage = false;
	} else {
		wikiContentDivId = 'taskWikiDivRight';
	}
	document.getElementById(wikiDivTop).innerHTML = loadingMsg +' <img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	if(typeof wikiItFor != 'undefined' && wikiItFor == 'workplanProject') {
		Ext.Ajax.request({
			url: JSPRootURL+'/pwiki/WikiAjaxHandler/getPageContent?module='+moduleId,
			params: {spaceId: spaceId, objectType: 'project', objectName: pageName, objectId: spaceId, isPreview: isPreview, 
						wikiPageId: wikiPgId},	//objectId is set to spaceId in order to set important values for previewing project's space wiki pages
			method: 'POST',
			success: function(result, request) {
				if(result.responseText != '') {
					document.getElementById(wikiDivBody).innerHTML = '';
					document.getElementById(wikiDivBody).innerHTML = result.responseText;
					changeWikiTabHeaderMessage(wikiDivTop, wikiPagesFoundMessage, wikiPagesNotFoundMessage, pageName);
				}
			},
			failure: function(result, request) {
				document.getElementById(wikiDivId).innerHTML = serverRequestFailed;
			},
			callback: function(result, request) {
				if( previewingProjectsPage == true ) {	// Projects Page Index previewing
					disableHtmlWikiButtons();
					showAssignToSelectedHtmlButton();
				} else {
			    	updateHtmlMenu();
					toggleHtmlEditing();								// upon retreiving the content - check should "Edit" button be displayed
					showWikiHtmlButtons();
				}
			}
		});
	} else {
			wikiObjectName = pageName;
    		Ext.Ajax.request({
    			url: JSPRootURL+'/pwiki/WikiAjaxHandler/getPageContent?module='+moduleId,
			    params: {spaceId: spaceId, objectType: 'project', objectName: pageName, objectId: spaceId, isPreview: isPreview, 
						wikiPageId: wikiPgId},	//objectId is set to spaceId in order to set important values for previewing project's space wiki pages
			    success: function(result, request) {
					document.getElementById(wikiDivBody).innerHTML = '';
					document.getElementById(wikiDivBody).innerHTML = result.responseText;
					changeWikiTabHeaderMessage(wikiDivTop, wikiPagesFoundMessage, wikiPagesNotFoundMessage, pageName);
					if( previewingProjectsPage == true ) {	// Projects Page Index previewing
						disableHtmlWikiButtons();
						showProjectPageIndexHtmlButton();
					} else {
						updateHtmlMenu();
						toggleHtmlEditing();
						showWikiHtmlButtons();
					}
					if(typeof(wikiItFor) != 'undefined' && wikiItFor == 'projectPortfolio'){
						showProjectPageIndexHtmlButton();
						disableCancelWikiPageButton();
					}
					//tableOfContentClickHandler();				// TOC click handler
			   	},
			    failure: function(result, response){
				   document.getElementById(wikiDivId).innerHTML = serverRequestFailed;
			    }
    		});
	}
}

// Function for updating the html menus in right wiki tab
function updateHtmlMenu() {
	if( typeof(wikiItFor) != 'undefined' && (wikiItFor == 'workPlanProject' || wikiItFor == 'myAssignments') ) {			/* WORKPLAN INTEGRATION */
		var wEContent = document.getElementById("wEContent");							// get "wEContent" div if exist
		if( !wEContent ) {
			disableHtmlWikiButtons();
		} else {
			disableHtmlWikiButtons();
			toggleHtmlEditing();
		}
	}
}

// Function for cancelling the current wiki page preview
function cancelWikiPagePreview() {
	
	if( typeof(wikiItFor) != 'undefined' && wikiItFor == 'workPlanProject' ) {		// for workplan integration
		wikiObjectName = document.getElementById("tn_"+getSelection(theForm)).innerHTML;
		wikiSpaceId = spaceId;
		getWikiPageForSelectedTask(wikiObjectName, wikiObjectId, wikiSpaceId, wikiObjectType, false ,'taskWikiDivRight', 'wikiDivBody', 'wikiDivTop');
	} else {
		wikiObjectName = objectName;
		wikiSpaceId = taskSpaceId;
		loadWikiForAssignment(wikiObjectName, wikiSpaceId);
	}
}

function assignWikiPageToAssignment() {
	document.getElementById('wikiDivTop').innerHTML = loadingMsg +' <img align="absmiddle" src="'+JSPRootURL+'/images/default/grid/loading.gif" />';
	Ext.Ajax.request({
		url: JSPRootURL+'/pwiki/WikiAjaxHandler/assignWikiPageToObject?module='+moduleId,
		params: {module: moduleId , spaceId: wikiSpaceId, objectId:wikiObjectId, wikiPgName:selectedWikiSpacePageName},
		method: 'POST',
		success: function(result, request) {
			if( typeof(wikiItFor) != 'undefined' && wikiItFor == 'workPlanProject' ) {			// WORKPLAN INTEGRATION
				var wikiObjectRootPageName = replaceAll(wikiObjectName, ' ', '_');
				getWikiPageForSelectedTask(wikiObjectRootPageName, wikiObjectId, wikiSpaceId, 'task', true, 'taskWikiDivRight', 'wikiDivBody', 'wikiDivTop');
				updateHtmlMenu();
			} else {
				selectedTask = objectName;
				loadWikiForAssignment(objectName, taskSpaceId, false, true);
			}
		},
		failure: function(result, request) {
		}
	});
}

function disableHtmlWikiButtons(){
	if(document.getElementById('editWikiPage').style.color == 'black') {
		document.getElementById('editWikiPage').style.color = 'gray';
		document.getElementById('editWikiPage').style.cursor = 'help';
		document.getElementById('editWikiPage').onclick = new Function('function disable(){}');
	}
	if(document.getElementById("assignSelected") && document.getElementById("assignSelected") != null){
		if(document.getElementById('assignSelected').style.color == 'black') {
			document.getElementById('assignSelected').style.color = 'gray';
			document.getElementById('assignSelected').style.cursor = 'help';
			document.getElementById('assignSelected').onclick = new Function('function disable(){}');
		}
	}
	
	document.getElementById('editWikiPage').style.color = 'gray';
	document.getElementById('editWikiPage').style.cursor = 'help';
	document.getElementById('editWikiPage').onclick = new Function('function disable(){}');
	
	document.getElementById('cancelWikiPage').style.color = 'gray';
	document.getElementById('cancelWikiPage').style.cursor = 'help';
	document.getElementById('cancelWikiPage').onclick = new Function('function disable(){}');
}

function showAssignToSelectedHtmlButton() {
	document.getElementById('assignSelected').style.color = 'black';
	document.getElementById('assignSelected').style.cursor = 'pointer';
	document.getElementById('assignSelected').onclick = new Function('assignWikiPageToAssignment()');
}

function showProjectPageIndexHtmlButton() {
	document.getElementById('backPageIndex').style.color = 'black';
	document.getElementById('backPageIndex').style.cursor = 'pointer';
	document.getElementById('backPageIndex').onclick = new Function('backToProjectPageIndex()');
}

function disableProjectPageIndexHtmlButton() {
	document.getElementById('backPageIndex').style.color = 'gray';
	document.getElementById('backPageIndex').style.cursor = 'help';
	document.getElementById('backPageIndex').onclick = new Function('function disable(){}');
}

function enableEditWikiPageButton() {
	document.getElementById('editWikiPage').style.color = 'black';
	document.getElementById('editWikiPage').style.cursor = 'pointer';
	document.getElementById('editWikiPage').onclick = new Function('handleEditWikiPage()');
}

function disableEditWikiPageButton() {
	document.getElementById('editWikiPage').style.color = 'gray';
	document.getElementById('editWikiPage').style.cursor = 'help';
	document.getElementById('editWikiPage').onclick = new Function('function disable(){}');
}

// To enable cancle button in two pane view right side wiki tab - wiki menu
function enableCancelWikiPageButton(){
	document.getElementById('cancelWikiPage').style.color = 'black';
	document.getElementById('cancelWikiPage').style.cursor = 'pointer';
	document.getElementById('cancelWikiPage').onclick = new Function('cancelWikiPagePreview()');
}

// To disable cancle button in two pane view right side wiki tab - wiki menu
function disableCancelWikiPageButton(){
	document.getElementById('cancelWikiPage').style.color = 'gray';
	document.getElementById('cancelWikiPage').style.cursor = 'help';
	document.getElementById('cancelWikiPage').onclick = new Function('function disable(){}');
}
/** @param listItemSelected - is item(object) from the list(grid) clicked. This 
 * 	parameter is marking grid object's <b>initial</b> click. Necessary for unassign btn functioning */
function toggleHtmlEditing(listItemSelected) {
	var wEContent = document.getElementById("wEContent");							// get "wEContent" div if exist
	if(wEContent) {
		enableEditWikiPageButton();
	} else {
		disableEditWikiPageButton();
	}
	
	// handle assign/unassign btn update
	updateMenuBtns(listItemSelected);

}

function showWikiHtmlButtons() {
	if(document.getElementById("assignSelected") && document.getElementById("assignSelected") != null){
		document.getElementById('assignSelected').style.color = 'black';
		document.getElementById('assignSelected').style.cursor = 'pointer';
		document.getElementById('assignSelected').onclick = new Function('assignWikiPageToAssignment()');
	}	
	document.getElementById('cancelWikiPage').style.color = 'black';
	document.getElementById('cancelWikiPage').style.cursor = 'pointer';
	document.getElementById('cancelWikiPage').onclick = new Function('cancelWikiPagePreview()');
}

// Handler function for displaying project Page Index
function backToProjectPageIndex() {
	// helper variables
	var wikiPageName;					// variable to hold the name of selected Wiki Page to edit
	var wikiSpaceId;					// id of current space

	wikiObjectName = objectName;	// Uros: wikiObjectName holds name of objectSelected
	wikiPageName = replaceAll(wikiObjectName, ' ', '_');	//replace all " " occurences with "_" in Wiki Page Names
	wikiSpaceId = (wikiItFor == 'projectPortfolio') ? selectedProjectId : taskSpaceId;
	if(wikiItFor && wikiItFor == 'workplanProject'){
		getWikiPageForSelectedTask(wikiPageName, wikiObjectId, wikiSpaceId, 'task', false, 'taskWikiDivRight', 'wikiDivBody', 'wikiDivTop');
	} else if (wikiItFor && wikiItFor == 'projectPortfolio'){
		getWikiPageForSelectedProject(wikiPageName, wikiSpaceId, wikiSpaceId, true, false);
	} else {
		if(objectType == 'project') {
			loadWikiForAssignment(wikiPageName, wikiSpaceId, true);
		} else {
			loadWikiForAssignment(wikiPageName, wikiSpaceId);
		}
	}
}

function resizeWikiMenu() {
	if(document.getElementById('wikiSubMenu').style.display == '') {
		var menuPos = getElementAbsolutePos(document.getElementById('wikiMenu'));
		var leftDivWidth = (document.getElementById('slidingpanel-toolbar').style.width).replace("px", "");
		var rightDivWidth = parseInt((document.getElementById('rightTabSet').style.width).replace("px", ""));
		var wikiMenuWidth = parseInt((document.getElementById('wikiSubMenu').style.width).replace("px", ""));
		document.getElementById('wikiSubMenu').style.left =	parseInt(menuPos.x + 5) + 'px';
		if(wikiMenuWidth > rightDivWidth) {
			document.getElementById('wikiSubMenu').style.left =	'';
			document.getElementById('wikiSubMenu').style.right = (rightDivWidth - 50) + 'px';
		}
	}

}

// Mange height and width of window as per screen height and width
function manageWindowHeightWidrh() {
	var winHeight = getWindowHeight();
	var winWidth = getWindowWidth();
	document.getElementById('leftPageDiv').style.width = (winWidth - 242)/2 + 'px';
	document.getElementById('rightPageDiv').style.width = (winWidth - 242 )/2 + 'px';
	document.getElementById('leftPageDiv').style.height = (winHeight - 210 ) + 'px';
	document.getElementById('rightPageDiv').style.height = (winHeight - 210) + 'px';
}

// Manage scroll , if moved the scroll then other side scroll also moved
function moveLeftScroll(pagePosition) {
	if(pagePosition == 'left') {
		document.getElementById('rightPageDiv').scrollLeft = document.getElementById('leftPageDiv').scrollLeft;
		document.getElementById('rightPageDiv').scrollTop = document.getElementById('leftPageDiv').scrollTop;
	} else {
		document.getElementById('leftPageDiv').scrollLeft = document.getElementById('rightPageDiv').scrollLeft;
		document.getElementById('leftPageDiv').scrollTop = document.getElementById('rightPageDiv').scrollTop;
	}
}

// Manage the height and width of wiki edit difference window.
function diffForEditPage(){
	manageWindowHeightWidrh();
    var noOfRows = document.getElementById('leftTable').getElementsByTagName('tr');
    var diffWinHeight = noOfRows.length * 20;
    diffWinHeight = diffWinHeight + 20;
    if(diffWinHeight > 400){
		document.getElementById('leftPageDiv').style.height = 400 + 'px';
		document.getElementById('rightPageDiv').style.height = 400 + 'px';
	} else {
		document.getElementById('leftPageDiv').style.height = diffWinHeight + 'px';
		document.getElementById('rightPageDiv').style.height = diffWinHeight + 'px';
	}	
	document.getElementById('save').value = submitButtonCaption;
}

// Handle the submit(save wiki page content) the form on hitting enter key in IE
function setSubmitFor(e){
	if(navigator.userAgent.toLowerCase().indexOf("msie") >= 0) {
		var type = e ? e.target : window.event.srcElement;
		e =(window.event)? event : e;
		if(e.keyCode == 13) {
			document.getElementById('submitFor').value = 'save';
		}
	}	 
}

// Handle hide and display div of differece on edit wiki page
function showWikiPageDiff(){
	if(document.getElementById('pageDiff').style.display == 'none'){
		document.getElementById('pageDiff').style.display = '';
		document.getElementById('diffLink').innerHTML = hideDiffMsg;
	} else {
		document.getElementById('pageDiff').style.display = 'none';
		document.getElementById('diffLink').innerHTML = showDiffMsg;
	}
}



///////////////////////
function showUnassignToSelectedHtmlButton() {
	document.getElementById('assignSelected').style.color = 'black';
	document.getElementById('assignSelected').style.cursor = 'pointer';
	
	var tdElement = document.getElementById('assignSelected').getElementsByTagName('td')[0];
	tdElement.removeChild(tdElement.firstChild);
	tdElement.appendChild(document.createTextNode(unassignPageWikiMenuItem));
		
	document.getElementById('assignSelected').onclick = new Function('unassignWikiPageFromAssignment()');
}

function revertUnassignToSelectedHtmlButton() {
	var wContent = document.getElementById("wEContent");
	var createWikiFromScratch = document.getElementById("createWikiFromScratch");
	if(!wContent){
		return;
	}
	var tdElement = document.getElementById('assignSelected').getElementsByTagName('td')[0];
	tdElement.removeChild(tdElement.firstChild);
	tdElement.appendChild(document.createTextNode(assingPageWikiMenuItem));
	if(createWikiFromScratch != null){
		return;
	}
	document.getElementById('assignSelected').onclick = new Function('assignWikiPageToAssignment()');	
}

// unassing wiki page button handler
function unassignWikiPageFromAssignment() {
	
	if(wikiItFor  == 'workPlanProject') {
		wikiObjectId = objectId;
		wikiSpaceId = spaceId;
	} else {
		wikiObjectId = assignmentTreeNodeId;
		wikiSpaceId = taskSpaceId;
	}
	
	Ext.Ajax.request({
		url: JSPRootURL+'/pwiki/WikiAjaxHandler/unassignWikiPageFromObject?module='+moduleId,
		params: {module: moduleId , spaceId: wikiSpaceId, objectId:wikiObjectId, wikiPgName:selectedWikiSpacePageName},
		method: 'POST',
		success: function(result, request) {
			if(result.responseText == 'true') {
				if( typeof(wikiItFor) != 'undefined' && wikiItFor == 'workPlanProject' ) {			// WORKPLAN INTEGRATION
					var wikiObjectRootPageName = replaceAll(wikiObjectName, ' ', '_');
					getWikiPageForSelectedTask(wikiObjectRootPageName, wikiObjectId, wikiSpaceId, 'task', true, 'taskWikiDivRight', 'wikiDivBody', 'wikiDivTop');
//					updateHtmlMenu();
					//updateMenuBtns();
				} else {
					selectedTask = objectName;										// objectName: Personal Assignment page grid variable
					loadWikiForAssignment(selectedTask, taskSpaceId);
				}
//				TODO: if all OK - call revertUnassignToSelectedHtmlButton();	
			} else if(result.responseText == 'false') {
				document.getElementById('wikiDivBody').innerHTML = serverRequestFailed;
			}
		},
		failure: function(result, request) {
		}
	});
	
	// TODO: in ajax onsuccess handler - revert assign button and its handler to assign btn handler!!!

	revertUnassignToSelectedHtmlButton();	
}

/** 
 * Upon rendering menu, check what the assignButton label/handler should be
 * 
 * @param {} listItemSelected indicates that the item from the list is clicked.
 * So if displayed content has wEContent element, and clicked object type is 
 * assignment(task/form) - this element has wiki page assigned to it, so 
 * unasssing button should be displayed.
 */
function updateMenuBtns(listItemSelected){
	
	var wContent = document.getElementById("wEContent");
	var assignWButton = document.getElementById("assignSelected");
	var createWikiFromScratch = document.getElementById("createWikiFromScratch");
	
	if( wContent && (objectType=='task' || objectType=='form_data') && listItemSelected) {
	    if(assignWButton) {
	    	// page content is displayed
	    	// if listItemSelected is true item from the grid is selected (clicked)  
	    	// so convert assign btn to show unassign btn (because assigned page is displayed)
	    	if(createWikiFromScratch!=null){
	    		revertUnassignToSelectedHtmlButton();
	    	}
	    	else{
	    		showUnassignToSelectedHtmlButton();
	    	}
	    }
	} else if( !wContent && (objectType=='task' || objectType=='form_data') ) {
		// task/form selected - but no wEcontent is found - so page index is displayed for assignment
	    if(assignWButton) {
	    	// page content is displayed
	    	// if assign btn is disabled - this page is assigned 
	    	// so convert this btn to show unassign btn
			revertUnassignToSelectedHtmlButton();
	    }
	} else if( wContent && objectType=='project' ) {
		(wikiItFor == 'projectPortfolio') ? enableEditWikiPageButton() : showProjectPageIndexHtmlButton();
	}
}

// Remove special characters from page name
// These characters are same as defined for NOT_SUPPORTED_CHARACTERS_IN_WIKI_PAGE_NAME in WikiURLManager.java
function removeSpecialCharFromPageName(pageName){
	var  characterToAvoid = "~!@#$%^&*()',`/|;+=[]<>{}?\"&";
	for(var index = 0; index < pageName.length; index++){
	 	if(characterToAvoid.indexOf(pageName[index]) != -1){
	   		return false;
	 	}
	}
	return true;
}

// To change tab header message while loading wiki page in two pane view wiki tab.
function changeWikiTabHeaderMessage(headerDivId, WikiFoundMsg, WikiNotFoundMsg, objectName){
	if( document.getElementById("wEContent") || document.getElementById("projectPageIndex") )
		document.getElementById(headerDivId).innerHTML = msgFormat.format(WikiFoundMsg, objectName);
	else
		document.getElementById(headerDivId).innerHTML = msgFormat.format(WikiNotFoundMsg, objectName);
}
