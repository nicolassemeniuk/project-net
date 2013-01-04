/**
 * 
 */
package net.project.wiki;

import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.IPnWikiPageService;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.util.StringUtils;

/**
 *
 */
public class WikiPageFinder {
    
    /**
     * this is to find appropriate wiki page by all page name supplied in browser address bar. 
     * @param pageNames[] 
     * @return PnWikiPage
     */
    public static PnWikiPage findPage(Object[] pageNames, String requestPath) {
        PnWikiPage currentPage = null;
        PnWikiPage spaceRootpage = null;
        IPnWikiPageService pnWikiPageService = ServiceFactory.getInstance().getPnWikiPageService();
        
        if (SessionManager.getUser() != null && SessionManager.getUser().getCurrentSpace() != null) {
            spaceRootpage = pnWikiPageService.getRootPageOfSpace(Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID()));
        }
        
        //procees to find page which is requested in url 
        if (pageNames.length > 0) {
            currentPage = pnWikiPageService.getRootPageByPageName(pageNames[0].toString());
			if(currentPage == null) {
				currentPage = pnWikiPageService.getWikiPageWithName(pageNames[0].toString(), Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID()));
			}
            if (currentPage != null && pageNames.length > 1) {
//              PnWikiPage nextPage = pnWikiPageService.getChildWikiPageByName(pageNames[pageNames.length - 1].toString(), currentPage.getWikiPageId());
            	PnWikiPage nextPage = pnWikiPageService.getWikiPageWithName(pageNames[pageNames.length - 1].toString(), currentPage.getOwnerObjectId().getObjectId());
                if(nextPage == null && currentPage != null){
                	currentPage = new PnWikiPage(null, WikiURLManager.converToWikiPageName(pageNames[pageNames.length - 1].toString()), null, currentPage, ServiceFactory.getInstance().getPnObjectService().getObject(currentPage.getOwnerObjectId().getObjectId())); 
                } else {
                    currentPage = nextPage;
                }
            }
        } else {
            //if page is not requested, open current space root page as default page. 
            currentPage = spaceRootpage; 
        }
        
        //if the requested page is not available but a space root page is there.
        if(currentPage == null && spaceRootpage != null){
        	if (SessionManager.getUser() != null && SessionManager.getUser().getCurrentSpace() != null && requestPath.endsWith("wiki.form")) {
        		// get wiki page from space if available. Fix for when two pages opened in separate tabs the first tab page if submitted then
        		// pagenames array gives only one parameter intead of two though there are two parameters in url
        		currentPage = pnWikiPageService.getWikiPageWithName(pageNames[0].toString(), Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID()));
            }
        	if (currentPage == null){
	            // create a new object by setting space root page as parent page [PnWikiPage(pageId, pageName, pagecontent, parentpage, PnObjet{this is actually space})] 
	            currentPage = new PnWikiPage(null, WikiURLManager.converToWikiPageName(pageNames[0].toString()), null, spaceRootpage, spaceRootpage.getOwnerObjectId());
        	}
        } else if(currentPage == null && SessionManager.getUser() != null && SessionManager.getUser().getCurrentSpace() != null){
            // if no any requested page or no any current sapce root page is found return this as a root page
            currentPage = new PnWikiPage(null, WikiURLManager.getRootWikiPageNameForSpace(), null, null, ServiceFactory.getInstance().getPnObjectService().getObject(Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID())));
        } else if(currentPage == null) {
            currentPage = new PnWikiPage();
        }
        return currentPage;
    }
    
    /**
     * This method is to find page for any object, It will autometically create space root wiki page if it is not exist.  
     * @param objectId
     * @param spaceId
     * @param spaceName
     * @return wikipage for object.
     */
    public static PnWikiPage findPageForObject(String objectId, String spaceId, String spaceName){
        PnWikiPage wikiPage = null;
        PnWikiPage wikiPageForObject = null;
        IPnWikiPageService pnWikiPageService = ServiceFactory.getInstance().getPnWikiPageService();
        
        if(StringUtils.isNotBlank(objectId) && StringUtils.isNumeric(objectId)){
            wikiPage = pnWikiPageService.getWikiPageByObjectId(Integer.parseInt(objectId));
            if(wikiPage != null) wikiPageForObject = wikiPage;
        } 
        //Check if there any wiki pages for this object's space.
        if(wikiPage == null){
           wikiPage = pnWikiPageService.getRootPageOfSpace(Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID()));
        }
        //If wiki page is not found for space create it.
        if(wikiPage == null){
           WikiManager wikiMnager = new WikiManager(new PnWikiPage(null, WikiManager.getRootWikiPageNameForSpace(spaceName), " ", null, ServiceFactory.getInstance().getPnObjectService().getObject(Integer.parseInt(spaceId))));
           wikiMnager.saveWikiPage();
        }
        return wikiPageForObject;
    }
    
    public static Boolean isPageExist(String pageName, Integer spaceId){
        return ServiceFactory.getInstance().getPnWikiPageService().doesWikiPageWithGivenNameExist(pageName, spaceId);
    }
}
