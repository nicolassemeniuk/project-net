/**
 * 
 */
package net.project.view.pages.pwiki;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import net.project.base.property.PropertyProvider;
import net.project.hibernate.constants.WikiConstants;
import net.project.hibernate.model.PnWikiHistory;
import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.util.StringUtils;
import net.project.view.pages.Wiki;
import net.project.view.pages.base.BasePage;
import net.project.wiki.diff.FileLine;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.Property;

public class WikiPageDiff extends BasePage{
	
	@Property
	private FileLine currentLeftFileLine;
	
	@Property
	private FileLine currentRightFileLine;
	
	@Property
	private boolean blogEnabled;
	
	@Property
	private boolean actionsIconEnabled;
	
	@Property
	private String origVersion;
	
	@Property
	private String prevVersion;
	
	private boolean isCurrentPageHistory = true;
	
	@Property
	private String projectName;
	
	@Property
	private String wikiPageName;
	
	@Property
	private String origModifiedDate;
	
	@Property
	private String prevModifiedDate;
	
	@Property
	private String origAuthorName;
	
	@Property
	private String prevAuthorName;
	
	@Property
	private PnWikiPage pnWikiPage;
	
	@Property
	private String wikiAction;
	
	@Property
	private String currentVersionString;
	
	@Property
	private String previousVersionString;
	
	@Property
	private boolean accessIsExternal;
	
	void initialize(){
		blogEnabled = PropertyProvider.getBoolean("prm.blog.isenabled");
		actionsIconEnabled = PropertyProvider.getBoolean("prm.global.actions.icon.isenabled");
		accessIsExternal = (SessionManager.getUser() == null || SessionManager.getUser().getID() == null);
        if(accessIsExternal){
	    	try {
			 	HttpServletRequest httpServletRequest = getHttpServletRequest();
                getRequest().getSession(true).setAttribute("requestedPage", getRequest().getPath());
                RequestDispatcher requestDispatcher = httpServletRequest.getRequestDispatcher("/Login.jsp");
			    requestDispatcher.forward(httpServletRequest, getHttpServletResponse());
                return;
		    } catch (Exception ex) {
		        Logger.getLogger(Wiki.class).error("Error occurred while handling security for wiki: " + ex.getMessage());
		    }
        }
		projectName = SessionManager.getUser().getCurrentSpace().getName();
		wikiAction = WikiConstants.WIKI_HISTORY_ACTION;
	}
	
	/**
	 * Generate difference for versions other than current version
	 * @param currentHistoryId
	 * @param prevHistoryId
	 * @param versionNo
	 */
	void onActivate(String currentHistoryId, String prevHistoryId, Integer versionNo) {
		if(versionNo != null){
			initialize();
			origVersion = versionNo.toString()+", ";
			versionNo =  versionNo - 1; 
			prevVersion = versionNo.toString()+", ";
			PnWikiHistory pnWikiHistoryOrig = ServiceFactory.getInstance().getPnWikiHistoryService().get(Integer.parseInt(currentHistoryId));
			currentVersionString = pnWikiHistoryOrig.getContent();
			origModifiedDate = SessionManager.getUser().getDateFormatter().formatDate(pnWikiHistoryOrig.getEditDate(), "E, MMM dd, yyyy, HH:mm a");
			origAuthorName = pnWikiHistoryOrig.getEditedBy().getDisplayName();
			PnWikiHistory pnWikiHistoryRev = ServiceFactory.getInstance().getPnWikiHistoryService().get(Integer.parseInt(prevHistoryId));
			previousVersionString = pnWikiHistoryRev.getContent();
			prevModifiedDate = SessionManager.getUser().getDateFormatter().formatDate(pnWikiHistoryRev.getEditDate(), "E, MMM dd, yyyy, HH:mm a");
			prevAuthorName = pnWikiHistoryRev.getEditedBy().getDisplayName();
			pnWikiPage = ServiceFactory.getInstance().getPnWikiPageService().getWikiPage(pnWikiHistoryOrig.getWikiPageId().getWikiPageId());
			wikiPageName = pnWikiPage.getPageName().replaceAll("_", " ");
			isCurrentPageHistory = false;
		}	
	}
	
	/**
	 * Generate difference for current version
	 * @param prevHistoryId
	 * @param versionNo
	 */
	void onActivate(String origVersionObjectId, String versionNo) {
		if(isCurrentPageHistory && StringUtils.isNotEmpty(versionNo)){
			initialize();
			if(versionNo.equals("0")){
				pnWikiPage = ServiceFactory.getInstance().getPnWikiPageService().getWikiPage(Integer.parseInt(origVersionObjectId));
				currentVersionString = pnWikiPage.getContent();
				previousVersionString = "";
				origVersion = PropertyProvider.get("prm.wiki.editpage.wikidiff.currentversion.message");
				prevVersion = "";
				wikiPageName = pnWikiPage.getPageName().replaceAll("_", " ");
				origAuthorName = pnWikiPage.getEditedBy().getDisplayName();
				origModifiedDate = SessionManager.getUser().getDateFormatter().formatDate(pnWikiPage.getEditDate(), "E, MMM dd, yyyy, HH:mm a");
				prevAuthorName = "";
			}else if( versionNo.equals("00")){
				PnWikiHistory pnWikiHistoryOrig = ServiceFactory.getInstance().getPnWikiHistoryService().get(Integer.parseInt(origVersionObjectId));
				currentVersionString = pnWikiHistoryOrig.getContent();
				previousVersionString = "";
				origVersion = "1, ";
				prevVersion = "";
				
				pnWikiPage = ServiceFactory.getInstance().getPnWikiPageService().getWikiPage(pnWikiHistoryOrig.getWikiPageId().getWikiPageId());
				wikiPageName = pnWikiPage.getPageName().replaceAll("_", " ");
				
				origAuthorName = pnWikiHistoryOrig.getEditedBy().getDisplayName();
				origModifiedDate = SessionManager.getUser().getDateFormatter().formatDate(pnWikiHistoryOrig.getEditDate(), "E, MMM dd, yyyy, HH:mm a");
				prevAuthorName = "";
			}else {
				origVersion = PropertyProvider.get("prm.wiki.editpage.wikidiff.currentversion.message")+", ";
				prevVersion = versionNo+", ";
				PnWikiHistory pnWikiHistoryOrig = ServiceFactory.getInstance().getPnWikiHistoryService().get(Integer.parseInt(origVersionObjectId));
				pnWikiPage = ServiceFactory.getInstance().getPnWikiPageService().getWikiPage(pnWikiHistoryOrig.getWikiPageId().getWikiPageId());
				previousVersionString = pnWikiHistoryOrig.getContent();
				origModifiedDate = SessionManager.getUser().getDateFormatter().formatDate(pnWikiPage.getEditDate(), "E, MMM dd, yyyy, HH:mm a");
				origAuthorName = pnWikiHistoryOrig.getEditedBy().getDisplayName();
				currentVersionString = pnWikiPage.getContent();
				wikiPageName = pnWikiPage.getPageName().replaceAll("_", " ");
				prevModifiedDate = SessionManager.getUser().getDateFormatter().formatDate(pnWikiHistoryOrig.getEditDate(), "E, MMM dd, yyyy, HH:mm a");
				prevAuthorName = pnWikiPage.getEditedBy().getDisplayName();
			}	
		}
	}
}
