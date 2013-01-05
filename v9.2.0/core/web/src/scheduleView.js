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
var that = null;
var toggleExpandCollapse, toggleTreeFlat, viewName, isScheduleGridIndented, collapsed = true;
var childTaskList = '';
var assignmentTreeNodeId;
function TreeGrid(){
    var _columns = new Array(24); // Contains the Grid's columns
    var _records = new Array(); //Contains the Grid's records

    var _treeGrid = null;
    var _store = null;
    var _selModel = null;
    var _colModel = null;
    
    this.getTreeGrid = function(){
        return _treeGrid;
    };
    
    this.setTreeGrid = function(treeGrid){
        _treeGrid = treeGrid;
    };

    this.getColumnModel = function(){
        return _colModel;
    };

    this.setColumnModel = function(cm){
        _colModel = cm;
    };
    
    this.getSelectionModel = function(){
        return _selModel;
    };

    this.setSelectionModel = function(sm){
        _selModel = sm;
    };
    
    this.getStore = function(){
        return _store;
    };
    
    this.setStore = function(store){
        _store = store;
    };
    
    this.getColumns = function(){
        return _columns;
    };
    
    this.getRecords = function(){
        return _records;
    };
    
    this.addColumn = function (index, id, dataIndex, header, hidden, renderer, width, editor, fixed, hideable, resizable, sortable, alignment){
        var column = {};
        if(null != id){
            column.id = id;
        }
        if(null != dataIndex){
            column.dataIndex = dataIndex;
        }
        if(null != header){
            column.header = header;
        }
        if(null != hidden){
            column.hidden = hidden;
        }
        if(null != renderer){
            column.renderer = renderer;
        }
        if(null != width){
            column.width = width;
        }
        if(null != editor){
           column.editor = editor;
        }
        if(null != fixed){
           column.fixed = fixed;
        }
        if(null != hideable){
           column.hideable = hideable;
        }
        if(null != resizable){
           column.resizable = resizable;
        }
        if(null != sortable){
           column.sortable = sortable;
        }
        if(null != alignment){
           column.align = alignment;
        }

        _columns[index] = column;
    };
    
    this.addRecord = function (mapping, name, type, dateFormat){
        var record = {};
        if(null != mapping){
            record.mapping = mapping;
        }
        if(null != name){
            record.name = name;
        }
        if(null != type){
            record.type = type;
        }
        if(null != dateFormat){
            record.dateFormat = dateFormat;
        }
        _records[_records.length] = record;
    };

    this.dateRenderer = function dateRenderer(value, element, record, rowIndex , colIndex){
        /*var dataIndex = that.getColumnModel().getDataIndex(colIndex);
        var result = "";
        if(record.data[dataIndex]) {
            result = record.data[dataIndex].format('M d,y');
        }
        return result;
        */
        if(value)
        	return months[value.getMonth()]+' '+ value.getDate() + ', ' + value.getFullYear();
    };

    this.timeRenderer = function timeRenderer(value, element, record, rowIndex , colIndex){
        var dataIndex = that.getColumnModel().getDataIndex(colIndex);
	    return value ? String.format('{0} {1}', changeLocale(record.data[dataIndex]), record.data[dataIndex + 'Units']) : '0 ' + record.data[dataIndex + 'Units'];
    };
    
	function changeLocale(value){
		return (value+'').replace('.', decimalSeparator);
	}
	
    this.percentRenderer = function percentRenderer(value, element, record, rowIndex , colIndex) {
        return value ? String.format('{0} {1}', value, '%') : '0 %';
    };

    this.extraImagesRenderer = function extraImagesRenderer(value, element, record, rowIndex , colIndex) {
        var result = "";
        if(record.data['ExternalTask'] == "1"){
            result += String.format('<img src="{0}/images/schedule/externalTask.gif" hspace="2" border="0"  title="{1}"/>', JSPRootURL, record.data['ETTP']);
            result += String.format('<input type="hidden" name="hi{0}" id="hi{0}" value="{1}" />', record.id, record.data["name"]);
        }
        if(record.data['hasDependencies'] == "1"){
            var doc = getXMLObject(record.data['DTP']);
            var info = Ext.DomQuery.select("/TaskDependencyList/PopupInfo", doc);
            var idsList = Ext.DomQuery.select("/TaskDependencyList/idlist", doc);
            var infoText = info[0].firstChild.nodeValue;
            var idsText = idsList[0].firstChild.nodeValue;
            result += String.format('<img src="{0}/images/schedule/dependency.gif" hspace="2" border="0"  onmouseover="hiLite1(\'{2}\');dPopup(\'{1}\');" onmouseout="unLite1(\'{2}\');"/>', JSPRootURL, infoText, idsText);
        }
        if(record.data['isDateConstrained'] == "1"){
            result += String.format('<img src="{0}/images/schedule/constraint.gif" hspace="2" border="0"  title="{1}" />', JSPRootURL, record.data['DCTP']);
        }
        if(record.data['hasAssignments'] == "1"){
            result += String.format('<img src="{0}/images/group_person_small.gif" hspace="2" border="0" onmouseover="aPopup(\'{1}\')" onmouseout="aClose();" />', JSPRootURL, record.data['ATP']);
        }
        if(record.data['CriticalPath'] == "1"){
            result += String.format('<img src="{0}/images/schedule/critical_path.gif" hspace="2" border="0" title="{1}" />', JSPRootURL, criticalPathTitle);
        }
        if(record.data['AfterDeadline'] == "1"){
            result += String.format('<img src="{0}/images/schedule/after_deadline.gif" hspace="2" border="0" title="{1}" />', JSPRootURL, record.data['ADTP']);
        }
        return result;
    };

    this.nameRenderer = function nameRenderer(value, element, record){
        var result = "";
        // Check this whether it is a milestone
        if(record.data['isMilestone'] == "1"){
            result = String.format('<img src="{0}/images/milestone.gif" height="10" width="10" border="0" />', JSPRootURL);
            result += ' ';
        }
        result += value;
        return result;
    };

    // "That" reference is needed to access the above functions from inside the Ext.onReady()
    that = this;
}

