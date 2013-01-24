Ext.namespace('Ext.pnet');
/**
 * @class Ext.pnet.PnetDialogWindow
 * @extends Ext.Window
 * A specialized panel intended for use as an project.net application dialog window.
 * NOTE: To use this cmp please include pnetDialogWindow.css styles on your page.
 * 
 * One of the possible intializations could be following:
 *  var pnetWin = new Ext.pnet.PnetDialogWindow({
 *		id: 'pnetDialogWin',						// assigning an ID to the component
 *		title: 'Pnet Window - Title',				// specifying the title
 *		contentEl: 'formContanierID',				// specifying which underlaying HTML should be used for components body
 *		width: 600,									// specifying the width
 *		height: 460,								// specifying the height
 *		// Component specific configuration
 *		submitBtnLabel: 'Submit the form',			// Submit button text label to be used 
 *		cancelBtnLabel: 'Cancel and Close',			// Cancel button text label to be used
 *		submitDialogHandler : function(){},			// configuring components submit button handler function
 *		closeDialogHandler : function(){},			// configuring components cancel button handler function
 *		// ... more customization if needed	
 *		// adding additional buttons to the left of the Submit/Cancel buttons(that could be futher styled...	
 *  	buttons: [{
 *      	text: 'Clear All',
 *      	id: 'clearAllBtn',
 *      	handler: function(){extAlert('ClearAll Button Clicked!', '...Executing ClearAll Handler Function!', Ext.MessageBox.INFO);}
 *    	}],
 *    	subHeaderVisible: true,						// show Sub Header when displaying dialog window
 *    	subHeaderHtml: '<font>Business:</font>Project.net Test Business'	// set Sub Header HTML
 *  });
 *  pnetWin.show();
 *
 * To construct and set Errors that are to be displayed in Error Container use following:
 * 	pnetWin.setErrors('<h2>Following errors occured:</h2><p>Error while persisting...</p>');
 *
 * Then use following to show Error Container to the user programatically when needed:
 * 	pnetWin.showErrors();		// show existing errors
 *  pnetWin.showErrors(true);	// or trigger showing of existing errors with animation
 *  
 * To show/hide Sub Header programatically in your code use following:
 * 	pnetWin.showSubHeader();		// show Sub Header
 *	pnetWin.showSubHeader(true);	// show Sub Header with animation
 *	pnetWin.hideSubHeader();		// hide Sub Header 
 *	pnetWin.hideSubHeader(true);	// hide Sub Header with animation
 *
 * To set and show Sub Header loading message and indicator or hide Sub Header loading indicator programatically use follwing:
 * 	pnetWin.setSubHeaderLoadingHtml('...<i>Custom</i> Loading Message');
 *	pnetWin.hideSubHeaderLoadingIndicator();
 * 
 * To set Sub Header HTML programatically use following:
 * 	pnetWin.updateSubHeaderHtml('<font>Business:</font> Some other business');
 * 
 * @constructor
 * @param {Object} config The config object
 */
