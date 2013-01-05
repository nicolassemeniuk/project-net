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
package net.project.directory;

import java.util.ArrayList;
import java.util.List;

import net.project.base.PnetException;
import net.project.base.directory.search.DirectorySearchException;
import net.project.base.directory.search.IDirectoryContext;
import net.project.base.directory.search.ISearchResults;
import net.project.base.directory.search.ISearchableDirectory;
import net.project.base.directory.search.SearchControls;
import net.project.base.directory.search.SearchFilter;
import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.business.BusinessSpaceBean;
import net.project.hibernate.model.PnBusiness;
import net.project.persistence.PersistenceException;
import net.project.portfolio.BusinessPortfolio;
import net.project.portfolio.PortfolioManager;
import net.project.portfolio.ProjectPortfolioBean;
import net.project.project.ProjectSpace;
import net.project.resource.InvitationException;
import net.project.resource.SpaceInvitationManager;
import net.project.security.SecurityProvider;
import net.project.security.User;
import net.project.space.Space;
import net.project.space.SpaceList;
import net.project.space.SpaceListLoadableException;
import net.project.space.SpaceManager;
import net.project.util.StringUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

/**
 * Helper for Manage invite  and remove members.
 * 
 */
public class ManageDirectoryHelper {

	private static Logger log;

	
	public ManageDirectoryHelper() {
		log = Logger.getLogger(ManageDirectoryHelper.class);
	}

	/**
	 * Get project list depand on space and user.
	 * @param currentSpaceId
	 * @param user
	 * @return
	 */
	public static List<PnBusiness> getProjectLists(String currentSpaceId, User user, int action) {
		List<PnBusiness> accessibleProjectList = new ArrayList<PnBusiness>();
		try {
			ProjectPortfolioBean projectPortfolio = new ProjectPortfolioBean();
			SpaceList spaceList = null;
			ArrayList subbusiness = new ArrayList<PnBusiness>();
			BusinessPortfolio businessPortfolio = new BusinessPortfolio();
			BusinessSpaceBean businessSpaceBean = new BusinessSpaceBean();

			businessSpaceBean.setID(currentSpaceId);
			businessPortfolio.setID(businessSpaceBean.getProjectPortfolioID("owner"));

			spaceList = SpaceManager.getSubbusinesses(businessSpaceBean);
			businessPortfolio = (BusinessPortfolio) PortfolioManager.makePortfolioFromSpaceList(spaceList);
			businessPortfolio.load();

			BusinessSpace entry = null;
			java.util.Iterator it = businessPortfolio.iterator();
			while (it.hasNext()) {
				entry = (BusinessSpace) it.next();
				if (isAccessAllowed(new BusinessSpace(entry.getID()), net.project.base.Module.DIRECTORY, action, user)) {
					subbusiness.add(new PnBusiness(Integer.parseInt(entry.getID()), entry.getName()));
				}
			}

			projectPortfolio.clear();
			projectPortfolio.setID(businessSpaceBean.getProjectPortfolioID("owner"));
			projectPortfolio.setUser(user);

			try {
				projectPortfolio.load();
			} catch (PersistenceException e) {
				log.error("Error occurred while  getProjectList" + e.getMessage());
			}
			List<PnBusiness> projectList = projectPortfolio.getProjectList();

			if (CollectionUtils.isNotEmpty(projectList)) {
				for (PnBusiness business : projectList) {
					if (isAccessAllowed(new ProjectSpace(business.getBusinessId().toString()),
							net.project.base.Module.DIRECTORY, action, user)) {
						accessibleProjectList.add(business);
					}
				}
			}
			accessibleProjectList.addAll(subbusiness);
			if (CollectionUtils.isEmpty(accessibleProjectList)) {
				accessibleProjectList.add(0, new PnBusiness(-2, PropertyProvider
						.get("prm.directory.bulkinvitation.noprojecinlistmessage")));
			} else {
				accessibleProjectList.add(0, new PnBusiness(-1, PropertyProvider
						.get("prm.directory.bulkinvitation.projeclistmessage")));
			}
		} catch (SpaceListLoadableException e) {
			log.error("Error occurred while  getProjectList" + e.getMessage());
		} catch (PersistenceException e) {
			log.error("Error occurred while  getProjectList" + e.getMessage());
		} catch (PnetException e) {
			log.error("Error occurred while  getProjectList" + e.getMessage());
		}
		return accessibleProjectList;
	}

	/**
	 * load business Members
	 * @param directoryId
	 * @param searchUser
	 * @param spaceInvitationManager
	 * @return
	 */
	public static ISearchResults loadBusinessMembers(String directoryId, String searchUser,
			SpaceInvitationManager spaceInvitationManager) {
		ISearchResults searchResults = null;
		try {
			if (StringUtils.isNotEmpty(directoryId) && !directoryId.equals("-1")) {
				spaceInvitationManager.setDirectoryID(directoryId);
				spaceInvitationManager.setSearchName(searchUser);
				ISearchableDirectory searchDirectory = spaceInvitationManager.getSearchableDirectory();
				IDirectoryContext directoryContext = searchDirectory.getDirectoryContext();

				// Construct search filter
				SearchFilter searchFilter = new SearchFilter();
				searchFilter.add("name", spaceInvitationManager.getSearchName());

				// Build search control
				SearchControls searchControls = new SearchControls();

				// Perform search

				searchResults = directoryContext.search(searchFilter, searchControls);
			}
		} catch (DirectorySearchException e) {
			log.error("Error occurred while loadBusinessMember : " + e.getMessage());
		} catch (InvitationException e) {
			log.error("Error occurred while loadBusinessMember : " + e.getMessage());
		}
		return searchResults;
	}

	/**
	 * Check for access allowed for specified module and action in specified space for specified user.
	 * @param spaceId space identifier
	 * @return true or false
	 */
	public static boolean isAccessAllowed(Space space, int module, int action, User user) {
		SecurityProvider checkSecurityProvider = new SecurityProvider();
		checkSecurityProvider.setUser(user);
		checkSecurityProvider.setSpace(space);
		return checkSecurityProvider.isActionAllowed(null, module, action);
	}
}
