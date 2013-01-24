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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.project.base.property.PropertyProvider;
import net.project.hibernate.constants.WikiConstants;
import net.project.hibernate.model.PnWikiPage;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.view.pages.Wiki;
import net.project.wiki.WikiIndexLoopModel;
import net.project.wiki.WikiURLManager;
import net.project.wiki.model.PnWikiPageModel;

import org.apache.tapestry5.PrimaryKeyEncoder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.services.Response;

/**
 * 
 */
public class WikiPageIndex {
    
    private boolean pageIndex;
    
    private boolean showImages;
    
    private Integer objectId;
    
    private boolean emptyWiki;
    
    private List<WikiIndexLoopModel> resultingIndex;
	
    private Integer numberOfRecentChanges;
    
    private Map result;
    
    private PnWikiPageModel currWPgEl;
    
    private WikiIndexLoopModel currEl;
    
    @InjectPage
    private Wiki wiki;
    
    @Inject
    private Response response;
    
    @SetupRender
    void constructIndex() {
        if(wiki.getWikiManager().getPnWikiPage().getWikiPageId() == null){
            WikiURLManager.forwardPage(response, wiki.getWikiManager().getPnWikiPage(), WikiConstants.WIKI_VIEW_ACTION);  
        }
        
        setPageIndex(wiki.getWikiManager().isPageIndex());
        setShowImages(wiki.getWikiManager().isShowImages());
        setObjectId(wiki.getWikiManager().getPnWikiPage().getOwnerObjectId().getObjectId());
        resultingIndex = new ArrayList<WikiIndexLoopModel>();
		if(this.pageIndex || this.showImages) {
			constructPageIndex();
		} else {
			constructRecentChanges();
		}
	}
    
    // construct page index object
	@SuppressWarnings("unchecked")
	private void constructPageIndex() {
        setResult(ServiceFactory.getInstance().getWikiProvider().wikiPagesIndex(objectId, isShowImages()));
        // populate page properties for looping
        if (getResult().isEmpty()) {
            setEmptyWiki(true);
        } else {
            setEmptyWiki(false);
        }
        // create List to iterate through
        Iterator resultIt = getResult().entrySet().iterator();
        while (resultIt.hasNext()) {
            Map.Entry entry = (Map.Entry) resultIt.next();
            List formattedDateList = new ArrayList<PnWikiPageModel>();
            List<PnWikiPage> rcList = (List<PnWikiPage>) entry.getValue();
            ListIterator rcValueIt = rcList.listIterator();
            while (rcValueIt.hasNext()) {
                 
                PnWikiPage currPg = (PnWikiPage) rcValueIt.next();
                String pageName = isShowImages() ? currPg.getPageName().replaceFirst("Image:", "") : currPg.getPageName();
                PnWikiPageModel formattedPg = new PnWikiPageModel(currPg.getWikiPageId(), pageName, currPg
                                .getContent(), currPg.getParentPageName(), SessionManager.getUser().getDateFormatter()
                                .formatDate((java.util.Date) currPg.getEditDate(), "MMM dd, yyyy"), currPg
                                .getEditedBy(), currPg.getOwnerObjectId());
                formattedPg.setCommentText(currPg.getCommentText());
                formattedPg.setPageNameForLink(currPg.getPageName());
                if(isShowImages()){
                	Integer wikiPageId = currPg.getParentPageName() != null ? currPg.getParentPageName().getWikiPageId() : currPg.getWikiPageId();                	
                	formattedPg.setDeleteAttachmentLink("deleteAttachment("+ wikiPageId +","+ currPg.getOwnerObjectId().getObjectId() +",'"+ currPg.getPageName().replaceAll("Image:","") +"');");
                }                
                formattedDateList.add(formattedPg);
            }
            getResultingIndex().add(new WikiIndexLoopModel((String) entry.getKey(), formattedDateList));
        }
    }
    
	// construct recent changes object
	@SuppressWarnings("unchecked")
	private void constructRecentChanges() {
        setResult(ServiceFactory.getInstance().getWikiProvider().recentChanges( objectId, 10, null));
        if (getResult().isEmpty()) {
            setEmptyWiki(true);
        } else {
            setEmptyWiki(false);
        }
        // create List to iterate through
        List list = Arrays.asList(getResult().entrySet().toArray());
        Iterator resultIt = list.iterator();
        while (resultIt.hasNext()) {
            Map.Entry entry = (Map.Entry) resultIt.next();
            List formattedDateList = new ArrayList<PnWikiPageModel>();
            List<PnWikiPage> rcList = (List<PnWikiPage>) entry.getValue();
            Iterator rcValueIt = rcList.iterator();
            while (rcValueIt.hasNext()) {
                PnWikiPage currPg = (PnWikiPage) rcValueIt.next();
                PnWikiPageModel formattedPg = new PnWikiPageModel(currPg.getWikiPageId(), currPg.getPageName(), currPg
                                .getContent(), currPg.getParentPageName(), SessionManager.getUser().getDateFormatter()
                                .formatDate((java.util.Date) currPg.getEditDate(), "MMM dd, yyyy"), currPg
                                .getEditedBy(), currPg.getOwnerObjectId());
                formattedPg.setCommentText(currPg.getCommentText());
                formattedDateList.add(formattedPg); // add formatted PnWikiPage to formatted list
                formattedPg.setPageNameForLink(currPg.getPageName());
            }
            getResultingIndex().add(new WikiIndexLoopModel((String)entry.getKey(), formattedDateList));
        }

    }
	
