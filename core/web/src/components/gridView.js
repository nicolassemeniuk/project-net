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

function GridView(){
    var _buttons = new Array(); // Contains all buttons to be added to the grid.
    var _columns = new Array(); // Contains the Grid's columns
    var _colModel = null;
    var _configurationReader = null;
    var _dataStore = null;
    var _grid = null;
    var _records = new Array();
    var _store = null;
    var _selModel = null;
    
    this.getButtons = function(){
        return _buttons;
    }
        
    this.getGrid = function(){
        if(null != _grid){
            return _grid;
        }
    }
    
    this.setGrid = function(grid){
        _grid = grid;
    }
    
    this.getColumnModel = function(){
        if(null != _colModel){
            return _colModel;
        }
    }
    
    this.setColumnModel = function(cm){
        _colModel = cm;
    }
    
    this.setSelectionModel = function(sm){
        _selModel = sm;
    }
    
    this.getSelectionModel = function(){
        if(null != _selModel){
            return _selModel;
        }
    }
    
    this.getStore = function(){
        if(null != _store){
            return _store;
        }
    }
    
    this.setStore = function(store){
        _store = store;
    }
    
    this.getDataStore = function(){
        if(null != _dataStore){
            return _dataStore;
        }
    };
    
    this.setDataStore = function(dataStore){
        _dataStore = dataStore;
    };
    
    this.setConfigurationReader = function (id, record){
        _configurationReader = {};
        if(null != id){
            _configurationReader.id = id;
        }
        if(null != record){
            _configurationReader.record = record;
        }
    };
    
    this.getColumns = function(){
        return _columns;
    };
    
    this.getConfigurationReader = function(){
        return _configurationReader;
    };
    
    this.getRecords = function(){
        return _records;
    };
    
    this.addColumn = function (dataIndex, header, hidden, renderer, width, editor){
        var column = {};
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
        
        
        // Check whether it is needed to set an editor for this column
        if(null != editor){
           column.editor = editor;
        }
        _columns[_columns.length] = column;
    };
    
    this.addRecord = function (dateFormat, mapping, name, type){
        var record = {};
        if(null != dateFormat){
            record.dateFormat = dateFormat;
        }
        if(null != mapping){
            record.mapping = mapping;
        }
        if(null != name){
            record.name = name;
        }
        if(null != type){
            record.type = type;
        }
        _records[_records.length] = record;
    };
    
    this.addRowContextMenu = function(menuItems){
        if(null != menuItems && menuItems.length > 0){
            _grid.addListener({
	           'rowcontextmenu' : {
		          fn : function(grid, rowIndex, event) {
		              event.stopEvent();
		              /*
		               * In case the control key was down during the event,
		               * then keep existing selections.
		               */
		              _selModel.selectRow(rowIndex, event.ctrlKey);
                      var contextMenu = new Ext.menu.Menu({
                        items: menuItems
                      });
		              contextMenu.showAt(event.getXY());
		          }
	            }
            });
             _grid.render();
        }
    }
    
    /**
    * Adds a set of events to commit changes after editing a cell. In case a 
    * urlValidation is provided, then it will added an validation event to
    * validate whether the user is allowed to commit changes.
    *
    * @param urlUpdate the URL for the servlet in charge to edit the data
    * @param urlValidate the URL used for validation before editing the cell.
    *                    In case of null, then it means no validation is needed
    * 
    * @since 8.4 
    */
    this.addEditCellHandling = function(urlUpdate, urlValidate){
        /*
         * Depending on a URL for validation is provided, it will be added
         * a handler for 'beforeedit' event.
         */
         if(null != urlValidate){
            _grid.on('beforeedit', function(event){
                /*
                 * Retrieve the Task ID to be edited.
                 */ 
                var taskId = event.record.id;
                
                Ext.Ajax.request({
                    url: urlValidate,
                    method: 'POST',
                    params: { taskId: taskId},
                    success: function( result, response ) {
                        if(null != response.responseText && response.responseText.length > 0){
                            // There are errors present.
                            event.cancel = true;
                            extAlert('Error', result.responseText, Ext.MessageBox.ERROR);
                        }
                    },
                    failure: function() {
                        event.cancel = true;
                        extAlert('Error', 'It ocurred a connection error. Please, contact your support team.', Ext.MessageBox.ERROR);
                    }
                });
            }); 
        }
         
         // Add the 'afteredit' event handler
        _grid.on('afteredit', function(event) {
            /*
             * Retrieve the Task ID, Name and value of the field to be edited.
             * This information will be posted, so the servlet in charge to handle the data
             * will be able to determine how to proceed to commit the changes.
             */ 
            var taskId = event.record.id;
            var fieldName = event.field;
            var newValue = event.value;
 
            Ext.Ajax.request({
                url: urlUpdate,
                method: 'POST',
                params: { taskId: taskId, fieldName: fieldName, newValue: newValue },
                success: function( result, response ) {
                    if(null != response.responseText && response.responseText.length > 0){
                        // There are errors present.
                        extAlert('Error', result.responseText, Ext.MessageBox.ERROR);
                    }else {
                        _store.commitChanges();
                    }
                },
                failure: function() {
                    _store.rejectChanges();
                    extAlert('Error', 'It ocurred a connection error. Please, contact your support team.', Ext.MessageBox.ERROR);
                }
            });
        });
    }
    
    /**
    * Creates a numeric text field which provides validation both for blank values, as
    * negative values.
    *
    * @param blankMessage the error message to be shown when the field is left blank.
    *                       In case of this parameter is null, then it means the cell
    *                       can be set to blank and a zero default value will be used
    * @param negativeMessage the error message to be shown when the user enters a negative
    *                       number. In case this parameter is null, then it means the cell
    *                       can store negative numbers
    * maxValue indicates the maximum value allowed by the field. In case of null, then it will
    *          considered the maximum value is not restricted
    * maxValueMessage the error message to display when the maximum value is exceeded
    * minValue indicates the minimum value allowed by the field. In case of null, then it will
    *          considered the minimum value is not restricted, and for the case of non negative numbers
    *          the minimum value will be set to 0 (zero)
    * minValueMessage the error message to display when the minimum value is exceeded
    * @see Ext.form.NumberField
    * @see createConfigForNumberEditors
    * @since 8.4
    */
    this.createNumberFieldEditor = function (blankMessage, negativeMessage, maxValue, maxValueMessage, minValue, minValueMessage){
        // Creates the editor and returns it
        return new Ext.form.NumberField(this.createConfigForNumberEditors(blankMessage, negativeMessage, maxValue, maxValueMessage, minValue, minValueMessage));
    }  
    
    /**
    * Creates an input text field with validations for blank and max length.
    *
    * @param blankMessage the error message to display when the field is left blank.
    *                       In case of this parameter is null, then it means the cell
    *                       can be set to blank 
    * @param maxLength indicates the max length for a entered text
    * @param maxLengthMessage the error message to display when a the max length is exceeded
    *
    * @see Ext.form.TextField
    * @see createConfigForTextEditors
    * @since 8.4
    */
    this.createTextFieldEditor = function (blankMessage, maxLength, maxLengthMessage){
        // Creates the editor and returns it
        return new Ext.form.TextField(this.createConfigForTextEditors(blankMessage, maxLength, maxLengthMessage));
    }
    
    /**
    * Creates an input text area field with validations for blank and max length.
    *
    * @param blankMessage the error message to display when the field is left blank.
    *                       In case of this parameter is null, then it means the cell
    *                       can be set to blank 
    * @param maxLength indicates the max length for a entered text
    * @param maxLengthMessage the error message to display when a the max length is exceeded
    * @see Ext.form.TextArea
    * @see createConfigForTextEditors
    * @since 8.4
    */
    this.createTextAreaEditor = function (blankMessage, maxLength, maxLengthMessage){
        // Creates the editor and returns it
        return new Ext.form.TextArea(this.createConfigForTextEditors(blankMessage, maxLength, maxLengthMessage));
    }
    
    
    /**
    * Creates a configuration to be used when creating number editors.
    *
    * @param blankMessage the error message to display when the field is left blank.
    *                       In case of this parameter is null, then it means the cell
    *                       can be set to blank 
    * @param blankMessage the error message to be shown when the field is left blank.
    *                       In case of this parameter is null, then it means the cell
    *                       can be set to blank and a zero default value will be used
    * @param negativeMessage the error message to be shown when the user enters a negative
    *                       number. In case this parameter is null, then it means the cell
    *                       can store negative numbers
    * maxValue indicates the maximum value allowed by the field. In case of null, then it will
    *          considered the maximum value is not restricted
    * maxValueMessage the error message to display when the maximum value is exceeded
    * minValue indicates the minimum value allowed by the field. In case of null, then it will
    *          considered the minimum value is not restricted, and for the case of non negative numbers
    *          the minimum value will be set to 0 (zero)
    * minValueMessage the error message to display when the minimum value is exceeded
    * @since 8.4
    */
    this.createConfigForNumberEditors = function (blankMessage, negativeMessage, maxValue, maxValueMessage, minValue, minValueMessage){
        var config = {};
        
        // Null values
        if(null != blankMessage){
            config.allowBlank = false;
            config.blankText = blankMessage;
        } else{
            config.emptyText = 0;
        }
        
        // Negative values
        if(null != negativeMessage){
            config.allowNegative = false;
            config.negativeText = negativeMessage;
        }
        
        // Constraint for maximum value
        if(null != maxValue){
            config.maxValue = maxValue;
            config.maxText = maxValueMessage;
        }
        
        // Constraint for minimum value
        if(null != minValue){
            config.minValue = minValue;
            config.minText = minValueMessage;
        }
    }    
    
    /**
    * Creates a configuration to be used when creating text editors.
    *
    * @param blankMessage the error message to display when the field is left blank.
    *                       In case of this parameter is null, then it means the cell
    *                       can be set to blank 
    * @param maxLength indicates the max length for a entered text
    * @param maxLengthMessage the error message to display when a the max length is exceeded
    * @since 8.4
    */
    this.createConfigForTextEditors = function (blankMessage, maxLength, maxLengthMessage){
        var config = {};
        
        // Null values
        if(null != blankMessage){
            config.allowBlank = false;
            config.blankText = blankMessage;
        }
        
        // Constraints for maximum length
        if(null != maxLength){
            config.maxLength = maxLength;
            config.maxLengthText = maxLengthMessage;
        }
    }
    
    // "That" reference is needed to access the above functions from inside the Ext.onReady()
    that = this;
}

