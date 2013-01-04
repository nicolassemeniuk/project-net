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
package net.project.view.pages.blog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.calendar.PnCalendar;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.model.PnWeblogComment;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.service.IBlogProvider;
import net.project.hibernate.service.IBlogViewProvider;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.space.SpaceTypes;
import net.project.util.DateFormat;
import net.project.util.StringUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

public class BlogEntries {

	private static Logger log;

	@Persist
	private String jspRootURL;

	@Persist
	private List<PnWeblogEntry> userWeblogEntries;

	private PnWeblogEntry pnWeblogEntry;

	@Persist
	private boolean linkToPersonSpace;

	private PnWeblogComment pnWeblogComment;

	@Persist
	private String importantSymbolTooltip;

	@Persist
	private String message;

	@Inject
	private RequestGlobals requestGlobals;

	private IBlogProvider blogProvider;

	private IBlogViewProvider blogViewProvider;

	@Persist
	private boolean showEditLink;
	
	@Persist
	private boolean showPersonImage;
	
	@Persist
	private boolean showExpandCollapseImage;
	
	@Persist
	private String blogCommentDivClass;
	
	@Persist
	private String blogPostDivClass;
	
	private String addACommentLink;
	
	@Persist
	private String totalWorkDone;
	
	@Persist
	private boolean showCommentLink;
	
	private String moduleIdParameter;
	
	private boolean blogEntriesLoadedForObject;
	
	private String teamMemberId;
	
	@Persist
	private boolean isAssignmentPage;
	
	@Persist
	private boolean isMoreEntriesToSee;
	
	@Persist
	private String morePostUrl;
	
    @Persist
    private boolean showPrevLink;
    
    @Persist
    private boolean showNextLink;
    
    @Persist
    private int nextEntriesCount;
    
    @Persist
    private int totalNoOfBlogEntry;
    
    @Persist
    private int offsetForDisplay;
    
    @Persist
    private int rangeForDisplay;
    
    @Persist
    private boolean isPagingEnabled;
    
    @Persist
    private int posts;
    
    @Persist
    private String weblogEntryContentsClass; 
    
    @Persist
    private boolean showTwoPanePaging;
    
	private enum BlogAction {
		SHOW_BLOG_ENTRIES_FOR_TASK
	}

	private String blogEntryIds;

	@Property
	private String collapseIconTooltip;
	
	@Property
	private String expandIconTooltip;

	@CleanupRender
	void cleanValues(){
		setIsMoreEntriesToSee(false);
		setMorePostUrl("");
        setPagingEnabled(false);
        setWeblogEntryContentsClass("");
        blogEntryIds = "";
        setMessage("");
        setShowTwoPanePaging(false);
        totalNoOfBlogEntry = 0;
	}
	
	void onActivate() {
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
		try {
			log = Logger.getLogger(BlogEntries.class);
			jspRootURL = SessionManager.getJSPRootURL();
			blogProvider = ServiceFactory.getInstance().getBlogProvider();
			blogViewProvider = ServiceFactory.getInstance().getBlogViewProvider();
			importantSymbolTooltip = PropertyProvider.get("prm.blog.viewblog.importantsymbol.tooltip");
			addACommentLink = PropertyProvider.get("prm.blog.viewblog.addacomment.link");
			expandIconTooltip = PropertyProvider.get("prm.blog.viewblog.expand.tooltip");
			collapseIconTooltip = PropertyProvider.get("prm.blog.viewblog.collapse.tooltip");
			showCommentLink = true; 
			if (CollectionUtils.isNotEmpty(userWeblogEntries)) {
				blogEntryIds = "";
				for (PnWeblogEntry entry : userWeblogEntries) {
					for(Object comment : entry.getPnWeblogComment()){
						PnWeblogComment weblogComment = (PnWeblogComment) comment;
						weblogComment.setName(getCommentterName(weblogComment.getPersonId()));
					}
					blogEntryIds += entry.getWeblogEntryId() + ",";
				}
				setShowExpandCollapseImage(showExpandCollapseImage);
				setShowPersonImage(showPersonImage);
				setBlogCommentDivClass("comment-entry");
				setBlogPostDivClass("post-body");
				setShowCommentLink(showCommentLink);
			} else {
				setMessage(PropertyProvider.get("prm.blog.viewblog.noblogentriesfound.message"));
			}
			
			// setting module id parameter string with "&" for url paths on page
			if(!linkToPersonSpace){
				moduleIdParameter = "&module="+ Module.PROJECT_SPACE;
			} else {
				moduleIdParameter = "&module="+ Module.PERSONAL_SPACE;
			}
		} catch (Exception e) {
			log.error("Error occured while getting property tokens : " + e.getMessage());
		}
	}

