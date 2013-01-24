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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import net.project.base.PnWebloggerException;
import net.project.hibernate.constants.WeblogConstants;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.service.IBlogProvider;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.SessionManager;
import net.project.space.Space;
import net.project.util.DateFormat;
import net.project.view.pages.blog.ViewBlog;
import net.project.view.pages.resource.management.GenericSelectModel;

import org.apache.log4j.Logger;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.PropertyAccess;

/**
 * @author
 */
public class BlogArchives {

	private static Logger log;

	private List<BlogArchives> archivesLink;

	private IBlogProvider blogProvider;

	private String linkHrefValue;

	private String linkDisplayName;

	private boolean isMore;

	private String moreLinkHrefValue;

	private String archivesYear;

	private List<PnWeblogEntry> userWeblogEntries;

	private boolean isArchivesLinks;

	private BlogArchives blogArchive;

	private Integer moduleId;
	
	@Inject
	private PropertyAccess access;
	
	private GenericSelectModel<BlogArchives> archiveBeans;

	@InjectPage
	private ViewBlog viewBlog;

	@ApplicationState
	private String jSPRootURL;
	
	private List<BlogArchives> archivesListForDaysAndPost;
	
	private DateFormat userDateFormat;
	
	private String monthFormatPattern;
	
	private String daysTokens = "1,3,7,15,30,90";
	
	private String postsTokens = "20,50,100,500";
	
	private String commaSeparator = "";
	
	public BlogArchives() {
		log = Logger.getLogger(BlogArchives.class);
		blogProvider = ServiceFactory.getInstance().getBlogProvider();
		userDateFormat = SessionManager.getUser().getDateFormatter();
		monthFormatPattern = "MMM";
	}

	@SetupRender
	void setValues() {
		Date firstEntryDate = null;
		Date lastEntryDate = null;

		try {
			// get blog entries of a weblog
			userWeblogEntries = blogProvider.getWeblogEntries(viewBlog.getUserWeblog().getWeblogId(), null, null, null,
					WeblogConstants.STATUS_PUBLISHED, 0, 0);
			
			if (viewBlog.getSpaceType().equalsIgnoreCase(Space.PERSONAL_SPACE)) {
				// get blog entries from project weblogs
				if (userWeblogEntries != null && userWeblogEntries.size() > 0) {
					userWeblogEntries.addAll(blogProvider.getWeblogEntriesFromProjectBlogByPerson(viewBlog.getUserId(),
							null, null, null, WeblogConstants.STATUS_PUBLISHED, 0, 0));
				} else {
					userWeblogEntries = blogProvider.getWeblogEntriesFromProjectBlogByPerson(viewBlog.getUserId(),
							null, null, null, WeblogConstants.STATUS_PUBLISHED, 0, 0);
				}					
			}
            // to set the total number of blog entries for paging on blog pages
            if(userWeblogEntries != null) {
                viewBlog.setTotalBlogCount(userWeblogEntries.size());
            }
			setUserWeblogEntries(blogProvider.getSortedBlogEntries(userWeblogEntries));
		} catch (NumberFormatException pnetEx) {
			log.error(pnetEx.getMessage());
		} catch (PnWebloggerException pnetEx) {
			log.error(pnetEx.getMessage());
		}
		/*
		if (userWeblogEntries != null && userWeblogEntries.size() > 0) {
			PnWeblogEntry entry = userWeblogEntries.get(0);
			lastEntryDate = entry.getPubTime();
			entry = userWeblogEntries.get(userWeblogEntries.size() - 1);
			firstEntryDate = entry.getPubTime();
		}
		//getArchivesLink(firstEntryDate, lastEntryDate, WeblogConstants.DATE_RANGE_FOR_ARCHIVES, WeblogConstants.NO_OF_LINKS);
		//setMoreLinkHrefValue(SessionManager.getJSPRootURL() + "/blog/view/arichivesMore?module="+ viewBlog.getModuleId());
		setModuleId(viewBlog.getModuleId());
		
		if(firstEntryDate != null && lastEntryDate != null){
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(firstEntryDate);
			cal1.set(Calendar.HOUR, 0);
			cal1.set(Calendar.MINUTE, 0);
			cal1.set(Calendar.SECOND, 0);
			cal1.set(Calendar.MILLISECOND, 0);
			
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(lastEntryDate);
			cal2.set(Calendar.HOUR, 0);
			cal2.set(Calendar.MINUTE, 0);
			cal2.set(Calendar.SECOND, 0);
			cal2.set(Calendar.MILLISECOND, 0);
			cal2.add(Calendar.DATE, 1);
			archivesListForDaysAndPost = generateArchiveListFilterForDaysAndPosts(firstEntryDate, cal2.getTime());
		}
		if( archivesListForDaysAndPost != null && archivesListForDaysAndPost.size() > 0){
			blogArchive = archivesListForDaysAndPost.get(0);
		} else {
			archivesListForDaysAndPost = new ArrayList<BlogArchives>();
			blogArchive = new BlogArchives();
			blogArchive.setLinkDisplayName("No Blogs");
			blogArchive.setLinkHrefValue("0");
			archivesListForDaysAndPost.add(blogArchive);
		}*/
	}
	
