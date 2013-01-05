/**
 * 
 */
package net.project.view.pages;

import java.io.IOException;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.hibernate.constants.WikiConstants;
import net.project.project.ProjectSpaceBean;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.util.StringUtils;
import net.project.wiki.WikiManager;
import net.project.wiki.WikiPageFinder;
import net.project.wiki.WikiURLManager;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Submit;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;

/**
 *
 */
public class Wiki {
    
    @Inject
    private Request request;

    @Inject
    private Response response;
    
    @Inject
    private RequestGlobals requestGlobals;
    
    private String spaceId;
    
    private String userId;
    
    private String currentPageName;
    
    private String parentPageName;
    
    @Persist
    private String wikiPageTitle;
    
    @Persist
    private String wikiPageName;

	@Component
    private Submit save;
    
    @Component
    private Submit preview;
    
    @Persist
    private WikiManager wikiManager;
    
    @Persist
    private String pvalue;
    
    private boolean accessIsExternal;
    
    private Integer moduleId;
    
    private String jspRootUrl;

    @Persist
    private Integer objectIdToAssign;
    
    @Persist
    private Date dateToCheckDifference;
    
    @Property
    private String submitFor;
    
    @Property
    private String submitButtonCaption;
    
    @Property
    private String previewButtonCaption;
    
    @CleanupRender
    void clearData(){
    	wikiManager = null;
    }
    
    void initParameters(){
    	submitButtonCaption = PropertyProvider.get("prm.wiki.edit.submit.button");
    	previewButtonCaption = PropertyProvider.get("prm.wiki.edit.preview.button");
    	if(spaceId == null && userId == null && StringUtils.isEmpty(currentPageName)){
	        spaceId = SessionManager.getUser().getCurrentSpace().getID();
	        userId = SessionManager.getUser().getID();
            moduleId = Module.PROJECT_SPACE;
            jspRootUrl = SessionManager.getJSPRootURL();
	        if(wikiManager != null && wikiManager.getPnWikiPage() != null) {
	        	currentPageName = wikiManager.getPnWikiPage().getPageName();
	        	if(wikiManager.getPnWikiPage().getParentPageName() != null){
	        		parentPageName = wikiManager.getPnWikiPage().getParentPageName().getPageName();
	        	} else {
	        		parentPageName = currentPageName;
	        	}
	        } else {
	        	currentPageName = WikiURLManager.getRootWikiPageNameForSpace();
	        	parentPageName = currentPageName;
	        }
        }
    }
    
