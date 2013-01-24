/**
 * 
 */
package net.project.wiki;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.project.base.EventException;
import net.project.base.EventFactory;
import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.property.PropertyProvider;
import net.project.events.EventType;
import net.project.events.WikiEvent;
import net.project.hibernate.constants.WikiConstants;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnWikiHistory;
import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.IPnWikiHistoryService;
import net.project.hibernate.service.IPnWikiPageService;
import net.project.hibernate.service.IWikiProvider;
import net.project.hibernate.service.ServiceFactory;
import net.project.notification.EventCodes;
import net.project.project.ProjectSpaceBean;
import net.project.security.Action;
import net.project.security.SecurityProvider;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.xml.XMLUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 */
public class WikiManager {

    private static Logger log = Logger.getLogger(WikiManager.class);
    
    private static IPnWikiPageService pnWikiPageService = ServiceFactory.getInstance().getPnWikiPageService();
	
	private static IWikiProvider wikiProvider = ServiceFactory.getInstance().getWikiProvider();
	
    private IPnWikiHistoryService pnWikiHistoryService = ServiceFactory.getInstance().getPnWikiHistoryService();
    
    private PnWikiPage pnWikiPage;
    
    private PnWikiHistory pnWikiHistory;
    
    private String action = WikiConstants.WIKI_VIEW_ACTION;
    
    private Integer activateRevision;
    
    private Integer wikiPageHistoryId;

    private Integer objectIdToAssign;
    
    private String oldRevisionFoundMsg;
    
    private String refLink;
    
    private String wikiPageNameField;
    
    private String userPageString;
    
    private String otherUserPageString;
    
    private String beforeEditedUserName;
    
    public WikiManager(PnWikiPage pnWikiPage) {
        this.pnWikiPage = pnWikiPage;
//		System.out.println(" * WikiManager(PnWikiPage "+pnWikiPage.getPageName()+") -> ID: "+pnWikiPage.getWikiPageId());
    }
	
    public String getHtmlContent() {
        if (this.pnWikiPage.getContent() != null) {
        	return wikiProvider.convertToHtmlNew(this.pnWikiPage, isPreviewPage());
        } else {
            return null;
        }
    }
    
    /**
     * @param historyId
     */
    public void setOldRevision(Integer historyId) {
        try {
            pnWikiHistory = pnWikiHistoryService.get(historyId);
            setActivateRevision(pnWikiHistory.getWikiHistoryId());
            oldRevisionFoundMsg = PropertyProvider.get("prm.wiki.oldrevisionfound.message", pnWikiHistory.getEditedBy().getDisplayName());
        } catch(Exception e) {
            log.error("Error occurred while getting old revision by historyId: " + historyId + e.getMessage());
        }
    }
    
    public String getHtmlContentOfHistoryRevision() {
        return wikiProvider.convertToHtmlNew(this.pnWikiPage, isPreviewPage(), pnWikiHistory.getContent());
    }
    
