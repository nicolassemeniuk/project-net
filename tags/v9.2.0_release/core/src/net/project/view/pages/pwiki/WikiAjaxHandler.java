/**
 * 
 */
package net.project.view.pages.pwiki;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import net.project.hibernate.model.PnWikiAssignment;
import net.project.hibernate.model.PnWikiAttachment;
import net.project.hibernate.model.PnWikiHistory;
import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.IPnWikiAttachmentService;
import net.project.hibernate.service.IPnWikiPageService;
import net.project.hibernate.service.IWikiProvider;
import net.project.hibernate.service.ServiceFactory;
import net.project.notification.EventCodes;
import net.project.project.ProjectSpaceBean;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.util.StringUtils;
import net.project.view.pages.Wiki;
import net.project.view.pages.base.BasePage;
import net.project.wiki.WikiManager;
import net.project.wiki.WikiPageFinder;
import net.project.wiki.WikiURLManager;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.util.TextStreamResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author 
 */
public class WikiAjaxHandler extends BasePage {
	
    @Inject
    private Request request;
    
    @InjectPage
    private Wiki wiki;
    
    private static Logger log = Logger.getLogger(WikiAjaxHandler.class);
    
    Object onActivate(String action) {
    	try {
    		Space checkSpace = new ProjectSpaceBean();
    		if (StringUtils.isNotEmpty(request.getParameter("spaceId"))) {
				checkSpace.setID(request.getParameter("spaceId"));
			} else {
				checkSpace = getUser().getCurrentSpace();
			}
			
			if(!isAccessAllowed(checkSpace, Module.WIKI, Action.VIEW, getUser())){
				getRequest().setAttribute("exception", new net.project.security.AuthorizationFailedException(PropertyProvider.get("prm.project.main.authorizationfailed.message"), checkSpace));
				return new TextStreamResponse("Text", "/AccessDenied.jsp?spaceId="+checkSpace.getID());
			}
    	} catch (Exception e) {
			log.error("Error occured while handling access permissions for wiki:" + e.getMessage());
		}
		
		if (StringUtils.isNotEmpty(action) && action.equalsIgnoreCase("isPageExist")) {
			return isPageExist();
		} else if (StringUtils.isNotEmpty(action) && action.equalsIgnoreCase("whatLinksHere")) {
			return WikiManager.whatLinksHere(request);
		} else if (StringUtils.isNotEmpty(action) && action.equalsIgnoreCase("linksOnThisPage")) {
			return WikiManager.linksOnThisPage(request);
		} else if (StringUtils.isNotEmpty(action) && action.equalsIgnoreCase("getPageContent")) {
			return getPageContent();
		} else if (StringUtils.isNotEmpty(action) && action.equalsIgnoreCase("assignWikiPageToObject")) {
			return assignWikiPageToObject();
		} else if (StringUtils.isNotEmpty(action) && action.equalsIgnoreCase("unassignWikiPageFromObject")) {
			return unassignWikiPageFromObject();
		} else if (StringUtils.isNotEmpty(action) && action.equalsIgnoreCase("getCreateObjectWikiPageURL")) {
			return getCreateObjectWikiPageURL();
		} else if (StringUtils.isNotEmpty(action) && action.equalsIgnoreCase("getEditPageURL")) {
			return getEditPageURL();
		} else if (StringUtils.isNotEmpty(action) && action.equalsIgnoreCase("deletePage")) {
			return deletePage();
		} else if (StringUtils.isNotEmpty(action) && action.equalsIgnoreCase("deleteAttachment")) {
			return deleteAttachment();
		} else if (StringUtils.isNotEmpty(action) && action.equalsIgnoreCase("showAttachments")) {
			return showAttachments();
		} else if (StringUtils.isNotEmpty(action) && action.equalsIgnoreCase("renamePage")){
			return renamePageName();
		} else {
			return new TextStreamResponse("text/plain", "");
		}
    }
    
