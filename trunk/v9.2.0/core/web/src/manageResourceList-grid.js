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

function confirmDelete() {
	Ext.MessageBox.confirm('Confirm', 'This will delete Resource List as well as resources strored in it.'+
	 '<br />Are you sure you want to do that?', submitForm);
}

var Example3 = {
  init : function(){
  
    Ext.QuickTips.init();
   
	var xg = Ext.grid;	
	
	var sm = new xg.CheckboxSelectionModel({
		listeners: { rowselect: setId}
	});
	
	var reader = new Ext.data.ArrayReader({}, [  		          
			       {name: 'ResourceListId'},	
  		           {name: 'action'},
  		           {name: 'listName'},
                   {name: 'noResources'}              
			  ]);
			  
	var grid2 = new xg.GridPanel({
	    ds: new Ext.data.Store({
	        reader: reader,
	        data: xgdata2
	    }),
	    cm: new xg.ColumnModel([
	        {header: "", width: 0, sortable: false, locked:true, hidden:true, dataIndex: 'ResourceListId'}, 
	        {header: "", width: 60, sortable: false, locked:true, dataIndex: 'action', renderer: deleteAction}, 
			{id: 'listName', header: listColumnLabel, width: 180, sortable: true, locked:false, dataIndex: 'listName', renderer: viewAction},
			{header: resourcesColumnLabel, width: 80, sortable: true, dataIndex: 'noResources'}			
		]),
	    sm: sm,	    
	    width: 360,
	    height: 260,
	    frame: true,
	    title: 'Manage Lists',
	    iconCls: 'icon-grid',
	    renderTo: 'manageListGridPos' //document.body
	});
	
	function setId(sm, rowIndex, record){
		document.getElementById('hiddenResourceListId').value = record.data.ResourceListId;		
	}	
		
	function deleteAction(val){
		return '<a href="#" onclick="confirmDelete();"> Delete </a>';
	}
	
	function viewAction(val){		
		return '<a href="#" onclick="return submitForm(\'edit\');"> '+ val +' </a>';
	}
  } 
};

// Array data for the grids
var xgdata2 = Ext.grid.dummyData;
xgdata2 = manageResourceListGridData;

Ext.onReady(Example3.init, Example3);