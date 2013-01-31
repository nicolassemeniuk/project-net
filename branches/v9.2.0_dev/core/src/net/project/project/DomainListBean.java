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

 package net.project.project;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.business.BusinessSpace;
import net.project.database.DBBean;
import net.project.gui.html.HTMLOption;
import net.project.gui.html.HTMLOptionList;
import net.project.gui.html.IHTMLOption;
import net.project.hibernate.model.PnProjectSpaceMetaCombo;
import net.project.hibernate.service.IPnProjectSpaceMetaValueService;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.portfolio.IPortfolioEntry;
import net.project.portfolio.ProjectPortfolioBean;
import net.project.security.Action;
import net.project.security.SecurityProvider;
import net.project.security.User;
import net.project.space.Space;
import net.project.space.SpaceFactory;
import net.project.util.StringUtils;
import net.project.util.Validator;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

public class DomainListBean implements Serializable {

    private final DBBean db = new DBBean();

    public static String getBusinessName(String businessID) {

        if (businessID == null) {
            return null;
        }

        String name = null;
        String qstrGetBusinessName = "select b.business_name from pn_business b where" +
                " b.business_id = " + businessID;
        DBBean ldb = new DBBean();

        try {
            ldb.executeQuery(qstrGetBusinessName);

            if (ldb.result.next()) {
                name = ldb.result.getString("business_name");
            }

        } // end try
        catch (SQLException sqle) {
        	Logger.getLogger(DomainListBean.class).debug("project.DomainListBean.getBusinessName threw an SQL exception: " + sqle);
        } // end catch
        finally {
            ldb.release();
        }

        return name;

    } // end getBusinessName

    public String getAvailableBusinessOptionList(String userID) {
        return getAvailableBusinessOptionList(userID, null, null);
    }

    /**
     *
     * @param user
     * @return
     * @deprecated as of 7.6.5; use {@link #getAvailableBusinessOptionList(java.lang.String)} instead.
     * For example <code>getAvailableBusinessOptionList(user.getID())</code>.
     */
    public String getAvailableBusinessOptionList(User user) {
        return getAvailableBusinessOptionList(user.getID());
    }

    /**
     * Returns an HTML option list of active businesses where the person with the specified ID is a member of each
     * business.
     * <p/>
     * The option with business ID matching the specified defaultID is set to select, if present. </p>
     *
     * @param userID the ID of the user for whom to fetch a selection list of businesses
     * @param defaultID the ID of the business to select in the list; when null or not equal to any business, no option
     * is selected
     * @return the HTML options, sorted by business name, or null if the specified userID was null
     */
    public String getAvailableBusinessOptionList(String userID, String defaultID, String currentBusinessID) {

        if (userID == null) {
            return null;
        }

        class AllBusinessFilter implements IBusinessFilter {
            public boolean isInclude(String businessID) {
                return true;
            }
        }

        return HTMLOptionList.makeHtmlOptionList(getAvailableBusinessOptions(userID, new AllBusinessFilter(), defaultID, currentBusinessID), defaultID);
    }

    /**
     * Returns an HTML option list of active businesses where the user with the specified ID is a member of each
     * business and they have Create permission in the Business' business Space module.
     * <p/>
     * Since a user requires this permission to create a project in a business, this list effectively eliminates
     * businesses in which a user cannot create an owned project; thus provides a list that can be used to select a
     * business owner when creating a project. </p>
     * <p/>
     * The option with business ID matching the specified defaultID is select.  If the user doesn't have permission for
     * that business, it is still included.  This ensures that if a user is modifying a project for which they
     * previously had permission, they won't accidentally remove the business ownership.  For creating new projects, no
     * additional businesses will be included in the list. </p>
     *
     * @param user the user for whom to fetch a selection list of businesses
     * @param defaultID the ID of the business to select in the list; when null or not equal to any business, no option
     * is selected
     * @return the HTML options, sorted by business name, or null if the specified userID was null
     */
    public String getAvailableBusinessOptionListForProjectOwnership(User user, String defaultID) {

        if (user == null) {
            return null;
        }

        return HTMLOptionList.makeHtmlOptionList(getAvailableBusinessOptions(user.getID(), new CreatePermissionFilter(user), defaultID, null), defaultID);
    }
    
