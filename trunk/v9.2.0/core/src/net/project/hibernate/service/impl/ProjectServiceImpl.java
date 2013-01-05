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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.project.database.DBBean;
import net.project.hibernate.constants.ProjectConstants;
import net.project.hibernate.constants.SharingConstants;
import net.project.hibernate.model.PnCalendar;
import net.project.hibernate.model.PnDocContainer;
import net.project.hibernate.model.PnDocContainerHasObject;
import net.project.hibernate.model.PnDocContainerHasObjectPK;
import net.project.hibernate.model.PnDocProviderHasDocSpace;
import net.project.hibernate.model.PnDocProviderHasDocSpacePK;
import net.project.hibernate.model.PnDocSpace;
import net.project.hibernate.model.PnDocSpaceHasContainer;
import net.project.hibernate.model.PnDocSpaceHasContainerPK;
import net.project.hibernate.model.PnDocument;
import net.project.hibernate.model.PnModulePermission;
import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.PnPortfolio;
import net.project.hibernate.model.PnPortfolioHasSpace;
import net.project.hibernate.model.PnPortfolioHasSpacePK;
import net.project.hibernate.model.PnProjectSpace;
import net.project.hibernate.model.PnPropertySheet;
import net.project.hibernate.model.PnPropertySheetType;
import net.project.hibernate.model.PnSpaceHasCalendar;
import net.project.hibernate.model.PnSpaceHasCalendarPK;
import net.project.hibernate.model.PnSpaceHasDirectory;
import net.project.hibernate.model.PnSpaceHasDirectoryPK;
import net.project.hibernate.model.PnSpaceHasDocProvider;
import net.project.hibernate.model.PnSpaceHasDocSpace;
import net.project.hibernate.model.PnSpaceHasDocSpacePK;
import net.project.hibernate.model.PnSpaceHasModule;
import net.project.hibernate.model.PnSpaceHasModulePK;
import net.project.hibernate.model.PnSpaceHasPerson;
import net.project.hibernate.model.PnSpaceHasPersonPK;
import net.project.hibernate.model.PnSpaceHasPropertySheet;
import net.project.hibernate.model.PnSpaceHasPropertySheetPK;
import net.project.hibernate.service.IBaseService;
import net.project.hibernate.service.IPnProjectSpaceService;
import net.project.hibernate.service.IProjectService;
import net.project.hibernate.service.ServiceFactory;

import org.apache.log4j.Logger;

public class ProjectServiceImpl implements IProjectService {

	private final static Logger LOG = Logger.getLogger(ProjectServiceImpl.class);

