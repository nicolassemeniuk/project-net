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

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.hibernate.constants.WikiConstants;
import net.project.hibernate.model.PnWikiPage;
import net.project.project.NoSuchPropertyException;
import net.project.project.ProjectSpaceBean;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.space.SpaceTypes;
import net.project.util.StringUtils;
import net.project.view.pages.base.BasePage;
import net.project.wiki.WikiManager;
import net.project.wiki.WikiURLManager;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

/**
 * @author
 * 
 */
public class WikiMenu extends BasePage {
	
	private static Logger log;

	@Property
	private String jSPRootURL;
    
    @Parameter(required = true)
    private String action;
    
    @Parameter( required = true)
    private PnWikiPage wikiPage;
    
    @Inject
    private Request request;    

	@Property
	private String projectManager;

	@Property
	private String spaceType;
	
	@Property
	private String spaceName;

	@Property
	private String currentSpaceName;
	
	@Property
	private Integer moduleId;
	
    @Property
    private Integer wikiPageId;
	
    @SetupRender
    void setValues() {
    	log = Logger.getLogger(WikiMenu.class);
    	
    	if(SessionManager.getUser() != null && StringUtils.isNotEmpty(SessionManager.getUser().getID())){
    		spaceName = SessionManager.getUser().getCurrentSpace().getSpaceType().getName();
    		spaceType = SessionManager.getUser().getCurrentSpace().getSpaceType().getID();
    		if(spaceType.equals(Space.BUSINESS_SPACE)){
    			moduleId = Module.BUSINESS_SPACE;
    		} else if(spaceType.equals(Space.PROJECT_SPACE)) {
    			moduleId = Module.PROJECT_SPACE;
    		}
    		currentSpaceName = SessionManager.getUser().getCurrentSpace().getName();
    		wikiPageId = wikiPage.getWikiPageId();
    	}
    	// getting ProjectSpaceBean from session for project details
    	ProjectSpaceBean project = (ProjectSpaceBean) request.getSession(false).getAttribute("projectSpace");
		if(project != null){
			try {
				// setting project manager name
				projectManager = project.getMetaData().getProperty("ProjectManager");
			} catch (NoSuchPropertyException pnetEx) {
				log.error("Error occurred while getting property - ProjectManager :"+pnetEx.getMessage());
			}
		}
		jSPRootURL = SessionManager.getJSPRootURL();
    }
    
    /**
     * Getting root page name
     * @return Root page name
     */
    public String getRootPageName(){
    	// TODO: correct this to always get actual root page!
        if(wikiPage.getParentPageName() != null){
            return wikiPage.getParentPageName().getPageName(); 
        } else {
            return wikiPage.getPageName();
        }
    }
    
    public String getPageToCall(){
    	return WikiManager.getPagesToCall(wikiPage);
    }

    /**
     * @return the wikiPage
     */
    public PnWikiPage getWikiPage() {
        return wikiPage;
    }
    
    /**
     * @return the actionsIconEnabled
     */
    public boolean isActionsIconEnabled() {
        return PropertyProvider.getBoolean("prm.global.actions.icon.isenabled");
    }

    /**
     * @return the blogItImagePathOn
     */
    public String getBlogItImagePathOn() {
        return PropertyProvider.get("all.global.toolbar.standard.blogit.image.on");
    }

    /**
     * @return the blogItImagePathOver
     */
    public String getBlogItImagePathOver() {
        return PropertyProvider.get("all.global.toolbar.standard.blogit.image.over");
    }

    /**
     * @return the changeHistoryLabel
     */
    public String getChangeHistoryLabel() {
        return PropertyProvider.get("prm.wiki.menu.changeHistory.label");
    }

    /**
     * @return the createNewWikiPageLabel
     */
    public String getCreateNewWikiPageLabel() {
        return PropertyProvider.get("prm.project.wiki.menu.createNewWikiPage.label");
    }

    /**
     * @return the indexByTitleLabel
     */
    public String getIndexByTitleLabel() {
        return PropertyProvider.get("prm.wiki.menu.pageIndex.label");
    }

