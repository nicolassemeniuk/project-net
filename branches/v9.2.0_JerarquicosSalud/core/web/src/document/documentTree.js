var documentPanel;
var leftPanel;
var middlePanel;
var rightPanel;
var blogPanel;
var folderLeftTab;
var documentGrid;
var windowHeight = 0;
var windowWidth = 0;
var store;
var previewImageButton;
var imageUrl;
var myPanel;
var previewPopUpWindow;
var objectType;
var blogPopupScreen = document.createElement('div');
var businessCombo;
var projectCombo;
var previewImageButton;
var downloadButton;
var isPersonalSpace;

blogPopupScreen.id = 'blogPopupScreen';
blogPopupScreen.style.display = 'block';

Ext.onReady(function(){
	Ext.QuickTips.init();
	getWindowHeightWidth();

	blogPanel = new Ext.Panel({
		id: 'blog',
		title: 'Blog',
		autoScroll: true
	});
	
	leftPanel = new Ext.TabPanel({
		region : 'center',
		resizeTabs : true,
		deferredRender : false,
		activeTab: 0,
	    width: 200,
	    height: windowHeight-130,
	    autoScroll: true,
	    split: true
	});
	
	rightPanel = new Ext.TabPanel({
		region : 'east',
	    activeTab : 0,
	    width : 400,
	    collapsible : true,
	    collapsed : false,
	    split: true,
	    height: windowHeight-130
	});
	
	documentPanel = new Ext.Panel({
		resizable :true,
		height: windowHeight-150,
		width : '990',
		layout: 'border',
		closable: false,
		items: [leftPanel, rightPanel]
	});
	
	//Ext.Ajax.on('beforerequest', function(conn, options){Ext.get(documentPanel.body).mask('Loading...')},this);
    //Ext.Ajax.on('requestcomplete', function(conn, response, options){Ext.get(documentPanel.body).unmask()}, this);
	      
	//initializing Right Tab panel
	documentPanel.add( rightPanel );
	if( isBlogEnabled ){
		rightPanel.add( blogPanel );
	}
	leftPanel.add(documentGrid);
	documentPanel.render('documentHierarchy');
});

if ( spaceName == "Personal" ){
	isPersonalSpace = true;
} else {
	isPersonalSpace = false;
}

projectCombo = new Ext.form.ComboBox({
	store : new Ext.data.SimpleStore({
		fields :	['code', 'desc'], 
		data : Ext.util.JSON.decode(projectOptionsString)
	}),
	fieldLabel : '<b>Project</b>',
	listClass: 'x-combo-list-small',
	valueField :'code',
	displayField : 'desc',
	editable : false,
	typeAhead : true,
	mode : 'local',
	height: 25,
	triggerAction : 'all',
	//value : businessValue,
	emptyText  : 'Project',
	selectOnFocus : true,
	labelSeparator : ':',
	listWidth : 140,
	width : 100,
	listeners:{'select' : function (thisCombo, record, index){
		if(thisCombo.value != '' && thisCombo.value != 0 ){
			documentGrid.getStore().reload({params:{currentId:  projectCombo.value}} );
			businessCombo.setDisabled(true);
		} else {
			businessCombo.setDisabled(false);
		}
	}}
});

businessCombo = new Ext.form.ComboBox({
	store : new Ext.data.SimpleStore({
		fields :	['code', 'desc'], 
		data : Ext.util.JSON.decode(businessOptionsString)
	}),
	fieldLabel : '<b>Business</b>',
	listClass: 'x-combo-list-small',
	valueField :'code',
	displayField : 'desc',
	editable : false,
	typeAhead : true,
	mode : 'local',
	triggerAction : 'all',
	//value : businessValue,
	height: 25,
	emptyText  : 'Businesses',
	selectOnFocus : true,
	labelSeparator : ':',
	listWidth : 140,
	width : 100,
	listeners:{'select' : function (thisCombo, record, index){
		if(thisCombo.value != '' || thisCombo.value != 0){
			documentGrid.getStore().reload({params:{currentId:  businessCombo.value}} );
			projectCombo.setDisabled(true);
		} else {
			projectCombo.setDisabled(false);
		}
	}}
});

