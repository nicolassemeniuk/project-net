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
var Example2 = {
  init : function(){
  
    Ext.QuickTips.init();
   
	var xg = Ext.grid;	
	
	sm = new xg.CheckboxSelectionModel({
    		listeners: { 
    					 rowselect: setSelectedId,
    					 rowdeselect: setSelectedId 	
    					}
    	});
    	
	function setSelectedId(sm, rowIndex, record) {
					var str='';
    				var selectedData = new Array(sm.getSelections().length);
    				selectedData = sm.getSelections();
    				for(var i=0; i < sm.getSelections().length; i++){
    					str += selectedData[i].data.personId + ",";
    				}
  					document.getElementById('hiddenPersonIds').value = str;
  					//alert('Grid selections : '+str);
    }
    
	var reader = new Ext.data.ArrayReader({}, [  
				   {name: 'personId'},		          
			       {name: 'person'},
			       {name: 'type'},
			       {name: 'primary-role'}
			  ]);
	var grid2 = new xg.GridPanel({
	    ds: new Ext.data.Store({
	        reader: reader,
	        data: xgdata3
	    }),
	    cm: new xg.ColumnModel([
	        sm,	        
	        {header: "", width: 10, dataIndex: 'personId', hidden: true},
			{id:'person', header: personColumnLabel, width: 120, sortable: true, locked:false, dataIndex: 'person'},
			{header: typeColumnLabel, width: 100, sortable: true, dataIndex: 'type'},
			{header: primaryRoleColumnLabel, width: 100, sortable: true, dataIndex: 'primary-role'}			
		]),
	    sm: sm,
	    width: 380,
	    height: 260,
	    frame: true,
	    title: 'Select Resources',
	    iconCls:'icon-grid',
	    renderTo: 'resourceGridPosV8_5'  //document.body
	});
  }
};

// Array data for the grids
var xgdata3 = Ext.grid.dummyData;
xgdata3 = resourceGridData;

Ext.onReady(Example2.init, Example2);