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
package net.project.hibernate.service.impl;

import javax.servlet.ServletContext;

import net.project.events.async.ActivitySubscriber;
import net.project.events.async.EventPublisher;
import net.project.hibernate.service.*;
import net.project.versioncheck.service.IVersionCheckService;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.WebApplicationContextUtils;
/**
 * Implementation of the service factory that uses Spring to locate service
 * objects.
 */
@Service(value = "serviceFactory")
public class ServiceFactoryImpl extends ServiceFactory {

    /**
     * Class logger
     */

    private Logger log = Logger.getLogger(ServiceFactoryImpl.class);

    /**
     * Spring's bussiness context file name.
     */
    private static final String BUSSINESS_CONTEXT_FILE_NAME = "bussinessContext.xml";

    /**
     * Spring's bean factory.
     */
   // private BeanFactory beanFactory;
    
    /**
     * Spring's bean factory.
     */

    /**
     * Default constructor.
     */
    public ServiceFactoryImpl() {
        //ClassLoader cl = this.getClass().getClassLoader();
        if (log.isDebugEnabled()) {
            log.debug(" Spring BeanFactory initialization started. ");
        }
        //beanFactory = new XmlBeanFactory(new InputStreamResource(cl.getResourceAsStream(BUSSINESS_CONTEXT_FILE_NAME)));
        //beanFactory = new XmlBeanFactory(new ClassPathResource(BUSSINESS_CONTEXT_FILE_NAME));
        if (log.isDebugEnabled()) {
            log.debug(" Spring BeanFactory initialization finished. ");
        }
    }
    
    /**
     * Constructor with ServletContext as param to get WebApplicationContext from
     */
    public ServiceFactoryImpl(ServletContext servletContext) {
        if (log.isDebugEnabled()) {
            log.debug(" Spring BeanFactory initialization started. ");
        }
        beanFactory = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        if (log.isDebugEnabled()) {
            log.debug(" Spring BeanFactory initialization finished. ");
        }
    }

    @Override
    public IPnObjectService getPnObjectService() {
        return (IPnObjectService) beanFactory.getBean("pnObjectService");
    }

    @Override
    public IPnObjectNameService getPnObjectNameService() {
        return (IPnObjectNameService) beanFactory.getBean("pnObjectNameService");
    }
    
    @Override
    public IPnPersonService getPnPersonService() {
        return (IPnPersonService) beanFactory.getBean("pnPersonService");
    }

    @Override
    public IPnPortfolioService getPnPortfolioService() {
        return (IPnPortfolioService) beanFactory.getBean("pnPortfolioService");
    }

    @Override
    public IPnSpaceHasPersonService getPnSpaceHasPersonService() {
        return (IPnSpaceHasPersonService) beanFactory.getBean("pnSpaceHasPersonService");
    }

    @Override
    public IPnSpaceHasPortfolioService getPnSpaceHasPortfolioService() {
        return (IPnSpaceHasPortfolioService) beanFactory.getBean("pnSpaceHasPortfolioService");
    }

    @Override
    public IPnAddressService getPnAddressService() {
        return (IPnAddressService) beanFactory.getBean("pnAddressService");
    }

    @Override
    public IProfileService getProfileService() {
        return (IProfileService) beanFactory.getBean("profileService");
    }

    @Override
    public IPnPersonProfileService getPnPersonProfileService() {
        return (IPnPersonProfileService) beanFactory.getBean("pnPersonProfileService");
    }

    @Override
    public IPnDirectoryHasPersonService getPnDirectoryHasPersonService() {
        return (IPnDirectoryHasPersonService) beanFactory.getBean("pnDirectoryHasPersonService");
    }

    @Override
    public IPnPersonNotificationAddressService getPnPersonNotificationAddressService() {
        return (IPnPersonNotificationAddressService) beanFactory.getBean("pnPersonNotificationAddressService");
    }