    public String getMaterialTypeListForMaterialCreation(Collection collection){
    	return HTMLOptionList.makeHtmlOptionList(collection);
	}
    
    public String getMaterialTypeListForMaterialModification(Collection collection, String selectedValue){
    	return HTMLOptionList.makeHtmlOptionList(collection, selectedValue);
	}

    /**
     * Returns a collection of <code>IHTMLOption</code>s where the user with the specified ID is a member of each
     * business and each business passes a test specified by the filter.
     *
     * @param userID the ID of the user for whom to fetch businesses
     * @param filter the filter to use to filter out businesses
     * @param ignoreFilterID the ID of the business to always include; never filters it out Note:  This must still be
     * from the set of businesses that the user is a member of. When null, all businesses are filtered.
     * @return the IHTMLOpions matching the filter
     */
    private Collection getAvailableBusinessOptions(String userID, IBusinessFilter filter, String ignoreFilterID, String currentBusinessID) {

        // Provides HTML option values
        class BusinessOption extends HTMLOption implements IHTMLOption {
            public BusinessOption(String optionValue, String optionDisplay) {
                super(optionValue, optionDisplay);
            }
        }

        Collection optionList = new ArrayList();

        String qstrGetAvailableBusinessSpaces = "select b.business_id, b.business_name " +
                "from pn_space_has_person shp, pn_business b " +
                "where shp.person_id=" + userID + " and shp.record_status = 'A' " +
                "and b.business_id = shp.space_id and b.record_status = 'A' ";

        if (!Validator.isBlankOrNull(currentBusinessID)) {

                qstrGetAvailableBusinessSpaces += "and b.business_id not in " +
                "(select child_space_id from pn_space_has_space " +
                "where relationship_parent_to_child = 'superspace' " +
                "start with parent_space_id = " + currentBusinessID +
                " connect by prior child_space_id = parent_space_id) " +
                " and b.business_id != " + currentBusinessID + " ";
        }

        qstrGetAvailableBusinessSpaces += " order by upper(b.business_name) asc ";

        try {

            db.executeQuery(qstrGetAvailableBusinessSpaces);

            while (db.result.next()) {
                String businessID = db.result.getString("business_id");
                String businessName = db.result.getString("business_name");

                // Add it if filtering is ignored or filtering includes it
                if (businessID.equals(ignoreFilterID) || filter.isInclude(businessID)) {
                    optionList.add(new BusinessOption(businessID, businessName));
                }

            }

        } catch (SQLException sqle) {
        	Logger.getLogger(DomainListBean.class).debug("project.DomainListBean.getAvailableBusinessOptionList threw an SQL exception: " + sqle);

        } finally {
            db.release();
        }

        return optionList;
    }

    /**
     * Provides a mechanism for filterint businesses based on ID.
     */
    private interface IBusinessFilter {
        /**
         * Indicates whether to include the business with the specified ID.
         *
         * @param businessID the ID of the business to check
         * @return true if the busienss should be included; false otherwise
         */
        boolean isInclude(String businessID);
    }

    /**
     * Provides a class that filters businesses based on a user having create permission in the business space module
     * for that business.
     */
    private static class CreatePermissionFilter implements IBusinessFilter {

        /**
         * The security provider to use for checking security; we must use a private instance (not using
         * <code>getInstance</code>) because we're changing the space and we don't want to affect the current space
         * context of the current session.
         */
        private final SecurityProvider securityProvider;

        private CreatePermissionFilter(User user) {
            this.securityProvider = new SecurityProvider();
            securityProvider.setUser(user);
        }

        /**
         * Returns true if the user has create action permission in the business space module of the specified
         * business.
         *
         * @param businessID the id of the business from which to get permission
         * @return true if the user can create businesses in the specified business space; false otherwise
         */
        public boolean isInclude(String businessID) {
            securityProvider.setSpace(new BusinessSpace(businessID));
            return securityProvider.isActionAllowed(null, Module.BUSINESS_SPACE, Action.CREATE);
        }
    }