    /**
	 * Method to rename wiki page
	 * 
	 * @return url
	 */
	private Object renamePageName() {
		try{
			if (StringUtils.isNotEmpty(request.getParameter("wikiPageId"))) {
				PnWikiPage pageToEdit = ServiceFactory.getInstance().getPnWikiPageService().get(
						Integer.parseInt(request.getParameter("wikiPageId")));
				String oldPageName = pageToEdit.getPageName();
	
				pageToEdit.setPageName(WikiURLManager.converToWikiPageName(request.getParameter("editedPageName")));
				pageToEdit.setPageName( pageToEdit.getPageName() != null ? pageToEdit.getPageName().trim() : pageToEdit.getPageName());	// ensure that page name is trimmed 
				ServiceFactory.getInstance().getPnWikiPageService().update(pageToEdit);
	
				PnWikiHistory wikiHistory = new PnWikiHistory();
				wikiHistory.setWikiHistoryId(null);
				wikiHistory.setContent(pageToEdit.getContent());
				wikiHistory.setCommentText("Old name: " + oldPageName.replace("_",   " ") + ", New name: " + pageToEdit.getPageName().replace("_", " "));
				wikiHistory.setEditDate(new Date());
				wikiHistory.setEditedBy(new PnPerson(getUser()));
				wikiHistory.setWikiPageId(pageToEdit);
				wikiHistory.setEdityType("edit_name");
				ServiceFactory.getInstance().getPnWikiHistoryService().save(wikiHistory);
				return new TextStreamResponse("text/plain", getJSPRootURL() + "/wiki/" + pageToEdit.getPageName());
			}
		} catch (Exception e) {
				log.error("Error occured while rename wiki page" + e.getMessage());
		}
		return new TextStreamResponse("text/plain", "failed");
	}
    
    /**
	 * This method is written for deleting attachment of image type only TODO: when other file types supported this
	 * method will needs to be modified
	 * 
	 * @return Delete acknowledgment
	 */
	private Object deleteAttachment() {
		String wikiPageId = request.getParameter("wikiPageId");
		String ownerObjectId = request.getParameter("ownerObjectId");
		String imageName = request.getParameter("imageName");
		try {
			if(StringUtils.isNotEmpty(wikiPageId) && StringUtils.isNotEmpty(ownerObjectId) && StringUtils.isNotEmpty(imageName)){
				IPnWikiAttachmentService pnWikiAttachmentService = ServiceFactory.getInstance().getPnWikiAttachmentService();
				IPnWikiPageService pnWikiPageService = ServiceFactory.getInstance().getPnWikiPageService();
				PnWikiPage imagePageToDelete = pnWikiPageService.getWikiPageWithName("Image:"+imageName, Integer.valueOf(ownerObjectId));
				if(imagePageToDelete != null){
					pnWikiAttachmentService.deleteAttachmentFromWiki(Integer.valueOf(imagePageToDelete.getWikiPageId()), imageName);
					ServiceFactory.getInstance().getPnWikiAssignmentService().deleteWikiAssignmentsByPageId(imagePageToDelete.getWikiPageId());
					pnWikiPageService.deleteWikiPageById(imagePageToDelete.getWikiPageId());
				}
				pnWikiAttachmentService.deleteAttachmentFromWiki(Integer.valueOf(wikiPageId), imageName);
				pnWikiPageService.deleteWikiPageByName("Image:"+imageName, Integer.valueOf(ownerObjectId));	
				
				// publishing event to asynchronous queue
				try {	
		        	WikiEvent wikiEvent = (WikiEvent) EventFactory.getEvent(ObjectType.WIKI, EventType.DELETED);
		        	wikiEvent.setObjectID(imagePageToDelete.getWikiPageId().toString());
		        	wikiEvent.setObjectType(ObjectType.WIKI);
		        	wikiEvent.setName(imageName);
	            	wikiEvent.setObjectRecordStatus("D");
	            	if(imagePageToDelete.getParentPageName() != null)
		        		wikiEvent.setParentObjectId(imagePageToDelete.getParentPageName().getWikiPageId().toString());
		        	wikiEvent.publish();
				} catch (EventException ex) {
					Logger.getLogger(WikiAjaxHandler.class)
							.error("WikiAjaxHandler.deleteAttachment():: Wiki Page Remove Event Publishing Failed! "+ ex.getMessage());
				}
				return new TextStreamResponse("text/plain", "deleted successfully");
			}
		} catch (Exception e) {
			Logger.getLogger(WikiAjaxHandler.class).error("Error occurred while deleting attachment from wiki : "+e.getMessage());
		}
		return new TextStreamResponse("text/plain", "deletion failed");
	}