function hiLite1(idsText) {
    var recordSequence = getCurrentRecordIndexToHilite(idsText).split(',');
    for(var i = 0; i < recordSequence.length-1; i++) {
        var row = scheduleView.getTreeGrid().getView().getRow(recordSequence[i]);
        if(typeof(row) != 'undefined'){
	        if (row.className.search(/\browHighlight\b/) == -1) {
	            row.className += ' rowHighlight';
	        }
        }
    }
  
}

function unLite1(idsText) {
    var recordSequence = getCurrentRecordIndexToHilite(idsText).split(',');
    for(var i = 0; i < recordSequence.length-1; i++) {
        var row = scheduleView.getTreeGrid().getView().getRow(recordSequence[i]);
        if(typeof(row) != 'undefined'){
	        row.className = row.className.replace(/\b rowHighlight\b/, "");
	        row.className = row.className.replace(/\browHighlight\b/, "");
       }
    }
    dClose();
}

// get current record indenx to hilite and unlite
function getCurrentRecordIndexToHilite(idsText) {
 	var ids = idsText.split(',');
    var recordsIndex='';
    var records=new Array();
    for( var i = 0 ; i < scheduleView.getTreeGrid().getStore().getCount() ; i++ ){
    	for( var iterator = 0 ; iterator < ids.length ; iterator++){
    		if( scheduleView.getTreeGrid().getStore().getAt(i).data.id == ids[iterator] ){
				recordsIndex += i + ',';
    		}
    	}
    }
    return recordsIndex.trim();
}

