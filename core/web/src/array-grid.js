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


var Example = {
    init : function(){
        // some data yanked off the web
        var myData = [        	
	        ['Roger Bly',,,,,,,,,],
			['Task A','Project X','8/3 12:00am','8/10 12:00am','8/1 12:00am','8/6 12:00am','30h','30h','0h',100],			
			['Task B','Project X','8/8 12:00am','8/13 12:00am','8/5 12:00am','8/14 12:00am','26h','18h','8h',69],
			['Task Alpha','Project Zebra','8/15 12:00am','8/25 12:00am','8/14 12:00am','8/23 12:00am','88h','31h','57h',35],
			['Document The Install Process','Documentation Planning','8/23 12:00am','9/7 12:00am','8/25 12:00am','8/6 12:00am','56h','56h','40h',58],			
			['Samual Jones',,,,,,,,,],
            ['Task C','Project X',,,,,,,,],	
            ['Task D','Project X',,,,,,,,],	
            ['Task Gamma','Project Zebra',,,,,,,,],	
            ['Bob Jones',,,,,,,,,],
            ['No assignments in time period',,,,,,,,,],
			[,,,,,,,,,,],
            ['Total',,,,,,'268h','191h','77h',] 
		];

        var ds = new Ext.data.Store({
		        proxy: new Ext.data.MemoryProxy(myData),
		        reader: new Ext.data.ArrayReader({}, [
                       {name: 'task_assign'},
                       {name: 'workspace'},
                       {name: 'planStart', type: 'date', dateFormat: 'n/j h:ia'},
                       {name: 'planFinish', type: 'date', dateFormat: 'n/j h:ia'},
                       {name: 'actStart', type: 'date', dateFormat: 'n/j h:ia'},
                       {name: 'actFinish', type: 'date', dateFormat: 'n/j h:ia'},
                       {name: 'totWork'},
                       {name: 'wrkComplete'},
                       {name: 'wrkRemaining'},
                       {name: 'pctComplete'},
                  ])
        });
        ds.load();

		// example of custom renderer function
        function italic(value){
            return '<i>' + value + '</i>';
        }

		// example of custom renderer function
        function change(val){
            if(val > 0){
                return '<span style="color:green;">' + val + '</span>';
            }else if(val < 0){
                return '<span style="color:red;">' + val + '</span>';
            }
            return val;
        }
		// example of custom renderer function
        function pctChange(val){
		    if(val > 0){
		        return '<span style="color:green;">' + val + '%</span>';
		    }else if(val < 0){
		        return '<span style="color:red;">' + val + '%</span>';
		    }
		    return val;
		}

		// the DefaultColumnModel expects this blob to define columns. It can be extended to provide
        // custom or reusable ColumnModels
        var colModel = new Ext.grid.ColumnModel([
			{id:'task_assign',header: "Task Assignment", width: 170, sortable: true, locked:false, dataIndex: 'task_assign'},
			{header: "Workspace", width: 75, sortable: true, dataIndex: 'workspace'},
			{header: "Planned Start", width: 75, sortable: true, renderer: Ext.util.Format.dateRenderer('m/d/Y'), dataIndex: 'planStart'},
			{header: "Planned Finish", width: 75, sortable: true, renderer: Ext.util.Format.dateRenderer('m/d/Y'), dataIndex: 'planFinish'},
			{header: "Actual Start", width: 75, sortable: true, renderer: Ext.util.Format.dateRenderer('m/d/Y'), dataIndex: 'actStart'},
			{header: "Actual Finish", width: 75, sortable: true, renderer: Ext.util.Format.dateRenderer('m/d/Y'), dataIndex: 'actFinish'},
			{header: "Total Work", width: 75, sortable: true, dataIndex: 'totWork'},
			{header: "Work Complete", width: 75, sortable: true, dataIndex: 'wrkComplete'},
			{header: "Work Remaining", width: 75, sortable: true, dataIndex: 'wrkRemaining'},			
			{header: "Work % Complete", width: 85, sortable: true, dataIndex: 'pctComplete'}
		]);


        // create the Grid
        var grid = new Ext.grid.Grid('grid-example', {
            ds: ds,
            cm: colModel,
            autoExpandColumn: 'task_assign'
        });
        
        var layout = Ext.BorderLayout.create({
            center: {
                margins:{left:3,top:3,right:3,bottom:3},
                panels: [new Ext.GridPanel(grid)]
            }
        }, 'grid-panel');

        grid.render();
        

        grid.getSelectionModel().selectFirstRow();
    }
};
Ext.onReady(Example.init, Example);