	/**
	 * @return
	 */
	private Object deletePage() {
	   try {
		 	if(StringUtils.isNotEmpty(request.getParameter("wikiPageId"))){
				PnWikiPage pageToDelete = ServiceFactory.getInstance().getPnWikiPageService().get(
						Integer.parseInt(request.getParameter("wikiPageId")));
				// delete wiki page from wiki space
				if (pageToDelete != null && pageToDelete.getOwnerObjectId() != null) {
					// is selected page assigned to assignment objects, if it is - delete appropriate records from
					// PN_WIKI_ASSIGNMENT table
	
					pageToDelete.setRecordStatus("D");
					ServiceFactory.getInstance().getPnWikiPageService().update(pageToDelete);
	
                    //Create notification. 
					net.project.wiki.WikiEvent event = new net.project.wiki.WikiEvent();
					WikiManager wikiManager = new WikiManager(pageToDelete);
	    			if(pageToDelete.getParentPageName() != null){
						event.setSpaceID(SessionManager.getUser().getCurrentSpace().getID());
						event.setTargetObjectID(pageToDelete.getParentPageName().getWikiPageId().toString());
						event.setTargetObjectType(EventCodes.DELETE_WIKI_PAGE);
						event.setTargetObjectXML(wikiManager.getXmlBody());
						event.setEventType(EventCodes.DELETE_WIKI_PAGE);
						event.setUser(SessionManager.getUser());
						event.setDescription("Wiki page deleted : \"" + pageToDelete.getPageName() + "\"");
						event.store();
		    			}
					// publishing event to asynchronous queue
					WikiEvent wikiEvent = (WikiEvent) EventFactory.getEvent(ObjectType.WIKI, EventType.DELETED);
					wikiEvent.setObjectID(pageToDelete.getWikiPageId().toString());
					wikiEvent.setObjectType(ObjectType.WIKI);
					wikiEvent.setName(pageToDelete.getPageName());
					wikiEvent.setObjectRecordStatus("D");
					if (pageToDelete.getParentPageName() != null)
						wikiEvent.setParentObjectId(pageToDelete.getParentPageName().getWikiPageId().toString());
					wikiEvent.publish();
					return new TextStreamResponse("text/plain", "true");
				}
		 	}	
		} catch (EventException ex) {
			Logger.getLogger(WikiAjaxHandler.class)
					.error("WikiAjaxHandler.deletePage():: Wiki Page Remove Event Publishing Failed! "+ ex.getMessage());
		} catch (Exception e) {
			Logger.getLogger(WikiAjaxHandler.class)
					.error("Error occurred while deleting wiki page : " + e.getMessage());
		}
		return new TextStreamResponse("text/plain", "false");
	}

	private Object isPageExist(){
        return new TextStreamResponse("text/plain", WikiPageFinder.isPageExist(request.getParameter("wikiPageName"), Integer.parseInt(request.getParameter("spaceId"))).toString());
    }
    
    private Object assignWikiPageToObject() {
        String ownerSpaceId = request.getParameter("spaceId");
        String objectId = request.getParameter("objectId");
        String wikiPgName = request.getParameter("wikiPgName");
        
        PnWikiPage selectedWikiPage = ServiceFactory.getInstance().getPnWikiPageService().getWikiPageWithName(wikiPgName, Integer.valueOf(ownerSpaceId));
        ServiceFactory.getInstance().getPnWikiPageService().assignWikiPageToObject(selectedWikiPage, Integer.valueOf(objectId));
        return new TextStreamResponse("text/plain", "");
    }
    
    private Object unassignWikiPageFromObject() {
        String ownerSpaceId = request.getParameter("spaceId");
        String objectId = request.getParameter("objectId");
        
        // get currently assigned wiki page 
        PnWikiAssignment pnWikiAssignment = ServiceFactory.getInstance().getPnWikiAssignmentService()
        									.getWikiAssignmentByObjectId(Integer.valueOf(objectId));        	
        
        if(pnWikiAssignment != null) {
        	// record found - delete it
	        ServiceFactory.getInstance().getPnWikiAssignmentService().delete(pnWikiAssignment);
	        return new TextStreamResponse("text/plain", "true");
        }
        
        return new TextStreamResponse("text/plain", "false");
    }
    