    public static String getUseClassificationCodeName(String uccID) {

        return getCodeName("aec_property_group", "use_classification_code", uccID);

    }

    public static String getProjectTypeName(String projectTypeID) {

        return getCodeName("aec_property_group", "project_type_code", projectTypeID);

    }

    public static String getSiteConditionCodeName(String sccID) {

        return getCodeName("aec_property_group", "site_condition_code", sccID);

    }

    public static String getServicesCodeName(String servicesCodeID) {

        return getCodeName("aec_property_group", "services_code", servicesCodeID);

    }


    public static String getStatusOptionList(String defaultID) {
        ProjectStatus defaultStatus = null;

        if (defaultID != null && defaultID.length() > 0) {
            defaultStatus = ProjectStatus.findByID(defaultID);
        }

        if (defaultStatus == null) {
            defaultStatus = ProjectStatus.DEFAULT;
        }

        return HTMLOptionList.makeHtmlOptionList(ProjectStatus.getAllProjectStatus(), defaultStatus);
    }


    public static String getColorCodeName(String colorCodeID) {

        if (colorCodeID == null) {
            return null;
        }

        String name = null;
        String qstrGetColorCodeName = "select code_name from pn_global_domain where table_name = 'pn_project_space'" +
                " and column_name = 'color_code_id' and code = " + colorCodeID;
        DBBean ldb = new DBBean();


        try {
            ldb.executeQuery(qstrGetColorCodeName);

            if (ldb.result.next()) {
                name = PropertyProvider.get(ldb.result.getString("code_name"));
            }

        } // end try
        catch (SQLException sqle) {
        	Logger.getLogger(DomainListBean.class).debug("project.DomainListBean.getColorCodeName threw an SQL exception: " + sqle);
        } // end catch
        finally {
            ldb.release();
        }

        return name;

    } // end getColorCodeName


    public static String getProjectName(String projectID) {

        if (projectID == null) {
            return null;
        }

        String name = null;
        String qstrGetProjectName = "select p.project_name from pn_project_view p" +
                " where project_id = " + projectID;
        qstrGetProjectName += " order by upper(p.project_name) asc ";

        DBBean ldb = new DBBean();

        try {
            ldb.executeQuery(qstrGetProjectName);

            if (ldb.result.next()) {
                name = ldb.result.getString("project_name");
            }

        } // end try
        catch (SQLException sqle) {
        	Logger.getLogger(DomainListBean.class).debug("project.DomainListBean.getProjectName threw an SQL exception: " + sqle);
        } // end catch
        finally {
            ldb.release();
        }

        return name;

    } // end getProjectName


    /**
     * Returns an HTML option list of projects that the specified user is a member of, suitable for presenting as a list
     * of parent projects used during project create and project edit.
     * <p/>
     * Note: This list includes projects that the user may not have permission from which to create a sub-project.  This
     * should be validated elsewhere. </p>
     *
     * @param user the current user
     * @param projectID the ID of the project to select by default in the option list, if present
     * @return an HTML option list with zero or one item selected; null when the user is null
     * @deprecated As of 7.6.5; Use {@link #getParentProjectOptionList(net.project.security.User, java.lang.String)}
     *             instead.  For example <code>getParentProjectOptionList(user, space.getID())</code>. That method
     *             correctly uses security to limit the returned option list.
     */
    public String getSubProjectOptionList(User user, String projectID) {

        if (user == null) {
            return null;
        }

        ProjectPortfolioBean portfolio = new ProjectPortfolioBean();

        try {
            portfolio.setID(user.getMembershipPortfolioID());
            portfolio.setUser(user);
            portfolio.load();
        } catch (PersistenceException pe) {
        }

        return portfolio.getHtmlOptionList(projectID);
    }

