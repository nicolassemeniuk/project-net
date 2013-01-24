/**
 * 
 */
package net.project.view.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.project.base.Module;
import net.project.hibernate.constants.WikiConstants;
import net.project.hibernate.model.PnWikiHistory;
import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.IPnWikiHistoryService;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.wiki.WikiManager;
import net.project.wiki.WikiURLManager;
import net.project.wiki.model.PnWikiHistoryModel;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.services.Response;

/**
 * @author
 *
 */
public class WikiPageHistory {
	
    private static Logger log;

    private List<PnWikiHistory> wikiHistoryList;

    private PnWikiHistoryModel wHistory;

    private List<PnWikiHistoryModel> historyFormatedList;

    @Parameter (required=true)
    private PnWikiPage wikiPage;
    
    private String jspRootUrl;

    private IPnWikiHistoryService pnWikiHistoryService;

    private PnWikiHistory pnWikiHistory;

    private Integer wikiHistoryId;

    private Date editDate;

    private String editedBy;

    private Integer versionNo;
    
    private String date;
    
    private String historyTitle;
    
    private String latestVersionEditDate;
    
    @Property
    private Integer previousWikiHistoryId;
    
    @Property
    private Integer previousVersionId = 0;
    
    @Property
    private Integer wikiId;
    
    @Inject
    private Response response;
    
    @Property
    private Integer moduleId;
    
    @Property
    private boolean currentPageContentEdit;

    private void initalize() {
        try {
            log = Logger.getLogger(WikiPageHistory.class);
            pnWikiHistoryService = ServiceFactory.getInstance().getPnWikiHistoryService();
            jspRootUrl = SessionManager.getJSPRootURL();
            pnWikiHistory = new PnWikiHistory();
            moduleId = Module.WIKI;
            currentPageContentEdit = true;
        } catch (Exception e) {
            log.error("Error occured while getting property tokens : " + e.getMessage());
        }
    }
    
    @SetupRender
    void getWikiHistory(String objectName, Integer objectId) {
        if(wikiPage.getWikiPageId() == null){
            WikiURLManager.forwardPage(response, wikiPage, WikiConstants.WIKI_VIEW_ACTION);  
        }
        initalize();
        try {
            String historyDateFormat = "E, MMM dd, yyyy, HH:mm a";
            DateFormat userDateFormat = SessionManager.getUser().getDateFormatter();
            wikiPage.setEditDate(userDateFormat.parseDateString(userDateFormat.formatDate(wikiPage.getEditDate(), historyDateFormat), historyDateFormat));
            setLatestVersionEditDate(userDateFormat.formatDate(wikiPage.getEditDate(), historyDateFormat));
            wikiHistoryList = new ArrayList<PnWikiHistory>();
            wikiHistoryList = pnWikiHistoryService.findHistoryWithPageId(wikiPage.getWikiPageId());
            PnWikiHistory latestRevisionPage = wikiHistoryList.get(wikiHistoryList.size() - 1);
            if(latestRevisionPage.getEdityType() != null && latestRevisionPage.getEdityType().equals("edit_name")){
            	wikiPage.setCommentText(latestRevisionPage.getCommentText());
            	currentPageContentEdit = false;
            }
            historyFormatedList = new ArrayList<PnWikiHistoryModel>();
            int version = wikiHistoryList.size();
            for (int index = wikiHistoryList.size() - 2; index >= 0; index--) {
                version = version - 1;
                PnWikiHistory wikiHistory = wikiHistoryList.get(index);
                PnWikiHistoryModel historyContentModel = new PnWikiHistoryModel();
                historyContentModel.setVersionNo(version);
                historyContentModel.setWikiHistoryId(wikiHistory.getWikiHistoryId());
                if(index == wikiHistoryList.size() - 2){
            		previousWikiHistoryId = wikiHistory.getWikiHistoryId();
            		previousVersionId = version;
            		wikiId = wikiHistory.getWikiPageId().getWikiPageId();
            	}
                historyContentModel.setEditDate(userDateFormat.formatDate(wikiHistory.getEditDate(), historyDateFormat));
                historyContentModel.setEditedBy(wikiHistory.getEditedBy());
                historyContentModel.setCommentText(wikiHistory.getCommentText());
                historyContentModel.setPageNameRename((wikiHistory.getEdityType() != null && wikiHistory.getEdityType().equals("edit_name")));
                if(index != 0){
	                historyContentModel.setPreviousHistoryId(((PnWikiHistory) wikiHistoryList.get((index - 1))).getWikiHistoryId());
                }
                historyFormatedList.add(historyContentModel);
            }
        } catch (Exception e) {
            log.error("Error in parsing the date :" + e.getMessage());
        }
    }
        
