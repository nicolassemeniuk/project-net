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
package net.project.hibernate.service;

import java.lang.reflect.Constructor;

import javax.servlet.ServletContext;

import net.project.events.async.ActivitySubscriber;
import net.project.events.async.EventPublisher;
import net.project.versioncheck.service.IVersionCheckService;

import org.springframework.beans.factory.BeanFactory;

/**
 * Service factory.
 * 
 */
public abstract class ServiceFactory {

    protected BeanFactory beanFactory;

	/**
	 * Singleton instance.
	 */
	private static ServiceFactory instance = null;

	/**
	 * Default constructor. It's private, so this class cannot be instantiated.
	 */
	protected ServiceFactory() {
	}

	/**
	 * Initialize factory with implementation class.
	 */
	public static void init(Class clazz) throws Exception {
		instance = (ServiceFactory) clazz.newInstance();
	}

	/**
	 * Initialize factory with implementation class name.
	 */
	public static void init(String className) throws Exception {
		Class clazz = ServiceFactory.class.getClassLoader().loadClass(className);
        instance = (ServiceFactory) clazz.newInstance();
	}
	
	/**
	 * Initialize factory with implementation class name.
	 */
	public static void init(Class clazz, ServletContext context) throws Exception {
		Constructor[] classConstructors = clazz.getConstructors();
		Object[] params = new Object[1] ;
		params[0] = context;
		Constructor constructor = null; 
		for(int cindex = 0; cindex < classConstructors.length; cindex++){
			if(classConstructors[cindex].getParameterTypes().length == 1){
				constructor = classConstructors[cindex];
				break;
			}
		}
		if(constructor != null){
			instance = (ServiceFactory) constructor.newInstance(params);
		}
	}