    /**
     * Returns an HTML option list of projects that the specified user is a member of and in which they have security to
     * create sub-projects.
     * <p/>
     * This is can be used to present a list of available parent projects. </p>
     *
     * @param user the current user
     * @param selectedProjectID the projectID of the project to select by default in the option list, if present.  It is
     * included even if the user has no security for it so that editing a project for which you previous had security
     * does not remove the parent project selection.
     * @return the HTML option list of projects
     * @throws NullPointerException if user is null
     */
    public String getParentProjectOptionList(User user, String selectedProjectID, String currentProjectID){
    	return getParentProjectOptionList(user, selectedProjectID, currentProjectID, null);
    }
    
    /**
     * Returns an HTML option list of projects that the specified user is a member of and in which they have security to
     * create sub-projects.
     * <p/>
     * This is can be used to present a list of available parent projects. </p>
     *
     * @param user the current user
     * @param selectedProjectID the projectID of the project to select by default in the option list, if present.
     * @param ownerBusinessID the owner business ID of the current project
     * It is included even if the user has no security for it so that editing a project for which you previous had security
     * does not remove the parent project selection.
     * @return the HTML option list of projects
     * @throws NullPointerException if user is null
     */
    public String getParentProjectOptionList(User user, String selectedProjectID, String currentProjectID, String ownerBusinessID) {
    	List<ProjectSpace> projectList = null;
        if (user == null) {
            throw new NullPointerException("user is required");
        }

        Collection availableProjects = new ArrayList();
        ProjectPortfolioBean portfolio = new ProjectPortfolioBean();
        Space businessSpace = null;
        try {
            portfolio.setID(user.getMembershipPortfolioID());
            portfolio.setUser(user);
            portfolio.loadNonDependants(currentProjectID);
            if(StringUtils.isNotEmpty(ownerBusinessID) && !"null".equals(ownerBusinessID)){
            	businessSpace = SpaceFactory.constructSpaceFromID(ownerBusinessID);
            }
            // Now collect only those projects that user has permission for
            // but always included the specified selectedProjectID
            final SecurityProvider securityProvider = new SecurityProvider();
            securityProvider.setUser(user);

            for (Iterator iterator = portfolio.iterator(); iterator.hasNext();) {
                IPortfolioEntry nextEntry = (IPortfolioEntry) iterator.next();

                securityProvider.setSpace(new ProjectSpace(nextEntry.getID()));

                // fix for bug-3274. Security is checked already, there is no need to check it again.
                // this check causes big performance issues with big number of projects in the personal space
                //if (nextEntry.getID().equals(selectedProjectID) ||
                //        securityProvider.isActionAllowed(null, Module.PROJECT_SPACE, Action.CREATE)) {
                	if((businessSpace == null || nextEntry.getParentSpaceID() == null) || (businessSpace.isUserSpaceAdministrator(user) 
                				&& !nextEntry.getParentSpaceID().equals(ownerBusinessID))){
                		availableProjects.add(nextEntry);
                	}
                //}
            }
            
            // Find the visible project the of the parent business if the 
            // current user is space admin in business
            if(StringUtils.isNotEmpty(ownerBusinessID) && 
            		(businessSpace != null && businessSpace.isUserSpaceAdministrator(user))){
            	List<ProjectSpace> visibleProjectList = getProjectsVisibleBusinessSpaceAdmin(businessSpace, currentProjectID);
            	if(CollectionUtils.isNotEmpty(visibleProjectList)){
	            	for(ProjectSpace projectSpace : visibleProjectList){
            			availableProjects.add((IPortfolioEntry)projectSpace);
	            	}
            	}
            }
            projectList = new ArrayList<ProjectSpace>(availableProjects);
            Collections.sort(projectList);
        } catch (PersistenceException pe) {
            // Provide an empty option list
        }

        return HTMLOptionList.makeHtmlOptionList((Collection)projectList, selectedProjectID);
    }