    public void setActiveRevision(Integer activationId) {
        try {
            PnWikiHistory pnWikiHistory = pnWikiHistoryService.get(Integer.valueOf(activationId));
            //added to fix start
            PnPerson person = ServiceFactory.getInstance().getPnPersonService().getPerson(Integer.valueOf(SessionManager.getUser().getID()) );
            if( pnWikiPage != null ) {
                if( pnWikiHistory != null ) {
                    //add current version to history
                    PnWikiHistory newHistoryPage = new PnWikiHistory();
                    newHistoryPage.setContent(pnWikiPage.getContent());
                    newHistoryPage.setCommentText(pnWikiPage.getCommentText());
                    newHistoryPage.setEditDate(pnWikiPage.getEditDate());
                    newHistoryPage.setEditedBy(pnWikiPage.getEditedBy());
                    newHistoryPage.setWikiPageId(pnWikiPage);
                    Integer result = pnWikiHistoryService.save(newHistoryPage);
                    
                    //update active version with old one
                    pnWikiPage.setContent(pnWikiHistory.getContent());
                    pnWikiPage.setEditDate(new Date());
                    pnWikiPage.setEditedBy(person);
                    pnWikiPage.setRecordStatus("A");
                    pnWikiPage.setCommentText(StringUtils.isNotEmpty(pnWikiHistory.getCommentText())? pnWikiHistory.getCommentText() : "" + " (reactivated)");
                    pnWikiPageService.update(pnWikiPage);
                    setAction(WikiConstants.WIKI_VIEW_ACTION);
                    
                    // publishing event to asynchronous queue
                    WikiEvent wikiEvent = (WikiEvent) EventFactory.getEvent(ObjectType.WIKI, EventType.EDITED);
                	wikiEvent.setObjectID(this.pnWikiPage.getWikiPageId().toString());
                	wikiEvent.setObjectType(ObjectType.WIKI);
                	wikiEvent.setName(this.pnWikiPage.getPageName());
                	wikiEvent.setObjectRecordStatus(this.pnWikiPage.getRecordStatus());
                	if(this.pnWikiPage.getParentPageName() != null)
                		wikiEvent.setParentObjectId(this.pnWikiPage.getParentPageName().getWikiPageId().toString());
                	wikiEvent.publish();
                }
            }
        } catch (EventException ex) {
			Logger.getLogger(WikiManager.class).error("WikiManager.updatePage():: Wiki Page Modify Event Publishing Failed! "+ ex.getMessage());
		} catch(Exception e) {
            log.error("Error occurred while activating revision by activationId : " + activationId + e.getMessage());
        }
    }
    
    public String getWikiPageViewContent(){
        if(this.action.equals(WikiConstants.WIKI_EDIT_ACTION)){
            return null;
        } else {
            return getHtmlContent();
        }
    }
    
    public boolean isPageNotExist(){
        return this.pnWikiPage.getWikiPageId() == null && this.action.equals(WikiConstants.WIKI_VIEW_ACTION) && StringUtils.isNotEmpty(this.pnWikiPage.getPageName());   
    }
    
    /**
     * Save wiki page changes
     */
    public void saveWikiPage() {
//    	boolean isUpdated = false;
        populateWikiPageObjet();
        if ( isNewPage(this.pnWikiPage) ) {
			createPage();
        } else {
            updatePage();
//            isUpdated = true;
        }
        createHistory();
        assignWikiPageToObject();

        // if page was updated - check should renaming be triggered
//        if (isUpdated) {
//        	if (isPageNameChanged()) {
//        		renameWikiPage(this.wikiPageNameField, this.pnWikiPage);
//        	}
//        }
       
    }
    
    private boolean isNewPage(PnWikiPage wikiPage) {
    	if(wikiPage.getWikiPageId() == null) {
    		PnWikiPage foundPage = this.pnWikiPageService.getWikiPage(wikiPage.getPageName(), wikiPage.getOwnerObjectId().getObjectId());
    		if(foundPage != null) {
    			foundPage.setEditDate(this.pnWikiPage.getEditDate());
    			foundPage.setEditedBy(this.pnWikiPage.getEditedBy());
    			foundPage.setRecordStatus("A");
    			foundPage.setCommentText(this.pnWikiPage.getCommentText());
    			foundPage.setContent(this.pnWikiPage.getContent());
    			this.pnWikiPage = foundPage;
    			return false;
    		} else {
    			return true;
    		}
    	} 
    	return false;
    }
    
