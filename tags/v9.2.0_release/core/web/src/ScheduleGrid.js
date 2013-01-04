Ext.namespace('Ext.ux');

Ext.ux.Livegrid = Ext.extend(Ext.ux.grid.livegrid.GridPanel, {

    initComponent : function()
    {
       
        var bufferedReader = new Ext.ux.grid.livegrid.JsonReader({
            totalProperty   : 'response.value.total_count',
            root            : 'response.value.items',
            versionProperty : 'response.value.version',
            totalProperty   : 'response.value.total_count',
            id              : 'id'
          }, 
          [{name : 'id', sortType : 'int'},
          {name : 'sequenceNo', sortType : 'int'},
          {name : 'taskName', sortType : 'string'},
          {name : 'taskWork', sortType : 'float'},
          {name : 'taskDuration',   sortType : 'float'},
          {name : 'taskStartDate',   sortType : 'int'},
          {name : 'taskEndDate',   sortType : 'float'},
          {name : 'workComplete',   sortType : 'float'},
          {name : 'workPercentComplete',   sortType : 'float'},
          {name : 'actualStartDate',   sortType : 'float'},
          {name : 'actualEndDate',   sortType : 'float'},
          {name : 'statusNotifiers',   sortType : 'string'},
          {name : 'externalTask', sortType : 'string'},
          {name : 'hasDependencies', sortType : 'string'},
          {name : 'isDateConstrained', sortType : 'string'},
          {name : 'hasAssignments', sortType : 'string'},
          {name : 'criticalPath', sortType : 'string'},
          {name : 'afterDeadline', sortType : 'string'},
          {name : 'ETTP', sortType : 'string'},
          {name : 'ADTP', sortType : 'string'},
          {name : 'DCTP', sortType : 'string'},
          {name : 'ATP', sortType : 'string'},
          {name : 'isMilestone', sortType : 'string'}
          ]);

         
        this.store = new Ext.ux.grid.livegrid.Store({
            autoLoad   : true,
            bufferSize : 300,
            reader     : bufferedReader,
            sortInfo   : {field: 'taskName', direction: 'ASC'},
            url        : JSPRootURL+'/workplan/taskview/getFlatViewData',
            listeners : {'load' : function( thisStore, records, options ) {
            	setSelectionsForFlatView(getSelections(theForm, ','));
            }}
            
        });

        this.selModel = new Ext.ux.grid.livegrid.RowSelectionModel({listeners : {'rowselect' : onGridRowSelct, 'rowdeselect' : onGridRowDeSelect}});
        
        this.view = new Ext.ux.grid.livegrid.GridView({
            nearLimit      : 100,
            loadMask : {
                msg : 'Please wait...'
            }
        });
        Ext.ux.Livegrid.superclass.initComponent.call(this);
    }

});

       liveGrid = new Ext.ux.Livegrid({
            enableDragDrop : false,
            cm             : new Ext.grid.ColumnModel([
            	{header: "#", align : 'left',   width: 60, sortable: true, dataIndex: 'sequenceNo'},
                {header: "Task name", align : 'left',   width: 160, sortable: true, dataIndex: 'taskName', renderer : nameRenderer},
                {header: "Work", align : 'left',   width: 80, sortable: true, dataIndex: 'taskWork'},
                {header: "Duration",   align : 'left',  width: 80, sortable: true, dataIndex: 'taskDuration'},
                {header: "StartDate", align : 'left',   width: 80, sortable: true, dataIndex: 'taskStartDate'},
                {header: "EndDate", align : 'left',   width: 80, sortable: true, dataIndex: 'taskEndDate'},
                {header: "WorkComplete", align : 'left',   width: 80, sortable: true, dataIndex: 'workComplete', hidden: true},
                {header: "Work%Complete", align : 'left',   width: 80, sortable: true, dataIndex: 'workPercentComplete'},
                {header: "ActualStartDate", align : 'left',   width: 80, sortable: true, dataIndex: 'actualStartDate', hidden: true},
                {header: "ActualEndDate", align : 'left',   width: 80, sortable: true, dataIndex: 'actualEndDate', hidden: true},
                {header: "Status", align : 'left',   width: 80, sortable: true, renderer:extraImagesRenderer, dataIndex: 'statusNotifiers'}
                    
            ]),
            height:453,
            loadMask       : {
                msg : 'Loading...'
            }
           
        });
        
     function extraImagesRenderer(value, element, record, rowIndex , colIndex) {
        var result = "";  
        if(record.data['externalTask'] == "1"){
            result += String.format('<img src="{0}/images/schedule/externalTask.gif" hspace="2" border="0"  title="{1}"/>', JSPRootURL, record.data['ETTP']);//'<img src="'+JSPRootURL+'/images/schedule/externalTask.gif" hspace="2" border="0" title="'+record.data['ETTP']+'"/>';
        }
        if(record.data['hasDependencies'] == "1"){
           result += '<img src="'+JSPRootURL+'/images/schedule/dependency.gif" hspace="2" border="0"/>';
        }
        if(record.data['isDateConstrained'] == "1"){
            result += String.format('<img src="{0}/images/schedule/constraint.gif" hspace="2" border="0"  title="{1}" />', JSPRootURL, record.data['DCTP']);
        }
        if(record.data['hasAssignments'] == "1"){
           result += String.format('<img src="{0}/images/group_person_small.gif" hspace="2" border="0" onmouseover="aPopup(\'{1}\')" onmouseout="aClose();" />', JSPRootURL, record.data['ATP']);
        }
        if(record.data['criticalPath'] == "1"){
              result += String.format('<img src="{0}/images/schedule/critical_path.gif" hspace="2" border="0" title="{1}" />', JSPRootURL, criticalPathTitle);
        }
        if(record.data['afterDeadline'] == "1"){
             result += String.format('<img src="{0}/images/schedule/after_deadline.gif" hspace="2" border="0" title="{1}" />', JSPRootURL, record.data['ADTP']);
        }
        return result;
    }
  function nameRenderer(value, element, record){
       var result = "";
       // Check this whether it is a milestone
       if(record.data['isMilestone'] == "1"){
           result = String.format('<img src="{0}/images/milestone.gif" height="10" width="10" border="0" />', JSPRootURL);
           result += ' ';
       }
       result += value;
       return result;
   }    
    
 function setSelectionsForFlatView(lastSelectedIds){
	if(liveGrid){
   		var sm = liveGrid.getSelectionModel();
   		var scheduleStore = liveGrid.getStore();
    	lastSelectedIds = lastSelectedIds.split(',');
    	if(lastSelectedIds.length > 1){
    	sm. clearSelections();
	    	for(var index = 0; index < lastSelectedIds.length; index++){
	    		if(lastSelectedIds[index] != '' && typeof(lastSelectedIds[index]) != 'undefined'){
	    			if(typeof(scheduleStore.getById(lastSelectedIds[index])) != 'undefined'){
	    		 	 	sm.selectRow(scheduleStore.getById(lastSelectedIds[index]).data.sequenceNo-1,true);
	    		 	 }
	    		 }
	    	}
    	} else{
    		sm. clearSelections();
    	}
   	}
   }

function onGridRowSelct(sm, rowIndes, record){
   	document.getElementById('chk_'+record.id).checked = true;
}
    
function onGridRowDeSelect(sm, rowIndes, record){
   	document.getElementById('chk_'+record.id).checked = false;
}