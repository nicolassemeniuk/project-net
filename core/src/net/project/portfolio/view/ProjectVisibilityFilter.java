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
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.portfolio.view;

import net.project.base.finder.FinderFilter;
import net.project.base.finder.IFinderIngredientVisitor;
import net.project.project.ProjectVisibility;
import net.project.util.VisitException;

public class ProjectVisibilityFilter extends FinderFilter {

    /**
     * The ID of the portfolio from which to find projects for the current
     * user.
     */
    private final String portfolioID;

    /**
     * The ID of the current user, required for determining business membership.
     */
    private final String userID;

    private ProjectVisibility projectVisibility = null;

    /**
     * Creates a new ProjectVisibilityFilter to find projects for the specified
     * portfolio and user.
     * @param id the ID of this filter
     * @param nameToken the token providing the display name of this filter
     * @param portfolioID the ID of the portfolio from which to find projects
     * that a user is a member of; this is assumed to be the ID of a personal
     * portfolio.
     * @param userID the ID of the current user, required to find businesses
     * that they are a member of.  Those businesses are used to locate projects
     * owned by a business and are included if the ProjectVisibility is set
     * to {@link net.project.project.ProjectVisibility#OWNING_BUSINESS_PARTICIPANTS} or {@link net.project.project.ProjectVisibility#GLOBAL}
     */
    public ProjectVisibilityFilter(String id, String nameToken, String portfolioID, String userID) {
        super(id, nameToken);
        this.portfolioID = portfolioID;
        this.userID = userID;
    }

    /**
     * Returns the where clause to filter projects based on the current
     * project visibility.
     * This always returns a where clause.  If the Project Visibility setting
     * is currently null, it uses the default visibility of {@link ProjectVisibility#PROJECT_PARTICIPANTS}.
     * This ensures that a filter is always limited in some way.
     * @return the where clause
     * @throws java.lang.IllegalStateException if the current project visibility is
     * null
     */
    public String getWhereClause() {

        // Determine the current visibility, setting to the default
        // value if it is null
        ProjectVisibility currentVisibility = getProjectVisibility();
        if (currentVisibility == null) {
            currentVisibility = ProjectVisibility.PROJECT_PARTICIPANTS;
        }

        StringBuffer whereClause = new StringBuffer();

        whereClause.append("(");

        // Add clause to include projects in the portfolio
        // Deleted projects are already absent
        whereClause.append("(p.project_id in (select pv.project_id from pn_portfolio_view pv where pv.portfolio_id = ")
                .append(this.portfolioID).append("))");

        if (currentVisibility.equals(ProjectVisibility.OWNING_BUSINESS_PARTICIPANTS) ||
                currentVisibility.equals(ProjectVisibility.GLOBAL)) {

            // Expand filter to projects owned by the user's businesses where the project's
            // visibility flag is not participants only
            // Deleted projects are already gone from the business portfolio
            whereClause.append(" OR (")
                    .append("p.project_id in (")
                    .append("SELECT porthasproj.project_id ")
                    .append("FROM pn_space_has_person shp, pn_business b, pn_space_has_portfolio shport, pn_portfolio port, pn_portfolio_view porthasproj ")
                    .append("WHERE shp.person_id = ").append(this.userID).append(" AND b.business_id = shp.space_id AND b.record_status = 'A' ")
                    .append("AND shport.space_id = b.business_id AND port.portfolio_id = shport.portfolio_id AND port.portfolio_name = 'owner' AND porthasproj.portfolio_id = port.portfolio_id)")
                    .append(" AND ")
                    .append("p.visibility_id <> ").append(ProjectVisibility.PROJECT_PARTICIPANTS.getID())
                    .append(")");

        }

        if (currentVisibility.equals(ProjectVisibility.GLOBAL)) {

            // Expand to include global projects (remembering to only load non-deleted projects)
            whereClause.append(" OR (")
                    .append("p.visibility_id = ").append(ProjectVisibility.GLOBAL.getID()).append(" ")
                    .append("and p.record_status = 'A'")
                    .append(")");

        }

        whereClause.append(")");

        return whereClause.toString();
    }

    public String getFilterDescription() {
        return null;
    }

    protected void clearProperties() {
        setProjectVisibility(null);
    }

    public void setProjectVisibility(ProjectVisibility visibility) {
        this.projectVisibility = visibility;
    }

    public ProjectVisibility getProjectVisibility() {
        return this.projectVisibility;
    }

    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitProjectVisibilityFilter(this);
    }

}