    /**
     * @return the linksToExistingPagesLabel
     */
    public String getLinksToExistingPagesLabel() {
        return PropertyProvider.get("prm.wiki.menu.linkstoexistingpages.label");
    }

    /**
     * @return the recentChangesLabel
     */
    public String getRecentChangesLabel() {
        return PropertyProvider.get("prm.wiki.menu.recentChanges.label");
    }

    /**
     * @return the whatLinksHereLabel
     */
    public String getWhatLinksHereLabel() {
        return PropertyProvider.get("prm.wiki.menu.pageLinks.label");
    }

    /**
     * @return the wikiPageUrlImagePathOn
     */
    public String getWikiPageUrlImagePathOn() {
        return PropertyProvider.get("all.global.toolbar.standard.wikipageurl.image.on");
    }

    /**
     * @return the wikiPageUrlImagePathOver
     */
    public String getWikiPageUrlImagePathOver() {
        return PropertyProvider.get("all.global.toolbar.standard.wikipageurl.image.over");
    }

    /**
     * @return space id for current wiki page
     */
    public Integer getSpaceId(){
        return wikiPage.getOwnerObjectId().getObjectId();
    }
    
    /**
     * @return true if action is edit else false
     */
    public boolean isEditPage(){
        return action.equals(WikiConstants.WIKI_EDIT_ACTION);
    }
    
    /**
     * @return true if blog is enabled else false 
     */
    public boolean isBlogEnabled(){
    	Space currentSpace = SessionManager.getUser().getCurrentSpace();
		return PropertyProvider.getBoolean("prm.blog.isenabled") && currentSpace.isTypeOf(SpaceTypes.PERSONAL_SPACE)
				|| currentSpace.isTypeOf(SpaceTypes.PROJECT_SPACE);
    }
    
    /**
     * @return true if page exist else false
     */
    public boolean isPageExist(){
        return wikiPage.getWikiPageId() != null  && wikiPage.getRecordStatus().equals("A");
    }
    
    /**
     * @return true if action is history else false
     */
    public boolean isHistoryPage(){
        return action.equals(WikiConstants.WIKI_HISTORY_ACTION);
    }
    
    /**
     * @return true if action is view else false
     */
    public boolean isViewPage(){
        return action.equals(WikiConstants.WIKI_VIEW_ACTION) && isPageExist();
    }

    /**
     * @return index by title url
     */
    public String getOpenIndexByTitle(){
        return WikiManager.getPagesToCall(wikiPage)+"?op="+WikiConstants.WIKI_INDEX_ACTION;
    }
    
    /**
     * @return recent changes url
     */
    public String getOpenRecentChanges(){
        return WikiManager.getPagesToCall(wikiPage)+"?op="+WikiConstants.WIKI_RECENT_CHANGES_ACTION;
    }
    
    /**
     * @return change history url
     */
    public String getOpenChangeHistory(){
        return WikiManager.getPagesToCall(wikiPage)+"?op="+WikiConstants.WIKI_HISTORY_ACTION;
    }
    
    public boolean isUploadImageEnable(){
        return !action.equals(WikiConstants.WIKI_EDIT_ACTION) && !action.equals(WikiConstants.WIKI_PREVIEW_ACTION) && isPageExist() && !wikiPage.getPageName().startsWith("Image:");
    }
    
    public boolean isWhatLinkHereEnable(){
        return action.equals(WikiConstants.WIKI_VIEW_ACTION) && isPageExist() && !wikiPage.getPageName().startsWith("Image:");
    }
    
    public boolean isLinkOnThisPageEnable(){
        return action.equals(WikiConstants.WIKI_VIEW_ACTION) && isPageExist() && !wikiPage.getPageName().startsWith("Image:");
    }
    
    public boolean isChangeHistoryEnable(){
        return isPageExist() && !wikiPage.getPageName().startsWith("Image:") 
						     && !action.equals(WikiConstants.WIKI_INDEX_ACTION) 
						     && !action.equals(WikiConstants.WIKI_RECENT_CHANGES_ACTION)
						     && !action.equals(WikiConstants.WIKI_SHOW_IMAGES_ACTION);
    }
    
