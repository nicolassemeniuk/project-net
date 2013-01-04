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

import java.util.Collection;
import java.util.Iterator;

import net.project.persistence.PersistenceException;
import net.project.portfolio.ProjectPortfolio;
import net.project.space.PersonalSpace;
import net.project.space.Space;

/**
 * Provides the context for managing personal portfolio views.
 * A personal portfolio is a project of <code>ProjectSpace</code> portfolio
 * entries.  PersonalPortfolioViews are stored for a PersonalSpace.
 */
public class PersonalPortfolioViewContext extends SpaceViewContext {

    /**
     * The portfolio used in all views.
     */
    private final ProjectPortfolio portfolio;

    /**
     * Creates a new PersonalPortfolioViewContext based on the specified
     * personal portfolio.
     *
     * @param portfolio the personal portfolio to which this view context
     * applies; used in created views for limiting the set of results to
     * the project spaces in the portfolio
     */
    public PersonalPortfolioViewContext(ProjectPortfolio portfolio) {
        this.portfolio = portfolio;
    }

    /**
     * Returns the current portfolio.
     * @return the portfolio
     */
    ProjectPortfolio getPortfolio() {
        return this.portfolio;
    }

    /**
     * Specifies the space for this view context.
     * @param space the PersonalSpace
     * @throws ClassCastException if the space is not a <code>PersonalSpace</code>
     */
    public void setSpace(Space space) {
        PersonalSpace personalSpace = (PersonalSpace) space;
        super.setSpace(personalSpace);
    }

    public IView makeView() {
        return new PersonalPortfolioView(this);
    }

    /**
     * Returns all views for this personal portfolio view context.
     * @return the view list for this context
     * @throws PersistenceException if there is a problem constructing the
     * view list
     */
    public IViewList makeViewList() throws PersistenceException {
        PersonalPortfolioViewList viewList = new PersonalPortfolioViewList();
        viewList.setViewContext(this);
        viewList.load();
        return viewList;
    }

    /**
     * Returns the ID of the default view for the specified scenario.
     * @param scenario the scenario for which the default view is required
     * @return the ID of the default view or null if there is no default view for that scenario.
     */
    public String getDefaultViewID(DefaultScenario scenario) {

        String defaultViewID = null;

        try {

            // Now find the setting which is maintaining the specified scenario
            DefaultViewSetting defaultViewSetting = null;
            for (Iterator it = getDefaultViewSettings().iterator(); it.hasNext(); ) {
                DefaultViewSetting nextSetting = (DefaultViewSetting) it.next();

                if (nextSetting.getScenario().equals(scenario)) {
                    // We found the setting for the specified scenario
                    defaultViewSetting = nextSetting;
                    break;
                }
            }

            if (defaultViewSetting != null && defaultViewSetting.hasValue()) {
                defaultViewID = defaultViewSetting.getDefaultID();
            }

        } catch (PersistenceException pe) {
            // We return no view ID
        }

        return defaultViewID;
    }

    /**
     * Returns the Default View Settings for this context.
     * These are settings which indicate the default view for a number of
     * scenarios.  The settings are stored against this scenario.  This means
     * that each user has their own setting indicating the default view.
     *
     * @return a collection where each element is a <code>DefaultViewSetting</code>
     * @see #storeDefaultViewSettings
     * @throws PersistenceException if there is a problem loading
     */
    public Collection getDefaultViewSettings() throws PersistenceException {
        // Get the view settings for Personal Portfolio default scenarios
        // and the personal space as the context
        // This has the effect of returning the default views for this user
        DefaultViewProvider provider = new DefaultViewProvider();
        return provider.getDefaultViewSettings(getSpace().getID(), PersonalPortfolioDefaultScenario.getAllDefaultScenarioIDs());
    }

    /**
     * Stores the default view settings against this context.
     *
     * @param defaultViewSettings the default view settings
     * @see #getDefaultViewSettings
     * @throws PersistenceException if there is a problem storing
     */
    public void storeDefaultViewSettings(Collection defaultViewSettings) throws PersistenceException {
        DefaultViewProvider provider = new DefaultViewProvider();
        provider.storeDefaultViewSettings(getSpace().getID(), defaultViewSettings);
    }

}