    /**
     * Saving history for the wiki page
     */
    public void createHistory() {
    	try {
	        PnWikiHistory wikiHistory = new PnWikiHistory();
	        wikiHistory.setWikiHistoryId(null);
	        wikiHistory.setContent(this.pnWikiPage.getContent());
	        wikiHistory.setCommentText(this.pnWikiPage.getCommentText());
	        wikiHistory.setEditDate(this.pnWikiPage.getEditDate());
	        wikiHistory.setEditedBy(this.pnWikiPage.getEditedBy());
	        wikiHistory.setWikiPageId(this.pnWikiPage);
	        wikiHistory.setEdityType("edit_page");
	        pnWikiHistoryService.save(wikiHistory);
    	} catch (Exception e) {
    		log.error("Error occurred while creating wiki page history  " + e.getMessage());
		}
    }
    /**
     * Create new wiki page
     */
    private void createPage(){
    	try {
			pnWikiPageService.save(this.pnWikiPage);
			
			//Notification Event, If parent page available.
			String targetObjectID = this.pnWikiPage.getParentPageName() != null ? this.pnWikiPage.getParentPageName()
					.getWikiPageId().toString() : this.pnWikiPage.getWikiPageId().toString();
				net.project.wiki.WikiEvent event = new net.project.wiki.WikiEvent();
				event.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
				event.setTargetObjectID(targetObjectID);
				event.setTargetObjectType(EventCodes.CREATE_WIKI_PAGE);
				event.setTargetObjectXML(getXmlBody());
				event.setEventType(EventCodes.CREATE_WIKI_PAGE);
				event.setUser(SessionManager.getUser());
				event.setDescription("Wiki page created : \"" + pnWikiPage.getPageName() + "\"");
				event.store();
			
			// publishing event to asynchronous queue
        	WikiEvent wikiEvent = (WikiEvent) EventFactory.getEvent(ObjectType.WIKI, EventType.NEW);
        	wikiEvent.setObjectID(this.pnWikiPage.getWikiPageId().toString());
        	wikiEvent.setObjectType(ObjectType.WIKI);
        	wikiEvent.setName(this.pnWikiPage.getPageName());
        	wikiEvent.setObjectRecordStatus(this.pnWikiPage.getRecordStatus());
        	if(this.pnWikiPage.getParentPageName() != null)
        		wikiEvent.setParentObjectId(this.pnWikiPage.getParentPageName().getWikiPageId().toString());
        	wikiEvent.publish();
		} catch (EventException ex) {
			Logger.getLogger(WikiManager.class).error("WikiManager.createPage():: Wiki Page Create Event Publishing Failed! "+ ex.getMessage());
		} catch (Exception e) {
			log.error("Error occurred while saving wiki page : " + e.getMessage());
		}
    }
    
    /**
     * Update wiki page
     */
    private void updatePage(){
    	try{
    		pnWikiPageService.update(this.pnWikiPage);
			
    		//Notification Event
			String targetObjectID = this.pnWikiPage.getParentPageName() != null ? this.pnWikiPage.getParentPageName()
					.getWikiPageId().toString() : this.pnWikiPage.getWikiPageId().toString();
				net.project.wiki.WikiEvent event = new net.project.wiki.WikiEvent();
				event.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
				event.setTargetObjectID(targetObjectID);
				event.setTargetObjectType(EventCodes.MODIFY_WIKI_PAGE);
				event.setTargetObjectXML(getXmlBody());
				event.setEventType(EventCodes.MODIFY_WIKI_PAGE);
				event.setUser(SessionManager.getUser());
				event.setDescription("Wiki page modify : \"" + pnWikiPage.getPageName() + "\"");
				event.store();
				
			
			// publishing event to asynchronous queue
        	WikiEvent wikiEvent = (WikiEvent) EventFactory.getEvent(ObjectType.WIKI, EventType.EDITED);
        	wikiEvent.setObjectID(this.pnWikiPage.getWikiPageId().toString());
        	wikiEvent.setObjectType(ObjectType.WIKI);
        	wikiEvent.setName(this.pnWikiPage.getPageName());
        	wikiEvent.setObjectRecordStatus(this.pnWikiPage.getRecordStatus());
        	if(this.pnWikiPage.getParentPageName() != null)
        		wikiEvent.setParentObjectId(this.pnWikiPage.getParentPageName().getWikiPageId().toString());
        	wikiEvent.publish();
    	} catch (EventException ex) {
			Logger.getLogger(WikiManager.class).error("WikiManager.updatePage():: Wiki Page Modify Event Publishing Failed! "+ ex.getMessage());
		} catch (Exception e) {
			log.error("Error occurred while updating wiki page : " + e.getMessage());
		}
    }
    
