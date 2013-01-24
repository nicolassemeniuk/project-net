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
 
var roleComboData =  [ ['0','Team Member'] ];					
var isUserPropertyEdited = false;
var isDisplayNameEdited = false;
var folderView = false;
var isSpaceTreeExpanded = false;
var spacesToAdd = '';
var spacesToRemove = '';

Ext.onReady(function(){
	Ext.QuickTips.init();
	getWindowHeightWidth();	
	/*Resource right Role List list in business tab*/

	var businessRoleCombo = new Ext.form.ComboBox({
	      store: new Ext.data.SimpleStore({ 
	   			fields: ['roleId', 'roleName'], 
	   			data : roleComboData
	   	  }),
	      width : 130,
	      shadow :true,
	      fieldLabel: 'Role',
	      valueField : 'roleId',
	      displayField:'roleName',
	      triggerAction: 'all',
	      mode: 'local',
	      emptyText : 'Select a role..',
	      listClass: 'x-combo-list-small',
	      editable : false,
	      selectOnFocus:true
	});
	
	/*Resoruce right Role List in Project tab*/
       
	var projectRoleCombo = new Ext.form.ComboBox({
	      store: new Ext.data.SimpleStore({ 
	   			fields: ['roleId', 'roleName'], 
	   			data : roleComboData
	   	  }),
	      width : 130,
	      valueField : 'roleId',
	      displayField:'roleName',
	      triggerAction: 'all',
	      mode: 'local',
	      emptyText : 'Select a role..',
	      listClass: 'x-combo-list-small',
	      editable : false,
	      selectOnFocus:true
	});

	/*Resoruce right Business Combo in Business tab*/
       
	var businessCombo = new Ext.form.ComboBox({
	      store: new Ext.data.SimpleStore({ 
	   			fields: ['businessId', 'businessName']
	   	  }),
	   	  //fieldLabel :'Business',
	      width : 150,
	      valueField : 'businessId',
	      displayField:'businessName',
	      triggerAction: 'all',
	      mode: 'local',
	      emptyText : 'Select a business..',
	      listClass: 'x-combo-list-small',
	      editable : false,
	      selectOnFocus:true
	});
	
	/*....Event handling Resoruce right Business Combo in Business tab*/
	
	businessCombo.addListener( 'select', function(combo, record, index){
		getBusinessRoleComboData(combo.value);
	});
	
	/*Resoruce right Project Combo in Project tab*/
       
       var projectCombo = new Ext.form.ComboBox({
	      store: new Ext.data.SimpleStore({ 
	   			fields: ['projectId', 'projectName']
	   	  }),
	      width : 150,
		  valueField : 'projectId',
	      displayField:'projectName',
	      triggerAction: 'all',
	      mode: 'local',
	      emptyText : 'Select a project..',
	      listClass: 'x-combo-list-small',
	      editable : false,
	      selectOnFocus:true
	});
	
	/*......Event handling Resoruce right Project Combo in Project tab*/
	
	projectCombo.addListener( 'select', function(combo, record, index){
		getProjectRoleComboData(combo.value);
	});
	
		/*Resoruce right Business Grid in Business tab*/
		
	var checkBusinessColumn = new Ext.grid.CheckboxSelectionModel({
						width:20
		    	});

    var businessGridReader = new Ext.data.ArrayReader({}, [
						       {name: 'business_id'},
					           {name: 'business_Name', type: 'string'},
					           {name: 'business_Type', type: 'string'},
					           {name: 'no_Project', type: 'int'},
					           {name: 'no_People', type: 'int'}
						    ]);
   
    var businessGrid  =  new Ext.grid.GridPanel({
   						id: 'business',
	                	title: 'Business',
	                	store: new Ext.data.Store({
						            reader: businessGridReader
						        }),
	        			columns: [
	        				checkBusinessColumn,
				            {width:   0, sortable: false, hidden: true,	dataIndex: 'business_id'},
			            	{header: 'Busniness Name', width: 300, sortable: false, locked: false, dataIndex: 'business_Name'},
	        			    {header: 'Busniness Type', width: 90, sortable: false, locked: false, dataIndex: 'business_Type'},
			    	        {header: 'Project', width: 90, sortable: false, locked: false, dataIndex: 'no_Project'},
			    	        {header: 'People', width: 90, sortable: false, locked: false, dataIndex: 'no_People'}
			        	],
			        	stripeRows: true,
			        	sm: checkBusinessColumn,
			        	bbar:[{text:'<b>Business:</b>'},
			        		  businessCombo,
			        		  '-',
			        		  {text:'<b>*Role:</b>'},
			        		  businessRoleCombo,
			        		  '-',
			        		  {
			        		  	text:'<b>Add</b>',
				        		handler : function(){
					        		if(isNaN(businessCombo.value)){
					        			 extAlert('Error', 'Select a business from a business list to assign a user', Ext.MessageBox.ERROR);
					        		}else{
					        			Ext.MessageBox.confirm('Message','Do you want to assign this user to all projects under selected business?',
											function(btn){
												addBusiness(businessCombo.value, businessRoleCombo.value, btn);
											})
					        		}
				        		}
				        	  },
					          '-',
				        	  {
				        	  	text:'<b>Remove</b>',
					        	handler : function(){
					        		if(checkBusinessColumn.getCount()== 0){
					        			extAlert('Error', 'Select a business(s) from above available businesses to remove from user profile', Ext.MessageBox.ERROR);
					        		}else{
						        		Ext.MessageBox.confirm('Confirm','Remove this business from selected resource profile?',
											function(btn){
												if(btn == 'yes'){
													deleteBusiness(checkBusinessColumn.getSelections());
												}else{
													return false;
												}
											})
						        	}
					        	}
			        		  }
			        		]
	    				});
	businessGrid.addListener('rowdblclick', function(grid, rowIndex, e){
		window.location.href = jspRootUrl+'/business/Main.jsp?id='+grid.getStore().getAt(rowIndex).get('business_id');
    });  				
	    				
	/*Resoruce right Project Grid in Project tab*/
	
	var checkProjectColumn = new Ext.grid.CheckboxSelectionModel({
						width:20
		    	});
		
	var projectGridReader = new Ext.data.ArrayReader({}, [
				           {name: 'project_id'},
				           {name: 'project_Name', type: 'string'},
				           {name: 'percent_complete', type: 'int'}
				        ]);
   
    var projectGrid  =  new Ext.grid.GridPanel({
	                	id:'project',
	                	title: 'Project',
	                	store: new Ext.data.Store({
						            reader: projectGridReader
						        }),
	        			columns: [
	        				checkProjectColumn,
				            {width:   0, sortable: false, hidden: true,	dataIndex: 'project_id'},
			            	{header: 'Project Name', width: 290, sortable: false, locked: false, dataIndex: 'project_Name'},
			    	        {header: 'Percent Complete', width: 280, sortable: false, locked: false, dataIndex: 'percent_complete'}
			        	],
			        	stripeRows: true,
			        	sm: checkProjectColumn,
			        	bbar:[{text:'<b>Project:</b>'},
				        		  projectCombo,
				        		  '-',
				        		  {text:'<b>*Role:</b>'},
				        		  projectRoleCombo,
				        		  '-',
				        		  {
				        		  	text:'<b>Add</b>',
					        		handler : function(){
						        		if(isNaN(projectCombo.value)){
						        			 extAlert('Error', 'Select a project from a project list to assign a user', Ext.MessageBox.ERROR);
						        		}else{
						        			addProject(projectCombo.value, projectRoleCombo.value);
						        		}
					        	  	}
					        	  },
					        		'-',
				        		  {
				        		  	text:'<b>Remove</b>',
					        		handler : function(){
					        		if(checkProjectColumn.getCount()== 0){
					        			extAlert('Error', 'Select a project(s) from above available projects to remove from user profile', Ext.MessageBox.ERROR);
					        		}else{
					        			Ext.MessageBox.confirm('Confirm','Remove this project from selected resource profile?',
										function(btn){
											if(btn == 'yes'){
												deleteProject(checkProjectColumn.getSelections());
											}else{
												return false;
											}
										})
						        	}
					        	  }
			        		  	}
			        		]
	    				});  
	    				
	projectGrid.addListener('rowdblclick', function(grid, rowIndex, e){
    	window.location.href = jspRootUrl+'/project/Dashboard?id='+grid.getStore().getAt(rowIndex).get('project_id');
    });     				

	/*Resoruce right User property in user tab GridPanel view*/
	
	var userPropertyGrid = new Ext.grid.EditorGridPanel({
		               	id:'user',
		               	title: 'User',
						width:600,
	                	store: new Ext.data.SimpleStore({
					        fields: [
					           {name: 'name', type: 'string'},
					           {name: 'value', type: 'string'}
					        ]
					    }),
	        			columns: [
			    	        {header: 'Name', width: 290, sortable: false, locked: false, dataIndex: 'name'},
			    	        {header: 'Value', width: 300, sortable: false, locked: false, dataIndex: 'value',
				    	        editor: new Ext.form.TextField({
								            allowBlank : true,
									        style: 'text-align:left'
									    }),
				    	         renderer: function (v, params, record){
				    	         	params.attr = 'ext:qtip = "Click to edit"';
				    	         	if(v == 'Deleted'){
				    	         		 return '<span style="color: red;" >' +v+ '</span>';
				    	         	}else {
				    	         		return v;
				    	         	}
				    	         }
			    	        }
			        	],
		        		stripeRows: true,
		        		clicksToEdit: 1,
		        		autoHeight: true,
		        		buttons:[ {text:'<b>Add New</b>',
		        				handler : function(){
	        						openForm();
		        				}},
		        				
		        				{text:'<b>Update</b>',
		        				handler : function(){
		        					updateResourceProperty();
		        				}	
		        			 }]
    				  });
    
	/*.... Event handling in Resoruce right User property in user tab GridPanel view*/
	
	userPropertyGrid.addListener('cellclick', function(grid , rowIndex, columnIndex, e){
		if(columnIndex == 1 && grid.getStore().getAt(7).get('value') != 'Active'){
			grid.getColumnModel().setEditable(columnIndex, false);
			extAlert('Error', 'You cannot edit an '+grid.getStore().getAt(7).get('value')+' user', Ext.MessageBox.ERROR);
		} else if(rowIndex==7 && columnIndex == 1){
			grid.getColumnModel().setEditable(columnIndex, false);
			extAlert('Error', 'You can not edit User Status', Ext.MessageBox.ERROR);
		} else if(columnIndex == 1){
			grid.getColumnModel().setEditable(columnIndex, true);
		}
	});
	userPropertyGrid.addListener('afteredit', function(e){
		if(e.row == 0 && e.column == 1){
			isDisplayNameEdited = true;
		}
		isUserPropertyEdited = true;
	});
	
	/*Resource tab user staus combo 
	var statusCombo = new Ext.form.ComboBox({
	      store: new Ext.data.SimpleStore({ 
	   			fields: ['abbr', 'status'], 
	   			data : [['A','Active'],['D','Deleted'],['U','UnRegistered']] 
	   	  }),
	      width : 90,
	      displayField:'status',
	      mode: 'local',
	      emptyText : 'Select a Status..',
	      editable : false,
	      selectOnFocus:true
	});*/
	
	/*......Event handling in Resource tab user staus combo 
	
	statusCombo.addListener( 'select', function(combo, record, index){
    	var ln = resourceLeftTab.root.childNodes.length;
    	var nodes = resourceLeftTab.root.childNodes;
	    for(var i = ln-1; i > -1 ; i--){
			resourceLeftTab.root.removeChild(nodes[i]);
	    }
	    for(var i = 0; i<_resourceList.length; i++){
	  		resourceLeftTab.root.appendChild(getNode(_resourceList[i]));
    	}
	    resourceLeftTab.render();
    });
    */
	
	/*Resource tab dirctory combo */

	var directoryCombo = new Ext.form.ComboBox({
	      store: new Ext.data.SimpleStore({ 
	   			fields: ['id', 'directory'],
				data: directoryListData
	   	  }),
	      //width : 90,
		  triggerAction: 'all',
		  valueField : 'id',
	      displayField:'directory',
	      mode: 'local',
	      emptyText : 'Select a directory..',
	      listClass: 'x-combo-list-small',
	      editable : false,
	      selectOnFocus:true
	});


	/*Resource Left tab  Tree view*/
	
	var resourceLeftTab = new Ext.tree.TreePanel({
						id: 'resources',
				        title: 'Resources',
						height : 390,
				        split: true,
				        rootVisible: false,
				        lines: false,
				        autoScroll: true,
				        root: new Ext.tree.TreeNode('Resoruce Viewer'),
				        collapseFirst: false,
				        tbar: [ directoryCombo 
								,
								'-'
								,
								{text: '<b>Search</b>',
								 handler: function (){
									 if(!isNaN(directoryCombo.value)){
									 	getDirctoryResource(directoryCombo.value);
									 }else{
									 	extAlert('Error', 'Please select a directory.', Ext.MessageBox.ERROR);
									 }
								 }
								}
								,
								'-'
								,
								{//icon: jspRootUrl+'/images/default/tree/tree-icon.gif',
								iconCls: 'tree-icon',

								 handler: function (){
									 if(!folderView){
										 renderResourcesWithFolder();	
										 folderView = true;
									 }else{
										  renderResourcesWithoutFolder();
										  folderView = false;
									 }
								 }
								}
								,
								'-'
						]
				    });
	
	
	/*.....Event handling in Left resource tab tree*/

    /*resourceLeftTab.addListener( 'click', function(node, e){
    	alert(node.id);
    	businessStore.loadData(businessList1);
    	businessGrid.getView().refresh();
    });*/
    
    resourceLeftTab.getSelectionModel().on({
        'beforeselect' : function(sm, node){
        },
        'afterselect' : function(sm, node){
        },
        'selectionchange' : function(sm, node){
        	if(node != null && !isNaN(node.id)){
				getResourceProperty(node.id);
			}
    	}
    });
	
	/*Role left tab panel Tree view*/
	
	var roleLeftTab = new Ext.tree.TreePanel({
					id:'role',
			        title:'Role',
			        split:true,
			        rootVisible:false,
			        lines:false,
			        autoScroll:true,
			        root: new Ext.tree.TreeNode('Role Viewer'),
			        collapseFirst:false
			    });
			    roleLeftTab.root.appendChild(new Ext.tree.TreeNode({ id : '0', text: '	Space Administrator'}));
			    roleLeftTab.root.appendChild(new Ext.tree.TreeNode({ id : '1', text: 'Power User'}));
			    roleLeftTab.root.appendChild(new Ext.tree.TreeNode({ id : '2', text: 'Team Member'}));

	/*Role Right Resource list in Resources tab*/		    

	resourceCombo = new Ext.form.ComboBox({
				      store: new Ext.data.SimpleStore({ 
				   			fields: ['abbr', 'resource'], 
				   			data : resourceList 
				   	  }),
				      width : 100,
				      displayField:'resource',
				      typeAhead: true,
				      mode: 'local',
				      triggerAction: 'all',
				      emptyText : 'Select a resource..',
				      listClass: 'x-combo-list-small',
				      editable : false,
				      selectOnFocus:true
				});
	
	/*Role Right Resources Tab*/
	
	var resourcesGrid = new Ext.grid.GridPanel({
		               	title: 'Resources',
	                	store: new Ext.data.SimpleStore({
					        fields: [
					           {name: 'resource_id'},
					           {name: 'resource_name', type: 'string'}
					        ]//,
					    	//data: resourceList
					    }),
	        			columns: [
			    	        {width:   0, sortable: false, hidden: true,	dataIndex: 'resource_id'},
			    	        {header: 'Resource Name', width: 590, sortable: false, locked: false, dataIndex: 'resource_name'}
			        		],
			        	stripeRows: true,
			        	bbar:[{text:'<b>Resources:</b>'},
			        		  resourceCombo,
			        		  '-',
			        		  {text:'<b>ADD</b>'}
			        		]
			        		
    				  });
		
		/*Resource right tab sapce tree */
	    var spaceTree = new Ext.tree.ColumnTree({
				        width:575,
				        id:'businessAndProject',
				        //autoHeight:true,
				        rootVisible:false,
				        autoScroll:true,
				        title: 'Business & Project',
				        columns:[
						{
				            header:'Space Name',
				            width:320,
				            dataIndex:'spaceName'
				        },{
							header:'Role',
							width: 220,
							dataIndex:'role'
						},{
							header:'#',
							width:35,
							dataIndex:'spaceId'
						}],
				      loader: new Ext.tree.TreeLoader({
				            //dataUrl: '/pnet/src/column-data.json',
				            dataUrl: jspRootUrl+'/resource/management/Directory/anyParameter&getSpaceData?module=310',
				            uiProviders: {
				                'col': Ext.tree.ColumnNodeUI
				            }
				        }),
				        root: new Ext.tree.AsyncTreeNode({
				            text:'Space'
				        }),
				        tbar:[ '-',toggleExpandCollapse = new Ext.Button({
				       				iconCls: 'expandall-icon',
				       				text:	'<b>&nbsp;Expand All</b>',
				       				tooltip: '<b>Expand all/ Collapse all</b>',
			        				handler : function(){
			        						if(!isSpaceTreeExpanded){
		        								spaceTree.expandAll();
		        								isSpaceTreeExpanded = true;
		        								toggleExpandCollapse.setIconClass('collapseall-icon');
												toggleExpandCollapse.setText('<b>&nbsp;Collapse All</b>');
		        							}else{
		        								spaceTree.collapseAll();
		        								isSpaceTreeExpanded = false;
		        								toggleExpandCollapse.setIconClass('expandall-icon');
												toggleExpandCollapse.setText('<b>&nbsp;Expand All</b>');
		        							}
			        					}
								}),
								'-',
								{text:'<b>Save</b>',
				        		iconCls: 'save',
		        				handler : function(){
	        							confirmAndsaveSpaceChanges();
		        					}
								},
								'-'
							]
				    });
				    
	
	/*left tab panel */
	    				
	var leftTabPanel = new Ext.TabPanel({
                   		region:'west',
                   		autoScroll:true,
	                    collapsible : true,
	                    split: true,
						deferredRender:false,
	                    activeTab:0,
       		            width:260,
               		    height : 390,
               		    items:[
               		        resourceLeftTab
	                    	,
	                    	roleLeftTab
                    	]
       		        });
       		        
	/*Right tab panels */
	
	var rightTabPanel =  new Ext.TabPanel({
		                    region:'center',
							deferredRender:false,
		                    activeTab:0,
		                    width:600,
		                    height : 390,
		                    items:[
			             		userPropertyGrid//tab 0
		                     	,
			                   	businessGrid//tab 1
			                   	,
			                   	projectGrid//tab 2
			                   	,
			                   	spaceTree//tab 3
			                   	,
			                   	{//tab 4
		                    	id: 'description',
	                        	title: 'Description',
	                        	html:'<p align="center"><b><h3>Development in progress for ROLES functionality! </h3></b></p>',
      				                autoScroll:true
			                    }
			                    //,resourcesGrid
		                    ]
		                });	      		        
	
	var directoryPanel = new Ext.Panel({
            resizable :true,
            height: windowHeight-130,
			width : windowWidth-210,
            layout: 'border',
            items: [leftTabPanel, rightTabPanel]
        });
	directoryPanel.render('directoryPanelPosition');
	
	window.onresize = resizeGrid;
		function resizeGrid() {
			getWindowHeightWidth(); 	
			try{
				leftTabPanel.setHeight(windowHeight-130);
				rightTabPanel.setHeight(windowHeight-110);
				directoryPanel.setHeight(windowHeight-130);
				directoryPanel.setWidth(windowWidth-210);
			   }catch(err){}
		}

    rightTabPanel.hideTabStripItem(1); //business tab  
	rightTabPanel.hideTabStripItem(2); //proejct tab
	rightTabPanel.hideTabStripItem(4); //role description tab
       
    leftTabPanel.addListener( 'tabchange', function(e, tab){
       	if(tab.id == 'resources'){
			rightTabPanel.unhideTabStripItem(0);//user tab
			//rightTabPanel.unhideTabStripItem(1);//business tab
			//rightTabPanel.unhideTabStripItem(2);//project tab
			rightTabPanel.unhideTabStripItem(3);//Business & Project tab
			rightTabPanel.hideTabStripItem(4);//role description tab
			rightTabPanel.setActiveTab('user');
		}
		else{
			rightTabPanel.unhideTabStripItem(4);
			rightTabPanel.hideTabStripItem(0);//user tab
			//rightTabPanel.hideTabStripItem(1);//business tab
			//rightTabPanel.hideTabStripItem(2);//Project tab
			rightTabPanel.hideTabStripItem(3);//Business and Project tab
			rightTabPanel.setActiveTab('description');//role description tab
		}
       });
       
       rightTabPanel.addListener( 'tabchange', function(e, tab){
       	displayNote(tab.id);
       });
       
	function openForm(){

        for(var i = 0 ; i<userPropertyGrid.buttons.length; i++ ){
			userPropertyGrid.buttons[i].disabled = true;
		}
		        						
		var textFirstName = new Ext.form.TextField({
							  fieldLabel: 'First Name',
				              name: 'first',
				              allowBlank:false,
							  blankText : 'Please enter First Name'
							});
							
		var textLastName = new Ext.form.TextField({
							  fieldLabel: 'Last Name',
				              name: 'last',
				              allowBlank:false,
							  blankText : 'Please enter Last Name'	              
							});
		
		var textEmail = new Ext.form.TextField({
							  fieldLabel: 'Email',
				              name: 'email',
				              vtype:'email',
				              allowBlank:false,
							  blankText : 'Please enter valid Email'	              
							});
		
		userForm = new Ext.form.FormPanel({
		        labelWidth: 75,
		        frame:true,
		        title: 'Add New Resource',
		        bodyStyle:'padding:5px 5px 0',
		        width: 350,
		        defaults: {width: 230},
		        defaultType: 'textfield',
		        items: [
			        	textFirstName
			        	,
			        	textLastName
			        	,
			        	textEmail
			        ],
		        buttons: [{
		            text: 'Save',
		            handler: function(){
		            			textLastName.isValid(false);
		            			textEmail.isValid(false);
			  	   				if( textFirstName.isValid(false)
				  	   				&& textLastName.isValid(false)
				  	   				&& textEmail.isValid(false)){
				  	   				
				  	   				for(var i = 0 ; i<userPropertyGrid.buttons.length; i++ ){
										userPropertyGrid.buttons[i].disabled = false;
									}
				  	   				addNewResource(textFirstName, textLastName, textEmail);
				  	   				userForm.hide();
		  	   				}
				    }
		        },{
		            text: 'Cancel',
		            handler: function(){
		            		for(var i = 0 ; i<userPropertyGrid.buttons.length; i++ ){
								userPropertyGrid.buttons[i].disabled = false;
							}
		  	   				userForm.hide();
				    }
		        }]
		});
		userForm.render('resourceFormDiv');
	}
	
	function getResourceProperty(resourceId){
    	//getting User Property
    	document.getElementById('messageDiv').innerHTML 
												= '<label style="color:red; font-weight:bold;background-color: #FFFF99;">Loading...</label>';
       	Ext.Ajax.request({
		   url: jspRootUrl+'/resource/management/Directory/'+ resourceId + '&getResourceProperty',
		   params: {module: 310},
		   method: 'POST',
		   success: function(result, request){
						resText = result.responseText.split('|&|');
	  	 	        	
	  	 	        	loadUserPropertyGridData(resText[0], resText[1], resText[2], resText[3], resText[4], resText[5], resText[6], resText[7]);
	  	 	        	//Old code: Currently no need to load these components.
			        	//loadBusinessGridData(resText[8]);
				    	//loadProjectGridData(resText[9]);
				    	//loadBusinessComboData(resText[10]);
				    	//loadProjectComboData(resText[11]);
				    	spaceTree.root.reload();
						if(isSpaceTreeExpanded){
							spaceTree.expandAll();
						}else{
							spaceTree.collapseAll();
						}
						spacesToAdd = '';
						spacesToRemove = '';
						
				    	document.getElementById('messageDiv').innerHTML = '';
		   },
		   failure: function(result, response){
		   	   document.getElementById('messageDiv').innerHTML ='';
			   extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		   }
		});
    }
    
    function addBusiness(businessId, roleId, isProjectToAdd){
		//Adding buinsess
		document.getElementById('messageDiv').innerHTML 
												= '<label style="color:red; font-weight:bold;background-color: #FFFF99;">Adding...</label>';
		if(isNaN(roleId)){
			roleId = 0;
		}										
		if(isProjectToAdd == 'yes'){
			strUrl = jspRootUrl+'/resource/management/Directory/' + businessId + ',' + roleId + '&addBusinessAndProject'
		}else{
			strUrl = jspRootUrl+'/resource/management/Directory/' + businessId + ',' + roleId + '&addBusiness'
		}
												
       	Ext.Ajax.request({
		   url: strUrl,
		   params: {module: 310},
		   method: 'POST',
		   success: function(result, request){
						resText = result.responseText.split('|&|');
					    if(resText.length > 1){
						    loadBusinessGridData(resText[0]);
							loadBusinessComboData(resText[1]);
				    	
					    	if(resText.length >= 4){
					    		loadProjectGridData(resText[2]);
								loadProjectComboData(resText[3]);
					    	}
					    	if(resText.length == 5 && resText[4]!=''){
					    		extAlert('Error', resText[4], Ext.MessageBox.ERROR);
					    	}
				    	}else{
				    		extAlert('Error', resText[0], Ext.MessageBox.ERROR);
				    	}
				    	document.getElementById('messageDiv').innerHTML = '';
		   },
		   failure: function(result, response){
		   	   document.getElementById('messageDiv').innerHTML = '';
			   extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		   }
		});
    }
    
    function deleteBusiness(businessRecords){
    	var businessIds = '';
    	for(var i=0;i<businessRecords.length;i++){
    		businessIds += businessRecords[i].data.business_id + ',';
    	}
		//deleting buinsess
		document.getElementById('messageDiv').innerHTML 
												= '<label style="color:red; font-weight:bold; background-color: #FFFF99;">Removing...</label>';
       	Ext.Ajax.request({
		   url: jspRootUrl+'/resource/management/Directory/'+businessIds+ '&deleteBusiness',
		   params: {module: 310},
		   method: 'POST',
		   success: function(result, request){
						resText = result.responseText.split('|&|');
					    
					    loadBusinessGridData(resText[0]);
						loadBusinessComboData(resText[1]);
				    	
				    	if(resText.length == 3 && resText[2] != ''){
				    		extAlert('Error', resText[2], Ext.MessageBox.ERROR);
				    	}
				    	document.getElementById('messageDiv').innerHTML = '';
		   },
		   failure: function(result, response){
		   	   document.getElementById('messageDiv').innerHTML = '';		   
			   extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		   }
		});
    }
    
    function addProject(projectId, roleId){
		//Adding businsess
		document.getElementById('messageDiv').innerHTML 
												= '<label style="color:red; font-weight:bold;background-color: #FFFF99;">Adding...</label>';
		if(isNaN(roleId)){
			roleId = 0;
		}
       	Ext.Ajax.request({
		   url: jspRootUrl+'/resource/management/Directory/' + projectId + ',' + roleId + '&addProject',
		   params: {module: 310},
		   method: 'POST',
		   success: function(result, request){
						resText = result.responseText.split('|&|');
					    if(resText.length > 1){
					    	loadProjectGridData(resText[0]);
							loadProjectComboData(resText[1]);
						}else{
							extAlert('Error', resText[0], Ext.MessageBox.ERROR);
						}
				    	document.getElementById('messageDiv').innerHTML = '';
		   },
		   failure: function(result, response){
		   	   document.getElementById('messageDiv').innerHTML = '';		   
			   extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		   }
		});
    }
    
    function deleteProject(projectRecords){
    	var projectIds = '';
    	for(var i=0;i<projectRecords.length;i++){
    		projectIds += projectRecords[i].data.project_id + ',';
    	}
		//deleting businsess
		document.getElementById('messageDiv').innerHTML 
												= '<label style="color:red; font-weight:bold; background-color: #FFFF99;">Removing...</label>';
       	Ext.Ajax.request({
		   url: jspRootUrl+'/resource/management/Directory/'+projectIds+ '&deleteProject',
		   params: {module: 310},
		   method: 'POST',
		   success: function(result, request){
						resText = result.responseText.split('|&|');
					    
				    	loadProjectGridData(resText[0]);
						loadProjectComboData(resText[1]);
				    	
				    	if(resText.length == 3 && resText[2] != ''){
				    		extAlert('Error', resText[2], Ext.MessageBox.ERROR);
				    	}
				    	document.getElementById('messageDiv').innerHTML = '';
		   },
		   failure: function(result, response){
		   	   document.getElementById('messageDiv').innerHTML = '';
			   extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		   }
		});
    }
    
    function getBusinessRoleComboData(businessId){
    	Ext.Ajax.request({
		   url: jspRootUrl+'/resource/management/Directory/'+businessId+ '&getBusinessRoleComboData',
		   params: {module: 310},
		   method: 'POST',
		   success: function(result, request){
				    	businessRoleCombo.store.loadData(Ext.util.JSON.decode(result.responseText));
				    	businessRoleCombo.setValue("Team Member");
		   },
		   failure: function(result, response){
			   extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		   }
		});
    }
    
    function getProjectRoleComboData(projectId){
    	Ext.Ajax.request({
		   url: jspRootUrl+'/resource/management/Directory/'+projectId+ '&getProjectRoleComboData',
		   params: {module: 310},
		   method: 'POST',
		   success: function(result, request){
				    	projectRoleCombo.store.loadData(Ext.util.JSON.decode(result.responseText));
				    	projectRoleCombo.setValue("Team Member");
		   },
		   failure: function(result, response){
			   extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		   }
		});
    }
    
    function updateResourceProperty(){
	    if(!isUserPropertyEdited){
	    	extAlert('Message', 'No any value edited to update', Ext.MessageBox.ERROR);
	    	return;
	    }

     	var gridStore = userPropertyGrid.getStore();

	    if(!validateUserDetail(gridStore)){
	    	return false;
	    }
	    if((gridStore.getAt(3).get('value').search(/[a-zA-Z]+/))> -1) {
	    	extAlert('Error', 'Invalid office phone number', Ext.MessageBox.ERROR);
	    	return false;
	    }
		
		if((gridStore.getAt(4).get('value').search(/[a-zA-Z]+/))> -1 ) {
	    	extAlert('Error', 'Invalid Mobile numbers', Ext.MessageBox.ERROR);
	    	return false;
	    }
	   	var resourceProperty = '[{ "displayName" : "'+ gridStore.getAt(0).get('value')
   								+'", "firstName" : "'+ gridStore.getAt(1).get('value')
   								+'", "lastName" : "'+ gridStore.getAt(2).get('value')
   								+'", "officePhone" : "'+ gridStore.getAt(3).get('value')
   								+'", "mobile" : "'+ gridStore.getAt(4).get('value')
   								+'", "fax" : "'+ gridStore.getAt(5).get('value')
   								+'", "email" : "'+ gridStore.getAt(6).get('value')+'"}]';
		
		//Updating Resource
		document.getElementById('messageDiv').innerHTML 
												= '<label style="color:red; font-weight:bold;background-color: #FFFF99;">Saving...</label>';   								
    	isUserPropertyEdited = false;
    	Ext.Ajax.request({
		   url: jspRootUrl+'/resource/management/Directory/'+resourceProperty+ '&updateResourceProperty',
		   params: {module: 310},
		   method: 'POST',
		   success: function(result, request){
						resText = result.responseText.split('|&|');
						document.getElementById('messageDiv').innerHTML = '';

						if(resText.length == 1 && resText[0] != ''){
							extAlert('Error', resText[0], Ext.MessageBox.ERROR);
						}else{
							loadUserPropertyGridData(resText[1], resText[2], resText[3], resText[4], resText[5], resText[6], resText[7], resText[8]);
							if(isDisplayNameEdited){
								replaceNodeText(resText[0], resText[1]);	
							}
						}
						
		   },
		   failure: function(result, response){
		   				document.getElementById('messageDiv').innerHTML = '';
			  			extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		   }
		});
    }
 	
	function addNewResource(firstName, lastName, email){
			var newUser = '[{ "firstName" : "'+ firstName.getValue()
	   						+'", "lastName" : "'+ lastName.getValue()
	   						+'", "email" : "'+ email.getValue()
	   						+'"}]';
	   	//Adding new resource
    	document.getElementById('messageDiv').innerHTML 
												= '<label style="color:red; font-weight:bold;background-color: #FFFF99;">Saving...</label>';				
	    	Ext.Ajax.request({
			   url: jspRootUrl+'/resource/management/Directory/'+newUser+ '&addNewResource',
			   params: {module: 310},
			   method: 'POST',
			   success: function(result, request){
			   				resText = result.responseText.split('|&|');

			   				document.getElementById('messageDiv').innerHTML = '';

					    	if(resText.length == 1 &&  resText[0] != ''){
					    		extAlert('Error', resText[0], Ext.MessageBox.ERROR);
					    	} else{
					    		resourceLeftTab.root.appendChild(getNode(resText[0]+ ',' + resText[1]));
					    		resourceLeftTab.root.childNodes[resourceLeftTab.root.childNodes.length-1].select();
					    		loadUserPropertyGridData(resText[1], resText[2], resText[3], resText[4], resText[5], resText[6], resText[7], resText[8]);
					    		loadBusinessGridData(resText[9]);
						    	loadProjectGridData(resText[10]);
						    	loadBusinessComboData(resText[11]);
						    	loadProjectComboData(resText[12]);					    	
						    }
	
			   },
			   failure: function(result, response){
		   		   document.getElementById('messageDiv').innerHTML = '';
				   extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
			   }
		});
	}
    
    function getDirctoryResource(directoryId){
    	Ext.Ajax.request({
		   url: jspRootUrl+'/resource/management/Directory/'+directoryId+ '&getDirctoryResource',
		   params: {module: 310},
		   method: 'POST',
		   success: function(result, request){
		   		resourceList = Ext.util.JSON.decode(result.responseText);
		   		if(folderView){
			   		renderResourcesWithFolder();
		   		} else{
		   			renderResourcesWithoutFolder();
		   		}
		   },
		   failure: function(result, response){
			   extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		   }
		});
    }
    
    function confirmAndsaveSpaceChanges(){
    	if(spacesToAdd == '' && spacesToRemove == ''){
    		extAlert('Error', 'No changes to save.', Ext.MessageBox.ERROR);
    		return;
    	}
    	var spaceChanges = '[{ ';
    	if(spacesToAdd != ''){
    		spaceChanges += '"spacesToAdd" : ['+ spacesToAdd+']';
    	}
    	if(spacesToRemove != ''){
	    	if(spacesToAdd != ''){
	    		spaceChanges += ',';
	    	}
    		spaceChanges += '"spacesToRemove" : ['+ spacesToRemove +']';
    	}
   		spaceChanges +=  ' }]';
   		if(spaceChanges.search('spacesToRemove')) {
   			Ext.MessageBox.confirm('Confirm','Are you sure?' , 
			function(btn){
				if(btn=='yes'){
					saveSpaceChanges(spaceChanges);
				}else{
					return false;
				}
        	});
   		}else {
   			saveSpaceChanges(spaceChanges);
   		}
    }
    
    function saveSpaceChanges(spaceChanges) {
    	//Saving changes
    	document.getElementById('messageDiv').innerHTML 
												= '<label style="color:red; font-weight:bold;background-color: #FFFF99;">Saving...</label>';				
    	
    	Ext.Ajax.request({
		   url: jspRootUrl+'/resource/management/Directory/'+spaceChanges+ '&saveSpaceChanges',
		   params: {module: 310},
		   method: 'POST',
		   success: function(result, request){
		   		resText = result.responseText;
		   		if(resText != ''){
		   			extAlert('Error', resText, Ext.MessageBox.ERROR);
		   		}
		   		spacesToAdd = '';
		   		spacesToRemove = '';
		   		spaceTree.root.reload();
		   		if(isSpaceTreeExpanded){
					spaceTree.expandAll();
				}else{
					spaceTree.collapseAll();
				}
		   		document.getElementById('messageDiv').innerHTML = '';
		   },
		   failure: function(result, response){
			   extAlert('Error', 'Server request failed please try again...', Ext.MessageBox.ERROR);
		   }
		});
    }
    
    function loadBusinessGridData(data){
    	//Loading businesses
    	businessGrid.getStore().loadData(Ext.util.JSON.decode(data));
    	businessGrid.getView().refresh();
    }

    function loadProjectGridData(data){
    	//Loading projects
    	projectGrid.getStore().loadData(Ext.util.JSON.decode(data));
    	projectGrid.getView().refresh();
    }

    function loadBusinessComboData(data){
    	//Loading business combo
		businessCombo.store.loadData(Ext.util.JSON.decode(data));
		businessCombo.setValue("Select a business..");
    }

    function loadProjectComboData(data){
    	//Loading project combo
    	projectCombo.store.loadData(Ext.util.JSON.decode(data));
    	projectCombo.setValue("Select a project..");
    }
    
    function loadUserPropertyGridData(dispalyName, firstName, LastName, officePhone, mobile, fax, email, status){
    	//Loading User Details
       	userPropertyGrid.getStore().loadData([
    					   ["<b>Display Name</b>", dispalyName],
		                   ["<b>First Name</b>", firstName],
		                   ["<b>Last Name</b>", LastName],
	           		       ["<b>Office Phone</b>", officePhone],
		                   ["<b>Mobile</b>", mobile],
		                   ["<b>Fax</b>", fax],
	   		               ["<b>Email</b>", email],
	   		               ["<b>User Status</b>", status]
	   		              ]);
		userPropertyGrid.getView().refresh();
    }
    
    function getNode(param){
		var attr = (""+param).split(',');
		return new Ext.tree.TreeNode({ id : attr[0], text: attr[1], icon: jspRootUrl+'/images/default/tree/user.gif'});
	}
    
    
    function replaceNodeText(id, name){
    	var nodeLength = resourceLeftTab.root.childNodes.length;
    	var nodes = resourceLeftTab.root.childNodes;
	    for(var i = 0; i<nodeLength ; i++){
	    	if(folderView){
	    		folderLeafLength = nodes[i].childNodes.length;
	    		folderLeaf = nodes[i].childNodes;
	    		for(var leafIndex = 0; leafIndex < nodeLength ; leafIndex++){
	    			if(folderLeaf[leafIndex].id == id){
			    		folderLeaf[leafIndex].setText(name);
			    		break;
					}
	    		}
	    		break;
	    	}
	    	if(nodes[i].id == id){
	    		nodes[i].setText(name);
	    		break;
			}
	    }
    }
    
    function renderResourcesWithoutFolder(){
		removeAllTreeNode();
		AddNodes(resourceList);
		resourceLeftTab.root.firstChild.select();
	}
	
	function removeAllTreeNode(){
		var nodeLength = resourceLeftTab.root.childNodes.length;
		if(nodeLength > 0){
			for(var i = nodeLength-1 ; i>=0 ; i--){
				resourceLeftTab.root.childNodes[i].remove();
		    }
    	}
	}
	
	function AddNodes(){
		for(var i = 0; i<resourceList.length; i++){
		    resourceLeftTab.root.appendChild(getNode(resourceList[i]));
	    }
	}
	
	function getNodeText(param){
		var attr = (""+param).split(',');
		return attr[1];
	}
	
	function renderResourcesWithFolder(){
		removeAllTreeNode();
		folderName = new Array( '#','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');
		var hasLeaf = false;
		for(var index = 0; index< folderName.length; index++){
			resourceLeftTab.folder = resourceLeftTab.root.appendChild(
					new Ext.tree.TreeNode({
						id: folderName[index],
						text: folderName[index],
						expanded: false
				})
			);
			hasLeaf = false;
			//countLeaf = 0;
			for(var resourceIndex = 0; resourceIndex<resourceList.length && index > 0; resourceIndex++){
			   if( getNodeText(resourceList[resourceIndex]).indexOf(folderName[index]) == 0 ||
					getNodeText(resourceList[resourceIndex]).indexOf(folderName[index].toLowerCase()) == 0){
					
					resourceLeftTab.folder.appendChild(getNode(resourceList[resourceIndex]));	
					//countLeaf ++ ;
					hasLeaf = true;
			   }
			}
			if(!hasLeaf){
				resourceLeftTab.root.lastChild.remove();
			}//else{
				//resourceLeftTab.root.lastChild.setText(folderName[index]+' ('+countLeaf+')');
			//}
    	}
	}
    
    // This function is for triming the spaces in values
	String.prototype.trim = function(){
	    a = this.replace(/^\s+/, '');
	    return a.replace(/\s+$/, '');
	}
    
    function validateUserDetail(gridStore){
    	if(gridStore.getAt(0).get('value').trim() == ''){
    		extAlert('Error', 'Display name is required field!', Ext.MessageBox.ERROR);
			return false;
    	}

    	if(gridStore.getAt(1).get('value').trim() == ''){
    		extAlert('Error', 'First name is required field!', Ext.MessageBox.ERROR);
			return false;
    	}

    	if(gridStore.getAt(2).get('value').trim() == ''){
    		extAlert('Error', 'Last name is required field!', Ext.MessageBox.ERROR);
			return false;
    	}
    	
    	var re = /.+@.+\..+/
		var result = re.test(gridStore.getAt(6).get('value'));
		if (result == false){
			extAlert('Error', 'Please enter valid email.', Ext.MessageBox.ERROR);
			return false;
		}
		return true;
    }
    
    //Rendering tree node
    renderResourcesWithoutFolder();
    
    //Default selection
    businessRoleCombo.setValue("Team Member");
    projectRoleCombo.setValue("Team Member");
   
   
});
  
function displayNote(tab){
   	if(tab == 'business'){
   		document.getElementById('roleNoteDiv').style.visibility = 'visible';
   		document.getElementById('projectNoteDiv').style.visibility = 'hidden';
   		document.getElementById('businessNoteDiv').style.visibility = 'visible';
   	}else if(tab == 'project'){
   		document.getElementById('roleNoteDiv').style.visibility = 'visible';
   		document.getElementById('businessNoteDiv').style.visibility = 'hidden';
   		document.getElementById('projectNoteDiv').style.visibility = 'visible';
   	} else if(tab == 'businessAndProject'){
   		document.getElementById('roleNoteDiv').style.visibility = 'visible';
   	} 
   	else{
   		document.getElementById('roleNoteDiv').style.visibility = 'hidden';
   		document.getElementById('businessNoteDiv').style.visibility = 'hidden';
   		document.getElementById('projectNoteDiv').style.visibility = 'hidden';
   	} 
}  