TreeGrid.prototype.init = function init(renderTo, dataStoreUrl, id){
    isScheduleGridIndented = isIndentedView;
    if(isScheduleGridIndented) viewName = flatViewName;
    else viewName = indentedViewName;
        
    Ext.onReady(function(){
        // Create the data store    
         store = new Ext.ux.maximgb.treegrid.NestedSetStore({
            reader: new Ext.data.JsonReader({
            				id: id, 
            				root: 'data',
							totalProperty: 'total',
							successProperty: 'success'}, that.getRecords()),
           	url: dataStoreUrl,
            pruneModifiedRecords : true,
            listeners : {
                load: function( thisStore, records, options ) {
                    for(var i = 0; i < records.length; i++) {
                        var record = records[i];
                        if(record.data['treeVisibility'] && record.data['taskType'] == "summary") {
                            collapsed = false;
                            store.expandNode(record);
                        }
                    }
                    if (collapsed) {
                    	 toggleExpandCollapse.setIconClass('expandall-icon');
                    	 toggleExpandCollapse.setText('<b>&nbsp;' + expandAllTitle + '</b>');
                    } else {
                    	 toggleExpandCollapse.setIconClass('collapseall-icon');
						 toggleExpandCollapse.setText('<b>&nbsp;' + collapseAllTitle + '</b>');
                    }
					var r = that.getSelectionModel().getSelected();
					that.getSelectionModel().clearSelections();
                    if(options.params && options.params.all) {
                        //clear out the warnings
                        var messageTd = Ext.DomQuery.selectNode('td.warningMessage');
                        if(messageTd) {
                            var tableElt = Ext.get(messageTd).parent().parent();
                            Ext.destroy(tableElt);
                        }
                    }
                    
                     //grid reloaded and refresh the surrounding components too 
					 var selectedSpans = Ext.DomQuery.select('div#ddClosed table tr.tableFilterContentWB td>span.tableFilterHeaderWB:next(span)'); 
					 Ext.Ajax.request({ 
					         url:JSPRootURL + '/ajax/schedule/WorkplanAction', 
					         params: {workplanInfo : true}, 
					         method: 'POST', 
					         success: function(response, opts){ 
					             var workplan = Ext.util.JSON.decode(response.responseText); 
					             for(var xx = 0; xx < selectedSpans.length; xx++) { 
					                 var next = Ext.get(selectedSpans[xx]).next('span', true); 
					                 next.innerHTML = workplan.workplanInfo[xx]; 
					             } 
					         }, 
					         failure: function(response, opts){ 
					         } 
					 });
                },
                expandnode: function (grid, record) {
                    Ext.Ajax.request({
                        url:JSPRootURL + '/servlet/ScheduleController/Main/StoreTreeViewSettings',
                        params: {module : moduleId,
                                 name: "node"+record.id+"expanded",
                                 value: true},
                        method: 'POST'
                    });
                },
                collapsenode: function (grid, record) {
                    Ext.Ajax.request({
                        url:JSPRootURL + '/servlet/ScheduleController/Main/StoreTreeViewSettings',
                        params: {module : moduleId,
                                 name: "node"+record.id+"expanded",
                                 value: false},
                        method: 'POST'
                    });
                }
            }
        });

		schedulePagingToolBar= new Ext.ux.maximgb.treegrid.PagingToolbar({
						      	store: store,
						      	displayInfo: true,
						      	displayMsg: '',
						      	emptyMsg: '',
						      	pageSize: schedulePageSize
							});
        //Create the selection model
        var taskSelectionModel = new Ext.grid.RowSelectionModel( {listeners : {'rowselect' : onGridRowSelect} });
        
        // Column model definition
        var colModel = new Ext.grid.ColumnModel(that.getColumns());
        colModel.on({
            columnmoved : function(columnModel, oldIndex, newIndex ) {
                Ext.Ajax.request({
                    url:JSPRootURL + '/ajax/schedule/WorkplanAction',
                    params: {columnView : columnModel.getDataIndex(newIndex),
                             newIndex: newIndex},
                    method: 'POST',
                    success: function(response, options){
                        that.getStore().reload({params:{all: true}});
                    },
                    failure: function(response, options){
                        var obj = Ext.util.JSON.decode(response.responseText);
                        extAlert(errorTitle, obj.errors , Ext.MessageBox.ERROR);
                    }
                });
            },
            hiddenchange : function(columnModel, columnIndex, hidden ) {
                Ext.Ajax.request({
                    url:JSPRootURL + '/ajax/schedule/WorkplanAction',
                    params: {columnView : columnModel.getDataIndex(columnIndex),
                             hidden: hidden},
                    method: 'POST',
                    success: function(response, options){
                        that.getStore().reload({params:{all: true}});
                    },
                    failure: function(response, options){
                        var obj = Ext.util.JSON.decode(response.responseText);
                        extAlert(errorTitle, obj.errors , Ext.MessageBox.ERROR);
                    }
                });
            }
        });
        
        // create the Grid
        var grid = new Ext.ux.maximgb.treegrid.GridPanel({
              store: store,
              clicksToEdit: 1,
              master_column_id : 'name',
              cm: colModel,
              sm:taskSelectionModel,
              renderTo: renderTo,
              stripeRows: true,
              autoHeight: true,
              autoExpandColumn: 'name',
              autoScroll: true,
              loadMask: true,
              title: '',
              root_title: 'Tasks',
              tbar:[
                toggleTreeFlat = new Ext.Button({
                    iconCls: 'flat-icon',
                    text : '<b>' + viewName + '</b>',
                    tooltip:'<b>' + indentedViewName + '/' + flatViewName + '</b>',
                    handler: function(){    
                        if(isScheduleGridIndented) {
                            changeView(0);
                            initializeTreeFlatToggleButtons('flat');
                            isScheduleGridIndented = false;
                        } else {
                            changeView(1);
                            initializeTreeFlatToggleButtons('indented');
                            isScheduleGridIndented = true;
                        }
                    }
                }), '-',
                toggleExpandCollapse = new Ext.Button({
                    iconCls: 'expandall-icon',
                    text:    '<b>'+collapseAllTitle+'<b>',
                    tooltip: '<b>' + expandAllTitle + '/' + collapseAllTitle + '</b>',
                    handler : function(){
                        if(collapsed){
                            expand_all();
                            toggleExpandCollapse.setIconClass('collapseall-icon');
                            toggleExpandCollapse.setText('<b>&nbsp;' + collapseAllTitle + '</b>');
                            collapsed = false;
                        } else {
                            collapse_all();
                            toggleExpandCollapse.setIconClass('expandall-icon');
                            toggleExpandCollapse.setText('<b>&nbsp;'+ expandAllTitle + '</b>');
                            collapsed = true;
                        }
                    }
                }), '-',
                new Ext.Button({
                    text:   '<b>' + save + '</b>',
                    handler : function(){ 
                        var records = that.getStore().getModifiedRecords();
                        var idList = '';
                        for(var i=0;i<records.length;i++) {
                            idList += records[i].id;
                            if(i < records.length -1)
                                idList +=',';
                        }
						if(idList == '') {
							extAlert(errorTitle, noModifiedTasksMsg , Ext.MessageBox.ERROR);
							return;
						}
                        Ext.Ajax.request({
                            url:JSPRootURL + '/ajax/schedule/WorkplanAction',
                            params: {saveAll : true, idList: idList},
                            method: 'POST',
                            callback : function(options, success, response){
                                if(success) {
                                    var res = Ext.util.JSON.decode(response.responseText);
                                    if(!res.success) {
                                        if(res.errors) {
                                            extAlert(errorTitle, res.errors , Ext.MessageBox.ERROR);
                                        }
                                    } else {
										that.getStore().commitChanges();
                                        that.getStore().reload({params:{all: true,start:readCookie('scheduleGridOffset_'+scheduleUserId),limit:schedulePageSize}});
                                    }
                                } else {
                                    extAlert(errorTitle, generalError , Ext.MessageBox.ERROR);
                                }
                            }
                        });
                    }
                }) , '-',
                new Ext.Button({
                    text:   '<b>' + cancel + '</b>',
                    handler : function(){ 
                        Ext.Ajax.request({
                            url:JSPRootURL + '/ajax/schedule/WorkplanAction',
                            params: {cancelAll : true},
                            method: 'POST',
                            callback : function(options, success, response){
								that.getStore().rejectChanges();
                                that.getStore().reload();
                            }
                        });
                    }
                    
                })
              ], viewConfig : {
                    enableRowBody : true,
                    forceFit: true
              }, listeners: {
                    validateedit: function(e) {
                        var originalValue = e.originalValue;
                        var value = (e.field=='name' ? e.value.trim() : e.value);
                        if (e.originalValue instanceof Date ) {
                            value = e.value.format('n/j/y');
                            originalValue = e.originalValue.format('n/j/y');
                        }
						if (typeof e.originalValue == "number") {
							value = (value+'').replace('.', decimalSeparator);
                            originalValue = (originalValue+'').replace('.', decimalSeparator);
						}
                        Ext.Ajax.request({
                            url:JSPRootURL + '/ajax/schedule/WorkplanAction',
                            params: {editField : e.field,
                                     id: e.record.id,
                                     oldValue: originalValue,
                                     newValue: value},
                            method: 'POST',
                            callback : function(options, success, response){
                                if(success) {
                                    var res = Ext.util.JSON.decode(response.responseText);
                                    if(!res.success) {
                                        if(res.errors) {
                                            extAlert(errorTitle, res.errors , Ext.MessageBox.ERROR);
                                        }
                                    } else {
                                        if(res.data) {
                                            //array of data need to set everything
                                            var store = e.grid.getStore();
                                            for(var i=0; i< res.data.length; i++) {
                                                var rec = res.data[i];
                                                var record = store.getById(rec[0]);
                                                if(record) {
                                                    if(record.data['startDate']) {
                                                        var newDate = Date.parseDate(rec[1], 'n/j/y');
                                                        if(rec[1] != record.get('startDate').format('n/j/y'))
                                                            record.set('startDate', newDate);
                                                    } if(record.data['endDate']) {
                                                        var newDate = Date.parseDate(rec[2], 'n/j/y');
                                                        if(rec[2] != record.get('endDate').format('n/j/y'))
                                                            record.set('endDate', newDate);
                                                    } if(record.data['work'] || record.data['work'] == 0) {
                                                        if(rec[3] != record.get('work'))
                                                            record.set('work',rec[3]);
                                                    } if(record.data['workComplete'] || record.data['workComplete'] == 0) {
                                                        if(rec[4] != record.get('workComplete'))
                                                            record.set('workComplete',rec[4]);
                                                    } if(record.data['duration'] || record.data['duration'] == 0) {
                                                        if(rec[5] != record.get('duration'))
                                                            record.set('duration',rec[5]);
                                                    } if(record.data['workPercentComplete'] || record.data['workPercentComplete'] == 0) {
                                                        if(rec[6] != record.get('workPercentComplete'))
                                                            record.set('workPercentComplete',rec[6]);
                                                    }
                                                }
                                            }
                                        } else {
                                             e.record.set(e.field, (e.field=='name' ? e.value.trim() : e.value ));
                                         }
                                        delete e.cancel;
                                        grid.fireEvent("afteredit", e);
                                    }
                                } 
                            }
                        });
                        return false;
                    },
                    celldblclick : function ( grid, rowIndex, columnIndex, e ) {
                        var record = that.getStore().getAt(rowIndex);
                        if(record) {
                            self.location = JSPRootURL + "/servlet/ScheduleController/TaskView?module=60&action=1&id=" + record.id;
                        }
                    }
              }
        });
        toggleExpandCollapse.setDisabled(isFlatView);   
       
        that.setColumnModel(grid.getColumnModel()); 
        that.setSelectionModel(grid.getSelectionModel());     
        that.setStore(grid.getStore());             
        that.setTreeGrid(grid);
        if(schedulePageOffset != ''){
        	schedulePagingToolBar.doLoad(schedulePageOffset);
        } else {
        	schedulePagingToolBar.doLoad(0);
        }
     });
};