    private Object getPageContent() {
        IPnWikiPageService pnWikiPageService = ServiceFactory.getInstance().getPnWikiPageService();
        IWikiProvider wikiProvider = ServiceFactory.getInstance().getWikiProvider();
        
        String currWikiPgName = request.getParameter("wikiPgName");
        String objectId = request.getParameter("objectId");
        String projectSpaceId = request.getParameter("spaceId");
        PnWikiPage wikiPage =  null;
        String redirctedFrom = "?redirctedFrom="+request.getParameter("wikiItFor");
        
        if( currWikiPgName != null ) {
            wikiPage = pnWikiPageService.getWikiPageWithName(currWikiPgName, Integer.valueOf(projectSpaceId));
        }
        
        String content = null;
        String objectType = request.getParameter("objectType");
        String objName = request.getParameter("objectName");

        Integer wikiPgId = null;
        if(StringUtils.isNotEmpty(request.getParameter("wikiPageId"))) {
        	wikiPgId = Integer.parseInt(request.getParameter("wikiPageId"));
        }
        
        // setting spaceId - set this to request param for object
        if(StringUtils.isEmpty(objectId) || (StringUtils.isNotEmpty(objectId) && objectId.equals("0")))
            return new TextStreamResponse("text", "<br /><div style=\"padding-left: 15px;\"><label style=\"text-align: center; color: #848484; \">"+PropertyProvider.get("prm.schedule.wiki.selecttask.message")+"</label>");

        if (StringUtils.isNotEmpty(objectType) && !objectType.equalsIgnoreCase(ObjectType.PROJECT) &&  !objectType.equalsIgnoreCase(ObjectType.BUSINESS) && !objectType.equalsIgnoreCase(ObjectType.TASK) && !objectType.equalsIgnoreCase(ObjectType.FORM_DATA)) {
            return new TextStreamResponse("text", "<br /><div style=\"padding-left: 15px;\"><label style=\"text-align: center; color: #848484; \">"+PropertyProvider.get("prm.schedule.wiki.doesnotsupportforobject.message")+"</label>");
        }   
        
        if ((objectType.equals(ObjectType.PROJECT) || objectType.equals(ObjectType.BUSINESS)) && StringUtils.isNotEmpty(request.getParameter("isPreview"))
                && "false".equals(request.getParameter("isPreview")) ) {
        	
        	StringBuffer projectContent = new StringBuffer();
        	StringBuffer header = new StringBuffer("<div id=\"projectPageIndex\" style=\"padding: 20px;\" class=\"wContent\" >");
        	if(StringUtils.isNotEmpty(request.getParameter("isIndex")) && "true".equals(request.getParameter("isIndex"))) {
        		// get wiki page index
	            List<PnWikiPage> pageIndex = pnWikiPageService.getWikiPagesByOwnerAndRecordStatus(Integer.valueOf(projectSpaceId), "A");
	            
	            if( pageIndex != null && pageIndex.size() != 0) {   
	                projectContent.append( wikiProvider.wikiPagesIndex(Integer.valueOf(projectSpaceId), Integer.valueOf(objectId), true) ); 
	            } else {                        /* if project doesn't have wiki created - don't allow creation of assignment wiki page */
	                projectContent.append(PropertyProvider.get("prm.wiki.doesnothavecreatedwiki.message", objectType) +
	                   "&nbsp;<a href=\""+getJSPRootURL() + "/"+ objectType + "/Main.jsp?id="+projectSpaceId+"&module="+Module.PROJECT_SPACE+"&page="+getJSPRootURL()+"/wiki/"+redirctedFrom+"\">"+PropertyProvider.get("prm.wiki.createit.link.label")+"</a>");
	            }
        	} else {
        		// get root wiki page
        		PnWikiPage rootPage = pnWikiPageService.getRootPageOfSpace(Integer.valueOf(projectSpaceId));
        		if(rootPage != null) {
            		header = new StringBuffer("<div id=\"wEContent\" class=\"wContent\" style=\"padding: 10px;\">");
        			projectContent.append(wikiProvider.convertToHtmlNew(rootPage, false));
        		} else {
            		header = new StringBuffer("<div id=\"newWikiCreateLink\" class=\"wContent\" style=\"padding: 10px;\">");
	                projectContent.append(PropertyProvider.get("prm.wiki.doesnothavecreatedwiki.message", objectType) +
	 	                   "<a href=\""+getJSPRootURL() + "/"+ objectType + "/Main.jsp?id="+projectSpaceId+"&module="+Module.PROJECT_SPACE+"&page="+getJSPRootURL()+"/wiki/"+redirctedFrom+"\">"+PropertyProvider.get("prm.wiki.createit.link.label")+"</a>");
        		}
        			
        	}
            return new TextStreamResponse("text/plain; charset=utf-8", header + projectContent.toString() + "</div><br/><br/>");
        } else {
        	/* bug-2622 fix - start */
        	if(wikiPgId != null) {
        		wikiPage = pnWikiPageService.getWikiPage(wikiPgId);
        	}
        	if(wikiPage == null) {
        		/* bug-2622 fix - end */
        		/* is wiki already created - does it have wiki pages */
	            wikiPage = pnWikiPageService.getWikiPageWithName(WikiURLManager.converToWikiPageName(objName), Integer.valueOf(objectId));
        	}
        }
        
        if( wikiPage != null ) {        /* wiki for selected object - EXISTS */
            /* if previewing owner's object page for assigning page to assignment object - show it in preview mode */
            String isPreviewContent = request.getParameter("isPreview");
            
            content = wikiProvider.convertToHtmlNew(wikiPage, false);      // replace spaceId with passed request param
           
            String header = "<div id=\"wEContent\" class=\"wContent\" style=\"padding: 10px;\">";
            return new TextStreamResponse("text", header + content + "</div><br/><br/>");
            
        } else {                        /* wiki for selected object is not created - DOESN'T EXIST */
            /* different functionality for TASK/FORM wiki (select page from project index) */
            if( objectType.equalsIgnoreCase("task") || objectType.equalsIgnoreCase("form_data") ) {
                List<PnWikiPage> pageIndex = pnWikiPageService.getWikiPagesByOwnerAndRecordStatus(Integer.valueOf(projectSpaceId), "A");
                StringBuffer result;
                if( pageIndex != null && pageIndex.size() != 0) {   /* added if/else block for not giving the possibility to create assingment page if owning project doesn't have wiki created */
                    result = new StringBuffer("<div id=\"wEContent\" class=\"wContent\" style=\"padding: 20px;\">");
                    result.append("<br/><a id=\"createWikiFromScratch\" href=\"").append("javascript:createWikiPageFormScratch();" );
                    result.append("\">"+PropertyProvider.get("prm.wiki.createpagefromscratch.message")+"</a>").append("<br/><br/>");

                    result.append(PropertyProvider.get("prm.wiki.chooseone.message")+"<br/>");
                    result.append(wikiProvider.wikiPagesIndex(Integer.valueOf(projectSpaceId), Integer.valueOf(objectId), true));   //put projectId as argument to which space selected object belongs to
                    
                } else {                        /* if owning project doesn't have wiki created - don't allow creation of assignment wiki page */
                    result = new StringBuffer("<div id=\"newWikiCreateLink\" class=\"wContent\" style=\"padding: 20px;\">");
                    result.append(PropertyProvider.get("prm.wiki.partofwiki.message")+"<br> <b>"+PropertyProvider.get("prm.wiki.createwikifirst.message")+"</b>");

                }
                result.append("</div>");
                return new TextStreamResponse("text", result.toString());
                /* TASK/FORM specific functionality end */
            }

            return new TextStreamResponse("text", "<br /><b>"+PropertyProvider.get("prm.wiki.objecthasnowiki.message")+"</b>&nbsp;<a href=\"" 
                    + getJSPRootURL()+ "/wiki/" + WikiURLManager.converToWikiPageName(objName) + "\">"+PropertyProvider.get("prm.wiki.createwiki.link.label")+"</a>");
        }
    }
    
