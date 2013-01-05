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

import net.project.base.property.PropertyProvider;

/**
 * Provides a base class for an enumeration of Default Scenarios.
 * A Default Scenario provides an ID used for storing the default view
 * for a given scenario.
 * <p>
 * For example, for a certain view context (e.g. the Personal Portfolio view context)
 * we might store the default view for the Portfolio screen (this is one scenario)
 * and we might store the default view for the Personal Space dashboard screen
 * (this is another scenario).  We need to uniquely identify those two scenarios
 * for storing the default view in the database.
 * </p>
 * <p>
 * This is a standard abstract base class for defining separate enumerations
 * of values that share a common type.
 * </p>
 *
 * @since Version 7.4
 * @author Tim Morrow
 */
public abstract class DefaultScenario {

    //
    // Static members
    //

    /**
     * Returns the DefaultScenario with the specified id.
     * @param scenarioCollection the collection of <code>DefaultScenario</code>s
     * in which to find the matching one
     * @param id the id of the value to find
     * @return the DefaultScenario with matching id, or null if no value is
     * found with that id
     */
    protected static DefaultScenario findByID(Collection scenarioCollection, String id) {
        DefaultScenario foundDefaultScenarioID = null;
        boolean isFound = false;

        for (Iterator it = scenarioCollection.iterator(); it.hasNext() && !isFound;) {
            DefaultScenario nextDefaultScenarioID = (DefaultScenario) it.next();
            if (nextDefaultScenarioID.getID().equals(id)) {
                foundDefaultScenarioID = nextDefaultScenarioID;
                isFound = true;
            }
        }

        return foundDefaultScenarioID;
    }

    /**
     * Adds the specified scenario to the specified collection, ensuring it
     * is unique.
     * @param scenarioIDCollection the collection to which to add the scenario
     * @param scenario the scenario to add
     * @throws IllegalStateException if the specified scenario does not have
     * a unique ID.
     */
    protected static void add(Collection scenarioIDCollection, DefaultScenario scenario) {

        if (scenarioIDCollection.contains(scenario)) {
            throw new IllegalStateException("DefaultScenario IDs must be unique");
        }

        scenarioIDCollection.add(scenario);
    }

    //
    // Instance members
    //

    /**
     * The unique id.
     */
    private String id = null;

    /**
     * The token which provides the display name.
     */
    private String nameToken = null;

    /**
     * Creates a new DefaultScenario.
     * @param id the id
     * @param nameToken the token for the display name of this scenario
     */
    protected DefaultScenario(String id, String nameToken) {
        this.id = id;
        this.nameToken = nameToken;
    }

    /**
     * Returns the internal id of this DefaultScenario.
     * @return the id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the display name of this DefaultScenario.
     * @return the display name
     */
    public String getName() {
        return PropertyProvider.get(this.nameToken);
    }

    /**
     * Indicates whether the specified object is a DefaultScenario with
     * matching ID.
     * @param o the DefaultScenario object to compare
     * @return true if the specified DefaultScenario has a matching id; false otherwise
     */
    public boolean equals(Object o) {
        boolean isEqual = false;

        if (this == o) {

            isEqual = true;

        } else {

            if (o instanceof DefaultScenario) {
                DefaultScenario scenarioID = (DefaultScenario) o;
                if (id.equals(scenarioID.id)) {
                    isEqual = true;
                }
            }

        }

        return isEqual;
    }

    public int hashCode() {
        return id.hashCode();
    }

}