previewImageButton = new Ext.Button({
	id: 'preview',
	text:'<b>Preview</b>',
	tooltip: 'Image Preview',
	disabled: true,
	handler: function (){
		if(myPanel){
			previewPopUpWindow.remove(myPanel);
		}
		myPanel = new Ext.Panel({
			resizable :true,
			height : 500,
			width : 900,
			autoScroll: true,
			closable: false,
			plain: true,
			html: "<table height='100%' width='100%'><tr align='center' valign='center'><td><img src='"+ JSPRootURL +"/servlet/ViewDocument?id="+ objectId + "&module=" + moduleId + "'/><td></tr><table>"
		});
		previewPopUpWindow = createWindow();
		previewPopUpWindow.add(myPanel);
		blogPopupScreen.style.height = window.screen.height + 'px';
	    document.getElementsByTagName('body')[0].appendChild(blogPopupScreen);
		previewPopUpWindow.show();
		rightPanel.hide();
	},
});

downloadButton = new Ext.Button({
	id: 'Download',
	text:'<b>Download</b>',
	tooltip: 'Download Document',
	disabled: true,
	listeners: { 'click' : downLoadDocument }
})

var record = Ext.data.Record.create([
	{name: 'objectId', type: 'int'},
	{name: 'containerId', type: 'int'},
	{name: 'appIcon', type: 'string'},
	{name: 'name',type: 'string'},
	{name: 'format',type: 'string'},
	{name: 'version', type: 'string'},
	{name: 'is_checked_out',type: 'bool'},
	{name: 'status',type: 'string'},
	{name: 'author', type: 'string'},
	{name: 'date_modified', type: 'string'},
	{name: 'file_size', type: 'float'},
	{name: 'objectType', type: 'string'},
//	{name: 'imageUrl', type: 'string'},
	     	
	//TreeView related column
	{name: '_id', type: 'int'},
	{name: '_level', type: 'int'},
	{name: '_lft', type: 'int'},
	{name: '_rgt', type: 'int'},
	{name: '_is_leaf', type: 'bool'}
]);
	   	
var columnModel = [
	{id:'name', header: 'Name', renderer: renderDocumentIcon, width: 300, sortable: true, dataIndex: 'name', hideable : false},
    {id:'format', header: 'Format', sortable: true, dataIndex: 'format'},
	{id:'version', header: 'Version', sortable: true, width: 100, dataIndex: 'version'},
    {id:'is_checked_out', header: 'C/O',width: 50, sortable: true,renderer: is_checked_out, dataIndex: 'is_checked_out'},
	{id:'status', header: 'Status', sortable: true, dataIndex: 'status'},
	{id:'author', header: 'Author', sortable: true, dataIndex: 'author'},
    {id:'date_modified', header: 'Date Modified',sortable: true, dataIndex: 'date_modified'},
    {id:'file_size', header: 'Size', width: 10, sortable: true, dataIndex: 'file_size'}
];

var documentGridStore = new Ext.ux.maximgb.treegrid.NestedSetStore({
	url: JSPRootURL+'/documents/Details/getDocumentsTreeData?module='+moduleId + "&currentSpaceId=" +currentSpaceId,
	autoLoad : true,
	reader: new Ext.data.JsonReader({
				id: '_id',
				root: 'data',
				totalProperty: 'total',
				successProperty: 'success'
		},
		record
	)
	});

// Create document tree grid
documentGrid = new Ext.ux.maximgb.treegrid.GridPanel({
	store: documentGridStore,
	master_column_id : 'name',
	columns: columnModel,
	sm: new Ext.grid.RowSelectionModel( {singleSelect:true, listeners : {'rowselect' : onGridRowSelect} }),
	stripeRows: true,
	autoExpandColumn: 'name',
	title: 'Documents',
	id: 'documentGrid',
	height:520,
	width:505,
	autoScroll: true,
	loadMask: true,
	collapsed: false,
	viewConfig : {
		 enableRowBody : true,
		 forceFit: true
	},
	listeners: { 'rowClick' : onRowClick },
	tbar: [	isPersonalSpace ? projectCombo : '' , isPersonalSpace ? businessCombo : '','-', previewImageButton, '-', downloadButton ]
});

