package net.project.hibernate.service;

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
import net.project.hibernate.service.ISecurityService;

/**
 * Service factory.
 * 
 */
public abstract class ServiceFactory {

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
		try {
			instance = (ServiceFactory) clazz.newInstance();
			System.out.println("\n\n\n inicijalizuje se \n\n\n");
			if (instance == null) {
				System.out.println("\n\n\n opet je null \n\n\n");
			}else {
				System.out.println("\n\n\n nije je null \n\n\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize factory with implementation class name.
	 */
	public static void init(String className) throws Exception {
		Class clazz = ServiceFactory.class.getClassLoader().loadClass(className);
		instance = (ServiceFactory) clazz.newInstance();

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
	
	public abstract IReportService getReportService();

	// public abstract IPnTimesheetService getTimesheetService();
	/*
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
	*/
}