function onGridRowSelect(thisSelectionModel, rowIndex, record) {
	assignmentTreeNodeId = that.getStore().getAt(rowIndex).id;
    childTaskList = assignmentTreeNodeId;
	if( isBlogEnabled ){
 		loadBlogEntriesForAssignment('', false, assignmentTreeNodeId, childTaskList, spaceId);
 	}
}

// Method for toggling button between flat and tree view
function initializeTreeFlatToggleButtons(viewType){
    if(viewType == 'flat'){
        toggleTreeFlat.setIconClass('tree-icon');
        toggleTreeFlat.setText('<b>' + indentedViewName + '</b>');
        toggleExpandCollapse.setDisabled(true);     
    } else {
        toggleTreeFlat.setIconClass('flat-icon');
        toggleTreeFlat.setText('<b>' + flatViewName + '</b>');      
        toggleExpandCollapse.setDisabled(false);
    }
}

TreeGrid.prototype.getRecordById = function getRecordById(id){
    var result = null;
    if(null != id){
        var store = this.getStore();
        result = store.getById(id);
    }
    return result;
}

TreeGrid.prototype.getSelectedRowId = function getSelectedRowId(){
    // Selected records are driven by highligthed grid rows
    var selectionModel = this.getSelectionModel();
    var rowCount = selectionModel.getCount();
    
    // First, test at least one row was selected
    if(rowCount == 0){
        return -1;
    }
    
    return selectionModel.getSelected().id;
};