	/**
	 * Get singleton instance.
	 */
	public static synchronized ServiceFactory getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("Singleton instance is not ".concat("initialized. Call init method first!"));
        }
        return instance;
    }

	/**
	 * Get the PnObject management service implementation.
	 * 
	 * @return Reference to the object that implements PnObject management
	 *         methods.
	 */
	public abstract IPnObjectService getPnObjectService();

	/**
	 * Get the PnObjectName management service implementation.
	 * 
	 * @return Reference to the object that implements PnObjectName management
	 *         methods.
	 */
	public abstract IPnObjectNameService getPnObjectNameService();
	
	/**
	 * Get the PnPerson management service implementation.
	 * 
	 * @return Reference to the object that implements PnPerson management
	 *         methods.
	 */
	public abstract IPnPersonService getPnPersonService();

	/**
	 * Get the PnPortfolio management service implementation.
	 * 
	 * @return Reference to the object that implements PnPortfolio management
	 *         methods.
	 */
	public abstract IPnPortfolioService getPnPortfolioService();

	public abstract IPnSpaceHasPersonService getPnSpaceHasPersonService();

	public abstract IPnSpaceHasPortfolioService getPnSpaceHasPortfolioService();

	public abstract IPnAddressService getPnAddressService();

	public abstract IProfileService getProfileService();

	public abstract IPnPersonProfileService getPnPersonProfileService();

	public abstract IPnDirectoryHasPersonService getPnDirectoryHasPersonService();

	public abstract IPnPersonNotificationAddressService getPnPersonNotificationAddressService();

	public abstract IPnModuleService getPnModuleService();

	public abstract IPnSpaceHasModuleService getPnSpaceHasModuleService();

	public abstract IPnGroupService getPnGroupService();

	public abstract ISecurityService getSecurityService();

	public abstract IPnGroupHasPersonService getPnGroupHasPersonService();

	public abstract IPnSpaceHasGroupService getPnSpaceHasGroupService();

	public abstract IPnObjectPermissionService getPnObjectPermissionService();

	public abstract IPnObjectTypeService getPnObjectTypeService();

	public abstract IPnDefaultObjectPermissionService getPnDefaultObjectPermissionService();

	public abstract IPnModulePermissionService getPnModulePermissionService();

	public abstract IPnDirectoryService getPnDirectoryService();

	public abstract IDocumentService getDocumentService();

	public abstract IPnDocSpaceService getPnDocSpaceService();

	public abstract IPnSpaceHasDocSpaceService getPnSpaceHasDocSpaceService();

	public abstract IPnDocProviderHasDocSpaceService getPnDocProviderHasDocSpaceService();

	public abstract IPnDocProviderService getPnDocProviderService();

	public abstract IPnDocContainerService getPnDocContainerService();

	public abstract IPnDocSpaceHasContainerService getPnDocSpaceHasContainerService();

	public abstract IPnCalendarService getPnCalendarService();

	public abstract IPnSpaceHasCalendarService getPnSpaceHasCalendarService();

	public abstract IPnProjectSpaceMetaPropService getPnProjectSpaceMetaPropService();

	public abstract IPnProjectSpaceMetaValueService getPnProjectSpaceMetaValueService();

	public abstract IPnTimesheetService getTimesheetService();

	public abstract IPnAssignmentTimesheetService getAssignmentTimesheetService();

	public abstract IPnBusinessSpaceService getPnBusinessSpaceService();

	public abstract IPnProjectSpaceService getPnProjectSpaceService();

	public abstract IPnPagePermissionService getPnPagePermissionService();

	public abstract IPnGroupHasGroupService getPnGroupHasGroupService();

	public abstract IBaseService getBaseService();

	public abstract IPnGroupTypeService getPnGroupTypeService();

	public abstract IPnObjectSpaceService getPnObjectSpaceService();

	public abstract IPnSpaceHasSpaceService getPnSpaceHasSpaceService();

	public abstract IPnDocContainerHasObjectService getPnDocContainerHasObjectService();

	public abstract IPnBookmarkService getPnBookmarkService();

	public abstract IPnObjectLinkService getPnObjectLinkService();

	public abstract IPnDocumentService getPnDocumentService();

	public abstract IPnDocHistoryService getPnDocHistoryService();

	public abstract IPnDocActionLookupService getPnDocActionLookupService();

	public abstract IPnTmpDocumentService getPnTmpDocumentService();

	public abstract IPnDocTypeService getPnDocTypeService();

	public abstract IPnDocVersionService getPnDocVersionService();

	public abstract IPnDocContentElementService getPnDocContentElementService();

	public abstract IPnDocRepositoryBaseService getPnDocRepositoryBaseService();

	public abstract IPnDocVersionHasContentService getPnDocVersionHasContentService();

	public abstract IPnDocBySpaceViewService getPnDocBySpaceViewService();

	public abstract IDiscussionService getDiscussionService();

	public abstract IPnPostService getPnPostService();

	public abstract IPnPostReaderService getPnPostReaderService();

	public abstract IPnDiscussionGroupService getPnDiscussionGroupService();

	public abstract IPnObjectHasDiscussionService getPnObjectHasDiscussionService();

	public abstract IPnPostBodyClobService getPnPostBodyClobService();

	public abstract IPnSpaceHasPropertySheetService getPnSpaceHasPropertySheetService();

	public abstract IPnPropertySheetService getPnPropertySheetService();

	public abstract IBusinessSpaceService getBusinessSpaceService();

	// For resource tab
	public abstract IPnResourceListService getPnResourceListService();
	
	public abstract IPnResourceListHasPersonsService getPnResourceListHasPersonsService();
	
	public abstract IPnAssignmentService getPnAssignmentService();
	
	public abstract IPnSpaceHasDirectoryService getPnSpaceHasDirectoryService();
	
	public abstract IPnSpaceHasDocProviderService getPnSpaceHasDocProviderService();
	
	public abstract IPnPlanService getPnPlanService();
	
	public abstract IPnSpaceHasPlanService getPnSpaceHasPlanService();
	
	public abstract IPnPlanVersionService getPnPlanVersionService();
	
	public abstract IPnBaselineService getPnBaselineService();
	
	public abstract IScheduleService getScheduleService();
	
	public abstract ISharingService getSharingService();
	
	public abstract IPnShareableService getPnShareableService();
	
	public abstract IPnPortfolioHasSpaceService getPnPortfolioHasSpaceService();
	
	public abstract IUtilService getUtilService();

    public abstract IPnAssignmentWorkService getPnAssignmentWorkService();
    
    public abstract IPnPersonAllocationService getPnPersonAllocationService(); 
    
    // Blog related services
    public abstract IPnWeblogEntryAttributeService getPnWeblogEntryAttributeService();
    
    public abstract IPnWeblogService getPnWeblogService();
    
    public abstract IPnWeblogEntryService getPnWeblogEntryService();
    
    public abstract IPnWeblogCommentService getPnWeblogCommentService();
    
    public abstract IBlogProvider getBlogProvider();
    
    public abstract IPnTaskService getPnTaskService();
    
    //Wiki related services
    public abstract IPnWikiPageService getPnWikiPageService();
    
    public abstract IPnWikiHistoryService getPnWikiHistoryService();
    
    public abstract IWikiProvider getWikiProvider();
    
    public abstract IPnWikiAttachmentService getPnWikiAttachmentService();
    
    public abstract IPnWikiAssignmentService getPnWikiAssignmentService();
    
    public abstract IPnTimelogService geTimelogService();
    
    public abstract IBlogViewProvider getBlogViewProvider();
    
    public abstract IPnUserService getPnUserService();
    
    public abstract IPnSpaceAccessHistoryService getPnSpaceAccessHistoryService();
    
    //Task creation related services
    public abstract IAddTaskService getAddTaskService();
    
    // Resource assign related services
    public abstract IAssignResourceService getAssignResourceService();
    
    // Version check service
    public abstract IVersionCheckService getVersionCheckService();
    
    // Service for accessing the person properties
    public abstract IPnPersonPropertyService getPnPersonPropertyService();
    
    // Service for accessing Form details from database
    public abstract IPnClassService getPnClassService();
    
    //Service for accessing all Containers and Documents from database
    public abstract IPnDocContainerListViewService getDocContainerListViewService();
    
    // Service for accessing ActivitySubscriber
    public abstract ActivitySubscriber getActivitySubscriber();
    
    // Service for accessing EventPublisher
    public abstract EventPublisher getEventPublisher();
    
    // Service for accessing ActivityLog table
    public abstract IPnActivityLogService getPnActivityLogService();
    
    // Service for accessing PnNews table
    public abstract IPnNewsService getPnNewsService();

    public abstract ITaskMoveHandler getTaskMoveHandler();
    
    public abstract ITaskIndentionHandler getWorkplanTaskIndentionHandler();
    
    public abstract IAssignPhaseHandler getAssignPhaseHandler();
    
    public abstract ITaskLinkUnLinkHandler getTaskLinkUnlinkHandler();

	public abstract IWorkplanFilterHandler getWorkplanFilterHandler();
	
    public abstract IWorkplanUpdateHandler getWorkplanUpdateHandler();
    
    public abstract IDateChangeHandler getWorkplanDateChangeHandler();
    
    public abstract IWorkChangeHandler getWorkplanWorkChangeHandler();
    
    public abstract IDurationChangeHandler getWorkplanDurationChangeHandler();
    
    public abstract IWorkPercentChangeHandler getWorkplanWorkPercentChangeHandler();
    
    
    // Service for accessing ActivityLogMarked table
    public abstract IPnActivityLogMarkedService getPnActivityLogMarkedService();
    
    public abstract IPnUserDomainService getPnUserDomainService();
    
    public abstract IPnMethodologySpaceService getPnMethodologySpaceService();

    public abstract IPnMethodologyModulesService getPnMethodologyModulesService();
        
    // Service for accessing PnSpaceViewContext table
    public abstract IPnSpaceViewContextService getPnSpaceViewContextService();
    
    // Service for accessing PnBusinessHasView table
    public abstract IPnBusinessHasViewService getPnBusinessHasViewService();

    // Service for accessing PnChargeCode table
    public abstract IPnChargeCodeService getPnChargeCodeService();
    
    // Service for accessing PnObjectHasChargeCode table
    public abstract IPnObjectHasChargeCodeService getPnObjectHasChargeCodeService();
    
    public abstract IPnMaterialService getPnMaterialService();
    
    public abstract IPnSpaceHasMaterialService getPnSpaceHasMaterialService();
    
    public abstract IPnMaterialAssignmentService getPnMaterialAssignmentService();
    
    public abstract IMaterialService getMaterialService();
    
    public abstract IPnMaterialTypeService getPnMaterialTypeService();
    
    public abstract IPnFinancialSpaceService getPnFinancialSpaceService();

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