GridView.prototype.init = function init(renderTo){
    
    Ext.onReady(function(){
        // Data Store definition
        var store =  new Ext.data.Store({
                            url: that.getDataStore(),
                            reader: new Ext.data.XmlReader(that.getConfigurationReader(), that.getRecords())
                      });
        
        // Column model definition
        var colModel = new Ext.grid.ColumnModel(that.getColumns());
        colModel.defaultSortable = true;
        
        // Selection model definition
        var selModel = new Ext.grid.RowSelectionModel();
        
        // Create an editable grid
        var grid = new Ext.grid.EditorGridPanel({
            store: store,
            cm: colModel,
            sm: selModel,
            viewConfig: {forceFit: true,emptyText: 'It is no data available to be displayed.'},
            renderTo:renderTo,
            autoHeight: true,
            autoSizeColumns: true,
            loadMask: true
        });
        
        grid.render();
        
        // Finally, load the grid
        store.load();
        
        // Set variables for main function
        that.setStore(store);
        that.setSelectionModel(selModel);
        that.setColumnModel(colModel);
        that.setGrid(grid);
    });
};

/**
* Gets a record with a specific ID
*
* @param grid the Grid where the search will be performed on
* @param id the ID of the record to be searched
* @since 8.4
*/
GridView.prototype.getRecordById = function getRecordById(id){
    var result = null;
    if(null != id){
        var store = this.getStore();
        result = store.getById(id);
    }
    return result;
}