    public boolean isTopPageEnable(){
        return wikiPage.getParentPageName() != null || !action.equals(WikiConstants.WIKI_VIEW_ACTION);
    }
    
    public boolean isIndexByTitleEnable(){
        return isPageExist();
    }
    
    public boolean isRecentChangesEnable(){
        return isPageExist();
    }
    
    public boolean isCreatePageEnable(){
    	if(action.equals(WikiConstants.WIKI_PREVIEW_ACTION) && isPageExist()){
    		return false;
    	} else {
    		return !action.equals(WikiConstants.WIKI_EDIT_ACTION) && isPageExist();
    	}
    }
    
    public boolean isEditEnable(){
        return action.equals(WikiConstants.WIKI_VIEW_ACTION) && isPageExist()&& !wikiPage.getPageName().startsWith("Image:");
    }
    
    public boolean isDeleteEnable(){
        return action.equals(WikiConstants.WIKI_VIEW_ACTION) && isPageExist() && wikiPage.getParentPageName() != null && !wikiPage.getPageName().startsWith("Image:");
    }
        
    public boolean isAllImagesEnable(){
        return isPageExist();
    }
    
    public boolean isShowAttachmentsEnable(){
    	return action.equals(WikiConstants.WIKI_VIEW_ACTION) && isPageExist();
    }
    
    public String getBlogitAltOrTitle() {
        return PropertyProvider.get("all.global.toolbar.standard.blogit");
    }
    
    public String getEditPageAltOrTitle() {
        return PropertyProvider.get("prm.wiki.menu.editpage.option");
    }
    
    public String getDeletePageAltOrTitle() {
        return PropertyProvider.get("prm.wiki.left.deletepage.link");
    }
    
    public String getPageLinkAltOrTitle() {
        return PropertyProvider.get("prm.wiki.menu.pageLinks.label");
    }
    
    public String getLinksOnPageAltOrTitle() {
        return PropertyProvider.get("prm.wiki.menu.linkstoexistingpages.label");
    }
    
    public String getChangeHistoryAltOrTitle() {
        return PropertyProvider.get("prm.wiki.menu.changeHistory.label");
    }
    
    public String getTopPageAltOrTitle() {
        return PropertyProvider.get("prm.wiki.menu.toppage.option");
    }
    
    public String getIndexByTitleAltOrTitle() {
        return PropertyProvider.get("prm.wiki.left.indexbytitle.link");
    }
    
    public String getAllImagesAltOrTitle() {
        return PropertyProvider.get("prm.wiki.left.allimages.link");
    }
    
    public String getRecentChangesAltOrTitle() {
        return PropertyProvider.get("prm.wiki.left.recentchanges.link");
    }
    
    public String getCreatePageAltOrTitle() {
        return PropertyProvider.get("prm.wiki.left.createpage.link");
    }
    
    public String getUploadImageAltOrTitle() {
        return PropertyProvider.get("prm.wiki.menu.uploadimage.option");
    }
	
    public String getRootWikiPageName() {
    	return WikiURLManager.getRootWikiPageNameForSpace();
    }
    
    /**
     *	@return String
     */
    public String getShowAttachmentsAltOrTitle() {
        return PropertyProvider.get("prm.wiki.left.showattachments.tooltip");
    }
    
    /**
     * To get current space name
     * @return String
     */
    public String getCurrentSpaceNameEscaped() {
    	return org.apache.commons.lang.StringEscapeUtils.escapeJavaScript(wikiPage.getPageName());
    }
    
	/**
	 * To identify current space is project space
	 * @return boolean
	 */
	public boolean isProjectSpace(){
		return getUser().getCurrentSpace().getType().equalsIgnoreCase(Space.PROJECT_SPACE);
	}

	/**
	 * To get ProjectSpace
	 * @return ProjectSpaceBean
	 */
	public ProjectSpaceBean getProjectSpaceBean(){
		return (ProjectSpaceBean) getUser().getCurrentSpace();
	}
}