    @Override
    public IPnModuleService getPnModuleService() {
        return (IPnModuleService) beanFactory.getBean("pnModuleService");
    }

    @Override
    public IPnSpaceHasModuleService getPnSpaceHasModuleService() {
        return (IPnSpaceHasModuleService) beanFactory.getBean("pnSpaceHasModuleService");
    }

    @Override
    public IPnGroupService getPnGroupService() {
        return (IPnGroupService) beanFactory.getBean("pnGroupService");
    }

    @Override
    public ISecurityService getSecurityService() {
        return (ISecurityService) beanFactory.getBean("securityService");
    }

    @Override
    public IPnGroupHasPersonService getPnGroupHasPersonService() {
        return (IPnGroupHasPersonService) beanFactory.getBean("pnGroupHasPersonService");
    }

    @Override
    public IPnSpaceHasGroupService getPnSpaceHasGroupService() {
        return (IPnSpaceHasGroupService) beanFactory.getBean("pnSpaceHasGroupService");
    }

    @Override
    public IPnObjectPermissionService getPnObjectPermissionService() {
        return (IPnObjectPermissionService) beanFactory.getBean("pnObjectPermissionService");
    }

    @Override
    public IPnObjectTypeService getPnObjectTypeService() {
        return (IPnObjectTypeService) beanFactory.getBean("pnObjectTypeService");
    }

    @Override
    public IPnDefaultObjectPermissionService getPnDefaultObjectPermissionService() {
        return (IPnDefaultObjectPermissionService) beanFactory.getBean("pnDefaultObjectPermissionService");
    }

    @Override
    public IPnModulePermissionService getPnModulePermissionService() {
        return (IPnModulePermissionService) beanFactory.getBean("pnModulePermissionService");
    }

    @Override
    public IPnDirectoryService getPnDirectoryService() {
        return (IPnDirectoryService) beanFactory.getBean("pnDirectoryService");
    }

    @Override
    public IDocumentService getDocumentService() {
        return (IDocumentService) beanFactory.getBean("documentService");
    }

    @Override
    public IPnDocSpaceService getPnDocSpaceService() {
        return (IPnDocSpaceService) beanFactory.getBean("pnDocSpaceService");
    }

    @Override
    public IPnSpaceHasDocSpaceService getPnSpaceHasDocSpaceService() {
        return (IPnSpaceHasDocSpaceService) beanFactory.getBean("pnSpaceHasDocSpaceService");
    }

    @Override
    public IPnDocProviderHasDocSpaceService getPnDocProviderHasDocSpaceService() {
        return (IPnDocProviderHasDocSpaceService) beanFactory.getBean("pnDocProviderHasDocSpaceService");
    }

    @Override
    public IPnDocProviderService getPnDocProviderService() {
        return (IPnDocProviderService) beanFactory.getBean("pnDocProviderService");
    }

    @Override
    public IPnDocContainerService getPnDocContainerService() {
        return (IPnDocContainerService) beanFactory.getBean("pnDocContainerService");
    }

    @Override
    public IPnDocSpaceHasContainerService getPnDocSpaceHasContainerService() {
        return (IPnDocSpaceHasContainerService) beanFactory.getBean("pnDocSpaceHasContainerService");
    }

    @Override
    public IPnCalendarService getPnCalendarService() {
        return (IPnCalendarService) beanFactory.getBean("pnCalendarService");
    }

    @Override
    public IPnSpaceHasCalendarService getPnSpaceHasCalendarService() {
        return (IPnSpaceHasCalendarService) beanFactory.getBean("pnSpaceHasCalendarService");
    }

	@Override
    public IPnProjectSpaceMetaPropService getPnProjectSpaceMetaPropService() {
        return (IPnProjectSpaceMetaPropService) beanFactory.getBean("pnProjectSpaceMetaPropService");
    }

	@Override
    public IPnProjectSpaceMetaValueService getPnProjectSpaceMetaValueService() {
		return (IPnProjectSpaceMetaValueService) beanFactory.getBean("pnProjectSpaceMetaValueService");
    }