    /**
     * Populating wiki page object parameters
     */
    private void populateWikiPageObjet(){
        this.pnWikiPage.setEditDate(new Date());
        this.pnWikiPage.setEditedBy(new PnPerson(Integer.parseInt(SessionManager.getUser().getID())));
        this.pnWikiPage.setRecordStatus("A");
        this.pnWikiPage.setPageName(this.pnWikiPage.getPageName() != null ? this.pnWikiPage.getPageName().trim() : this.pnWikiPage.getPageName());
        //to set if page is creating
        if(this.pnWikiPage.getWikiPageId() == null){
            this.pnWikiPage.setCreatedBy(new PnPerson(Integer.parseInt(SessionManager.getUser().getID())));
            this.pnWikiPage.setCreatedDate(new Date());
            this.pnWikiPage.setAccessLevel(WikiConstants.PROJECT_LEVEL_ACCESS);
        }
    }
    
    /**
     * Assigning wiki page to an object
     */
    private void assignWikiPageToObject(){
        if(this.objectIdToAssign != null){
            pnWikiPageService.assignWikiPageToObject(this.pnWikiPage, this.objectIdToAssign);
            this.objectIdToAssign = null;
        }
    }
    
    private String getParentPageName() {
        if (this.pnWikiPage.getParentPageName() != null) {
            return this.pnWikiPage.getParentPageName().getPageName();
        } else {
            return this.pnWikiPage.getPageName();
        }
    }

