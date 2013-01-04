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
package net.project.view.pages.personal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.project.admin.RegistrationBean;
import net.project.base.Module;
import net.project.base.ObjectType;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.hibernate.model.PnObjectType;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.PnSpaceHasPerson;
import net.project.hibernate.model.PnWeblogEntry;
import net.project.hibernate.service.IBlogViewProvider;
import net.project.hibernate.service.IPnObjectTypeService;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.IPnSpaceAccessHistoryService;
import net.project.hibernate.service.IPnSpaceHasPersonService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.space.PersonalSpaceBean;
import net.project.space.Space;
import net.project.space.SpaceTypes;
import net.project.util.DateFormat;
import net.project.util.HTMLUtils;
import net.project.util.Version;
import net.project.view.pages.base.BasePage;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.slf4j.Logger;

/**
 *
 */
public class Profile extends BasePage {
	
	private static Logger log = logger;

	private String versionNumber; 
	
	private String importantSymbolTooltip;
	
	@Inject
	private RequestGlobals requestGlobals;
	
	@Persist
	private RegistrationBean registrationBean;
	
	@Persist
	private Integer moduleId;
	
	@Persist
	private Integer spaceId;
	
	private List<PnProjectSpace> visibleProjects;
	
	@Persist
	private List<PnSpaceHasPerson> spaceHasPersonObjects;
	
	private PnSpaceHasPerson pnSpaceHasPerson;
		
	private String projectList;
	
	@Persist
	private String lastLoginDate;
	
	@Persist
	private PnWeblogEntry lastBlogEntry;
	
	@Persist
	private boolean isLastBlogEntryExist;
	
	@Persist
	private boolean isSpaceAdmin;
	
	@Persist
	private boolean isOnline;
	
	@Persist
	private User user;
	
	@Persist
	private String currentTimeInTimeZone;
	
	@Persist
	private String imagePath;
	
	@Persist
	private boolean isImageAvailable;
	
	private String navbarURL;
	
	private String spaceForNavbar;

	private String profileHeaderStyle;
	

	private boolean showSkypeStatus;

	private boolean actionsIconEnabled;
	
	private String projectName;
	
	private String projectId;
	
	private String confirmRemoveImageMessege;
    
    private boolean blogEnabled;
    
    private IBlogViewProvider blogViewProvider;
	
    private String blogItImagePathOn;
    
    private String blogItImagePathOver;
    
    private String modifyImagePathOn;
    
    private String modifyImagePathOver;
    
    private String uploadImagePathOn;
    
    private String uploadImagePathOver;
    
    private String removeImagePathOn;
    
    private String removeImagePathOver;
    
    @Property
    private boolean isProjectDeleted = false;
    
    @Property
    private String blogitTitle;
    
    @Property
    private String editProfileTitle;
    
    @Property
    private String uploadPictureTitle;
    
    @Property
    private String removePictureTitle;
    
    @Property
    private String skypeStatusTitle;
    
    @Property
    private String myPictureTitle;
    
