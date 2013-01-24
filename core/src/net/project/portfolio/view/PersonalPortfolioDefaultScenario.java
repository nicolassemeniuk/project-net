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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Provides the scenarios in which we want to store a default view setting
 * for the Personal Portfolio.
 * <p>
 * Each scenario has a unique ID since it is used for persistence.
 * </p>
 *
 * @since Version 7.4
 * @author Tim Morrow
 */
public class PersonalPortfolioDefaultScenario extends DefaultScenario {

    //
    // Static members
    //

    /**
     * The collection of all Personal Portfolio Default Scenario IDs.
     */
    private static final Collection defaultScenarioIDCollection = new ArrayList();

    /**
     * Portfolio page scenario.
     * This identifies the default view for the Personal Portfolio page.
     */
    public static final DefaultScenario PORTFOLIO = new PersonalPortfolioDefaultScenario("100", "prm.portfolio.personal.view.defaultscenario.portfolio");

    /**
     * Personalspace dashboard scenario.
     * This identifies the default view for the personal space dashboard.
     */
    public static final DefaultScenario PERSONALSPACE_DASHBOARD = new PersonalPortfolioDefaultScenario("200", "prm.portfolio.personal.view.defaultscenario.personalspacedashboard");

    /**
     * Returns the DefaultScenario for the specified ID.
     * @param id the internal ID of the DefaultScenario to get
     * @return the DefaultScenario with matching ID, or null if none is found
     * matching
     */
    public static DefaultScenario findByID(String id) {
        return DefaultScenario.findByID(defaultScenarioIDCollection, id);
    }

    /**
     * Returns a collection of all the Personal Portfolio Default Scenarios.
     * @return a collection where each element is a <code>DefaultScenario</code>
     */
    public static Collection getAllDefaultScenarioIDs() {
        return Collections.unmodifiableCollection(defaultScenarioIDCollection);
    }

    //
    // Instance members
    //

    /**
     * Creates a new PersonalPortfolioDefaultScenario with the specified internal
     * id and name token.
     * @param id the internal ID of this scenario ID
     * @param nameToken the token used to provide the display name of this
     * scenario ID
     * @throws IllegalStateException if the ID is not unique
     */
    private PersonalPortfolioDefaultScenario(String id, String nameToken) {
        super(id, nameToken);
        add(defaultScenarioIDCollection, this);
    }

}