	/**
	 * @param firstEntryDate
	 * @param lastEntryDate
	 * @return
	 */
	private List<BlogArchives> generateArchiveListFilterForDaysAndPosts(Date firstEntryDate, Date lastEntryDate){
		List<BlogArchives> filterList = new ArrayList<BlogArchives>();
		List<BlogArchives> filterListOfPosts = new ArrayList<BlogArchives>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		StringTokenizer token = new	StringTokenizer(daysTokens,",");
		Integer days = null;
		while(token.hasMoreTokens()){
			days = Integer.valueOf(token.nextToken());
			Calendar cal1 = Calendar.getInstance();
			cal1.set(Calendar.HOUR, 0);
			cal1.set(Calendar.MINUTE, 0);
			cal1.set(Calendar.SECOND, 0);
			cal1.set(Calendar.MILLISECOND, 0);
			cal1.add(Calendar.DATE, -days);
			if(isBlogEntriesExistBetween(cal1.getTime(),lastEntryDate)){
				if(filterList.size() == 0){
					filterList.add(addLinkProperty("all",simpleDateFormat.format(firstEntryDate)+"-"+simpleDateFormat.format(lastEntryDate),true));
					filterList.add(addLinkProperty(" last ","",false));
				}
				filterList.add(addLinkProperty(days+"",simpleDateFormat.format(cal1.getTime())+"-"+simpleDateFormat.format(lastEntryDate),true));
			}
		}
		if(filterList != null && filterList.size() > 0){
			filterList.add(addLinkProperty(" days, ","0",false));
		}
		
		StringTokenizer postsToken = new StringTokenizer(postsTokens,",");
		int prevPost = 0;
		int curPost = 0;
		while(postsToken.hasMoreTokens()){
			curPost = Integer.parseInt(postsToken.nextToken());
			if (userWeblogEntries != null && userWeblogEntries.size() >= prevPost){
				filterListOfPosts.add(addLinkProperty(curPost+"",""+curPost,true));
			}
			prevPost = curPost;
		}
		if(filterListOfPosts != null && filterListOfPosts.size() > 0){
			filterListOfPosts.add(addLinkProperty(" posts","0",false));
		}
			filterList.addAll(filterListOfPosts);
		return filterList;
	}
	
	public BlogArchives addLinkProperty(String name,String link,boolean isLink){
		BlogArchives linkProperty = new BlogArchives();
		linkProperty.setLinkDisplayName(name);
		linkProperty.setLinkHrefValue(link);
		linkProperty.setIsArchivesLinks(isLink);
		if(isLink == true && (!name.equals(daysTokens.split(",")[daysTokens.split(",").length-1]) 
				&& !name.equals(postsTokens.split(",")[postsTokens.split(",").length-1]))) {
			linkProperty.setCommaSeparator(", ");
		}
		return linkProperty;
	}
		
	/**
	 * Method for getting dropdown list values for archives filter
	 * 
	 * @return GenericSelectModel<BlogArchives>
	 */
	public GenericSelectModel<BlogArchives> getArchivesModel() {
		return archiveBeans;
	}
	
	/**
	 * Method to check if blog entries exist between startDate and endDate
	 * 
	 * @param startDate
	 * @param endDate
	 * @return true if exist else false
	 */
	private boolean isBlogEntriesExistBetween(Date startDate, Date endDate){
		try {
			if (userWeblogEntries != null && userWeblogEntries.size() > 0) {
				for (PnWeblogEntry entry : userWeblogEntries) {
					if (entry.getPubTime().after(startDate) || entry.getPubTime().equals(startDate) 
							&& entry.getPubTime().before(endDate) || entry.getPubTime().equals(endDate)) {
						return true;
					}
				}
			}			
		} catch (Exception pnetEx) {
			log.error(pnetEx.getMessage());
		}	
		return false;
	}	