    @SetupRender
	void setValues() {
		try {
			versionNumber = StringUtils.deleteWhitespace(Version.getInstance().getAppVersion());		
			importantSymbolTooltip = PropertyProvider.get("prm.blog.viewblog.importantsymbol.tooltip");
			showSkypeStatus = PropertyProvider.getBoolean("prm.global.skype.isenabled");
			confirmRemoveImageMessege = PropertyProvider.get("prm.personal.profile.confirmremoveimage.message");
			moduleId = Module.PERSONAL_SPACE;
			actionsIconEnabled = PropertyProvider.get("prm.global.actions.icon.isenabled").equals("1");
			Space currentSpace = SessionManager.getUser().getCurrentSpace();
			blogEnabled = PropertyProvider.getBoolean("prm.blog.isenabled")
					&& currentSpace.isTypeOf(SpaceTypes.PERSONAL_SPACE)
					|| currentSpace.isTypeOf(SpaceTypes.PROJECT_SPACE);
            blogItImagePathOn = PropertyProvider.get("all.global.toolbar.standard.blogit.image.on");
            blogItImagePathOver = PropertyProvider.get("all.global.toolbar.standard.blogit.image.over");
            modifyImagePathOn = PropertyProvider.get("all.global.toolbar.standard.modify.image.on");
            modifyImagePathOver = PropertyProvider.get("all.global.toolbar.standard.modify.image.over");
            uploadImagePathOn = PropertyProvider.get("all.global.toolbar.standard.uploadpicture.image.on");
            uploadImagePathOver = PropertyProvider.get("all.global.toolbar.standard.uploadpicture.image.over");
            removeImagePathOn = PropertyProvider.get("all.global.toolbar.standard.remove.image.on");
            removeImagePathOver = PropertyProvider.get("all.global.toolbar.standard.remove.image.over");
            blogitTitle = PropertyProvider.get("all.global.toolbar.standard.blogit.alt");
            editProfileTitle = PropertyProvider.get("prm.personal.profile.editprofileinfo.alt");
            uploadPictureTitle = PropertyProvider.get("prm.personal.profile.uploadpicture.alt");
            removePictureTitle = PropertyProvider.get("prm.personal.profile.removepicture.alt");
            skypeStatusTitle = PropertyProvider.get("prm.personal.profile.skypestaus.alt");
            myPictureTitle = PropertyProvider.get("prm.personal.profile.mypicture.alt");
		} catch (Exception ex) {
			log.error("Error occured while getting property values in Profile page : "+ex.getMessage());			
		}
	}
	
	void onActivate() {
		if (net.project.security.SessionManager.getUser() == null) {
			throw new IllegalStateException("User is null");
		}
		spaceForNavbar = SessionManager.getUser().getCurrentSpace().getType();
		if (StringUtils.isNotEmpty(spaceForNavbar)) {
			if (spaceForNavbar.equalsIgnoreCase(Space.PROJECT_SPACE)) {
				navbarURL = "/project/include/NavBar.jsp?s=project";
				profileHeaderStyle = "leftheading-"+Space.PROJECT_SPACE;
			} else if (spaceForNavbar.equalsIgnoreCase(Space.BUSINESS_SPACE)) {
				navbarURL = "/business/include/NavBar.jsp?s=business";
				profileHeaderStyle = "leftheading-"+Space.BUSINESS_SPACE;
			} else if (spaceForNavbar.equalsIgnoreCase(Space.PERSONAL_SPACE)){
				spaceForNavbar = "personal";
				navbarURL = "/personal/include/NavBar.jsp?s=personal";
				profileHeaderStyle = "leftheading-"+Space.PERSONAL_SPACE;
			}
		} else {
			spaceForNavbar = "personal";
			navbarURL = "/personal/include/NavBar.jsp?s=personal";
			profileHeaderStyle = "leftheading-"+Space.PERSONAL_SPACE;
		}
		navbarURL +="&i=" + SessionManager.getUser().getID() + SessionManager.getUser().getCurrentSpace().getID(); 
	}
	
