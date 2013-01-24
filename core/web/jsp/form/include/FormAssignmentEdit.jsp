<%--
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
--%>

<%@ page
    contentType="text/html; charset=UTF-8"
    info="Edit Form Include" 
    language="java" 
    errorPage="/errors.jsp"
    import="net.project.base.property.PropertyProvider,
			net.project.base.Module,
            net.project.base.PnetException,
            net.project.form.*,
            net.project.security.*,
            net.project.util.JSPUtils" 
%>
<%@ include file="/base/taglibInclude.jsp" %>
<jsp:useBean id="user" class="net.project.security.User" scope="session" />
<jsp:useBean id="form" class="net.project.form.Form" scope="session" />

<%
	String module = request.getParameter("module");
    int action = Action.MODIFY;
    String formDataID = request.getParameter("formDataID");
    String formDataName = request.getParameter("formDataName");
    String userId = user.getID();
    String baseUrl = SessionManager.getJSPRootURL();
%>
<script language="javascript" type="text/javascript">
var userId = <%=userId%>;

/**
 * Set up plugin for a check column
 * @param {Object} config
 */
Ext.grid.CheckColumn = function(config){
    this.addEvents({
        click: true,
        keypress: true
    });
    Ext.grid.CheckColumn.superclass.constructor.call(this);
    
    Ext.apply(this, config, {
        init : function(grid){
            this.grid = grid;
            this.grid.on('render', function(){
                var view = this.grid.getView();
                view.mainBody.on('mousedown', this.onMouseDown, this);
            }, this);
        },
    
        onMouseDown : function(e, t){
            if(t.className && t.className.indexOf('x-grid3-cc-'+this.id) != -1){
                e.stopEvent();
                var index = this.grid.getView().findRowIndex(t);
                var record = this.grid.store.getAt(index);
                //record.set(this.dataIndex, !record.data[this.dataIndex]);
                this.fireEvent('click', this, e, record);
            }
        },

        onKeyDown : function(e, t){
            if(e.getKey() == EventObject.ENTER && t.className && t.className.indexOf('x-grid3-cc-'+this.id) != -1){
                e.stopEvent();
                var index = this.grid.getView().findRowIndex(t);
                var record = this.grid.store.getAt(index);
                //record.set(this.dataIndex, !record.data[this.dataIndex]);
                this.fireEvent('click', this, e, record);
            }
        },

        renderer : function(v, p, record){
            var checkState = (v == 'true') ? '-on' : '';
            p.css += ' x-grid3-check-col-td'; 
            return '<div class="x-grid3-check-col'+ checkState +' x-grid3-cc-'+this.id+'"> </div>';
        }
    });
    
  if(!this.id){
      this.id = Ext.id();
  }
  this.renderer = this.renderer.createDelegate(this);
};
    
Ext.extend(Ext.grid.CheckColumn, Ext.util.Observable);// extend Ext.util.Observable

