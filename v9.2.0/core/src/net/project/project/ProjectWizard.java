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

 /*----------------------------------------------------------------------+
|
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.project;

import java.io.Serializable;
import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.portfolio.ProjectPortfolio;

import org.apache.log4j.Logger;

/**
 * Provides methods for creating a new project.
 */
public class ProjectWizard
        extends ProjectSpace
        implements Serializable {

    /**
     * Creates an empty ProjectWizard.
     */
    public ProjectWizard() {
        super();
    }

    /**
     * Creates a ProjectWizard for the specified project id.
     * @param objectID the project space id
     * @deprecated as of 7.4; use {@link #ProjectWizard()} instead.
     * The specified id is never used; when the {@link #store} method is called
     * it ignores the id and creates a new project with a new id.
     */
    public ProjectWizard(String objectID) {
        super(objectID);
    }


    /**
     * Indicates whether the Terms Of Use page is enabled.
     * Current implementation uses the token <code>prm.project.create.termsofuse.isenabled</code>
     * @return true if the Terms Of Use page is enabled; false otherwise
     */
    public boolean isTermsOfUseEnabled() {
        return net.project.base.property.PropertyProvider.getBoolean("prm.project.create.termsofuse.isenabled");
    }

    /**
     * Indicates whether a user is a member of  at lease one active business.
     * @param userID the id of the user to check
     * @return true if the user is a member of at lease one business; false
     * if the user is a member of no businesses
     */
    public boolean isUserMemberOfBusinesses(String userID) {

        DBBean db = new DBBean();
        boolean isMember = false;
        String qstrGetAvailableBusinessSpaces = "select b.business_id, b.business_name from pn_space_has_person shp," +
                "pn_business b where shp.person_id=" + userID + " and shp.record_status = 'A' and b.business_id = shp.space_id and b.record_status = 'A' " +
                "order by b.business_name asc ";

        try {

            db.executeQuery(qstrGetAvailableBusinessSpaces);

            if (db.result.next()) {
                isMember = true;
            } else {
                isMember = false;
            }

        } catch (SQLException sqle) {
            isMember = false;
            Logger.getLogger(ProjectWizard.class).error("ProjectCreateWizard.isUserMemberOfBusinesses() threw an SQL exception: " + sqle);

        } finally {
            db.release();

        }

        return isMember;
    }


    /**
     * Indicates whether a user is a member of at leaset one active project.
     * @param userID the id of the user to check
     * @return true if the user is a member of at lease one project; false
     * if the user is a member of no projects
     */
    public boolean isUserMemberOfProjects(String userID) {

        DBBean db = new DBBean();
        boolean isMember = false;

        // Query to check if person is member of a project listed in their portfolio
        // Note that this means they will not be deemed a member if it is not
        // on their portfolio
        String qstrGetProjects = "select port.project_id from pn_portfolio_view port, pn_space_has_person shp" +
                " where port.portfolio_id =" + ProjectPortfolio.getUserPortfolioID(userID) + " and shp.person_id=" + userID +
                " and shp.space_id = port.project_id";

        try {

            db.executeQuery(qstrGetProjects);

            if (db.result.next()) {
                isMember = true;
            } else {
                isMember = false;
            }

        } catch (SQLException sqle) {
            isMember = false;
            Logger.getLogger(ProjectWizard.class).error("ProjectCreateWizard.isUserMemberOfProjects() threw an SQL exception: " + sqle);

        } finally {
            db.release();

        }

        return isMember;
    }

}