Ext.pnet.PnetDialogWindow = Ext.extend(Ext.Window, {
	
	/* Properties Declaration */
	
	/* variables holding initial width/height of the dialog is preserved */
	initWidth: 0,
    initHeight: 0,
    
    /* default configuration */
	minHeight: 100,
    minWidth: 200,
    closeAction: 'hide', 	/* hide is specified as close action */
    collapsible: true,
    animCollapse: true,
    border: true,
    resizable: true,
    layout: 'anchor',
    bodyStyle: 'background: #FFFFFF',
    cls: 'pnet-window-cls',
    
    /* Dialogs Footer Button Bar  */
    submitBtnLabel: '',
    cancelBtnLabel: '',
    SUBMIT_BUTTON_ID: 'submitBtn',
    CANCEL_BUTTON_ID: 'cancelBtn',
    buttons: [],

    /* toolbar options - closable(by default), restorable  */
	restored: true,								/* initially window is restored - set to its original position */
	restorable: true,
    
	/* Sub Header related configuration */
	subHeaderVisible: false,					/* should sub header be created */
	subHeaderHtml:	'',							/* html to be displayed within the Sub Header element */
	SUB_HEADER_ID: 'pnetWinSubHeaderCnt',
	SUB_HEADER_LOAD_INDICATOR_CLS: 'loadIndicator',
	subHeaderCls: 'pnet-window-subheader-cls',
	
	/* Error Container related configuration */
	errorContainerVisible: false,				/* should error container be created */
	errorContainerHtml:	'',						/* html to be displayed within the Error Containter element */
	ERROR_CNT_ID: 'pnetWinErrorCnt',
	errorContainerCls: 'pnet-window-errorcnt-cls',
	FIELD_ERROR_CLS: 'pnetWinFieldError',
	
	/* Actual cmp body container */
	ACTUAL_BODY_WRAPPER: 'pnetWinBodyWrapper',
	ACTUAL_BODY_WRAPPER_CLS: 'pnet-window-body-wrapper-cls',
	
	/* Initialization */
    initComponent : function(){
		this.initWidth = this.width;
	    this.initHeight = this.height;
	    
	    var SUBMIT_CANCEL_BUTTONS = [{
	     	id: this.SUBMIT_BUTTON_ID,
			text: 'Submit',
			handler: this.submitDialogHandler.createDelegate(this, [])
	 	},{
		 	id: this.CANCEL_BUTTON_ID,
			text: 'Cancel',
			handler:  this.closeDialogHandler.createDelegate(this, [])
	 	}];
	    if(this.buttons){
            var btns = this.buttons;
            this.buttons = [];
            for(var i = 0, len = btns.length; i < len; i++) {
                if(btns[i].render){ // button instance
                    btns[i].ownerCt = this;
                    this.buttons.push(btns[i]);
                }else{
                    this.addButton(btns[i]);
                }
            }
            
            for (var i = 0; i<SUBMIT_CANCEL_BUTTONS.length; i++) {
		    	var btn = SUBMIT_CANCEL_BUTTONS[i];
	            // add SUBMIT/CANCEL buttons at the end
	            this.buttons.push(btn);
			    
	            // set up the SUBMIT/CANCEL button labels        
		    	if(btn.id == this.SUBMIT_BUTTON_ID) {
				    if(this.submitBtnLabel && this.submitBtnLabel != '') 
				    	btn.text = this.submitBtnLabel;	    		
		    	} else if(btn.id == this.CANCEL_BUTTON_ID) {
			    	if(this.cancelBtnLabel && this.cancelBtnLabel != '')
			    		btn.text = this.cancelBtnLabel;
		    	}
			}
	    }
    	
	    if(this.contentEl) {
		    Ext.get(this.contentEl).show();
		}

        Ext.pnet.PnetDialogWindow.superclass.initComponent.call(this, arguments);
    },
    
    onRender : function(ct, position) {
    	Ext.pnet.PnetDialogWindow.superclass.onRender.call(this, ct, position);

		var footerCnt = Ext.select('.x-panel-btns-ct');
		if(footerCnt) {
		    footerCnt.removeClass('pnet-window-cls').addClass('pnet-window-cls');
		}
		
    },
    
    afterRender : function(container){
    	Ext.pnet.PnetDialogWindow.superclass.afterRender.call(this, container);
    	
    	/* Modify window content - add pnetWinSubHeader & pnetWinErrorCnt containers */
        var contentTarget = this.el;
		if (this.contentEl){
            var ce = Ext.getDom(this.contentEl);

            // append subHeader and errorCnt
            var subHeader = "<div id='"+this.SUB_HEADER_ID+"' class='"+this.subHeaderCls+"' style='display:none'><h2>"+this.subHeaderHtml+"</h2></div>";
            var errorCnt = "<div id='"+this.ERROR_CNT_ID+"' class='"+this.errorContainerCls+"' style='display:none'><div id='pnetWinErrorHtmlCntWrapper'>"+this.errorContainerHtml+"</div><div id='close'><a>[Close]</a></div></div>";
			var subHeaderEl = Ext.DomHelper.insertBefore(ce, subHeader, true);

			// wrap the contentEl within new element - actual body 
			var spec = {
			    tag: 'div',
			    id: this.ACTUAL_BODY_WRAPPER,
			    cls: this.ACTUAL_BODY_WRAPPER_CLS
			};
			var bodyWrapper = Ext.get(this.contentEl).wrap(spec);

			var errorCntEl = Ext.DomHelper.insertFirst(spec.id, errorCnt, true);
			
			if(this.subHeaderVisible) {
				subHeaderEl.setVisibilityMode(Ext.Element.DISPLAY);
				subHeaderEl.show();
			}
			if(this.errorContainerVisible) {
				errCnt.setVisibilityMode(Ext.Element.DISPLAY);
				errCnt.show();
			}
			
			this.errorsCntCloseBtnHandler(this, true);			// Register Close button click handler
			this.recalculateActualBodySize(bodyWrapper);		// set the size of the wrapper
		}
        
    },
    
    afterShow : function() {
	    Ext.pnet.PnetDialogWindow.superclass.afterShow.call(this);

	    if(this.getBox().width != this.initWidth || this.getBox().height != this.initHeight) {
	    	this.restored = false;
		    this.restore();
	    }
	    
	    this.positionDialog();	    
	    if(!this.height) {										// if height isn't specified - autocalculate the height
	    	this.setHeight(this.autoCalculateDialogsHeight());	// eliminate vertical scroll bars when showing the dialog
			this.doLayout();
	    }
	    this.recalculateActualBodySize();						// set the size of the wrapper
    },

    initTools : function(){
        if(this.restorable){
            this.addTool({
                id: 'restore',
                handler: this.restore.createDelegate(this, []),
                hidden:true
            });
        }
        if(this.closable){
            this.addTool({
                id: 'close',
                handler: this[this.closeAction].createDelegate(this, [])
            });
        }
    },
    
    /**
     * Restores a minimized/maximized window back to its original size and position 
     * prior to being minimized/maximized and hides 'restore' tool button.
     */
    restore : function(){
    	if(!this.restored){		// window is not in intial size - not restored
    		// set the window to restored - back to initial sizes
            this.el.removeClass('x-window-restored');
            this.tools.restore.hide();
            this.setSize(this.initWidth, this.initHeight);
            this.restored = true;
            this.el.enableShadow(true);
			
            if(this.dd){
                this.dd.unlock();
            }
            if(this.collapsible){
                this.tools.toggle.show();
            }
            this.container.removeClass('x-window-restored-ct');

            this.recalculateActualBodySize();		// recalculate the size of the wrapper
            this.doConstrain();
            this.positionDialog();
            
			// notify observers about the restore action
			this.fireEvent('restore', this);
    	}
    },
	
    collapse : function(){
    	Ext.pnet.PnetDialogWindow.superclass.collapse.call(this);
    	
    	if(!this.collapsed) {
    	    this.tools.restore.hide();
    	}
    	
    	// notify observers about the collapse action
		this.fireEvent('collapse', this);
    },
    
    expand : function(){
    	Ext.pnet.PnetDialogWindow.superclass.expand.call(this);
    	
    	if(!this.restored) {
            this.tools.restore.show();
    	}
    	
    	// notify observers about the collapse action
		this.fireEvent('expand', this);
    },
    
	/**
     * Hides the window, setting it to invisible and applying negative offsets.
     * NOTE: for this component this action will be used for window closing!!!
     * @param {String/Element} animateTarget (optional) The target element or id to which the window should
     * animate while hiding (defaults to null with no animation)
     * @param {Function} callback (optional) A callback function to call after the window is hidden
     * @param {Object} scope (optional) The scope in which to execute the callback
     */
    hide : function(animateTarget, cb, scope){
    	Ext.pnet.PnetDialogWindow.superclass.hide.call(this, animateTarget, cb, scope);
    	
    	this.hideErrors();	// make sure errors block is hidden
    },
    
    handleResize : function(box){
        var rz = this.resizeBox;
        if(rz.x != box.x || rz.y != box.y){
            this.updateBox(box);
        }else{
            this.setSize(box);
        }
        this.focus();
        this.updateHandles();
        this.saveState();
        if(this.layout){
            this.doLayout();
        }
		this.recalculateActualBodySize();						// handle Actual Body resize
        this.fireEvent("resize", this, box.width, box.height);
        
        // restore functionality preparation
        this.handleRestore();
    },
    
    /* Used to trigger display of restore button */
    handleRestore : function(){
      this.restored = false;
      this.tools.restore.show();
      this.el.enableShadow(true);
      this.doConstrain();
    },
	
	/* Handler method for Dialogs 'Submit' button. */
	submitDialogHandler : function() {
		extAlert('Handler function not Implemented', 'submitDialogHandler config not specified', Ext.MessageBox.INFO);
	},
	
	/* Handler method for Dialogs 'Cancel' button. */
	closeDialogHandler : function() {
		extAlert('Handler function not Implemented', 'closeDialogHandler config not specified', Ext.MessageBox.INFO);
	},

	/* Errors Container */
	/** 
	 * Error Container element getter method.
	 * Returns Error Containers actual body - the container holding the error messages.
	 * @return Ext.Element 
	 */
	getErrorContainerHtmlWrapperEl : function() {
		return Ext.get('pnetWinErrorHtmlCntWrapper');
	},
	
	getErrorContainerEl : function() {
		return Ext.get(this.ERROR_CNT_ID);
	},
	
	/* Sets HTML specified as argument in Errors Container Block, removing any previous errors. */
	setErrors : function(el) {
		var errCnt = this.getErrorContainerHtmlWrapperEl();
		errCnt.update(el);
	},
	
	/* Appends error HTML specified as argument to Errors Container Block. */
	appendErrors : function(el) {
		var errCnt = this.getErrorContainerHtmlWrapperEl();
		errCnt.createChild(el);
	},
	
	/* Cleares/Remove all errors that exist in error block content. */
	clearErrors : function() {
		var errCnt = this.getErrorContainerHtmlWrapperEl();
		if(errCnt) {
			errCnt.update('');
		}
	},
	
	/* Show errors. If argument has value true then animate the elements display. */
	showErrors : function(animate) {
		var errCnt = this.getErrorContainerEl();
		if(errCnt) {
			if(animate) {
				errCnt.slideIn('t', { duration: 1, useDisplay: true, scope: this, callback: function(){this.recalculateActualBodySize();} });
			} else {
				errCnt.show();
				this.recalculateActualBodySize();
			}
		}
	},
	
	/* Hide errors block. If argument has value true then animate the elements fading. */
	hideErrors : function(animate) {
		var errCnt = this.getErrorContainerEl();
		if(animate) {
			errCnt.slideOut('t', { duration: 1, useDisplay: true, scope: this, callback: function(){this.recalculateActualBodySize();this.resetErrorStyles(errCnt);} });
		} else {
			errCnt.setVisibilityMode(Ext.Element.DISPLAY);
			errCnt.hide();
			this.recalculateActualBodySize();
			this.resetErrorStyles(errCnt);		// TODO: only IF default error styles are changed - revert them
		}
	},

	errorsCntCloseBtnHandler : function(win, animate) {
		// Register Close button click handler
		var errCloseBtn = Ext.select('#'+this.ERROR_CNT_ID+' #close', true);
		errCloseBtn.on('click', function() { win.hideErrors(animate); });	
	},
	
	resetErrorStyles : function(errorContainer) {
		errorContainer.applyStyles('background:#EE0101');	// change errors content background color
		errorContainer.select('.tableContent').applyStyles('background:#EE0101;');
	},
	
	/* Sub Header */
	getSubHeaderHtmlCntEl : function() {
		return Ext.select('#'+this.SUB_HEADER_ID+' h2', true);
	},
	
	/* Update Sub Header text(html) with specified value. */
	updateSubHeaderHtml : function(html) {
		//var subHeader = Ext.get(this.SUB_HEADER_ID);
		var subHeaderHtmlCnt = this.getSubHeaderHtmlCntEl();
		subHeaderHtmlCnt.update(html);
	},
	
	/* Append Sub Header HTML with new child element. */
	appendSubHeaderHtml : function(el) {
		// var subHeader = Ext.get(this.SUB_HEADER_ID);
		var subHeaderHtmlCnt = this.getSubHeaderHtmlCntEl();
		subHeaderHtmlCnt.createChild(el); 
	},
	
	hideSubHeader : function(animate) {
		var subHeader = this.getSubHeaderEl(); //Ext.get(this.SUB_HEADER_ID);
		if(animate) {
			subHeader.slideOut('t', { duration: 1, useDisplay: true, scope: this, callback: function(){this.recalculateActualBodySize();} });
		} else {
			subHeader.setVisibilityMode(Ext.Element.DISPLAY);
			subHeader.hide();
			this.recalculateActualBodySize();
		}
	},
	
	showSubHeader : function(animate) {
		var subHeader = this.getSubHeaderEl(); //Ext.get(this.SUB_HEADER_ID);
		if(animate) {
			subHeader.slideIn('t', { duration: 1, useDisplay: true, scope: this, callback: function(){this.recalculateActualBodySize();} });
		} else {
			subHeader.show();
			this.recalculateActualBodySize();
		}
	},
	
	/** 
	 * Sub Header Container element getter method.
	 * @return Ext.Element 
	 */
	getSubHeaderEl : function() {
		return Ext.get(this.SUB_HEADER_ID);
	},
	
	/**
	 * Function used to set the Sub Header loading indicator message.
	 * @param msgHtml - HTML to be used with Loading Indicator
	 */
	setSubHeaderLoadingHtml : function(msgHtml) {
		var subHeader = this.getSubHeaderEl();
		if(subHeader) {
			var subHeaderLI = subHeader.down('.'+this.SUB_HEADER_LOAD_INDICATOR_CLS);
			if(!subHeaderLI) {
				var loadingHTML = '<span class="'+this.SUB_HEADER_LOAD_INDICATOR_CLS+'"><span>'+msgHtml+'<span></span>';
				subHeader.createChild(loadingHTML);
			} else {
				var subHeaderLISpan = subHeader.down('.'+this.SUB_HEADER_LOAD_INDICATOR_CLS+' span');
				if(subHeaderLISpan) {
					subHeaderLISpan.update(msgHtml);
					subHeaderLI.show();
				}	
			}
		}
	},
	
	hideSubHeaderLoadingIndicator : function(animation) {
		var subHeader = this.getSubHeaderEl();
		if(subHeader) {
			var subHeaderLI = subHeader.down('.'+this.SUB_HEADER_LOAD_INDICATOR_CLS);
			if(subHeaderLI) {
				subHeaderLI.hide(animation);
			}
		}
	},
	
	/* Actual Body */
	/**
	 * Gets the components actual body wrapper.
	 * @return {Ext.Element} - body wrapper element
	 */
	getCmpActualBody : function() {
		return Ext.get(this.ACTUAL_BODY_WRAPPER);
	},
	
	/**
	 * Helper function to recalculate and set Components Actual Body size.
	 * This is so that only this part should be overflowed correctly, and so that
	 * we would avoid users customization of the content when cmp is resizing. 
	 */
	recalculateActualBodySize : function(wrapper) {
		var bodyWrapper= Ext.apply({}, wrapper, this.getCmpActualBody());
		var subHeaderEl = this.getSubHeaderEl();
		var errCntEl = this.getErrorContainerEl();
		var subHeaderElBox = subHeaderEl.getBox();
		var errCntElBox = errCntEl.getBox();
		
		var height = this.getInnerHeight()-subHeaderElBox.height; // -errCntElBox.height;
		var width = bodyWrapper.getBox().width+(this.getInnerWidth()-bodyWrapper.getBox().width);
		
		// TODO: check this values for other Browsers - put this in separate method browser aware!
		if(bodyWrapper) {
			var errCntMrg = errCntEl.getMargins();
			var errCntMrgY = errCntEl.isVisible() ? errCntMrg.top+errCntMrg.bottom : 0;
			var subHeaderMrg = subHeaderEl.getMargins();
			var subHeaderMrgY = subHeaderEl.isVisible() ? subHeaderMrg.top+subHeaderMrg.bottom : 0;
			
			bodyWrapper.setSize(width, height-subHeaderMrgY);
		}
	},

	/**
	 * Function auto calculating the height of the <b>component(dialog window)</b>.
	 * This is used intially to eliminate the vertical scrollbars if 
	 * initial height of the window is not specified. 
	 * This method makes dialog smart enough to recalculate its height by taking
	 * in consideration the height of the component that will be displayed inside
	 * of it(component specified with contentEl config param). Makes dialog window "context-aware".
	 *
	 * NOTE: This method will be used only if clients didn't specified the
	 * <b>height</b> param config during components initialization! Otherwise the 
	 * height configuration param takes precedence in defining the components height.
	 * Method is setting the initHeight property only once! 
	 *
	 * @return {int} - the calculated hight of the component.
	 */
	autoCalculateDialogsHeight : function() {
		// Caching the calculated height of the window into initHeight variable 
		// if it isn't defined(height not specified in instantiation)
		// Caching is done due to issues on resizing(elements can change heights which causes recalculate issues).
		if(!this.initHeight) {	
			var actualBody = this.getCmpActualBody();
			var contentElElement = Ext.get(this.contentEl);
			var errCntEl = this.getErrorContainerEl();
			var hDiff = this.getSize().height - actualBody.getBox().height;
			var newHeight = hDiff + actualBody.getPadding('tb')  
				+ contentElElement.getHeight() + contentElElement.getMargins('tb')
				+ errCntEl.getHeight() + errCntEl.getMargins('tb');
			
			this.initHeight = newHeight;
		}
		
		return this.initHeight;
	},
	
	/* Form Validation related */
	// TODO: make this add field related error into Errors Array and then use this array when constructing errors
	/**
	 * Mark the specified field and apply specific class to it.
	 * @param fieldId - the ID string of the field to mark
	 */
	markErrorField : function(fieldId) {
		var fieldWihtErr = Ext.get(fieldId);
		if(fieldWihtErr) {
			fieldWihtErr.removeClass(this.FIELD_ERROR_CLS).addClass(this.FIELD_ERROR_CLS);
		}
	},
	
	/**
	 * Unmark the specified field and remove specific class from it.
	 * @param fieldId - the ID string of the field to unmark
	 */
	unMarkErrorField : function(fieldId) {
		var fieldWihtErr = Ext.get(fieldId);
		if(fieldWihtErr) {
			fieldWihtErr.removeClass(this.FIELD_ERROR_CLS);
		}
	},

	/**
	 * Function used to set the position of the component when it is displayed.
	 */
	positionDialog : function() {
	    this.center();
	    // this.setPosition(this.getPosition()[0], Ext.getBody().dom.scrollTop + 10);		
	}
});
Ext.reg('pnetDilaogWindow', Ext.pnet.PnetDialogWindow);
