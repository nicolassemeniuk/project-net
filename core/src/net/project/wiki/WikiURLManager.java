/**
 * 
 */
package net.project.wiki;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.space.ISpaceTypes;
import net.project.space.Space;
import net.project.space.SpaceFactory;

import org.apache.log4j.Logger;
import org.apache.tapestry5.services.Response;

/**
 * 
 */
public class WikiURLManager {
    
    private static Logger log = Logger.getLogger(WikiURLManager.class);
    
    // Changes in this string must be reflect in removeSpecialCharFromPageName() method of wiki.js
    public static final String NOT_SUPPORTED_CHARACTERS_IN_WIKI_PAGE_NAME  = "~!@#$%^&*()',`/|;+=[]<>{}?\"&";
    
    /**get url for wiki root page of current space, which will able to open space's root wiki page  
     * @return String page url
     */
    public static String getFirstPageUrl(){
        return  SessionManager.getJSPRootURL() + "/wiki/Welcome/"+SessionManager.getUser().getCurrentSpace().getID()+"/"+SessionManager.getUser().getID()+"?module="+Module.WIKI;
    }
    
    /**
     * Get suitable wiki root page name for current space 
     * @return String PageName
     */
    public static String getRootWikiPageNameForSpace(){
    	if(SessionManager.getUser() != null && SessionManager.getUser().getCurrentSpace() != null ){
	    	String spaceId = SessionManager.getUser().getCurrentSpace().getID();
	    	String spaceName = SessionManager.getUser().getCurrentSpace().getName().replaceAll(" ", "_");
	    	PnWikiPage wikiPage = WikiManager.getRootPageForSpace(Integer.parseInt(spaceId));
	    	if (wikiPage == null){
				return converToWikiPageName(WikiManager.getRootWikiPageNameForSpace(spaceName));
	    	} else {
				return converToWikiPageName(wikiPage.getPageName());
	    	}
    	} else {
    		return "";
    	}
    	
    }
    
    /**
     *  Get wiki root page name for spaceid
     * @param spaceId
     * @param returnIfNull
     * @return pageName
     */
    public static String getRootWikiPageNameBySpaceId(Integer spaceId, String returnIfNull){
        PnWikiPage wikiPage = ServiceFactory.getInstance().getPnWikiPageService().getRootPageOfSpace(spaceId);
        if(wikiPage != null){
            return wikiPage.getPageName();
        }else{
            return returnIfNull;
        }
    }
    
    /**
     * This method checks if new page is requested, by comparing open page and requested page. 
     * @param pageNames
     * @param wikiViewManager
     * @return boolean isNewWikiPageRequested
     */
    public static boolean isNewWikiPageRequested(Object pageNames[], WikiManager wikiViewManager){
        if(wikiViewManager != null){
            if(pageNames.length > 0){
               return !pageNames[pageNames.length-1].toString().equals(wikiViewManager.getPnWikiPage().getPageName()); 
            } else {
                return !wikiViewManager.getPnWikiPage().getPageName().equals(getRootWikiPageNameForSpace());
            }
        } else {
            return true;
        }
    }
    
    public static String[] getParsedRequestParameters(HttpServletRequest request){
    	String[] urlParameters = null;
    	String pageURL = request.getServletPath();
    	pageURL = pageURL.substring(pageURL.indexOf("/wiki")+5, (pageURL.contains("?") ? pageURL.indexOf("?") : pageURL.length()));
    	try {
			pageURL = URLDecoder.decode(pageURL, request.getCharacterEncoding());
		} catch (UnsupportedEncodingException pnetEx1) {
		    log.error("Error occurred while decoding the url of page :"+pnetEx1.getMessage());
		}
    	urlParameters = pageURL.split("/");	
    	String[] parameters = new String[urlParameters.length];
    	if(urlParameters.length > 0){
	    	parameters = new String[urlParameters.length-1];
	    	for(int index = 1; index < urlParameters.length; index++){    		
	    		try {
					parameters[index-1] = new String(urlParameters[index].getBytes(), request.getCharacterEncoding()).toString();
				} catch (UnsupportedEncodingException pnetEx) {
                    log.error("Error occurred while parsing url parameters in WikiURLManager: " + pnetEx.getMessage());
				}
	    	}
    	}
    	return parameters;
    }

