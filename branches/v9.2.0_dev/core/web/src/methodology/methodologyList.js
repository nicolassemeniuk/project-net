	/* ExtJs XSLT Template */
	var tmplItem = new Ext.XTemplate(
		'<tpl if="totalItems &gt; 0">',
			'<tpl for="templateItems">',
				'<!-- {category} Templates -->',
				'<div class="spaceTemplatesCnt">',
					'<h3 class="{[values.category === "project" ? "projectTmpls" : "businessTmpls"]}"> {[values.category === "project" ? "Project" : "Business"]} Templates</h3>',
					'<ul class="tmplList" id="{[values.category === "project" ? "projectList" : "businessList"]}">',
						// template category items
						'<tpl for="items">',
							// template item collapsed
							'<li>',
								'<a class="cntTrigger expand" href="#"></a>',
								'<input type="radio" name="selected" value="{objectId}" />',
								'<h4> <a href="'+JSPRootURL+'/methodology/Main.htm?id={objectId}">{name}</a> </h4> ',
								'<span class="by"><a href="{createdByHref}"> {createdBy} </a></span>',
								'<span class="tplTitleDescription"> {[values.description === "" ? " No description " : values.description]} </span>',
							'</li>',
							// template item expanded
							'<div class="tmplItemContent" style="display:none">',
								'<ul>',
									'<li class="general">',
										'<p>',
											'<div class="label">Date Created:</div>',
											'<div class="value"> {[values.createdDate === "" ? " No date " : values.createdDate]} </div>',
										'</p>',
										'<p>',
											'<div class="label">Owning Business:</div>',
											'<div class="value"> {owningBusiness} </div>',
										'</p>',
										'<p>',
											'<div class="label">Description:</div>',
											'<div class="value"> {[values.description === "" ? " No description " : values.description]} </div>',
										'</p>',
										'<p>',
											'<div class="label">Use scenario:</div>',
											'<div class="value useScenario"> {[values.useScenario === "" ? " No Use Scenario " : values.useScenario]} </div>',
										'</p>',
									'</li>	<!-- .general -->',
									'<li class="modules">',
										'<p>',
											'<div class="label">Included Modules:</div>',
											'<div class="value">',
												'<tpl for="modules">',
													'<p> {moduleDescription} </p>',
												'</tpl>',
											'</div>',
										'</p>',
									'</li>	<!-- .modules -->',
								'</ul>',
							'</div>			<!-- .tmplItemContent -->',
						'</tpl>',
					'</ul>					<!-- .tmplList-->',
				'</div>						<!-- #spaceTemplatesCnt -->',
			'</tpl>'
		,'</tpl>',
		'<tpl if="totalItems == 0">',
			'<h3>No templates exist!</h3>',
		'</tpl>'
	);
	
	/* Toggle one template items content display */
	function toggleItemContentDisplay(e, target) {
		e.preventDefault();
		var trigger = Ext.get(this.id);
        var wrappingLi = trigger.parent('li');
        var itemContentDetailsCnt = getTemplateDetails(wrappingLi);
        
		if(trigger.hasClass('expand')) {
			if( itemContentDetailsCnt ) {
				itemContentDetailsCnt
					.slideIn('tr', { duration: 1, useDisplay: true })
					.highlight("FFFFE7", { attr: 'background-color', duration: 1 });	// show template details
				wrappingLi.setStyle({'border-bottom':'none'});							// remove bottom border
				trigger.removeClass('expand').addClass('collapse');						// show collpase icon
			}
		} else if(trigger.hasClass('collapse')) {
			if( itemContentDetailsCnt ) {
				itemContentDetailsCnt
					.slideOut('tr', { duration: 1, useDisplay: true });					// hide template details
				wrappingLi.setStyle({'border-bottom':'1px solid #AAAAAA'});				// add bottom border
				trigger.removeClass('collapse').addClass('expand');						// show expand icon
			}
		}
		wrappingLi.stopFx().highlight("FFFFE7", { attr: 'background-color', duration: 1 });
	}
	
	/* 
	 * Helper gettter method to retrieve Ext.Element containing Template Item Details.
	 * Used to bypass browser compatible Ext Dom traverse issues.
	 */
	function getTemplateDetails(templateItemWrapper) {
		//return Ext.isIE ? templateItemWrapper.down('.tmplItemContent') : templateItemWrapper.next('.tmplItemContent');
		return templateItemWrapper.next('.tmplItemContent');
	}

	/**
	 * Expand or Collapse all item's content depneding on mod value:
	 *  <li>'expand'</li>
	 *  <li>'collapse'</li>
	 */ 
	function toggleAllItemsDetailsContentDisplay(mod) {
		var array = Ext.query('.tmplList li a.cntTrigger');
		for(var i in array) {
			if(array[i].id) {
		    	var trigger = Ext.get(array[i].id);
		        var wrappingLi = trigger.parent('li');
		        var itemContentDetailsCnt = getTemplateDetails(wrappingLi);
		        
				if( itemContentDetailsCnt ) {
					if( mod === 'expand' ) {
						itemContentDetailsCnt
							.slideIn('tr', { duration: 1, useDisplay: true })
							.highlight("FFFFE7", { attr: 'background-color', duration: 1 });		// show template details
						wrappingLi.setStyle({'border-bottom':'none'});								// remove bottom border
						trigger.removeClass('expand').removeClass('collapse').addClass('collapse'); // show collpase icon 
					} else if ( mod === 'collapse' ) {
						itemContentDetailsCnt
							.slideOut('tr', { duration: 1, useDisplay: true });						// hide template details
						wrappingLi.setStyle({'border-bottom':'1px solid #AAAAAA'});					// add bottom border
						trigger.removeClass('expand').removeClass('collapse').addClass('expand');	// show collpase icon		            
					}
					wrappingLi.stopFx().highlight("FFFFE7", { attr: 'background-color', duration: 1 });
				}
		  	}
		}
	}