    /**
     * Get all the projects that the user has access to. Select the parentProject in the returned list.
     *
     * @deprecated As of 7.6.5; Use {@link #getSubProjectOptionList(net.project.security.User, java.lang.String)}
     * instead.  For example <code>getSubProjectOptionList(user, space.getID())</code>.
     */
    public String getSubProjectOptionList(User user, Space space) {
        return getSubProjectOptionList(user, space.getID());
    }


    /**
     * **************************************************************************************************************
     * ****                                 Utility methods for getting option lists
     * ***** ***************************************************************************************************************
     */


    private static String getCodeName(String tableName, String columnName, String code) {

        if (code == null) {
            return null;
        }

        String name = null;
        DBBean ldb = new DBBean();
        String qstrGetStatusName = "select code_name from pn_global_domain where table_name = '" +
                tableName + "' and column_name = '" + columnName + "' and code = " + code;

        try {
            ldb.executeQuery(qstrGetStatusName);

            if (ldb.result.next()) {
                name = PropertyProvider.get(ldb.result.getString("code_name"));
            }

        } // end try
        catch (SQLException sqle) {
        	Logger.getLogger(DomainListBean.class).debug("project.DomainListBean.getCodeName threw an SQL exception: " + sqle);
        } // end catch
        finally {
            ldb.release();
        }

        return name;

    }

    public String getValuesOptionListForProperty(Integer propertyId, String selected) {
    	IPnProjectSpaceMetaValueService service = ServiceFactory.getInstance().getPnProjectSpaceMetaValueService();
    	List<PnProjectSpaceMetaCombo> metaCombos = service.getValuesOptionListForProperty(propertyId);
    	
    	return HTMLOptionList.makeHtmlOptionList(metaCombos, selected);
    }
    
    /**
     *  Populates a list of project space meta combo values 
     *  as per property id
     * @param propertyId, Property id of meta combo
     * @param selected, value to be selected by default 
     * @return meta options list
     */
    public List<String> getDomainOptionListForProperty(Integer propertyId, String selected) {
    	List<String> metaOptionList = new ArrayList<String>();
    	IPnProjectSpaceMetaValueService service = ServiceFactory.getInstance().getPnProjectSpaceMetaValueService();
    	List<PnProjectSpaceMetaCombo> metaCombos = service.getValuesOptionListForProperty(propertyId);
    	for(PnProjectSpaceMetaCombo projectSpaceMetaCombo : metaCombos) {
    		metaOptionList.add(projectSpaceMetaCombo.getHtmlOptionValue());
    	}
    	return metaOptionList;
    }
    
    /**
     * Method to find the visible projects of the parent business
     * in which if the current user is space admin
     * 
     * @param parentBusinessID parent business id
     * @param currentProjectID current project id
     * @return visible projects list
     */
    public List<ProjectSpace> getProjectsVisibleBusinessSpaceAdmin(Space businessSpace, String currentProjectID){
    	List<ProjectSpace> visibleProjectList = null;
    	DBBean db = new DBBean();
    	try {
			visibleProjectList = new ArrayList<ProjectSpace>();
			
			String visibleProjectSQL = "select p.project_id, p.project_name from pn_space_has_space shs, pn_project_view p " + 
					" where shs.child_space_id = p.project_id and parent_space_id = ? and relationship_child_to_parent = 'owned_by' " +
					" and child_space_id <> ? and p.record_status = 'A' ";
			db.prepareStatement(visibleProjectSQL);
			db.pstmt.setString(1, businessSpace.getID());
			db.pstmt.setString(2, currentProjectID);
			db.executePrepared();
			
			ProjectSpace projectSpace = null;
			while(db.result.next()) {
				projectSpace = new ProjectSpace();
				projectSpace.setID(db.result.getString("project_id"));
				projectSpace.setName(db.result.getString("project_name"));
				visibleProjectList.add(projectSpace);
			}
		} catch (SQLException pnetEx) {
			Logger.getLogger(DomainListBean.class).error("DomainListBean.getProjectsVisibleBusinessSpaceAdmin() failed.." +pnetEx.getMessage());
		} finally{
			db.release();
		}
		
		return visibleProjectList;
    }
    
    
}