TreeGrid.prototype.getSelectedRowIds = function getSelectedRowIds(){
    // Selected records are driven by highligthed grid rows
    var selectionModel = this.getSelectionModel();
    var rowCount = selectionModel.getCount();
    
    // Test at least one row was selected
    if(rowCount == 0){
        return [];
    }
    
    var store = this.getStore();
    var ids = new Array();
    store.each(function (r) {
        if(selectionModel.isSelected(r))
            ids[ids.length] =r.id;
    });
    return ids;
};

TreeGrid.prototype.getSelectedRowIdsOnly = function getSelectedRowIdsOnly(){
    var selectionModel = this.getSelectionModel();
    var rowCount = selectionModel.getCount();
    
    // Test at least two rows were selected
    if(rowCount <= 1){
        return [];
    }
    
    var store = this.getStore();
    var ids = new Array();
    store.each(function (r) {
        if(selectionModel.isSelected(r))
            ids[ids.length] =r.id;
    });
    return ids;
};


TreeGrid.prototype.getEditor = function getEditor(type){
    if(type == 'startDate' || type == 'endDate')
        return new Ext.form.DateField({format: 'n/j/y', allowBlank: false});
    else if(type == 'name')
        return new Ext.form.TextField({allowBlank: false});
    else if(type == 'workPercentComplete')
        return new Ext.form.NumberField({allowBlank: false, allowNegative: false, maxValue: 100, minValue: 0, decimalSeperator: decimalSeparator});
    else
        return new Ext.form.NumberField({allowBlank: false, allowNegative: false, decimalSeparator: decimalSeparator});
};

jQuery.noConflict();
jQuery(document).ready(function($) { 
var handleClick = function handleClick(e){
	executed = true;
	var store = that.getStore();
	if(store.getModifiedRecords().length > 0) {
		var c = confirm(scheduleModifiedMessage); 
		if (!c) { 
			executed = false;
			e.preventDefault();
			e.stopPropagation();
		}
	}
};

$('a').click(handleClick);
});

window.onbeforeunload = function(e){
	if(!executed){
		var store = that.getStore();
		if(store.getModifiedRecords().length > 0) {
			return modifiedMessage;
		}
	}else{
		executed = true;
	}
};
