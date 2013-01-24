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

var heightFlag = false;
var grid;
Ext.onReady(function(){
	Ext.QuickTips.init();
	var xg = Ext.grid;	
    var ds = new Ext.data.Store({
        reader: new Ext.data.ArrayReader({}, [
			   {name: 'resource_id'},
               {name: 'resource'},
               {name: 'pctAllocatedMax', type: 'float'},
               {name: 'firstOverAllocation'}
          ])
    });
    ds.loadData(gridData);
        
    function assignPercentSign(val){
		return val + '%';
    }

	var sm = new xg.RowSelectionModel({
		lock: true
	});
   
    var colModel = new xg.ColumnModel([
	    new xg.RowNumberer(),
		{header: "Resource Id", width: 0, hidden: true, locked: true, dataIndex: 'resource_id'},
        {id:'resource', header: resourceColumnLabel, width: 240, fixed:true, locked:true, dataIndex: 'resource'},
        {header: percentAllocatedMaxColumnLabel, width: 240, locked:true,  dataIndex: 'pctAllocatedMax', renderer: assignPercentSign},
        {header: firstOverAllocationColumnLabel, width: 240, locked:true, dataIndex: 'firstOverAllocation'}
    ]);
    
    grid = new xg.GridPanel({
        el: 'overAllocatedResourcesGridPosition',
		sm: sm,
        ds: ds,
        cm: colModel,
        autoExpandColumn: 'resource',
		width : 700,
        height: 300,
        title: gridTitle
    });
    grid.render();
	heightFlag = true;
});

window.onresize = resizeGrid;
function resizeGrid() {
	var gridHeight = 0;
	var gridWidth = 0;
	if(heightFlag){
		if((navigator.userAgent.toLowerCase()).indexOf( "msie" ) != -1) {
			//IF IE
			if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
			//IE 6+ in 'standards compliant mode'
				gridWidth = document.documentElement.clientWidth;
				gridHeight = document.documentElement.clientHeight;
			} else {
			//IE 4 compatible
				gridWidth = document.body.clientWidth;
				gridHeight = document.body.clientHeight;
			}
		}else{
			//Non-IE
			gridWidth = window.innerWidth;
			gridHeight = window.innerHeight;
		}
		grid.setHeight(gridHeight/2);
		grid.setWidth(gridWidth-280);
	}
}