	/**
	 * @param action
	 * @return
	 */
	Object onActivate(String action) {
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
		log = Logger.getLogger(BlogEntries.class);
		blogProvider = ServiceFactory.getInstance().getBlogProvider();
		blogViewProvider = ServiceFactory.getInstance().getBlogViewProvider();
		if (action.equalsIgnoreCase(BlogAction.SHOW_BLOG_ENTRIES_FOR_TASK.toString())) {
			HttpServletRequest request = requestGlobals.getHTTPServletRequest();
			String taskId = request.getParameter("taskId");
			String startDateString = request.getParameter("startDate");
			String endDateString = request.getParameter("endDate");
			
			Date startDate = null;
			Date endDate =  null;
			
			if(StringUtils.isNotEmpty(startDateString) && StringUtils.isNotEmpty(endDateString)){
				startDate = new Date(Long.parseLong(startDateString));
				endDate =  new Date(Long.parseLong(endDateString));
				PnCalendar calendar = new PnCalendar();
				calendar.setTime(endDate);
				calendar.add(Calendar.DATE, 1);
				endDate = calendar.getTime();
			}
			
			boolean showExpandCollapseImage = true;
			boolean showPersonImage = true;
			boolean showCommentLink = true;
			
			if(net.project.util.StringUtils.isNotEmpty(request.getParameter("showExpanCollapseImage")))
				showExpandCollapseImage = Boolean.valueOf(request.getParameter("showExpanCollapseImage"));
			
			if(net.project.util.StringUtils.isNotEmpty(request.getParameter("showPersonImage")))
				showPersonImage = Boolean.valueOf(request.getParameter("showPersonImage"));
			
			if(net.project.util.StringUtils.isNotEmpty(request.getParameter("showCommentLink")))
				showCommentLink = Boolean.valueOf(request.getParameter("showCommentLink"));

			try {
				if (StringUtils.isNotEmpty(taskId)) {
					setUserWeblogEntries(blogViewProvider.getFormattedBlogEntries(
							blogProvider.getWeblogEntriesByObjectId(taskId, WeblogConstants.STATUS_PUBLISHED, startDate, endDate), 
							getJspRootURL(), SpaceTypes.PROJECT_SPACE, 
							DateFormat.getInstance()));

					setLinkToPersonSpace(false);
					
					// setting module id parameter string with "&" for url paths on page
					moduleIdParameter = "&module="+Module.PROJECT_SPACE;
					setShowEditLink(false);
					blogEntryIds = "";
					if (CollectionUtils.isNotEmpty(userWeblogEntries)) {
						for (PnWeblogEntry entry : userWeblogEntries) {
							blogEntryIds += entry.getWeblogEntryId() + ",";
						}
						setMessage(userWeblogEntries.size() == 1 ?  + userWeblogEntries.size() + " " + PropertyProvider.get("prm.blog.viewblog.blogentryfound.message") : userWeblogEntries.size() + " " + PropertyProvider.get("prm.blog.viewblog.blogentriesfound.message"));
						setShowExpandCollapseImage(showExpandCollapseImage);
						setShowPersonImage(showPersonImage);
						setBlogCommentDivClass("comment-entry");
						setBlogPostDivClass("post-body");
						setShowCommentLink(showCommentLink);
					} else {
						setMessage(PropertyProvider.get("prm.blog.viewblog.noblogentriesfoundfortask.message"));
					}
				}
			} catch (Exception e) {
				log.error("Error occured while loading blog entries : " + e.getMessage());
			}
		}
		return BlogEntries.class;
	}
	
	/**
	 * To get the updated display name(member's name) of person for blog comments
	 * @param personId
	 * @return person name
	 */
	private String getCommentterName(Integer personId){
		String  commentterName = "";
		if(personId != null && personId != 0){
            try{
    			commentterName = ServiceFactory.getInstance().getPnPersonService().getPersonNameById(personId).getDisplayName();
    		}catch (Exception e) {
    			log.error("Error occurred while getting commentter name of blog entry : " + e.getMessage());
    		}
        }
		return commentterName;
	}

