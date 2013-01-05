	// Save As Template messages - define right side variables on pages where dialog is used 
	var saveAsTemplateWindowTitle = saveAsTemplateWindowTitleValue;
	var footerSubmitLabel = footerSubmitLabelValue;
	var footerCancelLabel = footerCancelLabelValue;
	var templateCreatedDialogTitle = templateCreatedDialogTitleValue;
	var templateSavingErrorsHeadingLabel = templateSavingErrorsHeadingLabelValue;
	var ajaxRequestFailureHeadingLabel = ajaxRequestFailureHeadingLabelValue;
	var ajaxRequestFailureMessage = ajaxRequestFailureMessageValue;
	var subHeaderLabel = subHeaderLabelValue;
	
	var win;
	var saveCallbackFn = function() {};					// callback function triggered once 'save as template' form is submitted

	// CLOSE Dialog Window button handler
	function closeDialogHandler() {
		resetForm();
		win.hide(); 	
	}

	// SUBMIT Dialog Window button handler
	function submitDialogHandler() {
		// necessary validation
		if(processForm(Ext.get('saveAsTemplateForm').dom)) {
			// show form submition indicator
			win.setSubHeaderLoadingHtml('...Saving data');
			var globalVisibility = false;
			if(document.getElementById('isGlobal') && document.getElementById('isGlobal').checked)
				globalVisibility = true;
			// Ajax request
			Ext.Ajax.request({
			   url: JSPRootURL+'/methodology/ajaxHandler/save',
			   timeout: 300000,									// 5 minutes
			   params: {
				   		isAjax: 'true',
				   		// hidden fields 
				   		moduleId: Ext.get('module').getValue(), 
				   		action: Ext.get('action').getValue(), 
				   		theAction: "submit", 
				   		refLink: Ext.get('refLink').getValue(),
				   		// user input
				   		name: Ext.get('name').getValue(),
				   		parentSpaceID: Ext.get('parentSpaceID').getValue(),
				   		isGlobal: globalVisibility,
				   		description: Ext.get('description').getValue(),
				   		useScenario: Ext.get('useScenario').getValue(),
				   		selectedModules: getSelectedModules().toString()
				},			   
			   method: 'POST',
			   success: function(result, request){
					var jsonData;
					try {
					    jsonData = Ext.decode(result.responseText);
					}
					catch (e) {
					    extAlert('Error!', 'Data returned is not valid data!', Ext.MessageBox.ERROR);
					    return false;
					}
					
					win.hideSubHeaderLoadingIndicator();	// hide form submit indicator
					if( jsonData.status == 1 ) {
						// check for errors that might occured during execution of createTemplate method and show them
						if(jsonData.hasErrors == 1) {
							win.hide();
							resetForm();
						    Ext.MessageBox.show({ title: templateCreatedDialogTitle, msg: jsonData.message + '<br/><br/><h3>'+jsonData.errorsHeader+'</h3><div style="max-height:200px;height:auto;overflow:auto;">'+jsonData.errors+'</div>', buttons: Ext.Msg.OK, icon: Ext.MessageBox.INFO, fn: templateSuccessfullyCreatedCallback });
						    Ext.select('.tableContent').applyStyles('background:none;');
						    Ext.select('.tableContent td').applyStyles('color:#000000');
							return;
						}
						win.hide();
						resetForm();
						Ext.MessageBox.show({ title: templateCreatedDialogTitle, msg: jsonData.message, buttons: Ext.Msg.OK, icon: Ext.MessageBox.INFO, fn: templateSuccessfullyCreatedCallback});
					} else if( jsonData.status == 0 && jsonData.hasErrors == 1 ) {
						win.hide();
						resetForm();
						Ext.MessageBox.show({ title: jsonData.title, msg: jsonData.message, buttons: Ext.Msg.OK, icon: Ext.MessageBox.ERROR, fn: templateSuccessfullyCreatedCallback});
					} else {
						resetErrors();
						win.setErrors('<h3>'+templateSavingErrorsHeadingLabel+'</h3><div class="error">'+jsonData.message+'</div>');
						win.showErrors(true);
					}
			   },
			   failure: function(result, response){
					win.hideSubHeaderLoadingIndicator();		// hide form submition indicator
					resetErrors();
					win.setErrors('<h3>'+ajaxRequestFailureHeadingLabel+'</h3><div class="error">'+ajaxRequestFailureMessage+'</div>');
					win.showErrors(true);
			   }
			});
		}			
	}
	
	// Function used for creating 'Save as Template' Dialog Window
	function createPopupDialog() {
		if(!win) {
			win = new Ext.pnet.PnetDialogWindow({
				id: 'saveAsTmplDialog',
				title: saveAsTemplateWindowTitleValue,
				contentEl: 'saveAsTemplateCnt',
				minWidth: 300,
				minHeight: 50,
				width: 600,
			    submitBtnLabel: footerSubmitLabel,
			    cancelBtnLabel: footerCancelLabel,
				submitDialogHandler : submitDialogHandler,
				closeDialogHandler : closeDialogHandler,
				subHeaderVisible: true,
                subHeaderHtml: subHeaderLabel,
				listeners: { beforehide: resetForm /*resize: dialogResized*/ }
			});
		}
		
		selectAllModules();
		win.show();
		Ext.get('saveAsTemplateCnt').fadeIn({duration: 2});			// show form elements
	}
	
	// Function used to create 'Save as Template' Dialog Window.
	// param: e - event parameter that is used to stop the event propagation.
	// param: callbackFn - callback function - triggered after the form is submitted.
	function openPopupDelegate(e, callbackFn) {
		if(callbackFn) {
			saveCallbackFn = callbackFn;			// set callback function
		}
		if(e)
			e.preventDefault();
		
		createPopupDialog();
	}
	
	// Form validation method
	function processForm(myForm) {
		if(!checkTextbox(myForm.name, nameRequiredMsg)) return false;
		if(!checkCheckBox_NoSelect(myForm.selectedModule, includedModulesRequiredMsg)) return false;
		return true;
	}
	
	// hide/unhide global checkbox if personal workspace is selected
	function checkWorkspaceSelection(selectedValue, userId){
		if(document.getElementById('globalCheckbox')){
			if(selectedValue == userId ){
				Ext.get('saveAsTemplateForm').dom.isGlobal.checked = false;
				document.getElementById('globalCheckbox').style.display = 'none';
			} else {
				document.getElementById('globalCheckbox').style.display = '';
			}
		}
	}
	
	// function handling hiding/showing of Visible To field depending on Owning Business value
	function handleOwningBusinessSelection(currentUserId) {
		checkWorkspaceSelection(Ext.get('parentSpaceID').dom.value, currentUserId);
		Ext.get('parentSpaceID').on('change', function(ev, el){
			checkWorkspaceSelection(el.value, currentUserId);
		});
		// on clicking dialog trigger link handle Visible To field display
		if(Ext.get('saveAsTemplateLink')) {
			Ext.get('saveAsTemplateLink').on('click', function(){
				checkWorkspaceSelection(Ext.get('parentSpaceID').dom.value, currentUserId);
			});
		}
	}

	// Helper function - used for reseting 'Save As Template' form and doing relevant cleanup/preparation
	function resetForm() {
		Ext.get('saveAsTemplateForm').dom.reset();
		selectAllModules();
		resetErrors();
	}

	// Helper function - used to reset all previous server side error messages 
	function resetErrors() {
		Ext.DomHelper.overwrite(Ext.get('errorsCnt'), '', true);
	    Ext.get('errorsCnt').setDisplayed('none').applyStyles('background:#EE0101');
	}
	
	// Helper function - used for selecting all modules checkboxes
	function selectAllModules() {
		var modules = Ext.query('input[name=selectedModule]');
		for(var i in modules) {
		    modules[i].checked = 'true';
		}
	}
	
	// Helper function - used for getting forms selected modules
	function getSelectedModules() {
		var selectedModules = new Array();
		var arr = Ext.query('input[name=selectedModule]')
		
		for(var i=0; i<arr.length; i++) {
		    if( arr[i].checked ) {
		        selectedModules.push( Ext.fly(arr[i]).getValue() );
		    }
		}

		return selectedModules;
	}
	
	function isFunction(v) {
	    var toString = Object.prototype.toString;
	    return toString.apply(v) === '[object Function]';
	}

	function templateCreatedAlert(title, msg, icon, callbackFn) {
		Ext.MessageBox.show({
		    title: title,
		    msg: msg,
		    buttons: Ext.Msg.OK,
		    icon: icon,
		    fn: callbackFn
		});
	}
	
	/**
	 * Callback function triggered only when template is successfully persisted(with or without errors), 
	 * upon clicking any button of Ext.Alert Dialog saying which template is created in which space.
	 * @param buttonId
	 * @param text
	 * @param opt
	 */
	function templateSuccessfullyCreatedCallback(buttonId, text, opt) {		 
		// Trigger clients callback function - if specified 
		if(isFunction(saveCallbackFn)) {
			// TODO: (to reconsider) if errors exist (hasErrors value is 1) - execute callback function in dialog window's hide event  

			saveCallbackFn.call();	// trigger CALLBACK function if it is defined
		}	
	}
	