    @Override
    public IPnTimesheetService getTimesheetService() {
        return (IPnTimesheetService) beanFactory.getBean("pnTimesheetService");
    }

    public IPnAssignmentTimesheetService getAssignmentTimesheetService() {
        return (IPnAssignmentTimesheetService) beanFactory.getBean("pnAssignmentTimesheetService");
    }

    public IPnBusinessSpaceService getPnBusinessSpaceService() {
        return (IPnBusinessSpaceService) beanFactory.getBean("pnBusinessSpaceService");
    }

    public IPnProjectSpaceService getPnProjectSpaceService() {
        return (IPnProjectSpaceService) beanFactory.getBean("pnProjectSpaceService");
    }

    public IPnPagePermissionService getPnPagePermissionService() {
        return (IPnPagePermissionService) beanFactory.getBean("pnPagePermissionService");
    }

    public IPnGroupHasGroupService getPnGroupHasGroupService() {
        return (IPnGroupHasGroupService) beanFactory.getBean("pnGroupHasGroupService");
    }

    public IBaseService getBaseService() {
        return (IBaseService) beanFactory.getBean("pnBaseService");
    }

    public IPnGroupTypeService getPnGroupTypeService() {
        return (IPnGroupTypeService) beanFactory.getBean("pnGroupTypeService");
    }

    public IPnObjectSpaceService getPnObjectSpaceService() {
        return (IPnObjectSpaceService) beanFactory.getBean("pnObjectSpaceService");
    }

    public IPnSpaceHasSpaceService getPnSpaceHasSpaceService() {
        return (IPnSpaceHasSpaceService) beanFactory.getBean("pnSpaceHasSpaceService");
    }

    public IPnDocContainerHasObjectService getPnDocContainerHasObjectService() {
        return (IPnDocContainerHasObjectService) beanFactory.getBean("pnDocContainerHasObjectService");
    }

    public IPnBookmarkService getPnBookmarkService() {
        return (IPnBookmarkService) beanFactory.getBean("pnBookmarkService");
    }

    public IPnObjectLinkService getPnObjectLinkService() {
        return (IPnObjectLinkService) beanFactory.getBean("pnObjectLinkService");
    }

    public IPnDocumentService getPnDocumentService() {
        return (IPnDocumentService) beanFactory.getBean("pnDocumentService");
    }

    public IPnDocHistoryService getPnDocHistoryService() {
        return (IPnDocHistoryService) beanFactory.getBean("pnDocHistoryService");
    }

    public IPnDocActionLookupService getPnDocActionLookupService() {
        return (IPnDocActionLookupService) beanFactory.getBean("pnDocActionLookupService");
    }

    public IPnTmpDocumentService getPnTmpDocumentService() {
        return (IPnTmpDocumentService) beanFactory.getBean("pnTmpDocumentService");
    }

    public IPnDocTypeService getPnDocTypeService() {
        return (IPnDocTypeService) beanFactory.getBean("pnDocTypeService");
    }

    public IPnDocVersionService getPnDocVersionService() {
        return (IPnDocVersionService) beanFactory.getBean("pnDocVersionService");
    }

    public IPnDocContentElementService getPnDocContentElementService() {
        return (IPnDocContentElementService) beanFactory.getBean("pnDocContentElementService");
    }

    public IPnDocRepositoryBaseService getPnDocRepositoryBaseService() {
        return (IPnDocRepositoryBaseService) beanFactory.getBean("pnDocRepositoryBaseService");
    }

    public IPnDocVersionHasContentService getPnDocVersionHasContentService() {
        return (IPnDocVersionHasContentService) beanFactory.getBean("pnDocVersionHasContentService");
    }

    public IPnDocBySpaceViewService getPnDocBySpaceViewService() {
        return (IPnDocBySpaceViewService) beanFactory.getBean("pnDocBySpaceViewService");
    }