	public String getJspRootURL() {
		return jspRootURL;
	}

	public void setJspRootURL(String jspRootURL) {
		this.jspRootURL = jspRootURL;
	}

	public PnWeblogEntry getPnWeblogEntry() {
		return pnWeblogEntry;
	}

	public void setPnWeblogEntry(PnWeblogEntry pnWeblogEntry) {
		this.pnWeblogEntry = pnWeblogEntry;
	}

	public List<PnWeblogEntry> getUserWeblogEntries() {
		return userWeblogEntries;
	}

	public void setUserWeblogEntries(List<PnWeblogEntry> userWeblogEntries) {
		this.userWeblogEntries = userWeblogEntries;
	}

	public boolean getLinkToPersonSpace() {
		return linkToPersonSpace;
	}

	public void setLinkToPersonSpace(boolean linkToPersonSpace) {
		this.linkToPersonSpace = linkToPersonSpace;
	}

	public PnWeblogComment getPnWeblogComment() {
		return pnWeblogComment;
	}

	public void setPnWeblogComment(PnWeblogComment pnWeblogComment) {
		this.pnWeblogComment = pnWeblogComment;
	}

	public String getImportantSymbolTooltip() {
		return importantSymbolTooltip;
	}

	public void setImportantSymbolTooltip(String importantSymbolTooltip) {
		this.importantSymbolTooltip = importantSymbolTooltip;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the blogEntryIds
	 */
	public String getBlogEntryIds() {
		return blogEntryIds;
	}

	public boolean getShowEditLink() {
		return showEditLink;
	}

	public void setShowEditLink(boolean showEditLink) {
		this.showEditLink = showEditLink;
	}

	public boolean getShowExpandCollapseImage() {
		return showExpandCollapseImage;
	}

	public void setShowExpandCollapseImage(boolean showExpandCollapseImage) {
		this.showExpandCollapseImage = showExpandCollapseImage;
	}

	public boolean getShowPersonImage() {
		return showPersonImage;
	}

	public void setShowPersonImage(boolean showPersonImage) {
		this.showPersonImage = showPersonImage;
	}

	public String getBlogCommentDivClass() {
		return blogCommentDivClass;
	}

	public void setBlogCommentDivClass(String blogCommentDivClass) {
		this.blogCommentDivClass = blogCommentDivClass;
	}

	public String getBlogPostDivClass() {
		return blogPostDivClass;
	}

	public void setBlogPostDivClass(String blogPostDivClass) {
		this.blogPostDivClass = blogPostDivClass;
	}

	public String getAddACommentLink() {
		return addACommentLink;
	}

	public String getTotalWorkDone() {
		return totalWorkDone;
	}

	public void setTotalWorkDone(String totalWorkDone) {
		this.totalWorkDone = totalWorkDone;
	}
	/**
	 * @return the showCommentLink
	 */
	public boolean getShowCommentLink() {
		return showCommentLink;
	}

	/**
	 * @param showCommentLink the showCommentLink to set
	 */
	public void setShowCommentLink(boolean showCommentLink) {
		this.showCommentLink = showCommentLink;
	}

	/**
	 * @return the moduleIdParameter
	 */
	public String getModuleIdParameter() {
		return moduleIdParameter;
	}

	/**
	 * @param moduleIdParameter the moduleIdParameter to set
	 */
	public void setModuleIdParameter(String moduleIdParameter) {
		this.moduleIdParameter = moduleIdParameter;
	}
	
	/**
	 * @return the blogEntriesLoadedForObject
	 */
	public boolean getBlogEntriesLoadedForObject() {
		return blogEntriesLoadedForObject;
	}

	/**
	 * @param blogEntriesLoadedForObject the blogEntriesLoadedForObject to set
	 */
	public void setBlogEntriesLoadedForObject(boolean blogEntriesLoadedForObject) {
		this.blogEntriesLoadedForObject = blogEntriesLoadedForObject;
	}

	public String getTeamMemberId() {
		return teamMemberId;
	}

	public void setTeamMemberId(String teamMemberId) {
		this.teamMemberId = teamMemberId;
	}

	/**
	 * @return the isAssignmentPage
	 */
	public boolean getIsAssignmentPage() {
		return isAssignmentPage;
	}

	/**
	 * @param isAssignmentPage the isAssignmentPage to set
	 */
	public void setIsAssignmentPage(boolean isAssignmentPage) {
		this.isAssignmentPage = isAssignmentPage;
	}

	/**
	 * @return the isMoreEntriesToSee
	 */
	public boolean getIsMoreEntriesToSee() {
		return isMoreEntriesToSee;
	}

	/**
	 * @param isMoreEntriesToSee the isMoreEntriesToSee to set
	 */
	public void setIsMoreEntriesToSee(boolean isMoreEntriesToSee) {
		this.isMoreEntriesToSee = isMoreEntriesToSee;
	}

	/**
	 * @return the morePostUrl
	 */
	public String getMorePostUrl() {
		return morePostUrl;
	}

	/**
	 * @param morePostUrl the morePostUrl to set
	 */
	public void setMorePostUrl(String morePostUrl) {
		this.morePostUrl = morePostUrl;
	}

    /**
     * @return the isPaggingEnabled
     */
    public boolean isPagingEnabled() {
        return isPagingEnabled;
    }

    /**
     * @param isPaggingEnabled the isPaggingEnabled to set
     */
    public void setPagingEnabled(boolean isPaggingEnabled) {
        this.isPagingEnabled = isPaggingEnabled;
    }

    /**
     * @return the offsetForDisplay
     */
    public int getOffsetForDisplay() {
        return offsetForDisplay;
    }

    /**
     * @param offsetForDisplay the offsetForDisplay to set
     */
    public void setOffsetForDisplay(int offsetForDisplay) {
        this.offsetForDisplay = offsetForDisplay;
    }

    /**
     * @return the posts
     */
    public int getPosts() {
        return posts;
    }

    /**
     * @param posts the posts to set
     */
    public void setPosts(int posts) {
        this.posts = posts;
    }

    /**
     * @return the rangeForDisplay
     */
    public int getRangeForDisplay() {
        return rangeForDisplay;
    }

    /**
     * @param rangeForDisplay the rangeForDisplay to set
     */
    public void setRangeForDisplay(int rangeForDisplay) {
        this.rangeForDisplay = rangeForDisplay;
    }

    /**
     * @return the showNextLink
     */
    public boolean isShowNextLink() {
        return showNextLink;
    }

    /**
     * @param showNextLink the showNextLink to set
     */
    public void setShowNextLink(boolean showNextLink) {
        this.showNextLink = showNextLink;
    }

    /**
     * @return the showPrevLink
     */
    public boolean isShowPrevLink() {
        return showPrevLink;
    }

    /**
     * @param showPrevLink the showPrevLink to set
     */
    public void setShowPrevLink(boolean showPrevLink) {
        this.showPrevLink = showPrevLink;
    }

    /**
     * @return the totalNoOfBlogEntry
     */
    public int getTotalNoOfBlogEntry() {
        return totalNoOfBlogEntry;
    }

    /**
     * @param totalNoOfBlogEntry the totalNoOfBlogEntry to set
     */
    public void setTotalNoOfBlogEntry(int totalNoOfBlogEntry) {
        this.totalNoOfBlogEntry = totalNoOfBlogEntry;
    }

    /**
     * @return the nextEntriesCount
     */
    public int getNextEntriesCount() {
        return nextEntriesCount;
    }

    /**
     * @param nextEntriesCount the nextEntriesCount to set
     */
    public void setNextEntriesCount(int nextEntriesCount) {
        this.nextEntriesCount = nextEntriesCount;
    }

    /**
     * @return the weblogEntryContentsClass
     */
    public String getWeblogEntryContentsClass() {
        return weblogEntryContentsClass;
    }

    /**
     * @param weblogEntryContentsClass the weblogEntryContentsClass to set
     */
    public void setWeblogEntryContentsClass(String weblogEntryContentsClass) {
        this.weblogEntryContentsClass = weblogEntryContentsClass;
    }

	/**
	 * @return the showTwoPanePaging
	 */
	public boolean isShowTwoPanePaging() {
		return showTwoPanePaging;
	}

	/**
	 * @param showTwoPanePaging the showTwoPanePaging to set
	 */
	public void setShowTwoPanePaging(boolean showTwoPanePaging) {
		this.showTwoPanePaging = showTwoPanePaging;
	}

}