    /**
     * Find and return the links of the pages which links to current page
     * @param request
     * @return JSONObject
     */
    public static Object whatLinksHere(Request request) {
		String currWikiPgName = request.getParameter("wikiPageName"); 
		String spaceId = request.getParameter("spaceId");
		
		PnWikiPage  currentWikiPage = pnWikiPageService.getWikiPageWithName(currWikiPgName, Integer.valueOf(spaceId));
		List<PnWikiPage> resultList = new ArrayList<PnWikiPage>();
		if( currentWikiPage != null ) {
			resultList = wikiProvider.whatLinksHere(currentWikiPage);
		}
		JSONArray record = new JSONArray();
		Iterator resultListIt = resultList.iterator();
		while( resultListIt.hasNext() ){
			PnWikiPage wikiPage = (PnWikiPage) resultListIt.next();
			String url = SessionManager.getJSPRootURL()+"/wiki/"+ getPagesToCall(wikiPage);
			JSONObject jsObject = new JSONObject();
			try {
				jsObject.put("id", wikiPage.getWikiPageId());
				jsObject.put("pageName", wikiPage.getPageName());
				jsObject.put("url", url);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			record.put(jsObject);
		}
		
		JSONObject resp = new JSONObject();
		try {
			resp.put("total", resultList.size());
			resp.put("results", record);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new TextStreamResponse("text", resp.toString());
	}
	
	/**
	 * Find and return the page links on the current page
	 * @param request
	 * @return JSONObject
	 */
	public static Object linksOnThisPage(Request request) {
		String currWikiPgName = request.getParameter("wikiPageName"); 
		String spaceId = request.getParameter("spaceId");	//parameter for tasks space (for page index option)
		
        PnWikiPage currWPg = pnWikiPageService.getWikiPageWithName(currWikiPgName, Integer.valueOf(spaceId));
		
		List<PnWikiPage> resultList = new ArrayList<PnWikiPage>();
		if( currWPg != null ) {
			resultList = wikiProvider.linksOnThisPage(currWPg);
		}
		JSONArray record = new JSONArray();
		Iterator resultListIt = resultList.iterator();
		while( resultListIt.hasNext() ){
			PnWikiPage wikipage = (PnWikiPage) resultListIt.next();
			String url = SessionManager.getJSPRootURL()+"/wiki/"+ getPagesToCall(wikipage);
			JSONObject jsObject = new JSONObject();
			try {
				jsObject.put("id", wikipage.getWikiPageId());
				jsObject.put("pageName", wikipage.getPageName());
				jsObject.put("url", url);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			record.put(jsObject);
		}

		JSONObject resp = new JSONObject();
		try {
			resp.put("total", resultList.size());
			resp.put("results", record);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new TextStreamResponse("text/json", resp.toString());
	}	
	
	public static PnWikiPage getRootWikiPageByName(String pageName){
		return pnWikiPageService.getRootPageByPageName(pageName);		
	}
	
	public static Object getWikiMarkupContent(Request request) {
		String currWikiPgName = request.getParameter("wikiPgName"); 
		String objectId = request.getParameter("objectId");

		PnWikiPage editingWikiPage = null;
		String wikiPageId = request.getParameter("wikiPageId");
		if( StringUtils.isNotEmpty(wikiPageId) ) {															// if wikiPageId is specified use it
			editingWikiPage = pnWikiPageService.get(Integer.valueOf(wikiPageId));
		} else {
			editingWikiPage = pnWikiPageService.getWikiPageWithName(currWikiPgName, Integer.valueOf(objectId));
		}
		String result;
		if( editingWikiPage != null ) {
			
			JSONObject jsObject = new JSONObject();
			try {
				jsObject.put("id", editingWikiPage.getWikiPageId());
				jsObject.put("pageName", editingWikiPage.getPageName());
				jsObject.put("content", editingWikiPage.getContent());
				jsObject.put("comment", editingWikiPage.getCommentText());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			JSONObject resp = new JSONObject();
			try {
				resp.put("results", jsObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			result = resp.toString();
		} else {
			result = "";
		}
		return new TextStreamResponse("text/json", result);
	}
	
	/**
	 * Method to get root wiki page name for space by checking if any another wiki page
	 * exist with the same name. Then return the page name by attaching the number of
	 * page instances of space name to the page name
	 * @param spaceName Name of the space
	 * @return page name
	 */
	public static String getRootWikiPageNameForSpace(String spaceName){
		List<PnWikiPage> pageList = pnWikiPageService.getRootWikiPagesByName(spaceName);
		if(CollectionUtils.isNotEmpty(pageList)){
			int pageIndex = 0;
			for(PnWikiPage page : pageList){
				if(page.getPageName().equals(spaceName) || page.getPageName().equals(spaceName+"_"+pageIndex) ){
					pageIndex++;
				}
			}
			return spaceName+"_"+pageIndex;
		} else {
			return spaceName;
		}
	}
    
	/**
	 * To get root page for space by space identifier
	 * 
	 * @param spaceId
	 * @return PnWikiPage instance
	 */
	public static PnWikiPage getRootPageForSpace(Integer spaceId){
		return pnWikiPageService.getRootPageOfSpace(spaceId);
	}
	
    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        //Image can not be edit.
        if(!(StringUtils.isNotEmpty(this.pnWikiPage.getPageName()) && this.pnWikiPage.getPageName().startsWith("Image:") && action.equals(WikiConstants.WIKI_EDIT_ACTION)))
        this.action = action;
    }

    /**
     * @return the pnWikiPage
     */
    public PnWikiPage getPnWikiPage() {
        return pnWikiPage;
    }

    /**
     * @param pnWikiPage the pnWikiPage to set
     */
    public void setPnWikiPage(PnWikiPage pnWikiPage) {
        this.pnWikiPage = pnWikiPage;
    }
    
    public boolean isEditPage(){
        return this.action.equals(WikiConstants.WIKI_EDIT_ACTION) || this.action.equals(WikiConstants.WIKI_PREVIEW_ACTION);
    }
    
    public boolean isPreviewPage(){
        return this.action.equals(WikiConstants.WIKI_PREVIEW_ACTION);
    }
    
    public String getPagesToCall(){
    	if(getParentPageName().equals(this.pnWikiPage.getPageName())){
            return WikiURLManager.encodePageName(this.pnWikiPage.getPageName());
        }else {
            return WikiURLManager.encodePageName(getParentPageName()) + "/" + WikiURLManager.encodePageName(this.pnWikiPage.getPageName());
        }
    }
    
    public static String getPagesToCall(PnWikiPage wikiPage){
        if(wikiPage.getParentPageName()== null){
           return  wikiPage.getPageName();
        }else {
            return  wikiPage.getParentPageName().getPageName() + "/" + wikiPage.getPageName();
        }
    }
    
    /**
	 * Check for wiki module access for current space for action
	 * 
	 * @param spaceId space identifier
	 * @param action view or modify or delete
	 * @return true or false
	 */	
	public boolean isAccessAllowed(String spaceId, Integer action) {
        boolean accessAllowed = false;
        try {
        if (StringUtils.isNotEmpty(spaceId)) {            
            SecurityProvider securityProvider = SessionManager.getSecurityProvider();
            
            // Security Check: Is user allowed access to requested module?
            Space testSpace = new ProjectSpaceBean();
            testSpace.setID(spaceId);
            securityProvider.setSpace(testSpace);
            if(action != null){
                    accessAllowed = securityProvider.isActionAllowed(null, Integer
                                    .toString(net.project.base.Module.WIKI), action);
            } else {
                    accessAllowed = securityProvider.isActionAllowed(null, Integer
                                    .toString(net.project.base.Module.WIKI), Action.VIEW);
                }
            }
        } catch (Exception e) {
            //This valid exception when user try to access wiki and loged in, Ignore it. 
        }
        return accessAllowed;
    }
	
	 /**
	 * Check for wiki module view access for current space
	 * 
	 * @param spaceId space identifier
	 * @return true or false
	 */
	public boolean isAccessAllowed(String spaceId){	
		return isAccessAllowed(spaceId, null);
	}

    public boolean isIndex(){
        return this.action.equals(WikiConstants.WIKI_RECENT_CHANGES_ACTION) 
        		|| this.action.equals(WikiConstants.WIKI_INDEX_ACTION)
        		|| this.action.equals(WikiConstants.WIKI_SHOW_IMAGES_ACTION);
        
    }
    
    public boolean isPageIndex(){
        return this.action.equals(WikiConstants.WIKI_INDEX_ACTION);
    }
    
    public boolean isHistory(){
        return this.action.equals(WikiConstants.WIKI_HISTORY_ACTION);
    }

    public boolean isVersion(){
        return this.action.equals(WikiConstants.WIKI_VERSION_ACTION);
    }
    
    public boolean isShowImages(){
        return this.action.equalsIgnoreCase(WikiConstants.WIKI_SHOW_IMAGES_ACTION);
    }
    
    public boolean isWiki() {
        return (this.action.equals(WikiConstants.WIKI_VIEW_ACTION) || this.action.equals(WikiConstants.WIKI_EDIT_ACTION)
                        || this.action.equals(WikiConstants.WIKI_PREVIEW_ACTION)) && !isPageDeleted() && !isPageNotExist();
    }
    
    public boolean isView() {
        return this.action.equals(WikiConstants.WIKI_VIEW_ACTION) && !pnWikiPage.getPageName().startsWith("Image:");
    }

    /**
     * @return the activateRevision
     */
    public Integer getActivateRevision() {
        return activateRevision;
    }

    /**
     * @param activateRevision the activateRevision to set
     */
    public void setActivateRevision(Integer activateRevision) {
        this.activateRevision = activateRevision;
    }

    /**
     * @return the pnWikiHistory
     */
    public PnWikiHistory getPnWikiHistory() {
        return pnWikiHistory;
    }

    /**
     * @return the wikiPageHistoryId
     */
    public Integer getWikiPageHistoryId() {
        return wikiPageHistoryId;
    }

    /**
     * @param wikiPageHistoryId the wikiPageHistoryId to set
     */
    public void setWikiPageHistoryId(Integer wikiPageHistoryId) {
        this.wikiPageHistoryId = wikiPageHistoryId;
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
    
    public void processRefLink(Request request) {
    	String redirctedFrom = request.getParameter("redirctedFrom");
        if (StringUtils.isNotEmpty(redirctedFrom) && redirctedFrom.equals("myAssignments")) {
            request.getSession(true).setAttribute("redirectFromUrl",(SessionManager.getJSPRootURL() + "/personal/Main.jsp?module="+Module.PERSONAL_SPACE+"&page=" + SessionManager.getJSPRootURL()
                    + "/assignments/My?module=" + Module.PERSONAL_SPACE));
        } else if (StringUtils.isNotEmpty(redirctedFrom) && redirctedFrom.equals("workPlanProject")) {
        	request.getSession(true).setAttribute("redirectFromUrl",(SessionManager.getJSPRootURL() + "/project/Main.jsp?module="+Module.PROJECT_SPACE+"&page=" + SessionManager.getJSPRootURL()
                    + "/workplan/taskview?module=" + Module.SCHEDULE));
        }else if (StringUtils.isNotEmpty(redirctedFrom) && redirctedFrom.equals("projectPortfolio")) {
        	request.getSession(true).setAttribute("redirectFromUrl",(SessionManager.getJSPRootURL() + "/portfolio/project"));
        }else {
        	removeRefLink(request);
        }
    }
    
    /**
	 * To check file type is of image type or not 
	 * 
	 * @param fileName name of file
	 * @return true or false
	 */
	public static boolean isFileTypeOfImage(String fileName) {
		boolean result = false;
		if( fileName.lastIndexOf('.') == -1 ) {
			return result;
		}
		String ext = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
		
		return ( ext.equalsIgnoreCase("gif") || ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg")
				|| ext.equalsIgnoreCase("jpeg")	|| ext.equalsIgnoreCase("bmp"));
	}

    /**
     * @return the refLink
     */
    public String getRefLink(Request request) {
        return (String)request.getSession(true).getAttribute("redirectFromUrl");
    }

    /**
     * @param refLink the refLink to remove
     */
    public void removeRefLink(Request request) {
        request.getSession(true).setAttribute("redirectFromUrl", null);
    }

    /**
     * @return the oldRevisionFoundMsg
     */
    public String getOldRevisionFoundMsg() {
        return oldRevisionFoundMsg;
    }
    
    public boolean isPageDeleted(){
        return StringUtils.isNotEmpty(this.pnWikiPage.getRecordStatus()) && this.pnWikiPage.getRecordStatus().equals("D");
    }
    
    public void reLoadWikiPage(){
        if(this.pnWikiPage.getWikiPageId() != null){
            this.pnWikiPage = pnWikiPageService.get(this.pnWikiPage.getWikiPageId());
        }
    }

    /**
     * @return true if action is defined else false
     */
    public boolean isUndefinedAction() {
        return !(this.action.equals(WikiConstants.WIKI_EDIT_ACTION)
                        || this.action.equals(WikiConstants.WIKI_VIEW_ACTION)
                        || this.action.equals(WikiConstants.WIKI_PREVIEW_ACTION)
                        || this.action.equals(WikiConstants.WIKI_DELETE_ACTION)
                        || this.action.equals(WikiConstants.WIKI_INDEX_ACTION)
                        || this.action.equals(WikiConstants.WIKI_RECENT_CHANGES_ACTION)
                        || this.action.equals(WikiConstants.WIKI_HISTORY_ACTION)
                        || this.action.equals(WikiConstants.WIKI_VERSION_ACTION)
                        || this.action.equals(WikiConstants.WIKI_SHOW_IMAGES_ACTION));
    }

    @Validate("required")
    public String getWikiPageNameField() {
    	if(this.action.equals(WikiConstants.WIKI_EDIT_ACTION)) {
    		this.wikiPageNameField = pnWikiPage.getPageName(); 
    	}
    	return wikiPageNameField;
    }
    
    public void setWikiPageNameField(String newWikiPageName) {
    	this.wikiPageNameField = newWikiPageName; 
    }
    
/**
 * Check if page name field is changed
 * @return true if page name is changed, false otherwise
 */
private boolean isPageNameChanged() {
	return !wikiPageNameField.trim().replaceAll(" ", "_").equals(this.pnWikiPage.getPageName().trim());
}

/**
 * Method called when wiki page name is changed.
 * Renaming supports MediaWiki renaming principle <a href="http://en.wikipedia.org/wiki/How_to_rename_a_page">MediaWiki_Rename</a>.
 * @param newPageName - new name of the page
 * @param wikiPageToRename - wiki page to rename
 */
private void renameWikiPage(String newPageName, PnWikiPage wikiPageToRename) {
//	System.out.println(" * Name is changed in edit form! Executing renameWikiPage method...");

	PnWikiPage newWikiPage = populateNewPageFields(newPageName, wikiPageToRename);
	pnWikiPageService.renameWikiPage(newWikiPage, wikiPageToRename);
}

private PnWikiPage populateNewPageFields(String newPageName, PnWikiPage wikiPageToRename) {
	//create new wiki page and copy old page fields in it
	PnPerson person = ServiceFactory.getInstance().getPnPersonService().getPerson(Integer.valueOf(SessionManager.getUser().getID()) );
	
	// set new page properties
	PnWikiPage newWikiPage = new PnWikiPage();
	newWikiPage.setPageName(newPageName.trim().replaceAll(" ", "_"));
	newWikiPage.setOwnerObjectId(wikiPageToRename.getOwnerObjectId());
	newWikiPage.setCommentText(wikiPageToRename.getCommentText());
	newWikiPage.setContent(wikiPageToRename.getContent());
	newWikiPage.setCreatedBy(wikiPageToRename.getCreatedBy());
	newWikiPage.setCreatedDate(wikiPageToRename.getCreatedDate());
	newWikiPage.setRecordStatus(wikiPageToRename.getRecordStatus());
	newWikiPage.setParentPageName(wikiPageToRename.getParentPageName());
	newWikiPage.setAccessLevel(wikiPageToRename.getAccessLevel());
	newWikiPage.setEditDate(new Date());
	newWikiPage.setEditedBy(person);
	
	return newWikiPage;
}

/**
 * @param wikiPage
 */
public void editDifference (){
	PnWikiPage wikiPage = ServiceFactory.getInstance().getPnWikiPageService().getWikiPageWithPageNameAndContent(this.pnWikiPage.getWikiPageId());
	userPageString = this.pnWikiPage.getContent();
	otherUserPageString = wikiPage.getContent();
    beforeEditedUserName = wikiPage.getEditedBy().getDisplayName();
}

/**
 * @return the otherUserPageString
 */
public String getOtherUserPageString() {
	return otherUserPageString;
}

/**
 * @param otherUserPageString the otherUserPageString to set
 */
public void setOtherUserPageString(String otherUserPageString) {
	this.otherUserPageString = otherUserPageString;
}

/**
 * @return the userPageString
 */
public String getUserPageString() {
	return userPageString;
}

/**
 * @param userPageString the userPageString to set
 */
public void setUserPageString(String userPageString) {
	this.userPageString = userPageString;
}

/**
 * @return the beforeEditedUserName
 */
public String getBeforeEditedUserName() {
	return beforeEditedUserName;
}

/**
 * @param beforeEditedUserName the beforeEditedUserName to set
 */
public void setBeforeEditedUserName(String beforeEditedUserName) {
	this.beforeEditedUserName = beforeEditedUserName;
}

/**
 * Converts the wiki to XML representation without the XML version tag.
 * This method returns the Wiki as XML text.
 *
 * @return XML representation
 */
public String getXmlBody() {
   StringBuffer sb = new StringBuffer();
   String wikiUrl = SessionManager.getJSPRootURL() + "/wiki/"+WikiURLManager.getRootWikiPageNameForSpace()+"?op=recent_changes";
   sb.append("<Wiki>\n");
   sb.append("<WikiPageName>" + XMLUtils.escape(this.pnWikiPage.getPageName()) + "</WikiPageName>\n");
   sb.append("<WikiPageUrl>" +  XMLUtils.escape(wikiUrl) + "</WikiPageUrl>\n");
   sb.append("</Wiki>\n");
   return sb.toString();
}
}