Ext.onReady(function(){

    Ext.QuickTips.init();

    // shorthand aliases
    var fm = Ext.form;
    var xg = Ext.grid;	
    var xd = Ext.data;

    // custom column plugin example
    var checkColumn = new xg.CheckColumn({
        header: "Check",
        dataIndex: 'Loaded', 
        width: 50, 
        sortable: false
    });

    // checkColumn listeners
	checkColumn.on('click', function(element, e, record) {
        var myField = this.dataIndex;//the field name
        var check = record.data[myField];//same as record.data.check (but more abstract)
        var checkStatus = (check == 'false') ? 'added' : 'removed';
        var personName = record.get('Name');
        var personId = record.get('Id');
        var myMsg = 'The "<i>' + personName + '</i>" would be <b>'+ checkStatus + '</b>';
        Ext.MessageBox.alert('Assignment Check', myMsg);
        var checkMsg = (check == 'true') ? "remove" : "add";

        Ext.Ajax.request({   
            waitMsg: 'Adding assignment...',
            url: '<%=baseUrl%>/servlet/FormAssignmentController/FormAssignmentAddRemove', 
            params: { 
                action: "<%=action%>",
                module: "<%=module%>",
                ID: "<%=formDataID%>",
                check: checkMsg,
                personID: personId
            },
            failure: function(response, options) {
                Ext.MessageBox.alert('Warning','Error adding assignment, please retry...');
                store.rejectChanges();
            },
            success: function(response, options) {
                var responseData = Ext.util.JSON.decode(response.responseText);
                if(responseData.Warning) {
                    Ext.MessageBox.alert('Warning', responseData.Warning);
                    store.rejectChanges();
                } else {
                    var newCheckStatus = responseData.Loaded;
                    record.set(myField, newCheckStatus);
                    store.commitChanges();
                }
            }
        }); 

	});

    // check to disable 'Add Work' button in the work grid in popup 
    function addWorkButtonShow(workStore, records, options) {
        var personId = options.params.personID;
        if(personId != userId) {
            addWorkButton.disabled = true;
        }else{
            addWorkButton.disabled = false;
        }
    };
    function addWorkButtonDisable() {
        addWorkButton.disabled = true;
    };

    // render date type function
    function formatDate(value) {
        if(value instanceof Date)
            return value ? value.dateFormat('M d, Y') : '';
        return value;
    };

    // render the time function
    function formatTime(value) {
        if(value instanceof Date)
            return value ? value.dateFormat('H:i') : '';
        return value;
    };
    
    // render the % type function
    function formatPercent(value) {
        return value ? value + ' %' : '';
    };

    // render the capture work icon
    function formatCaptureWorkIcon(value) {
        return '<img alt="Capture Work" title="Capture Work" border=0 hspace=0 src="<%=SessionManager.getJSPRootURL()%>/images/icons/toolbar-gen-capturework_on.gif" name="imgcapture_work">';
    };
    
    // render the add work icon
    function formatAddWorkIcon(value) {
        if(value == 0)
            return '<img alt="Add Work" title="Add Work" border=0 hspace=0 src="<%=SessionManager.getJSPRootURL()%>/images/icons/toolbar-gen-submit_on.gif" name="imgadd_work">';
        else
            return "";
    };


    // create mapping for each person record
    var personRecord = xd.Record.create([
        {name: 'Id', mapping: 'person_id'},
        {name: 'Loaded', mapping: 'loaded'},
        {name: 'Name', mapping: 'person_name'},
        {name: 'Work', mapping: 'work'},
        {name: 'WorkComplete', mapping: 'work_complete'},
        {name: 'PercentComplete', mapping: 'percent_complete'},
        {name: 'StartDate', mapping: 'start_time', type: 'date', dateFormat: 'Y-m-d\\TH:i:s'},
        {name: 'EndDate', mapping: 'end_time', type: 'date', dateFormat: 'Y-m-d\\TH:i:s'},
        {name: 'DueDate', mapping: 'due_date', type: 'date', dateFormat: 'Y-m-d\\TH:i:s'}
    ]);

     // the data will be in XML, so lets set up a xml assignment reader
    var reader = new xd.XmlReader(
        {record: 'assignment'}, 
        personRecord);
           
    // create the Data Store for assignment list
    var store = new xd.Store({
        // load using HTTP
        url: '<%=baseUrl%>/servlet/FormAssignmentController/FormAssignmentsView',
        baseParams: {
            module: "<%=module%>", 
            action: "<%=action%>",
            ID: "<%=formDataID%>",
            formDataID: "<%=formDataID%>",
            formDataName: "<%=formDataName%>"
        },
        reader: reader,
        sortInfo:{field:'Name', direction:'ASC'}
    });
    
    // create mapping for each person record
    var workRecord = xd.Record.create([
        {name: 'Id', mapping: 'ObjectID'},
        {name: 'Name', mapping: 'ObjectName'},
        {name: 'PersonId', mapping: 'PersonID'},
        {name: 'AssigneeName', mapping: 'AssigneeName'},
        {name: 'LogDate', mapping: 'StartDate',type: 'date', dateFormat: 'Y-m-d\\TH:i:s'},
        {name: 'HoursWorked', mapping: 'HoursWorkedFormatted'},
        {name: 'StartTime', mapping: 'StartDate', type: 'date', dateFormat: 'Y-m-d\\TH:i:s'},
        {name: 'EndTime', mapping: 'EndDate', type: 'date', dateFormat: 'Y-m-d\\TH:i:s'},
        {name: 'Comment', mapping: 'Comment'}
    ]);

     // the data will be in XML, so lets set up a xml AssignmentWorkLogEntry reader
    var workReader = new xd.XmlReader(
        {record: 'AssignmentWorkLogEntry'}, 
        workRecord
    );

    // create the Data Store for work logs
    var workStore = new xd.Store({
        // load using HTTP
        url: '<%=baseUrl%>/servlet/FormAssignmentController/FormAssignmentWorkView',
        reader: workReader,
        sortInfo:{field:'LogDate', direction:'ASC'}
    });
    workStore.addListener('load', addWorkButtonShow);
    workStore.addListener('loadexception', addWorkButtonDisable);

    // column model for person list
    var cm = new xg.ColumnModel([
         checkColumn,{
            header: 'Name', 
            dataIndex: 'Name', 
            width: 150,
            sortable: true
         },{
            header: 'Work', 
            dataIndex: 'Work', 
            width: 100,
            sortable: false,
            editor: new fm.TextField({
                allowBlank: false,
                regex: /^((\d\d*\.\d*)|(\d\d*))\s(\bhrs|\bdays|\wks)$/,
                regexText: 'Should be in format like \'4 hrs\' or \'4 days\' or \'4 wks\' only'
            })
         },{
            header: 'Work Complete', 
            dataIndex: 'WorkComplete', 
            width: 100,
            sortable: true
         },{
            header: '% Complete', 
            dataIndex: 'PercentComplete', 
            sortable: false,
            width: 75,
            renderer: formatPercent,
            editor: new fm.NumberField({
                allowNegative: false,
                allowBlank: false,
                maxLength: 6,
                maxValue: 100,
             	minValue: 0
            })
         },{
            header: 'Start Date', 
            dataIndex: 'StartDate', 
            width: 100,
            sortable: true,
            renderer: formatDate
         },{
            header: 'End Date',
            dataIndex: 'EndDate',                 
            width: 100,
            sortable: true,
            renderer: formatDate
         },{
            header: 'Due Date',
            dataIndex: 'DueDate',                 
            width: 100,
            sortable: false,
            renderer: formatDate,
            editor: new fm.DateField({
                format: 'm/d/y'
            })
         },{
            header: 'Capture Work', 
            dataIndex: 'Id', 
            width: 75,
            sortable: false,
            renderer: formatCaptureWorkIcon
         }
    ]);

    // column model for work log
    var workCM = new xg.ColumnModel([
         {
            header: 'Log Date', 
            dataIndex: 'LogDate', 
            width: 100,
            sortable: true,
            renderer: formatDate,
            editor: new fm.DateField({
                format: 'm/d/y'
            })
         },{
            header: 'Start Time', 
            dataIndex: 'StartTime', 
            width: 100,
            sortable: false,
            renderer: formatTime,
            editor: new fm.TimeField({
                allowBlank: false,
                format: 'H:i'
            })
         },{
            header: 'End Time', 
            dataIndex: 'EndTime', 
            width: 100,
            sortable: false,
            renderer: formatTime,
            editor: new fm.TimeField({
                allowBlank: false,
                format: 'H:i'
            })
         },{
            header: 'Hours Worked', 
            dataIndex: 'HoursWorked', 
            width: 100,
            sortable: true
         },{
            header: 'Comment', 
            dataIndex: 'Comment', 
            width: 250,
            sortable: false,
            editor: new fm.TextArea({
                maxLength: 4000
            })
         },{
            header: 'Add', 
            dataIndex: 'Id', 
            width: 50,
            sortable: false,
            renderer: formatAddWorkIcon
         }
    ]);

    // create the grid for assignments
    var grid = new xg.EditorGridPanel({
        store: store,
        cm: cm,
        plugins: checkColumn,
        autoHeight: true,
        autoScroll: true,
        loadMask: true,
        enableColumnHide: false,
        enableColumnMove: false,
        clicksToEdit: 1,
        renderTo: 'assignments-grid'
    });
    // add event listeners
    grid.addListener('afteredit', handleAfterEdit);
    grid.addListener('beforeedit', handleBeforeEdit);
    grid.addListener('cellclick', handleClick);

    var addWorkButton = new Ext.Button({
        text: 'Add Work',
        handler: function(){
            var w = new workRecord({
                Id: 0,
                LogDate: (new Date()).clearTime(),
                StartTime: '00:00',
                EndTime: '00:00',
                HoursWorked: '0 hrs',
                Comment: ''
            });
            workGrid.stopEditing();
            workStore.insert(0, w);
            workGrid.startEditing(0, 0);
        }
    });

    // create the grid for work cpature for an assignment
    var workGrid = new xg.EditorGridPanel({
        store: workStore,
        cm: workCM,
        autoHeight: true,
        autoScroll: true,
        loadMask: true,
        enableColumnHide: false,
        enableColumnMove: false,
        clicksToEdit: 1,

        tbar: [addWorkButton]
    });

    // add an event to handle any updates to work grid
    workGrid.addListener('beforeedit', handleBeforeWorkEdit);
    workGrid.addListener('cellclick', handleAddClick);

    // popup work capture window handler
    var win = new Ext.Window({
        id: 'grid-work',
        title: 'Work Log Window',
        layout: 'fit',
        width: 700,
        height: 200,
        autoScroll: true,
        closeAction: 'hide',
        plain: true,
        modal: true,
        
        items: workGrid,

        buttons: [{
            text: 'Close',
            handler: function(){
                //Close
                win.hide();
            }
        }]
    });

    // handler functions
    function handleBeforeEdit(editEvent) {
        var record = editEvent.record;//get the record row
        var data = record.get('Loaded');//check if the row is loaded
        var persondId = record.get('Id');//check for which person edit is made
        if(data == 'false' || persondId != userId) {
            editEvent.cancel = true;//cancel the edit
        }
    };

    function handleAfterEdit(editEvent) {
        var gridField = editEvent.field;//determine what column is being edited
        var record = editEvent.record;//get the record row
        var fieldValue = editEvent.value;
        var originalFieldValue = editEvent.originalValue ;
        if(fieldValue instanceof Date) {
            fieldValue = fieldValue.format('m/d/y');
            if(originalFieldValue instanceof Date)
                originalFieldValue = originalFieldValue.format('m/d/y');
        }

        var personId = record.get('Id');
        Ext.Ajax.request({   
            waitMsg: 'Updating assignment ...',
            url: '<%=baseUrl%>/servlet/FormAssignmentController/FormAssignmentUpdate', 
            params: { 
                action: "<%=action%>",
                module: "<%=module%>",
                ID: "<%=formDataID%>",
                field: gridField,
                value: fieldValue,//the updated value
                originalValue: originalFieldValue,//the original value
                personID: personId
            },
            failure: function(response, options) {
                Ext.MessageBox.alert('Warning','Error updating assignment, please retry...');
                store.rejectChanges();
                record.set(gridField, originalFieldValue);
            },
            success: function(response, options) {
                var responseData = Ext.util.JSON.decode(response.responseText);
                if(responseData.Warning) {
                    Ext.MessageBox.alert('Warning', responseData.Warning);
                    store.rejectChanges();
                    record.set(gridField, originalFieldValue);
                } else {
                    if(gridField == 'PercentComplete') {
                        record.set('Work', responseData.Work);
                    } else if(gridField == 'Work') {
                        record.set('PercentComplete', responseData.PercentComplete);
                    }
                    store.commitChanges();
                }
            }
        }); 
    };
    
    function handleBeforeWorkEdit(editEvent) {
        var record = editEvent.record;//get the record row
        var id = record.get('Id');//check if the row is loaded
        if(id != '0') {
            editEvent.cancel = true;//cancel the edit
        }
    };

    function handleClick(grid, rowIndex, columnIndex, e) {
        var record = grid.getStore().getAt(rowIndex);  // Get the Record
        var fieldName = grid.getColumnModel().getDataIndex(columnIndex); // Get field name
        var data = record.get(fieldName);// this would be the person id
        var data1 = record.get('Loaded');//check if the row is loaded
        if(fieldName == 'Id' && data1 == 'true') {
            workStore.load({
                params: {
                        module: "<%=module%>", 
                        action: "<%=action%>",
                        ID: "<%=formDataID%>",
                        formDataID: "<%=formDataID%>", 
                        personID: data,
                        index: rowIndex
                },
                add: false});
            win.show();
        }
    };

    function handleAddClick(grid, rowIndex, columnIndex, e) {
        var record = grid.getStore().getAt(rowIndex);  // Get the Record
        var fieldName = grid.getColumnModel().getDataIndex(columnIndex); // Get field name
        var data = record.get(fieldName);// this would be the object id
        if(fieldName == 'Id' && data == '0') {
            // Get the parent (person) record
            var personRecordRowIndex = grid.getStore().lastOptions.params.index;
            var parentRecord = store.getAt(personRecordRowIndex);
            //add the same entry in db
            Ext.Ajax.request({   
                waitMsg: 'Adding work ...',
                url: '<%=baseUrl%>/servlet/FormAssignmentController/FormAssignmentWorkUpdate', 
                params: { 
                    action: "<%=action%>",
                    module: "<%=module%>",
                    ID: "<%=formDataID%>",
                    formDataID: "<%=formDataID%>", 
                    personID: parentRecord.get('Id'),
                    logDate: record.get('LogDate').format('m/d/y'),
                    startTime: record.get('StartTime'),
                    endTime: record.get('EndTime'),
                    comment: record.get('Comment')
                },
                failure: function(response, options) {
                    Ext.MessageBox.alert('Warning','Error updating assignment, please retry...');
                },
                success: function(response, options) {
                    var responseData = Ext.util.JSON.decode(response.responseText);
                    if(responseData.Warning) {
                        Ext.MessageBox.alert('Warning', responseData.Warning);
                    } else {
                        parentRecord.set('StartDate',responseData.StartDate);
                        parentRecord.set('EndDate',responseData.EndDate);
                        parentRecord.set('Work',responseData.Work);
                        parentRecord.set('WorkComplete',responseData.WorkComplete);
                        parentRecord.set('PercentComplete',responseData.PercentComplete);

                        record.set('Id', responseData.Id);
                        record.set('HoursWorked', responseData.HoursWorked);
                        store.commitChanges();
                        workStore.commitChanges();
                    }
                }
            }); 
        }
    };

    store.load();
});


</script>


<div id="assignments-grid"></div>
<%
	// Clear out any errors in form
	form.clearErrors();	
%>