//Function for getting window height and width
function getWindowHeightWidth(){
	if(( navigator.userAgent.toLowerCase()).indexOf( "msie" ) != -1 ) {
		//IF IE
		if( document.documentElement && ( document.documentElement.clientWidth || document.documentElement.clientHeight )){
		//IE 6+ in 'standards compliant mode'
			windowWidth = document.documentElement.clientWidth;
			windowHeight = document.documentElement.clientHeight;
		} else {
		//IE 4 compatible
			windowWidth = document.body.clientWidth;
			windowHeight = document.body.clientHeight;
		}
	} else {
		//Non-IE
		windowWidth = window.innerWidth;
		windowHeight = window.innerHeight;
	}
}

function onGridRowSelect(thisSelectionModel, rowIndex, record){ 
	objectId = record.get('objectId');
	imageUrl = record.get('imageUrl');
	containerId = record.get('containerId');
	objectType = record.get('objectType');
	documentName = record.get('name');
	if( isBlogEnabled ){
		loadBlogEntriesForDocument();
	}
}

function onRowClick(grid, rowIndex, e){
	var record = documentGrid.getStore().getAt(rowIndex);
	format = record.get("format");

	if (format.indexOf("Image") != -1){
		previewImageButton.enable();
	} else {
		previewImageButton.disable();
	}
	
	if ( format.indexOf("File") != -1){
		downloadButton.disable();
	} else {
		downloadButton.enable();
	}
}

// Render a document icon
function renderDocumentIcon(name, params, record){
	return "<img src='" + JSPRootURL + record.data.appIcon + "'/>&nbsp;&nbsp;" + name;
}

// Create a new window
function createWindow(){
	var tempWindow = new Ext.Window({
		title: "Image Preview",
		id: 'wlhWindow',
		autoWidth : true,
		autoHeight: true,
		shadow : true,
		listeners : { 'destroy' : hideImagePreview }
	});
	return tempWindow;
}

// Download document
function downLoadDocument(){
	self.document.location = JSPRootURL + "/servlet/DownloadDocument?id=" + objectId;
}

// Ajax request for load blog entries
function loadBlogEntriesForDocument() {
	if(typeof(needRefresh)!= 'undefined' && needRefresh){
		needRefresh = false;
		refreshAssignmentGridAndCountFromStart(false);
	} else{
		blogPanel.load({
		    url: JSPRootURL +'/documents/Details/loadBlogEntries?module=' + moduleId,
		    params: {objectId : objectId, documentObjectType: objectType},
		    method: 'POST',
		    discardUrl: false,
		    nocache: false,
		    text: "Loading...",
		    timeout: 30,
		    scripts: false
		});
	}
}

// Ajax request for load blog entries
function removeDocuments() {
	if(typeof(needRefresh)!= 'undefined' && needRefresh){
		needRefresh = false;
		refreshAssignmentGridAndCountFromStart(false);
	} else {
		blogPanel.load({
		    url: JSPRootURL +'/documents/Details/removedocument?module=' + moduleId,
		    params: {objectId : objectId, documentObjectType: objectType},
		    method: 'POST',
		    discardUrl: false,
		    nocache: false,
		    text: "Loading...",
		    timeout: 30,
		    scripts: false
		});
	}
}

function getJsonDataString(id){
	var stringData = new Ext.ux.maximgb.treegrid.NestedSetStore({
	url: JSPRootURL+'/documents/Details/getDocumentsTreeData?module='+moduleId + "&currentSpaceId=" +id,
	autoLoad : true,
	reader: new Ext.data.JsonReader({
				id: '_id',
				root: 'data',
				totalProperty: 'total',
				successProperty: 'success'
		},
		record
	)
	});
	stringData.load();
	return stringData;
}

// check whether document is checked out or not
function is_checked_out(val){
      if(val)
      	return '<img border="0" src="'+JSPRootURL+'/images/check_green.gif" />';
      else 
      	return '';
}

// Hide image preview window
function hideImagePreview(window){
	previewPopUpWindow.hide();
	document.getElementsByTagName('body')[0].removeChild(blogPopupScreen);
	rightPanel.show();
}