    private Object getEditPageURL() {
        String objName = request.getParameter("wikiPageName");
        String objectId = request.getParameter("objectId");
        String spaceId = request.getParameter("spaceId");
        PnWikiPage wikiPage;
        if (StringUtils.isEmpty(objName)){
        	wikiPage = ServiceFactory.getInstance().getPnWikiPageService().getRootPageOfSpace(Integer.parseInt(spaceId));
        } else {
            wikiPage = ServiceFactory.getInstance().getPnWikiPageService().getWikiPageWithName(WikiURLManager.converToWikiPageName(objName), Integer.valueOf(objectId));
        }
        //if page is null get it by ownerObjectId(SpaceId), This will happen if wiki page is not assigned to any object. 
        if(wikiPage == null){
            wikiPage = ServiceFactory.getInstance().getPnWikiPageService().getWikiPageWithName(WikiURLManager.converToWikiPageName(objName), Integer.valueOf(spaceId));
        }
        return new TextStreamResponse("text/plain", getEncodedUrl(wikiPage));
    }
    
    private Object getCreateObjectWikiPageURL() {
        String objName = request.getParameter("wikiPageName");
        String objectId = request.getParameter("objectId");
        String spaceId = request.getParameter("spaceId");
        wiki.setObjectIdToAssign(Integer.valueOf(objectId));
        return new TextStreamResponse("text/plain", getEncodedUrl(new PnWikiPage(null, WikiURLManager.converToWikiPageName(objName), null, ServiceFactory.getInstance().getPnWikiPageService().getRootPageOfSpace(Integer.valueOf(spaceId)), null)));
    }
    
