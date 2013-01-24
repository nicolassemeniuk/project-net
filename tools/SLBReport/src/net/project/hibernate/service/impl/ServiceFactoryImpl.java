package net.project.hibernate.service.impl;

import net.project.hibernate.service.IDocumentService;
import net.project.hibernate.service.IPnAddressService;
import net.project.hibernate.service.IPnCalendarService;
import net.project.hibernate.service.IPnDefaultObjectPermissionService;
import net.project.hibernate.service.IPnDirectoryHasPersonService;
import net.project.hibernate.service.IPnDirectoryService;
import net.project.hibernate.service.IPnDocContainerService;
import net.project.hibernate.service.IPnDocProviderHasDocSpaceService;
import net.project.hibernate.service.IPnDocProviderService;
import net.project.hibernate.service.IPnDocSpaceHasContainerService;
import net.project.hibernate.service.IPnDocSpaceService;
import net.project.hibernate.service.IPnGroupHasPersonService;
import net.project.hibernate.service.IPnGroupService;
import net.project.hibernate.service.IPnModulePermissionService;
import net.project.hibernate.service.IPnModuleService;
import net.project.hibernate.service.IPnObjectPermissionService;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnObjectTypeService;
import net.project.hibernate.service.IPnPersonNotificationAddressService;
import net.project.hibernate.service.IPnPersonProfileService;
import net.project.hibernate.service.IPnPersonService;
import net.project.hibernate.service.IPnPortfolioService;
import net.project.hibernate.service.IPnProjectSpaceMetaPropService;
import net.project.hibernate.service.IPnProjectSpaceMetaValueService;
import net.project.hibernate.service.IPnSpaceHasCalendarService;
import net.project.hibernate.service.IPnSpaceHasDocSpaceService;
import net.project.hibernate.service.IPnSpaceHasGroupService;
import net.project.hibernate.service.IPnSpaceHasModuleService;
import net.project.hibernate.service.IPnSpaceHasPersonService;
import net.project.hibernate.service.IPnSpaceHasPortfolioService;
import net.project.hibernate.service.IProfileService;
import net.project.hibernate.service.IReportService;
import net.project.hibernate.service.ISecurityService;
import net.project.hibernate.service.ServiceFactory;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * Implementation of the service factory that uses Spring to locate service
 * objects.
 */
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
	private BeanFactory beanFactory;

	/**
	 * Default constructor.
	 */
	public ServiceFactoryImpl() {
		try {
			if (log.isDebugEnabled()) {
				log.debug(" Spring BeanFactory initialization started. ");
			}
			beanFactory = new XmlBeanFactory(new ClassPathResource(BUSSINESS_CONTEXT_FILE_NAME));
			if (log.isDebugEnabled()) {
				log.debug(" Spring BeanFactory initialization finished. ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public IPnObjectService getPnObjectService() {
		return (IPnObjectService) beanFactory.getBean("pnObjectService");
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

	
	public IReportService getReportService(){
		return (IReportService) beanFactory.getBean("reportService");
	}
	
	
	// @Override
	// public IPnTimesheetService getTimesheetService() {
	// return (IPnTimesheetService) beanFactory.getBean("pnTimesheetService");
	// }

	/*
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
	*/
}