	// Vlad fixed that method
	// Returns List of project_id, workplan_id and space_admin_role_id
	public List<Integer> createProject(String projectCreator, String subprojectOf, String businessSpaceId, String projectVisibility, String projectName, String projectDesc,
			String projectStatus, String projectColorCode, String calculationMethod, String percentComplete, Date startDate, Date endDate, String serial, String projectLogoId,
			String defaultCurrencyCode, String sponsor, Integer improvementCodeId, String currentStatusDescription, Integer financialStatColorCodeId,
			Integer financialStatImcodeId, Double budgetedTotalCostValue, String budgetedTotalCostCc, Double currentEstTotalCostValue, String currentEstTotalCostCc,
			Double actualToDateCostValue, String actualToDateCostCc, Integer estimatedRoiCostValue, String estimatedRoiCostCc, String costCenter, Integer scheduleStatColorCodeId,
			Integer scheduleStatImcodeId, Integer resourceStatColorCodeId, Integer resourceStatImcodeId, Integer priorityCodeId, Integer riskRatingCodeId, Integer visibilityId,
			Integer autocalcSchedule, String planName, Integer createShare) {
	    
	    	if (LOG.isDebugEnabled()) {
	    	    LOG.debug("ENTRY OK: createProject");
	    	}
	    	
	    	List<Integer> result = new ArrayList();
		try {
			Date currentDate = new Date(System.currentTimeMillis());

			// set subproject
			Integer isSubproject = 0;
			if (new Integer(subprojectOf) != 0) {
				isSubproject = 1;
			}

			// create new project identifier
			IBaseService baseService = ServiceFactory.getInstance().getBaseService();
			Integer projectId = baseService.createObject("project", new Integer(projectCreator), "A");

			IPnProjectSpaceService projectSpaceService = ServiceFactory.getInstance().getPnProjectSpaceService();
			PnProjectSpace projectSpace = new PnProjectSpace();
			projectSpace.setProjectId(projectId);
			projectSpace.setProjectName(projectName);
			projectSpace.setProjectDesc(projectDesc);
			projectSpace.setDateModified(currentDate);
			projectSpace.setPnPerson(new PnPerson(new Integer(projectCreator)));
			projectSpace.setStatusCodeId(new Integer(projectStatus));
			projectSpace.setPnDocument(new PnDocument(new Integer(projectLogoId)));
			projectSpace.setStartDate(startDate);
			projectSpace.setEndDate(endDate);
			projectSpace.setColorCodeId(new Integer(projectColorCode));
			projectSpace.setIsSubproject(isSubproject);
			projectSpace.setPercentCalculationMethod(calculationMethod);
			projectSpace.setPercentComplete(new Integer(percentComplete));
			projectSpace.setRecordStatus("A");
			projectSpace.setCrc(currentDate);
			projectSpace.setDefaultCurrencyCode(defaultCurrencyCode);
			projectSpace.setSponsorDesc(sponsor);
			projectSpace.setImprovementCodeId(improvementCodeId);
			projectSpace.setCurrentStatusDescription(currentStatusDescription);
			projectSpace.setFinancialStatusColorCodeId(financialStatColorCodeId);
			projectSpace.setFinancialStatusImpCodeId(financialStatImcodeId);
			projectSpace.setBudgetedTotalCostValue(budgetedTotalCostValue);
			projectSpace.setBudgetedTotalCostCc(budgetedTotalCostCc);
			projectSpace.setCurrentEstTotalCostValue(currentEstTotalCostValue);
			projectSpace.setCurrentEstTotalCostCc(currentEstTotalCostCc);
			projectSpace.setActualToDateCostValue(new Double(actualToDateCostValue));
			projectSpace.setActualToDateCostCc(actualToDateCostCc);
			projectSpace.setEstimatedRoiCostValue(estimatedRoiCostValue);
			projectSpace.setEstimatedRoiCostCc(estimatedRoiCostCc);
			projectSpace.setCostCenter(costCenter);
			projectSpace.setScheduleStatusColorCodeId(scheduleStatColorCodeId);
			projectSpace.setScheduleStatusImpCodeId(scheduleStatImcodeId);
			projectSpace.setResourceStatusColorCodeId(resourceStatColorCodeId);
			projectSpace.setResourceStatusImpCodeId(resourceStatImcodeId);
			projectSpace.setPriorityCodeId(priorityCodeId);
			projectSpace.setRiskRatingCodeId(riskRatingCodeId);
			projectSpace.setVisibilityId(visibilityId);

			projectSpaceService.saveProjectSpace(projectSpace);

			// get new identifiers from sequence for propertySheet and propertyGroup
			Integer propertySheetId = ServiceFactory.getInstance().getPnObjectService().generateNewId();
			Integer propertyGroupId = ServiceFactory.getInstance().getPnObjectService().generateNewId();

			PnPropertySheet propertySheet = new PnPropertySheet(propertySheetId, new PnPropertySheetType(ProjectConstants.AEC_PROPERTY_SHEET_TYPE), propertyGroupId);
			ServiceFactory.getInstance().getPnPropertySheetService().savePropertySheet(propertySheet);

			PnSpaceHasPropertySheet spaceHasPropertySheet = new PnSpaceHasPropertySheet(new PnSpaceHasPropertySheetPK(projectId, propertySheetId));
			ServiceFactory.getInstance().getPnSpaceHasPropertySheetService().saveSpaceHasPropertySheet(spaceHasPropertySheet);

			/***********************************************************************************************************************************************************************
			 * PROJECT SECURITY
			 **********************************************************************************************************************************************************************/

			// SPACE_HAS_MODULES
			// The new project space initially has access to all modules.
			// Copy all pn_module entries to pn_space_has_modules for this project
			// Disable below mentioned modules for the time-being. Modules 90 , 100 , 120 , 210 will are not active
			Integer[] moduleList = { 10, 20, 30, 40, 60, 70, 90, 100, 110, 120, 140, 150, 180, 190, 200, 210, 260, 310, 330 };
			for (int i = 0; i < moduleList.length; i++) {
				int active = 1;
				if (moduleList[i] == 90 || moduleList[i] == 100 || moduleList[i] == 120 || moduleList[i] == 210) {
					active = 0;
				}
				ServiceFactory.getInstance().getPnSpaceHasModuleService().saveSpaceHasModule(
					new PnSpaceHasModule(new PnSpaceHasModulePK(projectId, moduleList[i]), active));
			}

			// SPACE ADMINISTRATOR GROUP
			// The project creator is the inital space administrator of this new project	
			
			
			//Integer spaceAdminGroupId = ServiceFactory.getInstance().getSecurityService().createSpaceAdminGroup(
			//	new Integer(projectCreator), projectId, "@prm.project.security.group.type.spaceadmin.description" );
			
			DBBean db = new DBBean();
			db.setAutoCommit(false);
			int index = 0;
            db.prepareCall("{call security.F_CREATE_SPACE_ADMIN_GROUP(?,?,?)}");
            db.cstmt.setInt(++index, new Integer(projectCreator));
            db.cstmt.setInt(++index, projectId);
            db.cstmt.setString(++index, "@prm.project.security.group.type.spaceadmin.description");
            db.executeCallable();
            db.closeCStatement();
            
			
			// CREATE POWER USER GROUP
//			Integer powerUserGroupId = ServiceFactory.getInstance().getSecurityService().createPowerUserGroup(
//				new Integer(projectCreator), projectId, "@prm.security.group.type.poweruser.description");
			
			db = new DBBean();
			db.setAutoCommit(false);
			index = 0;
            db.prepareCall("{call security.F_CREATE_POWER_USER_GROUP(?,?,?)}");
            db.cstmt.setInt(++index, new Integer(projectCreator));
            db.cstmt.setInt(++index, projectId);
            db.cstmt.setString(++index, "@prm.security.group.type.poweruser.description");
            db.executeCallable();
            db.closeCStatement();
            
			// BUSINESS ADMINISTRATOR GROUP
			// If this project is owned by a business, add the business' space administrator
			// group to this space and initially afford it the permissions of the space admin group
			Integer parentAdminGroupId = 0;
			if (businessSpaceId != null && new Integer(businessSpaceId) != 0){
		    		parentAdminGroupId = ServiceFactory.getInstance().getSecurityService().createParentAdminRole(
		    			projectId, new Integer(businessSpaceId));
			}
			
			// PRINCIPAL GROUP
			// The projet creator (person) must be put into a principal group for this space.
			Integer groupId = null;
			groupId = ServiceFactory.getInstance().getSecurityService().createPrincipalGroup(new Integer(projectCreator), groupId);
			
			// Add project creator (Person) to their principal group
			boolean status = false;
			status = ServiceFactory.getInstance().getSecurityService().addPersonToGroup(groupId, new Integer(projectCreator));
			
			// TEAM MEMBER GROUP
			// The creator is the only initial team member
			groupId = null;
			groupId = ServiceFactory.getInstance().getSecurityService().createTeamMemberGroup(new Integer(projectCreator), groupId);
			
			status = false;
			status = ServiceFactory.getInstance().getSecurityService().addPersonToGroup(groupId, new Integer(projectCreator));
			
			// User will not have access to their personal document module until they
			// have created at least one project. Therefore we enable access to it at this point.
			
			List<PnModulePermission> modulePermissions = ServiceFactory.getInstance().getPnModulePermissionService().
				getModulePermissionsBySpaceAndModule(new Integer(projectCreator), 10);
			for (PnModulePermission pnModulePermission : modulePermissions) {
			    pnModulePermission.setActions(ProjectConstants.ALL_ACTIONS);
			    ServiceFactory.getInstance().getPnModulePermissionService().updateModulePermission(pnModulePermission);
			}
			
			/***********************************************************************************************************************************************************************
			 * END PROJECT SECURITY
			 **********************************************************************************************************************************************************************/
			
			// Add the creator to the project's team roster
			PnSpaceHasPerson spaceHasPerson = new PnSpaceHasPerson(new PnSpaceHasPersonPK(projectId, new Integer(projectCreator)), "member", "A");
			spaceHasPerson.setMemberTypeId(new BigDecimal(200));
			ServiceFactory.getInstance().getPnSpaceHasPersonService().saveSpaceHasPerson(spaceHasPerson);
			
			// Every project space gets the default directory
			Integer directoryId = ServiceFactory.getInstance().getPnDirectoryService().getDefaultDirectory().get(0).getDirectoryId();
			PnSpaceHasDirectory spaceHasDirectory = new PnSpaceHasDirectory(new PnSpaceHasDirectoryPK(projectId, directoryId));
			ServiceFactory.getInstance().getPnSpaceHasDirectoryService().saveSpaceHasDirectory(spaceHasDirectory);
			
			/***********************************************************************************************************************************************************************
			 * CREATE DOCUMENT SPACES FOR PROJECT
			 * The Business Space this project is being created within has a table of available doc providers.
			 * Each project space get it's own doc space created for each doc provider available to the business.
			 * TODO:  Set top-level container for the project's doc space.
			 **********************************************************************************************************************************************************************/
			
			// Get the doc_providers available to this project (defined by busines space)
			// Get a new project_id
			
			// T O D O      handle PLSQL exception for "no data found" when business space has no doc providers
			Integer docProviderId = 0;
			if (new Integer(businessSpaceId) != 0) {
			    PnSpaceHasDocProvider spaceHasDocProvider = ServiceFactory.getInstance().getPnSpaceHasDocProviderService().
			    	getDefaultSpaceHasDocProviderBySpace(new Integer(businessSpaceId));
			    if (spaceHasDocProvider != null) {
				docProviderId = spaceHasDocProvider.getComp_id().getDocProviderId();
			    }
			} else {
			    // Use only default doc provider initially
			    docProviderId = ServiceFactory.getInstance().getPnDocProviderService().
			    		getDocProviderIds().get(0).getDocProviderId();
			}
			
			// new doc space for this project
			Integer docSpaceId = ServiceFactory.getInstance().getBaseService().
					createObject("doc_space", new Integer(projectCreator), "A");
			
			PnDocSpace docSpace = new PnDocSpace(docSpaceId, "default", currentDate, "A");
			ServiceFactory.getInstance().getPnDocSpaceService().saveDocSpace(docSpace);
			
			// this project owns the doc space
			PnSpaceHasDocSpace spaceHasDocSpace = new PnSpaceHasDocSpace(new PnSpaceHasDocSpacePK(projectId, docSpaceId), 0);
			ServiceFactory.getInstance().getPnSpaceHasDocSpaceService().saveSpaceHasDocSpace(spaceHasDocSpace);
			
			// link new doc_space back to it's doc_provider
			PnDocProviderHasDocSpace pnDocProviderHasDocSpace = 
			    	new PnDocProviderHasDocSpace(new PnDocProviderHasDocSpacePK(docProviderId, docSpaceId));
			
			// Create new doc container for the Top-level folder for this project
			Integer docContainerId = ServiceFactory.getInstance().getBaseService().
					createObject("doc_container", new Integer(projectCreator), "A");
			
			PnDocContainer docContainer = new PnDocContainer();
			docContainer.setDocContainerId(docContainerId);
			docContainer.setContainerName("@prm.document.container.topfolder.name");
			docContainer.setContainerDescription("Top level document folder");
			docContainer.setPnPerson(new PnPerson(new Integer(projectCreator)));
			docContainer.setDateModified(currentDate);
			docContainer.setRecordStatus("A");
			docContainer.setCrc(currentDate);
			ServiceFactory.getInstance().getPnDocContainerService().saveDocContainer(docContainer);
			
			ServiceFactory.getInstance().getSecurityService().
				applyDocumentPermissions(docContainerId, null, "doc_container", projectId, new Integer(projectCreator));
			
			// Link container (top folder) to doc space
			// PHIL added the "is_root:1" to pn_doc_space_has_container
			
			ServiceFactory.getInstance().getPnDocSpaceHasContainerService().saveDocSpaceHasContainer(
				new PnDocSpaceHasContainer(new PnDocSpaceHasContainerPK(docSpaceId, docContainerId), 1));
			
			// SYSTEM container for this space
			// This container contains (*grin*) all system related objects
			Integer systemContainerId = ServiceFactory.getInstance().getBaseService().
				createObject("doc_container", new Integer(projectCreator), "A");
			
			PnDocContainer systemContainer = new PnDocContainer();
			systemContainer.setDocContainerId(systemContainerId);
			systemContainer.setContainerName(projectId.toString());
			systemContainer.setContainerDescription("System container for this space");
			systemContainer.setPnPerson(new PnPerson(new Integer(projectCreator)));
			systemContainer.setDateModified(currentDate);
			systemContainer.setRecordStatus("A");
			systemContainer.setIsHidden(1);
			systemContainer.setCrc(currentDate);
			ServiceFactory.getInstance().getPnDocContainerService().saveDocContainer(systemContainer);
			
			ServiceFactory.getInstance().getSecurityService().
				applyDocumentPermissions(systemContainerId, docContainerId, "doc_container", projectId, new Integer(projectCreator));
			
			ServiceFactory.getInstance().getPnDocSpaceHasContainerService().saveDocSpaceHasContainer(
				new PnDocSpaceHasContainer(new PnDocSpaceHasContainerPK(docSpaceId, systemContainerId), 0));
			
			ServiceFactory.getInstance().getPnDocContainerHasObjectService().save(
				new PnDocContainerHasObject(new PnDocContainerHasObjectPK(docContainerId, systemContainerId)));
			
			// Metrics container (contained by system container)
			Integer metricsContainerId = ServiceFactory.getInstance().getBaseService()
				.createObject("doc_container", new Integer(projectCreator), "A");
			
			PnDocContainer metricsContainer = new PnDocContainer();
			metricsContainer.setDocContainerId(metricsContainerId);
			metricsContainer.setContainerName(projectId + "::Metrics");
			metricsContainer.setContainerDescription("System container for this space");
			metricsContainer.setPnPerson(new PnPerson(new Integer(projectCreator)));
			metricsContainer.setDateModified(currentDate);
			metricsContainer.setRecordStatus("A");
			metricsContainer.setIsHidden(1);
			metricsContainer.setCrc(currentDate);
			ServiceFactory.getInstance().getPnDocContainerService().saveDocContainer(metricsContainer);
			
			ServiceFactory.getInstance().getSecurityService().
				applyDocumentPermissions(metricsContainerId, systemContainerId, "doc_container", projectId, new Integer(projectCreator));
			
			ServiceFactory.getInstance().getPnDocSpaceHasContainerService().saveDocSpaceHasContainer(
				new PnDocSpaceHasContainer(new PnDocSpaceHasContainerPK(docSpaceId, metricsContainerId), 0));
			
			ServiceFactory.getInstance().getPnDocContainerHasObjectService().save(
				new PnDocContainerHasObject(new PnDocContainerHasObjectPK(systemContainerId, metricsContainerId)));
			
			// Budget container
			Integer budgetContainerId = ServiceFactory.getInstance().getBaseService()
				.createObject("doc_container", new Integer(projectCreator), "A");
			
			PnDocContainer budgetContainer = new PnDocContainer();
			budgetContainer.setDocContainerId(budgetContainerId);
			budgetContainer.setContainerName(projectId + "::Budget");
			budgetContainer.setContainerDescription("System container for this space");
			budgetContainer.setPnPerson(new PnPerson(new Integer(projectCreator)));
			budgetContainer.setDateModified(currentDate);
			budgetContainer.setRecordStatus("A");
			budgetContainer.setIsHidden(1);
			budgetContainer.setCrc(currentDate);
			ServiceFactory.getInstance().getPnDocContainerService().saveDocContainer(budgetContainer);
			
			ServiceFactory.getInstance().getSecurityService().
				applyDocumentPermissions(budgetContainerId, systemContainerId, "doc_container", projectId, new Integer(projectCreator));
			
			ServiceFactory.getInstance().getPnDocSpaceHasContainerService().saveDocSpaceHasContainer(
				new PnDocSpaceHasContainer(new PnDocSpaceHasContainerPK(docSpaceId, budgetContainerId), 0));
			
			ServiceFactory.getInstance().getPnDocContainerHasObjectService().save(
				new PnDocContainerHasObject(new PnDocContainerHasObjectPK(systemContainerId, budgetContainerId)));
			
			
			/***********************************************************************************************************************************************************************
			 * PROJECT CALENDAR
			 **********************************************************************************************************************************************************************/
			
			// Create new doc container for the Top-level folder for this project
			Integer calendarId = ServiceFactory.getInstance().getBaseService()
				.createObject("calendar", new Integer(projectCreator), "A");
			
			PnCalendar calendar = new PnCalendar();
			calendar.setCalendarId(calendarId);
			calendar.setIsBaseCalendar(1);
			calendar.setCalendarName("Project Calendar");
			calendar.setCalendarDescription("Main Project Calendar");
			calendar.setRecordStatus("A");
			ServiceFactory.getInstance().getPnCalendarService().saveCalendar(calendar);
			
			ServiceFactory.getInstance().getSecurityService().createSecurityPermissions(calendarId, "calendar", projectId, new Integer(projectCreator));
			
			// Link calendar to project space
			ServiceFactory.getInstance().getPnSpaceHasCalendarService().saveSpaceHasCalendar(
				new PnSpaceHasCalendar(new PnSpaceHasCalendarPK(projectId, calendarId)));
			
			// Added by Robin 18-Apr-00
			// Add plan default
			Integer planId = ServiceFactory.getInstance().getScheduleService().storePlan(planName, "Main Project Plan", startDate,
				endDate, autocalcSchedule, null, null, new Integer(projectCreator), projectId, null, null, null, null, null, 
				"50", startDate, null);
			
			if (createShare > 0) {
			    // First, create the share for the project.
			    ServiceFactory.getInstance().getSharingService().storeShare(projectId, -1, null, projectId, 
				    SharingConstants.DEFAULT_PROPAGATION, SharingConstants.DEFAULT_ALLOWABLE_ACTIONS);
			    // Now, create the share for the plan.
			    ServiceFactory.getInstance().getSharingService().storeShare(planId, 1, projectId, projectId,
				    SharingConstants.DEFAULT_PROPAGATION, SharingConstants.DEFAULT_ALLOWABLE_ACTIONS);
			}
			
			
			/***********************************************************************************************************************************************************************
			 * PROJECT PORTFOLIOS
			 * If the creator chose business visibility, add the project to the
			 * businesses' "complete portfolio".  Otherwise, we will do this when
			 * the user "moves" the project from his personal space to the business space.
			 **********************************************************************************************************************************************************************/
			
			// If project is being added to the personal space scope,
			// it is private (still being setup by the user).
			
			Integer isPrivate = 0;
			if (projectVisibility.equals("Personal Space")) {
			    isPrivate = 1;
			}
			
			// Add this project to the creator's "membership portfolio".
			// set is_private flag is not visibility="personal".
			
			Integer membershipPortfolioId = 0;
			if (new Integer(projectCreator) != 0) {
			    PnPerson person = ServiceFactory.getInstance().getPnPersonService().getPerson(new Integer(projectCreator));
			    if (person != null) {
				membershipPortfolioId = person.getMembershipPortfolioId();
			    }
			}
			
			// The creator always gets the project added to their membership portfolio
			PnPortfolioHasSpace portfolioHasSpace = new PnPortfolioHasSpace(
				new PnPortfolioHasSpacePK(membershipPortfolioId, projectId));
			portfolioHasSpace.setIsPrivate(isPrivate);
			
			ServiceFactory.getInstance().getPnPortfolioHasSpaceService().savePortfolioHasSpace(portfolioHasSpace);
			
			// BUSINESS SPACE OWNED
			// Add this project to the owning business space portfolio
			Integer portfolioId = 0;
			if (new Integer(businessSpaceId) != 0) {
			    // "owner" portfolio is used for grouing projects in the portfolio management pages.
			    // Get the business' project "owner" portfolio.
			    PnPortfolio portfolio = ServiceFactory.getInstance().getPnPortfolioService().
			    	getPortfolioForSpace(new Integer(businessSpaceId));
			    if (portfolio != null) {
				 portfolioId = portfolio.getPortfolioId();
			    }

			    // Add this project to the Business Space's Onwer portfolio
			    portfolioHasSpace = new PnPortfolioHasSpace(new PnPortfolioHasSpacePK(portfolioId, projectId));
			    portfolioHasSpace.setIsPrivate(isPrivate);
			    ServiceFactory.getInstance().getPnPortfolioHasSpaceService().savePortfolioHasSpace(portfolioHasSpace);
			}
			
			result.add(projectId);
			// due to API requirement, we are now returning the workplan id as part of the project create
			result.add(planId);
			// alos return the role_id for space administrator role
			//result.add(spaceAdminGroupId);
			
		} catch (Exception e) {
		    if (LOG.isDebugEnabled()) {
		    	LOG.debug("EXIT FAIL: createProject");
		    }
			e.printStackTrace();
		}
		
		if (LOG.isDebugEnabled()) {
		    LOG.debug("EXIT OK: createProject");
		}
		return result;
	}

}
