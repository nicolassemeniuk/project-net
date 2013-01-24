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
/**
 * 
 */
package net.project.view.components;

import net.project.base.property.PropertyProvider;
import net.project.hibernate.constants.WikiConstants;
import net.project.hibernate.model.PnWikiPage;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;

/**
 * @author
 * 
 */
public class WikiInfo {
	
	private static Logger log;

	private String jSPRootURL;
	
	@Parameter(required = true)
	private String action;
	
	@Parameter(required = true)
	private PnWikiPage wikiPage;

	private String wikiPageTitle;
	
	@Property
	private String wikiPageName;

	@SetupRender
	void setValues() {
		setPageTitle(getAction());
	}

    /**
     * To set the Page Title and Page Name
     * @param action
     * @param pageName
     */
    public void setPageTitle(String pageAction) {        
        if(pageAction.equals(WikiConstants.WIKI_EDIT_ACTION)){
            wikiPageTitle = PropertyProvider.get("prm.wiki.edit.editTitle") + " - "; 
        } else if (pageAction.equals(WikiConstants.WIKI_HISTORY_ACTION)){
            wikiPageTitle = PropertyProvider.get("prm.wiki.history.historyTitle") + " - ";
        } else if (pageAction.equals(WikiConstants.WIKI_PREVIEW_ACTION)){
            wikiPageTitle = PropertyProvider.get("prm.wiki.preview.previewTitle") + " - ";
        } else if(pageAction.equals(WikiConstants.WIKI_INDEX_ACTION)){
            wikiPageTitle = "Page Index";
        } else {
            wikiPageTitle = "";
        }
        
        wikiPageName = wikiPage.getPageName().replace("_", " ");
    }    
    

	public String getJSPRootURL() {
		return jSPRootURL;
	}

    /**
     * @return the pageTitle
     */
    public String getwikiPageTitle() {
        return wikiPageTitle;
    }
    
	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
    
    public boolean isValidPageName(){
        return StringUtils.isNotEmpty(wikiPage.getPageName());
    }

    /**
     * @return the wikiPage
     */
    public PnWikiPage getWikiPage() {
        return wikiPage;
    }

}