	/* added for Map looping component */
	private final PrimaryKeyEncoder<String, WikiIndexLoopModel> _mapEncoder = new PrimaryKeyEncoder<String, WikiIndexLoopModel>()
    {	
        public String toKey(WikiIndexLoopModel value) { return value.getKey(); }

        public void prepareForKeys(List<String> keys) { }

        public WikiIndexLoopModel toValue(String key)
        {
            return wikiIndexLoopModelByKey(key);
        }

		public Class<String> getKeyType() {
			return null;
		}
    };
    
    public PrimaryKeyEncoder getMapEncoder() {
		return _mapEncoder;
	}	
	
	/* added for List looping component */
	private final PrimaryKeyEncoder<Integer,PnWikiPageModel> _listEncoder = new PrimaryKeyEncoder<Integer,PnWikiPageModel>(){	
        public Integer toKey(PnWikiPageModel value) { return value.getWikiPageId(); }

        public void prepareForKeys(List<Integer> keys) { }

        public PnWikiPageModel toValue(Integer key) {
            PnWikiPage wikiPg = ServiceFactory.getInstance().getPnWikiPageService().get(key);
            PnWikiPageModel formattedPg = new PnWikiPageModel(wikiPg.getWikiPageId(), wikiPg.getPageName(), wikiPg
                            .getContent(), wikiPg.getParentPageName(), SessionManager.getUser().getDateFormatter()
                            .formatDate((java.util.Date) wikiPg.getEditDate(), "MMM dd, yyyy"), wikiPg.getEditedBy(),
                            wikiPg.getOwnerObjectId());
            formattedPg.setCommentText(wikiPg.getCommentText());
            return formattedPg;
        }

		public Class<Integer> getKeyType() {
			return null;
		}
    };
    
    public PrimaryKeyEncoder getListEncoder() {
		return _listEncoder;
	}

    /* helper method for getting List element by key */
    private WikiIndexLoopModel wikiIndexLoopModelByKey(String key) {
    	WikiIndexLoopModel result = null;
    	
    	Iterator it = getResultingIndex().iterator();	// use returned index (found above) and iterate through it to find element with specified key
    	while( it.hasNext() ) {
    		WikiIndexLoopModel currEl = (WikiIndexLoopModel) it.next();
    		if( currEl.getKey().equals(key) ) {			// if element with specified key is found - store it in result
    			result = new WikiIndexLoopModel( currEl.getKey(), currEl.getValues() );
    			break;
    		}
    	}
    	
    	return result;
    }
    
	/**
	 * @return Returns the numberOfRecentChanges.
	 */
	public Integer getNumberOfRecentChanges() {
		return numberOfRecentChanges;
	}

	/**
	 * @param numberOfRecentChanges The numberOfRecentChanges to set.
	 */
	public void setNumberOfRecentChanges(Integer numberOfRecentChanges) {
		this.numberOfRecentChanges = numberOfRecentChanges;
	}

	/**
	 * @return Returns the emptyWiki.
	 */
	public boolean isEmptyWiki() {
		return emptyWiki;
	}

	/**
	 * @param emptyWiki The emptyWiki to set.
	 */
	public void setEmptyWiki(boolean emptyWiki) {
		this.emptyWiki = emptyWiki;
	}

	/**
	 * @return Returns the resultingIndex.
	 */
	public List<WikiIndexLoopModel> getResultingIndex() {
		return resultingIndex;
	}

	/**
	 * @param resultingIndex The resultingIndex to set.
	 */
	public void setResultingIndex(List<WikiIndexLoopModel> resultingIndex) {
		this.resultingIndex = resultingIndex;
	}

    /**
     * @return the jspRootUrl
     */
    public String getJspRootUrl() {
        return SessionManager.getJSPRootURL();
    }
    
    /**
     * @return the result
     */
    public Map getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(Map result) {
        this.result = result;
    }
    
    public String getIndexHeaderMsg(){
        if (this.pageIndex){
            return PropertyProvider.get("prm.wiki.index.wikipageindex.headermsg");
        } else if(wiki.getWikiManager().isShowImages()) {
            return PropertyProvider.get("prm.wiki.index.wikipageindex.allimages.headermsg");
        } else {
            return PropertyProvider.get("prm.wiki.index.wikirecentchangesindex.headermsg");
        }
    }
    
    /**
     * @return the currWPgEl
     */
    public PnWikiPageModel getCurrWPgEl() {
        return currWPgEl;
    }

    /**
     * @param currWPgEl the currWPgEl to set
     */
    public void setCurrWPgEl(PnWikiPageModel currWPgEl) {
        this.currWPgEl = currWPgEl;
    }
    
    /**
     * @return the currEl
     */
    public WikiIndexLoopModel getCurrEl() {
        return currEl;
    }

    /**
     * @param currEl the currEl to set
     */
    public void setCurrEl(WikiIndexLoopModel currEl) {
        this.currEl = currEl;
    }

    /**
     * @return the objectId
     */
    public Integer getObjectId() {
        return objectId;
    }

    /**
     * @param objectId the objectId to set
     */
    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    /**
     * @return the pageIndex
     */
    public boolean isPageIndex() {
        return pageIndex;
    }

    /**
     * @param pageIndex the pageIndex to set
     */
    public void setPageIndex(boolean pageIndex) {
        this.pageIndex = pageIndex;
    }

	/**
	 * @return the showImages
	 */
	public boolean isShowImages() {
		return showImages;
	}

	/**
	 * @param showImages the showImages to set
	 */
	public void setShowImages(boolean showImages) {
		this.showImages = showImages;
	}
	
}