    public IDiscussionService getDiscussionService() {
        return (IDiscussionService) beanFactory.getBean("discussionService");
    }

    public IPnPostService getPnPostService() {
        return (IPnPostService) beanFactory.getBean("pnPostService");
    }

    public IPnPostReaderService getPnPostReaderService() {
        return (IPnPostReaderService) beanFactory.getBean("pnPostReaderService");
    }

    public IPnDiscussionGroupService getPnDiscussionGroupService() {
        return (IPnDiscussionGroupService) beanFactory.getBean("pnDiscussionGroupService");
    }

    public IPnObjectHasDiscussionService getPnObjectHasDiscussionService() {
        return (IPnObjectHasDiscussionService) beanFactory.getBean("pnObjectHasDiscussionService");
    }

    public IPnPostBodyClobService getPnPostBodyClobService() {
        return (IPnPostBodyClobService) beanFactory.getBean("pnPostBodyClobService");
    }

	@Override
	public IPnPropertySheetService getPnPropertySheetService() {
		return (IPnPropertySheetService) beanFactory.getBean("pnPropertySheetService");
	}

	@Override
	public IPnSpaceHasPropertySheetService getPnSpaceHasPropertySheetService() {
		return (IPnSpaceHasPropertySheetService) beanFactory.getBean("pnSpaceHasPropertySheetService");
	}

	@Override
	public IPnAssignmentWorkService getPnAssignmentWorkService() {
		return (IPnAssignmentWorkService) beanFactory.getBean("pnAssignmentWorkService");
	}

	@Override
	public IScheduleService getScheduleService() {
		return (IScheduleService) beanFactory.getBean("scheduleService");
	}
	
	

	@Override
	public IBusinessSpaceService getBusinessSpaceService() {
		return (IBusinessSpaceService) beanFactory.getBean("businessSpaceService");
	}
	
	@Override
	public IPnResourceListService getPnResourceListService() {
		return (IPnResourceListService) beanFactory.getBean("resourceListService");
	}

	@Override
	public IPnAssignmentService getPnAssignmentService() {
		return (IPnAssignmentService) beanFactory.getBean("assignmentService");
	}

	@Override
	public IPnResourceListHasPersonsService getPnResourceListHasPersonsService() {		
		return (IPnResourceListHasPersonsService) beanFactory.getBean("resourceListHasPersonsService");
	}
	
	@Override
	public IPnSpaceHasDirectoryService getPnSpaceHasDirectoryService() {
	    return (IPnSpaceHasDirectoryService) beanFactory.getBean("pnSpaceHasDirectoryService");
	}
	
	@Override
	public IPnSpaceHasDocProviderService getPnSpaceHasDocProviderService() {
	    return (IPnSpaceHasDocProviderService) beanFactory.getBean("pnSpaceHasDocProviderService");
	}
	
	@Override
	public IPnPlanService getPnPlanService() {
	    return (IPnPlanService) beanFactory.getBean("pnPlanService");
	}
	
	@Override
	public IPnSpaceHasPlanService getPnSpaceHasPlanService() {
	    return (IPnSpaceHasPlanService) beanFactory.getBean("pnSpaceHasPlanService");
	}
	
	@Override
	public IPnPlanVersionService getPnPlanVersionService() {
	    return (IPnPlanVersionService) beanFactory.getBean("pnPlanVersionService");
	}
	
	@Override
	public IPnBaselineService getPnBaselineService() {
	    return (IPnBaselineService) beanFactory.getBean("pnBaselineService");
	}
	
	@Override
	public ISharingService getSharingService() {
	    return (ISharingService) beanFactory.getBean("sharingService");
	}
	
	@Override
	public IPnShareableService getPnShareableService() {
	    return (IPnShareableService) beanFactory.getBean("pnShareableService");
	}
	
	@Override
	public IPnPortfolioHasSpaceService getPnPortfolioHasSpaceService() {
	    return (IPnPortfolioHasSpaceService) beanFactory.getBean("pnPortfolioHasSpaceService");
	}
	