	void onActivate(Integer userId) {
		user = new User();
		user.setID(userId.toString());
		try {
			user.load();
		} catch (PersistenceException pnetEx2) {
			log.error("Error occurred while loading user data : "+pnetEx2.getMessage());
		}
		
		if(user != null){
			PersonalSpaceBean personalSpace = new PersonalSpaceBean();
			personalSpace.setID(user.getID());
			personalSpace.load();
			try {
				user.setCurrentSpace(personalSpace);
			} catch (PnetException pnetEx1) {
				log.error("Error occurred while setting users current space : "+pnetEx1.getMessage());
			}

			// Flag for checking user as space admin
			isSpaceAdmin = user.getID().equals(SessionManager.getUser().getID());

			// User's date format
			DateFormat userDateFormat = user.getDateFormatter();
			
			// Getting registration bean from session
			registrationBean = (RegistrationBean) requestGlobals.getHTTPServletRequest().getSession().getAttribute(
					"registration");

			// Current user's space id 
			spaceId = new Integer(user.getCurrentSpace().getID());
			
			// Getting user's image path
			if(user.getImageId() != 0){
				imagePath = SessionManager.getJSPRootURL()+"/servlet/photo?id="+user.getID() + "&size=medium&module=" + Module.DIRECTORY;
				isImageAvailable = true;
			} else {
				imagePath = SessionManager.getJSPRootURL()+"/images/NoPicture.gif";
				isImageAvailable = false;
			}
			
			if (registrationBean == null) {
				registrationBean = new RegistrationBean();
				// No security validation necessary since a user can only access their own Profile
				registrationBean.setID(user.getID());
				registrationBean.setEmail(user.getEmail());
				// Load the registration information and the directory entry
				try {
					registrationBean.load();
				} catch (PersistenceException pnetEx) {
					log.error("Error occurred while loading registration bean : "+pnetEx.getMessage());
				}
			}
			// Update the registration bean from the directory entry
			registrationBean.populateFromDirectoryEntry();
			if(StringUtils.isNotEmpty(registrationBean.getState()) && registrationBean.getState().equalsIgnoreCase("XX")){
				registrationBean.setState(null);
			}
			if(StringUtils.isNotEmpty(registrationBean.getZipcode()) && registrationBean.getZipcode().equalsIgnoreCase("null")){
				registrationBean.setZipcode(null);
			}
			
			// Getting current time in user's time zone
			currentTimeInTimeZone = user.getDateFormatter().formatTime(new Date());
			
			// Getting online presence of the user
			isOnline = ServiceFactory.getInstance().getPnUserService().isOnline(Integer.parseInt(user.getID()));

			IPnProjectSpaceService projectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();
			
			try {
				visibleProjects = projectSpaceService.getProjectsVisibleToUser(Integer.parseInt(user.getID()), Integer.parseInt(SessionManager.getUser().getID()));				
			} catch (Exception e) {
				log.error("Error occurred while getting projects visible to user : "+e.getMessage());
			}
			
			IPnSpaceHasPersonService spaceHasPersonService = ServiceFactory.getInstance().getPnSpaceHasPersonService();

			// Creating array of all visible space ids
			Integer[] spaceIds = null;
			if (visibleProjects != null && visibleProjects.size() > 0) {
				spaceIds = new Integer[visibleProjects.size()];
				int projectIndex = 0;
				for (PnProjectSpace projectSpace : visibleProjects) {					
					spaceIds[projectIndex++] = projectSpace.getProjectId();
				}
			}
			
			// Creating list of PnSpaceHasPerson objects to use in loop on html
			try {
				if (spaceIds != null) {
					spaceHasPersonObjects = spaceHasPersonService.getSpaceHasPersonByProjectandPerson(spaceIds, Integer
							.parseInt(user.getID()));

					if (spaceHasPersonObjects != null && visibleProjects != null) {
						IPnSpaceAccessHistoryService pnSpaceAccessHistoryService = ServiceFactory.getInstance()
								.getPnSpaceAccessHistoryService();
						for (PnProjectSpace projectSpace : visibleProjects) {
							for (PnSpaceHasPerson spaceHasPerson : spaceHasPersonObjects) {
								if (spaceHasPerson.getComp_id().getSpaceId().equals(projectSpace.getProjectId())) {
									spaceHasPerson.setProjectName("<a class='project' href=\""+SessionManager.getJSPRootURL()+"/project/Dashboard?id="+projectSpace.getProjectId()+"&module=150\">"+HTMLUtils.escape(projectSpace.getProjectName())+"</a>");
									if(projectSpace.getProjectDesc()==null)
										spaceHasPerson.setProjectDesc("");
									else
										spaceHasPerson.setProjectDesc(projectSpace.getProjectDesc());
									Date lastVisit = pnSpaceAccessHistoryService.getSpaceHistory(projectSpace
											.getProjectId(), Integer.parseInt(user.getID()));
									if (lastVisit != null) {
										spaceHasPerson.setLastVisit(SessionManager.getUser().getDateFormatter()
												.formatDate(lastVisit));
									} else {
										spaceHasPerson.setLastVisit("");
									}
									break;
								}
							}
						}
					}
				}
			} catch (Exception e) {
				log.error("Error occurred while getting space access history : "+e.getMessage());
			}
			
			// Getting last login date of user
			lastLoginDate = userDateFormat.formatDate(registrationBean.getLastLogin(), "hh:mm a, MMM dd, yyyy");

			// Getting last blog entry of the user
			lastBlogEntry = ServiceFactory.getInstance().getBlogProvider().getLastBlogEntryOfUser(
					Integer.parseInt(user.getID()), Integer.valueOf(SessionManager.getUser().getID()));
			
			// Checking blog entry for project name and project id
			if (lastBlogEntry != null) {
				isLastBlogEntryExist = true;
                Integer spaceId = lastBlogEntry.getPnWeblog().getSpaceId();
                IPnObjectTypeService objectTypeService = ServiceFactory.getInstance().getPnObjectTypeService();
                PnObjectType objectType = objectTypeService.getObjectTypeByObjectId(spaceId);
                
                if (objectType != null && objectType.getObjectType().equals(ObjectType.PROJECT)) {
                    projectName = lastBlogEntry.getPnWeblog().getName();
                    projectId = spaceId.toString();
                    PnProjectSpace pnProjectSpace = ServiceFactory.getInstance().getPnProjectSpaceService().getProjectDetailsWithRecordStatus(Integer.parseInt(projectId));
                    isProjectDeleted = (pnProjectSpace != null && pnProjectSpace.getRecordStatus().equalsIgnoreCase("D"));
                }
				try {
					List<PnWeblogEntry> singleWeblogEntryList = new ArrayList<PnWeblogEntry>();
                    lastBlogEntry.setPnWeblogComment(null);
                    singleWeblogEntryList.add(lastBlogEntry);
                    blogViewProvider = ServiceFactory.getInstance().getBlogViewProvider();
                    lastBlogEntry = blogViewProvider.getFormattedBlogEntries(singleWeblogEntryList, getJSPRootURL(), Space.PERSONAL_SPACE, userDateFormat).get(0);                    
                } catch (Exception e) {
					log.error("Error occurred while formatting last blog entry values : "+e.getMessage());
				}
			}
		}
	}