	/**
	 * @return the archivesLink
	 */
	public List<BlogArchives> getArchivesLink() {
		return archivesLink;
	}

	/**
	 * @param archivesLink
	 *            the archivesLink to set
	 */
	public void setArchivesLink(List<BlogArchives> archives) {
		archivesLink = archives;
	}

	/**
	 * @return the linkDisplayName
	 */
	public String getLinkDisplayName() {
		return linkDisplayName;
	}

	/**
	 * @param linkDisplayName
	 *            the linkDisplayName to set
	 */
	public void setLinkDisplayName(String linkDisplayName) {
		this.linkDisplayName = linkDisplayName;
	}

	/**
	 * @return the linkHrefValue
	 */
	public String getLinkHrefValue() {
		return linkHrefValue;
	}

	/**
	 * @param linkHrefValue
	 *            the linkHrefValue to set
	 */
	public void setLinkHrefValue(String linkHrefValue) {
		this.linkHrefValue = linkHrefValue;
	}

	/**
	 * @return the blogArchives
	 */
	public BlogArchives getBlogArchive() {
		return blogArchive;
	}

	/**
	 * @param blogArchives
	 *            the blogArchives to set
	 */
	public void setBlogArchive(BlogArchives blogArchive) {
		this.blogArchive = blogArchive;
	}

	/**
	 * @return the archivesYear
	 */
	public String getArchivesYear() {
		return archivesYear;
	}

	/**
	 * @param archivesYear
	 *            the archivesYear to set
	 */
	public void setArchivesYear(String archivesYear) {
		this.archivesYear = archivesYear;
	}

	/**
	 * @return the isArchivesLinks
	 */
	public boolean getIsArchivesLinks() {
		return isArchivesLinks;
	}

	/**
	 * @param isArchivesLinks
	 *            the isArchivesLinks to set
	 */
	public void setIsArchivesLinks(boolean isArchivesLinks) {
		this.isArchivesLinks = isArchivesLinks;
	}

	/**
	 * @return the isMore
	 */
	public boolean getIsMore() {
		return isMore;
	}

	/**
	 * @param isMore
	 *            the isMore to set
	 */
	public void setIsMore(boolean isMore) {
		this.isMore = isMore;
	}

	/**
	 * @return the moreLinkHrefValue
	 */
	public String getMoreLinkHrefValue() {
		return moreLinkHrefValue;
	}

	/**
	 * @param moreLinkHrefValue
	 *            the moreLinkHrefValue to set
	 */
	public void setMoreLinkHrefValue(String moreLinkHrefValue) {
		this.moreLinkHrefValue = moreLinkHrefValue;
	}

	/**
	 * @return the jSPRootURL
	 */
	public String getJSPRootURL() {
		return jSPRootURL;
	}

	/**
	 * @return the moduleId
	 */
	public Integer getModuleId() {
		return moduleId;
	}

	/**
	 * @param moduleId
	 *            the moduleId to set
	 */
	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	/**
	 * @return the viewBlog
	 */
	public ViewBlog getViewBlog() {
		return viewBlog;
	}

	/**
	 * @return the userWeblogEntries
	 */
	public List<PnWeblogEntry> getUserWeblogEntries() {
		return userWeblogEntries;
	}

	/**
	 * @param userWeblogEntries the userWeblogEntries to set
	 */
	public void setUserWeblogEntries(List<PnWeblogEntry> userWeblogEntries) {
		this.userWeblogEntries = userWeblogEntries;
	}
	/**
	 * @return the moduleId
	 */

	public List<BlogArchives> getArchivesListForDaysAndPost() {
		return archivesListForDaysAndPost;
	}

	public void setArchivesListForDaysAndPost(
			List<BlogArchives> archivesListForDaysAndPost) {
		this.archivesListForDaysAndPost = archivesListForDaysAndPost;
	}

	public String getCommaSeparator() {
		return commaSeparator;
	}

	public void setCommaSeparator(String commaSeparator) {
		this.commaSeparator = commaSeparator;
	}
}