    /**
     * @return the pnWikiHistory
     */
    public PnWikiHistory getPnWikiHistory() {
        return pnWikiHistory;
    }

    /**
     * @param pnWikiHistory
     *            the pnWikiHistory to set
     */
    public void setPnWikiHistory(PnWikiHistory pnWikiHistory) {
        this.pnWikiHistory = pnWikiHistory;
    }

    /**
     * @return the wikiHistoryList
     */
    public List<PnWikiHistory> getWikiHistoryList() {
        return wikiHistoryList;
    }

    /**
     * @param wikiHistoryList
     *            the wikiHistoryList to set
     */
    public void setWikiHistoryList(List<PnWikiHistory> wikiHistoryList) {
        this.wikiHistoryList = wikiHistoryList;
    }

    /**
     * @return the pnWikiPage
     */
    public PnWikiPage getPnWikiPage() {
        return wikiPage;
    }

    /**
     * @return the jspRootUrl
     */
    public String getJspRootUrl() {
        return jspRootUrl;
    }

    /**
     * @param jspRootUrl
     *            the jspRootUrl to set
     */
    public void setJspRootUrl(String jspRootUrl) {
        this.jspRootUrl = jspRootUrl;
    }

    /**
     * @return the editDate
     */
    public Date getEditDate() {
        return editDate;
    }

    /**
     * @param editDate
     *            the editDate to set
     */
    public void setEditDate(Date editDate) {
        this.editDate = editDate;
    }

    /**
     * @return the editedBy
     */
    public String getEditedBy() {
        return editedBy;
    }

    /**
     * @param editedBy
     *            the editedBy to set
     */
    public void setEditedBy(String editedBy) {
        this.editedBy = editedBy;
    }

    /**
     * @return the historyFormatedList
     */
    public List<PnWikiHistoryModel> getHistoryFormatedList() {
        return historyFormatedList;
    }

    /**
     * @param historyFormatedList
     *            the historyFormatedList to set
     */
    public void setHistoryFormatedList(List<PnWikiHistoryModel> historyFormatedList) {
        this.historyFormatedList = historyFormatedList;
    }

    /**
     * @return the wikiHistoryId
     */
    public Integer getWikiHistoryId() {
        return wikiHistoryId;
    }

    /**
     * @param wikiHistoryId
     *            the wikiHistoryId to set
     */
    public void setWikiHistoryId(Integer wikiHistoryId) {
        this.wikiHistoryId = wikiHistoryId;
    }

    /**
     * @return the versionNo
     */
    public Integer getVersionNo() {
        return versionNo;
    }

    /**
     * @param versionNo
     *            the versionNo to set
     */
    public void setVersionNo(Integer versionNo) {
        this.versionNo = versionNo;
    }

    /**
     * @return the wHistory
     */
    public PnWikiHistoryModel getWHistory() {
        return wHistory;
    }

    /**
     * @param history
     *            the wHistory to set
     */
    public void setWHistory(PnWikiHistoryModel history) {
        wHistory = history;
    }

    
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHistoryTitle() {
        return historyTitle;
    }

    /**
     * @return the latestVersionEditDate
     */
    public String getLatestVersionEditDate() {
        return latestVersionEditDate;
    }

    /**
     * @param latestVersionEditDate the latestVersionEditDate to set
     */
    public void setLatestVersionEditDate(String latestVersionEditDate) {
        this.latestVersionEditDate = latestVersionEditDate;
    }
    
    public String getPagesToCall(){
        return WikiManager.getPagesToCall(wikiPage);
    }

}