    /**
     * This method replaces blank spaces by '_'  and removes non alphanumeric characters.
     * @param pageName
     * @return String wiki page name
     */
    public static String converToWikiPageName(String pageName){
        StringBuffer name = new StringBuffer();
        for(char ch: pageName.toCharArray()){
            if(ch == ' ' || ch == '_'){
                name.append("_");
            } else if (!NOT_SUPPORTED_CHARACTERS_IN_WIKI_PAGE_NAME.contains(ch+"")){
            	name.append(ch);
            }
            // Will be changed once we update existing wiki page names
            /*else if(StringUtils.isAlphanumeric(ch+"")){
                name.append(ch);
            }*/
        }
        
        if (name != null && !name.equals("")) {
			return name.toString();
		} else {
			return name.append("_").toString();
		}
    }
    
    /**
     * This method is to reinitialize user space by requested wiki page space.
     * @param request
     * @param response
     */
    public static void initSpace(HttpServletRequest request, Response response) {
        String[] requestedPages = getParsedRequestParameters(request);
        if (requestedPages.length > 0 && SessionManager.getUser() != null && SessionManager.getUser().getCurrentSpace() != null) {
            PnWikiPage wikipage = ServiceFactory.getInstance().getPnWikiPageService().getRootPageByPageName(requestedPages[0]);
            if (wikipage != null && wikipage.getOwnerObjectId().getObjectId().intValue() != Integer.valueOf(SessionManager.getUser().getCurrentSpace().getID()).intValue()) {
                Space requstedSpace = null;
                try {
                    requstedSpace = SpaceFactory.constructSpaceFromID(wikipage.getOwnerObjectId().getObjectId().toString());
                } catch (PersistenceException pnetEx) {
                    log.error("Error occurred while building space: " + pnetEx.getMessage());
                }
               
                try {
					if (requstedSpace != null && requstedSpace.getType().equals(ISpaceTypes.PROJECT_SPACE)) {
						response.sendRedirect(SessionManager.getJSPRootURL()
								+ "/project/Main.jsp?id="
								+ requstedSpace.getID()
								+ "&module="
								+ Module.PROJECT_SPACE
								+ "&page="
								+ URLEncoder.encode(SessionManager.getJSPRootURL() + request.getServletPath(),
										SessionManager.getCharacterEncoding()));
						return;
					}else if(requstedSpace != null && requstedSpace.getType().equals(ISpaceTypes.BUSINESS_SPACE)){
						response.sendRedirect(SessionManager.getJSPRootURL()
								+ "/business/Main.jsp?id="
								+ requstedSpace.getID()
								+ "&module="
								+ Module.BUSINESS_SPACE
								+ "&page="
								+ URLEncoder.encode(SessionManager.getJSPRootURL() + request.getServletPath(),
										SessionManager.getCharacterEncoding()));
						return;
					}else{
						response.sendRedirect(SessionManager.getJSPRootURL()
								+ "/personal/Main.jsp?id="
								+ requstedSpace.getID()
								+ "&module="
								+ Module.PERSONAL_SPACE
								+ "&page="
								+ URLEncoder.encode(SessionManager.getJSPRootURL() + request.getServletPath(),
										SessionManager.getCharacterEncoding()));
						return;
					}
				} catch (UnsupportedEncodingException pnetEx) {
					log.error("Error occurred while encoding url: " + pnetEx.getMessage());
				} catch (IOException pnetEx) {
					log.error("Error occurred while redirecting url: " + pnetEx.getMessage());
				}
            }
        }
    }
    
    /**
     * Redirecting the page to required url. This is written to handle the page urls.
     * This will be handled properly in Tapestry 5.0.18 migration
     * @param response
     * @param wikiPage
     * @param action
     */
    public static void forwardPage(Response response, PnWikiPage wikiPage, String action){
        try {
            String redirectURL = SessionManager.getJSPRootURL()+"/wiki/"+ WikiManager.getPagesToCall(wikiPage) +"?op="+action;
                response.sendRedirect(redirectURL);
            return;
        } catch (IOException pnetEx) {
        }
    }
    
    /** To encode wiki page name
     * @param pageName
     * @return
     */
    public static String encodePageName(String pageName) {
        try {
            return URLEncoder.encode(pageName, SessionManager.getCharacterEncoding());
        } catch (UnsupportedEncodingException pnetEx) {
            log.error("Error occurred while ecoding page name : " + pnetEx.getMessage());
        }
        return pageName;
    }

}