	@Override
	public IUtilService getUtilService() {
	    return (IUtilService) beanFactory.getBean("utilService");
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getPnPersonAllocationService()
	 */
	@Override
	public IPnPersonAllocationService getPnPersonAllocationService() {
		return (IPnPersonAllocationService)beanFactory.getBean("pnPersonAllocationService");
	}
	
	// Blog related services

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getPnEntryAttributeService()
	 */
	@Override
	public IPnWeblogEntryAttributeService getPnWeblogEntryAttributeService() {
		return (IPnWeblogEntryAttributeService) beanFactory.getBean("pnWeblogEntryAttributeService");
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getPnWeblogCommentService()
	 */
	@Override
	public IPnWeblogCommentService getPnWeblogCommentService() {
		return (IPnWeblogCommentService) beanFactory.getBean("pnWeblogCommentService");
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getPnWeblogEntryService()
	 */
	@Override
	public IPnWeblogEntryService getPnWeblogEntryService() {
		return (IPnWeblogEntryService) beanFactory.getBean("pnWeblogEntryService");
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getPnWeblogService()
	 */
	@Override
	public IPnWeblogService getPnWeblogService() {
		return (IPnWeblogService) beanFactory.getBean("pnWeblogService");
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getBlogProvider()
	 */
	@Override
	public IBlogProvider getBlogProvider() {
		return (IBlogProvider) beanFactory.getBean("rollerBlogProvider");
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getPnTaskService()
	 */
	@Override
	public IPnTaskService getPnTaskService() {
		return (IPnTaskService) beanFactory.getBean("pnTaskService");
	}	
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getPnWikiPageService()
	 */
	@Override
	public IPnWikiPageService getPnWikiPageService() {
		return (IPnWikiPageService) beanFactory.getBean("pnWikiPageService");
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getPnWikiHistoryService()
	 */
	@Override
	public IPnWikiHistoryService getPnWikiHistoryService() {
		return (IPnWikiHistoryService) beanFactory.getBean("pnWikiHistoryService");
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getBlikiProvider()
	 */
	@Override
	public IWikiProvider getWikiProvider() {
		return (IWikiProvider) beanFactory.getBean("wikiProvider");
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getPnWikiAttachmentService()
	 */
	@Override
	public IPnWikiAttachmentService getPnWikiAttachmentService() {
		return (IPnWikiAttachmentService) beanFactory.getBean("pnWikiAttachmentService");
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getPnWikiAssignmentService()
	 */
	@Override
	public IPnWikiAssignmentService getPnWikiAssignmentService() {
		return (IPnWikiAssignmentService) beanFactory.getBean("pnWikiAssignmentService");
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#geTimelogService()
	 */
	@Override
	public IPnTimelogService geTimelogService() {
		return (IPnTimelogService) beanFactory.getBean("pnTimelogService");
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getBlogViewProvider()
	 */
	@Override
	public IBlogViewProvider getBlogViewProvider() {
		return (IBlogViewProvider) beanFactory.getBean("blogViewProvider");
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getPnUserService()
	 */
	@Override
	public IPnUserService getPnUserService() {
		return (IPnUserService) beanFactory.getBean("pnUserService");
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getPnSpaceAccessHistoryService()
	 */
	@Override
	public IPnSpaceAccessHistoryService getPnSpaceAccessHistoryService() {
		return (IPnSpaceAccessHistoryService) beanFactory.getBean("pnSpaceAccessHistoryService");
	}

	/* (non-Javadoc)
	@Override
	public IPnPersonPropertyService getPnPersonPropertyService() {
		return (IPnPersonPropertyService)  beanFactory.getBean("pnPersonPropertyService");
	}
	 * @see net.project.hibernate.service.ServiceFactory#getAddTaskServiece()
	 */
	@Override
	public IAddTaskService getAddTaskService() {
		return (IAddTaskService) beanFactory.getBean("addTaskService");
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getResourceAssignService()
	 */
	@Override
	public IAssignResourceService getAssignResourceService() {
		return (IAssignResourceService) beanFactory.getBean("assignResourceService");
	}
	@Override
	public IPnPersonPropertyService getPnPersonPropertyService() {
		return (IPnPersonPropertyService)  beanFactory.getBean("pnPersonPropertyService");
	}
	
	public IVersionCheckService getVersionCheckService(){
		return (IVersionCheckService) beanFactory.getBean("versionCheckService");
	}
	
	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getPnClassService()
	 */
	@Override
	public IPnClassService getPnClassService(){
		return (IPnClassService) beanFactory.getBean("pnClassService");
	}
	
    //Service for accessing all Containers and Documents from database
    public IPnDocContainerListViewService getDocContainerListViewService(){
    	return (IPnDocContainerListViewService) beanFactory.getBean("docContainerListViewService");
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.ServiceFactory#getActivitySubscriber()
     */
    @Override
    public ActivitySubscriber getActivitySubscriber() {
    	return (ActivitySubscriber) beanFactory.getBean("activityConsumer");
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.ServiceFactory#getEventPublisher()
     */
    @Override
    public EventPublisher getEventPublisher() {
    	return (EventPublisher) beanFactory.getBean("producer");
    }
    
    /* (non-Javadoc)
     * @see net.project.hibernate.service.ServiceFactory#getPnActivityLogService()
     */
    @Override
    public IPnActivityLogService getPnActivityLogService() {
    	return (IPnActivityLogService) beanFactory.getBean("pnActivityLogService");
    }
   
    /* (non-Javadoc)
     * @see net.project.hibernate.service.ServiceFactory#getPnNewsService()
     */
    @Override
    public IPnNewsService getPnNewsService() {
    	return (IPnNewsService) beanFactory.getBean("pnNewsService");
    }

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getWrokplanTaskMoveHandler()
	 */
	@Override
	public ITaskMoveHandler getTaskMoveHandler() {
		return (ITaskMoveHandler) beanFactory.getBean("taskMoveHandler");
	
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getWorkplanTaskIndentionHandler()
	 */
	@Override
	public ITaskIndentionHandler getWorkplanTaskIndentionHandler() {
		return (ITaskIndentionHandler) beanFactory.getBean("workplanTaskIndentionHandler");
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getAssignPhaseHandler()
	 */
	@Override
	public IAssignPhaseHandler getAssignPhaseHandler() {
		return (IAssignPhaseHandler)beanFactory.getBean("assignPhaseHandler");
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getTaskLinkUnlinkHandler()
	 */
	@Override
	public ITaskLinkUnLinkHandler getTaskLinkUnlinkHandler() {
		return (ITaskLinkUnLinkHandler)beanFactory.getBean("taskLinkUnlinkHandler");
	}

	@Override
	public IWorkplanFilterHandler getWorkplanFilterHandler() {
		return (IWorkplanFilterHandler)beanFactory.getBean("workplanFilterHandler");
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.ServiceFactory#getWorkplanUpdateHandler()
	 */
	@Override
	public IWorkplanUpdateHandler getWorkplanUpdateHandler() {
		return (IWorkplanUpdateHandler) beanFactory.getBean("workplanUpdateHandler");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.ServiceFactory#getWorkplanDateChangeHandler()
	 */
	@Override
	public IDateChangeHandler getWorkplanDateChangeHandler() {
		return (IDateChangeHandler) beanFactory.getBean("workplanDateChangeHandler");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.ServiceFactory#getWorkplanWorkChangeHandler()
	 */
	@Override
	public IWorkChangeHandler getWorkplanWorkChangeHandler() {
		return (IWorkChangeHandler) beanFactory.getBean("workplanWorkChangeHandler");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.project.hibernate.service.ServiceFactory#getWorkplanDurationChangeHandler()
	 */
	@Override
	public IDurationChangeHandler getWorkplanDurationChangeHandler() {
		return (IDurationChangeHandler) beanFactory.getBean("workplanDurationChangeHandler");
	}

	/* (non-Javadoc)
	 * @see net.project.hibernate.service.ServiceFactory#getWorkplanWorkPercentChangeHandler()
	 */
	@Override
	public IWorkPercentChangeHandler getWorkplanWorkPercentChangeHandler() {
		return (IWorkPercentChangeHandler) beanFactory.getBean("workplanWorkPercentChangeHandler");
	}
	
	@Override
    public IPnActivityLogMarkedService getPnActivityLogMarkedService() {
    	return (IPnActivityLogMarkedService) beanFactory.getBean("pnActivityLogMarkedService");
    }

	@Override
	public IPnUserDomainService getPnUserDomainService() {
		return (IPnUserDomainService)  beanFactory.getBean("pnUserDomainService");
	}

	@Override
	public IPnMethodologyModulesService getPnMethodologyModulesService() {
		return (IPnMethodologyModulesService)  beanFactory.getBean("pnMethodologyModulesService");
	}

	@Override
	public IPnMethodologySpaceService getPnMethodologySpaceService() {
		return (IPnMethodologySpaceService)  beanFactory.getBean("pnMethodologySpaceService");
	}

	@Override
	public IPnSpaceViewContextService getPnSpaceViewContextService() {
		return (IPnSpaceViewContextService)  beanFactory.getBean("pnSpaceViewContextService");
	}

	@Override
	public IPnBusinessHasViewService getPnBusinessHasViewService() {
		return (IPnBusinessHasViewService) beanFactory.getBean("pnBusinessHasViewService");
	}

	@Override
	public IPnChargeCodeService getPnChargeCodeService() {
		return (IPnChargeCodeService) beanFactory.getBean("pnChargeCodeService");
	}

	@Override
	public IPnObjectHasChargeCodeService getPnObjectHasChargeCodeService() {
		return (IPnObjectHasChargeCodeService) beanFactory.getBean("pnObjectHasChargeCodeService");
	}
	
	//@nicolas added
	@Override
    public IPnMaterialService getPnMaterialService() {
        return (IPnMaterialService) beanFactory.getBean("pnMaterialService");
    }
	
	@Override
	public IPnMaterialTypeService getPnMaterialTypeService(){
		return (IPnMaterialTypeService) beanFactory.getBean("pnMaterialTypeService");
	}

	@Override
    public IPnSpaceHasMaterialService getPnSpaceHasMaterialService(){
    	return (IPnSpaceHasMaterialService) beanFactory.getBean("pnSpaceHasMaterialService");
    }
	
	@Override
    public IPnMaterialAssignmentService getPnMaterialAssignmentService(){
    	return (IPnMaterialAssignmentService) beanFactory.getBean("pnMaterialAssignmentService");
    }
	
	
	@Override
	public IMaterialService getMaterialService(){
		return (IMaterialService) beanFactory.getBean("materialService");
	}

	@Override
	public IPnFinancialSpaceService getPnFinancialSpaceService() {
		return (IPnFinancialSpaceService) beanFactory.getBean("pnFinancialSpaceService");
	}

	@Override
	public IPnPersonSalaryService getPnPersonSalaryService() {
		return (IPnPersonSalaryService) beanFactory.getBean("pnPersonSalaryService");
	}

	@Override
	public IProjectFinancialService getProjectFinancialService() {
		return (IProjectFinancialService) beanFactory.getBean("projectFinancialService");
	}
	
	@Override
	public IPnSpaceViewService getPnSpaceViewService(){
		return (IPnSpaceViewService) beanFactory.getBean("pnSpaceViewService");
	}
    
	@Override
	public ITaskFinancialService getTaskFinancialService(){
		return (ITaskFinancialService) beanFactory.getBean("taskFinancialService");
	}
}