	/**
	 * @return the versionNumber
	 */
	public String getVersionNumber() {
		return versionNumber;
	}

	/**
	 * @return the registrationBean
	 */
	public RegistrationBean getRegistrationBean() {
		return registrationBean;
	}

	/**
	 * @return the moduleId
	 */
	public Integer getModuleId() {
		return moduleId;
	}

	/**
	 * @return the projectList
	 */
	public String getProjectList() {
		return projectList;
	}

	/**
	 * @return the lastLoginDate
	 */
	public String getLastLoginDate() {
		return lastLoginDate;
	}

	/**
	 * @return the lastBlogEntry
	 */
	public PnWeblogEntry getLastBlogEntry() {
		return lastBlogEntry;
	}

	/**
	 * @return the importantSymbolTooltip
	 */
	public String getImportantSymbolTooltip() {
		return importantSymbolTooltip;
	}

	/**
	 * @return the isLastBlogEntryExist
	 */
	public boolean getIsLastBlogEntryExist() {
		return isLastBlogEntryExist;
	}

	/**
	 * @return the spaceId
	 */
	public Integer getSpaceId() {
		return spaceId;
	}

	/**
	 * @return the isSpaceAdmin
	 */
	public boolean getIsSpaceAdmin() {
		return isSpaceAdmin;
	}

	
	/**
	 * @return the currentTimeInTimeZone
	 */
	public String getCurrentTimeInTimeZone() {
		return currentTimeInTimeZone;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @return the visibleProjects
	 */
	public List<PnProjectSpace> getVisibleProjects() {
		return visibleProjects;
	}

	/**
	 * @return the pnSpaceHasPerson
	 */
	public PnSpaceHasPerson getPnSpaceHasPerson() {
		return pnSpaceHasPerson;
	}

	/**
	 * @return the spaceHasPersonObjects
	 */
	public List<PnSpaceHasPerson> getSpaceHasPersonObjects() {
		return spaceHasPersonObjects;
	}

	/**
	 * @param pnSpaceHasPerson the pnSpaceHasPerson to set
	 */
	public void setPnSpaceHasPerson(PnSpaceHasPerson pnSpaceHasPerson) {
		this.pnSpaceHasPerson = pnSpaceHasPerson;
	}

	/**
	 * @return the isOnline
	 */
	public boolean getIsOnline() {
		return isOnline;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the isImageAvailable
	 */
	public boolean getIsImageAvailable() {
		return isImageAvailable;
	}
	
	/**
	 * @param isImageAvailable the isImageAvailable to set
	 */
	public void setImageAvailable(boolean isImageAvailable) {
		this.isImageAvailable = isImageAvailable;
	}

	/**
	 * @return the navbarURL
	 */
	public String getNavbarURL() {
		return navbarURL;
	}

	/**
	 * @return the spaceForNavbar
	 */
	public String getSpaceForNavbar() {
		return spaceForNavbar;
	}

	/**
	 * @return the profileHeaderStyle
	 */
	public String getProfileHeaderStyle() {
		return profileHeaderStyle;
	}

	/**
	 * @return the showSkypeStatus
	 */
	public boolean getShowSkypeStatus() {
		return showSkypeStatus;
	}

	/**
	 * @return the actionsIconEnabled
	 */
	public boolean isActionsIconEnabled() {
		return actionsIconEnabled;
	}

	/**
	 * @param actionsIconEnabled the actionsIconEnabled to set
	 */
	public void setActionsIconEnabled(boolean actionsIconEnabled) {
		this.actionsIconEnabled = actionsIconEnabled;
	}

	/**
	 * @return project name
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @return project id
	 */
	public String getProjectId() {
		return projectId;
	}
	
	/**
	 * @return project dashboard Url 
	 */
	public String getProjectUrl() {
		return getJSPRootURL() + "/project/Dashboard?id=" + projectId + "&module="
				+ Module.PROJECT_SPACE;
	}

	/**
	 * @return the confirmRemoveImageMessege
	 */
	public String getConfirmRemoveImageMessege() {
		return confirmRemoveImageMessege;
	}

    /**
     * @return the blogEnabled
     */
    public boolean isBlogEnabled() {
        return blogEnabled;
    }

	/**
	 * @return the blogItImagePathOn
	 */
	public String getBlogItImagePathOn() {
		return blogItImagePathOn;
	}

	/**
	 * @return the blogItImagePathOver
	 */
	public String getBlogItImagePathOver() {
		return blogItImagePathOver;
	}

	/**
	 * @return the modifyImagePathOn
	 */
	public String getModifyImagePathOn() {
		return modifyImagePathOn;
	}

	/**
	 * @return the modifyImagePathOver
	 */
	public String getModifyImagePathOver() {
		return modifyImagePathOver;
	}

	/**
	 * @return the uploadImagePathOn
	 */
	public String getUploadImagePathOn() {
		return uploadImagePathOn;
	}

	/**
	 * @return the uploadImagePathOver
	 */
	public String getUploadImagePathOver() {
		return uploadImagePathOver;
	}

	/**
	 * @return the removeImagePathOn
	 */
	public String getRemoveImagePathOn() {
		return removeImagePathOn;
	}

	/**
	 * @return the removeImagePathOver
	 */
	public String getRemoveImagePathOver() {
		return removeImagePathOver;
	}

}