/**
* Returns the ID related to a row the user has selected in the Grid.
* In case of errors, this function returns the value -1, which means no
* row was selected, or -2, which means more than one row was selected.
*
* @param grid the Grid used to get the selected row
* @since 8.4
*/
GridView.prototype.getSelectedRowIdFromGrid = function getSelectedRowIdFromGrid(){
    // Selected records are driven by highligthed grid rows
    var selectionModel = this.getSelectionModel();
    var rowCount = selectionModel.getCount();
    
    // First, test at least one row was selected
    if(rowCount == 0){
        return -1;
    }
    
    // Now, test only one row was selected
    if(rowCount > 1){
        return -2;
    }
    return selectionModel.getSelected().id;
};

GridView.prototype.getSelectedNonLastRowIdFromGrid = function getSelectedNonLastRowIdFromGrid(){
    var selectionModel = this.getSelectionModel();
    var rowCount = selectionModel.getCount();
    
    // First, test at least one row was selected
    if(rowCount == 0){
        return -1;
    }
    
    // Now, test only one row was selected
    if(rowCount > 1){
        return -2;
    }
    
    // Test it's not the last record.
    if(selectionModel.hasNext()){
        return selectionModel.getSelected().id;
    } else{
        return -3;
    }
};

GridView.prototype.getSelectedNonFirstRowIdFromGrid = function getSelectedNonFirstRowIdFromGrid(){
    var selectionModel = this.getSelectionModel();
    var rowCount = selectionModel.getCount();
    
    // First, test at least one row was selected
    if(rowCount == 0){
        return -1;
    }
    
    // Now, test only one row was selected
    if(rowCount > 1){
        return -2;
    }
    
    // Test it's not the first record.
    if(selectionModel.hasPrevious()){
        return selectionModel.getSelected().id;
    } else{
        return -3;
    }
};

/**
* Returns an array of IDs related to the rows the user has selected in the Grid.
* In case of errors, this function returns the value -1, which means no
* row was selected
*
* @param grid the Grid used to get the selected row
* @since 8.4
*/
GridView.prototype.getSelectedRowIdsFromGrid = function getSelectedRowIdFromGrid(){
    // Selected records are driven by highligthed grid rows
    var selectionModel = this.getSelectionModel();
    var rowCount = selectionModel.getCount();
    
    // Test at least one row was selected
    if(rowCount == 0){
        return -1;
    }
    
    var selectedRows = selectionModel.getSelections();
    var ids = new Array();
    for(var i = 0; i < selectedRows.length; i++){
        ids[ids.length] = selectedRows[i].id;
    }

    return ids;
};

GridView.prototype.getSelectedRowIdsOnlyFromGrid = function getSelectedRowIdOnlyFromGrid(){
    var selectionModel = this.getSelectionModel();
    var rowCount = selectionModel.getCount();
    
    // Test at least two rows were selected
    if(rowCount <= 1){
        return -1;
    }
    
    var selectedRows = selectionModel.getSelections();
    var ids = new Array();
    for(var i = 0; i < selectedRows.length; i++){
        ids[ids.length] = selectedRows[i].id;
    }

    return ids;
};