    /**
     * Checking access permission for current user for current page
     */
    private void checkForAccessPermission(){
    	try{
	    	if(wikiManager.getPnWikiPage().getAccessLevel() == null || (wikiManager.getPnWikiPage().getAccessLevel() != null && wikiManager.getPnWikiPage().getAccessLevel().equals(WikiConstants.PROJECT_LEVEL_ACCESS))){
	    		Integer projectId = wikiManager.getPnWikiPage().getOwnerObjectId().getObjectId();
	    		if(projectId != null){
		    		// security access check
					Space oldSpace = SessionManager.getSecurityProvider().getSpace();
					if(!wikiManager.isAccessAllowed(projectId.toString(), getActionForWikiPage())) {
						// Failed Security Check
						SessionManager.getSecurityProvider().setSpace(oldSpace);
						Space testSpace = new ProjectSpaceBean();
				        testSpace.setID(projectId.toString());
		                try {
		                	HttpServletRequest httpServletRequest = requestGlobals.getHTTPServletRequest();
		                	httpServletRequest.setAttribute("exception", new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.main.authorizationfailed.message"), testSpace));
		                    RequestDispatcher requestDispatcher = httpServletRequest.getRequestDispatcher("/AccessDenied.jsp");
		                    requestDispatcher.forward(httpServletRequest, requestGlobals.getHTTPServletResponse());
		                } catch (Exception ex) {
		                    Logger.getLogger(Wiki.class).error("Error occurred while handling security for wiki: " + ex.getMessage());
		                }
					}
	    		} else {
	    			throw new IllegalStateException("Project not found");
	    		}
	    	} else if(wikiManager.getPnWikiPage().getAccessLevel() != null && wikiManager.getPnWikiPage().getAccessLevel().equals(WikiConstants.SYSTEM_LEVEL_ACCESS)){
	    		// do nothing since if user is null then IllegalStateException with msg "User is null" will be thrown befire coming here
	    	}
    	}catch (Exception e) {
    		 Logger.getLogger(Wiki.class).error("Error occurred while handling access permissions for wiki: " + e.getMessage());
		}
    }
    
    /**
     * Method to get current action for wiki page
	 * @return action view
	 */
	private Integer getActionForWikiPage() {
		if(wikiManager != null) {
			if(wikiManager.getPnWikiPage() != null && wikiManager.getPnWikiPage().getWikiPageId() == null && wikiManager.getAction().equals(WikiConstants.WIKI_EDIT_ACTION)){
				return Action.CREATE;
			} else if(wikiManager.getAction().equals(WikiConstants.WIKI_EDIT_ACTION)){
				return Action.MODIFY;
			} else if(wikiManager.getAction().equals(WikiConstants.WIKI_DELETE_ACTION)){
				return Action.DELETE;
			}
		}
		return Action.VIEW;
	}

	/**
     * @return true if wiki page access is public
     */
    private boolean wikiPageIsPublic(){
    	return wikiManager.getPnWikiPage().getAccessLevel().equals(WikiConstants.PUBLIC_LEVEL_ACCESS);
    }
    
    void onActivate(Object[] pageNames) {
        WikiURLManager.initSpace(requestGlobals.getHTTPServletRequest(), response);
        initPage(pageNames);
        if (SessionManager.getUser() != null && SessionManager.getUser().getID() != null) {
        	initParameters();
        }
        accessIsExternal = (SessionManager.getUser() == null || SessionManager.getUser().getID() == null);
        if(accessIsExternal){
	    	//Space testSpace = new ProjectSpaceBean();
	        //testSpace.setID(wikiManager.getPnWikiPage().getOwnerObjectId().getObjectId().toString());
			try {
			 	HttpServletRequest httpServletRequest = requestGlobals.getHTTPServletRequest();
			 	//httpServletRequest.setAttribute("exception", new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.main.authorizationfailed.message"), testSpace));
                request.getSession(true).setAttribute("requestedPage", request.getPath());
                RequestDispatcher requestDispatcher = httpServletRequest.getRequestDispatcher("/Login.jsp");
			    requestDispatcher.forward(httpServletRequest, requestGlobals.getHTTPServletResponse());
                return;
		    } catch (Exception ex) {
		        Logger.getLogger(Wiki.class).error("Error occurred while handling security for wiki: " + ex.getMessage());
		    }
        }
        checkForAccessPermission();
    }

	/**
	 * Method to initialize page 
	 * @param params Array of object parameters
	 */
	private void initPage(Object[] pageNames) {
		if(pageNames.length > 0 && request.getPath().startsWith("/wiki/")){
			pageNames = WikiURLManager.getParsedRequestParameters(requestGlobals.getHTTPServletRequest());
		}
        if (WikiURLManager.isNewWikiPageRequested(pageNames, wikiManager)){
            wikiManager = new WikiManager(WikiPageFinder.findPage(pageNames, request.getPath()));
        }
		
        if (StringUtils.isNotEmpty(request.getParameter("op"))) {
        	wikiManager.setAction(request.getParameter("op"));
            if(StringUtils.isNotEmpty(request.getParameter("hid")) && StringUtils.isNumeric(request.getParameter("hid"))){
                Integer historyId = Integer.parseInt(request.getParameter("hid"));
                wikiManager.setOldRevision(historyId);
            }
        } else {
        	wikiManager.setAction(WikiConstants.WIKI_VIEW_ACTION);
        }
        if (StringUtils.isNotEmpty(request.getParameter("redirctedFrom"))) {
            wikiManager.processRefLink(request);
        }

        if(wikiManager != null && wikiManager.getPnWikiPage() != null ) {
        	if((wikiManager.getAction().equals(WikiConstants.WIKI_EDIT_ACTION)) || (wikiManager.getAction().equals(WikiConstants.WIKI_PREVIEW_ACTION))){
        		dateToCheckDifference = wikiManager.getPnWikiPage().getEditDate();
        	}
        	if(objectIdToAssign != null) {
	            wikiManager.setObjectIdToAssign(objectIdToAssign);
	            objectIdToAssign = null;
	        }
        }

	}
        
    public void onActionFromActivate(Integer historyId) {
        wikiManager.setAction(WikiConstants.WIKI_VIEW_ACTION);
        wikiManager.setActiveRevision(historyId);
        forwardPage();
    }
    
    public void onActionFromActivateBottomLink(Integer historyId) {
        wikiManager.setAction(WikiConstants.WIKI_VIEW_ACTION);
        wikiManager.setActiveRevision(historyId);
        forwardPage();
    }

	/**
	 * Method to set action edit on create action 
	 */
	public void onActionFromCreateNew() {
        wikiManager.getPnWikiPage().setContent("");
        wikiManager.getPnWikiPage().setCommentText("");
        wikiManager.getPnWikiPage().setRecordStatus("A");
        wikiManager.setAction(WikiConstants.WIKI_EDIT_ACTION);
        forwardPage();
    }
    
    /**
     * Method to undelete deleted page 
     */
    public void onActionFromUndelete() {
        wikiManager.reLoadWikiPage();
        wikiManager.saveWikiPage();
        wikiManager.setAction(WikiConstants.WIKI_VIEW_ACTION);
        forwardPage();
    }
    
    /**
     *  Method to save wiki page after hitting enter in IE 
     */
    public void onAction(){
		if(StringUtils.isNotEmpty(submitFor) && submitFor.equals("save")){
			submitFor = null;
			onSelectedFromSave();
		}
    }
    
    /**
     * Method to save wiki page.
     */
    public void onSelectedFromSave() {
    	if(submitFor == null) {
	    	if(wikiManager.getPnWikiPage().getEditDate() != null && dateToCheckDifference != null){
		    	if(wikiManager.getPnWikiPage().getEditDate().getTime() == dateToCheckDifference.getTime()){
		    		saveContent();
		    	} else {
		    		wikiManager.editDifference();
		        	wikiManager.setAction(WikiConstants.WIKI_EDIT_ACTION);
		            forwardPage();
		    	}
	    	} else {
	    		saveContent();
	    	}
    	}	
    }
    
    /**
     * Method to set action preview
     */
    public void onSelectedFromPreview() {
    	if(wikiManager.getPnWikiPage().getEditDate() != null  && dateToCheckDifference != null && 
    		(wikiManager.getPnWikiPage().getEditDate().getTime() != dateToCheckDifference.getTime())){
	    		wikiManager.editDifference();
    	}	
    	wikiManager.setAction(WikiConstants.WIKI_PREVIEW_ACTION);
        forwardPage();
    }
      
    /**
     * Method to handle page urls after page load
     * @return wiki page name
     */
    Object[] onPassivate() {
    	String[] pages = wikiManager.getPagesToCall(wikiManager.getPnWikiPage()).split("/");
    	return (pages.length > 1 ? new String[] { pages[0], pages[1] } : new String[] { pages[0] });
    }
    
    /**
     * To set the Page Title and Page Name
     * @param action
     * @param pageName
     */
    public void setPageInfo(String action, String pageName) {
        if (action.equals(WikiConstants.WIKI_HISTORY_ACTION)){
            wikiPageTitle = PropertyProvider.get("prm.wiki.history.historyTitle");
        } else if(action.equals(WikiConstants.WIKI_INDEX_ACTION)){
            wikiPageTitle = "Page Index";
        } else {
            wikiPageTitle = "Wiki Page";
        }
        wikiPageName = pageName;
    }
    
	/**
	 * @return the rootPage
	 */
	public String getCurrentPageName() {
		return WikiURLManager.encodePageName(currentPageName);
	}

	/**
	 * @return the spaceId
	 */
	public String getSpaceId() {
		return spaceId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @return the history
	 */
	public boolean isHistory() {
		return wikiManager.getAction().equalsIgnoreCase(WikiConstants.WIKI_HISTORY_ACTION);
	}

	/**
	 * @return the index
	 */
	public boolean isIndex() {
		return wikiManager.getAction().equalsIgnoreCase(WikiConstants.WIKI_INDEX_ACTION);
	}
	
	/**
	 * @return the viewOrEdit
	 */
	public boolean isViewOrEdit() {
		return (StringUtils.isNotEmpty(wikiManager.getAction()) 
				? wikiManager.getAction().equalsIgnoreCase(WikiConstants.WIKI_EDIT_ACTION) 
						|| wikiManager.getAction().equalsIgnoreCase(WikiConstants.WIKI_VIEW_ACTION) : Boolean.TRUE);
	}


    /**
     * Redirecting the page to required url. This is written to handle the page urls.
     * This will be handled properly in Tapestry 5.0.18 migration
     */
    private void forwardPage(){
        String forwardPage = "";
        if (wikiManager.getRefLink(request) != null && wikiManager.getAction().equals(WikiConstants.WIKI_VIEW_ACTION)) {
            forwardPage = wikiManager.getRefLink(request);
            wikiManager.removeRefLink(request);
        } else {
            forwardPage = SessionManager.getJSPRootURL() + "/wiki/" + wikiManager.getPagesToCall() + "?op="+ wikiManager.getAction();
        }
        try {
            response.sendRedirect(forwardPage);
            return;
        } catch (IOException pnetEx) {
        }
    }
    
    /**
     * Save the wiki page
     */
    private void saveContent(){
    	wikiManager.getPnWikiPage().setAccessLevel(StringUtils.isNotEmpty(getPvalue()) ? Integer.parseInt(getPvalue()) : WikiConstants.PROJECT_LEVEL_ACCESS);
    	wikiManager.saveWikiPage();
    	wikiManager.setAction(WikiConstants.WIKI_VIEW_ACTION);
        forwardPage();
    	
    }
	/**
	 * @return the wikiManager
	 */
	public WikiManager getWikiManager() {
		return wikiManager;
	}

	/**
	 * @param wikiManager the wikiManager to set
	 */
	public void setWikiManager(WikiManager wikiManager) {
		this.wikiManager = wikiManager;
	}

    /**
     * @return the wikiPageName
     */
    public String getWikiPageName() {
        return wikiPageName;
    }

    /**
     * @param wikiPageName the wikiPageName to set
     */
    public void setWikiPageName(String wikiPageName) {
        this.wikiPageName = wikiPageName;
    }

    /**
     * @return the wikiPageTitle
     */
    public String getWikiPageTitle() {
        return wikiPageTitle;
    }

    /**
     * @param wikiPageTitle the wikiPageTitle to set
     */
    public void setWikiPageTitle(String wikiPageTitle) {
        this.wikiPageTitle = wikiPageTitle;
    }

	/**
	 * @return the parentPageName
	 */
	public String getParentPageName() {
		return WikiURLManager.encodePageName(parentPageName);
	}

	/**
	 * @param currentPageName the currentPageName to set
	 */
	public void setCurrentPageName(String currentPageName) {
		this.currentPageName = currentPageName;
	}

	/**
	 * @param parentPageName the parentPageName to set
	 */
	public void setParentPageName(String parentPageName) {
		this.parentPageName = parentPageName;
	}

	/**
	 * @return the pvalue
	 */
	public String getPvalue() {
		return pvalue;
	}

	/**
	 * @param pvalue the pvalue to set
	 */
	public void setPvalue(String pvalue) {
		this.pvalue = pvalue;
	}

	/**
	 * @return the accessIsExternal
	 */
	public boolean isAccessIsExternal() {
		return accessIsExternal;
	}

    /**
     * @return the moduleId
     */
    public Integer getModuleId() {
        return moduleId;
    }

    /**
     * @return the jspRootUrl
     */
    public String getJspRootUrl() {
        return jspRootUrl;
    }

    /**
     * @return the objectIdToAssign
     */
    public Integer getObjectIdToAssign() {
        return objectIdToAssign;
    }

    /**
     * @param objectIdToAssign the objectIdToAssign to set
     */
    public void setObjectIdToAssign(Integer objectIdToAssign) {
        this.objectIdToAssign = objectIdToAssign;
    }

    /**
     * @return the allAuthenticatedUsersOpt
     */
    public String getAllAuthenticatedUsersOpt() {
        return PropertyProvider.get("prm.wiki.editpage.allauthenticatedusers");
    }

    /**
     * @return the allProjectMembersOpt
     */
    public String getAllProjectMembersOpt() {
        return PropertyProvider.get("prm.wiki.editpage.allprojectmembers");
    }

    /**
     * @return the allunauthenticatedOpt
     */
    public String getAllUnauthenticatedOpt() {
        return PropertyProvider.get("prm.wiki.editpage.allunauthenticated");
    }
    
    /**
     * @return the isSetAccessPermissions
     */
    public boolean isSetAccessPermissions() {
        return PropertyProvider.getBoolean("prm.wiki.editpage.setwikipageaccesspermissions.isenabled");
    }

    /**
     * @return the editHelpLinkHref
     */
    public String getEditHelpLinkHref() {
        return PropertyProvider.get("prm.project.wiki.edit.edithelplink.link");
    }
    
	// Show edit page fields only when editing existing page(not on page creation) 
	public boolean isWikiPageExist() {
		return (wikiManager.getPnWikiPage() != null) && (wikiManager.getPnWikiPage().getWikiPageId() != null); 
	}
	
    /**
     * Converts the wiki to XML representation without the XML version tag.
     * This method returns the wiki page name and wiki url as XML text.
     *
     * @return XML representation
     */
	public String getXMLBody() {
		StringBuffer sb = new StringBuffer();
		sb.append("<Wiki>\n");
		sb.append("<WikiPageName>" + XMLUtils.escape(wikiPageName) + "</WikiPageName>\n");
		sb.append("<WikiPageUrl>" + URLFactory.makeURL(wikiManager.getPnWikiPage().getWikiPageId().toString(), ObjectType.BLOG) + "</WikiPageUrl>\n");
		sb.append("</Wiki>\n");
		return sb.toString();
	}

}