    private String getEncodedUrl(PnWikiPage wikiPage){
        String redirctedFrom = "";
        if(StringUtils.isNotEmpty(request.getParameter("wikiItFor"))){
            redirctedFrom = "&redirctedFrom="+request.getParameter("wikiItFor");
        }
        if(wikiPage != null){
            try {
                return URLEncoder.encode(getJSPRootURL() + "/wiki/" + WikiManager.getPagesToCall(wikiPage) + "?op=" + WikiConstants.WIKI_EDIT_ACTION + redirctedFrom, 
                                SessionManager.getCharacterEncoding());
            } catch (UnsupportedEncodingException pnetEx) {
                Logger.getLogger(WikiAjaxHandler.class).error("Error occurred while encoding url: " + pnetEx.getMessage());
            }
        }
        return getJSPRootURL() + "/wiki";
    }
    
    /**
	 * Handler method for Action Link <i>Attachments</i>, to retrieve list of all images uploaded to current wiki space (with <b>spaceId</b>)
	 * @return JSONObject with all images uploaded to current wiki space.
	 */
	public Object showAttachments() {
		String result;
		Integer spaceId = Integer.valueOf(request.getParameter("spaceId"));
		IPnWikiAttachmentService pnWikiAttachmentService = ServiceFactory.getInstance().getPnWikiAttachmentService();
		List<PnWikiAttachment> imgDetailPgs = pnWikiAttachmentService.getAllImagesFromWiki(spaceId);
		String dateFormat = "E, MMM dd, yyyy, HH:mm a";
		net.project.util.DateFormat userDateFormat = SessionManager.getUser().getDateFormatter();
		PnWikiPage rootPage = WikiManager.getRootPageForSpace(spaceId);
				
		if( imgDetailPgs != null && imgDetailPgs.size() > 0 ) {
			JSONArray jsArr = new JSONArray();
			Iterator<PnWikiAttachment> imgIt = imgDetailPgs.iterator();
			
			while( imgIt.hasNext() ) {
				PnWikiAttachment imgDetails = imgIt.next();
				// get image details
				JSONObject jsObject = new JSONObject();
				
				try {
					StringBuffer imageLink = new StringBuffer();
					imageLink.append( SessionManager.getJSPRootURL() )
							 .append("/servlet/ViewWikiImage?imageName=").append(imgDetails.getAttachmentName())
							 .append("&wikiPageName=").append(WikiURLManager.getRootWikiPageNameForSpace())
							 .append("&ownerObjectId=").append(rootPage.getWikiPageId())	//spaceId)
							 .append("&isRootPage=").append(false)
							 .append("&module=").append(Module.DIRECTORY).toString();
					jsObject.put("id", imgDetails.getWikiAttachmentId());		// ID of PnWikiAttachment record
					jsObject.put("name", imgDetails.getAttachmentName());
					jsObject.put("lastmod", userDateFormat.formatDate(imgDetails.getAttachedOnDate(), dateFormat));
					jsObject.put("comment", imgDetails.getDescription() != null ? imgDetails.getDescription() : "Not commented!");
					jsObject.put("url", imageLink.toString());
					jsArr.put(jsObject);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			JSONObject jsResult = new JSONObject();
			try {
				jsResult.put("images", jsArr);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			result = jsResult.toString();
		} else {
			result = "";
		}
		return new TextStreamResponse("text", result);
	}
    
}
